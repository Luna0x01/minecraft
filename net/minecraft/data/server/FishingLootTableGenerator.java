package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biomes;

public class FishingLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	public static final LootCondition.Builder NEEDS_JUNGLE_BIOME = LocationCheckLootCondition.builder(LocationPredicate.Builder.create().biome(Biomes.field_9417));
	public static final LootCondition.Builder NEEDS_JUNGLE_HILLS_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.field_9432)
	);
	public static final LootCondition.Builder NEEDS_JUNGLE_EDGE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.field_9474)
	);
	public static final LootCondition.Builder NEEDS_BAMBOO_JUNGLE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.field_9440)
	);
	public static final LootCondition.Builder NEEDS_MODIFIED_JUNGLE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.field_9426)
	);
	public static final LootCondition.Builder NEEDS_MODIFIED_JUNGLE_EDGE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.field_9405)
	);
	public static final LootCondition.Builder NEEDS_BAMBOO_JUNGLE_HILLS_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.field_9468)
	);

	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.field_353,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(LootTableEntry.builder(LootTables.field_266).setWeight(10).setQuality(-2))
						.withEntry(LootTableEntry.builder(LootTables.field_854).setWeight(5).setQuality(2))
						.withEntry(LootTableEntry.builder(LootTables.field_795).setWeight(85).setQuality(-1))
				)
		);
		biConsumer.accept(
			LootTables.field_795,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withEntry(ItemEntry.builder(Items.field_8429).setWeight(60))
						.withEntry(ItemEntry.builder(Items.field_8209).setWeight(25))
						.withEntry(ItemEntry.builder(Items.field_8846).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8323).setWeight(13))
				)
		);
		biConsumer.accept(
			LootTables.field_266,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withEntry(ItemEntry.builder(Items.field_8370).setWeight(10).withFunction(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.9F))))
						.withEntry(ItemEntry.builder(Items.field_8745).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8606).setWeight(10))
						.withEntry(
							ItemEntry.builder(Items.field_8574)
								.setWeight(10)
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:water"))))
						)
						.withEntry(ItemEntry.builder(Items.field_8276).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8378).setWeight(2).withFunction(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.9F))))
						.withEntry(ItemEntry.builder(Items.field_8428).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8600).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8794).setWeight(1).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(10))))
						.withEntry(ItemEntry.builder(Blocks.field_10348).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(10))
						.withEntry(
							ItemEntry.builder(Blocks.field_10211)
								.withCondition(
									NEEDS_JUNGLE_BIOME.withCondition(NEEDS_JUNGLE_HILLS_BIOME)
										.withCondition(NEEDS_JUNGLE_EDGE_BIOME)
										.withCondition(NEEDS_BAMBOO_JUNGLE_BIOME)
										.withCondition(NEEDS_MODIFIED_JUNGLE_BIOME)
										.withCondition(NEEDS_MODIFIED_JUNGLE_EDGE_BIOME)
										.withCondition(NEEDS_BAMBOO_JUNGLE_HILLS_BIOME)
								)
								.setWeight(10)
						)
				)
		);
		biConsumer.accept(
			LootTables.field_854,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withEntry(ItemEntry.builder(Blocks.field_10588))
						.withEntry(ItemEntry.builder(Items.field_8448))
						.withEntry(ItemEntry.builder(Items.field_8175))
						.withEntry(
							ItemEntry.builder(Items.field_8102)
								.withFunction(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.25F)))
								.withFunction(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8378)
								.withFunction(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.25F)))
								.withFunction(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8529).withFunction(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
						.withEntry(ItemEntry.builder(Items.field_8864))
				)
		);
	}
}
