package com.chess.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Procedural sound synthesizer that generates clean tactile audio effects
 * using the Java Sound API without requiring external audio files.
 */
public class SoundManager {
    private static final int SAMPLE_RATE = 44100;
    private final ExecutorService audioExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "Chess-Audio-Thread");
        t.setDaemon(true);
        return t;
    });

    private boolean soundEnabled = true;

    public SoundManager() {}

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public void playMoveSound() {
        if (!soundEnabled) return;
        audioExecutor.execute(() -> {
            // Soft tactile wooden knock (sine burst with rapid decay)
            int durationMs = 45;
            int numSamples = (SAMPLE_RATE * durationMs) / 1000;
            byte[] buffer = new byte[numSamples];
            for (int i = 0; i < numSamples; i++) {
                double t = (double) i / SAMPLE_RATE;
                double decay = Math.exp(-t * 90.0);
                double freq = 320.0 - (t * 800.0);
                double sample = Math.sin(2 * Math.PI * Math.max(freq, 100.0) * t) * decay;
                buffer[i] = (byte) (sample * 110.0);
            }
            playBuffer(buffer);
        });
    }

    public void playCaptureSound() {
        if (!soundEnabled) return;
        audioExecutor.execute(() -> {
            // Punchy deeper thud with wood snap
            int durationMs = 70;
            int numSamples = (SAMPLE_RATE * durationMs) / 1000;
            byte[] buffer = new byte[numSamples];
            for (int i = 0; i < numSamples; i++) {
                double t = (double) i / SAMPLE_RATE;
                double decay = Math.exp(-t * 60.0);
                double freq = 220.0 - (t * 900.0);
                double noise = (Math.random() * 2.0 - 1.0) * Math.exp(-t * 180.0) * 0.3;
                double sample = (Math.sin(2 * Math.PI * Math.max(freq, 60.0) * t) * 0.7 + noise) * decay;
                buffer[i] = (byte) (sample * 125.0);
            }
            playBuffer(buffer);
        });
    }

    public void playCheckSound() {
        if (!soundEnabled) return;
        audioExecutor.execute(() -> {
            // Clear warning chime (dual harmonized tones)
            int durationMs = 180;
            int numSamples = (SAMPLE_RATE * durationMs) / 1000;
            byte[] buffer = new byte[numSamples];
            for (int i = 0; i < numSamples; i++) {
                double t = (double) i / SAMPLE_RATE;
                double decay = Math.exp(-t * 18.0);
                double tone1 = Math.sin(2 * Math.PI * 587.33 * t); // D5
                double tone2 = Math.sin(2 * Math.PI * 880.00 * t); // A5
                double sample = ((tone1 * 0.6) + (tone2 * 0.4)) * decay;
                buffer[i] = (byte) (sample * 105.0);
            }
            playBuffer(buffer);
        });
    }

    public void playCastleSound() {
        if (!soundEnabled) return;
        audioExecutor.execute(() -> {
            // Two quick clicks representing king & rook sliding
            int durationMs = 160;
            int numSamples = (SAMPLE_RATE * durationMs) / 1000;
            byte[] buffer = new byte[numSamples];
            for (int i = 0; i < numSamples; i++) {
                double t = (double) i / SAMPLE_RATE;
                double sample = 0;
                // First click at t = 0
                if (t < 0.05) {
                    double d1 = Math.exp(-t * 120.0);
                    sample += Math.sin(2 * Math.PI * 340.0 * t) * d1;
                }
                // Second click at t = 0.08
                if (t >= 0.08 && t < 0.13) {
                    double t2 = t - 0.08;
                    double d2 = Math.exp(-t2 * 120.0);
                    sample += Math.sin(2 * Math.PI * 420.0 * t2) * d2;
                }
                buffer[i] = (byte) (sample * 115.0);
            }
            playBuffer(buffer);
        });
    }

    public void playGameOverSound() {
        if (!soundEnabled) return;
        audioExecutor.execute(() -> {
            // Victory/Defeat chord fanfare
            int durationMs = 450;
            int numSamples = (SAMPLE_RATE * durationMs) / 1000;
            byte[] buffer = new byte[numSamples];
            for (int i = 0; i < numSamples; i++) {
                double t = (double) i / SAMPLE_RATE;
                double decay = Math.exp(-t * 7.0);
                double chord = (Math.sin(2 * Math.PI * 440.00 * t) +
                                Math.sin(2 * Math.PI * 554.37 * t) +
                                Math.sin(2 * Math.PI * 659.25 * t)) / 3.0;
                buffer[i] = (byte) (chord * decay * 110.0);
            }
            playBuffer(buffer);
        });
    }

    private void playBuffer(byte[] buffer) {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format);
            line.start();
            line.write(buffer, 0, buffer.length);
            line.drain();
        } catch (LineUnavailableException | IllegalArgumentException ignored) {
            // Audio hardware unavailable, gracefully proceed silently
        }
    }
}
