package com.company.view.button;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Square extends JButton {
    int i;
    int j;
    int player = 0;

    public Square(int i, int j) {
        super();
        this.i = i;
        this.j = j;
        setContentAreaFilled(false);
        setBorder(new LineBorder(new Color(0,0,0), 2));
    }

    public void paintX(int x) {
        player = x;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(5));
        if (player == 1) {
            g2d.drawLine(0,0,getWidth(),getHeight());
            g2d.drawLine(0,getHeight(),getWidth(),0);
        } else if (player == -1) {
            g2d.drawOval(0,0,getWidth(),getHeight());
        }
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }
}
