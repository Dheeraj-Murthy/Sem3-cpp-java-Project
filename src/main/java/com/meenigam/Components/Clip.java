package com.meenigam.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Clip extends JPanel {
    private final FileComponent fileComponent;
    private float start;
    private float end;
    private final float size;
    private WaveformPanel waveformPanel;
    private Track track;

    public Clip(FileComponent file, Track track) {
        this.fileComponent = file;
        this.track = track;
        this.waveformPanel = new WaveformPanel(file.getFile());
        this.waveformPanel.setSize(getWidth(), getHeight());
        setLayout(new BorderLayout());
        add(waveformPanel, BorderLayout.CENTER);

        System.out.println(file.getDuration());
        setBackground(new Color(100, 100, 100)); // Visual indicator of a clip
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension((int) file.getDuration() * 10, 100)); // Adjust as needed
        this.size = file.getDuration();
        this.start = 0;
        this.end = size;


        // Make it draggable
        MouseAdapter mouseHandler = new MouseAdapter() {
            private Point offset;

            @Override
            public void mousePressed(MouseEvent e) {
                offset = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = getLocation();
                Point newLocation = new Point(location);
                newLocation.translate(e.getX() - offset.x, 0);
                if (isOutOfBounds(newLocation))
                {
                    setLocation(location);
                    return;
                }

                Rectangle oldBounds = getBounds();
                setLocation(newLocation);
                Rectangle newBounds = getBounds();

                getParent().repaint(oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height);
                getParent().repaint(newBounds.x, newBounds.y, newBounds.width, newBounds.height);
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private boolean isOutOfBounds(Point location) {
        int clipLeft = location.x;  // Left edge of the clip
        int clipRight = clipLeft + (int) (size * 10);  // Right edge of the clip, based on size and scale

        // Check if the clip is out of bounds on the left or right
        return clipLeft < 0 || clipRight > track.getWidth();
    }

    private void setPos(int loco) {
        this.start = loco;
        this.end = start + size;
    }

    // Additional methods for interacting with the fileComponent
    public FileComponent getFileComponent() {
        return fileComponent;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

}