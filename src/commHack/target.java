package commHack;

import aic2024.user.*;

import commHack.*;

// This class contains all the functions to return target locations to go to
public class target {
    public constants constants;
    public map map;
    public Location permTarget;

    // Set up target with the needed packages
    public target(map map, constants constants) {
        this.constants = constants;
        this.map = map;
    }

    public Location getClosestPackage(UnitController uc) {
        // Go through every care package we scanned and return the closest one
        int dist = constants.maxDist;
        Location best = null;
        for (int index = 0; index < map.CarePackageLocs.length; index ++) {
            Location tempBest = map.CarePackageLocs[index];
            int tempDist = uc.getLocation().distanceSquared(tempBest);
            if (tempDist < dist) {
                dist = tempDist;
                best = tempBest;
            }
        }

        return best;
    }

    public Location getClosestBestPackage(UnitController uc) {
        // This function will get the best packages over lesser packages

        // Go through every care package we scanned and return the closest one
        int dist = constants.maxDist;
        Location best = null;
        int bestValue = -1; // If a value is -1 then do nothing
        for (int index = 0; index < map.CarePackageLocs.length; index ++) {
            // Calculate the best package
            // TODO: Can choose based off of package distance too
            int tempValue = constants.getCarePackageValue(map.CarePackageInfos[index].getCarePackageType());
            Location tempBest = map.CarePackageLocs[index];
            int tempDist = uc.getLocation().distanceSquared(tempBest);
            if (tempValue > bestValue) {
                dist = tempDist;
                best = tempBest;
                bestValue = tempValue;
            } else {
                if (tempDist < dist && tempValue > 0) {
                    dist = tempDist;
                    best = tempBest;
                    bestValue = tempValue;
                }
            }
        }

        return best;

    }

    public Location getClosestPackage(UnitController uc, CarePackage CarePackage) {
        // Go through every care package we scanned and return the closest one
        int dist = constants.maxDist;
        Location best = null;
        for (int index = 0; index < map.CarePackageLocs.length; index ++) {
            // Make sure that the package is the target package
            if (map.CarePackageInfos[index].equals(CarePackage)) {
                Location tempBest = map.CarePackageLocs[index];
                int tempDist = uc.getLocation().distanceSquared(tempBest);
                if (tempDist < dist) {
                    dist = tempDist;
                    best = tempBest;
                }
            }
        }

        return best;
    }

    // TODO: Should prob favor HQ over regular structure
    public Location getClosestEnemyStructure(UnitController uc) {
        int dist = constants.maxDist;
        Location best = null;
        for (int index = 0; index < map.opponentStructures.length; index ++) {
                Location tempBest = map.opponentStructureLocs[index];
                int tempDist = uc.getLocation().distanceSquared(tempBest);
                if (tempDist < dist) {
                    dist = tempDist;
                    best = tempBest;
                }
        }

        return best;
    }
}