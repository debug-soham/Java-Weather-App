package com.weatherapp.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A custom JTextField with rounded corners.
 * This component overrides its painting behavior to match the modern, rounded UI theme.
 */
public class RoundedTextField extends JTextField {
    private Shape shape;
    private final int cornerRadius;

    public RoundedTextField(int cornerRadius) {
        super();
        this.cornerRadius = cornerRadius;
        // Make the component transparent to allow for custom background drawing.
        setOpaque(false);
    }

    /**
     * Overrides the default component painting. This method first draws the
     * rounded background and then calls the superclass's paintComponent
     * to draw the text on top of it.
     * @param g The Graphics object provided by Swing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill the background with a rounded rectangle shape.
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));

        // Let the original JTextField class handle painting the text.
        super.paintComponent(g2);
        g2.dispose();
    }

    /**
     * Overrides the border painting to prevent Swing from drawing a default
     * rectangular border around the text field.
     * @param g The Graphics object.
     */
    @Override
    protected void paintBorder(Graphics g) {
        // No border is painted to maintain the clean, rounded look.
    }

    /**
     * Overrides contains() to define the component's clickable area. This ensures
     * that mouse events only register within the rounded shape, not in the corners.
     */
    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
        return shape.contains(x, y);
    }
}
