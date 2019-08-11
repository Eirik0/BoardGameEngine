package bge.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import bge.main.BoardGameEngineMain;

public interface DrawingMethods {
    default int round(double d) {
        return (int) Math.round(d);
    }

    default void drawRect(Graphics2D g, double x, double y, double width, double height, Color color) {
        g.setColor(color);
        g.drawRect(round(x), round(y), round(width), round(height));
    }

    default void fillRect(Graphics2D g, double x, double y, double width, double height, Color color) {
        g.setColor(color);
        g.fillRect(round(x), round(y), round(width), round(height));
    }

    static void fillRectS(Graphics2D g, double x, double y, double width, double height) {
        g.fillRect(roundS(x), roundS(y), roundS(width), roundS(height));
    }

    default void drawCenteredString(Graphics2D g, String text, double x, double y) {
        drawCenteredString(g, BoardGameEngineMain.DEFAULT_FONT, text, x, y);
    }

    default void drawCenteredString(Graphics2D g, Font font, String text, double x, double y) {
        g.setFont(font);
        Rectangle glyphVector = g.getFont().createGlyphVector(g.getFontRenderContext(), text).getPixelBounds(null, 0, 0);
        double width = glyphVector.getWidth();
        double height = glyphVector.getHeight();
        g.drawString(text, round(x - width / 2), round(y + height / 2));
    }

    static void drawCenteredStringS(Graphics2D g, String text, double x, double y) {
        Rectangle glyphVector = g.getFont().createGlyphVector(g.getFontRenderContext(), text).getPixelBounds(null, 0, 0);
        double width = glyphVector.getWidth();
        double height = glyphVector.getHeight();
        g.drawString(text, roundS(x - width / 2), roundS(y + height / 2));
    }

    default void drawCenteredYString(Graphics2D g, String text, double x, double y) {
        Rectangle glyphVector = g.getFont().createGlyphVector(g.getFontRenderContext(), text).getPixelBounds(null, 0, 0);
        double height = glyphVector.getHeight();
        g.drawString(text, round(x), round(y + height / 2));
    }

    default void drawCircle(Graphics2D g, double x, double y, double radius) {
        double height = 2 * radius;
        g.drawOval(round(x - radius), round(y - radius), round(height), round(height));
    }

    default void fillCircle(Graphics2D g, double x, double y, double radius) {
        double height = 2 * radius;
        g.fillOval(round(x - radius), round(y - radius), round(height), round(height));
    }

    static void fillCircleS(Graphics2D g, double x, double y, double radius) {
        double height = 2 * radius;
        g.fillOval(roundS(x - radius), roundS(y - radius), roundS(height), roundS(height));
    }

    default void drawThickLine(Graphics2D g, double x0, double y0, double x1, double y1, float thickness, boolean round) {
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(thickness, round ? BasicStroke.CAP_ROUND : BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 0));
        g.drawLine(round(x0), round(y0), round(x1), round(y1));
        g.setStroke(oldStroke);
    }

    default Color decayToColor(Color mainColor, double percent) {
        double red = percent * 255 + (1 - percent) * mainColor.getRed();
        double green = percent * 255 + (1 - percent) * mainColor.getGreen();
        double blue = percent * 255 + (1 - percent) * mainColor.getBlue();
        return new Color((int) red, (int) green, (int) blue);
    }

    static int roundS(double d) {
        return (int) Math.round(d);
    }
}
