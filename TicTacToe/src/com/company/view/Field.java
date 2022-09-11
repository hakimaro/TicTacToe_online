package com.company.view;

import com.company.model.game.Data;
import com.company.model.game.Move;
import com.company.model.online.Client;
import com.company.view.button.ButtonItem;
import com.company.view.button.Square;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Все, связанное с игровым полем
 */
public class Field extends JPanel {
    JFrame frame;
    private final Data board = new Data();
    fieldPanel centralPanel;
    sidePanel leftPanel;
    sidePanel rightPanel;
    bottomPanel bottomPanel = new bottomPanel();
    JDialog creator;

    public Field(JFrame frame, int size) {
        this.frame = frame;
        setLayout(new BorderLayout());
        centralPanel = new fieldPanel(size);
        leftPanel = new sidePanel("   Крестики   ");
        leftPanel.changeJLabelColor();
        leftPanel.addLabel("Ваш ход");
        rightPanel = new sidePanel("    Нолики    ");
        add(centralPanel, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        creator = new GameCreator();
        this.frame.setContentPane(this);
    }

    public Field(JFrame frame, int size, Client client, char m_player) {
        this.frame = frame;
        setLayout(new BorderLayout());
        centralPanel = new fieldPanel(size);
        leftPanel = new sidePanel("   Крестики   ");
        leftPanel.changeJLabelColor();
        rightPanel = new sidePanel("    Нолики    ");
        add(centralPanel, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        this.frame.setContentPane(this);
        board.startNewOnlineGame(size, m_player, client);
        leftPanel.addLabel("Ожидание соперника");
        rightPanel.addLabel("Ожидание соперника");
        Thread start = new WaitingForOpp();
        start.start();
        revalidate();
    }

    public void repaintField(int size) {
        remove(centralPanel);
        centralPanel = new fieldPanel(size);
        add(centralPanel, BorderLayout.CENTER);
        validate();
    }

    /**
     * Окно для задания параметров создания поля
     */
    class GameCreator extends JDialog implements ActionListener {
        JRadioButton[] size = new JRadioButton[8];
        ButtonGroup group = new ButtonGroup();
        
        public GameCreator() {
            super(frame);
            setUndecorated(true);
            setSize(400,200);
            setResizable(false);
            setLocation(800, 400);
            JPanel panel = new JPanel();
            panel.setLayout(null);
            panel.setBorder(new LineBorder(Color.black,2));

            ButtonItem b_start = new ButtonItem("Начать игру");
            panel.add(b_start);
            b_start.setBounds(135, 100, 125, 50);

            for(int i = 3; i <= 10; i++) {
                size[i-3] = new JRadioButton(String.valueOf(i));
                if (i == 3) size[i-3].setSelected(true);
                panel.add(size[i-3]);
                size[i-3].setBounds(40+40*(i-3), 0, 40, 35);
                group.add(size[i-3]);
            }

            b_start.addActionListener(this);

            add(panel);
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int size = 3;
            for (int i = 0; i < this.size.length; i++) {
                if (this.size[i].isSelected()) {
                    size += i;
                }
            }
            repaintField(size);
            board.startNewGame(size, 'x');
            dispose();
        }
    }

    /**
     * Клетки и обработка нажатий
     */
    class fieldPanel extends JPanel implements MouseListener {
        Square[][] squares;

        public fieldPanel(int size) {
            this.setLayout(new GridLayout(size, size));
            this.addMouseListener(this);
            this.setBackground(new Color(223, 215, 196));
            this.addMouseListener(this);
            squares = new Square[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    squares[i][j] = new Square(i, j);
                    squares[i][j].addMouseListener(this);
                    this.add(squares[i][j]);
                }
            }
        }

        public Square getSquare(int x, int y) {
            return squares[x][y];
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (board.isM_gameinprogress()) {
                Square btn = (Square) e.getSource();
                if (board.pieceAt(btn.getI(), btn.getJ()) == '_' && !board.isOnline()) {
                    leftPanel.changeJLabelColor();
                    rightPanel.changeJLabelColor();
                    String move = (btn.getI() + 1) + " " + (btn.getJ() + 1);
                    board.makeMove(new Move(btn.getI(), btn.getJ()));
                    if (board.pieceAt(btn.getI(), btn.getJ()) == 'x') {
                        leftPanel.removeLabel("Ваш ход");
                        leftPanel.addLabel(move);
                        rightPanel.addLabel("Ваш ход");
                        squares[btn.getI()][btn.getJ()].paintX(1);
                    } else if (board.pieceAt(btn.getI(), btn.getJ()) == 'o') {
                        rightPanel.removeLabel("Ваш ход");
                        rightPanel.addLabel(move);
                        leftPanel.addLabel("Ваш ход");
                        squares[btn.getI()][btn.getJ()].paintX(-1);
                    }

                    if (board.getWinner() != '_') {
                        rightPanel.removeLabel("Ваш ход");
                        leftPanel.removeLabel("Ваш ход");
                        leftPanel.addLabel((board.getWinner() == 'x') ? "Победа" : "Поражение");
                        rightPanel.addLabel((board.getWinner() == 'o') ? "Победа" : "Поражение");
                    } else if (board.isDraw()) {
                        rightPanel.removeLabel("Ваш ход");
                        leftPanel.removeLabel("Ваш ход");
                        leftPanel.addLabel("Ничья");
                        rightPanel.addLabel("Ничья");
                    }
                }

                if (board.pieceAt(btn.getI(), btn.getJ()) == '_' && board.isOnline()) {
                    board.makeMove(new Move(btn.getI(), btn.getJ()));
                    board.getClient().sendMessage("@send " + btn.getI() + " " + btn.getJ());
                }

                board.setSide((board.getSide() == 'x') ? 'o' : 'x');
                rightPanel.validate();
                leftPanel.validate();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    /**
     * Боковые панели для истории ходов
     */
    public class sidePanel extends JPanel {
        JLabel player;
        textArea textArea = new textArea();

        public sidePanel(String name) {
            setLayout(new BorderLayout());
            player = new JLabel(name);
            player.setBorder(new LineBorder(Color.BLACK, 2));
            Font font = new Font("Arial", Font.BOLD, 36);
            player.setFont(font);
            add(player, BorderLayout.NORTH);
            JScrollPane scrollPane = new JScrollPane(textArea);
            add(scrollPane, BorderLayout.CENTER);
            setBackground(new Color(213, 198, 175));
        }

        public void changeJLabelColor() {
            if (player.getForeground() == Color.red) player.setForeground(Color.black);
            else player.setForeground(Color.red);
        }

        public void removeLabel(String sentence) {
            textArea.removeLabel(sentence);
            repaint();
        }

        public void addLabel(String move) {
            textArea.addLabel(move);
            repaint();
        }

        class textArea extends JPanel {
            Font font = new Font("Arial", Font.PLAIN, 24);
            public textArea() {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setBackground(new Color(201, 187, 166));
                setBorder(new LineBorder(new Color(0,0,0), 2));
            }

            public void addLabel(String move) {
                JLabel movesList = new JLabel(move);
                movesList.setFont(font);
                add(movesList);
            }

            public void removeLabel(String sentence) {
                for (Component c : getComponents()) {
                    if (c instanceof JLabel) {
                        if (((JLabel) c).getText().equals(sentence)) remove(c);
                    }
                }
                textArea.revalidate();
            }
        }
    }

    /**
     * Нижняя панель для выхода в меню
     */
    public class bottomPanel extends JPanel implements ActionListener {
        Font font = new Font("Arial", Font.BOLD, 32);
        ButtonItem resign = new ButtonItem("Выйти");

        public bottomPanel() {
            setLayout(new BorderLayout());
            resign.setFont(font);
            resign.addActionListener(this);
            setBackground(new Color(201, 187, 166));
            add(resign, BorderLayout.CENTER);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.setVisible(false);
            if (creator != null) {
                creator.setVisible(false);
            }
            System.out.println(board.isOnline());
            if (board.isOnline()) {
                if (board.isM_gameinprogress()) board.getClient().sendMessage("@oppExit");
                else board.getClient().sendMessage("@delete");
                new TableOfPlayers(frame, board.getClient());
            }
            else new NewGame(frame);
        }
    }

    public void backToMenu() {
        new TableOfPlayers(frame, board.getClient());
    }

    public class WaitingForOpp extends Thread {
        @Override
        public void run() {
            while(!currentThread().isInterrupted()) {
                System.out.println("Ожидание сообщения от второго пользователя");
                String message = board.getClient().getMessage();
                if (message.indexOf("@start") == 0) {
                    if (message.substring(message.indexOf(" ") + 1).equals("o")) board.changeActivePlayer();
                    leftPanel.removeLabel("Ожидание соперника");
                    rightPanel.removeLabel("Ожидание соперника");
                    board.setM_gameinprogress(true);
                }
                else if (message.indexOf("@send ") == 0) {
                    int indX = message.indexOf(" ") + 1;
                    int indY = message.indexOf(" ", indX + 1) + 1;
                    int x = Integer.parseInt(message.substring(indX, indY-1));
                    int y = Integer.parseInt(message.substring(indY, message.lastIndexOf(" ")));
                    board.setMove(x, y, message.charAt(message.length()-1));
                    leftPanel.changeJLabelColor();
                    rightPanel.changeJLabelColor();
                    centralPanel.getSquare(x,y).paintX((board.pieceAt(x,y) == 'x') ? 1 : -1);
                    leftPanel.removeLabel("Ваш ход");
                    leftPanel.removeLabel("Ход соперника");
                    rightPanel.removeLabel("Ваш ход");
                    rightPanel.removeLabel("Ход соперника");

                    if ((board.pieceAt(x, y) == 'x')) {
                        leftPanel.addLabel((x + 1) + " " + (y + 1));
                    } else {
                        rightPanel.addLabel((x + 1) + " " + (y + 1));
                    }

                    if (board.pieceAt(x, y) == 'x') {
                        if (board.getM_player() == 'x') leftPanel.addLabel("Ход соперника");
                        else rightPanel.addLabel("Ваш ход");
                    } else {
                        if (board.getM_player() == 'x') leftPanel.addLabel("Ваш ход");
                        else rightPanel.addLabel("Ход соперника");
                    }

                    if (board.getWinner() != '_' || board.isDraw()) {
                        leftPanel.removeLabel("Ваш ход");
                        leftPanel.removeLabel("Ход соперника");
                        rightPanel.removeLabel("Ваш ход");
                        rightPanel.removeLabel("Ход соперника");
                        if (board.getWinner() == 'x') {
                            leftPanel.addLabel("Победитель");
                            rightPanel.addLabel("Проигравший");
                        } else if (board.getWinner() == 'o') {
                            leftPanel.addLabel("Проигравший");
                            rightPanel.addLabel("Победитель");
                        } else if (board.isDraw()) {
                            leftPanel.addLabel("Ничья");
                            rightPanel.addLabel("Ничья");
                        }
                        board.setM_gameinprogress(false);
                    }
                }
                else if (message.equals("@oppExit")) {
                    board.setM_gameinprogress(false);
                    if (board.getM_player() == 'x') {
                        leftPanel.removeLabel("Ваш ход");
                        leftPanel.removeLabel("Ход соперника");
                        leftPanel.addLabel("Победитель");
                        rightPanel.addLabel("Проигравший");
                    } else {
                        rightPanel.removeLabel("Ваш ход");
                        rightPanel.removeLabel("Ход соперника");
                        rightPanel.addLabel("Победитель");
                        leftPanel.addLabel("Проигравший");
                    }
                    return;
                } else if (message.equals("@canceled")) {
                    setVisible(false);
                    backToMenu();
                    return;
                }
                leftPanel.validate();
                rightPanel.validate();
                if (!board.isM_gameinprogress()) return;
            }
        }
    }
}
