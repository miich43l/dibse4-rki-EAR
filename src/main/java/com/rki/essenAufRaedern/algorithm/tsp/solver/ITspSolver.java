package com.rki.essenAufRaedern.algorithm.tsp.solver;

import com.rki.essenAufRaedern.algorithm.tsp.util.AdjacencyMatrix;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPathSequence;

/**
 * @author Thomas Widmann
 * Traveling salesman algorithm interface.
 */
public interface ITspSolver {
    TspPathSequence solve(AdjacencyMatrix matrix, int startLocation);
}
