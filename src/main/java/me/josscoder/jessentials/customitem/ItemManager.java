package me.josscoder.jessentials.customitem;

import cn.nukkit.Player;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.ConfigSection;
import lombok.Getter;
import me.josscoder.jessentials.JEssentialsPlugin;
import me.josscoder.jessentials.manager.Manager;

import java.util.*;
import java.util.stream.Collectors;

public class ItemManager extends Manager {

    private final ConfigSection itemSection;

    @Getter
    private final Map<String, ItemBuilder> items = new HashMap<>();

    public ItemManager() {
        super(JEssentialsPlugin.getInstance().getConfig());
        this.itemSection = config.getSection("items");
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

            storeItem(itemBuilder);
        });
    }

    public void storeItem(ItemBuilder itemBuilder) {
        items.put(itemBuilder.getUniqueId(), itemBuilder);
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
                .contains(group)).collect(Collectors.toList());
    }

    @Override
    public void close() {}
}
