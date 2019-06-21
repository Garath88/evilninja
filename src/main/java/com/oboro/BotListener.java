package com.oboro;

import com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import commands.emoji.BanEmojiCommand;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class BotListener implements EventListener {
    private static final String SUCCESS_EMOJI = "\uD83D\uDE08";
    private final CommandClientImpl client;
    private final EventWaiter waiter;

    BotListener(CommandClientImpl client, EventWaiter waiter) {
        this.client = client;
        this.waiter = waiter;
    }

    @Override
    public void onEvent(Event event) {
        JDA jda = event.getJDA();
        if (event instanceof ReadyEvent) {
            client.setSuccess(SUCCESS_EMOJI);
        } else if (event instanceof TextChannelDeleteEvent) {

        } else if (event instanceof GuildMemberJoinEvent) {

        } else if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageRecievedEvent = (MessageReceivedEvent)event;

            handleUser(messageRecievedEvent.getAuthor(), messageRecievedEvent);
        } else if (event instanceof MessageDeleteEvent) {

        } else if (event instanceof MessageReactionAddEvent) {
            MessageReactionAddEvent messageReactionAddEvent = (MessageReactionAddEvent)event;
            handleUser(messageReactionAddEvent.getUser(), messageReactionAddEvent.getReaction());
        } else if (event instanceof GuildMemberLeaveEvent) {

        }
    }

    private void handleUser(User author, MessageReceivedEvent messageRecievedEvent) {
        Message message = messageRecievedEvent.getMessage();
        String userId = author.getId();
        BanEmojiCommand.deleteMessageWithBlacklistedEmojis(userId, message);
    }

    private void handleUser(User author, MessageReaction messageReaction) {
        BanEmojiCommand.deleteReactionWithBlacklistedEmojis(author, messageReaction);
    }
}