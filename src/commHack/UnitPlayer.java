package commHack;

import aic2024.engine.Unit;
import aic2024.user.*;

import commHack.*;

public class UnitPlayer {

    // BRO literally have to add these fucking lines because statics are banned, dont make more of these!!!!
    public final constants constants = new constants();
    public navigation navigation;
    public final helper helper = new helper();
    public map map;
    public target target;
    public CarePackage myCarePackage;
    public comms comms;
    public spawnTargets spawnTargets;
    public Health health;

    public void run(UnitController uc) {

        if (uc.isStructure()) {
            HQ HQ = new HQ();
            HQ.run(uc);
        } else {
            bot bot = new bot();
            bot.run(uc);
        }
    }
}