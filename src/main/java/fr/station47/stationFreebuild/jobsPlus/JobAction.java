package fr.station47.stationFreebuild.jobsPlus;

import com.gamingmesh.jobs.container.JobProgression;
import fr.station47.inventoryGuiApi.inventoryAction.InventoryAction;
import org.bukkit.entity.Player;

public interface JobAction {
    void doOn(Player p, JobProgression job);
}
