package de.shurablack.model.worker;

import de.shurablack.core.event.EventWorker;
import de.shurablack.core.util.LocalData;
import de.shurablack.core.util.ServerUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ruler extends EventWorker {

    public static final Map<String, Integer> INTERACTIONS = new HashMap<>();

    @Override
    public void processButtonEvent(Member member, MessageChannelUnion channel, String textID, ButtonInteractionEvent event) {
        if (INTERACTIONS.containsKey(member.getId()) && INTERACTIONS.get(member.getId()) >= 2) {
            return;
        }

        Guild guild = channel.asTextChannel().getGuild();
        Role guestRole = guild.getRoleById(LocalData.getRoleID("guest"));
        Role memberRole = guild.getRoleById(LocalData.getRoleID("member"));

        if (INTERACTIONS.containsKey(member.getId())) {
            INTERACTIONS.replace(member.getId(), INTERACTIONS.get(member.getId())+1);
        } else {
            INTERACTIONS.put(member.getId(),1);
        }

        switch (textID) {
            case "ruler guest":
                if (hasHigherRank(member.getRoles())) {
                    sendFeedback(event,"Du hast bereits einen höheren Rang auf dem Server!", ServerUtil.RED,true);
                    return;
                }
                guild.addRoleToMember(UserSnowflake.fromId(member.getId()), guestRole).queue();
                guild.removeRoleFromMember(User.fromId(member.getId()), memberRole).queue();
                sendFeedback(event,"Du wurdest erfolgreich zum **Guest** ernannt!",ServerUtil.GREEN,true);
                break;
            case "ruler member":
                if (hasHigherRank(member.getRoles())) {
                    sendFeedback(event,"Du hast bereits einen höheren Rang auf dem Server!",ServerUtil.RED,true);
                    return;
                }
                guild.addRoleToMember(UserSnowflake.fromId(member.getId()), memberRole).queue();
                guild.removeRoleFromMember(User.fromId(member.getId()), guestRole).queue();
                sendFeedback(event,"Du wurdest erfolgreich zum **Member** ernannt!",ServerUtil.GREEN,true);
                break;
        }
    }

    private boolean hasHigherRank(List<Role> roles) {
        return roles.stream().anyMatch(role -> role.getId().equals("286631270258180117")
                || role.getId().equals("286631247315337219"));
    }

    private void sendFeedback(ButtonInteractionEvent event, String description, int color, boolean ephemeral) {
        Member m = event.getMember();
        if (INTERACTIONS.get(m.getId()) >= 2) {
            description += "\n\nSperre: Du hast die maximale Menge an Anfragen pro Tag überschritten!\n" +
                    "Versuch es morgen wieder";
            color = ServerUtil.RED;
            ephemeral = false;
        }
        EmbedBuilder accept = new EmbedBuilder()
                .setAuthor(m.getEffectiveName(), m.getEffectiveAvatarUrl(), m.getEffectiveAvatarUrl())
                .setColor(color)
                .setDescription(description)
                .setFooter("Anfragen - (" + INTERACTIONS.get(m.getId()) + "/2)");
        event.replyEmbeds(accept.build()).setEphemeral(ephemeral).queue();
    }
}
