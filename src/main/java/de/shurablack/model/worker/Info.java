package de.shurablack.model.worker;

import de.shurablack.core.builder.JDAUtil;
import de.shurablack.core.event.EventWorker;
import de.shurablack.core.util.ServerUtil;
import de.shurablack.model.database.Statement;
import de.shurablack.model.database.models.UserTimeModel;
import de.shurablack.sql.SQLRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Info extends EventWorker {

    @Override
    public void processGuildSlashEvent(Member member, MessageChannelUnion channel, String name, SlashCommandInteractionEvent event) {
        String option = event.getOption("entity").getAsString();
        if (option.equals("self")) {
            SQLRequest.Result<UserTimeModel> profile = SQLRequest.runSingle(
                    Statement.SELECT_USER_TIME(member.getId()), UserTimeModel.class);

            EmbedBuilder eb = new EmbedBuilder()
                    .setAuthor(member.getEffectiveName(),member.getEffectiveAvatarUrl(),member.getEffectiveAvatarUrl())
                    .addField("Mitgield seit:",member.getTimeJoined().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)),false);

            if (profile.isPresent()) {
                eb.setColor(ServerUtil.GREEN)
                        .addField("Aktive Zeit:",profile.value.toString(),false);
            } else {
                eb.setColor(ServerUtil.GREEN)
                        .addField("Aktive Zeit:","NONE",false);
            }
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
        } else if (option.equals("bot")) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(ServerUtil.BLUE)
                    .setDescription("INFO - CaptCommunity Bot")
                    .setThumbnail(JDAUtil.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .addField("","**Version:** 1.0.1\n**Build:** JDAUtil 1.0.0 | JDA 5.0.0-beta.6\n**Ping:** "
                            + JDAUtil.getJDA().getGatewayPing(),false);
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
        }
    }
}