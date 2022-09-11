package com.company.view.button;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;


public class ButtonItem extends JButton {
    public ButtonItem(String name) {
        super(name);
        setContentAreaFilled(true);
        setBackground(new Color(218, 210, 210));
        setBorder(new LineBorder(Color.black, 2));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
