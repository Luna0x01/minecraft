package net.minecraft.command.arguments;

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
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class BlockPosArgumentType implements ArgumentType<PosArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType UNLOADED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.pos.unloaded"));
	public static final SimpleCommandExceptionType OUT_OF_WORLD_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.pos.outofworld"));

	public static BlockPosArgumentType blockPos() {
		return new BlockPosArgumentType();
	}

	public static BlockPos getLoadedBlockPos(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		BlockPos blockPos = ((PosArgument)commandContext.getArgument(string, PosArgument.class)).toAbsoluteBlockPos((ServerCommandSource)commandContext.getSource());
		if (!((ServerCommandSource)commandContext.getSource()).getWorld().isChunkLoaded(blockPos)) {
			throw UNLOADED_EXCEPTION.create();
		} else {
			((ServerCommandSource)commandContext.getSource()).getWorld();
			if (!ServerWorld.isValid(blockPos)) {
				throw OUT_OF_WORLD_EXCEPTION.create();
			} else {
				return blockPos;
			}
		}
	}

	public static BlockPos getBlockPos(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return ((PosArgument)commandContext.getArgument(string, PosArgument.class)).toAbsoluteBlockPos((ServerCommandSource)commandContext.getSource());
	}

	public PosArgument parse(StringReader stringReader) throws CommandSyntaxException {
		return (PosArgument)(stringReader.canRead() && stringReader.peek() == '^' ? LookingPosArgument.parse(stringReader) : DefaultPosArgument.parse(stringReader));
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (!(commandContext.getSource() instanceof CommandSource)) {
			return Suggestions.empty();
		} else {
			String string = suggestionsBuilder.getRemaining();
			Collection<CommandSource.RelativePosition> collection;
			if (!string.isEmpty() && string.charAt(0) == '^') {
				collection = Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL);
			} else {
				collection = ((CommandSource)commandContext.getSource()).getBlockPositionSuggestions();
			}

			return CommandSource.suggestPositions(string, collection, suggestionsBuilder, CommandManager.getCommandValidator(this::parse));
		}
	}

	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
