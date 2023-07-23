package com.kalimero2.team.waystones.paper.util;

import java.util.HashMap;
import java.util.Map;

public enum SortMode {
    ALPHABETICAL,
    ALPHABETICAL_DESCENDING,

    NUMERIC,
    NUMERIC_DESCENDING,

    POPULARITY,
    POPULARITY_ASCENDING;

    private static final Map<Integer, SortMode> MAP = new HashMap<>();

    static {
        for (SortMode element : values()) {
            MAP.put(element.ordinal(), element);
        }
    }


    public static SortMode valueByNumber(int id) {
        try {
            return MAP.get(id);
        }
        catch (Exception e) {
            return NUMERIC;
        }
    }
}
