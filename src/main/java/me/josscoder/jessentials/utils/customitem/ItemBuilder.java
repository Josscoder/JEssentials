package me.josscoder.jessentials.utils.customitem;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import lombok.Data;
import me.josscoder.jessentials.JEssentialsPlugin;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.BiConsumer;

@Data
public class ItemBuilder implements Listener {

    private String uniqueId = UUID.randomUUID().toString();
    private int index;
    private int id;
    private int meta;
    private int count;
    private String customName;
    private CompoundTag customData = new CompoundTag();
    private String[] lore = new String[]{};
    private Enchantment[] enchantments = new Enchantment[]{};
    private boolean transferable = false;
    private String[] commands = new String[]{};
    private String[] groups = new String[]{};

    private static final String NBT_ITEM_KEY = "itemBuilder";
    private static final String NBT_COOLDOWN_KEY = "itemBuilderCooldown";

    public enum Action {
        DEFAULT,
        BLOCK_BREAK,
        BLOCK_PLACE,
        INTERACT_ENTITY
    }

    private BiConsumer<Player, Action> handler = (player, action) -> {};

    public ItemBuilder() {
        JEssentialsPlugin.getInstance().registerListener(this);
    }

    public Item build() {
        customData.putString(NBT_ITEM_KEY, uniqueId);

        Item item = new Item(id, meta, count)
                .setCustomBlockData(customData)
                .setLore(lore)
                .setCustomName(TextFormat.colorize(customName));
        Arrays.stream(enchantments).forEach(item::addEnchantment);

        return item;
    }

    private void handleExecution(Item item, Player player, Action action) {
        if (item.getCustomBlockData() == null) return;

        String itemUniqueId = item.getCustomBlockData().getString(NBT_ITEM_KEY);
        if (itemUniqueId.isEmpty() || !itemUniqueId.equalsIgnoreCase(uniqueId)) return;

        if (player.namedTag.contains(NBT_COOLDOWN_KEY) &&
                (player.namedTag.getLong(NBT_COOLDOWN_KEY) + 500) >= Instant.now().toEpochMilli()
        ) {
            return;
        }

        player.namedTag.putLong(NBT_COOLDOWN_KEY, Instant.now().toEpochMilli());
        handler.accept(player, action);
        Arrays.stream(commands).forEach(command -> player.getServer().dispatchCommand(player, command));
    }

    @EventHandler
    private void onItemInteract(PlayerInteractEvent event) {
        handleExecution(event.getItem(), event.getPlayer(), Action.DEFAULT);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        handleExecution(event.getItem(), event.getPlayer(), Action.BLOCK_BREAK);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        handleExecution(event.getItem(), event.getPlayer(), Action.BLOCK_PLACE);
    }

    @EventHandler
    private void onHitEntity(PlayerInteractEntityEvent event) {
        handleExecution(event.getItem(), event.getPlayer(), Action.INTERACT_ENTITY);
    }

    private void handleTransferCancel(Item item, Cancellable cancellable) {
        if (item.getCustomBlockData() == null) return;

        String itemUniqueId = item.getCustomBlockData().getString(NBT_ITEM_KEY);
        if (itemUniqueId.isEmpty() || !itemUniqueId.equalsIgnoreCase(uniqueId)) return;

        cancellable.setCancelled(!transferable);
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
    private void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) return;
        Arrays.stream(event.getDrops()).forEach(item -> handleTransferCancel(item, event));
    }
}
