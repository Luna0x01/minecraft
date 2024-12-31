package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_3902;
import net.minecraft.block.BlockState;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

public abstract class SingletonBiomeSource implements Tickable {
	private static final List<Biome> field_17663 = Lists.newArrayList(
		new Biome[]{Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.FOREST_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS}
	);
	protected final Map<class_3902<?>, Boolean> field_17661 = Maps.newHashMap();
	protected final Set<BlockState> field_17662 = Sets.newHashSet();

	protected SingletonBiomeSource() {
	}

	public List<Biome> method_11532() {
		return field_17663;
	}

	@Override
	public void tick() {
	}

	@Nullable
	public abstract Biome method_16480(BlockPos blockPos, @Nullable Biome biome);

	public abstract Biome[] method_16476(int i, int j, int k, int l);

	public Biome[] method_11540(int i, int j, int k, int l) {
		return this.method_16477(i, j, k, l, true);
	}

	public abstract Biome[] method_16477(int i, int j, int k, int l, boolean bl);

	public abstract Set<Biome> method_16475(int i, int j, int k);

	@Nullable
	public abstract BlockPos method_16478(int i, int j, int k, List<Biome> list, Random random);

	public float method_16482(int i, int j, int k, int l) {
		return 0.0F;
	}

	public abstract boolean method_16479(class_3902<?> arg);

	public abstract Set<BlockState> method_16481();
}
