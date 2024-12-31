package net.minecraft.world;

import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.NbtCompound;

public class GameRuleManager {
	private final TreeMap<String, GameRuleManager.Value> gameRules = new TreeMap();

	public GameRuleManager() {
		this.addGameRule("doFireTick", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("mobGriefing", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("keepInventory", "false", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("doMobSpawning", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("doMobLoot", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("doTileDrops", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("doEntityDrops", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("commandBlockOutput", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("naturalRegeneration", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("doDaylightCycle", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("logAdminCommands", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("showDeathMessages", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("randomTickSpeed", "3", GameRuleManager.VariableType.NUMERICAL);
		this.addGameRule("sendCommandFeedback", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("reducedDebugInfo", "false", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("spectatorsGenerateChunks", "true", GameRuleManager.VariableType.BOOLEAN);
		this.addGameRule("spawnRadius", "10", GameRuleManager.VariableType.NUMERICAL);
		this.addGameRule("disableElytraMovementCheck", "false", GameRuleManager.VariableType.BOOLEAN);
	}

	public void addGameRule(String name, String defaultValue, GameRuleManager.VariableType variableType) {
		this.gameRules.put(name, new GameRuleManager.Value(defaultValue, variableType));
	}

	public void setGameRule(String rule, String value) {
		GameRuleManager.Value value2 = (GameRuleManager.Value)this.gameRules.get(rule);
		if (value2 != null) {
			value2.setDefaultValue(value);
		} else {
			this.addGameRule(rule, value, GameRuleManager.VariableType.ANY);
		}
	}

	public String getString(String name) {
		GameRuleManager.Value value = (GameRuleManager.Value)this.gameRules.get(name);
		return value != null ? value.getStringDefaultValue() : "";
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
			this.setGameRule(string, nbt.getString(string));
		}
	}

	public String[] method_4670() {
		Set<String> set = this.gameRules.keySet();
		return (String[])set.toArray(new String[set.size()]);
	}

	public boolean contains(String key) {
		return this.gameRules.containsKey(key);
	}

	public boolean method_8474(String string, GameRuleManager.VariableType variableType) {
		GameRuleManager.Value value = (GameRuleManager.Value)this.gameRules.get(string);
		return value != null && (value.getVariableType() == variableType || variableType == GameRuleManager.VariableType.ANY);
	}

	static class Value {
		private String stringDefaultValue;
		private boolean booleanDefaultValue;
		private int intDefaultValue;
		private double doubleDefaultValue;
		private final GameRuleManager.VariableType type;

		public Value(String string, GameRuleManager.VariableType variableType) {
			this.type = variableType;
			this.setDefaultValue(string);
		}

		public void setDefaultValue(String value) {
			this.stringDefaultValue = value;
			this.booleanDefaultValue = Boolean.parseBoolean(value);
			this.intDefaultValue = this.booleanDefaultValue ? 1 : 0;

			try {
				this.intDefaultValue = Integer.parseInt(value);
			} catch (NumberFormatException var4) {
			}

			try {
				this.doubleDefaultValue = Double.parseDouble(value);
			} catch (NumberFormatException var3) {
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
		ANY,
		BOOLEAN,
		NUMERICAL;
	}
}
