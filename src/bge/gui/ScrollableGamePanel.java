package bge.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import bge.main.BoardGameEngineMain;

@SuppressWarnings("serial")
public class ScrollableGamePanel extends JPanel implements Scrollable {
    private final JViewport viewport;
    private Sizable sizable;
    private final GamePanelController controller;

    public ScrollableGamePanel(JViewport viewport, Sizable sizable, Consumer<Graphics2D> drawFunction) {
        setBackground(BoardGameEngineMain.BACKGROUND_COLOR);
        controller = new GamePanelController(drawFunction);
        this.viewport = viewport;
        this.sizable = sizable;
        viewport.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                checkResized();
            }
        });
    }

    public void setSizable(Sizable sizable) {
        this.sizable = sizable;
    }

    public void checkResized() {
        SwingUtilities.invokeLater(() -> {
            sizable.checkResized(viewport.getWidth(), viewport.getHeight());
            controller.gameImage.checkResized(sizable.getWidth(), sizable.getHeight());
            Dimension newPreferredSize = new Dimension(sizable.getWidth(), sizable.getHeight());
            if (!getPreferredSize().equals(newPreferredSize)) {
                setPreferredSize(newPreferredSize);
                viewport.revalidate();
            }
        });
    }

    public void addToGameLoop(String name) {
        controller.addToGameLoop(name, this::repaint);
    }

    public void removeFromGameLoop(String name) {
        controller.removeFromGameLoop(name);
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        controller.drawOn(g);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
        return BoardGameEngineMain.DEFAULT_SMALL_FONT_HEIGHT;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
        return BoardGameEngineMain.DEFAULT_SMALL_FONT_HEIGHT;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
