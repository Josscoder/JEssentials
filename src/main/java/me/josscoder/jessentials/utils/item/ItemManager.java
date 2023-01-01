package me.josscoder.jessentials.utils.item;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.ConfigSection;
import lombok.Getter;
import me.josscoder.jessentials.JEssentialsPlugin;
import me.josscoder.jessentials.manager.Manager;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ItemManager extends Manager implements Listener {

    private final ConfigSection itemSection;

    @Getter
    private final Map<String, ItemBuilder> items = new HashMap<>();

    @Getter
    private static ItemManager instance;

    private static final String NBT_COOLDOWN_KEY = "itemBuilderCooldown";

    public ItemManager() {
        super(JEssentialsPlugin.getInstance().getConfig());
        this.itemSection = config.getSection("items");
        instance = this;
    }

    @Override
    public void init() {
        itemSection.getKeys(false).forEach(key -> {
            ItemBuilder itemBuilder = new ItemBuilder();
            itemBuilder.setUniqueId(key);
            itemBuilder.setIndex(itemSection.getInt(key + ".index", 0));
            itemBuilder.setId(itemSection.getInt(key + ".id", 0));
            itemBuilder.setMeta(itemSection.getInt(key + ".meta", 0));
            itemBuilder.setCount(itemSection.getInt(key + ".count", 1));
            itemBuilder.setCustomName(itemSection.getString(key + ".customName", ""));
            itemBuilder.setTransferable(itemSection.getBoolean(key + ".transferable", false));
            itemBuilder.setGiveOnRespawn(itemSection.getBoolean(key + ".give-on-respawn", false));

            List<String> commands = itemSection.getStringList(key + ".commands");
            itemBuilder.setCommands(commands.toArray(new String[0]));

            List<String> enchantmentsList = itemSection.getStringList(key + ".enchantments");
            List<Enchantment> enchantments = new ArrayList<>();

            enchantmentsList.forEach(enchantment -> {
                String[] split = enchantment.split(":");
                enchantments.add(Enchantment.getEnchantment(Integer.parseInt(split[0]))
                        .setLevel(split.length > 2 ? Integer.parseInt(split[1]) : 1)
                );
            });

            itemBuilder.setEnchantments(enchantments.toArray(new Enchantment[0]));

            List<String> groups = itemSection.getStringList(key + ".groups");
            itemBuilder.setGroups(groups.toArray(new String[0]));
            itemBuilder.store();
        });
    }

    public void storeItem(ItemBuilder itemBuilder) {
        items.put(itemBuilder.getUniqueId(), itemBuilder);
    }

    public boolean containsItem(String uniqueId) {
        return items.containsKey(uniqueId);
    }

    public ItemBuilder getItem(String uniqueId) {
        return items.get(uniqueId);
    }

    public void switchGroupItems(String group, Player player) {
        player.getInventory().clearAll();

        getGroupItems(group).forEach(itemBuilder ->
                player.getInventory().setItem(itemBuilder.getIndex(), itemBuilder.build())
        );
    }

    public List<ItemBuilder> getGroupItems(String group) {
        return items.values().stream().filter(itemBuilder -> Arrays.stream(itemBuilder.getGroups())
                .collect(Collectors.toList())
                .contains(group))
                .collect(Collectors.toList());
    }

    @Override
    public void close() {}

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        switchGroupItems("lobby", event.getPlayer());
    }

    private void handleExecution(Item item, Player player, ItemBuilder.Action action) {
        if (item.getCustomBlockData() == null) return;

        String itemUniqueId = item.getCustomBlockData().getString(ItemBuilder.NBT_ITEM_KEY);
        if (itemUniqueId.isEmpty()) return;

        ItemBuilder customItem = getItem(itemUniqueId);
        if (customItem == null) return;

        if (player.namedTag.contains(NBT_COOLDOWN_KEY) &&
                (player.namedTag.getLong(NBT_COOLDOWN_KEY) + 500) >= Instant.now().toEpochMilli()
        ) {
            return;
        }

        player.namedTag.putLong(NBT_COOLDOWN_KEY, Instant.now().toEpochMilli());
        customItem.getHandler().accept(player, action);
        Arrays.stream(customItem.getCommands())
                .forEach(command -> player.getServer().dispatchCommand(player, command));
    }

    @EventHandler
    private void onItemInteract(PlayerInteractEvent event) {
        handleExecution(event.getItem(), event.getPlayer(), ItemBuilder.Action.DEFAULT);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        handleExecution(event.getItem(), event.getPlayer(), ItemBuilder.Action.BLOCK_BREAK);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        handleExecution(event.getItem(), event.getPlayer(), ItemBuilder.Action.BLOCK_PLACE);
    }

    @EventHandler
    private void onHitEntity(PlayerInteractEntityEvent event) {
        handleExecution(event.getItem(), event.getPlayer(), ItemBuilder.Action.INTERACT_ENTITY);
    }

    public boolean isTransferable(Item item) {
        if (item.getCustomBlockData() == null) return false;

        String itemUniqueId = item.getCustomBlockData().getString(ItemBuilder.NBT_ITEM_KEY);
        if (itemUniqueId.isEmpty()) return false;

        ItemBuilder customItem = getItem(itemUniqueId);
        if (customItem == null) return false;

        return customItem.isTransferable();
    }

    private void handleTransferCancel(Item item, Cancellable cancellable) {
        cancellable.setCancelled(!isTransferable(item));
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        handleTransferCancel(event.getItem(), event);
    }

    @EventHandler
    private void onPickupItem(InventoryPickupItemEvent event) {
        handleTransferCancel(event.getItem().getItem(), event);
    }

    @EventHandler
    private void onMoveItem(InventoryMoveItemEvent event) {
        handleTransferCancel(event.getItem(), event);
    }

    @EventHandler
    private void onInventoryTransaction(InventoryTransactionEvent event) {
        event.getTransaction().getActions().forEach(action -> {
            Item item = action.getSourceItem() != null
                    ? action.getSourceItem()
                    : action.getTargetItem();

            handleTransferCancel(item, event);
        });
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) return;
        Arrays.stream(event.getDrops()).forEach(item -> {
            if (!isTransferable(item)) item.setCount(0);
        });
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent event) {
        items.values()
                .stream()
                .filter(ItemBuilder::isGiveOnRespawn)
                .forEach(item -> event.getPlayer().getInventory().setItem(item.getIndex(), item.build()));
    }
}
