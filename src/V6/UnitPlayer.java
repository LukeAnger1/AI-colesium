package V6;

import aic2024.user.*;

import V6.*;

public class UnitPlayer {

    // BRO literally have to add these fucking lines because statics are banned, dont make more of these!!!!
    public final constants constants = new constants();
    public navigation navigation;
    public final helper helper = new helper();
    public map map;
    public target target;

    public void run(UnitController uc) {
        // Code to be executed only at the beginning of the unit's lifespan

        // First turn code to be ran on the first turn
        uc.println("Bro this is the first turn, chill the fuck out");

        // Set the teams
        constants.myTeam = uc.getTeam();
        constants.opponentTeam = uc.getOpponent();

        // Sets the structure type
        if (uc.isStructure()) {
            constants.type = uc.getType();

            // Sets the vision radius
            constants.visionRadius = constants.type.getVisionRange();

            // Sets the is struct to true
            constants.isStructure = true;
        } else {
            // Sets the astraunaut vision radius
            constants.visionRadius = GameConstants.ASTRONAUT_VISION_RANGE;

            // Sets the is struct to false
            constants.isStructure = false;
        }

        // get the map X and Y
        constants.width = uc.getMapWidth();
        constants.height = uc.getMapHeight();

        // Set up the map
        map = new map(helper, constants, constants.width, constants.height, uc);

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

                for (Direction dir : constants.directions) {
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
                    // Settlement
                    if (uc.canEnlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.SETTLEMENT)) {
                        uc.enlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.SETTLEMENT);
                        break;
                    }
                    // hyper jump
                    if (uc.canEnlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.HYPERJUMP)) {
                        uc.enlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.HYPERJUMP);
                        break;
                    }
                    // Dome
                    if (uc.canEnlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.DOME)) {
                        uc.enlistAstronaut(dir, constants.oxygenWithPackage, CarePackage.DOME);
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
                Location end;

                // See if there are any packages near here
                end = target.getClosestPackage(uc);

                // Pick the center of the map for shits and giggles, I dont really like this
                // NOTE: targeting a location is significantly better
                if (end == null) {
                    end = new Location(constants.width / 2, constants.height / 2);
                }

                // Do the navigation stuff
                // IMPORATANT TODO: Finish the objects that get in the way to be better
                navigation.navigateTo(uc, null, end, map.obstacles);

                uc.println("checking amount left 1.2 " + uc.getPercentageOfEnergyLeft());

                //Check if there are Care Packages at an adjacent tile. If so, retrieve them.
                for (Direction dir : constants.directions) {
                    Location adjLocation = uc.getLocation().add(dir);
                    if (!uc.canSenseLocation(adjLocation)) continue;
                    CarePackage cp = uc.senseCarePackage(adjLocation);
                    if (cp != null) {
                        if (uc.canPerformAction(ActionType.RETRIEVE, dir, 0)) {
                            uc.performAction(ActionType.RETRIEVE, dir, 0);
                            break;
                        }
                    }
                }

                uc.println("checking amount left 2 " + uc.getPercentageOfEnergyLeft());

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