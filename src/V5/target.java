package V5;

import aic2024.user.*;

import V5.*;

// This class contains all the functions to return target locations to go to
public class target {
    public constants constants;
    public map map;

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
}