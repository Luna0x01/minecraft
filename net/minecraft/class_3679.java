package net.minecraft;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.SingletonBiomeSource;

public class class_3679 extends SingletonBiomeSource {
	private final NoiseSampler field_17707;
	private final class_3812 field_17708;
	private final Biome[] field_17709 = new Biome[]{Biomes.SKY, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS};

	public class_3679(class_3680 arg) {
		this.field_17708 = new class_3812(arg.method_16543());
		this.field_17708.method_17285(17292);
		this.field_17707 = new NoiseSampler(this.field_17708);
	}

	@Nullable
	@Override
	public Biome method_16480(BlockPos blockPos, @Nullable Biome biome) {
		return this.method_16541(blockPos.getX() >> 4, blockPos.getZ() >> 4);
	}

	private Biome method_16541(int i, int j) {
		if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
			return Biomes.SKY;
		} else {
			float f = this.method_16482(i, j, 1, 1);
			if (f > 40.0F) {
				return Biomes.END_HIGHLANDS;
			} else if (f >= 0.0F) {
				return Biomes.END_MIDLANDS;
			} else {
				return f < -20.0F ? Biomes.SMALL_END_ISLANDS : Biomes.END_BARRENS;
			}
		}
	}

	@Override
	public Biome[] method_16476(int i, int j, int k, int l) {
		return this.method_11540(i, j, k, l);
	}

	@Override
	public Biome[] method_16477(int i, int j, int k, int l, boolean bl) {
		Biome[] biomes = new Biome[k * l];
		Long2ObjectMap<Biome> long2ObjectMap = new Long2ObjectOpenHashMap();

		for (int m = 0; m < k; m++) {
			for (int n = 0; n < l; n++) {
				int o = m + i >> 4;
				int p = n + j >> 4;
				long q = ChunkPos.getIdFromCoords(o, p);
				Biome biome = (Biome)long2ObjectMap.get(q);
				if (biome == null) {
					biome = this.method_16541(o, p);
					long2ObjectMap.put(q, biome);
				}

				biomes[m + n * k] = biome;
			}
		}

		return biomes;
	}

	@Override
	public Set<Biome> method_16475(int i, int j, int k) {
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		return Sets.newHashSet(this.method_11540(l, m, p, q));
	}

	@Nullable
	@Override
	public BlockPos method_16478(int i, int j, int k, List<Biome> list, Random random) {
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		Biome[] biomes = this.method_11540(l, m, p, q);
		BlockPos blockPos = null;
		int r = 0;

		for (int s = 0; s < p * q; s++) {
			int t = l + s % p << 2;
			int u = m + s / p << 2;
			if (list.contains(biomes[s])) {
				if (blockPos == null || random.nextInt(r + 1) == 0) {
					blockPos = new BlockPos(t, 0, u);
				}

				r++;
			}
		}

		return blockPos;
	}

	@Override
	public float method_16482(int i, int j, int k, int l) {
		float f = (float)(i * 2 + k);
		float g = (float)(j * 2 + l);
		float h = 100.0F - MathHelper.sqrt(f * f + g * g) * 8.0F;
		h = MathHelper.clamp(h, -100.0F, 80.0F);

		for (int m = -12; m <= 12; m++) {
			for (int n = -12; n <= 12; n++) {
				long o = (long)(i + m);
				long p = (long)(j + n);
				if (o * o + p * p > 4096L && this.field_17707.sample((double)o, (double)p) < -0.9F) {
					float q = (MathHelper.abs((float)o) * 3439.0F + MathHelper.abs((float)p) * 147.0F) % 13.0F + 9.0F;
					f = (float)(k - m * 2);
					g = (float)(l - n * 2);
					float r = 100.0F - MathHelper.sqrt(f * f + g * g) * q;
					r = MathHelper.clamp(r, -100.0F, 80.0F);
					h = Math.max(h, r);
				}
			}
		}

		return h;
	}

	@Override
	public boolean method_16479(class_3902<?> arg) {
		return (Boolean)this.field_17661.computeIfAbsent(arg, argx -> {
			for (Biome biome : this.field_17709) {
				if (biome.method_16435(argx)) {
					return true;
				}
			}

			return false;
		});
	}

	@Override
	public Set<BlockState> method_16481() {
		if (this.field_17662.isEmpty()) {
			for (Biome biome : this.field_17709) {
				this.field_17662.add(biome.method_16450().method_17720());
			}
		}

		return this.field_17662;
	}
}
