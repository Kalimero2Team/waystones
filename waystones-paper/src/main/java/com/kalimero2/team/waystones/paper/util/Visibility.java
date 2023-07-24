package com.kalimero2.team.waystones.paper.util;

import java.util.HashMap;
import java.util.Map;

public enum Visibility {
    PUBLIC,
    UNLISTED,
    PRIVATE;

    private static final Map<Integer, Visibility> MAP = new HashMap<>();
    private static final String[] TEXT = {"öffentlich", "ungelistet", "privat"};

    static {
        for (Visibility element : values()) {
            MAP.put(element.ordinal(), element);
        }
    }


    public static Visibility valueByNumber(int id) {
        try {
            return MAP.get(id);
        }
        catch (Exception e) {
            return PUBLIC;
        }
    }

    public String text() {
        return TEXT[ordinal()];
    }
}
