package me.josscoder.jessentials.utils.luckformat;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.utils.ConfigSection;
import lombok.Getter;
import me.josscoder.jessentials.JEssentialsPlugin;
import me.josscoder.jessentials.utils.LuckPermsAPI;
import me.josscoder.jessentials.manager.Manager;

import java.util.SortedMap;

public class LuckFormatManager extends Manager implements Listener {

    private final ConfigSection formatSection;

    private String chatFormat;
    private String tagFormat;

    private boolean allowChatFormat;
    private boolean allowTagFormat;

    @Getter
    private static LuckFormatManager instance;

    public LuckFormatManager() {
        super(JEssentialsPlugin.getInstance().getConfig());
        this.formatSection = config.getSection("luck-format");
        instance = this;
    }

    @Override
    public void init() {
        allowChatFormat = formatSection.getBoolean("chat.allow");
        chatFormat = formatSection.getString("chat.value");
        allowTagFormat = formatSection.getBoolean("tag.allow");
        tagFormat = formatSection.getString("tag.value");

        if (!allowTagFormat) return;

        Server.getInstance().getScheduler().scheduleRepeatingTask(JEssentialsPlugin.getInstance(), () ->
                Server.getInstance().getOnlinePlayers().values().forEach(player -> {
                    if (!player.isOnline() || !player.isConnected()) return;
                    player.setNameTag(getFormattedPlayerTag(player));
                }),
                20,
                true
        );
    }

    public String getFormattedPlayerChat(Player player, String message) {
        LuckPermsAPI luckPermsAPI = LuckPermsAPI.getInstance();
        SortedMap<Integer, String> playerPrefixes = luckPermsAPI.getPlayerPrefixes(player);
        SortedMap<Integer, String> playerSuffixes = luckPermsAPI.getPlayerSuffixes(player);

        return chatFormat
                .replace("{name}", player.getName())
                .replace("{ping}", player.getPing() + "ms")
                .replace("{world}", player.getLevel().getName())
                .replace("{message}", message)
                .replace("{prefix}", luckPermsAPI.getPlayerPrefix(player))
                .replace("{suffix}", luckPermsAPI.getSuffixColor(player) + luckPermsAPI.getPlayerSuffix(player))
                .replace("{prefixes}", String.join("", playerPrefixes.values()))
                .replace("{suffixes}", String.join("", playerSuffixes.values()))
                .replace("{username-color}", luckPermsAPI.getUsernameColor(player))
                .replace("{message-color}", luckPermsAPI.getMessageColor(player));
    }

    public String getFormattedPlayerTag(Player player) {
        LuckPermsAPI luckPermsAPI = LuckPermsAPI.getInstance();
        SortedMap<Integer, String> playerPrefixes = luckPermsAPI.getPlayerPrefixes(player);
        SortedMap<Integer, String> playerSuffixes = luckPermsAPI.getPlayerSuffixes(player);

        return tagFormat
                .replace("{name}", player.getName())
                .replace("{ping}", player.getPing() + "ms")
                .replace("{world}", player.getLevel().getName())
                .replace("{prefix}", luckPermsAPI.getPlayerPrefix(player))
                .replace("{suffix}", luckPermsAPI.getSuffixColor(player) + luckPermsAPI.getPlayerSuffix(player))
                .replace("{prefixes}", String.join("", playerPrefixes.values()))
                .replace("{suffixes}", String.join("", playerSuffixes.values()))
                .replace("{username-color}", luckPermsAPI.getUsernameColor(player))
                .replace("{message-color}", luckPermsAPI.getMessageColor(player));
    }

    public void setChatFormat(String chatFormat) {
        this.chatFormat = chatFormat;
        formatSection.set("chat.value", chatFormat);
        config.save();
    }

    public void setTagFormat(String tagFormat) {
        this.tagFormat = tagFormat;
        formatSection.set("tag.value", tagFormat);
        config.save();
    }

    public boolean allowChatFormat() {
        return allowChatFormat;
    }

    public void setAllowChatFormat(boolean allowChatFormat) {
        this.allowChatFormat = allowChatFormat;
        formatSection.set("chat.allow", allowChatFormat);
        config.save();
    }

    public boolean allowTagFormat() {
        return allowTagFormat;
    }

    public void setAllowTagFormat(boolean allowTagFormat) {
        this.allowTagFormat = allowTagFormat;
        formatSection.set("tag.allow", allowTagFormat);
        config.save();
    }

    public void reloadFromConfig() {
        init();
    }

    @EventHandler
    private void onChat(PlayerChatEvent event) {
        if (allowChatFormat) event.setFormat(getFormattedPlayerChat(event.getPlayer(), event.getMessage()));
    }

    @Override
    public void close() {}
}
