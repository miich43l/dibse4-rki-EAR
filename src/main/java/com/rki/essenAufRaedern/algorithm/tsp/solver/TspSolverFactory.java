package com.rki.essenAufRaedern.algorithm.tsp.solver;

/**
 * @author Thomas Widmann
 * Factory to create a new traveling salesman algorithm solver.
 */
public class TspSolverFactory {

    private static final TspSolverFactory oInstance = new TspSolverFactory();

    private TspSolverFactory() {
    }

    public static TspSolverFactory get() {
        return oInstance;
    }

    public ITspSolver createDefaultSolver() {
        return new TspDefaultSolver();
    }
}
