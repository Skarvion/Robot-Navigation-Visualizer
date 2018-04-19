package org.robotnav.SearchAlgorithm.informed;

import org.robotnav.SearchAlgorithm.Data.ActionNode;
import org.robotnav.SearchAlgorithm.Tree.Node;

import java.util.Comparator;

public class HeuristicComparator implements Comparator<Node<ActionNode>> {
    @Override
    public int compare(Node<ActionNode> o1, Node<ActionNode> o2) {
        int h1 = o1.getData().getHeuristic();
        int h2 = o2.getData().getHeuristic();

        if (h1 > h2) return 1;
        else if (h1 == h2) return -1;
        else return -1;
    }
}
