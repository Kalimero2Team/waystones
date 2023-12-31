package com.kalimero2.team.waystones.paper.util;

import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;

public enum Visibility {
    PUBLIC(0, "waystones.visibility.public"),
    UNLISTED(1, "waystones.visibility.unlisted"),
    PRIVATE(2, "waystones.visibility.private");

    private static final Map<Integer, Visibility> MAP = new HashMap<>();

    static {
        for (Visibility element : values()) {
            MAP.put(element.id(), element);
        }
    }

    private final int id;
    private final String translationKey;

    Visibility(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }


    public static Visibility valueByNumber(int id) {
        try {
            return MAP.get(id);
        } catch (NullPointerException e) {
            return PUBLIC;
        }
    }

    public int id() {
        return id;
    }

    public Component text() {
        return Component.translatable(translationKey);
    }
}
