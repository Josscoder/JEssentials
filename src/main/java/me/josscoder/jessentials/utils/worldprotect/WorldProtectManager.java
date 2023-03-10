package me.josscoder.jessentials.utils.worldprotect;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAir;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.*;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.level.ThunderChangeEvent;
import cn.nukkit.event.level.WeatherChangeEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.level.Level;
import cn.nukkit.utils.ConfigSection;
import lombok.Getter;
import me.josscoder.jessentials.JEssentialsPlugin;
import me.josscoder.jessentials.manager.Manager;

import java.util.HashSet;
import java.util.Set;

public class WorldProtectManager extends Manager implements Listener {

    private final ConfigSection worldProtectSection;
    private final Set<String> worldsToProtect = new HashSet<>();

    @Getter
    private static WorldProtectManager instance;

    public WorldProtectManager() {
        super(JEssentialsPlugin.getInstance().getConfig());
        this.worldProtectSection = config.getSection("world-protect");
        instance = this;
    }

    @Override
    public void init() {
        worldProtectSection.getKeys(false).forEach(key -> {
            boolean protect = worldProtectSection.getBoolean(key);
            if (protect) worldsToProtect.add(key);
        });
    }

    public boolean containsWorld(String world) {
        return worldsToProtect.contains(world);
    }

    public void unprotectWorld(String world) {
        worldsToProtect.remove(world);
        worldProtectSection.set(world, false);
        config.save();
    }

    public void protectWorld(String world) {
        worldsToProtect.add(world);
        worldProtectSection.set(world, true);
        config.save();
    }

    public void reloadFromConfig() {
        worldsToProtect.clear();
        init();
    }

    @Override
    public void close() {}

    private boolean handleCancel(Level level, Cancellable cancelable) {
        boolean cancel = containsWorld(level.getName());
        if (cancel) cancelable.setCancelled();
        return cancel;
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) return;

        if (handleCancel(entity.getLevel(), event) &&
                event.getCause().equals(EntityDamageEvent.DamageCause.VOID)
        ) {
            event.getEntity().teleport(entity.getLevel().getSafeSpawn());
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        handleCancel(event.getBlock().getLevel(), event);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        handleCancel(event.getBlock().getLevel(), event);
    }

    @EventHandler
    private void onBlockBurn(BlockBurnEvent event) {
        handleCancel(event.getBlock().getLevel(), event);
    }

    @EventHandler
    private void onBlockGrow(BlockGrowEvent event) {
        handleCancel(event.getBlock().getLevel(), event);
    }

    @EventHandler
    private void onBlockSpread(BlockSpreadEvent event) {
        handleCancel(event.getBlock().getLevel(), event);
    }

    @EventHandler
    private void onBlockFade(BlockFadeEvent event) {
        handleCancel(event.getBlock().getLevel(), event);
    }

    @EventHandler
    private void onBlockFall(BlockFallEvent event) {
        handleCancel(event.getBlock().getLevel(), event);
    }

    @EventHandler
    private void onBlockIgnite(BlockIgniteEvent event) {
        handleCancel(event.getBlock().getLevel(), event);
    }

    @EventHandler
    private void onBlockUpdate(BlockUpdateEvent event) {
        handleCancel(event.getBlock().getLevel(), event);
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        handleCancel(event.getPlayer().getLevel(), event);
    }

    @EventHandler
    private void onBedEnter(PlayerBedEnterEvent event) {
        handleCancel(event.getPlayer().getLevel(), event);
    }

    @EventHandler
    private void onBucketFill(PlayerBucketFillEvent event) {
        handleCancel(event.getPlayer().getLevel(), event);
    }

    @EventHandler
    private void onEditBook(PlayerEditBookEvent event) {
        handleCancel(event.getPlayer().getLevel(), event);
    }

    @EventHandler
    private void onItemConsume(PlayerItemConsumeEvent event) {
        handleCancel(event.getPlayer().getLevel(), event);
    }

    @EventHandler
    private void onInventoryTransaction(InventoryTransactionEvent event) {
        handleCancel(event.getTransaction().getSource().getLevel(), event);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!(event.getBlock() instanceof BlockAir)) handleCancel(event.getBlock().getLevel(), event);
    }

    @EventHandler
    private void onWorldLoad(LevelLoadEvent event) {
        Level level = event.getLevel();

        level.setTime(Level.TIME_DAY);
        level.stopTime();
        level.setRaining(false);
        level.setThundering(false);
    }

    @EventHandler
    private void onWorldThunderChange(ThunderChangeEvent event) {
        handleCancel(event.getLevel(), event);
    }

    @EventHandler
    private void onWorldWeatherChange(WeatherChangeEvent event) {
        handleCancel(event.getLevel(), event);
    }
}
