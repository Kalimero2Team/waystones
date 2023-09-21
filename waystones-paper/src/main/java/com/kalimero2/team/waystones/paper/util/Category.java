package com.kalimero2.team.waystones.paper.util;

public record Category(int id, String name, boolean isPublic) {
    public static Category NONE = new Category(-1, "NULL", true);

}
