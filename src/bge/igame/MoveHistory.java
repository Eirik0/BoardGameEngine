package bge.igame;

import java.util.ArrayList;
import java.util.List;

public class MoveHistory<M> {
    private final int numberOfPlayers;

    public MoveIndex selectedMoveIndex = new MoveIndex(0, 0);
    public MoveIndex maxMoveIndex = new MoveIndex(0, 0);

    private final List<HistoryMove<M>> moveHistoryList = new ArrayList<>();

    public MoveHistory(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        setIndex(new MoveIndex(-1, numberOfPlayers - 1));
    }

    private void setIndex(MoveIndex selectedAndMaxIndex) {
        maxMoveIndex = selectedAndMaxIndex;
        selectedMoveIndex = selectedAndMaxIndex;
    }

    public synchronized void addMove(M move, int playerNum) {
        if (move == null) {
            moveHistoryList.clear();
            setIndex(new MoveIndex(-1, numberOfPlayers - 1));
            return;
        }
        MoveIndex nextIndex = MoveIndex.nextIndex(selectedMoveIndex, playerNum - 1, numberOfPlayers);
        if (nextIndex.moveNumber == moveHistoryList.size()) {
            HistoryMove<M> historyMove = new HistoryMove<>(numberOfPlayers);
            historyMove.addMove(move, playerNum);
            moveHistoryList.add(historyMove);
            setIndex(new MoveIndex(nextIndex.moveNumber, playerNum - 1));
            return;
        }
        HistoryMove<M> historyMove = moveHistoryList.get(nextIndex.moveNumber);
        M playerMove = historyMove.getPlayerMove(playerNum);
        if (playerMove == null) {
            historyMove.addMove(move, playerNum);
            setIndex(new MoveIndex(nextIndex.moveNumber, playerNum - 1));
        } else if (playerMove.equals(move)) {
            historyMove.addMove(move, playerNum);
            selectedMoveIndex = new MoveIndex(nextIndex.moveNumber, playerNum - 1);
        } else {
            historyMove.addMove(move, playerNum);
            int moveNum = moveHistoryList.size() - 1;
            while (moveNum > nextIndex.moveNumber) {
                moveHistoryList.remove(moveNum);
                --moveNum;
            }
            historyMove.clearFrom(playerNum, numberOfPlayers);
            setIndex(new MoveIndex(nextIndex.moveNumber, playerNum - 1));
        }
    }

    public synchronized List<HistoryMove<M>> getMoveHistoryListCopy() {
        return new ArrayList<>(moveHistoryList);
    }

    public M setPositionFromHistory(IPosition<M> position, int moveNumToFind, int playerNumToFind) {
        int moveNum = 0;
        while (moveNum < moveNumToFind) {
            HistoryMove<M> historyMove = moveHistoryList.get(moveNum);
            makeMoves(position, historyMove, historyMove.moves.length - 1);
            ++moveNum;
        }
        HistoryMove<M> lastHistoryMove = moveHistoryList.get(moveNum);
        selectedMoveIndex = new MoveIndex(moveNumToFind, playerNumToFind);
        return makeMoves(position, lastHistoryMove, playerNumToFind);
    }

    private M makeMoves(IPosition<M> position, HistoryMove<M> historyMove, int maxPlayer) {
        M lastMove;
        int playerNum = 0;
        do {
            lastMove = historyMove.moves[playerNum];
            if (lastMove != null) {
                position.makeMove(lastMove);
            }
            ++playerNum;
        } while (playerNum <= maxPlayer);
        return lastMove;
    }

    public static class MoveIndex {
        public final int moveNumber;
        public final int playerNum;

        public MoveIndex(int moveNumber, int playerNum) {
            this.moveNumber = moveNumber;
            this.playerNum = playerNum;
        }

        public static MoveIndex nextIndex(MoveIndex currentIndex, int playerNum, int numberOfPlayers) {
            if (currentIndex.playerNum < numberOfPlayers - 1) {
                if (playerNum <= currentIndex.playerNum) {
                    return new MoveIndex(currentIndex.moveNumber + 1, playerNum);
                }
                return new MoveIndex(currentIndex.moveNumber, playerNum);
            } else {
                return new MoveIndex(currentIndex.moveNumber + 1, playerNum);
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            return prime * (prime + moveNumber) + playerNum;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            MoveIndex other = (MoveIndex) obj;
            return moveNumber == other.moveNumber && playerNum == other.playerNum;
        }

        @Override
        public String toString() {
            return "[" + moveNumber + "," + playerNum + "]";
        }
    }

    public static class HistoryMove<M> {
        public final M[] moves;

        @SuppressWarnings("unchecked")
        public HistoryMove(int numberOfPlayers) {
            moves = (M[]) new Object[numberOfPlayers];
        }

        void addMove(M move, int playerNum) {
            moves[playerNum - 1] = move;
        }

        M getPlayerMove(int playerNum) {
            return moves[playerNum - 1];
        }

        void clearFrom(int playerNum, int numberOfPlayers) {
            int i = playerNum;
            while (i < numberOfPlayers) {
                moves[i] = null;
                ++i;
            }
        }
    }
}
