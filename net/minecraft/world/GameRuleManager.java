package net.minecraft.world;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.class_3915;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Util;

public class GameRuleManager {
	private static final TreeMap<String, GameRuleManager.class_3596> field_17491 = Util.make(new TreeMap(), treeMap -> {
		treeMap.put("doFireTick", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("mobGriefing", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("keepInventory", new GameRuleManager.class_3596("false", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("doMobSpawning", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("doMobLoot", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("doTileDrops", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("doEntityDrops", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("commandBlockOutput", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("naturalRegeneration", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("doDaylightCycle", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("logAdminCommands", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("showDeathMessages", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("randomTickSpeed", new GameRuleManager.class_3596("3", GameRuleManager.VariableType.NUMERICAL_VALUE));
		treeMap.put("sendCommandFeedback", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("reducedDebugInfo", new GameRuleManager.class_3596("false", GameRuleManager.VariableType.BOOLEAN_VALUE, (minecraftServer, value) -> {
			byte b = (byte)(value.getBooleanDefaultValue() ? 22 : 23);

			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayers()) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
			}
		}));
		treeMap.put("spectatorsGenerateChunks", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("spawnRadius", new GameRuleManager.class_3596("10", GameRuleManager.VariableType.NUMERICAL_VALUE));
		treeMap.put("disableElytraMovementCheck", new GameRuleManager.class_3596("false", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("maxEntityCramming", new GameRuleManager.class_3596("24", GameRuleManager.VariableType.NUMERICAL_VALUE));
		treeMap.put("doWeatherCycle", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("doLimitedCrafting", new GameRuleManager.class_3596("false", GameRuleManager.VariableType.BOOLEAN_VALUE));
		treeMap.put("maxCommandChainLength", new GameRuleManager.class_3596("65536", GameRuleManager.VariableType.NUMERICAL_VALUE));
		treeMap.put("announceAdvancements", new GameRuleManager.class_3596("true", GameRuleManager.VariableType.BOOLEAN_VALUE));
	});
	private final TreeMap<String, GameRuleManager.Value> gameRules = new TreeMap();

	public GameRuleManager() {
		for (Entry<String, GameRuleManager.class_3596> entry : field_17491.entrySet()) {
			this.gameRules.put(entry.getKey(), ((GameRuleManager.class_3596)entry.getValue()).method_16303());
		}
	}

	public void method_16297(String string, String string2, @Nullable MinecraftServer minecraftServer) {
		GameRuleManager.Value value = (GameRuleManager.Value)this.gameRules.get(string);
		if (value != null) {
			value.method_16302(string2, minecraftServer);
		}
	}

	public boolean getBoolean(String name) {
		GameRuleManager.Value value = (GameRuleManager.Value)this.gameRules.get(name);
		return value != null ? value.getBooleanDefaultValue() : false;
	}

	public int getInt(String name) {
		GameRuleManager.Value value = (GameRuleManager.Value)this.gameRules.get(name);
		return value != null ? value.getIntDefaultValue() : 0;
	}

	public NbtCompound getNbt() {
		NbtCompound nbtCompound = new NbtCompound();

		for (String string : this.gameRules.keySet()) {
			GameRuleManager.Value value = (GameRuleManager.Value)this.gameRules.get(string);
			nbtCompound.putString(string, value.getStringDefaultValue());
		}

		return nbtCompound;
	}

	public void setNbt(NbtCompound nbt) {
		for (String string : nbt.getKeys()) {
			this.method_16297(string, nbt.getString(string), null);
		}
	}

	public GameRuleManager.Value method_16301(String string) {
		return (GameRuleManager.Value)this.gameRules.get(string);
	}

	public static TreeMap<String, GameRuleManager.class_3596> method_16300() {
		return field_17491;
	}

	public static class Value {
		private String stringDefaultValue;
		private boolean booleanDefaultValue;
		private int intDefaultValue;
		private double doubleDefaultValue;
		private final GameRuleManager.VariableType type;
		private final BiConsumer<MinecraftServer, GameRuleManager.Value> field_17492;

		public Value(String string, GameRuleManager.VariableType variableType, BiConsumer<MinecraftServer, GameRuleManager.Value> biConsumer) {
			this.type = variableType;
			this.field_17492 = biConsumer;
			this.method_16302(string, null);
		}

		public void method_16302(String string, @Nullable MinecraftServer minecraftServer) {
			this.stringDefaultValue = string;
			this.booleanDefaultValue = Boolean.parseBoolean(string);
			this.intDefaultValue = this.booleanDefaultValue ? 1 : 0;

			try {
				this.intDefaultValue = Integer.parseInt(string);
			} catch (NumberFormatException var5) {
			}

			try {
				this.doubleDefaultValue = Double.parseDouble(string);
			} catch (NumberFormatException var4) {
			}

			if (minecraftServer != null) {
				this.field_17492.accept(minecraftServer, this);
			}
		}

		public String getStringDefaultValue() {
			return this.stringDefaultValue;
		}

		public boolean getBooleanDefaultValue() {
			return this.booleanDefaultValue;
		}

		public int getIntDefaultValue() {
			return this.intDefaultValue;
		}

		public GameRuleManager.VariableType getVariableType() {
			return this.type;
		}
	}

	public static enum VariableType {
		ANY_VALUE(StringArgumentType::greedyString, (commandContext, string) -> (String)commandContext.getArgument(string, String.class)),
		BOOLEAN_VALUE(BoolArgumentType::bool, (commandContext, string) -> ((Boolean)commandContext.getArgument(string, Boolean.class)).toString()),
		NUMERICAL_VALUE(IntegerArgumentType::integer, (commandContext, string) -> ((Integer)commandContext.getArgument(string, Integer.class)).toString());

		private final Supplier<ArgumentType<?>> field_17499;
		private final BiFunction<CommandContext<class_3915>, String, String> field_17500;

		private VariableType(Supplier<ArgumentType<?>> supplier, BiFunction<CommandContext<class_3915>, String, String> biFunction) {
			this.field_17499 = supplier;
			this.field_17500 = biFunction;
		}

		public RequiredArgumentBuilder<class_3915, ?> method_16308(String string) {
			return CommandManager.method_17530(string, (ArgumentType)this.field_17499.get());
		}

		public void method_16307(CommandContext<class_3915> commandContext, String string, GameRuleManager.Value value) {
			value.method_16302((String)this.field_17500.apply(commandContext, string), ((class_3915)commandContext.getSource()).method_17473());
		}
	}

	public static class class_3596 {
		private final GameRuleManager.VariableType field_17493;
		private final String field_17494;
		private final BiConsumer<MinecraftServer, GameRuleManager.Value> field_17495;

		public class_3596(String string, GameRuleManager.VariableType variableType) {
			this(string, variableType, (minecraftServer, value) -> {
			});
		}

		public class_3596(String string, GameRuleManager.VariableType variableType, BiConsumer<MinecraftServer, GameRuleManager.Value> biConsumer) {
			this.field_17493 = variableType;
			this.field_17494 = string;
			this.field_17495 = biConsumer;
		}

		public GameRuleManager.Value method_16303() {
			return new GameRuleManager.Value(this.field_17494, this.field_17493, this.field_17495);
		}

		public GameRuleManager.VariableType method_16305() {
			return this.field_17493;
		}
	}
}
