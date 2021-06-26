package com.rki.essenAufRaedern.algorithm.tsp.solver;

import com.rki.essenAufRaedern.algorithm.tsp.util.AdjacencyMatrix;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPathSequence;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TspDefaultSolverTest {

    @Test
    void solve() {
        AdjacencyMatrix matrix = new AdjacencyMatrix(3);
        matrix.setElement(0, 0, 10);
        matrix.setElement(0, 1, 9);
        matrix.setElement(0, 2, 8);
        matrix.setElement(1, 0, 7);
        matrix.setElement(1, 1, 6);
        matrix.setElement(1, 2, 5);
        matrix.setElement(2, 0, 4);
        matrix.setElement(2, 1, 3);
        matrix.setElement(2, 2, 2);

        TspDefaultSolver solver = new TspDefaultSolver();
        TspPathSequence sequence = solver.solve(matrix, 0);

        List<Integer> expectedResult = new ArrayList<>();
        expectedResult.add(0);
        expectedResult.add(1);
        expectedResult.add(2);
        expectedResult.add(0);

        assertEquals(expectedResult, sequence.getPath());
    }
}