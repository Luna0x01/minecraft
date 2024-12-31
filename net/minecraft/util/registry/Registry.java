package net.minecraft.util.registry;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.Painting;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.potion.Potion;
import net.minecraft.sound.Sound;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.collection.ObjectIdIterable;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeSourceType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Registry<T> extends ObjectIdIterable<T> {
	Logger logger = LogManager.getLogger();
	Registry<Registry<?>> ROOT = new SimpleRegistry<>();
	Registry<Block> BLOCK = create("block", new BiDefaultedRegistry<>(new Identifier("air")));
	Registry<Fluid> FLUID = create("fluid", new BiDefaultedRegistry<>(new Identifier("empty")));
	Registry<Painting> PAINTING = create("motive", new BiDefaultedRegistry<>(new Identifier("kebab")));
	Registry<Potion> POTION = create("potion", new BiDefaultedRegistry<>(new Identifier("empty")));
	Registry<DimensionType> DIMENSION_TYPE = create("dimension_type", new SimpleRegistry<>());
	Registry<Identifier> CUSTOM_STAT = create("custom_stat", new SimpleRegistry<>());
	Registry<Biome> BIOME = create("biome", new SimpleRegistry<>());
	Registry<BiomeSourceType<?, ?>> BIOME_SOURCE_TYPE = create("biome_source_type", new SimpleRegistry<>());
	Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = create("block_entity_type", new SimpleRegistry<>());
	Registry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPE = create("chunk_generator_type", new SimpleRegistry<>());
	Registry<Enchantment> ENCHANTMENT = create("enchantment", new SimpleRegistry<>());
	Registry<EntityType<?>> ENTITY_TYPE = create("entity_type", new SimpleRegistry<>());
	Registry<Item> ITEM = create("item", new SimpleRegistry<>());
	Registry<StatusEffect> MOB_EFFECT = create("mob_effect", new SimpleRegistry<>());
	Registry<ParticleType<? extends ParticleEffect>> PARTICLE_TYPE = create("particle_type", new SimpleRegistry<>());
	Registry<Sound> SOUND_EVENT = create("sound_event", new SimpleRegistry<>());
	Registry<StatType<?>> STATS = create("stats", new SimpleRegistry<>());

	static <T> Registry<T> create(String identifier, Registry<T> registry) {
		ROOT.add(new Identifier(identifier), registry);
		return registry;
	}

	static void validate() {
		ROOT.forEach(registry -> {
			if (registry.isEmpty()) {
				logger.error("Registry '{}' was empty after loading", ROOT.getId(registry));
				if (SharedConstants.isDevelopment) {
					throw new IllegalStateException("Registry: '" + ROOT.getId(registry) + "' is empty, not allowed, fix me!");
				}
			}

			if (registry instanceof BiDefaultedRegistry) {
				Identifier identifier = registry.getDefaultId();
				Validate.notNull(registry.getByIdentifier(identifier), "Missing default of DefaultedMappedRegistry: " + identifier, new Object[0]);
			}
		});
	}

	@Nullable
	Identifier getId(T object);

	T get(@Nullable Identifier identifier);

	Identifier getDefaultId();

	int getRawId(@Nullable T object);

	@Nullable
	T getByRawId(int rawId);

	Iterator<T> iterator();

	@Nullable
	T getByIdentifier(@Nullable Identifier identifier);

	void set(int rawId, Identifier identifier, T object);

	void add(Identifier identifier, T object);

	Set<Identifier> getKeySet();

	boolean isEmpty();

	@Nullable
	T getRandom(Random random);

	default Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	boolean containsId(Identifier identifier);
}
