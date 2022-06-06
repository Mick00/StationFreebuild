package fr.station47.stationFreebuild.misc.boost;

import fr.station47.stationAPI.api.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class BoostPapiExpansion extends PlaceholderExpansion {

    private BoostManager boostManager;

    public BoostPapiExpansion(BoostManager boostManager){
        this.boostManager = boostManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "boost";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Tabaribou";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return Possibly-null String of the requested identifier.
     */
    @Override
    public String onRequest(OfflinePlayer player, String identifier){
        if (identifier.equals("multiplier")){
            return boostManager.getCurrentBooster().getPlayerName();
        }
        if(identifier.equals("current")){
            return String.valueOf(boostManager.getCurrentBooster().getMultiplier());
        }
        if(identifier.equals("timeleft")){
            return Utils.millisToHumanTime("%hh %mm",boostManager.getCurrentBooster().timeLeft());
        }
        return null;
    }
}
