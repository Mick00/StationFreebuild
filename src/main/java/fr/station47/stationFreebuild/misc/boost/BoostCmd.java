package fr.station47.stationFreebuild.misc.boost;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.CurrencyType;
import fr.station47.stationAPI.api.Utils;
import fr.station47.stationAPI.api.commands.MainCommand;
import fr.station47.stationAPI.api.commands.SubCommand;
import fr.station47.theme.Theme;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class BoostCmd extends MainCommand{

    private BoostManager bm;

    public BoostCmd(String label, JavaPlugin plugin, BoostManager bm) {
        super(label, plugin);
        this.bm = bm;
        addsubcommands();
    }

    private void addsubcommands(){
        SubCommand add = new SubCommand("add","<money,exp,money_and_exp> <multiplier> <temps en sec> <declencheur>; Ajoute un booster","boost.add") {
            @Override
            public boolean executeCommand(CommandSender sender, String[] args) {
                String declencheur;
                double multiplier;
                BoostType type;
                long duration;

                if (args.length < 3) {
                    return false;
                }
                type = BoostType.valueOf(args[0].toUpperCase());

                if (Utils.isNumber(args[1])){
                    multiplier = Double.valueOf(args[1]);
                } else {
                    Theme.sendMessage(sender, "Le multiplicateur n'est pas un nombre");
                    return false;
                }

                if (Utils.isNumber(args[2])){
                    duration = Long.valueOf(args[2]);
                } else {
                    Theme.sendMessage(sender, "La duree du boost n'est pas un nombre en secondes");
                    return false;
                }

                if (args.length == 3){
                    declencheur = sender.getName();
                } else {
                    declencheur = String.join(" ",Arrays.copyOfRange(args,3,args.length));
                }
                bm.queueBoost(new Boost(declencheur,type,multiplier,duration*1000));
                return true;
            }
        };
        addSubcommands(add);

        SubCommand check = new SubCommand("check","; Affiche le booster en cours","none") {
            @Override
            public boolean executeCommand(CommandSender sender, String[] args) {
                Boost boost = bm.getCurrentBooster();
                Theme.sendMessage(sender,"Voici le booster actuel");
                sender.sendMessage(Theme.accentColor+"Type: "+ ChatColor.RESET + boost.getType().getName()+" x"+boost.getMultiplier());
                sender.sendMessage(Theme.accentColor+"Déclenché par "+ ChatColor.RESET + boost.getPlayerName());
                sender.sendMessage(Theme.accentColor+"Temps restant: "+ ChatColor.RESET  + Utils.millisToHumanTime("%h heure(s) %m minute(s) et %s seconde(s)", boost.getStartedAt()+boost.getDuration()-System.currentTimeMillis()));
                return true;
            }
        };
        addSubcommands(check);

        SubCommand next = new SubCommand("next", "; Passe au prochain Booster s'il y en a un","boost.next") {
            @Override
            public boolean executeCommand(CommandSender sender, String[] args) {
                bm.next();
                return true;
            }
        };
        addSubcommands(next);

        SubCommand debug = new SubCommand("debug","; Shows boost level from Jobs","boost.debug"){
            @Override
            public boolean executeCommand(CommandSender sender, String[] args) {
                Jobs.getJobs().forEach(j->{
                    sender.sendMessage(j.getName()+": money "+j.getBoost().get(CurrencyType.MONEY)+", exp: "+j.getBoost().get(CurrencyType.EXP));
                });
                return true;
            }
        };
        addSubcommands(debug);

    }
}
