package net.minecraft.data.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class EntityLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	private static final EntityPredicate.Builder NEEDS_ENTITY_ON_FIRE = EntityPredicate.Builder.create()
		.flags(EntityFlagsPredicate.Builder.create().onFire(true).build());
	private static final Set<EntityType<?>> ENTITY_TYPES_IN_MISC_CATEGORY_TO_CHECK = ImmutableSet.of(
		EntityType.field_6097, EntityType.field_6131, EntityType.field_6147, EntityType.field_6047, EntityType.field_6077
	);
	private final Map<Identifier, LootTable.Builder> lootTables = Maps.newHashMap();

	private static LootTable.Builder createForSheep(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible)))
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(LootTableEntry.builder(EntityType.field_6115.getLootTableId())));
	}

	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		this.register(EntityType.field_6131, LootTable.builder());
		this.register(EntityType.field_6108, LootTable.builder());
		this.register(EntityType.field_20346, LootTable.builder());
		this.register(
			EntityType.field_6099,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8894)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.field_16281,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8276).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F))))
				)
		);
		this.register(
			EntityType.field_6084,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8276)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8680)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.field_6132,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8153)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8726)
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6070,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8429)
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, NEEDS_ENTITY_ON_FIRE))
								)
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8324))
						.withCondition(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.register(
			EntityType.field_6085,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8046)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6046,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8054)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withEntry(TagEntry.builder(ItemTags.field_15541))
						.withCondition(
							EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_936, EntityPredicate.Builder.create().type(EntityTypeTags.field_15507))
						)
				)
		);
		this.register(
			EntityType.field_6087,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8429)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, NEEDS_ENTITY_ON_FIRE))
								)
						)
				)
		);
		this.register(
			EntityType.field_6067,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6123,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8511)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8695))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.05F, 0.01F))
				)
		);
		this.register(
			EntityType.field_6086,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8662)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8429)
								.setWeight(3)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, NEEDS_ENTITY_ON_FIRE))
								)
						)
						.withEntry(ItemEntry.builder(Items.field_8434).setWeight(2).withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F))))
						.withEntry(EmptyEntry.Serializer())
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.field_10562))
						.withCondition(KilledByPlayerLootCondition.builder())
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(LootTableEntry.builder(LootTables.field_795))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(EntityType.field_6116, LootTable.builder());
		this.register(
			EntityType.field_6091,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8634)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.field_6128, LootTable.builder());
		this.register(
			EntityType.field_6090,
			LootTable.builder()
				.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.field_8288)))
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8687)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(EntityType.field_17943, LootTable.builder());
		this.register(
			EntityType.field_6107,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8070)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8054)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.field_6095, LootTable.builder());
		this.register(
			EntityType.field_6118,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8662)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8429)
								.setWeight(2)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, NEEDS_ENTITY_ON_FIRE))
								)
						)
						.withEntry(ItemEntry.builder(Items.field_8434).setWeight(2).withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F))))
						.withEntry(EmptyEntry.Serializer())
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(LootTableEntry.builder(LootTables.field_795))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(
			EntityType.field_6139,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6071,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8511)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8620))
						.withEntry(ItemEntry.builder(Items.field_8179))
						.withEntry(ItemEntry.builder(Items.field_8567))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(
			EntityType.field_6134,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8175).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		this.register(EntityType.field_6065, LootTable.builder());
		this.register(
			EntityType.field_6147,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.field_10449).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8620).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
				)
		);
		this.register(
			EntityType.field_6074,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6102,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8135)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-2.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6057,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6143,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8046)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.field_6081, LootTable.builder());
		this.register(
			EntityType.field_6146,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.field_10211).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		this.register(
			EntityType.field_6104,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8153)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6078,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8614)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.field_6093,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8389)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.field_6105, LootTable.builder());
		this.register(EntityType.field_6097, LootTable.builder());
		this.register(
			EntityType.field_6042,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8429)
								.setWeight(3)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8209)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6062,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8323).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8324))
						.withCondition(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.register(
			EntityType.field_6140,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8245)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8504)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8073))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.1F, 0.03F))
				)
		);
		this.register(
			EntityType.field_6073,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8209)
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, NEEDS_ENTITY_ON_FIRE))
								)
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8324))
						.withCondition(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.register(
			EntityType.field_6115,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8748)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(LootTables.BLACK_SHEEP_ENTITY, createForSheep(Blocks.field_10146));
		this.register(LootTables.BLUE_SHEEP_ENTITY, createForSheep(Blocks.field_10514));
		this.register(LootTables.BROWN_SHEEP_ENTITY, createForSheep(Blocks.field_10113));
		this.register(LootTables.CYAN_SHEEP_ENTITY, createForSheep(Blocks.field_10619));
		this.register(LootTables.GRAY_SHEEP_ENTITY, createForSheep(Blocks.field_10423));
		this.register(LootTables.GREEN_SHEEP_ENTITY, createForSheep(Blocks.field_10170));
		this.register(LootTables.LIGHT_BLUE_SHEEP_ENTITY, createForSheep(Blocks.field_10294));
		this.register(LootTables.LIGHT_GRAY_SHEEP_ENTITY, createForSheep(Blocks.field_10222));
		this.register(LootTables.LIME_SHEEP_ENTITY, createForSheep(Blocks.field_10028));
		this.register(LootTables.MAGENTA_SHEEP_ENTITY, createForSheep(Blocks.field_10215));
		this.register(LootTables.ORANGE_SHEEP_ENTITY, createForSheep(Blocks.field_10095));
		this.register(LootTables.PINK_SHEEP_ENTITY, createForSheep(Blocks.field_10459));
		this.register(LootTables.PURPLE_SHEEP_ENTITY, createForSheep(Blocks.field_10259));
		this.register(LootTables.RED_SHEEP_ENTITY, createForSheep(Blocks.field_10314));
		this.register(LootTables.WHITE_SHEEP_ENTITY, createForSheep(Blocks.field_10446));
		this.register(LootTables.YELLOW_SHEEP_ENTITY, createForSheep(Blocks.field_10490));
		this.register(
			EntityType.field_6109,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8815))
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.5F, 0.0625F))
				)
		);
		this.register(EntityType.field_6125, LootTable.builder());
		this.register(
			EntityType.field_6137,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8107)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8606)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6075,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8606)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6069,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8777)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6047,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8543).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 15.0F))))
				)
		);
		this.register(
			EntityType.field_6079,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8276)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8680)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.field_6114,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8794)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6098,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8107)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8606)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)).withLimit(1))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:slowness"))))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.field_17714,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6111,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8846).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8324))
						.withCondition(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.register(
			EntityType.field_6113,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Blocks.field_10376)
								.setWeight(3)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8428))
						.withCondition(DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().lightning(true)))
				)
		);
		this.register(EntityType.field_6059, LootTable.builder());
		this.register(EntityType.field_6077, LootTable.builder());
		this.register(EntityType.field_17713, LootTable.builder());
		this.register(
			EntityType.field_6117,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8687)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.field_6145,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(
							ItemEntry.builder(Items.field_8601)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8479)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8725)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8680)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8469)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8054)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8600)
								.setWeight(2)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.field_6119, LootTable.builder());
		this.register(
			EntityType.field_6076,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8713)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8606)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.field_10177))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(EntityType.field_6055, LootTable.builder());
		this.register(
			EntityType.field_6051,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8511)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8620))
						.withEntry(ItemEntry.builder(Items.field_8179))
						.withEntry(ItemEntry.builder(Items.field_8567))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(
			EntityType.field_6048,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8511)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.field_6050,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8511)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8397)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8695))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(
			EntityType.field_6054,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8511)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8620))
						.withEntry(ItemEntry.builder(Items.field_8179))
						.withEntry(ItemEntry.builder(Items.field_8567))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		Set<Identifier> set = Sets.newHashSet();

		for (EntityType<?> entityType : Registry.field_11145) {
			Identifier identifier = entityType.getLootTableId();
			if (!ENTITY_TYPES_IN_MISC_CATEGORY_TO_CHECK.contains(entityType) && entityType.getCategory() == EntityCategory.field_17715) {
				if (identifier != LootTables.EMPTY && this.lootTables.remove(identifier) != null) {
					throw new IllegalStateException(
						String.format("Weird loottable '%s' for '%s', not a LivingEntity so should not have loot", identifier, Registry.field_11145.getId(entityType))
					);
				}
			} else if (identifier != LootTables.EMPTY && set.add(identifier)) {
				LootTable.Builder builder = (LootTable.Builder)this.lootTables.remove(identifier);
				if (builder == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", identifier, Registry.field_11145.getId(entityType)));
				}

				biConsumer.accept(identifier, builder);
			}
		}

		this.lootTables.forEach(biConsumer::accept);
	}

	private void register(EntityType<?> entityType, LootTable.Builder builder) {
		this.register(entityType.getLootTableId(), builder);
	}

	private void register(Identifier identifier, LootTable.Builder builder) {
		this.lootTables.put(identifier, builder);
	}
}
