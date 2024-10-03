package commHack;

import aic2024.engine.Unit;
import aic2024.user.*;

public class constants {
    public final Direction[] directions = Direction.values();
    public Team myTeam;
    public Team opponentTeam;
    public StructureType type;
    public int visionRadius;
    public boolean isStructure;
    public int width;
    public int height;

    // This is the max squared distance for the map
    public int maxDist = 214748364;
    public int min = -21478364;

    // This is the size of the ciruclar buffer
    public final int circularBufferSize = 100;

    public final byte land = 0;
    public final byte my_permanent_structure = 1;
    public final byte oppoennt_permanent_structure = 2;
    public final byte domes = 3;
//    public final byte terraform = ;
    public final byte noTravel = 4;
    public final byte travel = 5;
    public final byte hot_zones = 6;
    public final byte water = 7;

    // Keep track of what symmetries can be
    public boolean canBeRotational = true;
    public boolean canBeHorizontal = true;
    public boolean canBeVerticl = true;

    // This is how much oxygen to make an astraunaut with
    public final int oxygenNoPackage = 30;
    public final int oxygenWithPackage = 50;

    // This is how valuable eaach care package is, make negative to not collect
    public final int DOME_VALUE = 10;
    public final int HYPERJUMP_VALUE = -1; // TODO: implement the ones below to be able to use later
    public final int OXYGEN_TANK_VALUE = 14;
    public final int PLANTS_VALUE = 15;
    public final int RADIO_VALUE = -1;
    public final int REINFORCED_SUIT_VALUE = 6;
    public final int SETTLEMENT_VALUE = -1;
    public final int SURVIVAL_KIT_VALUE = 2;

    // This is ally HQs
    public Location[] ourHQs;
    public Location[] enemyHQs;
    public int ourHQIndex = -1; // This is used for ourHQs to keep track of what enemyHQ they need to target
    public Location ourLoc;

    // This is the spawn location
    public Location spawnLoc;

    // This function helps convert the CarePackage to the value (can't use enum because static is banned)
    public int getCarePackageValue(CarePackage carePackage) {
        if (carePackage == null) {
            return -1;
        }

        if (carePackage.equals(CarePackage.DOME)) {
            return DOME_VALUE;
        }
        if (carePackage.equals(CarePackage.HYPERJUMP)) {
            return HYPERJUMP_VALUE;
        }
        if (carePackage.equals(CarePackage.OXYGEN_TANK)) {
            return OXYGEN_TANK_VALUE;
        }
        if (carePackage.equals(CarePackage.PLANTS)) {
            return PLANTS_VALUE;
        }
        if (carePackage.equals(CarePackage.RADIO)) {
            return RADIO_VALUE;
        }
        if (carePackage.equals(CarePackage.REINFORCED_SUIT)) {
            return REINFORCED_SUIT_VALUE;
        }
        if (carePackage.equals(CarePackage.SETTLEMENT)) {
            return SETTLEMENT_VALUE;
        }
        if (carePackage.equals(CarePackage.SURVIVAL_KIT)) {
            return SURVIVAL_KIT_VALUE;
        }

        // Return 0 or some default value if no match is found
        return 0;
    }



    // TODO: the below
    // 9. terraforms (semi permanent)

    // my bots (super temporary)
    // opponent bots (super temporary structures
}