package de.shurablack.model.manager;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import de.shurablack.model.player.AudioPlayerSendHandler;
import de.shurablack.model.player.TrackScheduler;

import java.util.function.Consumer;

public class MusicManager {

    public static final float[] LINEAR = {
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f
    };

    public static final float[] BASS = {
            0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f
    };

    public static final float[] MID = {
            -0.15f, -0.10f, -0.05f, 0.00f, 0.02f, 0.02f, 0.02f, 0.05f, 0.02f, 0.02f, 0.2f, 0.5f, 0.05f, 0.1f, 0.1f
    };

    public static final float[] TREBLE = {
            -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.05f, 0.0f, 0.05f, 0.1f, 0.15f, 0.2f
    };

    /**
     * Audio player for the guild.
     */
    public final AudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    public final TrackScheduler scheduler;
    /**
     * Equalizer manipulating sound
     */
    public final EqualizerFactory equalizer;

    /**
     * Creates a player and a track scheduler.
     * @param manager Audio player manager to use for creating the player.
     */
    public MusicManager(AudioPlayerManager manager) {
        player = manager.createPlayer();
        player.setVolume(20);
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
        equalizer = new EqualizerFactory();
        player.setFilterFactory(equalizer);
        player.setFrameBufferDuration(500);
    }

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }

    public static Consumer<Float> applyEq(float[] preset, MusicManager manager) {
        return (percentage) -> {
            final float multiplier = percentage / 100.00f;
            for (int i = 0; i < preset.length; i++) {
                manager.equalizer.setGain(i, preset[i] * multiplier);
            }
        };
    }
}
