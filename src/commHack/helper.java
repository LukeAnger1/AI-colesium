package commHack;

import aic2024.user.*;

public class helper {

    // This function checks if the first object is in the second array
    public boolean isIn (Object obj, Object[] arr) {
        for (Object obj2: arr) {
            if (obj.equals(obj2)) {
                return true;
            }
        }
        return false;
    }

    // Function to combine an array of arrays into a single array
    public Location[] combineArrays(Location[] array1, Location[] array2) {
        // Calculate total length
        int totalLength = 0;
        totalLength += array1.length;
        totalLength += array2.length;

        // Create the combined array
        Location[] combinedArray = new Location[totalLength];

        // Copy each array into the combined array
        int currentPosition = 0;
        for (Location element : array1) {
            // NOTE: This is a shallow copy
            combinedArray[currentPosition] = element;
            currentPosition ++;
        }
        for (Location element : array2) {
            // NOTE: This is a shallow copy
            combinedArray[currentPosition] = element;
            currentPosition ++;
        }

        return combinedArray;
    }

    // Function to combine three arrays into a single array
    public Location[] combineArrays(Location[] array1, Location[] array2, Location[] array3) {
        // Calculate total length
        int totalLength = array1.length + array2.length + array3.length;

        // Create the combined array
        Location[] combinedArray = new Location[totalLength];

        // Copy the first array into the combined array
        int currentPosition = 0;
        for (Location element : array1) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the second array into the combined array
        for (Location element : array2) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the third array into the combined array
        for (Location element : array3) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        return combinedArray;
    }

    // Function to combine four arrays into a single array
    public Location[] combineArrays(Location[] array1, Location[] array2, Location[] array3, Location[] array4) {
        // Calculate total length
        int totalLength = array1.length + array2.length + array3.length + array4.length;

        // Create the combined array
        Location[] combinedArray = new Location[totalLength];

        // Copy the first array into the combined array
        int currentPosition = 0;
        for (Location element : array1) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the second array into the combined array
        for (Location element : array2) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the third array into the combined array
        for (Location element : array3) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fourth array into the combined array
        for (Location element : array4) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        return combinedArray;
    }

    // Function to combine five arrays into a single array
    public Location[] combineArrays(Location[] array1, Location[] array2, Location[] array3, Location[] array4, Location[] array5) {
        // Calculate total length
        int totalLength = array1.length + array2.length + array3.length + array4.length + array5.length;

        // Create the combined array
        Location[] combinedArray = new Location[totalLength];

        // Copy the first array into the combined array
        int currentPosition = 0;
        for (Location element : array1) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the second array into the combined array
        for (Location element : array2) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the third array into the combined array
        for (Location element : array3) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fourth array into the combined array
        for (Location element : array4) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fifth array into the combined array
        for (Location element : array5) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        return combinedArray;
    }

    // Function to combine six arrays into a single array
    public Location[] combineArrays(Location[] array1, Location[] array2, Location[] array3, Location[] array4, Location[] array5, Location[] array6) {
        // Calculate total length
        int totalLength = array1.length + array2.length + array3.length + array4.length + array5.length + array6.length;

        // Create the combined array
        Location[] combinedArray = new Location[totalLength];

        // Copy the first array into the combined array
        int currentPosition = 0;
        for (Location element : array1) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the second array into the combined array
        for (Location element : array2) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the third array into the combined array
        for (Location element : array3) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fourth array into the combined array
        for (Location element : array4) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fifth array into the combined array
        for (Location element : array5) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the sixth array into the combined array
        for (Location element : array6) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        return combinedArray;
    }

    // Function to combine seven arrays into a single array
    public Location[] combineArrays(Location[] array1, Location[] array2, Location[] array3, Location[] array4, Location[] array5, Location[] array6, Location[] array7) {
        // Calculate total length
        int totalLength = array1.length + array2.length + array3.length + array4.length + array5.length + array6.length + array7.length;

        // Create the combined array
        Location[] combinedArray = new Location[totalLength];

        // Copy the first array into the combined array
        int currentPosition = 0;
        for (Location element : array1) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the second array into the combined array
        for (Location element : array2) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the third array into the combined array
        for (Location element : array3) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fourth array into the combined array
        for (Location element : array4) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fifth array into the combined array
        for (Location element : array5) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the sixth array into the combined array
        for (Location element : array6) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the seventh array into the combined array
        for (Location element : array7) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        return combinedArray;
    }

    // Function to combine eight arrays into a single array
    public Location[] combineArrays(Location[] array1, Location[] array2, Location[] array3, Location[] array4, Location[] array5, Location[] array6, Location[] array7, Location[] array8) {
        // Calculate total length
        int totalLength = array1.length + array2.length + array3.length + array4.length + array5.length + array6.length + array7.length + array8.length;

        // Create the combined array
        Location[] combinedArray = new Location[totalLength];

        // Copy the first array into the combined array
        int currentPosition = 0;
        for (Location element : array1) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the second array into the combined array
        for (Location element : array2) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the third array into the combined array
        for (Location element : array3) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fourth array into the combined array
        for (Location element : array4) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fifth array into the combined array
        for (Location element : array5) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the sixth array into the combined array
        for (Location element : array6) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the seventh array into the combined array
        for (Location element : array7) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the eighth array into the combined array
        for (Location element : array8) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        return combinedArray;
    }

    // Function to combine nine arrays into a single array
    public Location[] combineArrays(Location[] array1, Location[] array2, Location[] array3, Location[] array4, Location[] array5, Location[] array6, Location[] array7, Location[] array8, Location[] array9) {
        // Calculate total length
        int totalLength = array1.length + array2.length + array3.length + array4.length + array5.length + array6.length + array7.length + array8.length + array9.length;

        // Create the combined array
        Location[] combinedArray = new Location[totalLength];

        // Copy the first array into the combined array
        int currentPosition = 0;
        for (Location element : array1) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the second array into the combined array
        for (Location element : array2) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the third array into the combined array
        for (Location element : array3) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fourth array into the combined array
        for (Location element : array4) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fifth array into the combined array
        for (Location element : array5) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the sixth array into the combined array
        for (Location element : array6) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the seventh array into the combined array
        for (Location element : array7) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the eighth array into the combined array
        for (Location element : array8) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the ninth array into the combined array
        for (Location element : array9) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        return combinedArray;
    }

    // Function to combine ten arrays into a single array
    public Location[] combineArrays(Location[] array1, Location[] array2, Location[] array3, Location[] array4, Location[] array5, Location[] array6, Location[] array7, Location[] array8, Location[] array9, Location[] array10) {
        // Calculate total length
        int totalLength = array1.length + array2.length + array3.length + array4.length + array5.length + array6.length + array7.length + array8.length + array9.length + array10.length;

        // Create the combined array
        Location[] combinedArray = new Location[totalLength];

        // Copy the first array into the combined array
        int currentPosition = 0;
        for (Location element : array1) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the second array into the combined array
        for (Location element : array2) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the third array into the combined array
        for (Location element : array3) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fourth array into the combined array
        for (Location element : array4) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fifth array into the combined array
        for (Location element : array5) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the sixth array into the combined array
        for (Location element : array6) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the seventh array into the combined array
        for (Location element : array7) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the eighth array into the combined array
        for (Location element : array8) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the ninth array into the combined array
        for (Location element : array9) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the tenth array into the combined array
        for (Location element : array10) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        return combinedArray;
    }

    // Function to combine eleven arrays into a single array
    public Location[] combineArrays(Location[] array1, Location[] array2, Location[] array3, Location[] array4, Location[] array5, Location[] array6, Location[] array7, Location[] array8, Location[] array9, Location[] array10, Location[] array11) {
        // Calculate total length
        int totalLength = array1.length + array2.length + array3.length + array4.length + array5.length + array6.length + array7.length + array8.length + array9.length + array10.length + array11.length;

        // Create the combined array
        Location[] combinedArray = new Location[totalLength];

        // Copy the first array into the combined array
        int currentPosition = 0;
        for (Location element : array1) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the second array into the combined array
        for (Location element : array2) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the third array into the combined array
        for (Location element : array3) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fourth array into the combined array
        for (Location element : array4) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the fifth array into the combined array
        for (Location element : array5) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the sixth array into the combined array
        for (Location element : array6) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the seventh array into the combined array
        for (Location element : array7) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the eighth array into the combined array
        for (Location element : array8) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the ninth array into the combined array
        for (Location element : array9) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the tenth array into the combined array
        for (Location element : array10) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        // Copy the eleventh array into the combined array
        for (Location element : array11) {
            combinedArray[currentPosition] = element;
            currentPosition++;
        }

        return combinedArray;
    }

}
