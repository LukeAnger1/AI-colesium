package fuckRush2;

// This class gets the optimal health to spawn astraunauts at

import aic2024.engine.Unit;
import aic2024.user.*;

import fuckRush2.*;

public class spawnTargets {

    public UnitController uc;
    public Health Health;
    public constants constants;

    public spawnTargets (UnitController uc, Health Health, constants constants) {
        this.uc = uc;
        this.Health = Health;
        this.constants = constants;
    }

    public Direction getSpawnLoc(Location start, int health) {
        return getSpawnLoc(start, null, health);
    }

    public Direction getSpawnLoc(Location start, Location end, int health) {
        // handle the cases that null is put in
        if (start == null) {
            start = uc.getLocation();
        }

        if (end == null) {
            end = new Location(constants.width / 2, constants.height / 2);
        }

        return getSpawnLoc(start, end, start.directionTo(end), health);
    }

    public Direction getSpawnLoc(Location start, Location end, Direction favorite, int health) {

        // TODO: Add in care packages at some point if u want
        if (uc.canEnlistAstronaut(favorite, Health.getHealthSuggested(start, end), null)) {
            return favorite;
        }

        Direction right = favorite;
        Direction left = favorite;

        // We are going to rotate around until we find one we can do
        for (int i = 0; i < 3; i ++) {
            right = right.rotateRight();

            uc.println("trying to spawn on the right " + right);

            if (uc.canEnlistAstronaut(right, health, null)) {
                return right;
            }

            left = left.rotateLeft();

            uc.println("trying to spawn on the left " + left);

            if (uc.canEnlistAstronaut(left, health, null)) {
                return left;
            }
        }

        if (uc.canEnlistAstronaut(favorite.opposite(), health, null)) {
            return favorite.opposite();
        }

        return null;
    }
}