package V4;

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

    public final byte water = 7;

    // TODO: the below
    // 8. hyper jump (semi permanent)
    // 9. terraforms (semi permanent)

    // my bots (super temporary)
    // opponent bots (super temporary structures
}