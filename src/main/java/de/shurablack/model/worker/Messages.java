package de.shurablack.model.worker;

import de.shurablack.core.event.EventWorker;
import de.shurablack.core.util.AssetPool;
import de.shurablack.core.util.LocalData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;
import java.time.OffsetDateTime;

public class Messages extends EventWorker {

    @Override
    public void processPublicChannelEvent(Member member, MessageChannelUnion channel, String message, MessageReceivedEvent event) {
        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            return;
        }

        if (message.contains("rules")) {
            sendRulesMessages(channel);
        } else if (message.contains("subs")) {
            sendSubscriptionMessages(channel);
        } else if (message.contains("roles")) {
            sendRolesMessages(channel);
        }
    }

    private void sendRulesMessages(MessageChannelUnion channel) {
        EmbedBuilder font = new EmbedBuilder()
                .setImage(AssetPool.get("url_font_rules"));
        EmbedBuilder rules = new EmbedBuilder()
                .setDescription("**§1** Beleidigt, diskriminiert oder stört keine anderen Member\n\n" +
                        "**§2** Die Servermoderation muss zu jeder aktiven Zeit mit dir Kommunizieren können\n\n" +
                        "**§3** Provoziere keine Member\n\n" +
                        "**§4** Nicknames werden, wenn sie *§1* verletzen, auf dem Server verändert\n\n" +
                        "**§5** Sende keine Links mit unangemessenem Inhalt (z.B. Pornografische oder Gewaltätige Darstellung, ...)\n\n" +
                        "**§6** Keine Eigenwerbung jeglicher Art (z.B. Discord Server, Youtube Kanal, Facebook- und Twitter Fanpages, sowie Klans/Teams)\n\n" +
                        "**§7** Hochgeladene Daten dürfen *§1,3,5,6* nicht verletzen\n\n" +
                        "**§8** Das Spamming in Voice- oder TextChannel\n\n" +
                        "**§9** Ungekennzeichnete Bots werden entfernt (Unter Absprache möglich)\n\n" +
                        "**§10** Betrüge niemanden\n\n" +
                        "**§11** Halte dich an Discord´s [TOS](https://discord.com/terms)\n\n" +
                        "_Missachten der Server-Richtlinien führt zum Ausschluss!_")
                .setFooter("Gültig ab dem")
                .setTimestamp(OffsetDateTime.now());
        EmbedBuilder ebbottom = new EmbedBuilder();
        ebbottom.setColor(Color.RED);
        ebbottom.addField("Meldet Probleme oder Beschwerden der Server Moderation",
                "Wie auch beim Gesetz: Unwissenheit schützt nicht vor Strafe und deswegen " +
                        "empfehlen wir die Regeln __gut__ durchzulesen und zu kennen",false);

        EmbedBuilder redirect = new EmbedBuilder();
        redirect.addField("Zur Rollenverteilung:"
                ,"[Roles Channel](https://discord.com/channels/286628427140825088/799449909090713631/1015080368036126832)",false);

        channel.sendMessageEmbeds(font.build(), rules.build(), ebbottom.build(), redirect.build()).queue();
    }

    private void sendRolesMessages(MessageChannelUnion channel) {
        channel.sendMessageEmbeds(new EmbedBuilder().setImage(AssetPool.get("url_font_roles")).build()).queue();

        EmbedBuilder eb = new EmbedBuilder()
                .setDescription("Über diese Nachricht ist jeder Nutzer in der Lage, eine Standard-Rolle zu erhalten." +
                        "\nNutzer mit höherem Rang können sich nicht über diese Nachricht degradieren.")
                .addField("","**Auswahl:**",false)
                .addField("<:bronze:1007022490037518526> Guest","Für gelegentliche/kurzzeitige Nutzer. Keine besonderen Rechte",true)
                .addField("<:silber:1007022487411896320> Member","Für bekannte Mitspieler/Freunde. Erweiterte Rechte",true)
                .addField("<:gold:1007022493955010631> Veteran / <:platin:1007022498296103024> Moderator",
                        "Benachrichtige die Servermoderation um diese Ränge zu beantragen",false)
                .addBlankField(false)
                .addField("Freunde einladen","Nutze diesen Link dafür:\nhttps://discord.gg/6yJm5kpfDp",false)
                .setFooter("Das Missbrauchen der Funktion führt zu einem Ausschluss (2 Anfrage pro Tag/Person)");

        MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
                .addEmbeds(eb.build())
                .setComponents(ActionRow.of(
                        Button.secondary("ruler guest", Emoji.fromCustom("bronze",1007022490037518526L,false)),
                        Button.secondary("ruler member",Emoji.fromCustom("silber",1007022487411896320L, false))
                ));
        channel.sendMessage(messageBuilder.build()).complete();
    }

    private void sendSubscriptionMessages(MessageChannelUnion channel) {
        final Emoji league = Emoji.fromCustom("league",Long.parseLong(LocalData.getEmojiID("league")),false);
        final Emoji warframe = Emoji.fromCustom("warframe",Long.parseLong(LocalData.getEmojiID("warframe")),false);
        final Emoji moon = Emoji.fromCustom("moon",Long.parseLong(LocalData.getEmojiID("moon")),false);
        final Emoji mc = Emoji.fromCustom("mc",Long.parseLong(LocalData.getEmojiID("minecraft")),false);
        final Emoji sati = Emoji.fromCustom("sati",Long.parseLong(LocalData.getEmojiID("satisfactory")),false);
        final Emoji phasmo = Emoji.fromCustom("phasmo",Long.parseLong(LocalData.getEmojiID("phasmo")),false);
        final Emoji casino = Emoji.fromCustom("casino",Long.parseLong(LocalData.getEmojiID("casino")),false);
        final String ex = "✖️";

        EmbedBuilder eb = new EmbedBuilder()
                .setDescription("Spielst du Games? Dann zeig welches Spiel du mit anderne teilst")
                .addField("","Verfügbare Spiele:",false)
                .addField(league.getFormatted() + " League of Legends","",true)
                .addField(warframe.getFormatted() + " Warframe","",true)
                .addField(mc.getFormatted() + " Minecraft","",true)
                .addField(sati.getFormatted() + " Satisfactory","",true)
                .addField(phasmo.getFormatted() + " Phasmophobia","",true)
                .addBlankField(true)
                .addField("❌ Entfernen,","Entfernt alle abonnierten Channel von dir",false);

        MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
                .addEmbeds(new EmbedBuilder().setImage(AssetPool.get("url_font_games")).build(), eb.build())
                .setComponents(ActionRow.of(
                        Button.secondary("sub league", league),
                        Button.secondary("sub warframe", warframe),
                        Button.secondary("sub minecraft", mc),
                        Button.secondary("sub satisfactory", sati),
                        Button.secondary("sub phasmophobia", phasmo)
                ));
        MessageCreateBuilder removeGame = new MessageCreateBuilder()
                .setComponents(ActionRow.of(
                        Button.danger("sub delete_news", Emoji.fromUnicode(ex))
                ));
        channel.sendMessage(messageBuilder.build()).queue();
        channel.sendMessage(removeGame.build()).queue();

        eb = new EmbedBuilder()
                .addField(moon.getFormatted() + " Moonstruck,","Information über den Streamer, sowie Ankündigungen für den Stream",false)
                .addField(casino.getFormatted() + " Games,","Schaltet alle Server privaten Games Channel frei\n" +
                        "```diff\n- Du benötigst ebenfalls mindestens den Member Rang und bestätigst damit auch das du über 18 bist\n```",false)
                .addField("❌ Entfernen,","Entfernt alle abonnierten Partner und Specials von dir",false)
                .setFooter("Das Missbrauchen der Funktion führt zu einem Ausschluss (10 Anfrage pro Tag/Person)");

        messageBuilder.clear().addEmbeds(new EmbedBuilder().setImage(AssetPool.get("url_font_partner")).build(), eb.build())
                .setComponents(ActionRow.of(
                        Button.secondary("sub moon", moon),
                        Button.secondary("sub casino", casino),
                        Button.danger("sub delete_special", Emoji.fromUnicode(ex))
                ));
        channel.sendMessage(messageBuilder.build()).queue();

    }
}
