package com.kalimero2.team.waystones.paper.command;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.apiguardian.api.API;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class WaystoneArgument<C> extends CommandArgument<C, StoredWaystone> {


    private static WaystoneManager manager = PaperWayStones.getPlugin(PaperWayStones.class).getManager();

    private WaystoneArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<@NotNull CommandContext<C>, @NotNull String,
                    @NotNull List<@NotNull String>> suggestionsProvider,
            final @NotNull ArgumentDescription defaultDescription
    ) {
        super(required, name, new WaystoneArgument.WaystoneParser<>(), defaultValue, StoredWaystone.class, suggestionsProvider, defaultDescription);
        manager = PaperWayStones.getPlugin(PaperWayStones.class).getManager();
    }

    /**
     * Create a new {@link WaystoneArgument.Builder}.
     *
     * @param name argument name
     * @param <C>  sender typekit.entity.Player;
    import org.jetbrains.annotations.Nullable;
    import org.jetbrains.annotations.NotNull;

    import java.util.ArrayList;
    import java.util.List;
    import java
     * @return new {@link WaystoneArgument.Builder}
     */
    @API(status = API.Status.STABLE, since = "1.8.0")
    public static <C> WaystoneArgument.@NotNull Builder<C> builder(final @NotNull String name) {
        return new WaystoneArgument.Builder<>(name);
    }


    /**
     * Create a new required command component
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created component
     */
    public static <C> @NotNull CommandArgument<C, StoredWaystone> of(final @NotNull String name) {
        return WaystoneArgument.<C>builder(name).asRequired().build();
    }

    /**
     * Create a new optional command component
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created component
     */
    public static <C> @NotNull CommandArgument<C, StoredWaystone> optional(final @NotNull String name) {
        return WaystoneArgument.<C>builder(name).asOptional().build();
    }



    public static final class Builder<C> extends CommandArgument.Builder<C, StoredWaystone> {

        private Builder(final @NotNull String name) {
            super(StoredWaystone.class, name);
        }

        /**
         * Builder a new boolean component
         *
         * @return Constructed component
         */
        @Override
        public @NotNull WaystoneArgument<C> build() {
            return new WaystoneArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription()
            );
        }
    }
    public static final class WaystoneParser<C> implements ArgumentParser<C, StoredWaystone> {

        @Override
        public @NotNull ArgumentParseResult<StoredWaystone> parse(final @NotNull CommandContext<C> commandContext, final @NotNull Queue<@NotNull String> inputQueue) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(WaystoneArgument.WaystoneParser.class, commandContext));
            }

            StoredWaystone waystone = null;
            try {
                waystone = manager.getWaystone(Integer.parseInt(input));
            } catch (NumberFormatException ignored) {}

            if (waystone == null) {
                waystone = manager.getWaystone(input.replaceAll("–", " "));
            }

            if (waystone == null) {
                return ArgumentParseResult.failure(new WaystoneParseException(input, commandContext));
            }

            inputQueue.remove();

            return ArgumentParseResult.success(waystone);
        }

        @Override
        public @NotNull List<@NotNull String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            List<String> output = new ArrayList<>();

            if (commandContext.getSender() instanceof Player player) {
                for (StoredWaystone waystone : manager.getWaystones(player)) {
                    output.add(String.valueOf(waystone.name()).replaceAll(" ", "–"));
                }
            }
            else {
                for (StoredWaystone waystone : manager.getWaystones()) {
                    output.add(String.valueOf(waystone.name()).replaceAll(" ", "–"));
                }
            }

            return output;
        }
    }

    /**
     * StoredWaystone parse exception
     */
    public static final class WaystoneParseException extends ParserException {

        private final String input;

        /**
         * Construct a new StoredWaystone parse exception
         *
         * @param input   String input
         * @param context Command context
         */
        public WaystoneParseException(
                final @NotNull String input,
                final @NotNull CommandContext<?> context
        ) {
            super(
                    WaystoneArgument.WaystoneParser.class,
                    context,
                    Caption.of("argument.parse.failure.waystone"),
                    CaptionVariable.of("input", input)
            );
            this.input = input;

            if (!context.isSuggestions()) {
                ((CommandSender) context.getSender()).sendMessage(Component.text("No Waystone with ID or name " + input + " exists.").color(TextColor.color(255, 78, 0)));
            }
        }

        /**
         * Get the supplied input
         *
         * @return String value
         */
        public @NotNull String getInput() {
            return this.input;
        }
    }
}
