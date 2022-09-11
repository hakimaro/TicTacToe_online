 package com.company.view;

import com.company.view.button.ButtonItem;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewGame extends GeneralPanel implements ActionListener {
    JButton solo = new ButtonItem("Одиночная игра");
    JButton online = new ButtonItem("Сетевая игра");
    JButton back = new ButtonItem("Выйти");
    JDialog form;

    public NewGame(JFrame frame) {
        super(frame);
        solo.addActionListener(this);
        online.addActionListener(this);
        back.addActionListener(this);

        add(solo);
        add(online);
        add(back);

        frame.setContentPane(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (ButtonItem) e.getSource();
        switch (btn.getText()) {
            case "Одиночная игра":
                this.setVisible(false);
                new Field(super.frame, 5);
                break;
            case "Сетевая игра":
                if (super.createClient()) {
                    solo.setText("Регистрация");
                    online.setText("Авторизация");
                    back.setText("Выйти в меню");
                }
                break;
            case "Выйти":
                this.setVisible(false);
                System.exit(0);
                break;
            case "Регистрация":
                if (form != null) {
                    form.setVisible(false);
                    form = null;
                }
                form = new Authentication(true);
                break;
            case "Авторизация":
                if (form != null) {
                    form.setVisible(false);
                    form = null;
                }
                form = new Authentication(false);
                break;
            case "Выйти в меню":
                if (form != null) form.setVisible(false);
                solo.setText("Одиночная игра");
                online.setText("Сетевая игра");
                back.setText("Выйти");
                break;
        }
    }

    public void tableOfPlayers() {
        form.setVisible(false);
        this.setVisible(false);
        new TableOfPlayers(frame, client);
    }

    public void sendMessage(String message) {
        client.sendMessage(message);
    }

    public String getMessage() {
        return client.getMessage();
    }

    class Authentication extends JDialog implements ActionListener {
        JTextField nickname = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
        ButtonItem ok = new ButtonItem("Продолжить");
        JLabel info = new JLabel("Введите  логин  и  пароль");
        boolean isRegistr;

        Authentication(boolean isRegistr) {
            super(frame);
            setLocation(890,350);
            setSize(235,100);
            setResizable(false);
            setUndecorated(true);
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(nickname);
            panel.add(password);
            panel.add(info);
            panel.add(ok);
            panel.setBackground(new Color(225, 225, 225, 255));
            panel.setBorder(new LineBorder(Color.black, 1));
            ok.addActionListener(this);
            add(panel);
            this.isRegistr = isRegistr;
            setVisible(true);
        }

        public boolean nLet(char let) {
            return ((let < 48 || let > 57) && (let < 65 || let > 90) && (let < 97 || let > 122));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String nick = nickname.getText();
            char[] pswd = password.getPassword();
            if (nick.length() <= 3 || pswd.length <= 3) {
                info.setText("Слишком короткий логин или пароль");
                return;
            }
            String reg = (isRegistr) ? "true" : "false";
            StringBuilder password = new StringBuilder();
            for (int i = 0; i < nick.length(); i++) {
                if (nLet(nick.charAt(i))) {
                    nickname.setText(null);
                    this.password.setText(null);
                    info.setText("Неверный формат ввода");
                    return;
                }
            }
            for (int i = 0; i < pswd.length; i++) {
                    password.append(pswd[i]);
                    if (nLet(pswd[i])) {
                    nickname.setText(null);
                    this.password.setText(null);
                    info.setText("Неверный формат ввода");
                    return;
                }
            }
            sendMessage(nick + " " + password);
            sendMessage(reg);
            ok.setEnabled(false);
            info.setText(getMessage());
            ok.setEnabled(true);
            if (info.getText().equals("Authorization completed")) {
                tableOfPlayers();
            }
        }
    }
}
