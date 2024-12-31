package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class class_4430 {
	public static void method_21011(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("setworldspawn").requires(arg -> arg.method_17575(2)))
					.executes(commandContext -> method_21010((class_3915)commandContext.getSource(), new BlockPos(((class_3915)commandContext.getSource()).method_17467()))))
				.then(
					CommandManager.method_17530("pos", class_4252.method_19358())
						.executes(commandContext -> method_21010((class_3915)commandContext.getSource(), class_4252.method_19361(commandContext, "pos")))
				)
		);
	}

	private static int method_21010(class_3915 arg, BlockPos blockPos) {
		arg.method_17468().setSpawnPos(blockPos);
		arg.method_17473().getPlayerManager().sendToAll(new PlayerSpawnPositionS2CPacket(blockPos));
		arg.method_17459(new TranslatableText("commands.setworldspawn.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), true);
		return 1;
	}
}
