package com.company.view.layout;

import java.awt.*;

public class MenuLayout implements LayoutManager {
    private final Dimension size = new Dimension();
    @Override
    public void addLayoutComponent(String name, Component comp) {

    }

    @Override
    public void removeLayoutComponent(Component comp) {

    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return size;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return size;
    }

    @Override
    public void layoutContainer(Container container) {
        Component[] list = container.getComponents();
        int currentY = 800/(2*list.length);
        for (Component component : list) {
            component.setPreferredSize(new Dimension(400,80));
            Dimension pref = component.getPreferredSize();
            component.setBounds(400, currentY, pref.width, pref.height);
            currentY += 800/(2*list.length);
            currentY += pref.height;
        }
    }
}
