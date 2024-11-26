package com.meenigam.Components;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class WaveformPanel extends JPanel {

    private float[] audioSamples;

    public WaveformPanel(File audioFile) {
        try {
            audioSamples = readAudioData(audioFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (audioSamples == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);

        int width = getWidth();
        int height = getHeight();
        int middle = height / 2;

        g2d.fillRect(0, middle - 1, width, 2); // Draw middle line

        for (int i = 0; i < width; i++) {
            // Map samples to screen width
            int sampleIndex = (int) ((i / (float) width) * audioSamples.length);
            int amplitude = (int) (audioSamples[sampleIndex] * middle); // Scale to panel height

            g2d.drawLine(i, middle - amplitude, i, middle + amplitude);
        }
    }

    private float[] readAudioData(File audioFile) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        AudioFormat format = audioInputStream.getFormat();

        if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            throw new UnsupportedAudioFileException("Only PCM signed format supported");
        }

        byte[] audioBytes = audioInputStream.readAllBytes();
        int sampleSizeInBits = format.getSampleSizeInBits();
        int numSamples = audioBytes.length / (sampleSizeInBits / 8);

        float[] audioData = new float[numSamples];

        // Convert bytes to PCM samples
        for (int i = 0; i < numSamples; i++) {
            int sample;
            if (sampleSizeInBits == 16) {
                sample = (audioBytes[i * 2 + 1] << 8) | (audioBytes[i * 2] & 0xFF); // 16-bit PCM
            } else {
                sample = audioBytes[i]; // 8-bit PCM
            }
            audioData[i] = sample / (float) (1 << (sampleSizeInBits - 1)); // Normalize to range [-1, 1]
        }

        return audioData;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Waveform Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);

        File wavFile = new File("path/to/your/audiofile.wav");
        WaveformPanel panel = new WaveformPanel(wavFile);

        frame.add(panel);
        frame.setVisible(true);
    }
}