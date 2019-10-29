package fr.station47.stationFreebuild.misc;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.api.JobsJoinEvent;
import com.gamingmesh.jobs.container.JobProgression;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.List;

public class FirstJobBQTag implements Listener {
    @EventHandler
    public void giveTagOnFirstJob(JobsJoinEvent event){
        addFirstJobTag(event.getPlayer().getPlayer());
    }

    @EventHandler
    public void verifyOnJoin(PlayerJoinEvent event){
        if (Jobs.getPlayerManager().getJobsPlayer(event.getPlayer().getUniqueId()) !=null) {
            List<JobProgression> jobs = Jobs.getPlayerManager().getJobsPlayer(event.getPlayer()).getJobProgression();
            if (jobs.size() > 0) {
                addFirstJobTag(event.getPlayer());
            }
        }
    }

    private void addFirstJobTag(org.bukkit.entity.Player player){
        BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player.getName())).addTag("default.first_job");
    }
}
