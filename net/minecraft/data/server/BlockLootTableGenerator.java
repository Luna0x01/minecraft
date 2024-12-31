package net.minecraft.data.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PotatoesBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.DynamicEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.CopyStateFunction;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.LimitCountLootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.SetContentsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.state.property.Property;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;

public class BlockLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	private static final LootCondition.Builder NEEDS_SILK_TOUCH = MatchToolLootCondition.builder(
		ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.field_9099, NumberRange.IntRange.atLeast(1)))
	);
	private static final LootCondition.Builder DOESNT_NEED_SILK_TOUCH = NEEDS_SILK_TOUCH.invert();
	private static final LootCondition.Builder NEEDS_SHEARS = MatchToolLootCondition.builder(ItemPredicate.Builder.create().item(Items.field_8868));
	private static final LootCondition.Builder NEEDS_SILK_TOUCH_SHEARS = NEEDS_SHEARS.withCondition(NEEDS_SILK_TOUCH);
	private static final LootCondition.Builder DOESNT_NEED_SILK_TOUCH_SHEARS = NEEDS_SILK_TOUCH_SHEARS.invert();
	private static final Set<Item> ALWAYS_DROPPED_FROM_EXPLOSION = (Set<Item>)Stream.of(
			Blocks.field_10081,
			Blocks.field_10327,
			Blocks.field_10502,
			Blocks.field_10481,
			Blocks.field_10177,
			Blocks.field_10432,
			Blocks.field_10241,
			Blocks.field_10042,
			Blocks.field_10337,
			Blocks.field_10603,
			Blocks.field_10371,
			Blocks.field_10605,
			Blocks.field_10373,
			Blocks.field_10532,
			Blocks.field_10140,
			Blocks.field_10055,
			Blocks.field_10203,
			Blocks.field_10320,
			Blocks.field_10275,
			Blocks.field_10063,
			Blocks.field_10407,
			Blocks.field_10051,
			Blocks.field_10268,
			Blocks.field_10068,
			Blocks.field_10199,
			Blocks.field_10600
		)
		.map(ItemConvertible::asItem)
		.collect(ImmutableSet.toImmutableSet());
	private static final float[] SAPLING_DROP_CHANCES_FROM_LEAVES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
	private static final float[] JUNGLE_SAPLING_DROP_CHANCES_FROM_LEAVES = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
	private final Map<Identifier, LootTable.Builder> lootTables = Maps.newHashMap();

	private static <T> T addExplosionDecayLootFunction(ItemConvertible itemConvertible, LootFunctionConsumingBuilder<T> lootFunctionConsumingBuilder) {
		return !ALWAYS_DROPPED_FROM_EXPLOSION.contains(itemConvertible.asItem())
			? lootFunctionConsumingBuilder.withFunction(ExplosionDecayLootFunction.builder())
			: lootFunctionConsumingBuilder.getThis();
	}

	private static <T> T addSurvivesExplosionLootCondition(ItemConvertible itemConvertible, LootConditionConsumingBuilder<T> lootConditionConsumingBuilder) {
		return !ALWAYS_DROPPED_FROM_EXPLOSION.contains(itemConvertible.asItem())
			? lootConditionConsumingBuilder.withCondition(SurvivesExplosionLootCondition.builder())
			: lootConditionConsumingBuilder.getThis();
	}

	private static LootTable.Builder create(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(
				addSurvivesExplosionLootCondition(
					itemConvertible, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible))
				)
			);
	}

	private static LootTable.Builder create(Block block, LootCondition.Builder builder, LootEntry.Builder<?> builder2) {
		return LootTable.builder()
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(block).withCondition(builder).withChild(builder2)));
	}

	private static LootTable.Builder createForNeedingSilkTouch(Block block, LootEntry.Builder<?> builder) {
		return create(block, NEEDS_SILK_TOUCH, builder);
	}

	private static LootTable.Builder createForNeedingShears(Block block, LootEntry.Builder<?> builder) {
		return create(block, NEEDS_SHEARS, builder);
	}

	private static LootTable.Builder createForNeedingSilkTouchShears(Block block, LootEntry.Builder<?> builder) {
		return create(block, NEEDS_SILK_TOUCH_SHEARS, builder);
	}

	private static LootTable.Builder createForBlockWithItemDrops(Block block, ItemConvertible itemConvertible) {
		return createForNeedingSilkTouch(block, (LootEntry.Builder<?>)addSurvivesExplosionLootCondition(block, ItemEntry.builder(itemConvertible)));
	}

	private static LootTable.Builder create(ItemConvertible itemConvertible, LootTableRange lootTableRange) {
		return LootTable.builder()
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						(LootEntry.Builder<?>)addExplosionDecayLootFunction(
							itemConvertible, ItemEntry.builder(itemConvertible).withFunction(SetCountLootFunction.builder(lootTableRange))
						)
					)
			);
	}

	private static LootTable.Builder createForBlockWithItemDrops(Block block, ItemConvertible itemConvertible, LootTableRange lootTableRange) {
		return createForNeedingSilkTouch(
			block,
			(LootEntry.Builder<?>)addExplosionDecayLootFunction(block, ItemEntry.builder(itemConvertible).withFunction(SetCountLootFunction.builder(lootTableRange)))
		);
	}

	private static LootTable.Builder createForNeedingSilkTouch(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(LootPool.builder().withCondition(NEEDS_SILK_TOUCH).withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible)));
	}

	private static LootTable.Builder createForPottedPlant(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(
				addSurvivesExplosionLootCondition(
					Blocks.field_10495, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Blocks.field_10495))
				)
			)
			.withPool(
				addSurvivesExplosionLootCondition(
					itemConvertible, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible))
				)
			);
	}

	private static LootTable.Builder createForSlabs(Block block) {
		return LootTable.builder()
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						(LootEntry.Builder<?>)addExplosionDecayLootFunction(
							block,
							ItemEntry.builder(block)
								.withFunction(
									SetCountLootFunction.builder(ConstantLootTableRange.create(2))
										.withCondition(
											BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.field_12682))
										)
								)
						)
					)
			);
	}

	private static <T extends Comparable<T> & StringIdentifiable> LootTable.Builder createForMultiblock(Block block, Property<T> property, T comparable) {
		return LootTable.builder()
			.withPool(
				addSurvivesExplosionLootCondition(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(block)
								.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(property, comparable)))
						)
				)
			);
	}

	private static LootTable.Builder createForNameableContainer(Block block) {
		return LootTable.builder()
			.withPool(
				addSurvivesExplosionLootCondition(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(block).withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.field_1023)))
				)
			);
	}

	private static LootTable.Builder createForShulkerBox(Block block) {
		return LootTable.builder()
			.withPool(
				addSurvivesExplosionLootCondition(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(block)
								.withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.field_1023))
								.withFunction(
									CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.field_17027)
										.withOperation("Lock", "BlockEntityTag.Lock")
										.withOperation("LootTable", "BlockEntityTag.LootTable")
										.withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed")
								)
								.withFunction(SetContentsLootFunction.builder().withEntry(DynamicEntry.builder(ShulkerBoxBlock.CONTENTS)))
						)
				)
			);
	}

	private static LootTable.Builder createForBanner(Block block) {
		return LootTable.builder()
			.withPool(
				addSurvivesExplosionLootCondition(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(block)
								.withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.field_1023))
								.withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.field_17027).withOperation("Patterns", "BlockEntityTag.Patterns"))
						)
				)
			);
	}

	private static LootTable.Builder createForBeeNest(Block block) {
		return LootTable.builder()
			.withPool(
				LootPool.builder()
					.withCondition(NEEDS_SILK_TOUCH)
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						ItemEntry.builder(block)
							.withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.field_17027).withOperation("Bees", "BlockEntityTag.Bees"))
							.withFunction(CopyStateFunction.getBuilder(block).method_21898(BeehiveBlock.HONEY_LEVEL))
					)
			);
	}

	private static LootTable.Builder createForBeehive(Block block) {
		return LootTable.builder()
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						ItemEntry.builder(block)
							.withCondition(NEEDS_SILK_TOUCH)
							.withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.field_17027).withOperation("Bees", "BlockEntityTag.Bees"))
							.withFunction(CopyStateFunction.getBuilder(block).method_21898(BeehiveBlock.HONEY_LEVEL))
							.withChild(ItemEntry.builder(block))
					)
			);
	}

	private static LootTable.Builder createForOreWithSingleItemDrop(Block block, Item item) {
		return createForNeedingSilkTouch(
			block,
			(LootEntry.Builder<?>)addExplosionDecayLootFunction(block, ItemEntry.builder(item).withFunction(ApplyBonusLootFunction.oreDrops(Enchantments.field_9130)))
		);
	}

	private static LootTable.Builder createForLargeMushroomBlock(Block block, ItemConvertible itemConvertible) {
		return createForNeedingSilkTouch(
			block,
			(LootEntry.Builder<?>)addExplosionDecayLootFunction(
				block,
				ItemEntry.builder(itemConvertible)
					.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-6.0F, 2.0F)))
					.withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMin(0)))
			)
		);
	}

	private static LootTable.Builder createForTallGrass(Block block) {
		return createForNeedingShears(
			block,
			(LootEntry.Builder<?>)addExplosionDecayLootFunction(
				block,
				ItemEntry.builder(Items.field_8317)
					.withCondition(RandomChanceLootCondition.builder(0.125F))
					.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130, 2))
			)
		);
	}

	private static LootTable.Builder createForCropStem(Block block, Item item) {
		return LootTable.builder()
			.withPool(
				addExplosionDecayLootFunction(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(item)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.06666667F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 0)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.13333334F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 1)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.2F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 2)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.26666668F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 3)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.33333334F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 4)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.4F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 5)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.46666667F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 6)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 7)))
								)
						)
				)
			);
	}

	private static LootTable.Builder createForAttachedCropStem(Block block, Item item) {
		return LootTable.builder()
			.withPool(
				addExplosionDecayLootFunction(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(item).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336F))))
				)
			);
	}

	private static LootTable.Builder createForBlockNeedingShears(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withCondition(NEEDS_SHEARS).withEntry(ItemEntry.builder(itemConvertible)));
	}

	private static LootTable.Builder createForLeaves(Block block, Block block2, float... fs) {
		return createForNeedingSilkTouchShears(
				block,
				((LeafEntry.Builder)addSurvivesExplosionLootCondition(block, ItemEntry.builder(block2)))
					.withCondition(TableBonusLootCondition.builder(Enchantments.field_9130, fs))
			)
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withCondition(DOESNT_NEED_SILK_TOUCH_SHEARS)
					.withEntry(
						((LeafEntry.Builder)addExplosionDecayLootFunction(
								block, ItemEntry.builder(Items.field_8600).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
							))
							.withCondition(TableBonusLootCondition.builder(Enchantments.field_9130, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))
					)
			);
	}

	private static LootTable.Builder createForOakLeaves(Block block, Block block2, float... fs) {
		return createForLeaves(block, block2, fs)
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withCondition(DOESNT_NEED_SILK_TOUCH_SHEARS)
					.withEntry(
						((LeafEntry.Builder)addSurvivesExplosionLootCondition(block, ItemEntry.builder(Items.field_8279)))
							.withCondition(TableBonusLootCondition.builder(Enchantments.field_9130, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))
					)
			);
	}

	private static LootTable.Builder createForCrops(Block block, Item item, Item item2, LootCondition.Builder builder) {
		return addExplosionDecayLootFunction(
			block,
			LootTable.builder()
				.withPool(LootPool.builder().withEntry(ItemEntry.builder(item).withCondition(builder).withChild(ItemEntry.builder(item2))))
				.withPool(
					LootPool.builder()
						.withCondition(builder)
						.withEntry(ItemEntry.builder(item2).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.field_9130, 0.5714286F, 3)))
				)
		);
	}

	public static LootTable.Builder createEmpty() {
		return LootTable.builder();
	}

	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		this.registerForSelfDrop(Blocks.field_10474);
		this.registerForSelfDrop(Blocks.field_10289);
		this.registerForSelfDrop(Blocks.field_10508);
		this.registerForSelfDrop(Blocks.field_10346);
		this.registerForSelfDrop(Blocks.field_10115);
		this.registerForSelfDrop(Blocks.field_10093);
		this.registerForSelfDrop(Blocks.field_10566);
		this.registerForSelfDrop(Blocks.field_10253);
		this.registerForSelfDrop(Blocks.field_10445);
		this.registerForSelfDrop(Blocks.field_10161);
		this.registerForSelfDrop(Blocks.field_9975);
		this.registerForSelfDrop(Blocks.field_10148);
		this.registerForSelfDrop(Blocks.field_10334);
		this.registerForSelfDrop(Blocks.field_10218);
		this.registerForSelfDrop(Blocks.field_10075);
		this.registerForSelfDrop(Blocks.field_10394);
		this.registerForSelfDrop(Blocks.field_10217);
		this.registerForSelfDrop(Blocks.field_10575);
		this.registerForSelfDrop(Blocks.field_10276);
		this.registerForSelfDrop(Blocks.field_10385);
		this.registerForSelfDrop(Blocks.field_10160);
		this.registerForSelfDrop(Blocks.field_10102);
		this.registerForSelfDrop(Blocks.field_10534);
		this.registerForSelfDrop(Blocks.field_10571);
		this.registerForSelfDrop(Blocks.field_10212);
		this.registerForSelfDrop(Blocks.field_10431);
		this.registerForSelfDrop(Blocks.field_10037);
		this.registerForSelfDrop(Blocks.field_10511);
		this.registerForSelfDrop(Blocks.field_10306);
		this.registerForSelfDrop(Blocks.field_10533);
		this.registerForSelfDrop(Blocks.field_10010);
		this.registerForSelfDrop(Blocks.field_10436);
		this.registerForSelfDrop(Blocks.field_10366);
		this.registerForSelfDrop(Blocks.field_10254);
		this.registerForSelfDrop(Blocks.field_10622);
		this.registerForSelfDrop(Blocks.field_10244);
		this.registerForSelfDrop(Blocks.field_10519);
		this.registerForSelfDrop(Blocks.field_10126);
		this.registerForSelfDrop(Blocks.field_10155);
		this.registerForSelfDrop(Blocks.field_10307);
		this.registerForSelfDrop(Blocks.field_10303);
		this.registerForSelfDrop(Blocks.field_9999);
		this.registerForSelfDrop(Blocks.field_10178);
		this.registerForSelfDrop(Blocks.field_10250);
		this.registerForSelfDrop(Blocks.field_10558);
		this.registerForSelfDrop(Blocks.field_10204);
		this.registerForSelfDrop(Blocks.field_10084);
		this.registerForSelfDrop(Blocks.field_10103);
		this.registerForSelfDrop(Blocks.field_10374);
		this.registerForSelfDrop(Blocks.field_10258);
		this.registerForSelfDrop(Blocks.field_10562);
		this.registerForSelfDrop(Blocks.field_10441);
		this.registerForSelfDrop(Blocks.field_9979);
		this.registerForSelfDrop(Blocks.field_10292);
		this.registerForSelfDrop(Blocks.field_10361);
		this.registerForSelfDrop(Blocks.field_10179);
		this.registerForSelfDrop(Blocks.field_10425);
		this.registerForSelfDrop(Blocks.field_10025);
		this.registerForSelfDrop(Blocks.field_10615);
		this.registerForSelfDrop(Blocks.field_10560);
		this.registerForSelfDrop(Blocks.field_10446);
		this.registerForSelfDrop(Blocks.field_10095);
		this.registerForSelfDrop(Blocks.field_10215);
		this.registerForSelfDrop(Blocks.field_10294);
		this.registerForSelfDrop(Blocks.field_10490);
		this.registerForSelfDrop(Blocks.field_10028);
		this.registerForSelfDrop(Blocks.field_10459);
		this.registerForSelfDrop(Blocks.field_10423);
		this.registerForSelfDrop(Blocks.field_10222);
		this.registerForSelfDrop(Blocks.field_10619);
		this.registerForSelfDrop(Blocks.field_10259);
		this.registerForSelfDrop(Blocks.field_10514);
		this.registerForSelfDrop(Blocks.field_10113);
		this.registerForSelfDrop(Blocks.field_10170);
		this.registerForSelfDrop(Blocks.field_10314);
		this.registerForSelfDrop(Blocks.field_10146);
		this.registerForSelfDrop(Blocks.field_10182);
		this.registerForSelfDrop(Blocks.field_10449);
		this.registerForSelfDrop(Blocks.field_10086);
		this.registerForSelfDrop(Blocks.field_10226);
		this.registerForSelfDrop(Blocks.field_10573);
		this.registerForSelfDrop(Blocks.field_10270);
		this.registerForSelfDrop(Blocks.field_10048);
		this.registerForSelfDrop(Blocks.field_10156);
		this.registerForSelfDrop(Blocks.field_10315);
		this.registerForSelfDrop(Blocks.field_10554);
		this.registerForSelfDrop(Blocks.field_9995);
		this.registerForSelfDrop(Blocks.field_10606);
		this.registerForSelfDrop(Blocks.field_10548);
		this.registerForSelfDrop(Blocks.field_10251);
		this.registerForSelfDrop(Blocks.field_10559);
		this.registerForSelfDrop(Blocks.field_10205);
		this.registerForSelfDrop(Blocks.field_10085);
		this.registerForSelfDrop(Blocks.field_10104);
		this.registerForSelfDrop(Blocks.field_9989);
		this.registerForSelfDrop(Blocks.field_10540);
		this.registerForSelfDrop(Blocks.field_10336);
		this.registerForSelfDrop(Blocks.field_10563);
		this.registerForSelfDrop(Blocks.field_10091);
		this.registerForSelfDrop(Blocks.field_10201);
		this.registerForSelfDrop(Blocks.field_9980);
		this.registerForSelfDrop(Blocks.field_10121);
		this.registerForSelfDrop(Blocks.field_10411);
		this.registerForSelfDrop(Blocks.field_10231);
		this.registerForSelfDrop(Blocks.field_10284);
		this.registerForSelfDrop(Blocks.field_10544);
		this.registerForSelfDrop(Blocks.field_10330);
		this.registerForSelfDrop(Blocks.field_9983);
		this.registerForSelfDrop(Blocks.field_10167);
		this.registerForSelfDrop(Blocks.field_10596);
		this.registerForSelfDrop(Blocks.field_10363);
		this.registerForSelfDrop(Blocks.field_10158);
		this.registerForSelfDrop(Blocks.field_10484);
		this.registerForSelfDrop(Blocks.field_10332);
		this.registerForSelfDrop(Blocks.field_10592);
		this.registerForSelfDrop(Blocks.field_10026);
		this.registerForSelfDrop(Blocks.field_10397);
		this.registerForSelfDrop(Blocks.field_10470);
		this.registerForSelfDrop(Blocks.field_10523);
		this.registerForSelfDrop(Blocks.field_10494);
		this.registerForSelfDrop(Blocks.field_10029);
		this.registerForSelfDrop(Blocks.field_10424);
		this.registerForSelfDrop(Blocks.field_10223);
		this.registerForSelfDrop(Blocks.field_10620);
		this.registerForSelfDrop(Blocks.field_10261);
		this.registerForSelfDrop(Blocks.field_10515);
		this.registerForSelfDrop(Blocks.field_10114);
		this.registerForSelfDrop(Blocks.field_10147);
		this.registerForSelfDrop(Blocks.field_10009);
		this.registerForSelfDrop(Blocks.field_10450);
		this.registerForSelfDrop(Blocks.field_10137);
		this.registerForSelfDrop(Blocks.field_10323);
		this.registerForSelfDrop(Blocks.field_10486);
		this.registerForSelfDrop(Blocks.field_10017);
		this.registerForSelfDrop(Blocks.field_10608);
		this.registerForSelfDrop(Blocks.field_10246);
		this.registerForSelfDrop(Blocks.field_10056);
		this.registerForSelfDrop(Blocks.field_10065);
		this.registerForSelfDrop(Blocks.field_10416);
		this.registerForSelfDrop(Blocks.field_10552);
		this.registerForSelfDrop(Blocks.field_10576);
		this.registerForSelfDrop(Blocks.field_10188);
		this.registerForSelfDrop(Blocks.field_10089);
		this.registerForSelfDrop(Blocks.field_10392);
		this.registerForSelfDrop(Blocks.field_10588);
		this.registerForSelfDrop(Blocks.field_10266);
		this.registerForSelfDrop(Blocks.field_10364);
		this.registerForSelfDrop(Blocks.field_10159);
		this.registerForSelfDrop(Blocks.field_10593);
		this.registerForSelfDrop(Blocks.field_10471);
		this.registerForSelfDrop(Blocks.field_10524);
		this.registerForSelfDrop(Blocks.field_10142);
		this.registerForSelfDrop(Blocks.field_10348);
		this.registerForSelfDrop(Blocks.field_10234);
		this.registerForSelfDrop(Blocks.field_10569);
		this.registerForSelfDrop(Blocks.field_10408);
		this.registerForSelfDrop(Blocks.field_10122);
		this.registerForSelfDrop(Blocks.field_10625);
		this.registerForSelfDrop(Blocks.field_9990);
		this.registerForSelfDrop(Blocks.field_10495);
		this.registerForSelfDrop(Blocks.field_10057);
		this.registerForSelfDrop(Blocks.field_10066);
		this.registerForSelfDrop(Blocks.field_10417);
		this.registerForSelfDrop(Blocks.field_10553);
		this.registerForSelfDrop(Blocks.field_10278);
		this.registerForSelfDrop(Blocks.field_10493);
		this.registerForSelfDrop(Blocks.field_10481);
		this.registerForSelfDrop(Blocks.field_10177);
		this.registerForSelfDrop(Blocks.field_10241);
		this.registerForSelfDrop(Blocks.field_10042);
		this.registerForSelfDrop(Blocks.field_10337);
		this.registerForSelfDrop(Blocks.field_10535);
		this.registerForSelfDrop(Blocks.field_10105);
		this.registerForSelfDrop(Blocks.field_10414);
		this.registerForSelfDrop(Blocks.field_10224);
		this.registerForSelfDrop(Blocks.field_10582);
		this.registerForSelfDrop(Blocks.field_10377);
		this.registerForSelfDrop(Blocks.field_10429);
		this.registerForSelfDrop(Blocks.field_10002);
		this.registerForSelfDrop(Blocks.field_10153);
		this.registerForSelfDrop(Blocks.field_10044);
		this.registerForSelfDrop(Blocks.field_10437);
		this.registerForSelfDrop(Blocks.field_10451);
		this.registerForSelfDrop(Blocks.field_10546);
		this.registerForSelfDrop(Blocks.field_10611);
		this.registerForSelfDrop(Blocks.field_10184);
		this.registerForSelfDrop(Blocks.field_10015);
		this.registerForSelfDrop(Blocks.field_10325);
		this.registerForSelfDrop(Blocks.field_10143);
		this.registerForSelfDrop(Blocks.field_10014);
		this.registerForSelfDrop(Blocks.field_10444);
		this.registerForSelfDrop(Blocks.field_10349);
		this.registerForSelfDrop(Blocks.field_10590);
		this.registerForSelfDrop(Blocks.field_10235);
		this.registerForSelfDrop(Blocks.field_10570);
		this.registerForSelfDrop(Blocks.field_10409);
		this.registerForSelfDrop(Blocks.field_10123);
		this.registerForSelfDrop(Blocks.field_10526);
		this.registerForSelfDrop(Blocks.field_10328);
		this.registerForSelfDrop(Blocks.field_10626);
		this.registerForSelfDrop(Blocks.field_10256);
		this.registerForSelfDrop(Blocks.field_10616);
		this.registerForSelfDrop(Blocks.field_10030);
		this.registerForSelfDrop(Blocks.field_10453);
		this.registerForSelfDrop(Blocks.field_10135);
		this.registerForSelfDrop(Blocks.field_10006);
		this.registerForSelfDrop(Blocks.field_10297);
		this.registerForSelfDrop(Blocks.field_10350);
		this.registerForSelfDrop(Blocks.field_10190);
		this.registerForSelfDrop(Blocks.field_10130);
		this.registerForSelfDrop(Blocks.field_10359);
		this.registerForSelfDrop(Blocks.field_10466);
		this.registerForSelfDrop(Blocks.field_9977);
		this.registerForSelfDrop(Blocks.field_10482);
		this.registerForSelfDrop(Blocks.field_10290);
		this.registerForSelfDrop(Blocks.field_10512);
		this.registerForSelfDrop(Blocks.field_10040);
		this.registerForSelfDrop(Blocks.field_10393);
		this.registerForSelfDrop(Blocks.field_10591);
		this.registerForSelfDrop(Blocks.field_10209);
		this.registerForSelfDrop(Blocks.field_10433);
		this.registerForSelfDrop(Blocks.field_10510);
		this.registerForSelfDrop(Blocks.field_10043);
		this.registerForSelfDrop(Blocks.field_10473);
		this.registerForSelfDrop(Blocks.field_10338);
		this.registerForSelfDrop(Blocks.field_10536);
		this.registerForSelfDrop(Blocks.field_10106);
		this.registerForSelfDrop(Blocks.field_10415);
		this.registerForSelfDrop(Blocks.field_10381);
		this.registerForSelfDrop(Blocks.field_10344);
		this.registerForSelfDrop(Blocks.field_10117);
		this.registerForSelfDrop(Blocks.field_10518);
		this.registerForSelfDrop(Blocks.field_10420);
		this.registerForSelfDrop(Blocks.field_10360);
		this.registerForSelfDrop(Blocks.field_10467);
		this.registerForSelfDrop(Blocks.field_9978);
		this.registerForSelfDrop(Blocks.field_10483);
		this.registerForSelfDrop(Blocks.field_10291);
		this.registerForSelfDrop(Blocks.field_10513);
		this.registerForSelfDrop(Blocks.field_10041);
		this.registerForSelfDrop(Blocks.field_10457);
		this.registerForSelfDrop(Blocks.field_10196);
		this.registerForSelfDrop(Blocks.field_10020);
		this.registerForSelfDrop(Blocks.field_10299);
		this.registerForSelfDrop(Blocks.field_10319);
		this.registerForSelfDrop(Blocks.field_10144);
		this.registerForSelfDrop(Blocks.field_10132);
		this.registerForSelfDrop(Blocks.field_10455);
		this.registerForSelfDrop(Blocks.field_10286);
		this.registerForSelfDrop(Blocks.field_10505);
		this.registerForSelfDrop(Blocks.field_9992);
		this.registerForSelfDrop(Blocks.field_10462);
		this.registerForSelfDrop(Blocks.field_10092);
		this.registerForSelfDrop(Blocks.field_10541);
		this.registerForSelfDrop(Blocks.field_9986);
		this.registerForSelfDrop(Blocks.field_10166);
		this.registerForSelfDrop(Blocks.field_10282);
		this.registerForSelfDrop(Blocks.field_10595);
		this.registerForSelfDrop(Blocks.field_10280);
		this.registerForSelfDrop(Blocks.field_10538);
		this.registerForSelfDrop(Blocks.field_10345);
		this.registerForSelfDrop(Blocks.field_10096);
		this.registerForSelfDrop(Blocks.field_10046);
		this.registerForSelfDrop(Blocks.field_10567);
		this.registerForSelfDrop(Blocks.field_10220);
		this.registerForSelfDrop(Blocks.field_10052);
		this.registerForSelfDrop(Blocks.field_10078);
		this.registerForSelfDrop(Blocks.field_10426);
		this.registerForSelfDrop(Blocks.field_10550);
		this.registerForSelfDrop(Blocks.field_10004);
		this.registerForSelfDrop(Blocks.field_10475);
		this.registerForSelfDrop(Blocks.field_10383);
		this.registerForSelfDrop(Blocks.field_10501);
		this.registerForSelfDrop(Blocks.field_10107);
		this.registerForSelfDrop(Blocks.field_10210);
		this.registerForSelfDrop(Blocks.field_10585);
		this.registerForSelfDrop(Blocks.field_10242);
		this.registerForSelfDrop(Blocks.field_10542);
		this.registerForSelfDrop(Blocks.field_10421);
		this.registerForSelfDrop(Blocks.field_10434);
		this.registerForSelfDrop(Blocks.field_10038);
		this.registerForSelfDrop(Blocks.field_10172);
		this.registerForSelfDrop(Blocks.field_10308);
		this.registerForSelfDrop(Blocks.field_10206);
		this.registerForSelfDrop(Blocks.field_10011);
		this.registerForSelfDrop(Blocks.field_10439);
		this.registerForSelfDrop(Blocks.field_10367);
		this.registerForSelfDrop(Blocks.field_10058);
		this.registerForSelfDrop(Blocks.field_10458);
		this.registerForSelfDrop(Blocks.field_10197);
		this.registerForSelfDrop(Blocks.field_10022);
		this.registerForSelfDrop(Blocks.field_10300);
		this.registerForSelfDrop(Blocks.field_10321);
		this.registerForSelfDrop(Blocks.field_10145);
		this.registerForSelfDrop(Blocks.field_10133);
		this.registerForSelfDrop(Blocks.field_10522);
		this.registerForSelfDrop(Blocks.field_10353);
		this.registerForSelfDrop(Blocks.field_10628);
		this.registerForSelfDrop(Blocks.field_10233);
		this.registerForSelfDrop(Blocks.field_10404);
		this.registerForSelfDrop(Blocks.field_10456);
		this.registerForSelfDrop(Blocks.field_10023);
		this.registerForSelfDrop(Blocks.field_10529);
		this.registerForSelfDrop(Blocks.field_10287);
		this.registerForSelfDrop(Blocks.field_10506);
		this.registerForSelfDrop(Blocks.field_9993);
		this.registerForSelfDrop(Blocks.field_10342);
		this.registerForSelfDrop(Blocks.field_10614);
		this.registerForSelfDrop(Blocks.field_10264);
		this.registerForSelfDrop(Blocks.field_10396);
		this.registerForSelfDrop(Blocks.field_10111);
		this.registerForSelfDrop(Blocks.field_10488);
		this.registerForSelfDrop(Blocks.field_10502);
		this.registerForSelfDrop(Blocks.field_10081);
		this.registerForSelfDrop(Blocks.field_10211);
		this.registerForSelfDrop(Blocks.field_10435);
		this.registerForSelfDrop(Blocks.field_10039);
		this.registerForSelfDrop(Blocks.field_10173);
		this.registerForSelfDrop(Blocks.field_10310);
		this.registerForSelfDrop(Blocks.field_10207);
		this.registerForSelfDrop(Blocks.field_10012);
		this.registerForSelfDrop(Blocks.field_10440);
		this.registerForSelfDrop(Blocks.field_10549);
		this.registerForSelfDrop(Blocks.field_10245);
		this.registerForSelfDrop(Blocks.field_10607);
		this.registerForSelfDrop(Blocks.field_10386);
		this.registerForSelfDrop(Blocks.field_10497);
		this.registerForSelfDrop(Blocks.field_9994);
		this.registerForSelfDrop(Blocks.field_10216);
		this.registerForSelfDrop(Blocks.field_10269);
		this.registerForSelfDrop(Blocks.field_10530);
		this.registerForSelfDrop(Blocks.field_10413);
		this.registerForSelfDrop(Blocks.field_10059);
		this.registerForSelfDrop(Blocks.field_10072);
		this.registerForSelfDrop(Blocks.field_10252);
		this.registerForSelfDrop(Blocks.field_10127);
		this.registerForSelfDrop(Blocks.field_10489);
		this.registerForSelfDrop(Blocks.field_10311);
		this.registerForSelfDrop(Blocks.field_10630);
		this.registerForSelfDrop(Blocks.field_10001);
		this.registerForSelfDrop(Blocks.field_10517);
		this.registerForSelfDrop(Blocks.field_10083);
		this.registerForSelfDrop(Blocks.field_16492);
		this.registerForSelfDrop(Blocks.field_21211);
		this.registerForSelfDrop(Blocks.field_21212);
		this.register(Blocks.field_10362, Blocks.field_10566);
		this.register(Blocks.field_10589, Items.field_8276);
		this.register(Blocks.field_10194, Blocks.field_10566);
		this.register(Blocks.field_10463, Blocks.field_9993);
		this.register(Blocks.field_10108, Blocks.field_10211);
		this.registerWithFunction(Blocks.field_10340, blockx -> createForBlockWithItemDrops(blockx, Blocks.field_10445));
		this.registerWithFunction(Blocks.field_10219, blockx -> createForBlockWithItemDrops(blockx, Blocks.field_10566));
		this.registerWithFunction(Blocks.field_10520, blockx -> createForBlockWithItemDrops(blockx, Blocks.field_10566));
		this.registerWithFunction(Blocks.field_10402, blockx -> createForBlockWithItemDrops(blockx, Blocks.field_10566));
		this.registerWithFunction(Blocks.field_10309, blockx -> createForBlockWithItemDrops(blockx, Blocks.field_10614));
		this.registerWithFunction(Blocks.field_10629, blockx -> createForBlockWithItemDrops(blockx, Blocks.field_10264));
		this.registerWithFunction(Blocks.field_10000, blockx -> createForBlockWithItemDrops(blockx, Blocks.field_10396));
		this.registerWithFunction(Blocks.field_10516, blockx -> createForBlockWithItemDrops(blockx, Blocks.field_10111));
		this.registerWithFunction(Blocks.field_10464, blockx -> createForBlockWithItemDrops(blockx, Blocks.field_10488));
		this.registerWithFunction(Blocks.field_10504, blockx -> createForBlockWithItemDrops(blockx, Items.field_8529, ConstantLootTableRange.create(3)));
		this.registerWithFunction(Blocks.field_10460, blockx -> createForBlockWithItemDrops(blockx, Items.field_8696, ConstantLootTableRange.create(4)));
		this.registerWithFunction(Blocks.field_10443, blockx -> createForBlockWithItemDrops(blockx, Blocks.field_10540, ConstantLootTableRange.create(8)));
		this.registerWithFunction(Blocks.field_10491, blockx -> createForBlockWithItemDrops(blockx, Items.field_8543, ConstantLootTableRange.create(4)));
		this.register(Blocks.field_10021, create(Items.field_8233, UniformLootTableRange.between(0.0F, 1.0F)));
		this.registerForPottedPlant(Blocks.field_10468);
		this.registerForPottedPlant(Blocks.field_10192);
		this.registerForPottedPlant(Blocks.field_10577);
		this.registerForPottedPlant(Blocks.field_10304);
		this.registerForPottedPlant(Blocks.field_10564);
		this.registerForPottedPlant(Blocks.field_10076);
		this.registerForPottedPlant(Blocks.field_10128);
		this.registerForPottedPlant(Blocks.field_10354);
		this.registerForPottedPlant(Blocks.field_10151);
		this.registerForPottedPlant(Blocks.field_9981);
		this.registerForPottedPlant(Blocks.field_10162);
		this.registerForPottedPlant(Blocks.field_10365);
		this.registerForPottedPlant(Blocks.field_10598);
		this.registerForPottedPlant(Blocks.field_10249);
		this.registerForPottedPlant(Blocks.field_10400);
		this.registerForPottedPlant(Blocks.field_10061);
		this.registerForPottedPlant(Blocks.field_10074);
		this.registerForPottedPlant(Blocks.field_10358);
		this.registerForPottedPlant(Blocks.field_10273);
		this.registerForPottedPlant(Blocks.field_9998);
		this.registerForPottedPlant(Blocks.field_10138);
		this.registerForPottedPlant(Blocks.field_10324);
		this.registerForPottedPlant(Blocks.field_10487);
		this.registerForPottedPlant(Blocks.field_10018);
		this.registerForPottedPlant(Blocks.field_10586);
		this.registerWithFunction(Blocks.field_10031, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10257, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10191, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10351, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10500, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10623, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10617, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10390, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10119, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10298, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10236, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10389, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10175, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10237, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10624, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10007, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_18891, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_18890, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10071, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10131, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10454, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10136, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10329, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10283, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10024, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10412, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10405, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10064, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10262, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10601, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10189, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10016, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10478, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10322, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10507, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.field_10232, blockx -> createForMultiblock(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.registerWithFunction(Blocks.field_10352, blockx -> createForMultiblock(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.registerWithFunction(Blocks.field_10403, blockx -> createForMultiblock(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.registerWithFunction(Blocks.field_9973, blockx -> createForMultiblock(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.registerWithFunction(Blocks.field_10627, blockx -> createForMultiblock(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.registerWithFunction(Blocks.field_10149, blockx -> createForMultiblock(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.registerWithFunction(Blocks.field_10521, blockx -> createForMultiblock(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.registerWithFunction(Blocks.field_10461, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10527, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10288, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10109, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10141, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10561, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10621, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10326, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10180, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10230, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10019, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10410, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10610, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10069, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10120, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10356, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.field_12560));
		this.registerWithFunction(Blocks.field_10378, blockx -> createForMultiblock(blockx, TallPlantBlock.HALF, DoubleBlockHalf.field_12607));
		this.registerWithFunction(Blocks.field_10583, blockx -> createForMultiblock(blockx, TallPlantBlock.HALF, DoubleBlockHalf.field_12607));
		this.registerWithFunction(Blocks.field_10003, blockx -> createForMultiblock(blockx, TallPlantBlock.HALF, DoubleBlockHalf.field_12607));
		this.registerWithFunction(Blocks.field_10430, blockx -> createForMultiblock(blockx, TallPlantBlock.HALF, DoubleBlockHalf.field_12607));
		this.register(
			Blocks.field_10375,
			LootTable.builder()
				.withPool(
					addSurvivesExplosionLootCondition(
						Blocks.field_10375,
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								ItemEntry.builder(Blocks.field_10375)
									.withCondition(
										BlockStatePropertyLootCondition.builder(Blocks.field_10375).method_22584(StatePredicate.Builder.create().exactMatch(TntBlock.UNSTABLE, false))
									)
							)
					)
				)
		);
		this.registerWithFunction(
			Blocks.field_10302,
			blockx -> LootTable.builder()
					.withPool(
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								(LootEntry.Builder<?>)addExplosionDecayLootFunction(
									blockx,
									ItemEntry.builder(Items.field_8116)
										.withFunction(
											SetCountLootFunction.builder(ConstantLootTableRange.create(3))
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(CocoaBlock.AGE, 2)))
										)
								)
							)
					)
		);
		this.registerWithFunction(
			Blocks.field_10476,
			blockx -> LootTable.builder()
					.withPool(
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								(LootEntry.Builder<?>)addExplosionDecayLootFunction(
									Blocks.field_10476,
									ItemEntry.builder(blockx)
										.withFunction(
											SetCountLootFunction.builder(ConstantLootTableRange.create(2))
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 2)))
										)
										.withFunction(
											SetCountLootFunction.builder(ConstantLootTableRange.create(3))
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 3)))
										)
										.withFunction(
											SetCountLootFunction.builder(ConstantLootTableRange.create(4))
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 4)))
										)
								)
							)
					)
		);
		this.registerWithFunction(
			Blocks.field_17563,
			blockx -> LootTable.builder()
					.withPool(LootPool.builder().withEntry((LootEntry.Builder<?>)addExplosionDecayLootFunction(blockx, ItemEntry.builder(Items.COMPOSTER))))
					.withPool(
						LootPool.builder()
							.withEntry(ItemEntry.builder(Items.field_8324))
							.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(ComposterBlock.LEVEL, 8)))
					)
		);
		this.registerWithFunction(Blocks.field_10327, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_10333, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_10034, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_10200, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_10228, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_10485, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_10181, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_10312, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_10380, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_16334, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_16333, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_16328, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_16336, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_16331, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_16337, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_16330, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_16329, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_16335, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.field_16332, BlockLootTableGenerator::create);
		this.registerWithFunction(Blocks.field_16541, BlockLootTableGenerator::create);
		this.registerWithFunction(Blocks.field_10603, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10371, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10605, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10373, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10532, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10140, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10055, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10203, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10320, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10275, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10063, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10407, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10051, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10268, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10068, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10199, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10600, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.field_10062, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10281, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10602, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10165, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10185, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10198, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10452, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_9985, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10229, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10438, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10045, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10612, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10368, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10406, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10154, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.field_10547, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(
			Blocks.field_10432,
			blockx -> LootTable.builder()
					.withPool(
						addSurvivesExplosionLootCondition(
							blockx,
							LootPool.builder()
								.withRolls(ConstantLootTableRange.create(1))
								.withEntry(
									ItemEntry.builder(blockx).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.field_17027).withOperation("Owner", "SkullOwner"))
								)
						)
					)
		);
		this.registerWithFunction(Blocks.field_20421, BlockLootTableGenerator::createForBeeNest);
		this.registerWithFunction(Blocks.field_20422, BlockLootTableGenerator::createForBeehive);
		this.registerWithFunction(Blocks.field_10539, blockx -> createForLeaves(blockx, Blocks.field_10575, SAPLING_DROP_CHANCES_FROM_LEAVES));
		this.registerWithFunction(Blocks.field_10098, blockx -> createForLeaves(blockx, Blocks.field_10385, SAPLING_DROP_CHANCES_FROM_LEAVES));
		this.registerWithFunction(Blocks.field_10335, blockx -> createForLeaves(blockx, Blocks.field_10276, JUNGLE_SAPLING_DROP_CHANCES_FROM_LEAVES));
		this.registerWithFunction(Blocks.field_9988, blockx -> createForLeaves(blockx, Blocks.field_10217, SAPLING_DROP_CHANCES_FROM_LEAVES));
		this.registerWithFunction(Blocks.field_10503, blockx -> createForOakLeaves(blockx, Blocks.field_10394, SAPLING_DROP_CHANCES_FROM_LEAVES));
		this.registerWithFunction(Blocks.field_10035, blockx -> createForOakLeaves(blockx, Blocks.field_10160, SAPLING_DROP_CHANCES_FROM_LEAVES));
		LootCondition.Builder builder = BlockStatePropertyLootCondition.builder(Blocks.field_10341)
			.method_22584(StatePredicate.Builder.create().exactMatch(BeetrootsBlock.AGE, 3));
		this.register(Blocks.field_10341, createForCrops(Blocks.field_10341, Items.field_8186, Items.field_8309, builder));
		LootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(Blocks.field_10293)
			.method_22584(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7));
		this.register(Blocks.field_10293, createForCrops(Blocks.field_10293, Items.field_8861, Items.field_8317, builder2));
		LootCondition.Builder builder3 = BlockStatePropertyLootCondition.builder(Blocks.field_10609)
			.method_22584(StatePredicate.Builder.create().exactMatch(CarrotsBlock.AGE, 7));
		this.register(
			Blocks.field_10609,
			addExplosionDecayLootFunction(
				Blocks.field_10609,
				LootTable.builder()
					.withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.field_8179)))
					.withPool(
						LootPool.builder()
							.withCondition(builder3)
							.withEntry(ItemEntry.builder(Items.field_8179).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.field_9130, 0.5714286F, 3)))
					)
			)
		);
		LootCondition.Builder builder4 = BlockStatePropertyLootCondition.builder(Blocks.field_10247)
			.method_22584(StatePredicate.Builder.create().exactMatch(PotatoesBlock.AGE, 7));
		this.register(
			Blocks.field_10247,
			addExplosionDecayLootFunction(
				Blocks.field_10247,
				LootTable.builder()
					.withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.field_8567)))
					.withPool(
						LootPool.builder()
							.withCondition(builder4)
							.withEntry(ItemEntry.builder(Items.field_8567).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.field_9130, 0.5714286F, 3)))
					)
					.withPool(
						LootPool.builder().withCondition(builder4).withEntry(ItemEntry.builder(Items.field_8635).withCondition(RandomChanceLootCondition.builder(0.02F)))
					)
			)
		);
		this.registerWithFunction(
			Blocks.field_16999,
			blockx -> addExplosionDecayLootFunction(
					blockx,
					LootTable.builder()
						.withPool(
							LootPool.builder()
								.withCondition(
									BlockStatePropertyLootCondition.builder(Blocks.field_16999).method_22584(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 3))
								)
								.withEntry(ItemEntry.builder(Items.field_16998))
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F)))
								.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
						)
						.withPool(
							LootPool.builder()
								.withCondition(
									BlockStatePropertyLootCondition.builder(Blocks.field_16999).method_22584(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 2))
								)
								.withEntry(ItemEntry.builder(Items.field_16998))
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
						)
				)
		);
		this.registerWithFunction(Blocks.field_10580, blockx -> createForLargeMushroomBlock(blockx, Blocks.field_10251));
		this.registerWithFunction(Blocks.field_10240, blockx -> createForLargeMushroomBlock(blockx, Blocks.field_10559));
		this.registerWithFunction(Blocks.field_10418, blockx -> createForOreWithSingleItemDrop(blockx, Items.field_8713));
		this.registerWithFunction(Blocks.field_10013, blockx -> createForOreWithSingleItemDrop(blockx, Items.field_8687));
		this.registerWithFunction(Blocks.field_10213, blockx -> createForOreWithSingleItemDrop(blockx, Items.field_8155));
		this.registerWithFunction(Blocks.field_10442, blockx -> createForOreWithSingleItemDrop(blockx, Items.field_8477));
		this.registerWithFunction(
			Blocks.field_10090,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx,
						ItemEntry.builder(Items.field_8759)
							.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F)))
							.withFunction(ApplyBonusLootFunction.oreDrops(Enchantments.field_9130))
					)
				)
		);
		this.registerWithFunction(
			Blocks.field_10343,
			blockx -> createForNeedingSilkTouchShears(blockx, (LootEntry.Builder<?>)addSurvivesExplosionLootCondition(blockx, ItemEntry.builder(Items.field_8276)))
		);
		this.registerWithFunction(
			Blocks.field_10428,
			blockx -> createForNeedingShears(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx, ItemEntry.builder(Items.field_8600).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
					)
				)
		);
		this.registerWithFunction(Blocks.field_10376, BlockLootTableGenerator::createForBlockNeedingShears);
		this.registerWithFunction(Blocks.field_10597, BlockLootTableGenerator::createForBlockNeedingShears);
		this.register(Blocks.field_10238, createForBlockNeedingShears(Blocks.field_10376));
		this.registerWithFunction(
			Blocks.field_10313,
			blockx -> createForNeedingShears(
					Blocks.field_10112,
					((LeafEntry.Builder)((LeafEntry.Builder)addSurvivesExplosionLootCondition(blockx, ItemEntry.builder(Items.field_8317)))
							.withCondition(
								BlockStatePropertyLootCondition.builder(blockx)
									.method_22584(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.field_12607))
							))
						.withCondition(RandomChanceLootCondition.builder(0.125F))
				)
		);
		this.register(
			Blocks.field_10214,
			createForNeedingShears(
				Blocks.field_10479,
				((LeafEntry.Builder)((LeafEntry.Builder)addSurvivesExplosionLootCondition(Blocks.field_10214, ItemEntry.builder(Items.field_8317)))
						.withCondition(
							BlockStatePropertyLootCondition.builder(Blocks.field_10214)
								.method_22584(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.field_12607))
						))
					.withCondition(RandomChanceLootCondition.builder(0.125F))
			)
		);
		this.registerWithFunction(Blocks.field_10168, blockx -> createForCropStem(blockx, Items.field_8188));
		this.registerWithFunction(Blocks.field_10150, blockx -> createForAttachedCropStem(blockx, Items.field_8188));
		this.registerWithFunction(Blocks.field_9984, blockx -> createForCropStem(blockx, Items.field_8706));
		this.registerWithFunction(Blocks.field_10331, blockx -> createForAttachedCropStem(blockx, Items.field_8706));
		this.registerWithFunction(
			Blocks.field_10528,
			blockx -> LootTable.builder()
					.withPool(
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								((LeafEntry.Builder)addSurvivesExplosionLootCondition(blockx, ItemEntry.builder(blockx)))
									.withCondition(EntityPropertiesLootCondition.create(LootContext.EntityTarget.field_935))
							)
					)
		);
		this.registerWithFunction(Blocks.field_10112, BlockLootTableGenerator::createForTallGrass);
		this.registerWithFunction(Blocks.field_10479, BlockLootTableGenerator::createForTallGrass);
		this.registerWithFunction(
			Blocks.field_10171,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx,
						ItemEntry.builder(Items.field_8601)
							.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F)))
							.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
							.withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4)))
					)
				)
		);
		this.registerWithFunction(
			Blocks.field_10545,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx,
						ItemEntry.builder(Items.field_8497)
							.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F)))
							.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
							.withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMax(9)))
					)
				)
		);
		this.registerWithFunction(
			Blocks.field_10080,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx,
						ItemEntry.builder(Items.field_8725)
							.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 5.0F)))
							.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
					)
				)
		);
		this.registerWithFunction(
			Blocks.field_10174,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx,
						ItemEntry.builder(Items.field_8434)
							.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F)))
							.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
							.withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 5)))
					)
				)
		);
		this.registerWithFunction(
			Blocks.field_9974,
			blockx -> LootTable.builder()
					.withPool(
						addExplosionDecayLootFunction(
							blockx,
							LootPool.builder()
								.withRolls(ConstantLootTableRange.create(1))
								.withEntry(
									ItemEntry.builder(Items.field_8790)
										.withFunction(
											SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3)))
										)
										.withFunction(
											ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3)))
										)
								)
						)
					)
		);
		this.registerWithFunction(
			Blocks.field_10477,
			blockx -> LootTable.builder()
					.withPool(
						LootPool.builder()
							.withCondition(EntityPropertiesLootCondition.create(LootContext.EntityTarget.field_935))
							.withEntry(
								AlternativeEntry.builder(
									AlternativeEntry.builder(
											ItemEntry.builder(Items.field_8543)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 1))),
											ItemEntry.builder(Items.field_8543)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2)))
												.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2))),
											ItemEntry.builder(Items.field_8543)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3)))
												.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(3))),
											ItemEntry.builder(Items.field_8543)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4)))
												.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(4))),
											ItemEntry.builder(Items.field_8543)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5)))
												.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(5))),
											ItemEntry.builder(Items.field_8543)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6)))
												.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(6))),
											ItemEntry.builder(Items.field_8543)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7)))
												.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(7))),
											ItemEntry.builder(Items.field_8543).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(8)))
										)
										.withCondition(DOESNT_NEED_SILK_TOUCH),
									AlternativeEntry.builder(
										ItemEntry.builder(Blocks.field_10477)
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 1))),
										ItemEntry.builder(Blocks.field_10477)
											.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2))),
										ItemEntry.builder(Blocks.field_10477)
											.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(3)))
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3))),
										ItemEntry.builder(Blocks.field_10477)
											.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(4)))
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4))),
										ItemEntry.builder(Blocks.field_10477)
											.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(5)))
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5))),
										ItemEntry.builder(Blocks.field_10477)
											.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(6)))
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6))),
										ItemEntry.builder(Blocks.field_10477)
											.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(7)))
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7))),
										ItemEntry.builder(Blocks.field_10491)
									)
								)
							)
					)
		);
		this.registerWithFunction(
			Blocks.field_10255,
			blockx -> createForNeedingSilkTouch(
					blockx,
					addSurvivesExplosionLootCondition(
						blockx,
						ItemEntry.builder(Items.field_8145)
							.withCondition(TableBonusLootCondition.builder(Enchantments.field_9130, 0.1F, 0.14285715F, 0.25F, 1.0F))
							.withChild(ItemEntry.builder(blockx))
					)
				)
		);
		this.registerWithFunction(
			Blocks.field_17350,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addSurvivesExplosionLootCondition(
						blockx, ItemEntry.builder(Items.field_8665).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))
					)
				)
		);
		this.registerForNeedingSilkTouch(Blocks.field_10033);
		this.registerForNeedingSilkTouch(Blocks.field_10087);
		this.registerForNeedingSilkTouch(Blocks.field_10227);
		this.registerForNeedingSilkTouch(Blocks.field_10574);
		this.registerForNeedingSilkTouch(Blocks.field_10271);
		this.registerForNeedingSilkTouch(Blocks.field_10049);
		this.registerForNeedingSilkTouch(Blocks.field_10157);
		this.registerForNeedingSilkTouch(Blocks.field_10317);
		this.registerForNeedingSilkTouch(Blocks.field_10555);
		this.registerForNeedingSilkTouch(Blocks.field_9996);
		this.registerForNeedingSilkTouch(Blocks.field_10248);
		this.registerForNeedingSilkTouch(Blocks.field_10399);
		this.registerForNeedingSilkTouch(Blocks.field_10060);
		this.registerForNeedingSilkTouch(Blocks.field_10073);
		this.registerForNeedingSilkTouch(Blocks.field_10357);
		this.registerForNeedingSilkTouch(Blocks.field_10272);
		this.registerForNeedingSilkTouch(Blocks.field_9997);
		this.registerForNeedingSilkTouch(Blocks.field_10285);
		this.registerForNeedingSilkTouch(Blocks.field_9991);
		this.registerForNeedingSilkTouch(Blocks.field_10496);
		this.registerForNeedingSilkTouch(Blocks.field_10469);
		this.registerForNeedingSilkTouch(Blocks.field_10193);
		this.registerForNeedingSilkTouch(Blocks.field_10578);
		this.registerForNeedingSilkTouch(Blocks.field_10305);
		this.registerForNeedingSilkTouch(Blocks.field_10565);
		this.registerForNeedingSilkTouch(Blocks.field_10077);
		this.registerForNeedingSilkTouch(Blocks.field_10129);
		this.registerForNeedingSilkTouch(Blocks.field_10355);
		this.registerForNeedingSilkTouch(Blocks.field_10152);
		this.registerForNeedingSilkTouch(Blocks.field_9982);
		this.registerForNeedingSilkTouch(Blocks.field_10163);
		this.registerForNeedingSilkTouch(Blocks.field_10419);
		this.registerForNeedingSilkTouch(Blocks.field_10118);
		this.registerForNeedingSilkTouch(Blocks.field_10070);
		this.registerForNeedingSilkTouch(Blocks.field_10295);
		this.registerForNeedingSilkTouch(Blocks.field_10225);
		this.registerForNeedingSilkTouch(Blocks.field_10384);
		this.registerForNeedingSilkTouch(Blocks.field_10195);
		this.registerForNeedingSilkTouch(Blocks.field_10556);
		this.registerForNeedingSilkTouch(Blocks.field_10082);
		this.registerForNeedingSilkTouch(Blocks.field_10572);
		this.registerForNeedingSilkTouch(Blocks.field_10296);
		this.registerForNeedingSilkTouch(Blocks.field_10579);
		this.registerForNeedingSilkTouch(Blocks.field_10032);
		this.registerForNeedingSilkTouch(Blocks.field_10125);
		this.registerForNeedingSilkTouch(Blocks.field_10339);
		this.registerForNeedingSilkTouch(Blocks.field_10134);
		this.registerForNeedingSilkTouch(Blocks.field_10618);
		this.registerForNeedingSilkTouch(Blocks.field_10169);
		this.registerForNeedingSilkTouch(Blocks.field_10448);
		this.registerForNeedingSilkTouch(Blocks.field_10097);
		this.registerForNeedingSilkTouch(Blocks.field_10047);
		this.registerForNeedingSilkTouch(Blocks.field_10568);
		this.registerForNeedingSilkTouch(Blocks.field_10221);
		this.registerForNeedingSilkTouch(Blocks.field_10053);
		this.registerForNeedingSilkTouch(Blocks.field_10079);
		this.registerForNeedingSilkTouch(Blocks.field_10427);
		this.registerForNeedingSilkTouch(Blocks.field_10551);
		this.registerForNeedingSilkTouch(Blocks.field_10005);
		this.registerForNeedingSilkTouch(Blocks.field_10277, Blocks.field_10340);
		this.registerForNeedingSilkTouch(Blocks.field_10492, Blocks.field_10445);
		this.registerForNeedingSilkTouch(Blocks.field_10387, Blocks.field_10056);
		this.registerForNeedingSilkTouch(Blocks.field_10480, Blocks.field_10065);
		this.registerForNeedingSilkTouch(Blocks.field_10100, Blocks.field_10416);
		this.registerForNeedingSilkTouch(Blocks.field_10176, Blocks.field_10552);
		this.register(Blocks.field_10183, createEmpty());
		this.register(Blocks.field_10110, createEmpty());
		this.register(Blocks.field_10260, createEmpty());
		Set<Identifier> set = Sets.newHashSet();

		for (Block block : Registry.field_11146) {
			Identifier identifier = block.getDropTableId();
			if (identifier != LootTables.EMPTY && set.add(identifier)) {
				LootTable.Builder builder5 = (LootTable.Builder)this.lootTables.remove(identifier);
				if (builder5 == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", identifier, Registry.field_11146.getId(block)));
				}

				biConsumer.accept(identifier, builder5);
			}
		}

		if (!this.lootTables.isEmpty()) {
			throw new IllegalStateException("Created block loot tables for non-blocks: " + this.lootTables.keySet());
		}
	}

	public void registerForPottedPlant(Block block) {
		this.registerWithFunction(block, blockx -> createForPottedPlant(((FlowerPotBlock)blockx).getContent()));
	}

	public void registerForNeedingSilkTouch(Block block, Block block2) {
		this.register(block, createForNeedingSilkTouch(block2));
	}

	public void register(Block block, ItemConvertible itemConvertible) {
		this.register(block, create(itemConvertible));
	}

	public void registerForNeedingSilkTouch(Block block) {
		this.registerForNeedingSilkTouch(block, block);
	}

	public void registerForSelfDrop(Block block) {
		this.register(block, block);
	}

	private void registerWithFunction(Block block, Function<Block, LootTable.Builder> function) {
		this.register(block, (LootTable.Builder)function.apply(block));
	}

	private void register(Block block, LootTable.Builder builder) {
		this.lootTables.put(block.getDropTableId(), builder);
	}
}
