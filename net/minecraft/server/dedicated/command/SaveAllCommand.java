package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class SaveAllCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.save.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("save-all")
						.requires(source -> source.hasPermissionLevel(4)))
					.executes(context -> saveAll((ServerCommandSource)context.getSource(), false)))
				.then(CommandManager.literal("flush").executes(context -> saveAll((ServerCommandSource)context.getSource(), true)))
		);
	}

	private static int saveAll(ServerCommandSource source, boolean flush) throws CommandSyntaxException {
		source.sendFeedback(new TranslatableText("commands.save.saving"), false);
		MinecraftServer minecraftServer = source.getServer();
		minecraftServer.getPlayerManager().saveAllPlayerData();
		boolean bl = minecraftServer.save(true, flush, true);
		if (!bl) {
			throw FAILED_EXCEPTION.create();
		} else {
			source.sendFeedback(new TranslatableText("commands.save.success"), true);
			return 1;
		}
	}
}
