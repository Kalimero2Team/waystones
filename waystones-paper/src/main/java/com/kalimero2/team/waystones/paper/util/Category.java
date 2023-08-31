package com.kalimero2.team.waystones.paper.util;

public record Category(String name, boolean isPublic) {
    public static Category NONE = new Category("NULL", true);

}
