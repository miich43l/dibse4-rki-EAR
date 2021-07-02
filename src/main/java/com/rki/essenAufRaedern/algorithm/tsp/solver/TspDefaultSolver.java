package com.rki.essenAufRaedern.algorithm.tsp.solver;

import com.rki.essenAufRaedern.algorithm.tsp.util.AdjacencyMatrix;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPathSequence;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Widmann
 * Default traveling salesman algorithm implementation.
 */
public class TspDefaultSolver implements ITspSolver {

    // TODO:
    // - remove hungarian notation

    private int numberOfLocations = 0;
    private double[][] graphData;
    private boolean[] pointsVisited;
    private List<Integer> bestPath = new ArrayList<>();

    @Override
    public TspPathSequence solve(AdjacencyMatrix matrix, int startLocation) {
        numberOfLocations = matrix.getDimension();
        graphData = matrix.getMatrix();
        pointsVisited = new boolean[matrix.getDimension()];
        pointsVisited[0] = true;

        List<Integer> lstTmpPath = new ArrayList<>();
        double cost = tsp(startLocation, 1, 0, Double.MAX_VALUE, lstTmpPath);

        bestPath.add(bestPath.size(), startLocation);

        System.out.println("Best path: " + bestPath);

        return new TspPathSequence(cost, bestPath);
    }

    private double tsp(int nCurrentPosIdx, int nDepth, double dCurrentCost, double dBestCost, List<Integer> lstVisits) {
        lstVisits.add(nCurrentPosIdx);

        if (nDepth == numberOfLocations && graphData[nCurrentPosIdx][0] > 0) {
            double dNewCost = dCurrentCost + graphData[nCurrentPosIdx][0];

            //System.out.println("End reached. Path: " + lstVisits + " Cost: " + dNewCost + " Best: " + dBestCost);

            if(dNewCost < dBestCost)
            {
                dBestCost = dNewCost;
                bestPath.clear();
                bestPath.addAll(lstVisits);
            }

            lstVisits.remove(lstVisits.size() - 1);

            return dBestCost;
        }

        for (int nLocIdx = 0; nLocIdx < numberOfLocations; nLocIdx++)
        {
            if (!pointsVisited[nLocIdx]
                    && graphData[nCurrentPosIdx][nLocIdx] > 0)
            {
                pointsVisited[nLocIdx] = true;
                dBestCost = tsp(nLocIdx, nDepth + 1,  dCurrentCost + graphData[nCurrentPosIdx][nLocIdx], dBestCost, lstVisits);
                pointsVisited[nLocIdx] = false;
            }
        }

        lstVisits.remove(lstVisits.size() - 1);

        return dBestCost;
    }
}
