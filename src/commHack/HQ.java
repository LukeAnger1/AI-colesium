package commHack;

import aic2024.engine.Unit;
import aic2024.user.*;

import commHack.*;

public class HQ extends UnitPlayer {
    public void run (UnitController uc) {
        uc.println("my ID is " + uc.getID());

        constants.isStructure = true;

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
                        boolean[] holderSymm = comms.intToThreeBooleans(getSymmetries.get(index));
                        if (!holderSymm[0]) {
                            constants.canBeHorizontal = false;
                        }

                        if (!holderSymm[1]) {
                            constants.canBeVerticl = false;
                        }
                        if (!holderSymm[2]) {
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
                // DONT CHECK EVERY HQ for getAllHack because the first one is the only one that
                // This is the basic comunicatino receiving from bots
                if (uc.getRound() > 5) {

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
                uc.println("trying to spawn with spawnTarget direection " + dir);
                // TODO: consider not using max amount of oxygen
                while (dir != null) {
                    uc.enlistAstronaut(dir, oxygen, null);
                    uc.println("here 1 trying to spawrn");
                    dir = spawnTargets.getSpawnLoc(uc.getLocation(), oxygen);
                }

            }

            uc.println("ending my fucking turn");

            uc.yield(); // End of turn
        }
    }
}