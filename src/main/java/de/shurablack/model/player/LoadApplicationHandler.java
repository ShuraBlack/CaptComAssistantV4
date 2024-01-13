package de.shurablack.model.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.shurablack.model.manager.MusicManager;

public class LoadApplicationHandler implements AudioLoadResultHandler {

    private final MusicManager manager;

    public LoadApplicationHandler(MusicManager manager) {
        this.manager = manager;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        manager.scheduler.queue(track);
        manager.scheduler.editQueueMessage();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        for (AudioTrack track : playlist.getTracks()) {
            manager.scheduler.queue(track);
        }
        manager.scheduler.editQueueMessage();
    }

    @Override
    public void noMatches() {
    }

    @Override
    public void loadFailed(FriendlyException e) {
    }
}
