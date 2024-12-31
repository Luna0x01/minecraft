package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.class_3915;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionType;

public class DifficultyCommand {
	private static final DynamicCommandExceptionType field_21724 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.difficulty.failure", object)
	);

	public static void method_20660(CommandDispatcher<class_3915> commandDispatcher) {
		LiteralArgumentBuilder<class_3915> literalArgumentBuilder = CommandManager.method_17529("difficulty");

		for (Difficulty difficulty : Difficulty.values()) {
			literalArgumentBuilder.then(
				CommandManager.method_17529(difficulty.getName()).executes(commandContext -> method_20659((class_3915)commandContext.getSource(), difficulty))
			);
		}

		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.requires(arg -> arg.method_17575(2))).executes(commandContext -> {
				Difficulty difficultyx = ((class_3915)commandContext.getSource()).method_17468().method_16346();
				((class_3915)commandContext.getSource()).method_17459(new TranslatableText("commands.difficulty.query", difficultyx.method_15537()), false);
				return difficultyx.getId();
			})
		);
	}

	public static int method_20659(class_3915 arg, Difficulty difficulty) throws CommandSyntaxException {
		MinecraftServer minecraftServer = arg.method_17473();
		if (minecraftServer.method_20312(DimensionType.OVERWORLD).method_16346() == difficulty) {
			throw field_21724.create(difficulty.getName());
		} else {
			minecraftServer.setDifficulty(difficulty);
			arg.method_17459(new TranslatableText("commands.difficulty.success", difficulty.method_15537()), true);
			return 0;
		}
	}
}
