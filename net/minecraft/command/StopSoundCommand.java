package net.minecraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.class_3915;
import net.minecraft.class_4062;
import net.minecraft.class_4181;
import net.minecraft.class_4317;
import net.minecraft.class_4327;
import net.minecraft.class_4380;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class StopSoundCommand {
	public static void method_21035(CommandDispatcher<class_3915> commandDispatcher) {
		RequiredArgumentBuilder<class_3915, class_4317> requiredArgumentBuilder = (RequiredArgumentBuilder<class_3915, class_4317>)((RequiredArgumentBuilder)CommandManager.method_17530(
					"targets", class_4062.method_17904()
				)
				.executes(commandContext -> method_21034((class_3915)commandContext.getSource(), class_4062.method_17907(commandContext, "targets"), null, null)))
			.then(
				CommandManager.method_17529("*")
					.then(
						CommandManager.method_17530("sound", class_4181.method_18904())
							.suggests(class_4327.field_21256)
							.executes(
								commandContext -> method_21034(
										(class_3915)commandContext.getSource(), class_4062.method_17907(commandContext, "targets"), null, class_4181.method_18910(commandContext, "sound")
									)
							)
					)
			);

		for (SoundCategory soundCategory : SoundCategory.values()) {
			requiredArgumentBuilder.then(
				((LiteralArgumentBuilder)CommandManager.method_17529(soundCategory.getName())
						.executes(commandContext -> method_21034((class_3915)commandContext.getSource(), class_4062.method_17907(commandContext, "targets"), soundCategory, null)))
					.then(
						CommandManager.method_17530("sound", class_4181.method_18904())
							.suggests(class_4327.field_21256)
							.executes(
								commandContext -> method_21034(
										(class_3915)commandContext.getSource(),
										class_4062.method_17907(commandContext, "targets"),
										soundCategory,
										class_4181.method_18910(commandContext, "sound")
									)
							)
					)
			);
		}

		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("stopsound").requires(arg -> arg.method_17575(2)))
				.then(requiredArgumentBuilder)
		);
	}

	private static int method_21034(
		class_3915 arg, Collection<ServerPlayerEntity> collection, @Nullable SoundCategory soundCategory, @Nullable Identifier identifier
	) {
		class_4380 lv = new class_4380(identifier, soundCategory);

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			serverPlayerEntity.networkHandler.sendPacket(lv);
		}

		if (soundCategory != null) {
			if (identifier != null) {
				arg.method_17459(new TranslatableText("commands.stopsound.success.source.sound", identifier, soundCategory.getName()), true);
			} else {
				arg.method_17459(new TranslatableText("commands.stopsound.success.source.any", soundCategory.getName()), true);
			}
		} else if (identifier != null) {
			arg.method_17459(new TranslatableText("commands.stopsound.success.sourceless.sound", identifier), true);
		} else {
			arg.method_17459(new TranslatableText("commands.stopsound.success.sourceless.any"), true);
		}

		return collection.size();
	}
}
