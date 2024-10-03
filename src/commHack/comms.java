package commHack;

import aic2024.engine.Unit;
import aic2024.user.*;
import commHack.*;

// IMPORTANT NOTE: The communicaation works quite poorly in this game
// Bots can post messages which can be read in the same turn by other bots whoose turn is after
// Bots can also poll messages, but this will remove that message from the broadcast, ONLY after every bots has taken their turn for that teams turn
// Also bots cannot read their own messages
// This class artifically slows down communication by suggesting to use send and receive every other turn. This gaurantees bots all see the same message (minus the fact they cant read their own messages)
// TODO: We can actually communicate with bots that dont have radios by using polls to send numbers to HQ by removing integers to communicate information
// NOTE: To do this we have to make sure bots dont send messages at the same time

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
        // IMPORTANT NOTE: Will not keep track of self made messages

        // Try to do only every even turn
        if (uc.getRound() % 2 != 0) {
            uc.println("Try to call this function every even turn!!!");
        }

//        uc.println("comms 1 with " + uc.getPercentageOfEnergyLeft());

        // Reset the circular buffer if it is a new turn
        if (roundNum != uc.getRound()) {
            Buffer.clear();
            roundNum = uc.getRound();
        }
//            uc.println("clearing the buffer, the buffer has size " + Buffer.size());
//        } else {
//            uc.println("not clearing the buffer");
//        }

//        uc.println("comms 2 with " + uc.getPercentageOfEnergyLeft());

        BroadcastInfo broadcastInfo = uc.pollBroadcast();

        // Go through every message that isnt null
        while (broadcastInfo != null) {
            int value = broadcastInfo.getMessage();
//            uc.println("the message value is " + value);
            Buffer.add(value);
            broadcastInfo = uc.pollBroadcast();
        }

//        uc.println("comms 6 with " + uc.getPercentageOfEnergyLeft());

        // return the answer
        return Buffer;
    }

    public void commBroadcast(int value) {
        // Try to call this function only every odd turn
        if (uc.getRound() == 0) {
//            uc.println("Try to call this function only on odd turns");
        }
        uc.performAction(ActionType.BROADCAST, null, value);
    }

    // got to be better way to do this but I am lazy
    public int booleanToInt (boolean bool) {
        return bool ? 1: 0;
    }

    // This will convert 3 booleans into an int and back again
    public int threeBooleanToInt(boolean a, boolean b, boolean c) {
        return 4*booleanToInt(a) + 2*booleanToInt(b) + booleanToInt(c);
    }

    // Convert an integer (0 to 7) to three boolean values
    public boolean[] intToThreeBooleans(int value) {

        boolean a = (value & 4) != 0;
        boolean b = (value & 2) != 0;
        boolean c = (value & 1) != 0;

        return new boolean[] { a, b, c };
    }
}