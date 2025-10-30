package com.weatherapp.gui.component;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JPanel that renders with rounded corners.
 * This class overrides the default painting behavior to create a modern, rounded look.
 */
public class RoundedPanel extends JPanel {
    private final int cornerRadius;

    /**
     * Constructs a new RoundedPanel.
     * @param layout The LayoutManager to use.
     * @param radius The radius of the corners in pixels.
     */
    public RoundedPanel(LayoutManager layout, int radius) {
        super(layout);
        this.cornerRadius = radius;
        // setOpaque(false) is critical for custom painting. It allows the
        // panel to be transparent so that we can draw our own rounded background.
        setOpaque(false);
    }

    /**
     * Overrides the default painting behavior to draw a rounded rectangle.
     * This is where the custom "rounded" look is created.
     * @param g The Graphics object provided by Swing for drawing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;

        // Enable anti-aliasing for smooth, high-quality corners.
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the rounded rectangle background with the panel's background color.
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
    }
}
