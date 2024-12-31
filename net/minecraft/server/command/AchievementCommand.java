package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class AchievementCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "advancement";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.advancement.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.advancement.usage");
		} else {
			AchievementCommand.class_3293 lv = AchievementCommand.class_3293.method_14656(args[0]);
			if (lv != null) {
				if (args.length < 3) {
					throw lv.method_14655();
				}

				ServerPlayerEntity serverPlayerEntity = method_4639(minecraftServer, commandSource, args[1]);
				AchievementCommand.class_3294 lv2 = AchievementCommand.class_3294.method_14663(args[2]);
				if (lv2 == null) {
					throw lv.method_14655();
				}

				this.method_14653(minecraftServer, commandSource, args, serverPlayerEntity, lv, lv2);
			} else {
				if (!"test".equals(args[0])) {
					throw new IncorrectUsageException("commands.advancement.usage");
				}

				if (args.length == 3) {
					this.method_14648(commandSource, method_4639(minecraftServer, commandSource, args[1]), method_14654(minecraftServer, args[2]));
				} else {
					if (args.length != 4) {
						throw new IncorrectUsageException("commands.advancement.test.usage");
					}

					this.method_14649(commandSource, method_4639(minecraftServer, commandSource, args[1]), method_14654(minecraftServer, args[2]), args[3]);
				}
			}
		}
	}

	private void method_14653(
		MinecraftServer minecraftServer,
		CommandSource commandSource,
		String[] strings,
		ServerPlayerEntity serverPlayerEntity,
		AchievementCommand.class_3293 arg,
		AchievementCommand.class_3294 arg2
	) throws CommandException {
		if (arg2 == AchievementCommand.class_3294.EVERYTHING) {
			if (strings.length == 3) {
				int i = arg.method_14659(serverPlayerEntity, minecraftServer.method_14910().method_14940());
				if (i == 0) {
					throw arg2.method_14662(arg, serverPlayerEntity.getTranslationKey());
				} else {
					arg2.method_14660(commandSource, this, arg, serverPlayerEntity.getTranslationKey(), i);
				}
			} else {
				throw arg2.method_14661(arg);
			}
		} else if (strings.length < 4) {
			throw arg2.method_14661(arg);
		} else {
			SimpleAdvancement simpleAdvancement = method_14654(minecraftServer, strings[3]);
			if (arg2 == AchievementCommand.class_3294.ONLY && strings.length == 5) {
				String string = strings[4];
				if (!simpleAdvancement.getCriteria().keySet().contains(string)) {
					throw new CommandException("commands.advancement.criterionNotFound", simpleAdvancement.getIdentifier(), strings[4]);
				}

				if (!arg.method_14658(serverPlayerEntity, simpleAdvancement, string)) {
					throw new CommandException(arg.field_16114 + ".criterion.failed", simpleAdvancement.getIdentifier(), serverPlayerEntity.getTranslationKey(), string);
				}

				run(
					commandSource,
					this,
					arg.field_16114 + ".criterion.success",
					new Object[]{simpleAdvancement.getIdentifier(), serverPlayerEntity.getTranslationKey(), string}
				);
			} else {
				if (strings.length != 4) {
					throw arg2.method_14661(arg);
				}

				List<SimpleAdvancement> list = this.method_14650(simpleAdvancement, arg2);
				int j = arg.method_14659(serverPlayerEntity, list);
				if (j == 0) {
					throw arg2.method_14662(arg, simpleAdvancement.getIdentifier(), serverPlayerEntity.getTranslationKey());
				}

				arg2.method_14660(commandSource, this, arg, simpleAdvancement.getIdentifier(), serverPlayerEntity.getTranslationKey(), j);
			}
		}
	}

	private void method_14651(SimpleAdvancement simpleAdvancement, List<SimpleAdvancement> list) {
		for (SimpleAdvancement simpleAdvancement2 : simpleAdvancement.getChildren()) {
			list.add(simpleAdvancement2);
			this.method_14651(simpleAdvancement2, list);
		}
	}

	private List<SimpleAdvancement> method_14650(SimpleAdvancement simpleAdvancement, AchievementCommand.class_3294 arg) {
		List<SimpleAdvancement> list = Lists.newArrayList();
		if (arg.field_16123) {
			for (SimpleAdvancement simpleAdvancement2 = simpleAdvancement.getParent(); simpleAdvancement2 != null; simpleAdvancement2 = simpleAdvancement2.getParent()) {
				list.add(simpleAdvancement2);
			}
		}

		list.add(simpleAdvancement);
		if (arg.field_16124) {
			this.method_14651(simpleAdvancement, list);
		}

		return list;
	}

	private void method_14649(CommandSource commandSource, ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement, String string) throws CommandException {
		AdvancementFile advancementFile = serverPlayerEntity.getAdvancementFile();
		CriterionProgress criterionProgress = advancementFile.method_14923(simpleAdvancement).getCriteria(string);
		if (criterionProgress == null) {
			throw new CommandException("commands.advancement.criterionNotFound", simpleAdvancement.getIdentifier(), string);
		} else if (!criterionProgress.hasBeenObtained()) {
			throw new CommandException("commands.advancement.test.criterion.notDone", serverPlayerEntity.getTranslationKey(), simpleAdvancement.getIdentifier(), string);
		} else {
			run(
				commandSource,
				this,
				"commands.advancement.test.criterion.success",
				new Object[]{serverPlayerEntity.getTranslationKey(), simpleAdvancement.getIdentifier(), string}
			);
		}
	}

	private void method_14648(CommandSource commandSource, ServerPlayerEntity serverPlayerEntity, SimpleAdvancement simpleAdvancement) throws CommandException {
		AdvancementProgress advancementProgress = serverPlayerEntity.getAdvancementFile().method_14923(simpleAdvancement);
		if (!advancementProgress.method_14833()) {
			throw new CommandException("commands.advancement.test.advancement.notDone", serverPlayerEntity.getTranslationKey(), simpleAdvancement.getIdentifier());
		} else {
			run(
				commandSource,
				this,
				"commands.advancement.test.advancement.success",
				new Object[]{serverPlayerEntity.getTranslationKey(), simpleAdvancement.getIdentifier()}
			);
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, new String[]{"grant", "revoke", "test"});
		} else {
			AchievementCommand.class_3293 lv = AchievementCommand.class_3293.method_14656(strings[0]);
			if (lv != null) {
				if (strings.length == 2) {
					return method_2894(strings, server.getPlayerNames());
				}

				if (strings.length == 3) {
					return method_2894(strings, AchievementCommand.class_3294.field_16121);
				}

				AchievementCommand.class_3294 lv2 = AchievementCommand.class_3294.method_14663(strings[2]);
				if (lv2 != null && lv2 != AchievementCommand.class_3294.EVERYTHING) {
					if (strings.length == 4) {
						return method_10708(strings, this.method_14652(server));
					}

					if (strings.length == 5 && lv2 == AchievementCommand.class_3294.ONLY) {
						SimpleAdvancement simpleAdvancement = server.method_14910().method_14938(new Identifier(strings[3]));
						if (simpleAdvancement != null) {
							return method_10708(strings, simpleAdvancement.getCriteria().keySet());
						}
					}
				}
			}

			if ("test".equals(strings[0])) {
				if (strings.length == 2) {
					return method_2894(strings, server.getPlayerNames());
				}

				if (strings.length == 3) {
					return method_10708(strings, this.method_14652(server));
				}

				if (strings.length == 4) {
					SimpleAdvancement simpleAdvancement2 = server.method_14910().method_14938(new Identifier(strings[2]));
					if (simpleAdvancement2 != null) {
						return method_10708(strings, simpleAdvancement2.getCriteria().keySet());
					}
				}
			}

			return Collections.emptyList();
		}
	}

	private List<Identifier> method_14652(MinecraftServer minecraftServer) {
		List<Identifier> list = Lists.newArrayList();

		for (SimpleAdvancement simpleAdvancement : minecraftServer.method_14910().method_14940()) {
			list.add(simpleAdvancement.getIdentifier());
		}

		return list;
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return args.length > 1 && ("grant".equals(args[0]) || "revoke".equals(args[0]) || "test".equals(args[0])) && index == 1;
	}

	public static SimpleAdvancement method_14654(MinecraftServer minecraftServer, String string) throws CommandException {
		SimpleAdvancement simpleAdvancement = minecraftServer.method_14910().method_14938(new Identifier(string));
		if (simpleAdvancement == null) {
			throw new CommandException("commands.advancement.advancementNotFound", string);
		} else {
			return simpleAdvancement;
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

		final String field_16113;
		final String field_16114;

		private class_3293(String string2) {
			this.field_16113 = string2;
			this.field_16114 = "commands.advancement." + string2;
		}

		@Nullable
		static AchievementCommand.class_3293 method_14656(String string) {
			for (AchievementCommand.class_3293 lv : values()) {
				if (lv.field_16113.equals(string)) {
					return lv;
				}
			}

			return null;
		}

		CommandException method_14655() {
			return new CommandException(this.field_16114 + ".usage");
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
	}

	static enum class_3294 {
		ONLY("only", false, false),
		THROUGH("through", true, true),
		FROM("from", false, true),
		UNTIL("until", true, false),
		EVERYTHING("everything", true, true);

		static final String[] field_16121 = new String[values().length];
		final String field_16122;
		final boolean field_16123;
		final boolean field_16124;

		private class_3294(String string2, boolean bl, boolean bl2) {
			this.field_16122 = string2;
			this.field_16123 = bl;
			this.field_16124 = bl2;
		}

		CommandException method_14662(AchievementCommand.class_3293 arg, Object... objects) {
			return new CommandException(arg.field_16114 + "." + this.field_16122 + ".failed", objects);
		}

		CommandException method_14661(AchievementCommand.class_3293 arg) {
			return new CommandException(arg.field_16114 + "." + this.field_16122 + ".usage");
		}

		void method_14660(CommandSource commandSource, AchievementCommand achievementCommand, AchievementCommand.class_3293 arg, Object... objects) {
			AbstractCommand.run(commandSource, achievementCommand, arg.field_16114 + "." + this.field_16122 + ".success", objects);
		}

		@Nullable
		static AchievementCommand.class_3294 method_14663(String string) {
			for (AchievementCommand.class_3294 lv : values()) {
				if (lv.field_16122.equals(string)) {
					return lv;
				}
			}

			return null;
		}

		static {
			for (int i = 0; i < values().length; i++) {
				field_16121[i] = values()[i].field_16122;
			}
		}
	}
}
