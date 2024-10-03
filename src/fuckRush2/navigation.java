package fuckRush2;

import aic2024.engine.Unit;
import aic2024.user.*;
import fuckRush2.*;

import java.nio.file.DirectoryIteratorException;
import java.util.Arrays;

public class navigation {

    public constants constants;
    public Buffer CircularBuffer;
    public map map;
    public helper helper;

    public UnitController uc;

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

        dir = bug(start, end);
        if (dir != null) {
            uc.performAction(ActionType.MOVE, dir, 0);
        }

        // TODO: Add logic that gives good gues at direction if too many astruanauts for bug

//        dir = broWTF(uc, start, tempEnd);
//        uc.println("bro wtf returned " + dir);
//        if (dir != null) {
//            uc.println("doing bro wtf");
//            uc.performAction(ActionType.MOVE, dir, 0);
//        }

        // If there is a goal location go in that direction
//        dir = greedyBFS(uc, start, end);
//        if (dir != null){
//            uc.performAction(ActionType.MOVE, dir, 0);
//            return;
//        }

        // If there is a goal location go in that direction
        // NOTE: Make sure this is never ran as greedyBFS should always run and domintate
        // IMPORTANT TODO: End code once moved
        dir = basicDumbAssGoingInLine(uc, start, end);
        uc.println("going to try to move in " + dir + " using basic dumb ass");
        if (dir != null){
            uc.performAction(ActionType.MOVE, dir, 0);
            return;
        }


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

    // This function is optimal bfs to find optimal moves for optimization
    // IMPORTANT NOTE: This wont work because will repeatedly explore the same combinations
    public Direction broWTF (UnitController uc, Location start, Location end) {
        // This is the base case
//        if (start.equals(end)) {
//            return null;
//        }

        uc.println("the start location is " + start + " the end location is " + end);

        // Make sure to skip any null Locations
        Location[] possibleLocFromEnd = new Location[9];

        for (int index = 0; index < 9; index ++) {
            possibleLocFromEnd[index] = end.add(constants.directions[index]);
        }

        // Sort this array with the lower distance in the 0 index
        // To find the distance run start.distanceSquaredTo(possibleLocFromEnd)
        // Filter out null locations and sort by distance squared to the start
        Arrays.sort(possibleLocFromEnd, (loc1, loc2) -> {
            if (loc1 == null && loc2 == null) return 0;
            if (loc1 == null) return 1; // nulls go to the end
            if (loc2 == null) return -1; // nulls go to the end
            return Integer.compare(start.distanceSquared(loc1), start.distanceSquared(loc2));
        });

        for (Location loc: possibleLocFromEnd) {

            // This is the case it hits the nulls and should break
            if (loc == null) {
                break;
            }

            // This checks if the next dir will return the start state and return if so, aka the base case
            if (loc.equals(start)) {
                return start.directionTo(loc);
            }

            // Check if we can travel to the new spot
            if (map.canTravel(loc)) {
                // If we can then we are going to recursively call this
                Direction dir = broWTF(uc, start, loc);
                if (dir.equals(Direction.ZERO)) {
                    uc.println("The dir is ZE bro");
                }

                // We are going to check if it eventually hit the base case
                // NOTE: This should be a redudant check to see if can move but we will see
                uc.println("going to try to move in " + dir);
                if (dir != null && uc.canPerformAction(ActionType.MOVE, dir, 0)) {
                    uc.println("returning the dir");
                    return dir;
                }
            }
        }

        return null;
    }

    // This is basic bug navigation to get from point A to point B
    public Direction rightHandRuleDir;
    public double m;
    public double b;
    public int distToGoalOnceHitObject;
    public boolean onObj = false;
    public final int howLongStayOnObj = 4;
    public int howLongStayOnObjCount = 0;

    public boolean onLine(double slope, double intercept, Location loc) {
        // Check if close enough to the line and return true if so
        return loc.distanceSquared(new Location(loc.x, (int)(slope*loc.x + intercept))) <= 2;
    }

    public double getSlope (Location one, Location two) {
        // return big ass num if vertical so I dont have to deal with this shit
        if (one.x == two.x) return 100000;

        return (double)(two.y - one.y) / (two.x - one.x);
    }

    public double getIntercept (Location one, Location two, double slope) {
        // since y = mx + b
        return one.y - slope * one.x;
    }

    public Direction bug (Location start, Location end) {

        // Set the start to our loc if it is null
        if (start.equals(null)) {
            start = uc.getLocation();
        }

        // If there are astraunauts around us then dont use bug nav
        for (Direction dir: constants.directions) {
            Location holder = start.add(dir);
            if (helper.isIn(holder, map.oponnentAstronautLocs) || helper.isIn(holder, map.myAstronautLocs)) {
                uc.println("not using bug nav and saying I am not on obj");
                onObj = false;
                return null;
            }
        }

        uc.println("calling bug nav with start " + start + " end " + end + " and I am on an obj " + onObj);

        // This handles the logic to see if we should exit bug nav mode, we should be on the line and closer to the destination
        if (onObj && howLongStayOnObjCount < howLongStayOnObj && onLine(m, b, start) && end.distanceSquared(start) < distToGoalOnceHitObject) {
            uc.println("in 1 bug");
            howLongStayOnObjCount++;
            onObj = false;
            return bug(start, end);
        }

        // If not on an object try to move forard, if can move forward then do
        Direction dirToGoal = start.directionTo(end);
        if (!onObj && uc.canPerformAction(ActionType.MOVE, dirToGoal, 0)) {
            uc.println("in 2 bug");
            return dirToGoal;
        }

        // I got to be on obj then, so if not currently recording set the objectives
        if (!onObj) {
            uc.println("in 3 bug");
            howLongStayOnObjCount = 0;
            onObj = true;
            distToGoalOnceHitObject = start.distanceSquared(end);
            m = getSlope(start, end);
            b = getIntercept(start, end, m);
            // I dont want to move diagonal so we are going to only use NORTH WEST SOUTH EAST
            rightHandRuleDir = start.directionTo(end);
            // rotate until I get N E S W
            uc.println("the right hand dir is " + rightHandRuleDir);
            if (rightHandRuleDir != Direction.NORTH || rightHandRuleDir != Direction.EAST || rightHandRuleDir != Direction.SOUTH || rightHandRuleDir != Direction.WEST) {
                uc.println("in the loop with dir " + rightHandRuleDir);
                rightHandRuleDir = rightHandRuleDir.rotateLeft();
            }
            // rotate 90 for the direciton we should go in
            rightHandRuleDir = rightHandRuleDir.rotateLeft().rotateLeft();
            return bug(start, end);
        }

        // Try to go right 90 then forward then left 90, I only want to use NORTH EAST SOUTH WEST
        rightHandRuleDir = rightHandRuleDir.rotateRight().rotateRight();
        if (uc.canPerformAction(ActionType.MOVE, rightHandRuleDir, 0)) {
            uc.println("in 4 bug");
            return rightHandRuleDir;
        }

        for (int index = 0; index < 7; index ++) {
            rightHandRuleDir = rightHandRuleDir.rotateLeft();
            if (uc.canPerformAction(ActionType.MOVE, rightHandRuleDir, 0)) {
                uc.println("in 5 bug");
                return rightHandRuleDir;
            }
        }

        uc.println("in 6 bug");
        // bro we cant move
        return null;
    }

    // This functino is on some frickin steroids
    public Direction sixthWestPathFinding (Location start, Location end) {
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

        // Go ahead and set enemy HQ locs
        constants.enemyHQs = new Location[constants.ourHQs.length];

        // If there is only one symmetry we can find the enemy HQ locations
        if (holder) {

            // Going to cycle through
            // NOTE: There is a better way to do this but I only know it in python
            if (constants.canBeHorizontal) {
                for (int index = 0; index < constants.enemyHQs.length; index ++) {
                    constants.enemyHQs[index] = map.horizontalSymmerty(constants.ourHQs[index]);
                }
            }

            if (constants.canBeVerticl) {
                for (int index = 0; index < constants.enemyHQs.length; index ++) {
                    constants.enemyHQs[index] = map.verticalSymmerty(constants.ourHQs[index]);
                }
            }

            if (constants.canBeRotational) {
                for (int index = 0; index < constants.enemyHQs.length; index ++) {
                    constants.enemyHQs[index] = map.rotationalSymmerty(constants.ourHQs[index]);
                }
            }

            // Sort the array so that every HQ has the same order to prevent conflicts with sending troups
            sortLocationArray(constants.enemyHQs);

            // This will sort the enemies to be optimally placed
            // IMPORTANT TODO: Have to implement this later
//            sortEnemyHQsToBeOptimalWithOurHQ();
        } else {

            // We are going to make assumptions about the symmetry until we can figure it out better later, so fuck off bitch
            if (constants.canBeHorizontal) {
                for (int index = 0; index < constants.enemyHQs.length; index++) {
                    constants.enemyHQs[index] = map.horizontalSymmerty(constants.ourHQs[index]);
                }
            } else {
                if (constants.canBeVerticl) {
                    for (int index = 0; index < constants.enemyHQs.length; index++) {
                        constants.enemyHQs[index] = map.verticalSymmerty(constants.ourHQs[index]);
                    }
                } else {
                    for (int index = 0; index < constants.enemyHQs.length; index++) {
                        constants.enemyHQs[index] = map.rotationalSymmerty(constants.ourHQs[index]);
                    }
                }
            }


            // Sort the array so that every HQ has the same order to prevent conflicts with sending troups
            sortLocationArray(constants.enemyHQs);
        }

        return false;
    }

    public void sortLocationArray (Location [] arr) {
        // This is soo fucking inefficient but I dont give a damn anymore

        // This loop will go through each index
        for (int indexToSort = 0; indexToSort < arr.length; indexToSort ++) {

            // This is the best location
            int bestIndex = indexToSort;
            Location best = arr[bestIndex];

            // This loop finds the smallest element
            for (int indexCompare = indexToSort + 1; indexCompare < arr.length; indexCompare ++) {

                // Switch to new best
                if (arr[indexCompare].x < best.x || (arr[indexCompare].x == best.x && arr[indexCompare].y < best.y)) {
                    bestIndex = indexCompare;
                    best = arr[bestIndex];
                }
            }

            // This will do a simple swap
            Location holder = best;
            arr[bestIndex] = arr[indexToSort];

            arr[indexToSort] = holder;

        }

        // This saves the index of our nhq location so we can use this in the corresponding enemy loc to help target which enemy HQ to go after
        for (int index = 0; index < constants.ourHQs.length; index ++) {
            // Save the index if they match
            if (constants.ourHQs[index].equals(constants.ourLoc)) {
                constants.ourHQIndex = index;
                break;
            }
        }
    }

    // NOTE: This is completely inefficient
    // IMPORTANT TODO: Fix this function
    public void sortEnemyHQsToBeOptimalWithOurHQ() {
        // This function takes the 2 sorted arrays of enemyHQs and ourHQs and sorts the enemyHQs to correspond to which enemy each our HQ should target

        // We are going to find the longest distance between HQs
        int longestDistance = -1;
        int indexOfLongestEnemyHQ = -1;
//        int indexOfLongestOurHQ  =-1;
        for (int index = 0; index < constants.enemyHQs.length; index ++) {
            int holder = constants.enemyHQs[index].distanceSquared(constants.ourHQs[index]);
            if (holder > longestDistance) {
                longestDistance = holder;
                indexOfLongestEnemyHQ = index;
//                indexOfLongestOurHQ = index;
            }
        }

        // Cycle through and see if there is a better enemy HQ to target
        for (int secondIndex = 0; secondIndex < constants.enemyHQs.length; secondIndex ++) {
            // Skip comparing to self
            if (indexOfLongestEnemyHQ == secondIndex) {
                continue;
            }

            // If there is one then switch it and rerun
            if (constants.enemyHQs[secondIndex].distanceSquared(constants.ourHQs[indexOfLongestEnemyHQ]) < longestDistance) {
                // switch logic
                Location holder = constants.enemyHQs[secondIndex];
                constants.enemyHQs[secondIndex] = constants.enemyHQs[indexOfLongestEnemyHQ];
                constants.enemyHQs[indexOfLongestEnemyHQ] = holder;

                // rerun
                sortEnemyHQsToBeOptimalWithOurHQ();
                break;
            }
        }

        // This is good enough to return
    }
}