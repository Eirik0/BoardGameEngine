package game.papersoccer;

public class PaperSoccerPositionHistory {
	final UndoPaperSoccerMove[] undoPaperSoccerMoves;
	int plyCount;

	public PaperSoccerPositionHistory() {
		this(new UndoPaperSoccerMove[PaperSoccerUtilities.MAX_MOVES], 0);
	}

	private PaperSoccerPositionHistory(UndoPaperSoccerMove[] undoPaperSoccerMoves, int plyCount) {
		this.undoPaperSoccerMoves = undoPaperSoccerMoves;
		this.plyCount = plyCount;
	}

	public void saveState(PaperSoccerPosition position) {
		undoPaperSoccerMoves[plyCount++] = new UndoPaperSoccerMove(position.currentPlayer, position.ballLocation);
	}

	public void unmakeMove(PaperSoccerPosition position) {
		UndoPaperSoccerMove undoPaperSoccerMove = undoPaperSoccerMoves[--plyCount];
		position.currentPlayer = undoPaperSoccerMove.player;
		position.ballLocation = undoPaperSoccerMove.ballLocation;
	}

	public PaperSoccerPositionHistory createCopy() {
		UndoPaperSoccerMove[] undoPaperSoccerMovesCopy = new UndoPaperSoccerMove[PaperSoccerUtilities.MAX_MOVES];
		System.arraycopy(undoPaperSoccerMoves, 0, undoPaperSoccerMovesCopy, 0, plyCount);
		return new PaperSoccerPositionHistory(undoPaperSoccerMovesCopy, plyCount);
	}

	static class UndoPaperSoccerMove {
		final int player;
		final int ballLocation;

		public UndoPaperSoccerMove(int player, int ballLocation) {
			this.player = player;
			this.ballLocation = ballLocation;
		}

		@Override
		public String toString() {
			return player + " " + ballLocation;
		}
	}
}
