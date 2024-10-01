package fuckingRush;

import aic2024.engine.Unit;
import aic2024.user.*;
import fuckingRush.*;

import java.nio.BufferUnderflowException;
import java.nio.file.DirectoryIteratorException;

// TODO: Can change this back to jbect to be more universal but nah

public class comms {

    public Buffer Buffer;
    public UnitController uc;
    public int roundNum = -1;

    // The Circular buffer is how much information can be stored during the turn
    public comms (UnitController uc, Buffer Buffer) {
        this.uc = uc;
        this.Buffer = Buffer;
    }

    // Cycle through all the broadcasts and save them
    public Buffer getAllComms() {

        uc.println("comms 1 with " + uc.getPercentageOfEnergyLeft());

        // Reset the circular buffer if it is a new turn
        if (roundNum != uc.getRound()) {
            Buffer.clear();
            roundNum = uc.getRound();
        }

        uc.println("comms 2 with " + uc.getPercentageOfEnergyLeft());

        BroadcastInfo broadcastInfo = uc.pollBroadcast();

        // Go through every message that isnt null
        while (broadcastInfo != null) {
            int value = broadcastInfo.getMessage();
            Buffer.add(value);
            broadcastInfo = uc.pollBroadcast();
        }

        // Return them back to the bufferspace
        // NOTE: This may change the order of messages
        for (int index = 0; index < Buffer.size(); index ++) {
            commBroadcast(Buffer.get(index));
            uc.println("comms 5 with " + uc.getPercentageOfEnergyLeft() + " with size " + Buffer.size());
        }

        uc.println("comms 6 with " + uc.getPercentageOfEnergyLeft());

        // return the answer
        return Buffer;
    }

    public void commBroadcast(int value) {
        uc.performAction(ActionType.BROADCAST, null, value);
    }
}