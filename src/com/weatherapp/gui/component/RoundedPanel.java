package com.weatherapp.gui.component;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JPanel with rounded corners.
 * This class overrides the paintComponent method to draw a rounded rectangle
 * as its background instead of a standard sharp-edged rectangle.
 */
public class RoundedPanel extends JPanel {
    /**
     * The corner radius for the rounded corners.
     * A higher value creates a more rounded corner.
     */
    private int cornerRadius;

    /**
     * Constructs a new RoundedPanel with a specified corner radius and layout manager.
     * @param layout The LayoutManager to use for this panel.
     * @param radius The radius of the rounded corners.
     */
    public RoundedPanel(LayoutManager layout, int radius) {
        super(layout);
        this.cornerRadius = radius;
        // This is important to ensure that the panel is not opaque, which allows the rounded corners to be drawn correctly.
        setOpaque(false);
    }

    /**
     * Overrides the default paintComponent method to draw the custom rounded background.
     * @param g The Graphics object to protect.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;

        // Enable anti-aliasing for smoother corners
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draws the rounded panel with a specified color.
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);

        // (Optional) uncomment the following lines to draw a border around the panel
        // graphics.setColor(getForeground());
        // graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
    }
}
