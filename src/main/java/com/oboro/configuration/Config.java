package com.oboro.configuration;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.TxtReader;

public final class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private static final URL CONFIG_FILE = Config.class.getResource("/configuration/config.txt");
    private static final TxtReader TXT_READER = new TxtReader(CONFIG_FILE);
    public static final String PREFIX = "-";
    private static final String TOKEN;
    private static final String OWNER_ID;
    static {
        List<String> list = Collections.emptyList();
        try {
            list = TXT_READER.readTxtFile();
        } catch (IOException e) {
            LOGGER.error("Failed to init config", e);
        }
        TOKEN = list.get(0);
        OWNER_ID = list.get(1);
    }

    private Config() {
    }

    public static String getToken() {
        return TOKEN;
    }

    public static String getOwnerId() {
        return OWNER_ID;
    }
}
