package com.rki.essenAufRaedern.tsp.util;

public class AdjacencyMatrix {

    public AdjacencyMatrix(int nDimension) {
        m_nDimension = nDimension;
        m_arrMatrix = new double[nDimension][nDimension];
    }

    public double[][] getMatrix() {
        return m_arrMatrix;
    }

    public void setElement(int x, int y, double dVal) {
        if(x > m_nDimension - 1 || x < 0) {
            throw new IllegalArgumentException("Index X out of range!");
        }

        if(y > m_nDimension - 1 || x < 0) {
            throw new IllegalArgumentException("Index Y out of range!");
        }

        m_arrMatrix[x][y] = dVal;
    }

    public int getDimension() {
        return m_nDimension;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Adjacency matrix:\n");
        str.append("Dimension: ").append(m_nDimension).append("x").append(m_nDimension).append("\n");

        for(int row = 0; row < m_nDimension; row++) {
            for(int col = 0; col < m_nDimension; col++) {
                str.append(m_arrMatrix[row][col]).append(" ");
            }
            str.append("\n");
        }

        return str.toString();
    }

    int m_nDimension;
    double[][] m_arrMatrix;
}
