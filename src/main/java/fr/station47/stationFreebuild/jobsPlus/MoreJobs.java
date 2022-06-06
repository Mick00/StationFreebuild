package fr.station47.stationFreebuild.jobsPlus;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import fr.station47.stationAPI.api.Utils;
import fr.station47.stationAPI.api.commands.MainCommand;
import fr.station47.stationAPI.api.commands.SubCommand;
import fr.station47.stationAPI.api.config.ConfigObject;
import fr.station47.stationFreebuild.StationFreebuild;
import fr.station47.stationFreebuild.jobsPlus.GUI.MainGUI;
import fr.station47.stationFreebuild.jobsPlus.jobsShop.JobShop;
import fr.station47.theme.Theme;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

public class MoreJobs extends MainCommand implements Listener {

    private ConfigObject config = new ConfigObject();
    private JobShop shop;

    public MoreJobs(){
        super("metier", StationFreebuild.instance);
        config.put("jobs.minLevelForNewJob", 60);
        config.put("jobs.minLevelNotReached", "Vous devez avoir atteint le niveau %minlevel% dans tous vos jobs avant de pouvoir rejoindre un nouveau métier");
        config.put("jobs.joinJob","Vous exercez maintenant le métier de {0}");
        StationFreebuild.configs.loadOrDefault("config",config);
        MainGUI.load();
        shop = new JobShop();
        //addSubcommands(openShopCMD());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onNewJobs(JobsJoinEvent event){
        if (event.getPlayer().getJobProgression().size()>0){
            if (!canTakeNewJob(event.getPlayer().getJobProgression())){
                event.setCancelled(true);
                Theme.sendMessage(event.getPlayer().getPlayer(), config.getString("jobs.minLevelNotReached").replace("%minlevel%",config.getString("jobs.minLevelForNewJob")));
                event.getPlayer().leaveJob(event.getJob());
            } else {
                Theme.sendMessage(event.getPlayer().getPlayer(), Utils.fill(config.getString("jobs.joinJob"),event.getJob().getName()));
            }
        }
    }

    @Override
    protected boolean noArgs(CommandSender sender){
        if (sender instanceof Player){
            Player player = ((Player) sender);
            if (!Jobs.getGCManager().getConfig().getStringList("Optimizations.DisabledWorlds.List").contains(player.getWorld().getName())){
                new MainGUI().open((Player)sender);
            }else {
                Theme.sendMessage(sender,"Vous ne pouvez ouvrir ce menu dans ce monde");
            }
        }
        return true;
    }

    private SubCommand openShopCMD(){
        return new SubCommand("shop","; Ouvre le menu du spécialiste","none") {
            @Override
            public boolean executeCommand(CommandSender sender, String[] args) {
                if (sender instanceof Player){
                    Player player = (Player) sender;
                    if (!Jobs.getGCManager().getConfig().getStringList("Optimizations.DisabledWorlds.List").contains(player.getWorld().getName())) {
                        openShop(player);
                    } else {
                        Theme.sendMessage(sender,"Vous ne pouvez ouvrir ce menu dans ce monde");
                    }
                } else {
                    Theme.sendMessage(sender, "Seulement les joueurs peuvent utiliser cette commande");
                }
                return true;
            }
        };
    }

    public void openShop(Player player) {
        shop.open(player);
    }

    public boolean canTakeNewJob(Player player){
        return canTakeNewJob(Jobs.getPlayerManager().getJobsPlayer(player).getJobProgression());
    }

    public boolean canTakeNewJob(List<JobProgression> jobs){
        int counter = 0;

        for (JobProgression job: jobs){
            if (job.getLevel()>= config.getInt("jobs.minLevelForNewJob")){
                ++counter;
                System.out.println(counter+"/"+jobs.size());
            }
        }
        return jobs.size()-1 <= counter;
    }

    public static JobsPlayer wrap(Player p){
        return Jobs.getPlayerManager().getJobsPlayer(p);
    }

    public int maxLevel(){
        return 0;
    }

}
