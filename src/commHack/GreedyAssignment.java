// NOt

package commHack;

import java.util.HashSet;
import java.util.Set;

public class GreedyAssignment {

    public static int[] assignConsumersToSuppliers(double[][] distanceMatrix) {
        int n = distanceMatrix.length;  // Number of consumers
        int m = distanceMatrix[0].length;  // Number of suppliers

        int[] assignments = new int[n];
        Set<Integer> assignedSuppliers = new HashSet<>(); // Keep track of which suppliers are already assigned

        for (int i = 0; i < n; i++) {
            int bestSupplier = -1;
            double minDistance = Double.MAX_VALUE;

            for (int j = 0; j < m; j++) {
                if (!assignedSuppliers.contains(j) && distanceMatrix[i][j] < minDistance) {
                    minDistance = distanceMatrix[i][j];
                    bestSupplier = j;
                }
            }

            assignments[i] = bestSupplier;
            assignedSuppliers.add(bestSupplier); // Mark supplier as assigned
        }

        return assignments;
    }
}