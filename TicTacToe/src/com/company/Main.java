package com.company;

import com.company.view.NewGame;
import com.company.view.TableOfPlayers;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class Main extends JFrame {
    Main(String name) {
        super(name);
        setLocation(400,100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200,800));
        setResizable(false);
        new NewGame(this);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main("TicTacToe");
    }
}
