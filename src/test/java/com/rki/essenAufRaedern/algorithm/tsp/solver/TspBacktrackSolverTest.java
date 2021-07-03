package com.rki.essenAufRaedern.algorithm.tsp.solver;

import com.rki.essenAufRaedern.algorithm.tsp.util.AdjacencyMatrix;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPathSequence;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TspBacktrackSolverTest {

    @Test
    void solve_starting_at_0() {
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

        List<Integer> expectedResult = new ArrayList<>();
        expectedResult.add(0);
        expectedResult.add(1);
        expectedResult.add(2);
        expectedResult.add(0);

        solveTsp(matrix, expectedResult, 0);
    }

    @Test
    void solve_starting_at_1() {
        AdjacencyMatrix matrix = new AdjacencyMatrix(4);
        matrix.setElement(0, 0, 0);
        matrix.setElement(0, 1, 20);
        matrix.setElement(0, 2, 42);
        matrix.setElement(0, 3, 35);
        matrix.setElement(1, 0, 20);
        matrix.setElement(1, 1, 0);
        matrix.setElement(1, 2, 30);
        matrix.setElement(1, 3, 34);
        matrix.setElement(2, 0, 42);
        matrix.setElement(2, 1, 30);
        matrix.setElement(2, 2, 0);
        matrix.setElement(2, 3, 12);
        matrix.setElement(3, 0, 35);
        matrix.setElement(3, 1, 34);
        matrix.setElement(3, 2, 12);
        matrix.setElement(3, 3, 0);

        List<Integer> expectedResult = new ArrayList<>();
        expectedResult.add(1);
        expectedResult.add(2);
        expectedResult.add(3);
        expectedResult.add(0);
        expectedResult.add(1);

        solveTsp(matrix, expectedResult, 1);
    }

    @Test
    void solve_empty_matrix() {
        assertThrows(IllegalArgumentException.class, () -> {
            solveTsp(new AdjacencyMatrix(0), new ArrayList<>(), 0);
        });
    }

    void solveTsp(AdjacencyMatrix adjacencyMatrix, List<Integer> expectedResult, int startLocation) {
        TspBacktrackSolver solver = new TspBacktrackSolver();
        TspPathSequence sequence = solver.solve(adjacencyMatrix, startLocation);

        assertEquals(expectedResult, sequence.getPath());
    }
}