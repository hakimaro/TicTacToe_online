package com.company.model.game;

import com.company.model.online.Client;

import java.util.Arrays;

public class Data {
    private char[][] board;
    private boolean m_gameinprogress;
    char m_player = 'x';
    int inARow = 3;
    char winner;

    // Для онлайн игры
    boolean online;
    Client client;
    char side = 'x';

    public void startNewGame(int size, char player) {
        gameStart(size);
        online = false;
        m_gameinprogress = true;
        if (size > 4 && size < 8) inARow = 4;
        else if (size >= 8) inARow = 5;
    }

    public void startNewOnlineGame(int size, char m_player, Client client) {
        gameStart(size);
        online = true;
        m_gameinprogress = false;
        client(client);
        if (size > 4 && size < 8) inARow = 4;
        else if (size >= 8) inARow = 5;
    }

    public void client(Client client) {
        this.client = client;
    }

    public void gameStart(int size) {
        board = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = '_';
            }
        }
    }

    public void setMove(int x, int y, char value) {
        board[x][y] = value;
        if ((winner = getWinner()) != '_') {
            if (online) client.sendMessage("@looser");
        }
    }

    public char pieceAt(int row, int column) {
        return board[row][column];
    }

    public void makeMove(Move move) {
        if (m_gameinprogress) {
            board[move.getToRow()][move.getToCol()] = m_player;
            if (online && side == m_player) {
                client.sendMessage("@game " + move.getToRow() + " " + move.getToCol());
            }
            if (!online) changeActivePlayer();
            if ((winner = getWinner()) != '_') {
                if (online) client.sendMessage("@winner");
                m_gameinprogress = false;
            }
        }
    }

    public void changeActivePlayer() {
        if (m_player == 'x') m_player = 'o';
        else m_player = 'x';
    }

    public char getWinner() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (checkFrom(i, j)) {
                    return board[i][j];
                }
            }
        }
        return '_';
    }

    public boolean checkFrom(int x, int y) {
        char player = board[x][y];
        if (player == '_') return false;
        boolean xWin = true, yWin = true, xyWin = true, yxWin = true;
        for (int i = 0; i < inARow; i++) {
            if (x + i >= board.length || board[x + i][y] != player) xWin = false;
            if (y + i >= board.length || board[x][y + i] != player) yWin = false;
            if (x + i >= board.length || y - i < 0 || board[x + i][y - i] != player) xyWin = false;
            if (x - i < 0 || y - i < 0 || board[x - i][y - i] != player) yxWin = false;
        }
        return xWin || yWin || xyWin || yxWin;
    }

    public boolean isDraw() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == '_') return false;
            }
        }
        m_gameinprogress = false;
        return true;
    }

    public void setM_gameinprogress(boolean m_gameinprogress) {
        this.m_gameinprogress = m_gameinprogress;
    }

    public boolean isM_gameinprogress() {
        return m_gameinprogress;
    }

    public boolean isOnline() {
        return online;
    }

    public char getM_player() {
        return m_player;
    }

    public Client getClient() {
        return client;
    }

    public char getSide() {
        return side;
    }

    public void setSide(char side) {
        this.side = side;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                sb.append(board[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
