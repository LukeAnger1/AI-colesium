package fuckingRush;

import aic2024.engine.Unit;
import aic2024.user.*;
import fuckingRush.*;

import java.nio.file.DirectoryIteratorException;

public class comms {

    public CircularBuffer circularBuffer;
    public UnitController uc;
    public int roundNum = -1;

    // The Circular buffer is how much information can be stored during the turn
    public comms (UnitController uc, CircularBuffer circularBuffer) {
        this.uc = uc;
        this.circularBuffer = circularBuffer;
    }

    // Cycle through all the broadcasts and save them
    public CircularBuffer getAllComms() {

        uc.println("comms 1 with " + uc.getPercentageOfEnergyLeft());

        // Reset the circular buffer if it is a new turn
        if (roundNum != uc.getRound()) {
            circularBuffer.clear();
            roundNum = uc.getRound();
        }

        uc.println("comms 2 with " + uc.getPercentageOfEnergyLeft());

        BroadcastInfo broadcastInfo = uc.pollBroadcast();

        // Go through every message that isnt null
        while (broadcastInfo != null) {
            circularBuffer.add(broadcastInfo.getMessage());
            broadcastInfo = uc.pollBroadcast();
        }

        // Return them back to the bufferspace
        // NOTE: This may change the order of messages
        for (int index = 0; index < circularBuffer.size(); index ++) {
            uc.performAction(ActionType.BROADCAST, null, (int)circularBuffer.getDynamic(index));
            uc.println("comms 5 with " + uc.getPercentageOfEnergyLeft());
        }

        uc.println("comms 6 with " + uc.getPercentageOfEnergyLeft());

        // return the answer
        return circularBuffer;
    }

    public void commBroadcast(int value) {
        uc.performAction(ActionType.BROADCAST, null, value);
    }
}