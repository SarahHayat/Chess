package fr.rphstudio.chess.game;

import fr.rphstudio.chess.interf.EmptyCellException;
import fr.rphstudio.chess.interf.IChess;
import fr.rphstudio.chess.interf.OutOfBoardException;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;


/**
 * Class used to retrieve the position's chess pieces.
 *
 * @author Team KING
 */
public class ChessModel implements IChess {

    /**
     * Private field containing coordinates of chessboard.
     */
    private GameBoard gameBoard = new GameBoard();
    private List<HashMap<ChessPosition, Piece>> allState;
    /**
     * Private field containing the only chessboard.
     */
    private static final IChess instance = new ChessModel();

    /**
     * Constructor's ChessModel.
     */
    private ChessModel() {
        this.allState = new ArrayList<>();
        allState.add(Utils.getStateBoard(gameBoard));

    }

    /**
     * Instance of the chessboard.
     */
    public static IChess getInstance() {
        return instance;
    }

    /**
     * Method used to reset chessboard.
     *
     */
    @Override
    public void reinit() {
        this.gameBoard = new GameBoard();
    }

    /**
     * Method used to search the piece type at a position.
     *
     * @param p position of the chess piece.
     * @return type's chess piece.
     */
    @Override
    public ChessType getPieceType(ChessPosition p) throws EmptyCellException, OutOfBoardException {
        Utils.checkPosition(p);
        Piece piece = gameBoard.getPiece(p);
        if (piece != null) {
            return piece.getChessType();
        } else {
            throw new EmptyCellException();
        }
    }

    /**
     * Method used to search the piece color at a position.
     *
     * @param p position's chess piece.
     * @return color's chess piece.
     */
    @Override
    public ChessColor getPieceColor(ChessPosition p) throws EmptyCellException, OutOfBoardException {
        Utils.checkPosition(p);
        Piece piece = gameBoard.getPiece(p);
        if (piece != null) {
            return piece.getChessColor();
        } else {
            throw new EmptyCellException();
        }
    }

    /**
     * Method used to knom a number's chess piece remaining.
     *
     * @param color color's piece.
     * @return the number of remaining chess pieces.
     */
    @Override
    public int getNbRemainingPieces(ChessColor color) {
        int nb = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (getPieces(i, j) != null) {
                    if (getPieces(i, j).getChessColor() == color)
                        nb++;
                }
            }
        }
        return nb;
    }

    /**
     * Method used to know the moves a chess piece.
     *
     * @param p position's chess piece.
     * @return the list of possible movements of a chess piece.
     */
    @Override
    public List<ChessPosition> getPieceMoves(ChessPosition p) {

        Piece piece = gameBoard.getPiece(p);

        List<ChessPosition> list = piece.getMove(p, this.gameBoard);
        for (int i = list.size() - 1; i >= 0; i--) {
            if (Utils.isOutOfBound(list.get(i))) {
                list.remove(list.get(i));
            }
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            if (!Utils.isEmpty(list.get(i), gameBoard) && !Utils.isEnemy(p, list.get(i), gameBoard)) {
                list.remove(list.get(i));
            }
        }
        // save KING
        for (int i = list.size() - 1; i >= 0; i--) {
            gameBoard.setTest(true);
            Piece currentPiece = gameBoard.getPiece(p);
            Piece pieceTemp = null;
            ChessPosition pTemp = list.get(i);
            if (!Utils.isEmpty(pTemp, gameBoard)) {
                pieceTemp = gameBoard.getPiece(pTemp);
            }
            movePiece(p, pTemp);

            if (getKingState(gameBoard.getPiece(pTemp).getChessColor()) == ChessKingState.KING_THREATEN) {
                list.remove(list.get(i));
            }
            gameBoard.setPiece(pTemp, null);
            gameBoard.setPiece(p, currentPiece);
            if (pieceTemp != null) {
                gameBoard.setPiece(pTemp, pieceTemp);
            }
            gameBoard.getPiece(p).setMovesCount(gameBoard.getPiece(p).getMovesCount() - 1);
            gameBoard.setTest(false);
        }


        return list;
    }

    /**
     * Method used to define the moves a chess piece.
     *
     * @param p0 position's chess piece before.
     * @param p1 position's chess piece after.
     */
    @Override
    public void movePiece(ChessPosition p0, ChessPosition p1) {

        gameBoard.getPiece(p0).setMovesCount(gameBoard.getPiece(p0).getMovesCount() + 1);
        gameBoard.setPiece(p1, gameBoard.getPiece(p0));

        //transforme pion en dame
        if (gameBoard.getPiece(p1).getChessType() == ChessType.TYP_PAWN && (p1.y == 0 || p1.y == 7)) {
            gameBoard.setPiece(p1, new Piece(gameBoard.getPiece(p1).getChessColor(), IChess.ChessType.TYP_QUEEN));

            if (!gameBoard.isTest()) {

                if (gameBoard.getPiece(p0).getChessColor() == ChessColor.CLR_BLACK) {
                    List<ChessType> list = new ArrayList<>(gameBoard.getListTemoinBlack());
                    for (int k = list.size() - 1; k >= 0; k--) {
                        if (list.get(k) == ChessType.TYP_PAWN) {
                            list.remove(list.get(k));
                            list.add(ChessType.TYP_QUEEN);
                            break;
                        }
                    }
                    gameBoard.setListTemoinBlack(list);
                }

                if (gameBoard.getPiece(p0).getChessColor() == ChessColor.CLR_WHITE) {
                    List<ChessType> list = new ArrayList<>(gameBoard.getListTemoinWhite());
                    for (int k = list.size() - 1; k >= 0; k--) {
                        if (list.get(k) == ChessType.TYP_PAWN) {
                            list.remove(list.get(k));
                            list.add(ChessType.TYP_QUEEN);
                            break;
                        }
                    }
                    gameBoard.setListTemoinWhite(list);
                }
            }
        }
        //section roque
        if (gameBoard.getPiece(p0).getChessType() == ChessType.TYP_KING) {

            ArrayList<IChess.ChessPosition> listRookPos = Utils.getRookPosAlly(p0, gameBoard);

            for (int i = 0; i <= listRookPos.size() - 1; i++) {
                IChess.ChessPosition tempPos = listRookPos.get(i);
                //petit roque
                if (tempPos.x == 7 && p1.x == p0.x + 2) {
                    ChessPosition target = new ChessPosition(p1.x - 1, p1.y);
                    gameBoard.getPiece(tempPos).setMovesCount(gameBoard.getPiece(tempPos).getMovesCount() + 1);
                    gameBoard.setPiece(target, gameBoard.getPiece(tempPos));
                    gameBoard.setPiece(tempPos, null);
                    if (gameBoard.isTest()) {
                        gameBoard.setPiece(tempPos, gameBoard.getPiece(target));
                        gameBoard.setPiece(target, null);
                        gameBoard.getPiece(tempPos).setMovesCount(gameBoard.getPiece(tempPos).getMovesCount() - 1);

                    }

                }
                //grand roque
                if (tempPos.x == 0 && p1.x == p0.x - 2) {
                    ChessPosition target = new ChessPosition(p1.x + 1, p1.y);
                    gameBoard.getPiece(tempPos).setMovesCount(gameBoard.getPiece(tempPos).getMovesCount() + 1);
                    gameBoard.setPiece(target, gameBoard.getPiece(tempPos));
                    gameBoard.setPiece(tempPos, null);
                    if (gameBoard.isTest()) {
                        gameBoard.setPiece(tempPos, gameBoard.getPiece(target));
                        gameBoard.setPiece(target, null);
                        gameBoard.getPiece(tempPos).setMovesCount(gameBoard.getPiece(tempPos).getMovesCount() - 1);

                    }
                }
            }

        }

        gameBoard.setPiece(p0, null);
        //enregistre board
        Utils.saveBoard(gameBoard,allState);

    }

    /**
     * Method used to know state's king.
     *
     * @param color color of the king.
     * @return if king safe or not.
     */
    @Override
    public ChessKingState getKingState(ChessColor color) {


        ChessPosition kingPos = Utils.getKingPosition(gameBoard, color);
        List<ChessPosition> list = Utils.enemyMovement(color, gameBoard);

        for (ChessPosition p : list) {
            if (kingPos.equals(p)) {
                return ChessKingState.KING_THREATEN;
            }
        }
        return ChessKingState.KING_SAFE;
    }

    /**
     * Method used to undo the last move.
     *
     * @return bool
     */
    @Override
    public List<ChessType> getRemovedPieces(ChessColor color) {

        List<ChessType> listTemoin = new ArrayList<>(gameBoard.getListTemoinWhite());
        if (color == ChessColor.CLR_BLACK)
            listTemoin = new ArrayList<>(gameBoard.getListTemoinBlack());
        List<ChessType> listFinal = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                if (!Utils.isEmpty(position, gameBoard) && color == gameBoard.getPiece(position).getChessColor()) {
                    listFinal.add(gameBoard.getPiece(position).getChessType());
                }
            }
        }
        for (int k = listFinal.size() - 1; k >= 0; k--) {
            listTemoin.remove(listFinal.get(k));
        }
        return listTemoin;
    }

    /**
     * Method used to undo the last move.
     *
     * @return bool
     */
    @Override
    public boolean undoLastMove() {

        if (allState.size() > 1) {
            allState.remove(allState.size() - 1);
            HashMap<IChess.ChessPosition, Piece> state = allState.get(allState.size() - 1);
            state.forEach((key, value) -> gameBoard.setPiece(key, value));
            return true;
        }
        return false;

    }

    /**
     * Method used to know time's turn player.
     *
     * @param color     is the color's player.
     * @param isPlaying if the player is playing.
     * @return the timer.
     */
    @Override
    public long getPlayerDuration(ChessColor color, boolean isPlaying) {
        return 0;
    }


    /**
     * Method used to check the position's chess piece.
     *
     * @param x X-position of the chess piece.
     * @param y Y-position of the chess piece.
     * @return chess piece at this position.
     */
    public Piece getPieces(int x, int y) {
        ChessPosition chessPosition = new ChessPosition(x, y);
        return gameBoard.getPiece(chessPosition);
    }


}
