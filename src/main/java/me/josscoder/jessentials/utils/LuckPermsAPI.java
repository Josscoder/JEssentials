package me.josscoder.jessentials.utils;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsAPI {

    @Getter
    private static LuckPermsAPI instance;

    @Getter
    private LuckPerms provider = null;

    public void init() {
        instance = this;
        provider = LuckPermsProvider.get();
    }

    public boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

    public CachedMetaData getCachedMetaData(Player player) {
        return provider == null ? null : provider.getPlayerAdapter(Player.class)
                .getUser(player)
                .getCachedData()
                .getMetaData();
    }

    public String getPlayerPrefix(Player player) {
        return getPlayerPrefix(player, true);
    }

    public String getPlayerPrefix(Player player, boolean brackets) {
        CachedMetaData cachedMetaData = getCachedMetaData(player);
        
        String prefix = (cachedMetaData == null
                ? ""
                : (cachedMetaData.getPrefix() == null ? "" : cachedMetaData.getPrefix())
        );

        if (!brackets) {
            prefix = prefix.replace("[", "").replace("]", "");
        }

        return TextFormat.colorize(prefix);
    }

    public String getPlayerSuffix(Player player) {
        CachedMetaData cachedMetaData = getCachedMetaData(player);

        String suffix = (cachedMetaData == null
                ? ""
                : (cachedMetaData.getSuffix() == null ? "" : cachedMetaData.getSuffix())
        );

        return TextFormat.colorize(suffix);
    }

    public SortedMap<Integer, String> getPlayerPrefixes(Player player) {
        CachedMetaData cachedMetaData = getCachedMetaData(player);
        return cachedMetaData == null ? new TreeMap<>() : cachedMetaData.getPrefixes();
    }

    public SortedMap<Integer, String> getPlayerSuffixes(Player player) {
        CachedMetaData cachedMetaData = getCachedMetaData(player);
        return cachedMetaData == null ? new TreeMap<>() : cachedMetaData.getSuffixes();
    }

    public String getMetaValue(String key, Player player, String defaultValue) {
        CachedMetaData cachedMetaData = getCachedMetaData(player);
        return cachedMetaData == null
                ? defaultValue
                : (cachedMetaData.getMetaValue(key) == null
                ? ""
                : cachedMetaData.getMetaValue(key)
        );
    }

    public String getUsernameColor(Player player) {
        return TextFormat.colorize(getMetaValue("username-color", player, "&7"));
    }

    public String getMessageColor(Player player) {
        return TextFormat.colorize(getMetaValue("message-color", player, "&f"));
    }

    public String getSuffixColor(Player player) {
        return TextFormat.colorize(getMetaValue("suffix-color", player, "&f"));
    }

    public String getPlayerGroup(Player player) {
        return provider == null ? "Default" : provider.getPlayerAdapter(Player.class)
                .getUser(player)
                .getPrimaryGroup();
    }

    //Use Async

    public User getUser(UUID uniqueId) {
        UserManager userManager = provider.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(uniqueId);

        return userFuture.join(); // ouch! (block until the User is loaded)
    }

    public void addPermission(User user, String permission) {
        user.data().add(Node.builder(permission).build());

        if (provider == null) return;
        provider.getUserManager().saveUser(user);
    }

    public void addPermission(UUID uniqueId, String permission) {
        if (provider == null) return;
        provider.getUserManager().modifyUser(uniqueId, user -> {
            user.data().add(Node.builder(permission).build());
        });
    }

    public void removePermission(User user, String permission) {
        user.data().remove(Node.builder(permission).build());

        if (provider == null) return;
        provider.getUserManager().saveUser(user);
    }

    public void removePermission(UUID uniqueId, String permission) {
        if (provider == null) return;
        provider.getUserManager().modifyUser(uniqueId, user -> {
            user.data().remove(Node.builder(permission).build());
        });
    }
}
