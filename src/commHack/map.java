package commHack;

import aic2024.user.*;

import commHack.*;

public class map {
    // This class will act as the map for this shit

    // See constants for break down on the grid
    public byte[][] grid_shit;
    public int width;
    public int height;
    public constants constants;
    public helper helper;
    public UnitController uc;

    // These are things that are saved turn by turn
    public AstronautInfo[] myAstronauts;
    public Location[] myAstronautLocs;
    public AstronautInfo[] opponentAstronauts;
    public Location[] oponnentAstronautLocs;
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
    public CarePackageInfo[] CarePackageInfos;
    public Location[] CarePackageLocs;
    public Location[] bots;



    public map (helper helper, constants constants, int x, int y, UnitController uc) {
        grid_shit = new byte[x][y];
        this.width = x;
        this.height = y;
        this.constants = constants;
        this.helper = helper;
        this.uc = uc;
    }

    public byte getByte(Location loc) {
        return grid_shit[loc.x][loc.y];
    }

    // This will setup all the information for the turn, make sure to run it every turn for the ionformation that is needed
    // TODO: Update the game map with more information later
    public void record(UnitController uc) {
        // IMPORTANT NOTE: Land is subject ot change as exploration happens and Travel is subject to change every turn

        // Scan the surrounding objects
        // Get our astraunauts
        myAstronauts = uc.senseAstronauts((float) constants.visionRadius, constants.myTeam);
        // Get the map locations
        // IMPORTANT TODO: Remove the myAstraunut locs and others as these are redudant with the grid shit
        myAstronautLocs = new Location[myAstronauts.length];
        for (int index = 0; index < myAstronauts.length; index ++) {
            myAstronautLocs[index] = myAstronauts[index].getLocation();
        }

//        uc.println("checking amount left inside map 2 " + uc.getPercentageOfEnergyLeft());

        // Get the opponent astraunauts
        opponentAstronauts = uc.senseAstronauts((float) constants.visionRadius, constants.opponentTeam);
        // Get the map locations
        oponnentAstronautLocs = new Location[opponentAstronauts.length];
        for (int index = 0; index < opponentAstronauts.length; index ++) {
            oponnentAstronautLocs[index] = opponentAstronauts[index].getLocation();
        }

        // Get the objects
        // DOME
        domes = uc.senseObjects(MapObject.DOME, (float) constants.visionRadius);
        // IMPORTANT TODO: reset the DOMES that arent there
        for (Location dome: domes) {
            grid_shit[dome.x][dome.y] = constants.domes;
        }

        // Save the DOMES
//        uc.println("checking amount left inside map 3 " + uc.getPercentageOfEnergyLeft());

        // DOME TILES
        // Location[] domeTiles = uc.senseObjects(MapObject.DOMED_TILE, (float) constants.visionRadius);

        // TODO: remove all structures
        // Get my structures
        myStructures = uc.senseStructures((float) constants.visionRadius, constants.myTeam);
        // Get the map locations
        myStructureLocs = new Location[myStructures.length];
        for (int index = 0; index < myStructures.length; index ++) {
            Location holder = myStructures[index].getLocation();
            myStructureLocs[index] = holder;
            // Include this in the grid map
            grid_shit[holder.x][holder.y] = constants.my_permanent_structure;
        }

        // Get opponent structures
        opponentStructures = uc.senseStructures((float) constants.visionRadius, constants.opponentTeam);
        // Get the map locations
        opponentStructureLocs = new Location[opponentStructures.length];
        for (int index = 0; index < opponentStructures.length; index ++) {
            Location holder = opponentStructures[index].getLocation();
            opponentStructureLocs[index] = holder;
            // Include this in the grid map
            grid_shit[holder.x][holder.y] = constants.oppoennt_permanent_structure;
        }

        // Get care package info
        CarePackageInfos = uc.senseCarePackages(constants.visionRadius);
        // Get the map locations
        CarePackageLocs = new Location[CarePackageInfos.length];
        for (int index = 0; index < CarePackageInfos.length; index ++) {
            CarePackageLocs[index] = CarePackageInfos[index].getLocation();
        }

        // LAND
        // TODO: Test this, we should make sure that this removes the domes and hyper jumps when they are gone
        // IMPORTANT NOTE: This should only remove what is visible this will cause issues!!!!
        lands = uc.senseObjects(MapObject.LAND, (float) constants.visionRadius);
        for (Location land: lands) {
            grid_shit[land.x][land.y] = constants.land;
        }

        // HOT ZONES
        hotZones = uc.senseObjects(MapObject.HOT_ZONE, (float) constants.visionRadius);
        for (Location hotZone: hotZones) {
            grid_shit[hotZone.x][hotZone.y] = constants.hot_zones;
        }

        // HYPER JUMP
        // NOTE: Not enough room in gytes to record this
        hyperJump = uc.senseObjects(MapObject.HYPERJUMP, (float) constants.visionRadius);
//        for (Location jump: hyperJump) {
//            grid_shit[jump.x][jump.y] = constants.hyper_jump;
//        }

        // TERRAFORMED
        terraforms = uc.senseObjects(MapObject.TERRAFORMED, (float) constants.visionRadius);

//        uc.println("checking amount left inside map 4 " + uc.getPercentageOfEnergyLeft());

        // WATER
        // NOTE: May not have to run all of these checks
        water = uc.senseObjects(MapObject.WATER, (float) constants.visionRadius);
        for (Location waterLoc : water) {
//            uc.println("adding water to " + waterLoc.x + " " + waterLoc.y);
//            uc.println("checking amount left inside map water " + uc.getPercentageOfEnergyLeft());
            grid_shit[waterLoc.x][waterLoc.y] = constants.water;
        }

        // NOTE: This is for bots
        bots = helper.combineArrays(myAstronautLocs, oponnentAstronautLocs);

//        uc.println("checking amount left inside map 5 " + uc.getPercentageOfEnergyLeft());

        // This is the array of everything that is in the way
//        obstacles = helper.combineArrays(uc, myAstronautLocs, oponnentAstronautLocs, domes, water);

        ///// Use this for debuging the map, causes too much energy loss (byte code loss)
//        for (int x = 0; x < this.x; x ++) {
//            for (int y = 0; y < this.y; y++) {
//                // mark the water
//                if (this.grid_shit[x][y] == constants.water) {
//                    uc.drawPointDebug(new Location(x, y), 0, 0, 255);
//                }
////                // mark the land
////                if (grid_shit[x][y] == StructureTypeByte.LANDS.getValue()) {
////                    uc.drawPointDebug(new Location(x, y), 0, 255, 0);
////                }
//            }
//        }

//        uc.println("checking amount left inside map 6 " + uc.getPercentageOfEnergyLeft());

        ///// Use this for debugging the map

    }

    // Add a travel square for transportation
    public boolean addTravel (int x, int y) {
        // Check if it is on the grid
        if (x < 0 || y < 0 || x >= constants.width || y >= constants.height) {
            return false;
        }

        // See if this square is marked as having been checked already
        if (grid_shit[x][y] == constants.noTravel) {
            return false;
        }
        if (grid_shit[x][y] == constants.travel) {
            return true;
        }

        // Going to check if can travel here, if so then we are done
        if (canTravel(x, y)) {
            grid_shit[x][y] = constants.travel;
            uc.drawPointDebug(new Location(x, y), 0, 255, 0);
            return true;
        }

        // marking as cant travel
        uc.drawPointDebug(new Location(x, y), 255, 0, 0);
        grid_shit[x][y] = constants.noTravel;

        // If not then I am going to recursively search the other squares
        // Side moves
        addTravel(x - 1, y); // Left
        addTravel(x + 1, y); // Right
        addTravel(x, y - 1); // Up
        addTravel(x, y + 1); // Down

        // NOTE: WHen change this to actual BFS we may want to remove these diagonal checks, IDK yet
        // Diagonal moves
        addTravel(x - 1, y - 1); // Top-left
        addTravel(x + 1, y - 1); // Top-right
        addTravel(x - 1, y + 1); // Bottom-left
        addTravel(x + 1, y + 1); // Bottom-right

        return false;
    }

    public boolean canTravel (int x, int y) {
        // Check if it is on the grid
        Location loc = new Location(x, y);
        if (uc.isOutOfMap(loc)) {
            return false;
        }

        byte holder = grid_shit[x][y];
        // TODO: Make sure there is nothing we dont think we can travel through
        return !helper.isIn(loc, bots) && (holder == constants.land || holder == constants.hot_zones || holder == constants.travel || holder == constants.domes);
    }

    public boolean canTravel (Location loc) {
        return canTravel(loc.x, loc.y);
    }

    ///// Find the symmerty information
    // Function to calculate rotational symmetry
    public int[] rotationalSymmetry(int x, int y) {
        int newX = width - x - 1;
        int newY = height - y - 1;
        return new int[]{newX, newY};
    }

    public Location rotationalSymmerty(Location loc) {
        int [] holder = rotationalSymmetry(loc.x, loc.y);
        return new Location(holder[0], holder[1]);
    }

    // Function to calculate horizontal symmetry
    public int[] horizontalSymmetry(int x, int y) {
        int newX = width - x - 1;
        int newY = y;
        return new int[]{newX, newY};
    }

    public Location horizontalSymmerty(Location loc) {
        int [] holder = horizontalSymmetry(loc.x, loc.y);
        return new Location(holder[0], holder[1]);
    }

    // Function to calculate vertical symmetry
    public int[] verticalSymmetry(int x, int y) {
        int newX = x;
        int newY = height - y - 1;
        return new int[]{newX, newY};
    }

    public Location verticalSymmerty(Location loc) {
        int [] holder = verticalSymmetry(loc.x, loc.y);
        return new Location(holder[0], holder[1]);
    }

    // TODO: Can optimize this number based off of size of map, make sure it is 2^n for shifting
    public final int locationConversionInt = 64;

    public int locationToInt(Location loc) {
        return loc.x * locationConversionInt + loc.y;
    }

    public Location intToLocation (int loc) {
        int x = loc / locationConversionInt;
        int y = loc % locationConversionInt;
        return new Location(x, y);
    }

    public int twoLocationsToInt(Location locA, Location locB) {
        int left = locationToInt(locA);
        int right = locationToInt(locB);
        return left * locationConversionInt*locationConversionInt + right;
    }

    public Location[] intToTwoLocations(int value) {
        // Dont judge me
        int right = value % (locationConversionInt*locationConversionInt);
        value = value / locationConversionInt / locationConversionInt;
        int left = value % (locationConversionInt*locationConversionInt);

        return new Location [] {intToLocation(left), intToLocation(right)};
    }

    // Try to eliminate symmetry
    public boolean canStillBeRotational() {
        // loop through all of our HQs
        for (Location ourHQLoc: constants.ourHQs) {

            uc.println("rot I am inside the for loop with our HQ loc " + ourHQLoc);

            // Rotate the HQ to be where the enemy should be
            Location possibleEnemyLoc = rotationalSymmerty(ourHQLoc);

            uc.println("rot while the distance is " + ourHQLoc.distanceSquared(possibleEnemyLoc) + " with an enemy location of " + possibleEnemyLoc);

            // Make sure it doesnt fall on top of our enemy locations
            if (helper.isIn(possibleEnemyLoc, constants.ourHQs)) {
                return false;
            }

            // Check if it should be visible
//            uc.println("the distance squared is " + ourHQLoc.distanceSquared(possibleEnemyLoc) + " and the vision radius is " + constants.visionRadius + " while ourHQ is at " + ourHQLoc);
            if (uc.getLocation().distanceSquared(possibleEnemyLoc) <= constants.visionRadius) {
                // If it should be viisble and isnt return false
                uc.println("hor possible enemy loc is in our HQs is " + helper.isIn(possibleEnemyLoc, constants.ourHQs) + " while our HQs are " + constants.ourHQs + " and the loc is " + possibleEnemyLoc);
                if (!helper.isIn(possibleEnemyLoc, opponentStructureLocs)) {
                    return false;
                }
            }

        }

        // return true
        return true;
    }

    // Try to eliminate horizontal symmetry
    public boolean canStillBeHorizontal() {
        // loop through all of our HQs
        for (Location ourHQLoc : constants.ourHQs) {

            uc.println("hor I am inside the for loop with our HQ loc " + ourHQLoc);

            // Reflect the HQ across the horizontal axis
            Location possibleEnemyLoc = horizontalSymmerty(ourHQLoc);

            uc.println("hor while the distance is " + ourHQLoc.distanceSquared(possibleEnemyLoc) + " with an enemy location of " + possibleEnemyLoc);

            // Make sure it doesnt fall on top of our enemy locations
            if (helper.isIn(possibleEnemyLoc, constants.ourHQs)) {
                return false;
            }

            // Check if it should be visible
            if (ourHQLoc.distanceSquared(possibleEnemyLoc) <= constants.visionRadius) {
                // If it should be visible and isn't, return false
                uc.println("hor possible enemy loc is in our HQs is " + helper.isIn(possibleEnemyLoc, constants.ourHQs) + " while our HQs are " + constants.ourHQs + " and the loc is " + possibleEnemyLoc);

                if (!helper.isIn(possibleEnemyLoc, opponentStructureLocs)) {
                    return false;
                }
            }
        }

        // return true
        return true;
    }

    // Try to eliminate vertical symmetry
    public boolean canStillBeVertical() {
        uc.println("checking if can be vertical");
        // loop through all of our HQs
        for (Location ourHQLoc : constants.ourHQs) {

            // Reflect the HQ across the vertical axis
            Location possibleEnemyLoc = verticalSymmerty(ourHQLoc);

            // Make sure it doesnt fall on top of our enemy locations
            if (helper.isIn(possibleEnemyLoc, constants.ourHQs)) {
                return false;
            }

            // Check if it should be visible
            if (ourHQLoc.distanceSquared(possibleEnemyLoc) <= constants.visionRadius) {
                // If it should be visible and isn't, return false
                if (!helper.isIn(possibleEnemyLoc, opponentStructureLocs)) {
                    return false;
                }
            }
        }

        // return true
        return true;
    }

    public double abs(double value) {
        return (value < 0) ? -value: value;
    }

    public int getTaxiDistenace(Location a, Location b) {
        return (int)(abs(a.x - b.x) + abs(a.y - b.y));
    }

}