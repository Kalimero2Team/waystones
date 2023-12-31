package com.kalimero2.team.waystones.paper.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class TextUtil {

    public static final TextColor RED = TextColor.color(255, 0, 0);
    public static final TextColor ORANGE = TextColor.color(255, 73, 0);
    public static final TextColor GREEN = TextColor.color(18, 255, 36);
    public static final TextColor WHITE = TextColor.color(255, 255, 255);


    @NotNull
    public static String compomentToString(Component title) {
        return PlainTextComponentSerializer.plainText().serialize(title);
    }
}
