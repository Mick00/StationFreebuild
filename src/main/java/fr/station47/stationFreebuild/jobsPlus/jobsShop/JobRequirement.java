package fr.station47.stationFreebuild.jobsPlus.jobsShop;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import fr.station47.stationAPI.api.Shop.Requirement;
import fr.station47.stationAPI.api.StationAPI;
import fr.station47.stationAPI.api.Utils;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class JobRequirement extends Requirement {
    public JobRequirement(String jobName, int minLevel, String label, String errorMsg) {
        super(p->verifyJobReq(p, jobName, minLevel), Utils.fill(label,jobName,String.valueOf(minLevel)), Utils.fill(errorMsg,jobName,String.valueOf(minLevel)));
    }

    private static boolean verifyJobReq(Player p, String jobName, int minLevel){
        JobsPlayer jobs = Jobs.getPlayerManager().getJobsPlayer(p);
        JobProgression job = jobs.getJobProgression(Jobs.getJob(jobName));
        return job != null && job.getLevel() >= minLevel;
    }
}
