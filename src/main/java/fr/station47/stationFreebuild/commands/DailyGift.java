package fr.station47.stationFreebuild.commands;

import fr.station47.stationAPI.api.commands.MainCommand;
import fr.station47.stationAPI.api.config.ConfigObject;
import fr.station47.stationFreebuild.StationFreebuild;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DailyGift extends MainCommand {

    YamlConfiguration config;
    public DailyGift(String label) {
        super(label, StationFreebuild.instance);
        config = StationFreebuild.configs.loadOrDefault("gifts",new ConfigObject());
    }

    @Override
    public boolean noArgs(CommandSender sender){

        if (sender instanceof Player) {

            long timePassed;
            if (!config.contains(sender.getName()) || (timePassed = System.currentTimeMillis() - config.getLong(sender.getName())) > 86400000) {
                double rdm = Math.random()*100;
                String[] message = {ChatColor.STRIKETHROUGH + "" + ChatColor.YELLOW + "-----------------------------\n", ChatColor.AQUA + "Voici votre cadeau journalier!\n", "", ChatColor.STRIKETHROUGH + "" + ChatColor.YELLOW + "-----------------------------\n"};

                ItemStack item;
                if (rdm < 20) {
                    item = new ItemStack(Material.EXPERIENCE_BOTTLE, 32);
                    message[2] = ChatColor.AQUA + "32 bouteilles d'exp\n";
                } else if (rdm < 40) {
                    item = new ItemStack(Material.DIAMOND, 1);
                    message[2] = ChatColor.AQUA + "1 diamant\n";
                } else if (rdm < 50) {
                    item = new ItemStack(Material.DIAMOND, 3);
                    message[2] = ChatColor.AQUA + "3 diamants\n";
                } else if (rdm < 70) {
                    item = new ItemStack(Material.CAKE, 12);
                    message[2] = ChatColor.AQUA + "12 gateaux\n";
                } else if (rdm < 90) {
                    item = new ItemStack(Material.ENDER_PEARL, 16);
                    message[2] = ChatColor.AQUA + "16 perles de l'end\n";
                } else {
                    item = new ItemStack(Material.DIAMOND_HORSE_ARMOR, 1);
                    message[2] = ChatColor.AQUA + "armure de cheval en diamant\n";
                }
                sender.sendMessage(message);
                ((Player) sender).getInventory().addItem(item);
                config.set(sender.getName(),System.currentTimeMillis());

            }
            else {
                long timeLeft = 86400000 - timePassed;
                long timeLeftSeconds = timeLeft/1000;
                long timeLeftMinutes = (timeLeftSeconds%3600)/60 ;
                long timeLeftHeures = timeLeftSeconds/3600;
                sender.sendMessage(ChatColor.RED +"Vous devez attendre " + timeLeftHeures + " heure(s) et "+ timeLeftMinutes + "minute(s) avant de recevoir un autre cadeau");
            }
        }
        return true;
    }

}
