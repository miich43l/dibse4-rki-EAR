package com.rki.essenAufRaedern.tsp.solver;

import com.rki.essenAufRaedern.tsp.util.AdjacencyMatrix;
import com.rki.essenAufRaedern.tsp.util.TspPathSequence;

public interface ITspSolver {
    TspPathSequence solve(AdjacencyMatrix matrix, int startLocation);
}
