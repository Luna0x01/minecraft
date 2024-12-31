package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.achievement.class_3348;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

public class class_4319 {
	private static final Map<String, class_4319.class_4321> field_21242 = Maps.newHashMap();
	public static final DynamicCommandExceptionType field_21234 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.entity.options.unknown", object)
	);
	public static final DynamicCommandExceptionType field_21235 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.entity.options.inapplicable", object)
	);
	public static final SimpleCommandExceptionType field_21236 = new SimpleCommandExceptionType(new TranslatableText("argument.entity.options.distance.negative"));
	public static final SimpleCommandExceptionType field_21237 = new SimpleCommandExceptionType(new TranslatableText("argument.entity.options.level.negative"));
	public static final SimpleCommandExceptionType field_21238 = new SimpleCommandExceptionType(new TranslatableText("argument.entity.options.limit.toosmall"));
	public static final DynamicCommandExceptionType field_21239 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.entity.options.sort.irreversible", object)
	);
	public static final DynamicCommandExceptionType field_21240 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.entity.options.mode.invalid", object)
	);
	public static final DynamicCommandExceptionType field_21241 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.entity.options.type.invalid", object)
	);

	private static void method_19849(String string, class_4319.class_4320 arg, Predicate<class_4318> predicate, Text text) {
		field_21242.put(string, new class_4319.class_4321(arg, predicate, text));
	}

	public static void method_19840() {
		if (field_21242.isEmpty()) {
			method_19849("name", arg -> {
				int i = arg.method_19794().getCursor();
				boolean bl = arg.method_19790();
				String string = arg.method_19794().readString();
				if (arg.method_19821() && !bl) {
					arg.method_19794().setCursor(i);
					throw field_21235.createWithContext(arg.method_19794(), "name");
				} else {
					if (bl) {
						arg.method_19789(true);
					} else {
						arg.method_19783(true);
					}

					arg.method_19766(entity -> entity.method_15540().computeValue().equals(string) != bl);
				}
			}, arg -> !arg.method_19820(), new TranslatableText("argument.entity.options.name.description"));
			method_19849("distance", arg -> {
				int i = arg.method_19794().getCursor();
				class_3638.class_3641 lv = class_3638.class_3641.method_16516(arg.method_19794());
				if ((lv.method_16505() == null || !((Float)lv.method_16505() < 0.0F)) && (lv.method_16511() == null || !((Float)lv.method_16511() < 0.0F))) {
					arg.method_19753(lv);
					arg.method_19798();
				} else {
					arg.method_19794().setCursor(i);
					throw field_21236.createWithContext(arg.method_19794());
				}
			}, arg -> arg.method_19801().method_16512(), new TranslatableText("argument.entity.options.distance.description"));
			method_19849("level", arg -> {
				int i = arg.method_19794().getCursor();
				class_3638.class_3642 lv = class_3638.class_3642.method_16525(arg.method_19794());
				if ((lv.method_16505() == null || (Integer)lv.method_16505() >= 0) && (lv.method_16511() == null || (Integer)lv.method_16511() >= 0)) {
					arg.method_19754(lv);
					arg.method_19768(false);
				} else {
					arg.method_19794().setCursor(i);
					throw field_21237.createWithContext(arg.method_19794());
				}
			}, arg -> arg.method_19804().method_16512(), new TranslatableText("argument.entity.options.level.description"));
			method_19849("x", arg -> {
				arg.method_19798();
				arg.method_19749(arg.method_19794().readDouble());
			}, arg -> arg.method_19811() == null, new TranslatableText("argument.entity.options.x.description"));
			method_19849("y", arg -> {
				arg.method_19798();
				arg.method_19770(arg.method_19794().readDouble());
			}, arg -> arg.method_19813() == null, new TranslatableText("argument.entity.options.y.description"));
			method_19849("z", arg -> {
				arg.method_19798();
				arg.method_19779(arg.method_19794().readDouble());
			}, arg -> arg.method_19814() == null, new TranslatableText("argument.entity.options.z.description"));
			method_19849("dx", arg -> {
				arg.method_19798();
				arg.method_19785(arg.method_19794().readDouble());
			}, arg -> arg.method_19815() == null, new TranslatableText("argument.entity.options.dx.description"));
			method_19849("dy", arg -> {
				arg.method_19798();
				arg.method_19791(arg.method_19794().readDouble());
			}, arg -> arg.method_19816() == null, new TranslatableText("argument.entity.options.dy.description"));
			method_19849("dz", arg -> {
				arg.method_19798();
				arg.method_19795(arg.method_19794().readDouble());
			}, arg -> arg.method_19817() == null, new TranslatableText("argument.entity.options.dz.description"));
			method_19849(
				"x_rotation",
				arg -> arg.method_19755(class_3783.method_17033(arg.method_19794(), true, MathHelper::wrapDegrees)),
				arg -> arg.method_19806() == class_3783.field_18843,
				new TranslatableText("argument.entity.options.x_rotation.description")
			);
			method_19849(
				"y_rotation",
				arg -> arg.method_19772(class_3783.method_17033(arg.method_19794(), true, MathHelper::wrapDegrees)),
				arg -> arg.method_19809() == class_3783.field_18843,
				new TranslatableText("argument.entity.options.y_rotation.description")
			);
			method_19849("limit", arg -> {
				int i = arg.method_19794().getCursor();
				int j = arg.method_19794().readInt();
				if (j < 1) {
					arg.method_19794().setCursor(i);
					throw field_21238.createWithContext(arg.method_19794());
				} else {
					arg.method_19751(j);
					arg.method_19793(true);
				}
			}, arg -> !arg.method_19819() && !arg.method_19822(), new TranslatableText("argument.entity.options.limit.description"));
			method_19849(
				"sort",
				arg -> {
					int i = arg.method_19794().getCursor();
					String string = arg.method_19794().readUnquotedString();
					arg.method_19765(
						(suggestionsBuilder, consumer) -> class_3965.method_17571(Arrays.asList("nearest", "furthest", "random", "arbitrary"), suggestionsBuilder)
					);
					BiConsumer<Vec3d, List<? extends Entity>> biConsumer;
					switch (string) {
						case "nearest":
							biConsumer = class_4318.field_21215;
							break;
						case "furthest":
							biConsumer = class_4318.field_21216;
							break;
						case "random":
							biConsumer = class_4318.field_21217;
							break;
						case "arbitrary":
							biConsumer = class_4318.field_21214;
							break;
						default:
							arg.method_19794().setCursor(i);
							throw field_21239.createWithContext(arg.method_19794(), string);
					}

					arg.method_19764(biConsumer);
					arg.method_19797(true);
				},
				arg -> !arg.method_19819() && !arg.method_19823(),
				new TranslatableText("argument.entity.options.sort.description")
			);
			method_19849("gamemode", arg -> {
				arg.method_19765((suggestionsBuilder, consumer) -> {
					String stringx = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
					boolean blx = !arg.method_19825();
					boolean bl2 = true;
					if (!stringx.isEmpty()) {
						if (stringx.charAt(0) == '!') {
							blx = false;
							stringx = stringx.substring(1);
						} else {
							bl2 = false;
						}
					}

					for (GameMode gameModex : GameMode.gameModes()) {
						if (gameModex != GameMode.NOT_SET && gameModex.getGameModeName().toLowerCase(Locale.ROOT).startsWith(stringx)) {
							if (bl2) {
								suggestionsBuilder.suggest('!' + gameModex.getGameModeName());
							}

							if (blx) {
								suggestionsBuilder.suggest(gameModex.getGameModeName());
							}
						}
					}

					return suggestionsBuilder.buildFuture();
				});
				int i = arg.method_19794().getCursor();
				boolean bl = arg.method_19790();
				if (arg.method_19825() && !bl) {
					arg.method_19794().setCursor(i);
					throw field_21235.createWithContext(arg.method_19794(), "gamemode");
				} else {
					String string = arg.method_19794().readUnquotedString();
					GameMode gameMode = GameMode.method_11495(string, GameMode.NOT_SET);
					if (gameMode == GameMode.NOT_SET) {
						arg.method_19794().setCursor(i);
						throw field_21240.createWithContext(arg.method_19794(), string);
					} else {
						arg.method_19768(false);
						arg.method_19766(entity -> {
							if (!(entity instanceof ServerPlayerEntity)) {
								return false;
							} else {
								GameMode gameMode2 = ((ServerPlayerEntity)entity).interactionManager.getGameMode();
								return bl ? gameMode2 != gameMode : gameMode2 == gameMode;
							}
						});
						if (bl) {
							arg.method_19803(true);
						} else {
							arg.method_19800(true);
						}
					}
				}
			}, arg -> !arg.method_19824(), new TranslatableText("argument.entity.options.gamemode.description"));
			method_19849("team", arg -> {
				boolean bl = arg.method_19790();
				String string = arg.method_19794().readUnquotedString();
				arg.method_19766(entity -> {
					if (!(entity instanceof LivingEntity)) {
						return false;
					} else {
						AbstractTeam abstractTeam = entity.getScoreboardTeam();
						String string2 = abstractTeam == null ? "" : abstractTeam.getName();
						return string2.equals(string) != bl;
					}
				});
				if (bl) {
					arg.method_19808(true);
				} else {
					arg.method_19805(true);
				}
			}, arg -> !arg.method_19741(), new TranslatableText("argument.entity.options.team.description"));
			method_19849("type", arg -> {
				arg.method_19765((suggestionsBuilder, consumer) -> {
					class_3965.method_17560(Registry.ENTITY_TYPE.getKeySet(), suggestionsBuilder, String.valueOf('!'));
					if (!arg.method_19744()) {
						class_3965.method_17559(Registry.ENTITY_TYPE.getKeySet(), suggestionsBuilder);
					}

					return suggestionsBuilder.buildFuture();
				});
				int i = arg.method_19794().getCursor();
				boolean bl = arg.method_19790();
				if (arg.method_19744() && !bl) {
					arg.method_19794().setCursor(i);
					throw field_21235.createWithContext(arg.method_19794(), "type");
				} else {
					Identifier identifier = Identifier.method_20442(arg.method_19794());
					EntityType<? extends Entity> entityType = (EntityType<? extends Entity>)Registry.ENTITY_TYPE.getByIdentifier(identifier);
					if (entityType == null) {
						arg.method_19794().setCursor(i);
						throw field_21241.createWithContext(arg.method_19794(), identifier.toString());
					} else {
						if (Objects.equals(EntityType.PLAYER, entityType) && !bl) {
							arg.method_19768(false);
						}

						arg.method_19766(entity -> Objects.equals(entityType, entity.method_15557()) != bl);
						if (bl) {
							arg.method_19742();
						} else {
							arg.method_19762(entityType.entityClass());
						}
					}
				}
			}, arg -> !arg.method_19743(), new TranslatableText("argument.entity.options.type.description"));
			method_19849("tag", arg -> {
				boolean bl = arg.method_19790();
				String string = arg.method_19794().readUnquotedString();
				arg.method_19766(entity -> "".equals(string) ? entity.getScoreboardTags().isEmpty() != bl : entity.getScoreboardTags().contains(string) != bl);
			}, arg -> true, new TranslatableText("argument.entity.options.tag.description"));
			method_19849("nbt", arg -> {
				boolean bl = arg.method_19790();
				NbtCompound nbtCompound = new StringNbtReader(arg.method_19794()).parseCompound();
				arg.method_19766(entity -> {
					NbtCompound nbtCompound2 = entity.toNbt(new NbtCompound());
					if (entity instanceof ServerPlayerEntity) {
						ItemStack itemStack = ((ServerPlayerEntity)entity).inventory.getMainHandStack();
						if (!itemStack.isEmpty()) {
							nbtCompound2.put("SelectedItem", itemStack.toNbt(new NbtCompound()));
						}
					}

					return NbtHelper.areEqual(nbtCompound, nbtCompound2, true) != bl;
				});
			}, arg -> true, new TranslatableText("argument.entity.options.nbt.description"));
			method_19849("scores", arg -> {
				StringReader stringReader = arg.method_19794();
				Map<String, class_3638.class_3642> map = Maps.newHashMap();
				stringReader.expect('{');
				stringReader.skipWhitespace();

				while (stringReader.canRead() && stringReader.peek() != '}') {
					stringReader.skipWhitespace();
					String string = stringReader.readUnquotedString();
					stringReader.skipWhitespace();
					stringReader.expect('=');
					stringReader.skipWhitespace();
					class_3638.class_3642 lv = class_3638.class_3642.method_16525(stringReader);
					map.put(string, lv);
					stringReader.skipWhitespace();
					if (stringReader.canRead() && stringReader.peek() == ',') {
						stringReader.skip();
					}
				}

				stringReader.expect('}');
				if (!map.isEmpty()) {
					arg.method_19766(entity -> {
						Scoreboard scoreboard = entity.method_12833().method_20333();
						String stringx = entity.method_15586();

						for (Entry<String, class_3638.class_3642> entry : map.entrySet()) {
							ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective((String)entry.getKey());
							if (scoreboardObjective == null) {
								return false;
							}

							if (!scoreboard.playerHasObjective(stringx, scoreboardObjective)) {
								return false;
							}

							ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(stringx, scoreboardObjective);
							int i = scoreboardPlayerScore.getScore();
							if (!((class_3638.class_3642)entry.getValue()).method_16531(i)) {
								return false;
							}
						}

						return true;
					});
				}

				arg.method_19810(true);
			}, arg -> !arg.method_19745(), new TranslatableText("argument.entity.options.scores.description"));
			method_19849("advancements", arg -> {
				StringReader stringReader = arg.method_19794();
				Map<Identifier, Predicate<AdvancementProgress>> map = Maps.newHashMap();
				stringReader.expect('{');
				stringReader.skipWhitespace();

				while (stringReader.canRead() && stringReader.peek() != '}') {
					stringReader.skipWhitespace();
					Identifier identifier = Identifier.method_20442(stringReader);
					stringReader.skipWhitespace();
					stringReader.expect('=');
					stringReader.skipWhitespace();
					if (stringReader.canRead() && stringReader.peek() == '{') {
						Map<String, Predicate<CriterionProgress>> map2 = Maps.newHashMap();
						stringReader.skipWhitespace();
						stringReader.expect('{');
						stringReader.skipWhitespace();

						while (stringReader.canRead() && stringReader.peek() != '}') {
							stringReader.skipWhitespace();
							String string = stringReader.readUnquotedString();
							stringReader.skipWhitespace();
							stringReader.expect('=');
							stringReader.skipWhitespace();
							boolean bl = stringReader.readBoolean();
							map2.put(string, (Predicate)criterionProgress -> criterionProgress.hasBeenObtained() == bl);
							stringReader.skipWhitespace();
							if (stringReader.canRead() && stringReader.peek() == ',') {
								stringReader.skip();
							}
						}

						stringReader.skipWhitespace();
						stringReader.expect('}');
						stringReader.skipWhitespace();
						map.put(identifier, (Predicate)advancementProgress -> {
							for (Entry<String, Predicate<CriterionProgress>> entry : map2.entrySet()) {
								CriterionProgress criterionProgress = advancementProgress.getCriteria((String)entry.getKey());
								if (criterionProgress == null || !((Predicate)entry.getValue()).test(criterionProgress)) {
									return false;
								}
							}

							return true;
						});
					} else {
						boolean bl2 = stringReader.readBoolean();
						map.put(identifier, (Predicate)advancementProgress -> advancementProgress.method_14833() == bl2);
					}

					stringReader.skipWhitespace();
					if (stringReader.canRead() && stringReader.peek() == ',') {
						stringReader.skip();
					}
				}

				stringReader.expect('}');
				if (!map.isEmpty()) {
					arg.method_19766(entity -> {
						if (!(entity instanceof ServerPlayerEntity)) {
							return false;
						} else {
							ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
							AdvancementFile advancementFile = serverPlayerEntity.getAdvancementFile();
							class_3348 lv = serverPlayerEntity.method_12833().method_14910();

							for (Entry<Identifier, Predicate<AdvancementProgress>> entry : map.entrySet()) {
								SimpleAdvancement simpleAdvancement = lv.method_14938((Identifier)entry.getKey());
								if (simpleAdvancement == null || !((Predicate)entry.getValue()).test(advancementFile.method_14923(simpleAdvancement))) {
									return false;
								}
							}

							return true;
						}
					});
					arg.method_19768(false);
				}

				arg.method_19812(true);
			}, arg -> !arg.method_19746(), new TranslatableText("argument.entity.options.advancements.description"));
		}
	}

	public static class_4319.class_4320 method_19846(class_4318 arg, String string, int i) throws CommandSyntaxException {
		class_4319.class_4321 lv = (class_4319.class_4321)field_21242.get(string);
		if (lv != null) {
			if (lv.field_21244.test(arg)) {
				return lv.field_21243;
			} else {
				throw field_21235.createWithContext(arg.method_19794(), string);
			}
		} else {
			arg.method_19794().setCursor(i);
			throw field_21234.createWithContext(arg.method_19794(), string);
		}
	}

	public static void method_19844(class_4318 arg, SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (Entry<String, class_4319.class_4321> entry : field_21242.entrySet()) {
			if (((class_4319.class_4321)entry.getValue()).field_21244.test(arg) && ((String)entry.getKey()).toLowerCase(Locale.ROOT).startsWith(string)) {
				suggestionsBuilder.suggest((String)entry.getKey() + '=', ((class_4319.class_4321)entry.getValue()).field_21245);
			}
		}
	}

	public interface class_4320 {
		void handle(class_4318 arg) throws CommandSyntaxException;
	}

	static class class_4321 {
		public final class_4319.class_4320 field_21243;
		public final Predicate<class_4318> field_21244;
		public final Text field_21245;

		private class_4321(class_4319.class_4320 arg, Predicate<class_4318> predicate, Text text) {
			this.field_21243 = arg;
			this.field_21244 = predicate;
			this.field_21245 = text;
		}
	}
}
