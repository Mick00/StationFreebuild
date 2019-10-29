package fr.station47.stationFreebuild.misc;

import fr.station47.stationAPI.api.Module;
import fr.station47.stationAPI.api.StationAPI;
import fr.station47.stationAPI.api.config.ConfigObject;
import fr.station47.stationFreebuild.StationFreebuild;

import fr.station47.theme.Theme;
import me.badbones69.crazyauctions.api.events.AuctionListEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import sun.security.krb5.Config;

/**
 * Created by micdu on 9/10/2017.
 */
public class NewAuctionBroadcast implements Listener, Module {

    private String broadcastMessage;
    private ConfigObject config;

    public NewAuctionBroadcast(){
        StationAPI.addModule(this);
        config = new ConfigObject();
        config.put("auction.newListingMessage","%p% vient de mettre un nouvel item en vente dans l'hôtel des ventes.");
        config.loadFrom(StationFreebuild.configs.getConfig("config"));
        broadcastMessage = (String)config.getMap().get("auction.newListingMessage");

    }

    @EventHandler
    public void onNewAuction(AuctionListEvent event){
        Theme.broadcast(broadcastMessage.replaceAll("%p%",event.getPlayer().getDisplayName()));
    }

    @Override
    public void reload() {
        config.loadFrom(StationFreebuild.configs.getConfig("config"));
        broadcastMessage = (String)config.getMap().get("auction.newListingMessage");
    }
}
