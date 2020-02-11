package fr.rphstudio.chess.game.moves;

import fr.rphstudio.chess.game.GameBoard;
import fr.rphstudio.chess.game.IMove;
import fr.rphstudio.chess.game.Piece;
import fr.rphstudio.chess.interf.IChess;

import java.util.ArrayList;
import java.util.List;

public class Pawn implements IMove {
    @Override
    public List<IChess.ChessPosition> getPossibleMoves(IChess.ChessPosition p, GameBoard gameBoard) {

        ArrayList<IChess.ChessPosition> list = new ArrayList<>();
        IChess.ChessPosition position = new IChess.ChessPosition(4,4);
       list.add(position);
       return list;
    }

}
