package fr.station47.stationFreebuild.jobsPlus.GUI;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import fr.station47.inventoryGuiApi.ConfirmMenu;
import fr.station47.inventoryGuiApi.InventoryBuilder;
import fr.station47.inventoryGuiApi.inventoryAction.InventoryItem;
import fr.station47.stationFreebuild.StationFreebuild;
import fr.station47.stationFreebuild.jobsPlus.JobAction;
import fr.station47.theme.Theme;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import static com.gamingmesh.jobs.Jobs.getPlayerManager;

public class SelectJobGui {

    private InventoryBuilder inv = new InventoryBuilder(9,"Choississez un job", StationFreebuild.instance);
    private Player p;
    private boolean confirm = false;

    public SelectJobGui(Player player, JobAction jobAction){
        p = player;
        List<JobProgression> jobs = Jobs.getPlayerManager().getJobsPlayer(player).getJobProgression();
        for (int i = 0; i< jobs.size(); i++){
            final JobProgression job = jobs.get(i);
            inv.setItemAndAction(i, getGuiItem(job),e->jobAction.doOn((Player)e.getWhoClicked(), job));
        }
        inv.listenTo(true).unregisterListenerOnInvclose(true);
    }

    public SelectJobGui(Player player, JobAction jobAction, String confirmTitle, String confirmText, String cancelText){
        p = player;
        DecimalFormat df = new DecimalFormat("#.#");
        List<JobProgression> jobs = Jobs.getPlayerManager().getJobsPlayer(player).getJobProgression();
        for (int i = 0; i< jobs.size(); i++){
            final JobProgression job = jobs.get(i);
            InventoryItem item = new InventoryItem(i,job.getJob().getName(),job.getJob().getGuiItem().getType())
                    .setAction(e -> jobAction.doOn((Player)e.getWhoClicked(), job))
                    .addLore("Niv. "+job.getLevel())
                    .addLore("Exp: "+df.format(job.getExperience())+"/"+df.format(job.getMaxExperience()))
                    .setConfirm(true)
                    .confirmMessage(confirmTitle,confirmText,cancelText);
            inv.setInventoryItem(item);
        }
        inv.listenTo(true).unregisterListenerOnInvclose(true);
    }

    public void open(){
        p.openInventory(inv.build());
    }

    public void open(Player player){
        player.openInventory(inv.build());
    }

    private ItemStack getGuiItem(JobProgression progression){
        ItemStack item = new ItemStack(progression.getJob().getGuiItem().getType());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(progression.getJob().getName());
        DecimalFormat df = new DecimalFormat("#.#");
        meta.setLore(Arrays.asList(Theme.accentColor+"Niv.: "+ ChatColor.RESET+progression.getLevel(),Theme.accentColor+"EXP: "+ChatColor.RESET+df.format(progression.getExperience())+"/"+df.format(progression.getMaxExperience())));
        item.setItemMeta(meta);
        return item;
    }
}
