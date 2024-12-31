package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<GameRules.RuleKey<?>, GameRules.RuleType<?>> RULE_TYPES = Maps.newTreeMap(Comparator.comparing(ruleKey -> ruleKey.name));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19387 = register("doFireTick", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19388 = register("mobGriefing", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19389 = register("keepInventory", GameRules.BooleanRule.create(false));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19390 = register("doMobSpawning", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19391 = register("doMobLoot", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19392 = register("doTileDrops", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19393 = register("doEntityDrops", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19394 = register("commandBlockOutput", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19395 = register("naturalRegeneration", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19396 = register("doDaylightCycle", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19397 = register("logAdminCommands", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19398 = register("showDeathMessages", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.IntRule> field_19399 = register("randomTickSpeed", GameRules.IntRule.create(3));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19400 = register("sendCommandFeedback", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19401 = register(
		"reducedDebugInfo", GameRules.BooleanRule.create(false, (minecraftServer, booleanRule) -> {
			byte b = (byte)(booleanRule.get() ? 22 : 23);

			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
			}
		})
	);
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19402 = register("spectatorsGenerateChunks", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.IntRule> field_19403 = register("spawnRadius", GameRules.IntRule.create(10));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19404 = register("disableElytraMovementCheck", GameRules.BooleanRule.create(false));
	public static final GameRules.RuleKey<GameRules.IntRule> field_19405 = register("maxEntityCramming", GameRules.IntRule.create(24));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19406 = register("doWeatherCycle", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19407 = register("doLimitedCrafting", GameRules.BooleanRule.create(false));
	public static final GameRules.RuleKey<GameRules.IntRule> field_19408 = register("maxCommandChainLength", GameRules.IntRule.create(65536));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19409 = register("announceAdvancements", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_19422 = register("disableRaids", GameRules.BooleanRule.create(false));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_20637 = register("doInsomnia", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_20638 = register(
		"doImmediateRespawn", GameRules.BooleanRule.create(false, (minecraftServer, booleanRule) -> {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(11, booleanRule.get() ? 1.0F : 0.0F));
			}
		})
	);
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_20634 = register("drowningDamage", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_20635 = register("fallDamage", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_20636 = register("fireDamage", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_21831 = register("doPatrolSpawning", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> field_21832 = register("doTraderSpawning", GameRules.BooleanRule.create(true));
	private final Map<GameRules.RuleKey<?>, GameRules.Rule<?>> rules = (Map<GameRules.RuleKey<?>, GameRules.Rule<?>>)RULE_TYPES.entrySet()
		.stream()
		.collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> ((GameRules.RuleType)entry.getValue()).createRule()));

	private static <T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(String string, GameRules.RuleType<T> ruleType) {
		GameRules.RuleKey<T> ruleKey = new GameRules.RuleKey<>(string);
		GameRules.RuleType<?> ruleType2 = (GameRules.RuleType<?>)RULE_TYPES.put(ruleKey, ruleType);
		if (ruleType2 != null) {
			throw new IllegalStateException("Duplicate game rule registration for " + string);
		} else {
			return ruleKey;
		}
	}

	public <T extends GameRules.Rule<T>> T get(GameRules.RuleKey<T> ruleKey) {
		return (T)this.rules.get(ruleKey);
	}

	public CompoundTag toNbt() {
		CompoundTag compoundTag = new CompoundTag();
		this.rules.forEach((ruleKey, rule) -> compoundTag.putString(ruleKey.name, rule.serialize()));
		return compoundTag;
	}

	public void load(CompoundTag compoundTag) {
		this.rules.forEach((ruleKey, rule) -> {
			if (compoundTag.contains(ruleKey.name)) {
				rule.deserialize(compoundTag.getString(ruleKey.name));
			}
		});
	}

	public static void forEachType(GameRules.RuleTypeConsumer ruleTypeConsumer) {
		RULE_TYPES.forEach((ruleKey, ruleType) -> accept(ruleTypeConsumer, ruleKey, ruleType));
	}

	private static <T extends GameRules.Rule<T>> void accept(
		GameRules.RuleTypeConsumer ruleTypeConsumer, GameRules.RuleKey<?> ruleKey, GameRules.RuleType<?> ruleType
	) {
		ruleTypeConsumer.accept(ruleKey, ruleType);
	}

	public boolean getBoolean(GameRules.RuleKey<GameRules.BooleanRule> ruleKey) {
		return this.get(ruleKey).get();
	}

	public int getInt(GameRules.RuleKey<GameRules.IntRule> ruleKey) {
		return this.get(ruleKey).get();
	}

	public static class BooleanRule extends GameRules.Rule<GameRules.BooleanRule> {
		private boolean value;

		private static GameRules.RuleType<GameRules.BooleanRule> create(boolean bl, BiConsumer<MinecraftServer, GameRules.BooleanRule> biConsumer) {
			return new GameRules.RuleType<>(BoolArgumentType::bool, ruleType -> new GameRules.BooleanRule(ruleType, bl), biConsumer);
		}

		private static GameRules.RuleType<GameRules.BooleanRule> create(boolean bl) {
			return create(bl, (minecraftServer, booleanRule) -> {
			});
		}

		public BooleanRule(GameRules.RuleType<GameRules.BooleanRule> ruleType, boolean bl) {
			super(ruleType);
			this.value = bl;
		}

		@Override
		protected void setFromArgument(CommandContext<ServerCommandSource> commandContext, String string) {
			this.value = BoolArgumentType.getBool(commandContext, string);
		}

		public boolean get() {
			return this.value;
		}

		public void set(boolean bl, @Nullable MinecraftServer minecraftServer) {
			this.value = bl;
			this.changed(minecraftServer);
		}

		@Override
		protected String serialize() {
			return Boolean.toString(this.value);
		}

		@Override
		protected void deserialize(String string) {
			this.value = Boolean.parseBoolean(string);
		}

		@Override
		public int getCommandResult() {
			return this.value ? 1 : 0;
		}

		protected GameRules.BooleanRule getThis() {
			return this;
		}
	}

	public static class IntRule extends GameRules.Rule<GameRules.IntRule> {
		private int value;

		private static GameRules.RuleType<GameRules.IntRule> create(int i, BiConsumer<MinecraftServer, GameRules.IntRule> biConsumer) {
			return new GameRules.RuleType<>(IntegerArgumentType::integer, ruleType -> new GameRules.IntRule(ruleType, i), biConsumer);
		}

		private static GameRules.RuleType<GameRules.IntRule> create(int i) {
			return create(i, (minecraftServer, intRule) -> {
			});
		}

		public IntRule(GameRules.RuleType<GameRules.IntRule> ruleType, int i) {
			super(ruleType);
			this.value = i;
		}

		@Override
		protected void setFromArgument(CommandContext<ServerCommandSource> commandContext, String string) {
			this.value = IntegerArgumentType.getInteger(commandContext, string);
		}

		public int get() {
			return this.value;
		}

		@Override
		protected String serialize() {
			return Integer.toString(this.value);
		}

		@Override
		protected void deserialize(String string) {
			this.value = parseInt(string);
		}

		private static int parseInt(String string) {
			if (!string.isEmpty()) {
				try {
					return Integer.parseInt(string);
				} catch (NumberFormatException var2) {
					GameRules.LOGGER.warn("Failed to parse integer {}", string);
				}
			}

			return 0;
		}

		@Override
		public int getCommandResult() {
			return this.value;
		}

		protected GameRules.IntRule getThis() {
			return this;
		}
	}

	public abstract static class Rule<T extends GameRules.Rule<T>> {
		private final GameRules.RuleType<T> type;

		public Rule(GameRules.RuleType<T> ruleType) {
			this.type = ruleType;
		}

		protected abstract void setFromArgument(CommandContext<ServerCommandSource> commandContext, String string);

		public void set(CommandContext<ServerCommandSource> commandContext, String string) {
			this.setFromArgument(commandContext, string);
			this.changed(((ServerCommandSource)commandContext.getSource()).getMinecraftServer());
		}

		protected void changed(@Nullable MinecraftServer minecraftServer) {
			if (minecraftServer != null) {
				this.type.changeCallback.accept(minecraftServer, this.getThis());
			}
		}

		protected abstract void deserialize(String string);

		protected abstract String serialize();

		public String toString() {
			return this.serialize();
		}

		public abstract int getCommandResult();

		protected abstract T getThis();
	}

	public static final class RuleKey<T extends GameRules.Rule<T>> {
		private final String name;

		public RuleKey(String string) {
			this.name = string;
		}

		public String toString() {
			return this.name;
		}

		public boolean equals(Object object) {
			return this == object ? true : object instanceof GameRules.RuleKey && ((GameRules.RuleKey)object).name.equals(this.name);
		}

		public int hashCode() {
			return this.name.hashCode();
		}

		public String getName() {
			return this.name;
		}
	}

	public static class RuleType<T extends GameRules.Rule<T>> {
		private final Supplier<ArgumentType<?>> argumentType;
		private final Function<GameRules.RuleType<T>, T> ruleFactory;
		private final BiConsumer<MinecraftServer, T> changeCallback;

		private RuleType(Supplier<ArgumentType<?>> supplier, Function<GameRules.RuleType<T>, T> function, BiConsumer<MinecraftServer, T> biConsumer) {
			this.argumentType = supplier;
			this.ruleFactory = function;
			this.changeCallback = biConsumer;
		}

		public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String string) {
			return CommandManager.argument(string, (ArgumentType<T>)this.argumentType.get());
		}

		public T createRule() {
			return (T)this.ruleFactory.apply(this);
		}
	}

	@FunctionalInterface
	public interface RuleTypeConsumer {
		<T extends GameRules.Rule<T>> void accept(GameRules.RuleKey<T> ruleKey, GameRules.RuleType<T> ruleType);
	}
}
