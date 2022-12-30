package me.josscoder.jessentials.lobby;

import cn.nukkit.utils.ConfigSection;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.service.ServiceHandler;
import me.josscoder.jbridge.service.ServiceInfo;
import me.josscoder.jessentials.JEssentialsPlugin;
import me.josscoder.jessentials.manager.Manager;

import java.util.ArrayList;
import java.util.List;

public class LobbyManager extends Manager {

    private final ConfigSection lobbySection;

    private List<String> lobbyGroups;
    private String sortMode;

    public LobbyManager() {
        super(JEssentialsPlugin.getInstance().getConfig());
        this.lobbySection = config.getSection("lobby");
    }

    @Override
    public void init() {
        lobbyGroups = lobbySection.getStringList("groups");
        sortMode = lobbySection.getString("sortmode");
    }

    public boolean containsGroup(String group) {
        return lobbyGroups.contains(group);
    }

    public void addGroup(String group) {
        lobbyGroups.add(group);
        updateGroups();
    }

    public void removeGroup(String group) {
        lobbyGroups.remove(group);
        updateGroups();
    }

    private void updateGroups() {
        lobbySection.set("groups", lobbyGroups);
        config.save();
    }

    public void setSortMode(String sortMode) {
        this.sortMode = sortMode;
        lobbySection.set("sort-mode", sortMode.toUpperCase());
        config.save();
    }

    public List<ServiceInfo> getLobbyServices() {
        List<ServiceInfo> services = new ArrayList<>();
        lobbyGroups.forEach(lobbyGroup -> services.addAll(
                JBridgeCore.getInstance().getServiceHandler().getGroupServices(lobbyGroup)
        ));

        return services;
    }

    public ServiceInfo getSortedLobbyService() {
        return JBridgeCore.getInstance()
                .getServiceHandler()
                .getSortedServiceFromList(getLobbyServices(), ServiceHandler.SortMode.valueOf(sortMode));
    }

    public String geSortedLobbyServiceShortId() {
        ServiceInfo serviceInfo = getSortedLobbyService();
        return serviceInfo == null ? "" : serviceInfo.getShortId();
    }

    public void reloadFromConfig() {
        init();
    }

    @Override
    public void close() {}
}
