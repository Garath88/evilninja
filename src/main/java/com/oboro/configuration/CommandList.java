package com.oboro.configuration;

import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.oboro.RebootCommand;

import commands.QuoteCommand;
import commands.RemoveRoleCommand;
import commands.emoji.BanEmojiCommand;
import commands.emoji.RemoveEmojiBanCommand;
import commands.say.DMCommand;
import commands.say.ReadDMHistoryCommand;
import commands.say.SayCommand;
import commands.say.SayEditCommand;
import commands.say.SayEditLastCommand;
import commands.say.SetChanCommand;
import commands.system.HelpCommand;
import commands.system.PingCommand;
import commands.system.ShutdownCommand;
import commands.waifu.WaifuCommand;

public class CommandList {
    private static final String BOT_NAME = "Oboro";
    private List<Command> commands = new ArrayList<>();

    public CommandList(EventWaiter waiter) {
        // adds commands
        //        client.addCommands(
        //            // command to show information about the bot
        //            /*
        //            new AboutCommand(Color.BLUE, "an example bot",
        //                new String[] { "Cool commands", "Nice examples", "Lots of fun!" },
        //                new Permission[] { Permission.ADMINISTRATOR }),
        //                */
        //
        //            // command to show a random cat
        //            //new CatCommand(),
        //
        //            // command to make a random choice
        //            //new ChooseCommand(),
        //
        //            // command to say hello
        //            new HelloCommand(waiter),
        //
        //

        commands.add(new HelpCommand());
        commands.add(new PingCommand());
        commands.add(new ShutdownCommand());
        commands.add(new RebootCommand());
        commands.add(new SetChanCommand(BOT_NAME));
        commands.add(new SayCommand(BOT_NAME));
        commands.add(new SayEditCommand(BOT_NAME));
        commands.add(new SayEditLastCommand(BOT_NAME));
        commands.add(new DMCommand(BOT_NAME));
        commands.add(new ReadDMHistoryCommand(BOT_NAME));
        commands.add(new RemoveRoleCommand());
        commands.add(new BanEmojiCommand());
        commands.add(new RemoveEmojiBanCommand());
        commands.add(new QuoteCommand());
        commands.add(new WaifuCommand(waiter));
    }

    public List<Command> getCommands() {
        return commands;
    }
}
