package me.josscoder.jessentials.utils.item;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import lombok.Data;

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
    private boolean giveOnRespawn = false;
    private String[] commands = new String[]{};
    private String[] groups = new String[]{};

    public enum Action {
        DEFAULT,
        BLOCK_BREAK,
        BLOCK_PLACE,
        INTERACT_ENTITY
    }

    private BiConsumer<Player, Action> handler = (player, action) -> {};

    public static final String NBT_ITEM_KEY = "itemBuilder";

    public ItemBuilder store() {
        ItemManager.getInstance().storeItem(this);
        return this;
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
}
