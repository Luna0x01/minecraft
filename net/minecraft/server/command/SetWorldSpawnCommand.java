package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.network.packet.PlayerSpawnPositionS2CPacket;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class SetWorldSpawnCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("setworldspawn")
						.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)))
					.executes(
						commandContext -> execute((ServerCommandSource)commandContext.getSource(), new BlockPos(((ServerCommandSource)commandContext.getSource()).getPosition()))
					))
				.then(
					CommandManager.argument("pos", BlockPosArgumentType.blockPos())
						.executes(commandContext -> execute((ServerCommandSource)commandContext.getSource(), BlockPosArgumentType.getBlockPos(commandContext, "pos")))
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, BlockPos blockPos) {
		serverCommandSource.getWorld().setSpawnPos(blockPos);
		serverCommandSource.getMinecraftServer().getPlayerManager().sendToAll(new PlayerSpawnPositionS2CPacket(blockPos));
		serverCommandSource.sendFeedback(new TranslatableText("commands.setworldspawn.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), true);
		return 1;
	}
}
