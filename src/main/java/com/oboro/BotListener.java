package com.oboro;

import com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.oboro.actions.HandleImagesAction;

import commands.emoji.BanEmojiCommand;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import tasks.AutoKickTask;
import tasks.Task;

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
        if (event instanceof ReadyEvent) {
            client.setSuccess(SUCCESS_EMOJI);
            startAutoKickCheckForNonMembers(event);
        } else if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageRecievedEvent = (MessageReceivedEvent)event;
            handleUser(messageRecievedEvent.getAuthor(), messageRecievedEvent);
            HandleImagesAction.checkImage(messageRecievedEvent);
        } else if (event instanceof MessageReactionAddEvent) {
            MessageReactionAddEvent messageReactionAddEvent = (MessageReactionAddEvent)event;
            handleUser(messageReactionAddEvent.getUser(), messageReactionAddEvent.getReaction());
        }
    }

    private void startAutoKickCheckForNonMembers(Event event) {
        Task task = new AutoKickTask(24 * 60, 0, event.getJDA());
        task.scheduleTask();
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