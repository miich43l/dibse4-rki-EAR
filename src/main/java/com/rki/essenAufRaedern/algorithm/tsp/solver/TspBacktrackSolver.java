package com.rki.essenAufRaedern.algorithm.tsp.solver;

import com.rki.essenAufRaedern.algorithm.tsp.util.AdjacencyMatrix;
import com.rki.essenAufRaedern.algorithm.tsp.util.TspPathSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Thomas Widmann
 * This implementation solves the TSP by using the backtrack algorithm.
 */
public class TspBacktrackSolver implements ITspSolver {

    private static class Node {
        boolean visited = false;

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        public static List<Node> createNodeList(int numberOfNodes) {
            return IntStream.rangeClosed(0, numberOfNodes).mapToObj(i -> new Node()).collect(Collectors.toList());
        }
    }

    private AdjacencyMatrix adjacencyMatrix;
    private List<Node> nodes;
    private final List<Integer> bestPath = new ArrayList<>();

    @Override
    public TspPathSequence solve(AdjacencyMatrix matrix, int startLocation) {
        if(matrix.getDimension() < 1) {
            throw new IllegalArgumentException("Invalid matrix passed! Dimension: " + matrix.getDimension());
        }

        if(startLocation < 0 || startLocation > matrix.getDimension() - 1) {
            throw new IllegalArgumentException("Invalid start location passed! Start location: " + startLocation + " matrix dimension: " + matrix.getDimension());
        }

        this.adjacencyMatrix = matrix;
        this.nodes = Node.createNodeList(matrix.getDimension());
        this.nodes.get(startLocation).setVisited(true);

        List<Integer> tmpPath = new ArrayList<>();
        double pathCost = solveTsp(startLocation, 1, 0, Double.MAX_VALUE, tmpPath, startLocation);

        this.bestPath.add(bestPath.size(), startLocation);

        return new TspPathSequence(pathCost, bestPath);
    }

    private double solveTsp(int currentPosIdx, int depth, double currentCost, double bestCost, List<Integer> visits, int startLocation) {
        visits.add(currentPosIdx);

        final boolean isMaxDepthReached = (depth == adjacencyMatrix.getDimension());
        final boolean hasEdgeToStartNode = (adjacencyMatrix.getElement(currentPosIdx, startLocation) > 0);

        if (isMaxDepthReached && hasEdgeToStartNode) {
            double costOfPathVariant = currentCost + adjacencyMatrix.getElement(currentPosIdx, 0);
            //System.out.println("End reached. Path: " + visits + " Cost: " + dNewCost + " Best: " + bestCost);

            if(costOfPathVariant < bestCost) {
                bestCost = costOfPathVariant;
                bestPath.clear();
                bestPath.addAll(visits);
            }

            visits.remove(visits.size() - 1);

            return bestCost;
        }

        for (int locationIndex = 0; locationIndex < adjacencyMatrix.getDimension(); locationIndex++) {
            if ( ! nodes.get(locationIndex).isVisited()  && adjacencyMatrix.getElement(currentPosIdx, locationIndex) > 0) {
                nodes.get(locationIndex).setVisited(true);
                bestCost = solveTsp(locationIndex, depth + 1,  currentCost + adjacencyMatrix.getElement(currentPosIdx, locationIndex), bestCost, visits, startLocation);
                nodes.get(locationIndex).setVisited(false);
            }
        }

        visits.remove(visits.size() - 1);

        return bestCost;
    }
}
