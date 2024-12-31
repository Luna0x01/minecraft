package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionType;

public class DifficultyCommand {
	private static final DynamicCommandExceptionType FAILURE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.difficulty.failure", object)
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("difficulty");

		for (Difficulty difficulty : Difficulty.values()) {
			literalArgumentBuilder.then(
				CommandManager.literal(difficulty.getName()).executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), difficulty))
			);
		}

		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)))
				.executes(
					commandContext -> {
						Difficulty difficultyx = ((ServerCommandSource)commandContext.getSource()).getWorld().getDifficulty();
						((ServerCommandSource)commandContext.getSource())
							.sendFeedback(new TranslatableText("commands.difficulty.query", difficultyx.getTranslatableName()), false);
						return difficultyx.getId();
					}
				)
		);
	}

	public static int execute(ServerCommandSource serverCommandSource, Difficulty difficulty) throws CommandSyntaxException {
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		if (minecraftServer.getWorld(DimensionType.field_13072).getDifficulty() == difficulty) {
			throw FAILURE_EXCEPTION.create(difficulty.getName());
		} else {
			minecraftServer.setDifficulty(difficulty, true);
			serverCommandSource.sendFeedback(new TranslatableText("commands.difficulty.success", difficulty.getTranslatableName()), true);
			return 0;
		}
	}
}
