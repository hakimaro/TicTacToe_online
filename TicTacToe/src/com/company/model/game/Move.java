package com.company.model.game;

import java.util.Objects;

public class Move {
    private final int x;
    private final int y;

    public Move(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public int getToRow() {
        return x;
    }

    public int getToCol() {
        return y;
    }

    public String toString() {
        return x + "" + y;
    }
}
