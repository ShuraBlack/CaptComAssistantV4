package de.shurablack.model.listener;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.shurablack.core.scheduling.Dispatcher;
import de.shurablack.core.util.LocalData;
import de.shurablack.core.util.ServerUtil;
import de.shurablack.model.service.task.RulerReenableUserTask;
import de.shurablack.model.service.task.SubReenableUserTask;
import de.shurablack.model.service.task.UpdateStatsTask;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class DefaultListener extends ListenerAdapter {

    private static final Logger LOGGER = LogManager.getLogger(DefaultListener.class);

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        User u = event.getUser();
        ServerUtil.sendWebHookMessage(LocalData.getWebHookLink("lobby")
                ,createMessage(
                        u.getAsTag(),
                        u.getEffectiveAvatarUrl(),
                        u.getAsMention() + ", wurde des Servers verwiesen!",
                        ServerUtil.RED
                ));
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        User u = event.getUser();
        ServerUtil.sendWebHookMessage(LocalData.getWebHookLink("lobby")
                ,createMessage(
                        u.getAsTag(),
                        u.getEffectiveAvatarUrl(),
                        u.getAsMention() + ", wurde eine zweite Chance ermöglicht!",
                        ServerUtil.BLUE
                ));
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        User u = event.getUser();
        ServerUtil.sendWebHookMessage(LocalData.getWebHookLink("lobby")
                ,createMessage(
                        u.getAsTag(),
                        u.getEffectiveAvatarUrl(),
                        joinMessage(u.getAsMention()),
                        ServerUtil.GREEN
                ));
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        User u = event.getUser();
        ServerUtil.sendWebHookMessage(LocalData.getWebHookLink("lobby")
                ,createMessage(
                        u.getAsTag(),
                        u.getEffectiveAvatarUrl(),
                        removeMessage(u.getAsMention()),
                        ServerUtil.RED
                ));
    }

    private WebhookEmbedBuilder createMessage(String tag, String avatar, String text, int color) {
        return new WebhookEmbedBuilder()
                .setTitle(new WebhookEmbed.EmbedTitle(tag,null))
                .setThumbnailUrl(avatar)
                .setDescription(text)
                .setColor(color);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.debug("JDA announced to be ready");
        scheduleTasks();
    }

    private void scheduleTasks() {
        Dispatcher.scheduleCronTask("0 1 * * 1-7","UpdateStatsTask" ,new UpdateStatsTask(LOGGER));
        Dispatcher.scheduleCronTask("0 2 * * 1-7", "RulerReenableUserTask", new RulerReenableUserTask(LOGGER));
        Dispatcher.scheduleCronTask("55 1 * * 1-7", "SubReenableUserTask", new SubReenableUserTask(LOGGER));
    }

    private String joinMessage(String mention) {
        String[] messages = {
                "{n} trat dem Server bei - glhf",
                "{n}, wir hoffen du hast Pizza dabei",
                "{n}, lass deine Waffen hier",
                "{n} erschien. Seems OP GGEZ - please nerf"
        };
        return messages[ThreadLocalRandom.current().nextInt(0, messages.length)].replace("{n}",mention);
    }

    private String removeMessage(String name) {
        String[] messages = {
                "{n}. Mission failed. We will get them next time",
                "{n}. Another one bits the dust",
                "{n} auf wiedersehen",
                "{n}. Got nerfed and removed"
        };
        return messages[ThreadLocalRandom.current().nextInt(0, messages.length)].replace("{n}",name);
    }
}
