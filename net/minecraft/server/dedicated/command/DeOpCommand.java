package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.class_3915;
import net.minecraft.class_3965;
import net.minecraft.class_4073;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;

public class DeOpCommand {
	private static final SimpleCommandExceptionType field_21720 = new SimpleCommandExceptionType(new TranslatableText("commands.deop.failed"));

	public static void method_20641(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("deop").requires(arg -> arg.method_17575(3)))
				.then(
					CommandManager.method_17530("targets", class_4073.method_17988())
						.suggests(
							(commandContext, suggestionsBuilder) -> class_3965.method_17570(
									((class_3915)commandContext.getSource()).method_17473().getPlayerManager().getOpNames(), suggestionsBuilder
								)
						)
						.executes(commandContext -> method_20640((class_3915)commandContext.getSource(), class_4073.method_17991(commandContext, "targets")))
				)
		);
	}

	private static int method_20640(class_3915 arg, Collection<GameProfile> collection) throws CommandSyntaxException {
		PlayerManager playerManager = arg.method_17473().getPlayerManager();
		int i = 0;

		for (GameProfile gameProfile : collection) {
			if (playerManager.isOperator(gameProfile)) {
				playerManager.deop(gameProfile);
				i++;
				arg.method_17459(new TranslatableText("commands.deop.success", ((GameProfile)collection.iterator().next()).getName()), true);
			}
		}

		if (i == 0) {
			throw field_21720.create();
		} else {
			arg.method_17473().method_20313(arg);
			return i;
		}
	}
}
