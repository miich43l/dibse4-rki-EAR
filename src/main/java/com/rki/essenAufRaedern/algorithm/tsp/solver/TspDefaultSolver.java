package com.rki.essenAufRaedern.algorithm.tsp.solver;

import com.rki.essenAufRaedern.algorithm.tsp.util.AdjacencyMatrix;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPathSequence;

import java.util.ArrayList;
import java.util.List;

public class TspDefaultSolver implements ITspSolver {

    public TspDefaultSolver() {
    }

    @Override
    public TspPathSequence solve(AdjacencyMatrix matrix, int startLocation) {
        m_nLocations = matrix.getDimension();
        m_arrGraph = matrix.getMatrix();
        m_arrVisited = new boolean[matrix.getDimension()];
        m_arrVisited[0] = true;

        List<Integer> lstTmpPath = new ArrayList<>();
        double cost = tsp(startLocation, 1, 0, Double.MAX_VALUE, lstTmpPath);

        m_lstBestPath.add(m_lstBestPath.size(), startLocation);

        System.out.println("Best path: " + m_lstBestPath);

        return new TspPathSequence(cost, m_lstBestPath);
    }

    private double tsp(int nCurrentPosIdx, int nDepth, double dCurrentCost, double dBestCost, List<Integer> lstVisits) {
        lstVisits.add(nCurrentPosIdx);

        if (nDepth == m_nLocations && m_arrGraph[nCurrentPosIdx][0] > 0) {
            double dNewCost = dCurrentCost + m_arrGraph[nCurrentPosIdx][0];

            System.out.println("End reached. Path: " + lstVisits + " Cost: " + dNewCost + " Best: " + dBestCost);

            if(dNewCost < dBestCost)
            {
                dBestCost = dNewCost;
                m_lstBestPath.clear();
                m_lstBestPath.addAll(lstVisits);
            }

            lstVisits.remove(lstVisits.size() - 1);

            return dBestCost;
        }

        for (int nLocIdx = 0; nLocIdx < m_nLocations; nLocIdx++)
        {
            if (!m_arrVisited[nLocIdx]
                    && m_arrGraph[nCurrentPosIdx][nLocIdx] > 0)
            {
                m_arrVisited[nLocIdx] = true;
                dBestCost = tsp(nLocIdx, nDepth + 1,  dCurrentCost + m_arrGraph[nCurrentPosIdx][nLocIdx], dBestCost, lstVisits);
                m_arrVisited[nLocIdx] = false;
            }
        }

        lstVisits.remove(lstVisits.size() - 1);

        return dBestCost;
    }


    int m_nLocations = 0;
    double[][] m_arrGraph;
    boolean[] m_arrVisited;
    List<Integer> m_lstBestPath = new ArrayList<>();
}
