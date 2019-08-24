package com.oboro;

import com.oboro.configuration.CommandList;

public class Main {
    static {
        System.setProperty("logback.configurationFile", "./logback.xml");
    }
    public static void main(String[] args) {
        BotImpl bot = new BotImpl();
        CommandList commandList = new CommandList(bot.getEventWaiter());
        bot.addCommands(commandList);
        bot.start();
    }
}
