package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.palette.PaletteData;

public class class_3804 {
	private final PaletteData field_19006 = new PaletteData(9, 256);
	private final class_3761<BlockState> field_19007;
	private final class_3781 field_19008;

	public class_3804(class_3781 arg, class_3804.class_3805 arg2) {
		this.field_19007 = class_3762.method_16948(class_3762.method_16950(arg2.method_17247()));
		this.field_19008 = arg;
	}

	public void method_17238() {
		int i = this.field_19008.method_17001() + 16;

		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					this.method_17241(j, k, this.method_17243(pooled, j, k, this.field_19007, i));
				}
			}
		}
	}

	public boolean method_17242(int i, int j, int k, @Nullable BlockState blockState) {
		int l = this.method_17240(i, k);
		if (j <= l - 2) {
			return false;
		} else {
			if (this.field_19007.test(blockState, this.field_19008, new BlockPos(i, j, k))) {
				if (j >= l) {
					this.method_17241(i, k, j + 1);
					return true;
				}
			} else if (l - 1 == j) {
				this.method_17241(i, k, this.method_17243(null, i, k, this.field_19007, j));
				return true;
			}

			return false;
		}
	}

	private int method_17243(@Nullable BlockPos.Mutable mutable, int i, int j, class_3761<BlockState> arg, int k) {
		if (mutable == null) {
			mutable = new BlockPos.Mutable();
		}

		for (int l = k - 1; l >= 0; l--) {
			mutable.setPosition(i, l, j);
			BlockState blockState = this.field_19008.getBlockState(mutable);
			if (arg.test(blockState, this.field_19008, mutable)) {
				return l + 1;
			}
		}

		return 0;
	}

	public int method_17240(int i, int j) {
		return this.method_17239(method_17246(i, j));
	}

	private int method_17239(int i) {
		return this.field_19006.get(i);
	}

	private void method_17241(int i, int j, int k) {
		this.field_19006.set(method_17246(i, j), k);
	}

	public void method_17244(long[] ls) {
		System.arraycopy(ls, 0, this.field_19006.getBlockStateIds(), 0, ls.length);
	}

	public long[] method_17245() {
		return this.field_19006.getBlockStateIds();
	}

	private static int method_17246(int i, int j) {
		return i + j * 16;
	}

	public static enum class_3805 {
		WORLD_SURFACE_WG("WORLD_SURFACE_WG", class_3804.class_3806.WORLDGEN, class_3760.method_16942(Blocks.AIR)),
		OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", class_3804.class_3806.WORLDGEN, class_3760.method_16942(Blocks.AIR), class_3766.method_16954()),
		LIGHT_BLOCKING("LIGHT_BLOCKING", class_3804.class_3806.LIVE_WORLD, class_3760.method_16942(Blocks.AIR), class_3765.method_16952()),
		MOTION_BLOCKING("MOTION_BLOCKING", class_3804.class_3806.LIVE_WORLD, class_3760.method_16942(Blocks.AIR), class_3767.method_16956()),
		MOTION_BLOCKING_NO_LEAVES(
			"MOTION_BLOCKING_NO_LEAVES",
			class_3804.class_3806.LIVE_WORLD,
			class_3760.method_16942(Blocks.AIR),
			class_3769.method_16961(BlockTags.LEAVES),
			class_3767.method_16956()
		),
		OCEAN_FLOOR("OCEAN_FLOOR", class_3804.class_3806.LIVE_WORLD, class_3760.method_16942(Blocks.AIR), class_3768.method_16958()),
		WORLD_SURFACE("WORLD_SURFACE", class_3804.class_3806.LIVE_WORLD, class_3760.method_16942(Blocks.AIR));

		private final class_3761<BlockState>[] field_19016;
		private final String field_19017;
		private final class_3804.class_3806 field_19018;
		private static final Map<String, class_3804.class_3805> field_19019 = Util.make(Maps.newHashMap(), hashMap -> {
			for (class_3804.class_3805 lv : values()) {
				hashMap.put(lv.field_19017, lv);
			}
		});

		private class_3805(String string2, class_3804.class_3806 arg, class_3761<BlockState>... args) {
			this.field_19017 = string2;
			this.field_19016 = args;
			this.field_19018 = arg;
		}

		public class_3761<BlockState>[] method_17247() {
			return this.field_19016;
		}

		public String method_17250() {
			return this.field_19017;
		}

		public class_3804.class_3806 method_17251() {
			return this.field_19018;
		}

		public static class_3804.class_3805 method_17248(String string) {
			return (class_3804.class_3805)field_19019.get(string);
		}
	}

	public static enum class_3806 {
		WORLDGEN,
		LIVE_WORLD;
	}
}
