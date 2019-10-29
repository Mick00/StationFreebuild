package fr.station47.stationFreebuild.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

/**
 * Created by micdu on 3/11/2017.
 */
public class ChatManager implements Listener,CommandExecutor {
    private boolean slowMode = false;
    HashMap<String, Long> slowTime = new HashMap<>();

    private boolean muteAll = false;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("/chatman <info/slow/muteall>");
            return true;
        }

        switch (args[0]){
            case "muteall":
                if (!sender.hasPermission("station.chatman.muteall")) {
                    sender.sendMessage(ChatColor.RED+"Permission insufisante");
                    break;
                }
                if (muteAll){
                    muteAll = false;
                    Bukkit.broadcastMessage(ChatColor.BOLD+"Station"+ChatColor.BLUE+"47"+ChatColor.DARK_GRAY+" >> "+ChatColor.YELLOW+"Le chat n'est plus muet.");
                    break;
                }
                Bukkit.broadcastMessage(ChatColor.BOLD+"Station"+ChatColor.BLUE+"47"+ChatColor.DARK_GRAY+" >> "+ChatColor.YELLOW+"Le chat est maintenant muet.");
                muteAll = true;
                break;
            case "slow":
                if (!sender.hasPermission("station.chatman.slow")) {
                    sender.sendMessage(ChatColor.RED+"Permission insufisante");
                    break;
                }
                if (slowMode){
                    slowMode = false;
                    Bukkit.broadcastMessage(ChatColor.BOLD+"Station"+ChatColor.BLUE+"47"+ChatColor.DARK_GRAY+" >> "+ChatColor.YELLOW+"Le chat n'est plus en mode lent.");
                    break;
                }
                Bukkit.broadcastMessage(ChatColor.BOLD+"Station"+ChatColor.BLUE+"47"+ChatColor.DARK_GRAY+" >> "+ChatColor.YELLOW+"Le chat est désormais en mode lent.");
                slowMode = true;
                break;
            default:
                String isSlow = "non";
                if (slowMode)
                    isSlow = "oui";
                String isMuteAll = "non";
                if (muteAll)
                    isMuteAll = "oui";
                sender.sendMessage(ChatColor.YELLOW+"Slow mode activé: "+ChatColor.AQUA + ChatColor.BOLD+isSlow );
                sender.sendMessage(ChatColor.YELLOW+"Mute global:"+ChatColor.AQUA+ ChatColor.BOLD+isMuteAll);
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatEvent(AsyncPlayerChatEvent e)
    {
        Player sender = e.getPlayer();
        if (e.getMessage().length() > 1 && e.getMessage().charAt(0) == '!' && sender.hasPermission("station.staff"))
        {
            String message = e.getMessage();
            char channel = message.toLowerCase().charAt(1);

            if (channel == 'a' && sender.hasPermission("station.channel.admin.send"))
            {
                Bukkit.broadcast(ChatColor.RED+"AdminChat"+ ChatColor.DARK_GRAY+" >> "+e.getPlayer().getDisplayName()+ChatColor.DARK_GRAY+" >> "+ChatColor.YELLOW+e.getMessage().substring(message.indexOf(' ')+1),"station.channel.admin.receive");
            }
            else if (channel == 's' && sender.hasPermission("station.channel.staff.send"))
            {
                Bukkit.broadcast(ChatColor.DARK_BLUE+""+ChatColor.BOLD+"StaffChat"+ ChatColor.DARK_GRAY+" >> "+e.getPlayer().getDisplayName()+ChatColor.DARK_GRAY+" >> "+ChatColor.AQUA+e.getMessage().substring(message.indexOf(' ')+1),"station.channel.staff.receive");
            }
            else if (channel == 'b' && sender.hasPermission("station.channel.builder.send"))
            {
                Bukkit.broadcast(ChatColor.GOLD+""+ChatColor.BOLD+"BuilderChat"+ ChatColor.DARK_GRAY+" >> "+e.getPlayer().getDisplayName()+ChatColor.DARK_GRAY+" >> "+ChatColor.GRAY+e.getMessage().substring(message.indexOf(' ')+1),"station.channel.builder.receive");
            }

            else
            {
                sender.sendMessage(ChatColor.RED+"Ce canal n'existe pas :'(");
            }
            e.setCancelled(true);
            return;
        }
        if (sender.hasPermission("station.chatman.exempt")) {
            return;
        }

        if (muteAll){
            sender.sendMessage(ChatColor.RED+"Vous ne pouvez pas parler, le chat est muté globalement. ");
            e.setCancelled(true);
        }

        if (slowMode) {
            if (slowTime.get(sender.getName()) == null){
                slowTime.put(sender.getName(),System.currentTimeMillis());
                return;
            }

            if (((System.currentTimeMillis() - slowTime.get(sender.getName())) > 4000)) {
                slowTime.put(sender.getName(),System.currentTimeMillis());
                return;
            }
            e.setCancelled(true);
            sender.sendMessage(ChatColor.RED+"Le chat est en mode lent, vous ne parler qu'une fois tous les 4 secondes.");
        }

    }
}
