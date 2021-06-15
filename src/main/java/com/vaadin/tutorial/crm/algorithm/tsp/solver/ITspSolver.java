package com.vaadin.tutorial.crm.algorithm.tsp.solver;

import com.vaadin.tutorial.crm.algorithm.tsp.util.AdjacencyMatrix;
import com.vaadin.tutorial.crm.algorithm.tsp.util.TspPathSequence;

public interface ITspSolver {
    TspPathSequence solve(AdjacencyMatrix matrix, int startLocation);
}
