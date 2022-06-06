package fr.station47.stationFreebuild.jobsPlus.GUI;

import com.gamingmesh.jobs.container.JobProgression;
import fr.station47.inventoryGuiApi.InventoryBuilder;
import fr.station47.inventoryGuiApi.inventoryAction.InventoryItem;
import fr.station47.stationAPI.api.Utils;
import fr.station47.stationAPI.api.config.ConfigObject;
import fr.station47.stationFreebuild.StationFreebuild;
import fr.station47.stationFreebuild.jobsPlus.MoreJobs;
import fr.station47.theme.Theme;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainGUI {
    private InventoryBuilder builder;
    private static ConfigObject config;

    public static void load(){
        config = new ConfigObject();
        config.setPathPrefix("jobs.gui.");
        config.put("mainName","Métiers");
        config.put("leaveJob","Quitter un métier");
        config.put("confirmLeaveTitle","Êtes-vous certain de vouloir démissionner?");
        config.put("confirmLeave","Démisionner");
        config.put("cancel","Annuler");
        config.put("joinJob","Rejoindre un métier");
        config.put("levelLabel","Niv. {0}");
        config.put("expLabel","Exp: {0}/{1}");
        config.put("shoptitle","Boutique du spécialiste");
        config.put("shoplore","En développement");
        List<String> description = new ArrayList<>(6);
        description.add("- Les jobs vous permettent de gagner de l'argent");
        description.add("- Vous pouvez exercer un nouveau métier lorsque");
        description.add("tous vos métiers ont atteint le niveau minimum");
        config.put("jobDescription", description);
        StationFreebuild.configs.loadOrDefault("jobsgui",config);
    }

    public MainGUI(){
        builder = new InventoryBuilder(27,config.getString("mainName"), StationFreebuild.instance);
        builder/*.setInventoryItem(openShop(2))*/
                .setInventoryItem(info(0))
                .setInventoryItem(joinJob(3))
                .setInventoryItem(quitJob(5))
                .listenTo(true).unregisterListenerOnInvclose(true);

    }

    public void open(Player p){
        List<JobProgression> jobs = MoreJobs.wrap(p).getJobProgression();
        for (int i = 0; i <= 17; i++){
            if ( i >= jobs.size()){
                builder.setInventoryItem(new InventoryItem(9+i,"", Material.GLASS_PANE));
            } else {
                builder.setInventoryItem(getJobStats(jobs.get(i)).setSlot(i+9));
            }
        }
        p.openInventory(builder.build());
    }

    private InventoryItem info(int slot){
        InventoryItem item = new InventoryItem(slot,config.getString("mainName"),Material.GOLD_BLOCK);
        //item.setLore(config.getStringList("jobDescription"));
        return item;
    }

    private InventoryItem getJobStats(JobProgression job){
        DecimalFormat df = new DecimalFormat("#.##");
        return new InventoryItem(0,job.getJob().getName(),job.getJob().getGuiItem().getType())
                .addLore(Utils.fill(config.getString("levelLabel"),df.format(job.getLevel())))
                .addLore(Utils.fill(config.getString("expLabel"),df.format(job.getExperience()),df.format(job.getMaxExperience())));
    }

    private InventoryItem joinJob(int slot){
        return new InventoryItem(slot,config.getString("joinJob"), Material.EMERALD)
            .setAction(e -> ((Player) e.getWhoClicked()).performCommand("jobs browse"));
    }

    private InventoryItem quitJob(int slot) {
        return new InventoryItem(slot,config.getString("leaveJob"), Material.BARRIER)
                .setAction(e->new SelectJobGui((Player)e.getWhoClicked(),
                        (p,j) -> p.performCommand("jobs leave "+j.getJob().getName())
                        , config.getString("confirmLeaveTitle")
                        , config.getString("confirmLeave")
                        , config.getString("cancel")).open());
    }

    private InventoryItem openShop(int slot){
        return new InventoryItem(slot,config.getString("shoptitle"), Material.DIAMOND_AXE)
                .setAction(e-> Theme.sendMessage(e.getWhoClicked(),config.getString("shoplore")));
    }
}
