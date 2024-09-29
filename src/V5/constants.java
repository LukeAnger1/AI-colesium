package V5;

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

    // This is the size of the ciruclar buffer
    public final int circularBufferSize = 100;

    public final byte land = 0;
    public final byte my_permanent_structure = 1;
    public final byte oppoennt_permanent_structure = 2;
    public final byte domes = 3;
//    public final byte hyper_jump = 4;
    public final byte noTravel = 4;
    public final byte travel = 5;
    public final byte hot_zones = 6;
    public final byte water = 7;


    // TODO: the below
    // 9. terraforms (semi permanent)

    // my bots (super temporary)
    // opponent bots (super temporary structures
}