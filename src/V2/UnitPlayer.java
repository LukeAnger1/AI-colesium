package V2;

import aic2024.user.*;

import V2.navigation.*;
import V2.constants.*;

public class UnitPlayer {

    // BRO literally have to add these fucking lines because statics are banned
    public final constants constants = new constants();
    public final navigation navigation = new navigation();

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




        while (true) {

            //Case in which we are a HQ
            // NOTE: Dont run isBeingConstructed
            if (constants.isStructure && uc.getType() == StructureType.HQ) {
                uc.println("I am HQ going to do HQ stuff");
                //Spawn exactly one astronaut with 30 oxygen, if possible
                for (Direction dir : constants.directions) {
                    if (uc.canEnlistAstronaut(dir, 30, null)) {
                        uc.enlistAstronaut(dir, 30, null);
                        break;
                    }
                }
            }

            // Code to be executed every round, if we are astraunaut and not being made
            if (!constants.isStructure && !uc.getAstronautInfo().isBeingConstructed()) {

                uc.println("here 11");

                // Scan the surrounding objects
                // Get our astraunauts
                AstronautInfo[] myAstronauts = uc.senseAstronauts((float) constants.visionRadius, constants.myTeam);

                // Get the opponent astraunauts
                AstronautInfo[] opponentAstronauts = uc.senseAstronauts((float) constants.visionRadius, constants.opponentTeam);

                // Get the objects
                // DOME
                Location[] domes = uc.senseObjects(MapObject.DOME, (float) constants.visionRadius);

                // DOME TILES
                Location[] domeTiles = uc.senseObjects(MapObject.DOMED_TILE, (float) constants.visionRadius);

                // HOT ZONES
                Location[] hotZones = uc.senseObjects(MapObject.HOT_ZONE, (float) constants.visionRadius);

                // HYPER JUMP
                Location[] hyperJump = uc.senseObjects(MapObject.HYPERJUMP, (float) constants.visionRadius);

                // LAND
                Location[] lands = uc.senseObjects(MapObject.LAND, (float) constants.visionRadius);

                // TERRAFORMED
                Location[] terraforms = uc.senseObjects(MapObject.TERRAFORMED, (float) constants.visionRadius);

                // WATER
                // NOTE: May not have to run all of these checks
                Location[] water = uc.senseObjects(MapObject.WATER, (float) constants.visionRadius);

                // Get my structures
                StructureInfo[] myStructures = uc.senseStructures((float) constants.visionRadius, constants.myTeam);

                // Get opponent structures
                StructureInfo[] opponentStructures = uc.senseStructures((float) constants.visionRadius, constants.opponentTeam);

                // Do the navigation stuff
                navigation.navigateTo(uc, null, null, null);

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
            }

            uc.println("here 3");

            uc.yield(); // End of turn
        }
    }
}