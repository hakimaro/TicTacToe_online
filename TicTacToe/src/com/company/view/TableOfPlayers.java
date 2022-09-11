package com.company.view;

import com.company.model.online.Client;
import com.company.view.button.ButtonItem;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TableOfPlayers extends GeneralPanel implements ActionListener {
    ButtonItem create = new ButtonItem("Создать");
    ButtonItem update = new ButtonItem("Обновить");
    ButtonItem exit = new ButtonItem("Выйти");
    Game game;
    Table table = new Table();

    public TableOfPlayers(JFrame frame, Client client) {
        super(frame, client);
        setLayout(null);
        create.setBounds(350,10,200,40);
        add(create);
        create.addActionListener(this);
        update.setBounds(650, 10,200, 40);
        add(update);
        update.addActionListener(this);
        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(300,70,600, 600);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(pane);
        exit.setBounds(500,680,200,40);
        add(exit);
        exit.addActionListener(this);
        frame.setContentPane(this);
    }

    public String getRoom() {
        return client.getMessage();
    }

    public void update() {
        table.removeAll();
        table.update();
        table.repaint();
    }

    class Table extends JPanel implements ActionListener {
        Table() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            String name;
        }

        public void update() {
            String name;
            while (!(name = getRoom()).equals("0")) {
                addRoom(name);
            }
        }

        public void addRoom(String nickname) {
            JButton btn = new ButtonItem(nickname);
            btn.setFont(new Font("Arial", Font.PLAIN,16));
            btn.addActionListener(this);
            add(btn);
            validate();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ButtonItem btn = (ButtonItem) e.getSource();
            String nickname = btn.getText().substring(btn.getText().indexOf(" ") + 1, btn.getText().indexOf("."));
            int size = Integer.parseInt(btn.getText().substring(btn.getText().lastIndexOf(" ") + 1));
            client.sendMessage("@join " + nickname);
            if (game != null) game.dispose();
            new Field(frame, size, client, 'o');
        }
    }

    class Game extends JDialog implements ActionListener {
        ButtonItem X = new ButtonItem("Крестики");
        ButtonItem O = new ButtonItem("Нолики");
        JRadioButton[] size = new JRadioButton[8];
        ButtonGroup group = new ButtonGroup();

        public Game() {
            super(frame);
            setUndecorated(true);
            setSize(400,200);
            setResizable(false);
            setLocation(800, 400);
            JPanel panel = new JPanel();
            panel.setLayout(null);
            panel.setBorder(new LineBorder(Color.black,2));
            panel.add(X);
            panel.add(O);
            X.setBounds(50, 40, 125, 50);
            O.setBounds(400-185, 40, 125, 50);
            for(int i = 3; i <= 10; i++) {
                size[i-3] = new JRadioButton(String.valueOf(i));
                if (i == 3) size[i-3].setSelected(true);
                panel.add(size[i-3]);
                size[i-3].setBounds(40+40*(i-3), 0, 40, 35);
                group.add(size[i-3]);
            }
            X.addActionListener(this);
            O.addActionListener(this);
            add(panel);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton btn = (JButton) e.getSource();
            int size = 3;
            for (int i = 0; i < this.size.length; i++) {
                if (this.size[i].isSelected()) {
                    size = i+3;
                }
            }
            switch (btn.getText()) {
                case "Крестики":
                    client.sendMessage("@create " + size + " x");
                    new Field(frame, size, client, 'x');
                    dispose();
                    break;
                case "Нолики":
                    client.sendMessage("@create " + size + " o");
                    new Field(frame, size, client, 'o');
                    dispose();
                    break;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ButtonItem btn = (ButtonItem) e.getSource();
        switch (btn.getText()) {
            case "Создать":
                game = new Game();
                break;
            case "Обновить":
                client.sendMessage("@update");
                if (game != null) game.dispose();
                update();
                break;
            case "Выйти":
                client.sendMessage("@exit");
                client.closeSocket();
                this.setVisible(false);
                new NewGame(frame);
                break;
        }
    }
}
