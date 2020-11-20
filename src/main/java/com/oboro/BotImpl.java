package com.oboro;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import com.jagrosh.jdautilities.commons.ConfigLoader;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.oboro.configuration.CommandList;
import com.typesafe.config.Config;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class BotImpl implements Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotImpl.class);
    private static final Config CONFIG = ConfigLoader.getConfig();
    private CommandClientBuilder client = new CommandClientBuilder();
    private EventWaiter waiter = new EventWaiter();

    BotImpl() {
        setupParameters();
    }

    private void setupParameters() {
        // The default game is: playing Type [prefix]help
        client.setActivity(Activity.watching("You"));

        // sets the owner of the bot
        client.setOwnerId(CONFIG.getString("ownerId"));

        // sets emojis used throughout the bot on successes, warnings, and failures
        client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");

        // sets the bot prefix
        client.setPrefix("-");

        client.useHelpBuilder(true);
        client.setWaiter(waiter);
    }

    @Override
    public EventWaiter getEventWaiter() {
        return waiter;
    }

    @Override
    public void addCommands(CommandList commands) {
        commands.getCommands()
            .forEach(client::addCommands);
    }

    void start() {
        // start getting a bot account set up
        try {
            init();
        } catch (LoginException e) {
            LOGGER.error("Failed to start bot", e);
        }
    }

    private void init() throws LoginException {
        CommandClient bot = client.build();
        // set the token
        JDABuilder.create(CONFIG.getString("token"), GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            // set the game for when the bot is loading
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .setActivity(Activity.playing("loading..."))
            // add the listeners
            .addEventListeners(bot)
            .addEventListeners(waiter)
            .addEventListeners(new BotListener((CommandClientImpl)bot, waiter))
            .build();
    }
}




