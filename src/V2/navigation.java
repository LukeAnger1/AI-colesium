package V2;

import aic2024.engine.Unit;
import aic2024.user.*;
import V2.constants.*;

public class navigation {

    public final constants constants = new constants();

    // This is a level of abstraction to easily switch between different navigation protocals
    public void navigateTo(UnitController  uc, Location start, Location end, Location[] objects) {
        // objects is a list of all locations the bot cant move into
        // NOTE: This will be different based off of a few things
        // 1. Other bots can be in the way temporarily
        // 2. some objects can be jumped with the new game rules
        // 3. not all map knowledge is known, will have to work on this

        // I included start in the parameters because will prob need elsewhere, similarly for objects, this is logic to get start location if not null
        if (start == null) {
            start = uc.getLocation();
        }

        // Check if the mvoement cooldown is done
        // NOTE: movement cooldown increase by 1.4 ish when moving diagonal so it may be better to do taxi distance
        if (!isMovementCooldownReady(uc)) {
            return;
        }

        // If there is a goal location go in that direction
        if (end != null) {
            // Get the best direction to move in to get to goal
            Direction dir = basicDumbAssGoingInLine(uc, start, end, objects);

            if (dir != null){
                uc.performAction(ActionType.MOVE, dir, 0);
                return;
            }
        }

        // Move in a random direction
        Direction dir = randromDirection(uc, start, objects);
        if (dir != null) {
            uc.performAction(ActionType.MOVE, dir, 0);
            return;
        }

    }

    ////////// NOTE: Make sure these functions also make sure the uc can move in that direction before returning //////////
    public Direction basicDumbAssGoingInLine(UnitController uc, Location start, Location end, Location[] objects) {
        Direction dir = start.directionTo(end);

        // Check if we can move in the desired direction before returning
        if (uc.canPerformAction(ActionType.MOVE, dir, 0)) {
            return start.directionTo(end);
        }

        return null;
    }

    public Direction randromDirection(UnitController uc, Location start, Location[] objects) {
        //move randomly, turning right if we can't move.
        int dirIndex = (int)(uc.getRandomDouble()*8.0);
        Direction randomDir = constants.directions[dirIndex];
        for (int i = 0; i < 8; ++i){
            //Note that the 'value' of the following command is irrelevant.
            if (uc.canPerformAction(ActionType.MOVE, randomDir, 0)){
                return randomDir;
            }
            randomDir = randomDir.rotateRight();
        }
        return null;
    }
    ////////// NOTE: Make sure these functions also make sure the uc can move in that direction before returning //////////

    public Boolean isMovementCooldownReady(UnitController uc) {
        // TODO: Check the logic below to make sure it is legal
        return uc.getAstronautInfo().getCurrentMovementCooldown() < 1;
    }
}