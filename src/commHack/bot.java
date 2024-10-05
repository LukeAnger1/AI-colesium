package commHack;

import aic2024.engine.Unit;
import aic2024.user.*;

import commHack.*;

public class bot extends UnitPlayer {
    public void run (UnitController uc) {

        // TODO:Remove this later
        constants.isStructure = false;

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

        // Sets the astraunaut vision radius
        constants.visionRadius = GameConstants.ASTRONAUT_VISION_RANGE;

        // Sets the is struct to false
        constants.isStructure = false;

        // Gets the care package informartion
        myCarePackage = uc.getAstronautInfo().getCarePackage();

        // Saves the location the astraunaut spawned at (not the structure that spawned it location)
        constants.spawnLoc = uc.getLocation();

        // Set up the target
        target = new target(map, constants);

        // set up the navigation
        navigation = new navigation(constants, map);
        // Add the below into a constructor
        navigation.uc = uc;
        navigation.helper = helper;

        while (true) {
            // This sets our location
            constants.ourLoc = uc.getLocation();

            uc.println("bot getting set up");
            uc.yield();

            // Make sure we are not being constructed
            if (!uc.getAstronautInfo().isBeingConstructed()) {

                // record everything for the turn
                map.record(uc);

                // Get the targets to go after
                Location end = null;

                // There should be instructions posted in the comms, will retreive them every turn
                // IMPORTANT NOTE: Eventually change this to allow bots to communicate with HQ

                // IM{ORATNT TODO: REMOVE THIS AFTER TESTING
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
                        constants.listeningToHQIndex = index;
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

                // Check if we have eliminated the target
                if (target.permTarget != null && target.permTarget.distanceSquared(constants.ourLoc) <= constants.visionRadius && !helper.isIn(target.permTarget, map.opponentStructureLocs)) {
                    // IMPORTANT TODO: Add logic to notify the HQ that there is no building
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

                // TODO: Check if we can make it to the perm target, if not then dont go there

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