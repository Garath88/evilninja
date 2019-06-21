package com.oboro;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.oboro.configuration.CommandList;

public interface Bot {
    EventWaiter getEventWaiter();

    void addCommands(CommandList commands);
}
