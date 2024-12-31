package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class GiftLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.field_16216,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8245).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8073).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8726).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8153).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8276).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8614).setWeight(2))
				)
		);
		biConsumer.accept(
			LootTables.field_19062,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8283))
						.withEntry(ItemEntry.builder(Items.field_8873))
						.withEntry(ItemEntry.builder(Items.field_8218))
						.withEntry(ItemEntry.builder(Items.field_8313))
				)
		);
		biConsumer.accept(
			LootTables.field_19063,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8752))
						.withEntry(ItemEntry.builder(Items.field_8544))
						.withEntry(ItemEntry.builder(Items.field_8261))
						.withEntry(ItemEntry.builder(Items.field_8176))
						.withEntry(ItemEntry.builder(Items.field_8347))
				)
		);
		biConsumer.accept(
			LootTables.field_19064,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8895))
						.withEntry(ItemEntry.builder(Items.field_8407))
				)
		);
		biConsumer.accept(
			LootTables.field_19065,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8725))
						.withEntry(ItemEntry.builder(Items.field_8759))
				)
		);
		biConsumer.accept(
			LootTables.field_19066,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8229))
						.withEntry(ItemEntry.builder(Items.field_8741))
						.withEntry(ItemEntry.builder(Items.field_8423))
				)
		);
		biConsumer.accept(
			LootTables.field_19067,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8429))
						.withEntry(ItemEntry.builder(Items.field_8209))
				)
		);
		biConsumer.accept(
			LootTables.field_19068,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8107).setWeight(26))
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:swiftness"))))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:slowness"))))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:strength"))))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:healing"))))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:harming"))))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:leaping"))))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:regeneration"))))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:water_breathing"))))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:invisibility"))))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:night_vision"))))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:weakness"))))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:poison"))))
						)
				)
		);
		biConsumer.accept(
			LootTables.field_19069,
			LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.field_8745)))
		);
		biConsumer.accept(
			LootTables.field_19070,
			LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.field_8529)))
		);
		biConsumer.accept(
			LootTables.field_19071,
			LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.CLAY)))
		);
		biConsumer.accept(
			LootTables.field_19072,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.WHITE_WOOL))
						.withEntry(ItemEntry.builder(Items.ORANGE_WOOL))
						.withEntry(ItemEntry.builder(Items.MAGENTA_WOOL))
						.withEntry(ItemEntry.builder(Items.LIGHT_BLUE_WOOL))
						.withEntry(ItemEntry.builder(Items.YELLOW_WOOL))
						.withEntry(ItemEntry.builder(Items.LIME_WOOL))
						.withEntry(ItemEntry.builder(Items.PINK_WOOL))
						.withEntry(ItemEntry.builder(Items.GRAY_WOOL))
						.withEntry(ItemEntry.builder(Items.LIGHT_GRAY_WOOL))
						.withEntry(ItemEntry.builder(Items.CYAN_WOOL))
						.withEntry(ItemEntry.builder(Items.PURPLE_WOOL))
						.withEntry(ItemEntry.builder(Items.BLUE_WOOL))
						.withEntry(ItemEntry.builder(Items.BROWN_WOOL))
						.withEntry(ItemEntry.builder(Items.GREEN_WOOL))
						.withEntry(ItemEntry.builder(Items.RED_WOOL))
						.withEntry(ItemEntry.builder(Items.BLACK_WOOL))
				)
		);
		biConsumer.accept(
			LootTables.field_19073,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8387))
						.withEntry(ItemEntry.builder(Items.field_8062))
						.withEntry(ItemEntry.builder(Items.field_8431))
						.withEntry(ItemEntry.builder(Items.field_8776))
				)
		);
		biConsumer.accept(
			LootTables.field_19074,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8062))
						.withEntry(ItemEntry.builder(Items.field_8825))
						.withEntry(ItemEntry.builder(Items.field_8475))
				)
		);
	}
}
