package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

public class class_4412 {
	private static final DynamicCommandExceptionType field_21715 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.datapack.unknown", object)
	);
	private static final DynamicCommandExceptionType field_21716 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.datapack.enable.failed", object)
	);
	private static final DynamicCommandExceptionType field_21717 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.datapack.disable.failed", object)
	);
	private static final SuggestionProvider<class_3915> field_21718 = (commandContext, suggestionsBuilder) -> class_3965.method_17573(
			((class_3915)commandContext.getSource())
				.method_17473()
				.method_20327()
				.method_21354()
				.stream()
				.map(class_4465::method_21365)
				.map(StringArgumentType::escapeIfRequired),
			suggestionsBuilder
		);
	private static final SuggestionProvider<class_3915> field_21719 = (commandContext, suggestionsBuilder) -> class_3965.method_17573(
			((class_3915)commandContext.getSource())
				.method_17473()
				.method_20327()
				.method_21353()
				.stream()
				.map(class_4465::method_21365)
				.map(StringArgumentType::escapeIfRequired),
			suggestionsBuilder
		);

	public static void method_20612(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("datapack")
							.requires(arg -> arg.method_17575(2)))
						.then(
							CommandManager.method_17529("enable")
								.then(
									((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530(
															"name", StringArgumentType.string()
														)
														.suggests(field_21719)
														.executes(
															commandContext -> method_20610(
																	(class_3915)commandContext.getSource(),
																	method_20615(commandContext, "name", true),
																	(list, arg) -> arg.method_21368().method_21370(list, arg, argx -> argx, false)
																)
														))
													.then(
														CommandManager.method_17529("after")
															.then(
																CommandManager.method_17530("existing", StringArgumentType.string())
																	.suggests(field_21718)
																	.executes(
																		commandContext -> method_20610(
																				(class_3915)commandContext.getSource(),
																				method_20615(commandContext, "name", true),
																				(list, arg) -> list.add(list.indexOf(method_20615(commandContext, "existing", false)) + 1, arg)
																			)
																	)
															)
													))
												.then(
													CommandManager.method_17529("before")
														.then(
															CommandManager.method_17530("existing", StringArgumentType.string())
																.suggests(field_21718)
																.executes(
																	commandContext -> method_20610(
																			(class_3915)commandContext.getSource(),
																			method_20615(commandContext, "name", true),
																			(list, arg) -> list.add(list.indexOf(method_20615(commandContext, "existing", false)), arg)
																		)
																)
														)
												))
											.then(
												CommandManager.method_17529("last")
													.executes(commandContext -> method_20610((class_3915)commandContext.getSource(), method_20615(commandContext, "name", true), List::add))
											))
										.then(
											CommandManager.method_17529("first")
												.executes(
													commandContext -> method_20610((class_3915)commandContext.getSource(), method_20615(commandContext, "name", true), (list, arg) -> list.add(0, arg))
												)
										)
								)
						))
					.then(
						CommandManager.method_17529("disable")
							.then(
								CommandManager.method_17530("name", StringArgumentType.string())
									.suggests(field_21718)
									.executes(commandContext -> method_20609((class_3915)commandContext.getSource(), method_20615(commandContext, "name", false)))
							)
					))
				.then(
					((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("list")
								.executes(commandContext -> method_20608((class_3915)commandContext.getSource())))
							.then(CommandManager.method_17529("available").executes(commandContext -> method_20620((class_3915)commandContext.getSource()))))
						.then(CommandManager.method_17529("enabled").executes(commandContext -> method_20628((class_3915)commandContext.getSource())))
				)
		);
	}

	private static int method_20610(class_3915 arg, class_4465 arg2, class_4412.class_4413 arg3) throws CommandSyntaxException {
		class_4462<class_4465> lv = arg.method_17473().method_20327();
		List<class_4465> list = Lists.newArrayList(lv.method_21354());
		arg3.apply(list, arg2);
		lv.method_21349(list);
		LevelProperties levelProperties = arg.method_17473().method_20312(DimensionType.OVERWORLD).method_3588();
		levelProperties.method_17952().clear();
		lv.method_21354().forEach(argx -> levelProperties.method_17952().add(argx.method_21365()));
		levelProperties.method_17951().remove(arg2.method_21365());
		arg.method_17459(new TranslatableText("commands.datapack.enable.success", arg2.method_21360(true)), true);
		arg.method_17473().method_14912();
		return lv.method_21354().size();
	}

	private static int method_20609(class_3915 arg, class_4465 arg2) {
		class_4462<class_4465> lv = arg.method_17473().method_20327();
		List<class_4465> list = Lists.newArrayList(lv.method_21354());
		list.remove(arg2);
		lv.method_21349(list);
		LevelProperties levelProperties = arg.method_17473().method_20312(DimensionType.OVERWORLD).method_3588();
		levelProperties.method_17952().clear();
		lv.method_21354().forEach(argx -> levelProperties.method_17952().add(argx.method_21365()));
		levelProperties.method_17951().add(arg2.method_21365());
		arg.method_17459(new TranslatableText("commands.datapack.disable.success", arg2.method_21360(true)), true);
		arg.method_17473().method_14912();
		return lv.method_21354().size();
	}

	private static int method_20608(class_3915 arg) {
		return method_20628(arg) + method_20620(arg);
	}

	private static int method_20620(class_3915 arg) {
		class_4462<class_4465> lv = arg.method_17473().method_20327();
		if (lv.method_21353().isEmpty()) {
			arg.method_17459(new TranslatableText("commands.datapack.list.available.none"), false);
		} else {
			arg.method_17459(
				new TranslatableText(
					"commands.datapack.list.available.success", lv.method_21353().size(), ChatSerializer.method_20193(lv.method_21353(), argx -> argx.method_21360(false))
				),
				false
			);
		}

		return lv.method_21353().size();
	}

	private static int method_20628(class_3915 arg) {
		class_4462<class_4465> lv = arg.method_17473().method_20327();
		if (lv.method_21354().isEmpty()) {
			arg.method_17459(new TranslatableText("commands.datapack.list.enabled.none"), false);
		} else {
			arg.method_17459(
				new TranslatableText(
					"commands.datapack.list.enabled.success", lv.method_21354().size(), ChatSerializer.method_20193(lv.method_21354(), argx -> argx.method_21360(true))
				),
				false
			);
		}

		return lv.method_21354().size();
	}

	private static class_4465 method_20615(CommandContext<class_3915> commandContext, String string, boolean bl) throws CommandSyntaxException {
		String string2 = StringArgumentType.getString(commandContext, string);
		class_4462<class_4465> lv = ((class_3915)commandContext.getSource()).method_17473().method_20327();
		class_4465 lv2 = lv.method_21348(string2);
		if (lv2 == null) {
			throw field_21715.create(string2);
		} else {
			boolean bl2 = lv.method_21354().contains(lv2);
			if (bl && bl2) {
				throw field_21716.create(string2);
			} else if (!bl && !bl2) {
				throw field_21717.create(string2);
			} else {
				return lv2;
			}
		}
	}

	interface class_4413 {
		void apply(List<class_4465> list, class_4465 arg) throws CommandSyntaxException;
	}
}
