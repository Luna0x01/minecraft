package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;

public class class_4404 {
	private static final SuggestionProvider<class_3915> field_21681 = (commandContext, suggestionsBuilder) -> {
		Collection<SimpleAdvancement> collection = ((class_3915)commandContext.getSource()).method_17473().method_14910().method_20451();
		return class_3965.method_17566(collection.stream().map(SimpleAdvancement::getIdentifier), suggestionsBuilder);
	};

	public static void method_20489(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("advancement").requires(arg -> arg.method_17575(2)))
					.then(
						CommandManager.method_17529("grant")
							.then(
								((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530(
														"targets", class_4062.method_17904()
													)
													.then(
														CommandManager.method_17529("only")
															.then(
																((RequiredArgumentBuilder)CommandManager.method_17530("advancement", class_4181.method_18904())
																		.suggests(field_21681)
																		.executes(
																			commandContext -> method_20487(
																					(class_3915)commandContext.getSource(),
																					class_4062.method_17907(commandContext, "targets"),
																					class_4404.class_3293.GRANT,
																					method_20493(class_4181.method_18906(commandContext, "advancement"), class_4404.class_4405.ONLY)
																				)
																		))
																	.then(
																		CommandManager.method_17530("criterion", StringArgumentType.greedyString())
																			.suggests(
																				(commandContext, suggestionsBuilder) -> class_3965.method_17571(
																						class_4181.method_18906(commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder
																					)
																			)
																			.executes(
																				commandContext -> method_20488(
																						(class_3915)commandContext.getSource(),
																						class_4062.method_17907(commandContext, "targets"),
																						class_4404.class_3293.GRANT,
																						class_4181.method_18906(commandContext, "advancement"),
																						StringArgumentType.getString(commandContext, "criterion")
																					)
																			)
																	)
															)
													))
												.then(
													CommandManager.method_17529("from")
														.then(
															CommandManager.method_17530("advancement", class_4181.method_18904())
																.suggests(field_21681)
																.executes(
																	commandContext -> method_20487(
																			(class_3915)commandContext.getSource(),
																			class_4062.method_17907(commandContext, "targets"),
																			class_4404.class_3293.GRANT,
																			method_20493(class_4181.method_18906(commandContext, "advancement"), class_4404.class_4405.FROM)
																		)
																)
														)
												))
											.then(
												CommandManager.method_17529("until")
													.then(
														CommandManager.method_17530("advancement", class_4181.method_18904())
															.suggests(field_21681)
															.executes(
																commandContext -> method_20487(
																		(class_3915)commandContext.getSource(),
																		class_4062.method_17907(commandContext, "targets"),
																		class_4404.class_3293.GRANT,
																		method_20493(class_4181.method_18906(commandContext, "advancement"), class_4404.class_4405.UNTIL)
																	)
															)
													)
											))
										.then(
											CommandManager.method_17529("through")
												.then(
													CommandManager.method_17530("advancement", class_4181.method_18904())
														.suggests(field_21681)
														.executes(
															commandContext -> method_20487(
																	(class_3915)commandContext.getSource(),
																	class_4062.method_17907(commandContext, "targets"),
																	class_4404.class_3293.GRANT,
																	method_20493(class_4181.method_18906(commandContext, "advancement"), class_4404.class_4405.THROUGH)
																)
														)
												)
										))
									.then(
										CommandManager.method_17529("everything")
											.executes(
												commandContext -> method_20487(
														(class_3915)commandContext.getSource(),
														class_4062.method_17907(commandContext, "targets"),
														class_4404.class_3293.GRANT,
														((class_3915)commandContext.getSource()).method_17473().method_14910().method_20451()
													)
											)
									)
							)
					))
				.then(
					CommandManager.method_17529("revoke")
						.then(
							((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530(
													"targets", class_4062.method_17904()
												)
												.then(
													CommandManager.method_17529("only")
														.then(
															((RequiredArgumentBuilder)CommandManager.method_17530("advancement", class_4181.method_18904())
																	.suggests(field_21681)
																	.executes(
																		commandContext -> method_20487(
																				(class_3915)commandContext.getSource(),
																				class_4062.method_17907(commandContext, "targets"),
																				class_4404.class_3293.REVOKE,
																				method_20493(class_4181.method_18906(commandContext, "advancement"), class_4404.class_4405.ONLY)
																			)
																	))
																.then(
																	CommandManager.method_17530("criterion", StringArgumentType.greedyString())
																		.suggests(
																			(commandContext, suggestionsBuilder) -> class_3965.method_17571(
																					class_4181.method_18906(commandContext, "advancement").getCriteria().keySet(), suggestionsBuilder
																				)
																		)
																		.executes(
																			commandContext -> method_20488(
																					(class_3915)commandContext.getSource(),
																					class_4062.method_17907(commandContext, "targets"),
																					class_4404.class_3293.REVOKE,
																					class_4181.method_18906(commandContext, "advancement"),
																					StringArgumentType.getString(commandContext, "criterion")
																				)
																		)
																)
														)
												))
											.then(
												CommandManager.method_17529("from")
													.then(
														CommandManager.method_17530("advancement", class_4181.method_18904())
															.suggests(field_21681)
															.executes(
																commandContext -> method_20487(
																		(class_3915)commandContext.getSource(),
																		class_4062.method_17907(commandContext, "targets"),
																		class_4404.class_3293.REVOKE,
																		method_20493(class_4181.method_18906(commandContext, "advancement"), class_4404.class_4405.FROM)
																	)
															)
													)
											))
										.then(
											CommandManager.method_17529("until")
												.then(
													CommandManager.method_17530("advancement", class_4181.method_18904())
														.suggests(field_21681)
														.executes(
															commandContext -> method_20487(
																	(class_3915)commandContext.getSource(),
																	class_4062.method_17907(commandContext, "targets"),
																	class_4404.class_3293.REVOKE,
																	method_20493(class_4181.method_18906(commandContext, "advancement"), class_4404.class_4405.UNTIL)
																)
														)
												)
										))
									.then(
										CommandManager.method_17529("through")
											.then(
												CommandManager.method_17530("advancement", class_4181.method_18904())
													.suggests(field_21681)
													.executes(
														commandContext -> method_20487(
																(class_3915)commandContext.getSource(),
																class_4062.method_17907(commandContext, "targets"),
																class_4404.class_3293.REVOKE,
																method_20493(class_4181.method_18906(commandContext, "advancement"), class_4404.class_4405.THROUGH)
															)
													)
											)
									))
								.then(
									CommandManager.method_17529("everything")
										.executes(
											commandContext -> method_20487(
													(class_3915)commandContext.getSource(),
													class_4062.method_17907(commandContext, "targets"),
													class_4404.class_3293.REVOKE,
													((class_3915)commandContext.getSource()).method_17473().method_14910().method_20451()
												)
										)
								)
						)
				)
		);
	}

	private static int method_20487(
		class_3915 arg, Collection<ServerPlayerEntity> collection, class_4404.class_3293 arg2, Collection<SimpleAdvancement> collection2
	) {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			i += arg2.method_14659(serverPlayerEntity, collection2);
		}

		if (i == 0) {
			if (collection2.size() == 1) {
				if (collection.size() == 1) {
					throw new CommandException(
						new TranslatableText(
							arg2.method_20507() + ".one.to.one.failure",
							((SimpleAdvancement)collection2.iterator().next()).method_14803(),
							((ServerPlayerEntity)collection.iterator().next()).getName()
						)
					);
				} else {
					throw new CommandException(
						new TranslatableText(arg2.method_20507() + ".one.to.many.failure", ((SimpleAdvancement)collection2.iterator().next()).method_14803(), collection.size())
					);
				}
			} else if (collection.size() == 1) {
				throw new CommandException(
					new TranslatableText(arg2.method_20507() + ".many.to.one.failure", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).getName())
				);
			} else {
				throw new CommandException(new TranslatableText(arg2.method_20507() + ".many.to.many.failure", collection2.size(), collection.size()));
			}
		} else {
			if (collection2.size() == 1) {
				if (collection.size() == 1) {
					arg.method_17459(
						new TranslatableText(
							arg2.method_20507() + ".one.to.one.success",
							((SimpleAdvancement)collection2.iterator().next()).method_14803(),
							((ServerPlayerEntity)collection.iterator().next()).getName()
						),
						true
					);
				} else {
					arg.method_17459(
						new TranslatableText(arg2.method_20507() + ".one.to.many.success", ((SimpleAdvancement)collection2.iterator().next()).method_14803(), collection.size()),
						true
					);
				}
			} else if (collection.size() == 1) {
				arg.method_17459(
					new TranslatableText(arg2.method_20507() + ".many.to.one.success", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).getName()), true
				);
			} else {
				arg.method_17459(new TranslatableText(arg2.method_20507() + ".many.to.many.success", collection2.size(), collection.size()), true);
			}

			return i;
		}
	}

	private static int method_20488(
		class_3915 arg, Collection<ServerPlayerEntity> collection, class_4404.class_3293 arg2, SimpleAdvancement simpleAdvancement, String string
	) {
		int i = 0;
		if (!simpleAdvancement.getCriteria().containsKey(string)) {
			throw new CommandException(new TranslatableText("commands.advancement.criterionNotFound", simpleAdvancement.method_14803(), string));
		} else {
			for (ServerPlayerEntity serverPlayerEntity : collection) {
				if (arg2.method_14658(serverPlayerEntity, simpleAdvancement, string)) {
					i++;
				}
			}

			if (i == 0) {
				if (collection.size() == 1) {
					throw new CommandException(
						new TranslatableText(
							arg2.method_20507() + ".criterion.to.one.failure",
							string,
							simpleAdvancement.method_14803(),
							((ServerPlayerEntity)collection.iterator().next()).getName()
						)
					);
				} else {
					throw new CommandException(
						new TranslatableText(arg2.method_20507() + ".criterion.to.many.failure", string, simpleAdvancement.method_14803(), collection.size())
					);
				}
			} else {
				if (collection.size() == 1) {
					arg.method_17459(
						new TranslatableText(
							arg2.method_20507() + ".criterion.to.one.success",
							string,
							simpleAdvancement.method_14803(),
							((ServerPlayerEntity)collection.iterator().next()).getName()
						),
						true
					);
				} else {
					arg.method_17459(
						new TranslatableText(arg2.method_20507() + ".criterion.to.many.success", string, simpleAdvancement.method_14803(), collection.size()), true
					);
				}

				return i;
			}
		}
	}

	private static List<SimpleAdvancement> method_20493(SimpleAdvancement simpleAdvancement, class_4404.class_4405 arg) {
		List<SimpleAdvancement> list = Lists.newArrayList();
		if (arg.field_21687) {
			for (SimpleAdvancement simpleAdvancement2 = simpleAdvancement.getParent(); simpleAdvancement2 != null; simpleAdvancement2 = simpleAdvancement2.getParent()) {
				list.add(simpleAdvancement2);
			}
		}

		list.add(simpleAdvancement);
		if (arg.field_21688) {
			method_20492(simpleAdvancement, list);
		}

		return list;
	}

	private static void method_20492(SimpleAdvancement simpleAdvancement, List<SimpleAdvancement> list) {
		for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
			list.add(simpleAdvancement2);
			method_20492(simpleAdvancement2, list);
		}
	}

	static enum class_3293 {
		GRANT("grant") {
			@Override
			protected boolean method_14657(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement) {
				AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementFile().method_14923(simpleAdvancement);
				if (advancementProgress.method_14833()) {
					return false;
				} else {
					for (String string : advancementProgress.method_14845()) {
						serverPlayerEntity.getAdvancementFile().method_14919(simpleAdvancement, string);
					}

					return true;
				}
			}

			@Override
			protected boolean method_14658(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement, String string) {
				return serverPlayerEntity.getAdvancementFile().method_14919(simpleAdvancement, string);
			}
		},
		REVOKE("revoke") {
			@Override
			protected boolean method_14657(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement) {
				AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementFile().method_14923(simpleAdvancement);
				if (!advancementProgress.method_14838()) {
					return false;
				} else {
					for (String string : advancementProgress.method_14846()) {
						serverPlayerEntity.getAdvancementFile().method_14924(simpleAdvancement, string);
					}

					return true;
				}
			}

			@Override
			protected boolean method_14658(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement, String string) {
				return serverPlayerEntity.getAdvancementFile().method_14924(simpleAdvancement, string);
			}
		};

		private final String field_16114;

		private class_3293(String string2) {
			this.field_16114 = "commands.advancement." + string2;
		}

		public int method_14659(ServerPlayerEntity serverPlayerEntity, Iterable<SimpleAdvancement> iterable) {
			int i = 0;

			for (SimpleAdvancement simpleAdvancement : iterable) {
				if (this.method_14657(serverPlayerEntity, simpleAdvancement)) {
					i++;
				}
			}

			return i;
		}

		protected abstract boolean method_14657(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement);

		protected abstract boolean method_14658(ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement, String string);

		protected String method_20507() {
			return this.field_16114;
		}
	}

	static enum class_4405 {
		ONLY(false, false),
		THROUGH(true, true),
		FROM(false, true),
		UNTIL(true, false),
		EVERYTHING(true, true);

		private final boolean field_21687;
		private final boolean field_21688;

		private class_4405(boolean bl, boolean bl2) {
			this.field_21687 = bl;
			this.field_21688 = bl2;
		}
	}
}
