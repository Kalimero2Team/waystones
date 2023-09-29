package com.kalimero2.team.waystones.paper.util;

import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;

public enum Visibility {
    PUBLIC,
    UNLISTED,
    PRIVATE;

    private static final Map<Integer, Visibility> MAP = new HashMap<>();
    private static final Component[] TEXT = {Component.translatable("waystones.visibility.public"), Component.translatable("waystones.visibility.unlisted"), Component.translatable("waystones.visibility.private")};

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

    public Component text() {
        return TEXT[ordinal()];
    }
}
