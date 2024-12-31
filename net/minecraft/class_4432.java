package net.minecraft;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;

public class class_4432 {
	private static final SimpleCommandExceptionType field_21792 = new SimpleCommandExceptionType(new TranslatableText("commands.tag.add.failed"));
	private static final SimpleCommandExceptionType field_21793 = new SimpleCommandExceptionType(new TranslatableText("commands.tag.remove.failed"));

	public static void method_21049(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("tag").requires(arg -> arg.method_17575(2)))
				.then(
					((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530("targets", class_4062.method_17899())
								.then(
									CommandManager.method_17529("add")
										.then(
											CommandManager.method_17530("name", StringArgumentType.word())
												.executes(
													commandContext -> method_21048(
															(class_3915)commandContext.getSource(), class_4062.method_17901(commandContext, "targets"), StringArgumentType.getString(commandContext, "name")
														)
												)
										)
								))
							.then(
								CommandManager.method_17529("remove")
									.then(
										CommandManager.method_17530("name", StringArgumentType.word())
											.suggests(
												(commandContext, suggestionsBuilder) -> class_3965.method_17571(
														method_21052(class_4062.method_17901(commandContext, "targets")), suggestionsBuilder
													)
											)
											.executes(
												commandContext -> method_21053(
														(class_3915)commandContext.getSource(), class_4062.method_17901(commandContext, "targets"), StringArgumentType.getString(commandContext, "name")
													)
											)
									)
							))
						.then(
							CommandManager.method_17529("list")
								.executes(commandContext -> method_21047((class_3915)commandContext.getSource(), class_4062.method_17901(commandContext, "targets")))
						)
				)
		);
	}

	private static Collection<String> method_21052(Collection<? extends Entity> collection) {
		Set<String> set = Sets.newHashSet();

		for (Entity entity : collection) {
			set.addAll(entity.getScoreboardTags());
		}

		return set;
	}

	private static int method_21048(class_3915 arg, Collection<? extends Entity> collection, String string) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : collection) {
			if (entity.addScoreboardTag(string)) {
				i++;
			}
		}

		if (i == 0) {
			throw field_21792.create();
		} else {
			if (collection.size() == 1) {
				arg.method_17459(new TranslatableText("commands.tag.add.success.single", string, ((Entity)collection.iterator().next()).getName()), true);
			} else {
				arg.method_17459(new TranslatableText("commands.tag.add.success.multiple", string, collection.size()), true);
			}

			return i;
		}
	}

	private static int method_21053(class_3915 arg, Collection<? extends Entity> collection, String string) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : collection) {
			if (entity.removeScoreboardTag(string)) {
				i++;
			}
		}

		if (i == 0) {
			throw field_21793.create();
		} else {
			if (collection.size() == 1) {
				arg.method_17459(new TranslatableText("commands.tag.remove.success.single", string, ((Entity)collection.iterator().next()).getName()), true);
			} else {
				arg.method_17459(new TranslatableText("commands.tag.remove.success.multiple", string, collection.size()), true);
			}

			return i;
		}
	}

	private static int method_21047(class_3915 arg, Collection<? extends Entity> collection) {
		Set<String> set = Sets.newHashSet();

		for (Entity entity : collection) {
			set.addAll(entity.getScoreboardTags());
		}

		if (collection.size() == 1) {
			Entity entity2 = (Entity)collection.iterator().next();
			if (set.isEmpty()) {
				arg.method_17459(new TranslatableText("commands.tag.list.single.empty", entity2.getName()), false);
			} else {
				arg.method_17459(new TranslatableText("commands.tag.list.single.success", entity2.getName(), set.size(), ChatSerializer.method_20191(set)), false);
			}
		} else if (set.isEmpty()) {
			arg.method_17459(new TranslatableText("commands.tag.list.multiple.empty", collection.size()), false);
		} else {
			arg.method_17459(new TranslatableText("commands.tag.list.multiple.success", collection.size(), set.size(), ChatSerializer.method_20191(set)), false);
		}

		return set.size();
	}
}
