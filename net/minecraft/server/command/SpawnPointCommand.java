package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.class_3915;
import net.minecraft.class_4062;
import net.minecraft.class_4252;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class SpawnPointCommand {
	public static void method_21005(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("spawnpoint").requires(arg -> arg.method_17575(2)))
					.executes(
						commandContext -> method_21004(
								(class_3915)commandContext.getSource(),
								Collections.singleton(((class_3915)commandContext.getSource()).method_17471()),
								new BlockPos(((class_3915)commandContext.getSource()).method_17467())
							)
					))
				.then(
					((RequiredArgumentBuilder)CommandManager.method_17530("targets", class_4062.method_17904())
							.executes(
								commandContext -> method_21004(
										(class_3915)commandContext.getSource(),
										class_4062.method_17907(commandContext, "targets"),
										new BlockPos(((class_3915)commandContext.getSource()).method_17467())
									)
							))
						.then(
							CommandManager.method_17530("pos", class_4252.method_19358())
								.executes(
									commandContext -> method_21004(
											(class_3915)commandContext.getSource(), class_4062.method_17907(commandContext, "targets"), class_4252.method_19361(commandContext, "pos")
										)
								)
						)
				)
		);
	}

	private static int method_21004(class_3915 arg, Collection<ServerPlayerEntity> collection, BlockPos blockPos) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.setPlayerSpawn(blockPos, true);
		}

		if (collection.size() == 1) {
			arg.method_17459(
				new TranslatableText(
					"commands.spawnpoint.success.single", blockPos.getX(), blockPos.getY(), blockPos.getZ(), ((ServerPlayerEntity)collection.iterator().next()).getName()
				),
				true
			);
		} else {
			arg.method_17459(new TranslatableText("commands.spawnpoint.success.multiple", blockPos.getX(), blockPos.getY(), blockPos.getZ(), collection.size()), true);
		}

		return collection.size();
	}
}
