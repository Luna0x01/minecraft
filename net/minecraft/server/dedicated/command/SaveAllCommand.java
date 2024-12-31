package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.class_3915;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.level.storage.WorldSaveException;

public class SaveAllCommand {
	private static final SimpleCommandExceptionType field_21774 = new SimpleCommandExceptionType(new TranslatableText("commands.save.failed"));

	public static void method_20933(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("save-all").requires(arg -> arg.method_17575(4)))
					.executes(commandContext -> method_20932((class_3915)commandContext.getSource(), false)))
				.then(CommandManager.method_17529("flush").executes(commandContext -> method_20932((class_3915)commandContext.getSource(), true)))
		);
	}

	private static int method_20932(class_3915 arg, boolean bl) throws CommandSyntaxException {
		arg.method_17459(new TranslatableText("commands.save.saving"), false);
		MinecraftServer minecraftServer = arg.method_17473();
		boolean bl2 = false;
		minecraftServer.getPlayerManager().saveAllPlayerData();

		for (ServerWorld serverWorld : minecraftServer.method_20351()) {
			if (serverWorld != null && method_20935(serverWorld, bl)) {
				bl2 = true;
			}
		}

		if (!bl2) {
			throw field_21774.create();
		} else {
			arg.method_17459(new TranslatableText("commands.save.success"), true);
			return 1;
		}
	}

	private static boolean method_20935(ServerWorld serverWorld, boolean bl) {
		boolean bl2 = serverWorld.savingDisabled;
		serverWorld.savingDisabled = false;

		boolean var4;
		try {
			serverWorld.save(true, null);
			if (bl) {
				serverWorld.method_5323();
			}

			return true;
		} catch (WorldSaveException var8) {
			var4 = false;
		} finally {
			serverWorld.savingDisabled = bl2;
		}

		return var4;
	}
}
