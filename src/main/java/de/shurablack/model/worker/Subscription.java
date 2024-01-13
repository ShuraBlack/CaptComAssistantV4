package de.shurablack.model.worker;

import de.shurablack.core.event.EventWorker;
import de.shurablack.core.util.LocalData;
import de.shurablack.core.util.ServerUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Subscription extends EventWorker {

    public static final Map<String, Integer> INTERACTIONS = new HashMap<>();

    @Override
    public void processButtonEvent(Member member, MessageChannelUnion channel, String textID, ButtonInteractionEvent event) {
        if (INTERACTIONS.containsKey(member.getId()) && INTERACTIONS.get(member.getId()) >= 10) {
            return;
        }

        if (INTERACTIONS.containsKey(member.getId())) {
            INTERACTIONS.replace(member.getId(), INTERACTIONS.get(member.getId())+1);
        } else {
            INTERACTIONS.put(member.getId(),1);
        }

        Guild guild = channel.asTextChannel().getGuild();

        final Role leaguerole = guild.getRoleById(LocalData.getRoleID("league"));
        final Role warframerole = guild.getRoleById(LocalData.getRoleID("warframe"));
        final Role moonrole = guild.getRoleById(LocalData.getRoleID("moon"));
        final Role mcrole = guild.getRoleById(LocalData.getRoleID("mc"));
        final Role satirole = guild.getRoleById(LocalData.getRoleID("sati"));
        final Role phasmorole = guild.getRoleById(LocalData.getRoleID("phasmo"));
        final Role casinorole = guild.getRoleById(LocalData.getRoleID("casino"));

        switch (textID) {
            case "sub league":
                assert leaguerole != null;
                guild.addRoleToMember(UserSnowflake.fromId(member.getId()),leaguerole).queue();
                sendFeedback(event, "Du hast erfolgreich den **League of Legends** Channel abonniert!", ServerUtil.GREEN,true);
                break;
            case "sub warframe":
                assert warframerole != null;
                guild.addRoleToMember(User.fromId(member.getId()),warframerole).queue();
                sendFeedback(event, "Du hast erfolgreich den **Warframe** Channel abonniert!",ServerUtil.GREEN,true);
                break;
            case "sub minecraft":
                assert mcrole != null;
                guild.addRoleToMember(User.fromId(member.getId()), mcrole).queue();
                sendFeedback(event, "Du hast erfolgreich den **Minecraft** Channel abonniert!",ServerUtil.GREEN,true);
                break;
            case "sub satisfactory":
                assert satirole != null;
                guild.addRoleToMember(User.fromId(member.getId()), satirole).queue();
                sendFeedback(event, "Du hast erfolgreich den **Satisfactory** Channel abonniert!",ServerUtil.GREEN,true);
                break;
            case "sub moon":
                assert moonrole != null;
                guild.addRoleToMember(User.fromId(member.getId()), moonrole).queue();
                sendFeedback(event, "Du hast erfolgreich den **Moonstruck** Channel abonniert!",ServerUtil.GREEN,true);
                break;
            case "sub phasmophobia":
                assert phasmorole != null;
                guild.addRoleToMember(User.fromId(member.getId()), phasmorole).queue();
                sendFeedback(event, "Du hast erfolgreich den **Phasmophobia** Channel abonniert!",ServerUtil.GREEN,true);
                break;
            case "sub casino":
                if (member.getRoles().stream().map(Role::getName).noneMatch(name -> name.equals("Member")
                        || name.equals("Veteran") || name.equals("Moderator"))) {
                    sendFeedback(event, "Du benötigst mindestens den **Member** Rang für diese Aktion!",ServerUtil.RED,true);
                    return;
                }
                assert casinorole != null;
                guild.addRoleToMember(User.fromId(member.getId()), casinorole).queue();
                sendFeedback(event, "Du hast erfolgreich die **Game (Casino)** Channels abonniert!",ServerUtil.GREEN,true);
                break;
            case "sub delete_news":
                List<Role> rNews = new ArrayList<>();
                rNews.add(leaguerole);
                rNews.add(warframerole);
                rNews.add(mcrole);
                rNews.add(satirole);

                guild.modifyMemberRoles(member, new ArrayList<>(), rNews).queue();
                sendFeedback(event, "Alle deine Channel Abonnements wurden entfernt!",ServerUtil.GREEN,true);
                break;
            case "sub delete_special":
                List<Role> rSpecials = new ArrayList<>();
                rSpecials.add(moonrole);
                rSpecials.add(casinorole);

                guild.modifyMemberRoles(member, new ArrayList<>(), rSpecials).queue();
                sendFeedback(event, "Alle deine Partner & Special Abonnements wurden entfernt!",ServerUtil.GREEN,true);
                break;
        }
    }

    private void sendFeedback(ButtonInteractionEvent event, String description, int color, boolean ephemeral) {
        Member m = event.getMember();
        assert m != null;
        if (INTERACTIONS.get(m.getId()) >= 10) {
            description += "\n\nSperre: Du hast die maximale Menge an Anfragen pro Tag überschritten!\n" +
                    "Versuch es morgen wieder";
            color = ServerUtil.RED;
            ephemeral = false;
        }
        EmbedBuilder accept = new EmbedBuilder()
                .setAuthor(m.getEffectiveName(), m.getEffectiveAvatarUrl(), m.getEffectiveAvatarUrl())
                .setColor(color)
                .setDescription(description)
                .setFooter("Anfragen - (" + INTERACTIONS.get(m.getId()) + "/10)");
        event.replyEmbeds(accept.build()).setEphemeral(ephemeral).queue();
    }
}
