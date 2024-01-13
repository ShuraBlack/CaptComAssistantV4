package de.shurablack.model.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.shurablack.core.builder.JDAUtil;
import de.shurablack.core.scheduling.Dispatcher;
import de.shurablack.model.manager.DiscordBot;
import de.shurablack.model.manager.MusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoadResultHandler implements AudioLoadResultHandler {

    private final MusicManager musicManager;
    private final Member m;
    private final TextChannel channel;
    private final ModalInteractionEvent event;
    private final String value;
    private final boolean ytSearch;

    public LoadResultHandler(MusicManager musicManager, Member m, TextChannel channel, String value, ModalInteractionEvent event, boolean ytSearch) {
        this.musicManager = musicManager;
        this.m = m;
        this.channel = channel;
        this.value = value;
        this.event = event;
        this.ytSearch = ytSearch;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        if(!play(channel.getGuild(), musicManager, track, m, channel)) return;
        EmbedBuilder eb = new EmbedBuilder().setDescription(String.format("Hinzugefügt zur Warteschlange **%s**", track.getInfo().title));
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
        musicManager.scheduler.editQueueMessage();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (ytSearch) {
            if(!play(channel.getGuild(), musicManager, playlist.getTracks().get(0), m, channel)) return;
        } else {
            for (AudioTrack at : playlist.getTracks()) {
                if(!play(channel.getGuild(), musicManager, at, m, channel)) return;
            }
        }
        musicManager.scheduler.editQueueMessage();
        EmbedBuilder eb = new EmbedBuilder().setDescription(String.format("Hinzugefügt zur Warteschlange **%s**", playlist.getName()));
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

    @Override
    public void noMatches() {
        EmbedBuilder eb = new EmbedBuilder().setDescription(String.format("Kein Ergebnis für **%s**", value));
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

    @Override
    public void loadFailed(FriendlyException e) {
        EmbedBuilder eb = new EmbedBuilder().setDescription(String.format("Konnte **%s** nicht abspielen", value))
                .addField("Error:",e.getMessage(),false);
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

    private Boolean play(Guild guild, MusicManager musicManager, AudioTrack track, Member m, TextChannel channel) {
        if (connectToVoiceChannel(guild.getAudioManager(), m, channel)) {
            musicManager.scheduler.queue(track);
            return true;
        }
        return false;
    }

    private static Boolean connectToVoiceChannel(AudioManager audioManager, Member m, TextChannel channel) {
        if (!audioManager.isConnected()) {
            VoiceChannel vc = (VoiceChannel) Objects.requireNonNull(m.getVoiceState()).getChannel();
            if (vc != null) {
                Runnable task = () -> {
                    if (audioManager.isConnected() && vc.getMembers().size() == 1) {
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
                audioManager.openAudioConnection(vc);
                return true;
            } else {
                EmbedBuilder eb = new EmbedBuilder().setDescription(String.format("%s, verbinde dich zuvor mit einem VoiceChannel", m.getAsMention()));
                channel.sendMessageEmbeds(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                return false;
            }
        } else {
            VoiceChannel vc = (VoiceChannel) Objects.requireNonNull(m.getVoiceState()).getChannel();
            if (vc != null) {
                if (vc.getMembers().stream().map(ISnowflake::getId)
                        .noneMatch(id -> id.equals(JDAUtil.getJDA().getSelfUser().getId()))) {
                    EmbedBuilder eb = new EmbedBuilder().setDescription(String.format("%s, verbinde dich zuvor mit einem aktiven VoiceChannel", m.getAsMention()));
                    channel.sendMessageEmbeds(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                    return false;
                }
                return true;
            } else {
                EmbedBuilder eb = new EmbedBuilder().setDescription(String.format("%s, verbinde dich zuvor mit einem aktiven VoiceChannel", m.getAsMention()));
                channel.sendMessageEmbeds(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                return false;
            }
        }
    }
}
