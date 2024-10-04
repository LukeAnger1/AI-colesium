package commHack;

// This class gets the optimal health to spawn astraunauts at

import aic2024.engine.Unit;
import aic2024.user.*;

import commHack.*;

public class Health {
    // Astraunats lose 1 oxygen every turn not under construction
    public final double HealthPerDistance = 2;
    public final int startingHealth;

    public map map;
    public constants constants;

    public Health (map map, constants constants) {
        this.map = map;
        this.constants = constants;
        startingHealth = (int)((double)constants.width * constants.height / 30 / 30 * 25);
    }

    public int getHealthSuggested(Location start, Location end) {
        // If there is no end goal do the default
        if (end == null) {
            return getHealthSuggested();
        }

        int holder = (int)(map.getTaxiDistenace(start, end) * HealthPerDistance);
        // Change the suggested amount to match the minimum health
        holder = (holder < GameConstants.MIN_OXYGEN_ASTRONAUT) ? -(int)(-GameConstants.MIN_OXYGEN_ASTRONAUT ): holder;
        return holder;
    }

    public int getHealthSuggested() {
        return startingHealth;
    }
}