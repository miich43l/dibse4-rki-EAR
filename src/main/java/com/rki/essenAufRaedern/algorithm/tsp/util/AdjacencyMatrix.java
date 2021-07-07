package com.rki.essenAufRaedern.algorithm.tsp.util;

/**
 * @author Thomas Widmann
 */
public class AdjacencyMatrix {

    private final int dimension;
    private final double[][] matrixData;

    public AdjacencyMatrix(int dimension) {
        this.dimension = dimension;
        matrixData = new double[dimension][dimension];
    }

    public double[][] getMatrix() {
        return matrixData;
    }

    public void setElement(int x, int y, double dVal) {
        if(x > dimension - 1 || x < 0) {
            throw new IllegalArgumentException("Index X out of range!");
        }

        if(y > dimension - 1 || x < 0) {
            throw new IllegalArgumentException("Index Y out of range!");
        }

        matrixData[x][y] = dVal;
    }

    public double getElement(int x, int y) {
        if( ! (x >= 0 && x < dimension)) {
            throw new IllegalArgumentException("Index X out of range!");
        }

        if( ! (y >= 0 && y < dimension)) {
            throw new IllegalArgumentException("Index Y out of range!");
        }

        return  matrixData[x][y];
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Adjacency matrix:\n");
        str.append("Dimension: ").append(dimension).append("x").append(dimension).append("\n");

        for(int row = 0; row < dimension; row++) {
            for(int col = 0; col < dimension; col++) {
                str.append(matrixData[row][col]).append(" ");
            }
            str.append("\n");
        }

        return str.toString();
    }
}
