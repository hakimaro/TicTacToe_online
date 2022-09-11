package com.company.view;

import com.company.model.online.Client;
import com.company.view.layout.MenuLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Класс, который наследуют все панели
 */

public class GeneralPanel extends JPanel {
    JFrame frame;
    Client client;
    File file = new File("./src/resources", "background.jpg");

    GeneralPanel(JFrame frame) {
        this.frame = frame;
        setLayout(new MenuLayout());
    }

    GeneralPanel(JFrame frame, Client client) {
        this.frame = frame;
        this.client = client;
    }

    public boolean createClient() {
        try {
            client = new Client();
        } catch (IOException e) {
            System.out.println("Ошибка присоединения к серверу!");
            return false;
        }
        return true;
    }

    protected void paintComponent(Graphics g) {
        try {
            BufferedImage image = ImageIO.read(file);
            g.drawImage(image, 0, 0, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
