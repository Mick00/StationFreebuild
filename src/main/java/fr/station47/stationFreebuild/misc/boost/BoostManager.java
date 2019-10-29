package fr.station47.stationFreebuild.misc.boost;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.CurrencyType;
import fr.station47.stationFreebuild.StationFreebuild;
import fr.station47.theme.Theme;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.Queue;

public class BoostManager {

    private Queue<Boost> boostQueue;

    public BoostManager(){
        boostQueue = new LinkedList<>();
        BoostCmd boostCmd = new BoostCmd("boost", StationFreebuild.instance, this);
    }

    public void queueBoost(Boost boost){
        boostQueue.add(boost);
        if (!boostQueue.peek().isOngoing()){
            startBoost();
        }
    }

    private void startBoost(){
        Boost boost = boostQueue.peek();
        if (!boost.isDone() && !boost.isOngoing()) {
            if (boost.getType().equals(BoostType.EXP) || boost.getType().equals(BoostType.MONEY_AND_EXP)) {
                Jobs.getJobs().forEach(j -> j.addBoost(CurrencyType.EXP, boost.getMultiplier()));
            }
            if (boost.getType().equals(BoostType.MONEY) || boost.getType().equals(BoostType.MONEY_AND_EXP)) {
                Jobs.getJobs().forEach(j -> j.addBoost(CurrencyType.MONEY, boost.getMultiplier()));
            }
            boost.start();

            Theme.broadcast(ChatColor.YELLOW + boost.getPlayerName()+ ChatColor.RESET + " a déclenché un booster "+ ChatColor.AQUA+boost.getType().getName()+ChatColor.YELLOW+" x"+boost.getMultiplier());

            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (boost.isOngoing() && !boost.isDone()) {
                        stopBoost();
                    }
                }
            };
            runnable.runTaskLater(StationFreebuild.instance, boost.getDuration() / 50);
        }
    }

    private void stopBoost(){
        Boost boost = boostQueue.poll();
        boost.end();
        if (boost.getType().equals(BoostType.EXP) || boost.getType().equals(BoostType.MONEY_AND_EXP)){
            Jobs.getJobs().forEach(j-> j.addBoost(CurrencyType.EXP, 1));
        }
        if (boost.getType().equals(BoostType.MONEY) || boost.getType().equals(BoostType.MONEY_AND_EXP)){
            Jobs.getJobs().forEach(j-> j.addBoost(CurrencyType.MONEY, 1));
        }
        Theme.broadcast("Le booster de "+boost.getPlayerName()+" vient de se terminer");
        if (!boostQueue.isEmpty()){
            startBoost();
        }
    }

    public Boost getCurrentBooster(){
        if (boostQueue.isEmpty()){
            return new Boost("Aucun booster", BoostType.MONEY_AND_EXP,1, 1000);
        } else {
            return boostQueue.peek();
        }
    }

    public void next(){
        stopBoost();
    }
}
