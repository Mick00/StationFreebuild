package fr.station47.stationFreebuild.misc.boost;

public enum BoostType {
    MONEY("Argent"),
    EXP("Expérience"),
    MONEY_AND_EXP("Argent et expérience");

    private String typeName;

    BoostType(String typeName){
        this.typeName = typeName;
    }

    public String getName() {return typeName;}

}
