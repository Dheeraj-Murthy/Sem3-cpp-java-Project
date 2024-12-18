package com.meenigam.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Clip extends JPanel {
    private final FileComponent fileComponent;
    private float start;
    private float end;
    private float size;
    private WaveformPanel waveformPanel;
    private Track track;
    private final Clip thisClip;

    public Clip(FileComponent file, Track track) {
        this.thisClip = this;
        this.fileComponent = file;
        this.track = track;
        this.waveformPanel = new WaveformPanel(file.getFile(), this);
        this.waveformPanel.setSize(getWidth(), getHeight());
        setLayout(new BorderLayout());
        add(waveformPanel, BorderLayout.CENTER);

        System.out.println(file.getDuration());
        setBackground(new Color(100, 100, 0)); // Visual indicator of a clip
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setPreferredSize(new Dimension((int) file.getDuration() * 10, track.getClipContainer().getHeight())); // Adjust as needed
        setOpaque(true);
        this.size = file.getDuration();
        this.start = 0;
        this.end = size;
        // Listen for changes in the parent's size
        track.getClipContainer().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateHeight();
                setSize((int) file.getDuration() * 10, getHeight());
            }
        });

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
                if (isOutOfBounds(newLocation)) {
                    if (isLeft(newLocation)) {
                        setLocation(new Point(0, location.y));
                    } else if (isRight(newLocation)) {
                        setLocation(new Point(track.getClipContainer().getWidth() - (int) size * 10, location.y));
                    } else {
                        setLocation(new Point(location.x, location.y));
                    }
                    return;
                }
                setPos(location.x * 10);
                System.out.println("current location " + location.x);
                setLocation(newLocation);
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public String getPath() { return this.fileComponent.getFilePath(); }
    public float getStart() { return (this.start *10); }
    public float getEnd() { return this.end; }

    private void updateHeight() {
        int newHeight = track.getClipContainer().getHeight();
        setPreferredSize(new Dimension(getPreferredSize().width, newHeight));
        revalidate(); // Notify Swing to update the layout
    }

    private boolean isOutOfBounds(Point location) {
        int clipLeft = location.x;  // Left edge of the clip
        int clipRight = clipLeft + (int) (size * 10);  // Right edge of the clip, based on size and scale

        // Check if the clip is out of bounds on the left or right
        return clipLeft < -1 || clipRight > track.getWidth()+1;
    }

    private boolean isLeft(Point location) {
        int clipLeft = location.x;
        return clipLeft < 0;
    }

    private boolean isRight(Point location) {
        int clipRight = location.x + (int) (size * 10);  // Right edge of the clip, based on size and scale
        return clipRight > track.getWidth();
    }

    private void setPos(float loco) {
        this.start = loco;
        this.end = start + size;
    }

    // Additional methods for interacting with the fileComponent
    public FileComponent getFileComponent() {
        return fileComponent;
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.size = fileComponent.getDuration();
        this.end = this.start + this.size;
        super.paintComponent(g);
    }

    public void reset() {
        // Set the new size based on the file component's duration
        this.fileComponent.resetFile();
        this.size = fileComponent.getDuration();
        this.start = 0;
        this.end = size;

        // Update the preferred size of the clip based on the new file component's duration
        this.setPreferredSize(new Dimension((int) (fileComponent.getDuration() * 10), track.getClipContainer().getHeight()));

        // Resize the waveform panel accordingly
        waveformPanel.setSize(getWidth(), getHeight()); // Update the size of the waveform panel

        // Revalidate and repaint the Clip to ensure it gets updated in the layout
        revalidate();
        repaint();

        // Also notify the parent container to update its layout
        getParent().revalidate();
        getParent().repaint();
        waveformPanel.revalidate();
        waveformPanel.repaint();
    }
}