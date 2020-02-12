package fr.rphstudio.chess.game;

import fr.rphstudio.chess.interf.IChess;
import fr.rphstudio.chess.interf.OutOfBoardException;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    /**
     * Method used to check the position's chess piece.
     *
     * @param p position of the chess piece.
     * @return if the chess piece is in the chessboard or not.
     */
    public static void checkPosition(IChess.ChessPosition p) throws OutOfBoardException {
        if (p.x < 0 || p.x > 7) {
            throw new OutOfBoardException();
        } else if (p.y < 0 || p.y > 7) {
            throw new OutOfBoardException();
        }
    }
    public static boolean isEmpty (IChess.ChessPosition p, GameBoard gameBoard ){
        return gameBoard.getPiece(p) == null;
    }

    public static boolean isEnemy(IChess.ChessPosition currentPosition, IChess.ChessPosition targetPosition, GameBoard gameBoard) {
        if (!isEmpty(targetPosition, gameBoard)) {
            return gameBoard.getPiece(currentPosition).getChessColor() != gameBoard.getPiece(targetPosition).getChessColor();
        } else {
            return false;
        }
    }

    public static boolean isOutofBound(IChess.ChessPosition p) {
        return ((p.x < 0 || p.x > 7) || p.y < 0 || p.y > 7);
    }
    public static IChess.ChessPosition getKingPosition(GameBoard gameBoard, IChess.ChessColor kingColor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!isEmpty(new IChess.ChessPosition(i, j), gameBoard)){
                    IChess.ChessType type = gameBoard.getPiece(new IChess.ChessPosition(i, j)).getChessType();
                    IChess.ChessColor color = gameBoard.getPiece(new IChess.ChessPosition(i, j)).getChessColor();
                    if (type == IChess.ChessType.TYP_KING && color == kingColor) {
                        return new IChess.ChessPosition(i, j);
                    }
                }

            }
        }
        return new IChess.ChessPosition(-32,-32);
    }


    //}
    public static ArrayList<IChess.ChessPosition> getRookPosAlly(IChess.ChessPosition p, GameBoard gameBoard) {
        IChess.ChessPosition positionBL = new IChess.ChessPosition(0, 0);
        IChess.ChessPosition positionBR = new IChess.ChessPosition(7, 0);
        IChess.ChessPosition positionWL = new IChess.ChessPosition(0, 7);
        IChess.ChessPosition positionWR = new IChess.ChessPosition(7, 7);
        ArrayList<IChess.ChessPosition> listRook = new ArrayList();

        if (!isEmpty(positionBL, gameBoard)) {
            if (!isEnemy(p, positionBL, gameBoard)) {
                listRook.add(positionBL);

            }
        }
        if (!isEmpty(positionWL, gameBoard)) {
            if (!isEnemy(p, positionWL, gameBoard)) {
                listRook.add(positionWL);
            }
        }
        if (!isEmpty(positionBR, gameBoard)) {
            if (!isEnemy(p, positionBR, gameBoard)) {
                listRook.add(positionBR);
            }
        }
        if (!isEmpty(positionWR, gameBoard)) {
            if (!isEnemy(p, positionWR, gameBoard)) {
                listRook.add(positionWR);
            }
        }
        return listRook;
    }
}
