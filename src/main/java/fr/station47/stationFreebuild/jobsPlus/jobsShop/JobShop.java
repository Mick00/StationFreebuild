package fr.station47.stationFreebuild.jobsPlus.jobsShop;

import com.gamingmesh.jobs.container.JobProgression;
import fr.station47.stationAPI.api.Shop.BuyableItem;
import fr.station47.stationAPI.api.Shop.Shop;
import fr.station47.stationAPI.api.Shop.ShopItem;
import fr.station47.stationAPI.api.StationAPI;
import fr.station47.stationAPI.api.config.ConfigObject;
import fr.station47.stationAPI.api.gui.GUI;
import fr.station47.stationFreebuild.StationFreebuild;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobShop implements GUI {

    private Shop shop;
    private List<ShopItem> items;
    private ConfigObject config;

    public JobShop(){
        config = new ConfigObject();
        config.setPathPrefix("jobs.shop.");
        config.put("pnjName", "Spécialiste");
        config.put("reqLabel", "Métier: {0}\nNiv. requis: {1}");
        config.put("reqNotMet", "Vous devez être niveau {0} {1} minimum pour acheter cet item.");
        StationFreebuild.configs.loadOrDefault("jobsgui",config);
        if (StationAPI.isShopManagerActive()){
            StationAPI.shopManager.registerShop(config.getString("pnjName"), this);
        }
        items = new ArrayList<>();
        ItemStack unbreakableHoe = new ItemStack(Material.GOLDEN_HOE);
        ItemMeta meta = unbreakableHoe.getItemMeta();
        meta.setUnbreakable(true);
        unbreakableHoe.setItemMeta(meta);
        BuyableItem buyablehoe = new BuyableItem(unbreakableHoe,1000);
        buyablehoe.addRequirement(new JobRequirement("fermier", 90,config.getString("reqLabel"),config.getString("reqNotMet")));
        ShopItem hoeItem = new ShopItem("Houe incassable", Arrays.asList("Une houe qui ne peut etre brisee"),buyablehoe);
        hoeItem.setGuiMat(Material.GOLDEN_HOE);
        items.add(hoeItem);
        shop = new Shop(config.getString("pnjName"),items, false);
    }


    @Override
    public void open(Player player) {
        shop.open(player);
    }
}
