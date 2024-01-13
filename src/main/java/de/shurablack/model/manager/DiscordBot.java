package de.shurablack.model.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import de.shurablack.core.builder.CommandAction;
import de.shurablack.core.builder.JDAUtil;
import de.shurablack.core.builder.UtilBuilder;
import de.shurablack.core.event.EventHandler;
import de.shurablack.core.event.interaction.Interaction;
import de.shurablack.core.event.interaction.InteractionSet;
import de.shurablack.core.event.interaction.Type;
import de.shurablack.core.util.Config;
import de.shurablack.model.listener.*;
import de.shurablack.model.worker.*;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.http.client.config.RequestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

import static de.shurablack.core.util.LocalData.getChannelID;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class DiscordBot {

    private static final Logger LOGGER = LogManager.getLogger(DiscordBot.class);

    public static JDAUtil UTIL;

    public static boolean MUTE = false;

    public static final String GUILD = "286628427140825088";

    // AudioPlayer
    private static AudioPlayerManager PLAYER_MANAGER;
    private static MusicManager MUSIC_MANAGER;

    public static void main(String[] args) {
        UtilBuilder.init();

        if (Config.getConfig("youtube_api_token") == null
                || Config.getConfig("youtube_email") == null
                || Config.getConfig("youtube_password") == null) {
            LOGGER.error("Missing youtube properties. This can lead to errors ...");
        }

        PLAYER_MANAGER = new DefaultAudioPlayerManager();
        PLAYER_MANAGER.getConfiguration().setFilterHotSwapEnabled(true);
        PLAYER_MANAGER.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager());
        PLAYER_MANAGER.registerSourceManager(new TwitchStreamAudioSourceManager());
        PLAYER_MANAGER.setHttpRequestConfigurator((config) -> RequestConfig.copy(config).setConnectTimeout(5000).build());
        MUSIC_MANAGER = new MusicManager(PLAYER_MANAGER);


        new DiscordBot();
    }

    public DiscordBot() {
        UTIL = UtilBuilder
                .create(createJDA(), createHandler())
                .addDatabase()
                .setCommandLineAction(
                        new CommandAction(
                                "upload slash",
                                "Upload slash commands",
                                (input) -> uploadSlashCommands()
                        ),
                        new CommandAction(
                                "mute",
                                "<on/off> Un/mute listeners",
                                (input) -> {
                                    if (input.equals("mute on")) {
                                        MUTE = true;
                                        LOGGER.info("Bot got muted");
                                    }

                                    if (input.equals("mute off")) {
                                        MUTE = false;
                                        LOGGER.info("Bot got unmuted");
                                    }
                                }
                        )
                )
                .build();
    }

    private EventHandler createHandler() {
        return EventHandler.create(EventHandler.DEFAULT_PREFIX,true)
                .registerEvent(InteractionSet.fromJson("interactionset.json"));
    }

    private JDABuilder createJDA() {
        return JDABuilder.createDefault(Config.getConfig("access_token"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT)
                .disableIntents(getDisabledIntents())
                .disableCache(getDisabledCacheFlags())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setLargeThreshold(50)
                .addEventListeners(new DefaultListener())
                .addEventListeners(new MessageListener())
                .addEventListeners(new VoiceChannelListener())
                .addEventListeners(new SlashListener())
                .addEventListeners(new ActiveListener())
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("The CaptCom Server"));
    }

    private List<GatewayIntent> getDisabledIntents () {
        List<GatewayIntent> list = new LinkedList<>();
        list.add(GatewayIntent.DIRECT_MESSAGE_REACTIONS);
        list.add(GatewayIntent.DIRECT_MESSAGE_TYPING);
        list.add(GatewayIntent.GUILD_PRESENCES);
        return list;
    }

    private List<CacheFlag> getDisabledCacheFlags () {
        List<CacheFlag> list = new LinkedList<>();
        list.add(CacheFlag.ACTIVITY);
        list.add(CacheFlag.CLIENT_STATUS);
        return list;
    }

    public static AudioPlayerManager getAudioPlayer() {
        return PLAYER_MANAGER;
    }

    public static MusicManager getMusicManager() {
        JDAUtil.getJDA().getGuildById(GUILD).getAudioManager().setSendingHandler(MUSIC_MANAGER.getSendHandler());
        return MUSIC_MANAGER;
    }

    private void uploadSlashCommands() {
        LOGGER.info("Try to upload new Slash command set ...");
        CommandListUpdateAction commands = JDAUtil.getJDA().updateCommands();
        commands.addCommands().queue();

        JDAUtil.getJDA().updateCommands().addCommands(
                Commands.slash("coinflip","Wirft eine Münze für dich"),
                Commands.slash("dice","Wirft Würfel für dich")
                        .addOptions(new OptionData(INTEGER,"dices","Anzahl an Würfel die geworfen werden").setRequired(false))
                        .addOptions(new OptionData(INTEGER,"eyes","Anzahl an Augen der Würfels").setRequired(false)),
                Commands.slash("badge", "Global Slash Interaction für Badge")
        ).queue();

        Guild guild = JDAUtil.getJDA().getGuildById("286628427140825088");

        guild.updateCommands().addCommands(
                // Commands for MusicPlayer
                Commands.slash("player","Befehle für die Interaktion mit dem MusicPlayer").addSubcommands(
                        new SubcommandData("volume", "Verändert die Lautstärke")
                                .addOptions(new OptionData(INTEGER,"value","Lautstärke zwischen 0-100").setRequired(true).setMinValue(0).setMaxValue(100)),
                        new SubcommandData("remove", "Entfernt Track mit der angegebenen Nummer")
                                .addOptions(new OptionData(INTEGER,"tracknumber","Die Nummer des Tracks").setRequired(true).setMinValue(1)),
                        new SubcommandData("start", "Startet Track mit der angegebenen Nummer")
                                .addOptions(new OptionData(INTEGER,"tracknumber","Die Nummer des Tracks").setRequired(true).setMinValue(1)),
                        new SubcommandData("pop","Nächster Track ist die angegebene Nummer")
                                .addOptions(new OptionData(INTEGER,"tracknumber","Die Nummer des Tracks").setRequired(true).setMinValue(1)),
                        new SubcommandData("position","Setzt Zeit des Tracks auf Position")
                                .addOptions(new OptionData(INTEGER,"hour","Stundenziffer").setRequired(true).setMinValue(0))
                                .addOptions(new OptionData(INTEGER,"minute","Minutenziffer (0-59)").setRequired(true).setMaxValue(59).setMinValue(0))
                                .addOptions(new OptionData(INTEGER,"second","Sekundenziffer (0-59)").setRequired(true).setMaxValue(59).setMinValue(0)),
                        new SubcommandData("equalizer","Setzt den Equilizer des Musik Players")
                                .addOptions(new OptionData(STRING,"preset","Vorauswahl an Einstellungen").setRequired(true)
                                        .addChoice("Bass","bass")
                                        .addChoice("Mitte","mid")
                                        .addChoice("Höhen","treble")
                                        .addChoice("Linear","linear"))
                                .addOptions(new OptionData(INTEGER,"percentage","Stärke des Presets (0-200%)")
                                        .setRequired(true).setMinValue(0).setMaxValue(200))
                ),
                Commands.slash("info","Zeigt Info über ausgewählte Entität an")
                        .addOptions(new OptionData(STRING,"entity","Die zur Auswahl stehenden Entitäten")
                                .addChoice("self","self")
                                .addChoice("bot","bot"))
        ).queue();
        LOGGER.info("Successfully uploaded Slash command set. The Commands will be activated within an hour");
    }
}