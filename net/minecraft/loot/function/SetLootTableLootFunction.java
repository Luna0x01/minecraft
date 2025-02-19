package net.minecraft.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class SetLootTableLootFunction extends ConditionalLootFunction {
	final Identifier id;
	final long seed;

	SetLootTableLootFunction(LootCondition[] lootConditions, Identifier identifier, long l) {
		super(lootConditions);
		this.id = identifier;
		this.seed = l;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_LOOT_TABLE;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.isEmpty()) {
			return stack;
		} else {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putString("LootTable", this.id.toString());
			if (this.seed != 0L) {
				nbtCompound.putLong("LootTableSeed", this.seed);
			}

			stack.getOrCreateTag().put("BlockEntityTag", nbtCompound);
			return stack;
		}
	}

	@Override
	public void validate(LootTableReporter reporter) {
		if (reporter.hasTable(this.id)) {
			reporter.report("Table " + this.id + " is recursively called");
		} else {
			super.validate(reporter);
			LootTable lootTable = reporter.getTable(this.id);
			if (lootTable == null) {
				reporter.report("Unknown loot table called " + this.id);
			} else {
				lootTable.validate(reporter.withTable("->{" + this.id + "}", this.id));
			}
		}
	}

	public static ConditionalLootFunction.Builder<?> builder(Identifier id) {
		return builder(conditions -> new SetLootTableLootFunction(conditions, id, 0L));
	}

	public static ConditionalLootFunction.Builder<?> builder(Identifier id, long seed) {
		return builder(conditions -> new SetLootTableLootFunction(conditions, id, seed));
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetLootTableLootFunction> {
		public void toJson(JsonObject jsonObject, SetLootTableLootFunction setLootTableLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setLootTableLootFunction, jsonSerializationContext);
			jsonObject.addProperty("name", setLootTableLootFunction.id.toString());
			if (setLootTableLootFunction.seed != 0L) {
				jsonObject.addProperty("seed", setLootTableLootFunction.seed);
			}
		}

		public SetLootTableLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "name"));
			long l = JsonHelper.getLong(jsonObject, "seed", 0L);
			return new SetLootTableLootFunction(lootConditions, identifier, l);
		}
	}
}
