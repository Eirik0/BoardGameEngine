package game.forkjoinexample;

import java.util.ArrayList;
import java.util.List;

import game.TwoPlayers;

public class ForkJoinExampleNode {
    private final int player;

    private final Integer number;
    private double score = 0.0;
    private boolean quiescent = false;

    private ForkJoinExampleNode parent;
    private final ForkJoinExampleNode[] children;

    public ForkJoinExampleNode(boolean playerOne, int number, ForkJoinExampleNode[] children) {
        player = playerOne ? TwoPlayers.PLAYER_1 : TwoPlayers.PLAYER_2;
        this.number = Integer.valueOf(number);
        this.children = children;
        for (ForkJoinExampleNode child : children) {
            child.setParent(this);
        }
    }

    public int getPlayer() {
        return player;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isQuiescent() {
        return quiescent;
    }

    public void setQuiescent(boolean quiescent) {
        this.quiescent = quiescent;
    }

    public ForkJoinExampleNode getParent() {
        return parent;
    }

    private void setParent(ForkJoinExampleNode parent) {
        this.parent = parent;
    }

    public ForkJoinExampleNode[] getChildren() {
        return children;
    }

    @Override
    public String toString() {
        List<Integer> nodeList = new ArrayList<>();
        nodeList.add(number);
        ForkJoinExampleNode parentNode = parent;
        if (parentNode == null) {
            return number.toString();
        }
        while (parentNode != null) {
            nodeList.add(parentNode.number);
            parentNode = parentNode.parent;
        }
        StringBuilder sb = new StringBuilder();
        int i = nodeList.size() - 1;
        do {
            sb.append(nodeList.get(i)).append("->");
            --i;
        } while (i > 0);
        sb.append(nodeList.get(0));
        return sb.toString();
    }
}
