package fuckingRush;

import aic2024.engine.Unit;
import aic2024.user.*;
import fuckingRush.*;

import java.nio.file.DirectoryIteratorException;

public class navigation {

    public constants constants;
    public Buffer CircularBuffer;
    public map map;

    public navigation (constants constants, map map) {
        this.constants = constants;
        this.CircularBuffer = new Buffer(this.constants.circularBufferSize);
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

        // If there is a goal location go in that direction
        dir = greedyBFS(uc, start, end);
        if (dir != null){
            uc.performAction(ActionType.MOVE, dir, 0);
            return;
        }

        // If there is a goal location go in that direction
        // NOTE: Make sure this is never ran as greedyBFS should always run and domintate
//        dir = basicDumbAssGoingInLine(uc, start, end, objects);
//        if (dir != null){
//            uc.performAction(ActionType.MOVE, dir, 0);
//            return;
//        }


        // Move to explore
        dir = explorationDirection(uc);
        if (dir != null) {
            uc.println("I am explroing");
            uc.performAction(ActionType.MOVE, dir, 0);
            return;
        }

        // Move in a random direction
        dir = randromDirection(uc);
        if (dir != null) {
            uc.println("I am moving in a random direction");
            uc.performAction(ActionType.MOVE, dir, 0);
            return;
        }

    }

    ////////// NOTE: Make sure these functions also make sure the uc can move in that direction before returning //////////
    public Direction basicDumbAssGoingInLine(UnitController uc, Location start, Location end) {

        // check if the end location is even possible
        if (end == null) {
            return null;
        }

        Direction dir = start.directionTo(end);

        // Check if we can move in the desired direction before returning
        if (uc.canPerformAction(ActionType.MOVE, dir, 0)) {
            return start.directionTo(end);
        }

        return null;
    }

    public Direction randromDirection(UnitController uc) {
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

    public Direction explorationDirection(UnitController uc) {

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
    public Direction greedyBFS(UnitController uc, Location start, Location end) {
        // NOTE: Can optimize by only doing the first obstacle and only if there is an obstacl in the way
        // IMPORTANT TODO: Change the code to instead choose the square with the least steps to get to end!!!!, Also only needs to get around the first object!!! OPTIMZE OPTIMIZE
        if (end == null) {
            return null;
        }

        uc.drawLineDebug(start, end, 0, 0, 255);

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

    // Use this function as the start to see if we know enemy hq locations and then save them for later usage
    public boolean doWeKnowEnemyHQAndSet() {
        boolean holder = (constants.canBeVerticl && !constants.canBeRotational && !constants.canBeHorizontal) || (!constants.canBeVerticl && constants.canBeRotational && !constants.canBeHorizontal) || (!constants.canBeVerticl && !constants.canBeRotational && constants.canBeHorizontal);

        // If there is only one symmetry we can find the enemy HQ locations
        if (holder) {
            // Go ahead and set enemy HQ locs
            constants.enemyHQs = new Location[constants.ourHQs.length];

            // Going to cycle through
            // NOTE: There is a better way to do this but I only know it in python
            if (constants.canBeHorizontal) {
                for (int index = 0; index < constants.enemyHQs.length; index ++) {
                    constants.enemyHQs[index] = map.horizontalSymmerty(constants.ourHQs[index]);
                }
                return true;
            }

            if (constants.canBeVerticl) {
                for (int index = 0; index < constants.enemyHQs.length; index ++) {
                    constants.enemyHQs[index] = map.verticalSymmerty(constants.ourHQs[index]);
                }
                return true;
            }

            if (constants.canBeRotational) {
                for (int index = 0; index < constants.enemyHQs.length; index ++) {
                    constants.enemyHQs[index] = map.rotationalSymmerty(constants.ourHQs[index]);
                }
                return true;
            }
        }

        return false;
    }
}