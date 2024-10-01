package fuckingRush;

import aic2024.user.*;

import fuckingRush.*;

public class UnitPlayer {

    // BRO literally have to add these fucking lines because statics are banned, dont make more of these!!!!
    public final constants constants = new constants();
    public navigation navigation;
    public final helper helper = new helper();
    public map map;
    public target target;
    public CarePackage myCarePackage;
    public comms comms;

    public void run(UnitController uc) {
        // Code to be executed only at the beginning of the unit's lifespan

        // TODO: Hard code the first few turns outside of the while loop to make more bytecode optimized
        // First turn code to be ran on the first turn
        uc.println("Bro this is the first turn, chill the fuck out");

        // Set the teams
        constants.myTeam = uc.getTeam();
        constants.opponentTeam = uc.getOpponent();

        // get the map X and Y
        constants.width = uc.getMapWidth();
        constants.height = uc.getMapHeight();

        // sets up the comms, figure out a good size for the buffer
        comms = new comms(uc, new Buffer(1000));

        // Set up the map
        map = new map(helper, constants, constants.width, constants.height, uc);

        // Sets the structure type
        if (uc.isStructure()) {
            constants.type = uc.getType();

            // Sets the vision radius
            constants.visionRadius = constants.type.getVisionRange();

            // Sets the is struct to true
            constants.isStructure = true;

            // Send the our initial positions
            // NOTE: this will be used by the HQs to start then it will contain what the bots should do
            int holder = map.locationToInt(uc.getLocation());
            uc.println("trying to save value " + holder);
            comms.commBroadcast(holder);

        } else {
            // Sets the astraunaut vision radius
            constants.visionRadius = GameConstants.ASTRONAUT_VISION_RANGE;

            // Sets the is struct to false
            constants.isStructure = false;

            // Gets the care package informartion
            myCarePackage = uc.getAstronautInfo().getCarePackage();

            // Saves the location the astraunaut spawned at (not the structure that spawned it location)
            constants.spawnLoc = uc.getLocation();
        }

        // Set up the target
        target = new target(map, constants);

        // set up the navigation
        navigation = new navigation(constants, map);


        uc.println("finished up first turn shit going into regular shit");

        while (true) {

            //Case in which we are a HQ
            // IMPORTANT TODO: limit making astraunauts as they cost oxygen
            if (constants.isStructure && uc.getType() == StructureType.HQ) {
                uc.println("I am HQ going to do HQ stuff");

                map.record(uc);

                // Turn 2 as HQ we are going to get all broadcasted ally HQ locations
                if (uc.getRound() == 2) {
                    // Get all ally HQ locations in the buffer
                    Buffer allyLocationsInInts = comms.getAllComms();
                    uc.println("The comms information is " + allyLocationsInInts);

                    // our HQs are the number of ally HQs plus 1
                    constants.ourHQs = new Location[allyLocationsInInts.size() + 1];

                    // cycle through our allies and copy them over
                    for (int index = 0; index < allyLocationsInInts.size(); index ++) {
                        constants.ourHQs[index] = map.intToLocation(allyLocationsInInts.get(index));
                    }

                    // Add in our location
                    constants.ourHQs[allyLocationsInInts.size()] = uc.getLocation();

                    // Save every possiblity of the symmetry
                    constants.canBeVerticl = map.canStillBeVertical();
                    constants.canBeHorizontal = map.canStillBeHorizontal();
                    constants.canBeRotational = map.canStillBeRotational();

                }

                if (uc.getRound() == 3) {
                    // broadcast the information
                    comms.commBroadcast(comms.threeBooleanToInt(constants.canBeHorizontal, constants.canBeVerticl, constants.canBeRotational));
                }

                if (uc.getRound() == 4) {
                    // We are going to cycle through the comms
                    Buffer getSymmetries = comms.getAllComms();

                    // Going to go through each comm and if a symmetry is false in this then we will update our own symmetry
                    for (int index = 0; index < getSymmetries.size(); index ++) {
                        boolean[] holder = comms.intToThreeBooleans(getSymmetries.get(index));
                        if (!holder[0]) {
                            constants.canBeHorizontal = false;
                        }

                        if (!holder[1]) {
                            constants.canBeVerticl = false;
                        }
                        if (!holder[2]) {
                            constants.canBeRotational = false;
                        }
                    }

                    uc.println("the comms information is hor " + constants.canBeHorizontal + " rot " + constants.canBeRotational + " vet " + constants.canBeVerticl);
                }

                // This will check if we know enemy HQ locations and set them if we know them
                // NOTE: Because of turn order we should be able to broadcast this information then retreive it in the same turn using the other bots
                if (uc.getRound() == 5 && navigation.doWeKnowEnemyHQAndSet()) {
                    for (Location allyLoc: map.myAstronautLocs) {
                        // IMPORTANT NOTE: Change this to better destination after testing
                        comms.commBroadcast(map.twoLocationsToInt(allyLoc, new Location (0, 0)));
                    }
                }

                for (Direction dir : constants.directions) {
                    // Settlement
                    uc.println("trying to make an settlement");
                    if (uc.canEnlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.SETTLEMENT)) {
                        uc.println("I think I can build");
                        uc.enlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.SETTLEMENT);
                        uc.println("after trying to build");
                        break;
                    }
                    // Dome
                    if (uc.canEnlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.DOME)) {
                        uc.enlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.DOME);
                        break;
                    }
                    // Reinforced suit
                    if (uc.canEnlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.REINFORCED_SUIT)) {
                        uc.enlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.REINFORCED_SUIT);
                        break;
                    }
                    // survival kit
                    if (uc.canEnlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.SURVIVAL_KIT)) {
                        uc.enlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.SURVIVAL_KIT);
                        break;
                    }
                    // Radio
                    if (uc.canEnlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.RADIO)) {
                        uc.enlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.RADIO);
                        break;
                    }
                    // hyper jump
                    if (uc.canEnlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.HYPERJUMP)) {
                        uc.enlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.HYPERJUMP);
                        break;
                    }
                    // Basic spawn
                    if (uc.canEnlistAstronaut(dir, constants.oxygenNoPackage, null)) {
                        uc.enlistAstronaut(dir, constants.oxygenNoPackage, null);
                        break;
                   }
                }
            }

            uc.println("checking amount left 1 " + uc.getPercentageOfEnergyLeft());

            // Code to be executed every round, if we are astraunaut and not being made
            if (!constants.isStructure && !uc.getAstronautInfo().isBeingConstructed()) {

                // record everything for the turn
                map.record(uc);
                uc.println("checking amount left 1.1 " + uc.getPercentageOfEnergyLeft());

                // Get the targets to go after
                Location end = null;

                // There should be instructions posted in the comms, will retreive them every turn
                // IMPORTANT NOTE: Eventually change this to allow bots to communicate with HQ
                Buffer possLocWithTarget = comms.getAllComms();
                for (int index = 0; index < possLocWithTarget.size(); index ++) {
                    Location[] holder = map.intToTwoLocations(possLocWithTarget.get(index));
                    Location ourLoc = holder[0];
                    Location possPermTarget = holder[1];
                    uc.println("I am getting ourLoc as " + ourLoc + " I am going to " + target);

                    // Check if ourLoc matches to then set target if it does as this is a command for us to move
                    if (ourLoc.equals(uc.getLocation())) {
                        target.permTarget = possPermTarget;
                        end = possPermTarget;
                    }
                }

                // Go try to sabatoge
                // IMPORTANT NOTE: Because sabotage takes priority they will try to do this over going to their target
                if (end == null) {
                    end = target.getClosestEnemyStructure(uc);
                }

                // Use perm target if we have one
                if (end == null) {
                    end = target.permTarget;
                }

                // only target care packages if u dont have one otherwiase there is a waste as picking one up kills u
                // NOTE: Going to bum rush so not caring about packages
//                if (myCarePackage == null && end == null) {
//                    // See if there are any packages near here
//                    end = target.getClosestBestPackage(uc);
//                }

                // Pick the center of the map for shits and giggles, I dont really like this
                // NOTE: targeting a location is significantly better
                if (end == null) {
                    end = new Location(constants.width / 2, constants.height / 2);
                }

                // Do the navigation stuff
                // IMPORATANT TODO: Finish the objects that get in the way to be better
                navigation.navigateTo(uc, null, end, map.bots);

                uc.println("checking amount left 1.2 " + uc.getPercentageOfEnergyLeft());

                // Try to sabotage anything we are near
                // TODO: We shouldnt check this every turn like this
                for (Direction dir : constants.directions) {
                    Location adjLocation = uc.getLocation().add(dir);
                    if (!uc.canSenseLocation(adjLocation)) continue;
                    uc.println("Checking " + adjLocation + " to see if there is an opponent permanet structure");
                    // TODO: Use grid logic later
                    StructureInfo possibleEnemy = uc.senseStructure(adjLocation);
                    if (possibleEnemy == null) continue;
                    if (possibleEnemy.getTeam().equals(constants.myTeam)) continue;
                    // Location opponentStructureLoc = map.grid_shit[adjLocation.x][adjLocation.y] == constants.oppoennt_permanent_structure ? adjLocation: null;
                    uc.println("going to try to sabotage");
                    if (uc.canPerformAction(ActionType.SABOTAGE, dir, 0)) {
                        uc.println("Sabotaging");
                        uc.performAction(ActionType.SABOTAGE, dir, 0);
                        break;
                    }
                }

                // Dont pick up care packages if u have a care package
                if (myCarePackage == null) {
                    //Check if there arsenseStructure(Location loc)e Care Packages at an adjacent tile. If so, retrieve them.
                    // TODO: We shouldnt check this every turn like this
                    for (Direction dir : constants.directions) {
                        Location adjLocation = uc.getLocation().add(dir);
                        if (!uc.canSenseLocation(adjLocation)) continue;
                        // TODO: Change this to use grid_shit
                        CarePackage cp = uc.senseCarePackage(adjLocation);
                        if (cp != null) {
                            if (uc.canPerformAction(ActionType.RETRIEVE, dir, 0)) {
                                uc.performAction(ActionType.RETRIEVE, dir, 0);
                                break;
                            }
                        }
                    }
                }

                uc.println("checking amount left 2 " + uc.getPercentageOfEnergyLeft());

                if (myCarePackage != null) {
                    // If we are Dome build dome in a surrounding square
                    // TODO: choose to build better than when we run out of oxygen
                    if (myCarePackage.equals(CarePackage.DOME) && uc.getAstronautInfo().getOxygen() <= 2) {
                        // Make this better later
                        int dirIndex = (int) (uc.getRandomDouble() * 8.0);
                        Direction randomDir = constants.directions[dirIndex];
                        for (int i = 0; i < 8; ++i) {
                            //Note that the 'value' of the following command is irrelevant.
                            if (uc.canPerformAction(ActionType.BUILD_DOME, randomDir, 0)) {
                                uc.performAction(ActionType.BUILD_DOME, randomDir, 0);
                                break;
                            }
                            randomDir = randomDir.rotateRight();
                        }
                    }

                    // If we are settlement build in a sourounding square
                    // TODO: choose to build better than when we run out of oxygen
                    if (myCarePackage.equals(CarePackage.SETTLEMENT) && uc.getAstronautInfo().getOxygen() <= 2) {
                        // Make this better later
                        int dirIndex = (int) (uc.getRandomDouble() * 8.0);
                        Direction randomDir = constants.directions[dirIndex];
                        for (int i = 0; i < 8; ++i) {
                            //Note that the 'value' of the following command is irrelevant.
                            if (uc.canPerformAction(ActionType.BUILD_SETTLEMENT, randomDir, 0)) {
                                uc.performAction(ActionType.BUILD_SETTLEMENT, randomDir, 0);
                                break;
                            }
                            randomDir = randomDir.rotateRight();
                        }
                    }
                }

                //If we have 1 or 2 oxygen left, terraform my tile (alternatively, terraform a random tile)
                if (uc.getAstronautInfo().getOxygen() <= 2) {
                    if (uc.canPerformAction(ActionType.TERRAFORM, Direction.ZERO, 0)) {
                        uc.performAction(ActionType.TERRAFORM, Direction.ZERO, 0);
                    } else {
                        int dirIndex = (int) (uc.getRandomDouble() * 8.0);
                        Direction randomDir = constants.directions[dirIndex];
                        dirIndex = (int) (uc.getRandomDouble() * 8.0);
                        randomDir = constants.directions[dirIndex];
                        for (int i = 0; i < 8; ++i) {
                            //Note that the 'value' of the following command is irrelevant.
                            if (uc.canPerformAction(ActionType.TERRAFORM, randomDir, 0)) {
                                uc.performAction(ActionType.TERRAFORM, randomDir, 0);
                                break;
                            }
                            randomDir = randomDir.rotateRight();
                        }
                    }
                }

                uc.println("checking amount left 3 " + uc.getPercentageOfEnergyLeft());
            }

            uc.println("ending my fucking turn");

            uc.yield(); // End of turn
        }
    }
}