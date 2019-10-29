package fr.station47.stationFreebuild.misc;

import fr.station47.stationAPI.api.Module;
import fr.station47.stationAPI.api.StationAPI;
import fr.station47.stationAPI.api.config.ConfigObject;
import fr.station47.stationFreebuild.StationFreebuild;
import fr.station47.theme.Theme;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoBroadcast implements Runnable, Module {

    private List<String> messages;
    private int messageIndex = 0;
    private int delay = 600;
    private ConfigObject config;
    private int id;

    public AutoBroadcast(){
        StationAPI.addModule(this);
        config = new ConfigObject();
        List<String> ex = new ArrayList<>();
        ex.add("Ceci est un exemple de broadcast");
        config.put("autobroadcast.messages",ex);
        config.put("autobroadcast.delay", 600);
        StationFreebuild.configs.loadOrDefault("config",config);
        messages = config.getStringList("autobroadcast.messages");
        messages = messages.stream().map(Theme::parse).collect(Collectors.toList());
        delay = config.getInt("autobroadcast.delay");

    }

    @Override
    public void run() {
        if (messageIndex >= messages.size()){
            messageIndex = 0;
        }
        Bukkit.broadcastMessage(messages.get(messageIndex));
        messageIndex++;
    }

    public void startBroadcast(){
        Bukkit.getScheduler().runTaskLater(StationFreebuild.instance,()->{
            id = Bukkit.getScheduler().runTaskTimer(StationFreebuild.instance,this,20,delay*20).getTaskId();
        },10);

    }

    public int getDelayInS(){
        return delay;
    }


    @Override
    public void reload() {
        Bukkit.getScheduler().cancelTask(id);
        StationFreebuild.configs.loadOrDefault("config",config,true);
        messages = config.getStringList("autobroadcast.messages");
        messages = messages.stream().map(Theme::parse).collect(Collectors.toList());
        delay = config.getInt("autobroadcast.delay");
        this.startBroadcast();
    }
}
