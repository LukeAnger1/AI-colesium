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
    public CarePackage myCarePackage;

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

            // Gets the care package informartion
            myCarePackage = uc.getAstronautInfo().getCarePackage();
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

                // only target care packages if u dont have one otherwiase there is a waste as picking one up kills u
                if (myCarePackage == null) {
                    // See if there are any packages near here
                    end = target.getClosestBestPackage(uc);
                }

                // Pick the center of the map for shits and giggles, I dont really like this
                // NOTE: targeting a location is significantly better
                if (end == null) {
                    end = new Location(constants.width / 2, constants.height / 2);
                }

                // Do the navigation stuff
                // IMPORATANT TODO: Finish the objects that get in the way to be better
                navigation.navigateTo(uc, null, end, map.obstacles);

                uc.println("checking amount left 1.2 " + uc.getPercentageOfEnergyLeft());

                // Dont pick up care packages if u have a care package
                if (myCarePackage == null) {
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