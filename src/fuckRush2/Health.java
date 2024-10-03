package fuckRush2;

// This class gets the optimal health to spawn astraunauts at

import aic2024.engine.Unit;
import aic2024.user.*;

import fuckRush2.*;

public class Health {
    // Astraunats lose 1 oxygen every turn not under construction
    public final double HealthPerDistance = 1.5;
    public final int startingHealth = 20;

    public map map;

    public Health (map map) {
        this.map = map;
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