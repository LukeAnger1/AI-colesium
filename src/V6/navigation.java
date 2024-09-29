package V6;

import aic2024.engine.Unit;
import aic2024.user.*;
import V6.*;

import java.nio.file.DirectoryIteratorException;

public class navigation {

    public constants constants;
    public CircularBuffer CircularBuffer;
    public map map;

    public navigation (constants constants, map map) {
        this.constants = constants;
        this.CircularBuffer = new CircularBuffer(this.constants.circularBufferSize);
        this.map = map;
    }

    // This is the exploratino direction to explore in until we hit a wall or something
    public Direction explorationDir;

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

        Direction dir;

        // If there is a goal location go in that directio
        dir = greedyBFS(uc, start, end, objects);
        if (dir != null){
            uc.performAction(ActionType.MOVE, dir, 0);
            return;
        }

        // If there is a goal location go in that directio
        dir = basicDumbAssGoingInLine(uc, start, end, objects);
        if (dir != null){
            uc.performAction(ActionType.MOVE, dir, 0);
            return;
        }


        // Move to explore
        dir = explorationDirection(uc, start, null);
        if (dir != null) {
            uc.performAction(ActionType.MOVE, dir, 0);
            return;
        }

        // Move in a random direction
        dir = randromDirection(uc, start, objects);
        if (dir != null) {
            uc.performAction(ActionType.MOVE, dir, 0);
            return;
        }

    }

    ////////// NOTE: Make sure these functions also make sure the uc can move in that direction before returning //////////
    // TODO: Should change the code to use the objects array before checking action type
    public Direction basicDumbAssGoingInLine(UnitController uc, Location start, Location end, Location[] objects) {

        // check if the end location is even possible
        if (end == null) {
            return null;
        }

        Direction dir = start.directionTo(end);

        // Check if we can move in the desired direction before returning
        // TODO: Remove the objects in the parameter
        if (uc.canPerformAction(ActionType.MOVE, dir, 0)) {
            return start.directionTo(end);
        }

        return null;
    }

    public Direction randromDirection(UnitController uc, Location start, Location[] objects) {
        //move randomly, turning right if we can't move.
        Direction randomDir = getRandomDirection(uc);
        for (int i = 0; i < 8; ++i){
            //Note that the 'value' of the following command is irrelevant.
            if (uc.canPerformAction(ActionType.MOVE, randomDir, 0)){
                return randomDir;
            }
            randomDir = randomDir.rotateRight();
        }
        return null;
    }

    public Direction explorationDirection(UnitController uc, Location start, Location[] objects) {

        // if the exploration direction is null then set it
        if (explorationDir == null) {
            explorationDir = getRandomDirection(uc);
        }

        if (uc.canPerformAction(ActionType.MOVE, explorationDir, 0)) {
            return explorationDir;
        }

        int possibleDirCount = 0;
        Direction[] possibleDirs = new Direction[8];

        for (int count = 0; count < 8; count ++) {
            // Check if we can move in the exploration direction then record it
            if (uc.canPerformAction(ActionType.MOVE, constants.directions[count], 0)) {
                possibleDirs[possibleDirCount] = constants.directions[count];
                possibleDirCount++;
            }
        }

        if (possibleDirCount == 0) {
            return null;
        }

        explorationDir = constants.directions[getRandomInt(uc, possibleDirCount)];
        return explorationDir;
    }

    // IMPORTANT NOTE: Because we are always reseting the line there is a scenario where we are looped back onto itself
    public Direction greedyBFS(UnitController uc, Location start, Location end, Location[] objects) {
        // NOTE: Can optimize by only doing the first obstacle and only if there is an obstacl in the way
        // IMPORTANT TODO: Change the code to instead choose the square with the least steps to get to end
        if (end == null) {
            return null;
        }

        // initialize the right hand rule
        Direction rightHandRule = start.directionTo(end);

//        uc.drawLineDebug(start, end, 0, 0, 255);
        makeLine(start, end);

        // We now need to find every possible good travel location
        // First try to go in the straight line then repeatedly rotate left until can move forward
        Location holderLoc = start.add(rightHandRule);
        if (map.grid_shit[holderLoc.x][holderLoc.y] == constants.travel && uc.canPerformAction(ActionType.MOVE, rightHandRule, 0)) {
            return rightHandRule;
        }
        rightHandRule.rotateLeft();

        holderLoc = start.add(rightHandRule);
        if (map.grid_shit[holderLoc.x][holderLoc.y] == constants.travel && uc.canPerformAction(ActionType.MOVE, rightHandRule, 0)) {
            return rightHandRule;
        }
        rightHandRule.rotateLeft();

        holderLoc = start.add(rightHandRule);
        if (map.grid_shit[holderLoc.x][holderLoc.y] == constants.travel && uc.canPerformAction(ActionType.MOVE, rightHandRule, 0)) {
            return rightHandRule;
        }
        rightHandRule.rotateLeft();

        holderLoc = start.add(rightHandRule);
        if (map.grid_shit[holderLoc.x][holderLoc.y] == constants.travel && uc.canPerformAction(ActionType.MOVE, rightHandRule, 0)) {
            return rightHandRule;
        }
        rightHandRule.rotateLeft();

        holderLoc = start.add(rightHandRule);
        if (map.grid_shit[holderLoc.x][holderLoc.y] == constants.travel && uc.canPerformAction(ActionType.MOVE, rightHandRule, 0)) {
            return rightHandRule;
        }
        rightHandRule.rotateLeft();

        holderLoc = start.add(rightHandRule);
        if (map.grid_shit[holderLoc.x][holderLoc.y] == constants.travel && uc.canPerformAction(ActionType.MOVE, rightHandRule, 0)) {
            return rightHandRule;
        }
        rightHandRule.rotateLeft();

        holderLoc = start.add(rightHandRule);
        if (map.grid_shit[holderLoc.x][holderLoc.y] == constants.travel && uc.canPerformAction(ActionType.MOVE, rightHandRule, 0)) {
            return rightHandRule;
        }
        rightHandRule.rotateLeft();

        holderLoc = start.add(rightHandRule);
        if (map.grid_shit[holderLoc.x][holderLoc.y] == constants.travel && uc.canPerformAction(ActionType.MOVE, rightHandRule, 0)) {
            return rightHandRule;
        }

        return null;
    }
    ////////// NOTE: Make sure these functions also make sure the uc can move in that direction before returning //////////

    public Boolean isMovementCooldownReady(UnitController uc) {
        // TODO: Check the logic below to make sure it is legal
        return uc.getAstronautInfo().getCurrentMovementCooldown() < 1;
    }

    public Direction getRandomDirection(UnitController uc) {
        return constants.directions[getRandomInt(uc, 8)];
    }

    public Direction getRandomLegalDirection(UnitController uc) {
        return null;
    }

    public int getRandomInt(UnitController uc, int amount) {
        return (int)(uc.getRandomDouble()*(double)amount);
    }

    // This function will make a line using the
    public void makeLine(Location start, Location end) {

        // This is the slope
        double slope = (double)(end.y - start.y) / (end.x - start.x);

        // This is to make sure we can always add one to go from start to end
        if (end.x < start.x) {
            Location holder = end;
            end = start;
            start = holder;
        }

        // Makes the start travel
        map.addTravel(start.x, start.y);

        // This is the exact y value, not to be confused when we cast it into an integer
        double y = start.y;

        // This loop will go through every x value between and not including the start and the end
        for (int x = start.x + 1; x < end.x; x ++) {
            // Get the change in y
            double new_y = y + slope;

            // Make sure we are always adding in the y direction
            int y_start;
            int y_end;
            if (y < new_y) {
                y_start = (int)y;
                y_end = (int)new_y;
            } else {
                y_start = (int)new_y;
                y_end = (int)y;
            }

            // fill in the y in this range
            for (int y_holder = y_start; y_holder <= y_end; y_holder++) {
                map.addTravel(x, y_holder);
            }

            // make the old y into the new y
            y = new_y;
        }

        // makes the end travel
        map.addTravel(end.x, end.y);

    }
}