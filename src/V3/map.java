package V3;

import aic2024.user.*;

import V3.*;

public class map {
    // This class will act as the map for this shit

    // See constants for break down on the grid
    public byte[][] grid_shit;
    public constants constants;
    public helper helper;
    public StructureTypeByte StructureTypeByte;

    // These are things that are saved turn by turn
    public AstronautInfo[] myAstronauts;
    public Location[] myAstronautLocs;
    public AstronautInfo[] opponentAstronauts;
    public Location[] domes;
    public Location[] hotZones;
    public Location[] hyperJump;
    public Location[] lands;
    public Location[] terraforms;
    public Location[] water;
    public StructureInfo[] myStructures;
    public Location[] myStructureLocs;
    public StructureInfo[] opponentStructures;
    public Location[] opponentStructureLocs;



    public map (helper helper, constants constants, StructureTypeByte StructureTypeByte, int x, int y) {
        grid_shit = new byte[x][y];
        this.constants = constants;
        this.helper = helper;
        this.StructureTypeByte = StructureTypeByte;
    }

    public byte getByte(Location loc) {
        return grid_shit[loc.x][loc.y];
    }

    // This will setup all the information for the turn, make sure to run it every turn for the ionformation that is needed
    public void record(UnitController uc) {

        // Scan the surrounding objects
        // Get our astraunauts
        myAstronauts = uc.senseAstronauts((float) constants.visionRadius, constants.myTeam);
        // Get the map locations
        myAstronautLocs = new Location[myAstronauts.length];
        for (int index = 0; index < myAstronauts.length; index ++) {
            myAstronautLocs[index] = myAstronauts[index].getLocation();
        }

        // Get the opponent astraunauts
        opponentAstronauts = uc.senseAstronauts((float) constants.visionRadius, constants.opponentTeam);
        // Get the map locations
        Location[] oponnentAstronautLocs = new Location[opponentAstronauts.length];
        for (int index = 0; index < opponentAstronauts.length; index ++) {
            oponnentAstronautLocs[index] = opponentAstronauts[index].getLocation();
        }

        // Get the objects
        // DOME
        domes = uc.senseObjects(MapObject.DOME, (float) constants.visionRadius);
        // reset the DOMES that arent there

        // Save the DOMES


        // DOME TILES
        // Location[] domeTiles = uc.senseObjects(MapObject.DOMED_TILE, (float) constants.visionRadius);

        // HOT ZONES
        hotZones = uc.senseObjects(MapObject.HOT_ZONE, (float) constants.visionRadius);

        // HYPER JUMP
        hyperJump = uc.senseObjects(MapObject.HYPERJUMP, (float) constants.visionRadius);

        // LAND
        lands = uc.senseObjects(MapObject.LAND, (float) constants.visionRadius);

        // TERRAFORMED
        terraforms = uc.senseObjects(MapObject.TERRAFORMED, (float) constants.visionRadius);

        // WATER
        // NOTE: May not have to run all of these checks
        water = uc.senseObjects(MapObject.WATER, (float) constants.visionRadius);
        for (Location waterLoc : water) {
            grid_shit[waterLoc.x][waterLoc.y] = StructureTypeByte.WATER.getValue();
        }

        // Get my structures
        myStructures = uc.senseStructures((float) constants.visionRadius, constants.myTeam);
        // Get the map locations
        myStructureLocs = new Location[myStructures.length];
        for (int index = 0; index < myStructures.length; index ++) {
            myStructureLocs[index] = myStructures[index].getLocation();
        }

        // Get opponent structures
        opponentStructures = uc.senseStructures((float) constants.visionRadius, constants.opponentTeam);
        // Get the map locations
        opponentStructureLocs = new Location[opponentStructures.length];
        for (int index = 0; index < opponentStructures.length; index ++) {
            opponentStructureLocs[index] = opponentStructures[index].getLocation();
        }

        // This is the array of everything that is in the way
//        obstacles = helper.combineArrays(uc, myAstronautLocs, oponnentAstronautLocs, domes, water);

    }
}