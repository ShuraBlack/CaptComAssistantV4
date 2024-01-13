package de.shurablack.model.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.shurablack.core.builder.JDAUtil;
import de.shurablack.core.scheduling.Dispatcher;
import de.shurablack.model.manager.DiscordBot;
import de.shurablack.model.manager.MusicManager;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class HeadlessLoadResultHandler implements AudioLoadResultHandler {

    private final MusicManager musicManager;

    public HeadlessLoadResultHandler(MusicManager musicManager) {
        this.musicManager = musicManager;
        checkEmptyChannel();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        musicManager.scheduler.queue(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        for (AudioTrack at : playlist.getTracks()) {
            musicManager.scheduler.queue(at);
        }
    }

    @Override
    public void noMatches() {
    }

    @Override
    public void loadFailed(FriendlyException exception) {
    }

    private void checkEmptyChannel() {
        AudioManager audioManager = JDAUtil.getJDA().getGuildById(DiscordBot.GUILD).getAudioManager();
        VoiceChannel vc = (VoiceChannel) audioManager.getConnectedChannel();
        Runnable task = () -> {
            if (audioManager.isConnected() && (vc == null || vc.getMembers().size() == 1)) {
                MusicManager musicManager = DiscordBot.getMusicManager();
                musicManager.player.stopTrack();
                musicManager.scheduler.clear();
                musicManager.scheduler.clearMessages();
                musicManager.player.setVolume(20);
                musicManager.player.setPaused(false);
                musicManager.scheduler.setRepeatQueue(false);
                musicManager.scheduler.setAutoPlay(false);
                audioManager.closeAudioConnection();
                Dispatcher.descheduleCronTask("PlayerConnectionTask");
            }
        };
        if (!Dispatcher.getTasks().containsKey("PlayerConnectionTask")) {
            Dispatcher.scheduleCronTask("*/10 * * * *","PlayerConnectionTask" ,task);
        }
    }

}
