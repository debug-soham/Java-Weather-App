package com.weatherapp.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A custom JTextField with rounded corners.
 * This component overrides the default painting behavior to draw a rounded
 * rectangle for its background and border.
 */
public class RoundedTextField extends JTextField {
    private Shape shape;
    private final int cornerRadius;

    public RoundedTextField(int cornerRadius) {
        super();
        this.cornerRadius = cornerRadius;
        setOpaque(false); // Make the component transparent
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill the rounded rectangle background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));

        // Let the superclass paint the text
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // We don't paint a border, the background fill is enough.
        // For a custom border, you would draw a rounded rectangle here.
    }

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
        return shape.contains(x, y);
    }
}
