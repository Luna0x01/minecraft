package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPosArgumentType implements ArgumentType<PosArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType UNLOADED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.pos.unloaded"));
	public static final SimpleCommandExceptionType OUT_OF_WORLD_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.pos.outofworld"));
	public static final SimpleCommandExceptionType field_29499 = new SimpleCommandExceptionType(new TranslatableText("argument.pos.outofbounds"));

	public static BlockPosArgumentType blockPos() {
		return new BlockPosArgumentType();
	}

	public static BlockPos getLoadedBlockPos(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		BlockPos blockPos = ((PosArgument)context.getArgument(name, PosArgument.class)).toAbsoluteBlockPos((ServerCommandSource)context.getSource());
		if (!((ServerCommandSource)context.getSource()).getWorld().isChunkLoaded(blockPos)) {
			throw UNLOADED_EXCEPTION.create();
		} else if (!((ServerCommandSource)context.getSource()).getWorld().isInBuildLimit(blockPos)) {
			throw OUT_OF_WORLD_EXCEPTION.create();
		} else {
			return blockPos;
		}
	}

	public static BlockPos getBlockPos(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		BlockPos blockPos = ((PosArgument)context.getArgument(name, PosArgument.class)).toAbsoluteBlockPos((ServerCommandSource)context.getSource());
		if (!World.isValid(blockPos)) {
			throw field_29499.create();
		} else {
			return blockPos;
		}
	}

	public PosArgument parse(StringReader stringReader) throws CommandSyntaxException {
		return (PosArgument)(stringReader.canRead() && stringReader.peek() == '^' ? LookingPosArgument.parse(stringReader) : DefaultPosArgument.parse(stringReader));
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		if (!(context.getSource() instanceof CommandSource)) {
			return Suggestions.empty();
		} else {
			String string = builder.getRemaining();
			Collection<CommandSource.RelativePosition> collection;
			if (!string.isEmpty() && string.charAt(0) == '^') {
				collection = Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL);
			} else {
				collection = ((CommandSource)context.getSource()).getBlockPositionSuggestions();
			}

			return CommandSource.suggestPositions(string, collection, builder, CommandManager.getCommandValidator(this::parse));
		}
	}

	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
