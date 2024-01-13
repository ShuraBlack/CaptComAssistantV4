package de.shurablack.model.service.task;

import de.shurablack.core.builder.JDAUtil;
import de.shurablack.core.util.LocalData;
import de.shurablack.core.util.ServerUtil;
import de.shurablack.model.manager.DiscordBot;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class UpdateStatsTask implements Runnable {

    private final Logger logger;

    public UpdateStatsTask(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void run() {
        this.logger.info("Starting UpdateStatsTask CronJob ...");

        Guild guild = JDAUtil.getJDA().getGuildById(DiscordBot.GUILD);
        ServerUtil.GLOBAL_LOGGER.info("Boost/s: " + guild.getBoostCount() + " | Member/s: " + guild.getMemberCount());

        Objects.requireNonNull(Objects.requireNonNull(guild).getVoiceChannelById(LocalData.getChannelID("stats_member")))
                .getManager().setName("Member/s: " + guild.getMemberCount()).queue();
        Objects.requireNonNull(Objects.requireNonNull(guild).getVoiceChannelById(LocalData.getChannelID("stats_boost")))
                .getManager().setName("Boost/s: " + guild.getBoostCount()).queue();

        this.logger.info("Successfully finished UpdateStatsTask CronJob");
    }
}
