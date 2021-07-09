package com.rki.essenAufRaedern.algorithm.tsp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdjacencyMatrixTest {

    @Test
    void getMatrix() {
        AdjacencyMatrix matrix = new AdjacencyMatrix(3);
        double[][] doubleMatrix = matrix.getMatrix();
        for(int r = 0; r < matrix.getDimension(); r++) {
            for(int c = 0; c < matrix.getDimension(); c++) {
                assertEquals(0, doubleMatrix[r][c]);
            }
        }
    }

    @Test
    void setElement() {
        AdjacencyMatrix matrix = new AdjacencyMatrix(3);
        matrix.setElement(1, 2, 42);
        assertEquals(42, matrix.getMatrix()[1][2]);
    }

    @Test
    void getDimension() {
        AdjacencyMatrix matrix = new AdjacencyMatrix(42);
        assertEquals(42, matrix.getDimension());
    }
}