package fr.station47.stationFreebuild;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.FileUtils;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import fr.station47.stationAPI.api.Module;
import fr.station47.stationAPI.api.StationAPI;
import fr.station47.stationAPI.api.commands.MainCommand;
import fr.station47.stationAPI.api.commands.SubCommand;
import fr.station47.stationAPI.api.config.ConfigObject;
import fr.station47.theme.Theme;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class WorldRegen extends MainCommand implements Module{

    private MultiverseCore mvc = null;
    private String broadcastStartMessage;
    private List<String> worldsToRegen;
    private long timeBetweenRegen = 86400000 * 14;
    private BukkitRunnable regenTask;
    private String worldRegensInMessage;
    private ConfigObject config;
    private Random seedGenerator;


    public WorldRegen(String label) {
        super(label, StationFreebuild.instance);

        StationAPI.addModule(this);
        seedGenerator = new Random();

        Plugin plugin = StationFreebuild.instance.getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (plugin instanceof MultiverseCore) {
            mvc = (MultiverseCore) plugin;
        } else {
            Bukkit.getLogger().info("MultiverseCore not found, disabling regen functionnality");
        }

        config = new ConfigObject();
        config.put("worldRegen.broadcastStart","Le monde %worldname% est en train de se regénérer.");
        config.put("worldRegen.worldRegensIn","Le monde %worldname% se regénèrera dans %d jour(s), %h heure(s) et %m minute(s).");
        config.put("worldRegen.autoRegenWorlds", Arrays.asList("world_the_end","world_nether"));
        config.put("worldRegen.delayBetweenRegens",1209600000);
        reload();
        for (String world: worldsToRegen){
            config.put("worldRegen.lastRegen."+world,0);
        }
        reload();
        StationFreebuild.configs.applyModification("config",config);
        StationFreebuild.configs.save("config");


        regenTask = new BukkitRunnable() {
            @Override
            public void run() {
                List<String> toReg = new ArrayList<>();
                for (String world:worldsToRegen){
                    if (needsRegen(world)){
                        regenWorld(world,true);
                    }
                }

            }
        };
        regenTask.runTaskTimer(StationFreebuild.instance,0,500);

        addSubcommands(new SubCommand("regen","NomDuMonde","worldregen.regen") {
            @Override
            public boolean executeCommand(CommandSender sender, String[] args) {
                if (args.length==1){
                    if (regenWorld(args[0],false)){
                        Theme.sendMessage(sender,"Le monde a été régénérer");
                    } else {
                        Theme.sendMessage(sender,"Erreure");
                    }


                }
                return true;
            }
        });
        addSubcommands(new SubCommand("check","; Permet de voir quand les mondes seront regen","none") {
            @Override
            public boolean executeCommand(CommandSender sender, String[] args) {
                for (String world:worldsToRegen){
                    long timeLeft = timeBetweenRegen - timePassed(world);
                    double timeLeftSeconds = timeLeft/1000;
                    double daysLeft = Math.floor(timeLeftSeconds/86400);
                    double hoursLeft = Math.floor((timeLeftSeconds-daysLeft*86400)/3600);
                    double minutesLeft = Math.floor((timeLeftSeconds-daysLeft*86400-hoursLeft*3600)/60);
                    DecimalFormat df = new DecimalFormat("#");
                    Theme.sendMessage(sender,worldRegensInMessage.replaceAll("%worldname%",world)
                            .replace("%d",df.format(daysLeft))
                            .replace("%h",df.format(hoursLeft))
                            .replace("%m",df.format(minutesLeft)));
                }

                return true;
            }
        });
    }

    private boolean needsRegen(String worldName){
        return timePassed(worldName) > timeBetweenRegen;

    }

    private long timePassed(String worldName){
        ConfigObject configObject = new ConfigObject();
        configObject.put("worldRegen.lastRegen."+worldName, 0);
        configObject.loadFrom(StationFreebuild.configs.getConfig("config"));

        return System.currentTimeMillis() - Long.parseLong(String.valueOf(configObject.getMap().get("worldRegen.lastRegen."+worldName)));
    }

    private boolean regenWorld(String worldName, boolean updateLastRegen){

        if (worldsToRegen.contains(worldName)){
            Theme.broadcast(broadcastStartMessage.replaceAll("%worldname%",worldName));
            World worldToRegen = Bukkit.getWorld(worldName);
            World world;
            if (worldToRegen != null) {
                if (mvc.getMVWorldManager().isMVWorld(worldToRegen)){
                    mvc.getMVWorldManager().regenWorld(worldName,true,true,null);
                    world = mvc.getMVWorldManager().getMVWorld(worldName).getCBWorld();
                } else {
                    worldToRegen.getPlayers().forEach(player -> player.performCommand("spawn"));
                    Bukkit.getServer().unloadWorld(worldName, false);
                    File worldFile = new File(Bukkit.getWorldContainer(), worldName);
                    FileUtils.deleteFolder(worldFile);
                    world = Bukkit.createWorld(new WorldCreator(worldName).generator(worldToRegen.getGenerator()));
                    File lock = new File(worldFile,"session.lock");
                    try {
                        lock.createNewFile();
                        return false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    world.save();
                    world.setAutoSave(true);
                }
                ConfigObject worldConfig = new ConfigObject();
                worldConfig.put("worldRegen."+worldName+".schematic","spawnres");
                worldConfig.put("worldRegen."+worldName+".x",0);
                worldConfig.put("worldRegen."+worldName+".y",0);
                worldConfig.put("worldRegen."+worldName+".z",0);
                worldConfig.put("worldRegen."+worldName+".air",false);
                worldConfig.loadFrom(StationFreebuild.configs.getConfig("config"));
                pasteSchematics(world
                        , new File(Bukkit.getPluginManager().getPlugin("WorldEdit").getDataFolder() + "/schematics"
                        , worldConfig.getString("worldRegen."+worldName+".schematic")+".schematic")
                        ,worldConfig.getDouble("worldRegen."+worldName+".x")
                        ,worldConfig.getDouble("worldRegen."+worldName+".y")
                        ,worldConfig.getDouble("worldRegen."+worldName+".z")
                        ,worldConfig.getBoolean("worldRegen."+worldName+".air"));
                if (updateLastRegen) {
                    config.put("worldRegen.lastRegen." + worldName, System.currentTimeMillis());
                    StationFreebuild.configs.applyModification("config", config);
                    StationFreebuild.configs.save("config");
                }
                return true;
            }
        }
        return false;

    }

    private void pasteSchematics(World world, File file, double x, double y, double z, boolean air) {
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            if(reader !=null){
                Clipboard clipboard = reader.read();
                try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1)) {
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BlockVector3.at(x, y, z))
                            .ignoreAirBlocks(air)
                            .build();
                    Operations.complete(operation);
                } catch (WorldEditException ex){
                    Bukkit.getLogger().warning("An error occured while pasting the spawn schematic");
                }
            }
        } catch (IOException ex){
            Bukkit.getLogger().severe(ex.getMessage());
        }
    }

    @Override
    public void reload() {
        StationFreebuild.configs.loadOrDefault("config", config, true);
        config.loadFrom(StationFreebuild.configs.getConfig("config"));
        broadcastStartMessage = config.get("worldRegen.broadcastStart", String.class);
        worldRegensInMessage = config.get("worldRegen.worldRegensIn", String.class);
        worldsToRegen = config.getList("worldRegen.autoRegenWorlds", String.class);
        timeBetweenRegen = Integer.toUnsignedLong((int)config.getMap().get("worldRegen.delayBetweenRegens"))*1000;

    }

}
