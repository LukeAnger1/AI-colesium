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
        uc.println("my ID is " + uc.getID());

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

        // Set up the health
        health = new Health(map, constants);

        // Set up the spawn licatinos
        spawnTargets = new spawnTargets(uc, health, constants);

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

//             If this is empty I am first, then broadcast so others know they arent first
            Buffer amINotFirst = comms.getAllComms();

            uc.println("the comms are " + amINotFirst);

            if (amINotFirst.isEmpty()) {
                // Set to I am first then broadcast to others
                constants.isFirstHQ = true;
//                uc.performAction(ActionType.BROADCAST, null, comms.nullMessage);
            } else {
                // set to I am not first and poll so that the broadcast is empty
                constants.isFirstHQ = false;
                // broadcast the buffer so that it can stay there for future turns

            }

            uc.println("I am first is " + constants.isFirstHQ);

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
        // Add the below into a constructor
        navigation.uc = uc;
        navigation.helper = helper;


        uc.println("finished up first turn shit going into regular shit");

        while (true) {
            // This sets our location
            constants.ourLoc = uc.getLocation();

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

                    // Sort them for same across all HQs
//                    uc.println("before sorting " + constants.ourHQs);
//                    uc.yield();
                    navigation.sortLocationArray(constants.ourHQs);
//                    uc.println("after sorting " + constants.ourHQs);
//                    uc.yield();

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
                if (uc.getRound() == 5) {
//                    uc.println("I am going to yield 1");
//                    uc.yield();
//                    uc.println("in round " + uc.getRound());
                    if (navigation.doWeKnowEnemyHQAndSet()) {
                        uc.println("really in round 5 info");
                        uc.println("I am going to broadcast " + map.twoLocationsToInt(constants.ourLoc, constants.enemyHQs[constants.ourHQIndex]));
                        comms.commBroadcast(map.twoLocationsToInt(constants.ourLoc, constants.enemyHQs[constants.ourHQIndex]));
                    }
//                    uc.println("I am going to yield 2");
//                    uc.yield();
                }

                // Need to do this better
                // IMPORTANT TODO: got to makwe sure that doWeKnowAndSet alwasy sets
                if (uc.getRound() > 5 && uc.getRound() % 2 == 1) {
//                if (uc.getRound() > 5) {
                    uc.println("in round " + uc.getRound());
                    // Going to see if we can set
                    navigation.doWeKnowEnemyHQAndSet();
                    // IMPORTANT NOTE: Change this to better destination after testing
//                    uc.println("I am going to yield 10 my HQ index is " + constants.ourHQIndex);
//                    uc.yield();
                    uc.println("I am going to broadcast " + map.twoLocationsToInt(constants.ourLoc, constants.enemyHQs[constants.ourHQIndex]) + " our Loc is " + constants.ourLoc + " enemy is " + constants.enemyHQs[constants.ourHQIndex]);
//                    uc.yield();
                    comms.commBroadcast(map.twoLocationsToInt(constants.ourLoc, constants.enemyHQs[constants.ourHQIndex]));
                }

                int oxygen = health.getHealthSuggested();
                Direction dir = spawnTargets.getSpawnLoc(uc.getLocation(), oxygen);

                // TODO: consider not using max amount of oxygen
                while (dir != null) {
                    uc.enlistAstronaut(dir, oxygen, null);

                    dir = spawnTargets.getSpawnLoc(uc.getLocation(), oxygen);
                }

            }

//            uc.println("checking amount left 1 " + uc.getPercentageOfEnergyLeft());

            // Code to be executed every round, if we are astraunaut and not being made
            if (!constants.isStructure && !uc.getAstronautInfo().isBeingConstructed()) {

                // record everything for the turn
                map.record(uc);
//                uc.println("checking amount left 1.1 " + uc.getPercentageOfEnergyLeft());

                // Get the targets to go after
                Location end = null;

                if (uc.getRound() % 2 == 1) {
                    // There should be instructions posted in the comms, will retreive them every turn
                    // IMPORTANT NOTE: Eventually change this to allow bots to communicate with HQ
                    Buffer possLocWithTarget = comms.getAllComms();
                    //                uc.println("the possible loc with target are " + possLocWithTarget);
                    for (int index = 0; index < possLocWithTarget.size(); index++) {
                        Location[] holder = map.intToTwoLocations(possLocWithTarget.get(index));
                        Location ourLoc = holder[0];
                        Location possPermTarget = holder[1];
                        //                    uc.println("I am getting parent location as " + ourLoc + " I am going to " + target);

                        // Check if ourLoc matches to then set target if it does as this is a command for us to move
                        uc.println("th epossible parent location is " + ourLoc + " the target location is " + possPermTarget);

                        // Skip the targets we dont want to hit anymore
                        if (helper.isIn(possPermTarget, constants.eleminatedTargets)) {
                            continue;
                        }
                        if (ourLoc.equals(uc.getParent().getLocation())) {
                            target.permTarget = possPermTarget;
                            end = possPermTarget;
                            uc.println("setting as target " + target.permTarget);
                            break;
                        }

                        // set as the secondary target if we dont have one yet
                        int distHolder = constants.ourLoc.distanceSquared(possPermTarget);
                        if ((target.secondaryDist == -1) || (target.secondaryDist > distHolder)) {
                            target.secondPermTarget = possPermTarget;
                            target.secondaryDist = distHolder;
                        }
                    }
                    // reset the saved distance to use later for net target
                    target.secondaryDist = -1;
                }

                // Check if we have eliminated the target
                if (target.permTarget != null && target.permTarget.distanceSquared(constants.ourLoc) <= constants.visionRadius && !helper.isIn(target.permTarget, map.opponentStructureLocs)) {
                    constants.eleminatedTargets[constants.eleminatedTargetsIndex++] = target.permTarget;
                    target.permTarget = null;
                }
                // TODO: After eliminating target where should I go?

                // Go try to sabatoge
                // IMPORTANT NOTE: Because sabotage takes priority they will try to do this over going to their target
                if (end == null) {
                    uc.println("found enemy!! Going to attack");
                    end = target.getClosestEnemyStructure(uc);
                }

                // Use perm target if we have one
                if (end == null) {
                    uc.println("the perm target is " + target.permTarget);
                    end = target.permTarget;
                }

                if (end == null) {
                    uc.println("the secondary target is " + target.secondPermTarget);
                    end = target.secondPermTarget;
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
                    uc.println("Going to the center");
                    end = new Location(constants.width / 2, constants.height / 2);
                }

                // Do the navigation stuff
                // IMPORATANT TODO: Finish the objects that get in the way to be better
                navigation.navigateTo(uc, null, end, map.bots);

//                uc.println("checking amount left 1.2 " + uc.getPercentageOfEnergyLeft());

                // Try to sabotage anything we are near
                // TODO: We shouldnt check this every turn like this
                // TODO: Dont sabotage astraunauts only main structures
                for (Direction dir : constants.directions) {
                    Location adjLocation = uc.getLocation().add(dir);
                    if (!uc.canSenseLocation(adjLocation)) continue;
                    // TODO: Use grid logic later
                    StructureInfo possibleEnemy = uc.senseStructure(adjLocation);
                    if (possibleEnemy == null) continue;
                    if (possibleEnemy.getTeam().equals(constants.myTeam)) continue;
                    // Location opponentStructureLoc = map.grid_shit[adjLocation.x][adjLocation.y] == constants.oppoennt_permanent_structure ? adjLocation: null;
                    if (uc.canPerformAction(ActionType.SABOTAGE, dir, 0)) {
                        uc.println("Sabotaging");
                        uc.performAction(ActionType.SABOTAGE, dir, 0);
                        break;
                    }
                }

//                // Dont pick up care packages if u have a care package
//                // TODO: Like lets just quit picking up these care packages
//                if (myCarePackage == null) {
//                    //Check if there arsenseStructure(Location loc)e Care Packages at an adjacent tile. If so, retrieve them.
//                    // TODO: We shouldnt check this every turn like this
//                    for (Direction dir : constants.directions) {
//                        Location adjLocation = uc.getLocation().add(dir);
//                        if (!uc.canSenseLocation(adjLocation)) continue;
//                        // TODO: Change this to use grid_shit
//                        CarePackage cp = uc.senseCarePackage(adjLocation);
//                        if (cp != null) {
//                            if (uc.canPerformAction(ActionType.RETRIEVE, dir, 0)) {
//                                uc.performAction(ActionType.RETRIEVE, dir, 0);
//                                break;
//                            }
//                        }
//                    }
//                }
//
////                uc.println("checking amount left 2 " + uc.getPercentageOfEnergyLeft());
//
//                if (myCarePackage != null) {
//                    // If we are Dome build dome in a surrounding square
//                    // TODO: choose to build better than when we run out of oxygen
//                    if (myCarePackage.equals(CarePackage.DOME) && uc.getAstronautInfo().getOxygen() <= 2) {
//                        // Make this better later
//                        int dirIndex = (int) (uc.getRandomDouble() * 8.0);
//                        Direction randomDir = constants.directions[dirIndex];
//                        for (int i = 0; i < 8; ++i) {
//                            //Note that the 'value' of the following command is irrelevant.
//                            if (uc.canPerformAction(ActionType.BUILD_DOME, randomDir, 0)) {
//                                uc.performAction(ActionType.BUILD_DOME, randomDir, 0);
//                                break;
//                            }
//                            randomDir = randomDir.rotateRight();
//                        }
//                    }
//
//                    // If we are settlement build in a sourounding square
//                    // TODO: choose to build better than when we run out of oxygen
//                    if (myCarePackage.equals(CarePackage.SETTLEMENT) && uc.getAstronautInfo().getOxygen() <= 2) {
//                        // Make this better later
//                        int dirIndex = (int) (uc.getRandomDouble() * 8.0);
//                        Direction randomDir = constants.directions[dirIndex];
//                        for (int i = 0; i < 8; ++i) {
//                            //Note that the 'value' of the following command is irrelevant.
//                            if (uc.canPerformAction(ActionType.BUILD_SETTLEMENT, randomDir, 0)) {
//                                uc.performAction(ActionType.BUILD_SETTLEMENT, randomDir, 0);
//                                break;
//                            }
//                            randomDir = randomDir.rotateRight();
//                        }
//                    }
//                }

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