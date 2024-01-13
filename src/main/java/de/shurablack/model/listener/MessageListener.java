package de.shurablack.model.listener;

import de.shurablack.core.util.LocalData;
import de.shurablack.mapping.MultiKeyMap;
import de.shurablack.model.manager.DiscordBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {

    private final MultiKeyMap<String,String> mapper = new MultiKeyMap<>();

    public MessageListener() {
        mapper.put(LocalData.getChannelID("music") + "/" + LocalData.getMessageID("player_main"), "player");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (DiscordBot.MUTE) {
            return;
        }

        String message = event.getMessage().getContentDisplay();
        String command = message.split(" ")[0];
        if (event.isFromGuild()) {
            DiscordBot.UTIL.getHandler().onPublicChannelEvent(command, event);
        } else {
            DiscordBot.UTIL.getHandler().onPrivateChannelEvent(command, event);
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (DiscordBot.MUTE) {
            return;
        }

        if (event.isFromGuild()) {
            DiscordBot.UTIL.getHandler().onPublicReactionEvent(mapper.get(event.getChannel().getId() + "/" + event.getMessageId()), event);
        } else {
            DiscordBot.UTIL.getHandler().onPrivateReactionEvent(mapper.get(event.getChannel().getId() + "/" + event.getMessageId()), event);
        }
    }
}
