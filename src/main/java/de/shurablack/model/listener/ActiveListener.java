package de.shurablack.model.listener;

import de.shurablack.model.database.Statement;
import de.shurablack.model.database.models.UserTimeModel;
import de.shurablack.sql.SQLRequest;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ActiveListener extends ListenerAdapter {

    private final Map<String, LocalDateTime> joined = new HashMap<>();

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() != null && event.getChannelLeft() == null) {
            if (event.getMember().getId().equals(event.getJDA().getSelfUser().getId())) {
                return;
            }
            if (this.joined.containsKey(event.getMember().getId())) {
                return;
            }
            this.joined.put(event.getMember().getId(), LocalDateTime.now());
        } else if (event.getChannelJoined() == null && event.getChannelLeft() != null) {
            if (event.getMember().getId().equals(event.getJDA().getSelfUser().getId())) {
                return;
            }
            if (!this.joined.containsKey(event.getMember().getId())) {
                return;
            }
            LocalDateTime start = this.joined.remove(event.getMember().getId());
            LocalDateTime now = LocalDateTime.now();

            Duration timeInChanel = Duration.between(start,now);

            SQLRequest.Result<UserTimeModel> entry = SQLRequest.runSingle(
                    Statement.SELECT_USER_TIME(event.getMember().getId()), UserTimeModel.class);

            if (entry.isPresent()) {
                SQLRequest.run(Statement.UPDATE_USER_TIME(event.getMember().getId(),timeInChanel.toSeconds()));
            } else {
                SQLRequest.run(Statement.INSERT_USER_TIME(event.getMember().getId(),timeInChanel.toSeconds()));
            }
        }

        super.onGuildVoiceUpdate(event);
    }
}
