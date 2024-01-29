package com.kalimero2.team.waystones.paper.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum SortMode {
    ALPHABETICAL,
    ALPHABETICAL_DESCENDING,

    POPULARITY,
    POPULARITY_ASCENDING;

    private static final Map<Integer, SortMode> MAP = new HashMap<>();

    static {
        for (SortMode element : values()) {
            MAP.put(element.ordinal(), element);
        }
    }


    public static @NotNull SortMode valueByNumber(int id) {
        try {
            return MAP.getOrDefault(id, POPULARITY);
        }
        catch (Exception e) {
            return POPULARITY;
        }
    }
}
