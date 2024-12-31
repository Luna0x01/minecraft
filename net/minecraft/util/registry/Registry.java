package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.ContainerType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.structure.StructureFeatures;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.IndexedIterable;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.TreeDecoratorType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.placer.BlockPlacerType;
import net.minecraft.world.gen.stateprovider.StateProviderType;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Registry<T> implements IndexedIterable<T> {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES = Maps.newLinkedHashMap();
	public static final MutableRegistry<MutableRegistry<?>> REGISTRIES = new SimpleRegistry<>();
	public static final Registry<SoundEvent> field_11156 = create("sound_event", () -> SoundEvents.field_15197);
	public static final DefaultedRegistry<Fluid> field_11154 = create("fluid", "empty", () -> Fluids.field_15906);
	public static final Registry<StatusEffect> STATUS_EFFECT = create("mob_effect", () -> StatusEffects.field_5926);
	public static final DefaultedRegistry<Block> field_11146 = create("block", "air", () -> Blocks.field_10124);
	public static final Registry<Enchantment> field_11160 = create("enchantment", () -> Enchantments.field_9130);
	public static final DefaultedRegistry<EntityType<?>> field_11145 = create("entity_type", "pig", () -> EntityType.field_6093);
	public static final DefaultedRegistry<Item> field_11142 = create("item", "air", () -> Items.AIR);
	public static final DefaultedRegistry<Potion> field_11143 = create("potion", "empty", () -> Potions.field_8984);
	public static final Registry<Carver<?>> field_11157 = create("carver", () -> Carver.field_13304);
	public static final Registry<SurfaceBuilder<?>> field_11147 = create("surface_builder", () -> SurfaceBuilder.field_15701);
	public static final Registry<Feature<?>> field_11138 = create("feature", () -> Feature.field_13517);
	public static final Registry<Decorator<?>> field_11148 = create("decorator", () -> Decorator.field_14250);
	public static final Registry<Biome> field_11153 = create("biome", () -> Biomes.DEFAULT);
	public static final Registry<StateProviderType<?>> field_21445 = create("block_state_provider_type", () -> StateProviderType.field_21305);
	public static final Registry<BlockPlacerType<?>> field_21446 = create("block_placer_type", () -> BlockPlacerType.field_21223);
	public static final Registry<FoliagePlacerType<?>> field_21447 = create("foliage_placer_type", () -> FoliagePlacerType.field_21299);
	public static final Registry<TreeDecoratorType<?>> field_21448 = create("tree_decorator_type", () -> TreeDecoratorType.field_21321);
	public static final Registry<ParticleType<? extends ParticleEffect>> field_11141 = create("particle_type", () -> ParticleTypes.field_11217);
	public static final Registry<BiomeSourceType<?, ?>> field_11151 = create("biome_source_type", () -> BiomeSourceType.VANILLA_LAYERED);
	public static final Registry<BlockEntityType<?>> field_11137 = create("block_entity_type", () -> BlockEntityType.field_11903);
	public static final Registry<ChunkGeneratorType<?, ?>> field_11149 = create("chunk_generator_type", () -> ChunkGeneratorType.field_12766);
	public static final Registry<DimensionType> field_11155 = create("dimension_type", () -> DimensionType.field_13072);
	public static final DefaultedRegistry<PaintingMotive> field_11150 = create("motive", "kebab", () -> PaintingMotive.field_7146);
	public static final Registry<Identifier> field_11158 = create("custom_stat", () -> Stats.field_15428);
	public static final DefaultedRegistry<ChunkStatus> field_16643 = create("chunk_status", "empty", () -> ChunkStatus.field_12798);
	public static final Registry<StructureFeature<?>> field_16644 = create("structure_feature", () -> StructureFeatures.field_16709);
	public static final Registry<StructurePieceType> field_16645 = create("structure_piece", () -> StructurePieceType.MINESHAFT_ROOM);
	public static final Registry<RuleTest> field_16792 = create("rule_test", () -> RuleTest.field_16982);
	public static final Registry<StructureProcessorType> field_16794 = create("structure_processor", () -> StructureProcessorType.field_16986);
	public static final Registry<StructurePoolElementType> field_16793 = create("structure_pool_element", () -> StructurePoolElementType.field_16972);
	public static final Registry<ContainerType<?>> CONTAINER = create("menu", () -> ContainerType.field_17329);
	public static final Registry<RecipeType<?>> field_17597 = create("recipe_type", () -> RecipeType.CRAFTING);
	public static final Registry<RecipeSerializer<?>> field_17598 = create("recipe_serializer", () -> RecipeSerializer.SHAPELESS);
	public static final Registry<StatType<?>> field_11152 = create("stat_type", () -> Stats.field_15372);
	public static final DefaultedRegistry<VillagerType> field_17166 = create("villager_type", "plains", () -> VillagerType.PLAINS);
	public static final DefaultedRegistry<VillagerProfession> field_17167 = create("villager_profession", "none", () -> VillagerProfession.field_17051);
	public static final DefaultedRegistry<PointOfInterestType> field_18792 = create("point_of_interest_type", "unemployed", () -> PointOfInterestType.field_18502);
	public static final DefaultedRegistry<MemoryModuleType<?>> field_18793 = create("memory_module_type", "dummy", () -> MemoryModuleType.field_18437);
	public static final DefaultedRegistry<SensorType<?>> field_18794 = create("sensor_type", "dummy", () -> SensorType.field_18465);
	public static final Registry<Schedule> field_18795 = create("schedule", () -> Schedule.EMPTY);
	public static final Registry<Activity> field_18796 = create("activity", () -> Activity.field_18595);

	private static <T> Registry<T> create(String string, Supplier<T> supplier) {
		return putDefaultEntry(string, new SimpleRegistry<>(), supplier);
	}

	private static <T> DefaultedRegistry<T> create(String string, String string2, Supplier<T> supplier) {
		return putDefaultEntry(string, new DefaultedRegistry<>(string2), supplier);
	}

	private static <T, R extends MutableRegistry<T>> R putDefaultEntry(String string, R mutableRegistry, Supplier<T> supplier) {
		Identifier identifier = new Identifier(string);
		DEFAULT_ENTRIES.put(identifier, supplier);
		return REGISTRIES.add(identifier, mutableRegistry);
	}

	@Nullable
	public abstract Identifier getId(T object);

	public abstract int getRawId(@Nullable T object);

	@Nullable
	public abstract T get(@Nullable Identifier identifier);

	public abstract Optional<T> getOrEmpty(@Nullable Identifier identifier);

	public abstract Set<Identifier> getIds();

	@Nullable
	public abstract T getRandom(Random random);

	public Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	public abstract boolean containsId(Identifier identifier);

	public static <T> T register(Registry<? super T> registry, String string, T object) {
		return register(registry, new Identifier(string), object);
	}

	public static <T> T register(Registry<? super T> registry, Identifier identifier, T object) {
		return ((MutableRegistry)registry).add(identifier, object);
	}

	public static <T> T register(Registry<? super T> registry, int i, String string, T object) {
		return ((MutableRegistry)registry).set(i, new Identifier(string), object);
	}

	static {
		DEFAULT_ENTRIES.entrySet().forEach(entry -> {
			if (((Supplier)entry.getValue()).get() == null) {
				LOGGER.error("Unable to bootstrap registry '{}'", entry.getKey());
			}
		});
		REGISTRIES.forEach(mutableRegistry -> {
			if (mutableRegistry.isEmpty()) {
				LOGGER.error("Registry '{}' was empty after loading", REGISTRIES.getId(mutableRegistry));
				if (SharedConstants.isDevelopment) {
					throw new IllegalStateException("Registry: '" + REGISTRIES.getId(mutableRegistry) + "' is empty, not allowed, fix me!");
				}
			}

			if (mutableRegistry instanceof DefaultedRegistry) {
				Identifier identifier = ((DefaultedRegistry)mutableRegistry).getDefaultId();
				Validate.notNull(mutableRegistry.get(identifier), "Missing default of DefaultedMappedRegistry: " + identifier, new Object[0]);
			}
		});
	}
}
