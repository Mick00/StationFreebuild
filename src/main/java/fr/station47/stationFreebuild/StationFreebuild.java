package fr.station47.stationFreebuild;

import fr.station47.inventoryGuiApi.InventoryBuilder;
import fr.station47.stationAPI.api.StationAPI;
import fr.station47.stationAPI.api.config.ConfigHelper;
import fr.station47.stationAPI.api.config.ConfigObject;
import fr.station47.stationFreebuild.commands.DailyGift;
import fr.station47.stationFreebuild.jobsPlus.MoreJobs;
import fr.station47.stationFreebuild.misc.*;
import fr.station47.stationFreebuild.misc.boost.BoostManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class StationFreebuild extends JavaPlugin{

    public static ConfigHelper configs;
    public static StationFreebuild instance;
    public static BoostManager boostManager;

    @Override
    public void onEnable() {
        configs = new ConfigHelper(this);
        instance = this;
        configs.loadOrDefault("config",new ConfigObject());
        new DailyGift("cadeau");
        new WorldRegen("worldregen");
        boostManager = new BoostManager();

        AutoBroadcast ab = new AutoBroadcast();
        ab.startBroadcast();

        ChatManager chatManager = new ChatManager();
        Bukkit.getServer().getPluginManager().registerEvents(chatManager,this);
        getCommand("chatman").setExecutor(chatManager);

        if (StationAPI.isShopManagerActive()){
            StationAPI.shopManager.registerShop("merlin", p->p.performCommand("ce"));
            StationAPI.shopManager.registerShop("encanteur", p->p.performCommand("ah"));
        } else {
            Bukkit.getLogger().warning("Shop manager is not active. Merlin and Encanteur cannot be registered.");
        }

        this.getServer().getPluginManager().registerEvents(new MoreJobs(),this);
        this.getServer().getPluginManager().registerEvents(new NewAuctionBroadcast(), this);
        this.getServer().getPluginManager().registerEvents(new OPTabComplete(), this);
        this.getServer().getPluginManager().registerEvents(new FirstJobBQTag(),this);
    }

    public void onDisable(){

    }
}
