package fr.station47.stationFreebuild.misc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

public class OPTabComplete implements Listener {

    @EventHandler
    public void allowTabComplete(TabCompleteEvent event){
        if (!event.getSender().hasPermission("command.complete") && event.getBuffer().length() <= 3){
            event.getCompletions().clear();
        }
    }

}
