package fr.station47.stationFreebuild.misc.boost;

public class Boost {

    private String playerName;
    private BoostType type;
    private double multiplier;
    private long startedAt;
    private long duration;
    private boolean done    = false;
    private boolean ongoing = false;

    public Boost(String playerName, BoostType type, double multiplier, long duration) {
        this.playerName = playerName;
        this.type       = type;
        this.duration   = duration;
        this.multiplier = multiplier;
    }

    public void start() {
        startedAt   = System.currentTimeMillis();
        ongoing     = true;
    }

    public void end(){
        if (!done){
            done    = true;
            ongoing = false;
        }
    }

    public double getMultiplier() {
        return multiplier;
    }
    public boolean isDone() {return done;}

    public boolean isOngoing(){return ongoing;}

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public BoostType getType() {
        return type;
    }

    public void setType(BoostType type) {
        this.type = type;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String serealize(){
        long timeleft = startedAt+duration-System.currentTimeMillis();
        return playerName+"|"+type.toString()+"|"+(timeleft<0?duration:timeleft)+"|"+multiplier;
    }

    public static Boost unserealize(String s){
        String[] data = s.split("\\|");
        return new Boost(data[0],BoostType.valueOf(data[1]),Double.valueOf(data[2]),Long.valueOf(data[3]));
    }
}
