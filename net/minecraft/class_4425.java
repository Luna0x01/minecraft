package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;

public class class_4425 {
	public static void method_20842(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("list")
					.executes(commandContext -> method_20840((class_3915)commandContext.getSource())))
				.then(CommandManager.method_17529("uuids").executes(commandContext -> method_20844((class_3915)commandContext.getSource())))
		);
	}

	private static int method_20840(class_3915 arg) {
		return method_20841(arg, PlayerEntity::getName);
	}

	private static int method_20844(class_3915 arg) {
		return method_20841(arg, PlayerEntity::method_15935);
	}

	private static int method_20841(class_3915 arg, Function<ServerPlayerEntity, Text> function) {
		PlayerManager playerManager = arg.method_17473().getPlayerManager();
		List<ServerPlayerEntity> list = playerManager.getPlayers();
		Text text = ChatSerializer.method_20193(list, function);
		arg.method_17459(new TranslatableText("commands.list.players", list.size(), playerManager.getMaxPlayerCount(), text), false);
		return list.size();
	}
}
