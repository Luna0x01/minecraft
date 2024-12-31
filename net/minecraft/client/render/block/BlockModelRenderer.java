package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3600;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.BlockColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class BlockModelRenderer {
	private final BlockColors field_13551;
	private static final ThreadLocal<Object2IntLinkedOpenHashMap<BlockPos>> field_20772 = ThreadLocal.withInitial(() -> {
		Object2IntLinkedOpenHashMap<BlockPos> object2IntLinkedOpenHashMap = new Object2IntLinkedOpenHashMap<BlockPos>(50) {
			protected void rehash(int i) {
			}
		};
		object2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
		return object2IntLinkedOpenHashMap;
	});
	private static final ThreadLocal<Boolean> field_20773 = ThreadLocal.withInitial(() -> false);

	public BlockModelRenderer(BlockColors blockColors) {
		this.field_13551 = blockColors;
	}

	public boolean method_19197(
		class_3600 arg, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean bl, Random random, long l
	) {
		boolean bl2 = MinecraftClient.isAmbientOcclusionEnabled() && blockState.getLuminance() == 0 && bakedModel.useAmbientOcclusion();

		try {
			return bl2
				? this.method_9961(arg, bakedModel, blockState, blockPos, bufferBuilder, bl, random, l)
				: this.method_9967(arg, bakedModel, blockState, blockPos, bufferBuilder, bl, random, l);
		} catch (Throwable var14) {
			CrashReport crashReport = CrashReport.create(var14, "Tesselating block model");
			CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
			crashReportSection.add("Using AO", bl2);
			throw new CrashException(crashReport);
		}
	}

	public boolean method_9961(
		class_3600 arg, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean bl, Random random, long l
	) {
		boolean bl2 = false;
		float[] fs = new float[Direction.values().length * 2];
		BitSet bitSet = new BitSet(3);
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator = new BlockModelRenderer.AmbientOcclusionCalculator();

		for (Direction direction : Direction.values()) {
			random.setSeed(l);
			List<BakedQuad> list = bakedModel.method_19561(blockState, direction, random);
			if (!list.isEmpty() && (!bl || Block.method_16586(blockState, arg, blockPos, direction))) {
				this.method_12348(arg, blockState, blockPos, bufferBuilder, list, fs, bitSet, ambientOcclusionCalculator);
				bl2 = true;
			}
		}

		random.setSeed(l);
		List<BakedQuad> list2 = bakedModel.method_19561(blockState, null, random);
		if (!list2.isEmpty()) {
			this.method_12348(arg, blockState, blockPos, bufferBuilder, list2, fs, bitSet, ambientOcclusionCalculator);
			bl2 = true;
		}

		return bl2;
	}

	public boolean method_9967(
		class_3600 arg, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean bl, Random random, long l
	) {
		boolean bl2 = false;
		BitSet bitSet = new BitSet(3);

		for (Direction direction : Direction.values()) {
			random.setSeed(l);
			List<BakedQuad> list = bakedModel.method_19561(blockState, direction, random);
			if (!list.isEmpty() && (!bl || Block.method_16586(blockState, arg, blockPos, direction))) {
				int i = blockState.method_16878(arg, blockPos.offset(direction));
				this.method_19196(arg, blockState, blockPos, i, false, bufferBuilder, list, bitSet);
				bl2 = true;
			}
		}

		random.setSeed(l);
		List<BakedQuad> list2 = bakedModel.method_19561(blockState, null, random);
		if (!list2.isEmpty()) {
			this.method_19196(arg, blockState, blockPos, -1, true, bufferBuilder, list2, bitSet);
			bl2 = true;
		}

		return bl2;
	}

	private void method_12348(
		class_3600 arg,
		BlockState blockState,
		BlockPos blockPos,
		BufferBuilder bufferBuilder,
		List<BakedQuad> list,
		float[] fs,
		BitSet bitSet,
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator
	) {
		Vec3d vec3d = blockState.getOffsetPos(arg, blockPos);
		double d = (double)blockPos.getX() + vec3d.x;
		double e = (double)blockPos.getY() + vec3d.y;
		double f = (double)blockPos.getZ() + vec3d.z;
		int i = 0;

		for (int j = list.size(); i < j; i++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(i);
			this.method_9964(blockState, bakedQuad.getVertexData(), bakedQuad.getFace(), fs, bitSet);
			ambientOcclusionCalculator.method_9971(arg, blockState, blockPos, bakedQuad.getFace(), fs, bitSet);
			bufferBuilder.putArray(bakedQuad.getVertexData());
			bufferBuilder.faceTexture2(
				ambientOcclusionCalculator.light[0], ambientOcclusionCalculator.light[1], ambientOcclusionCalculator.light[2], ambientOcclusionCalculator.light[3]
			);
			if (bakedQuad.hasColor()) {
				int k = this.field_13551.method_18332(blockState, arg, blockPos, bakedQuad.getColorIndex());
				float g = (float)(k >> 16 & 0xFF) / 255.0F;
				float h = (float)(k >> 8 & 0xFF) / 255.0F;
				float l = (float)(k & 0xFF) / 255.0F;
				bufferBuilder.faceTint(
					ambientOcclusionCalculator.brightness[0] * g, ambientOcclusionCalculator.brightness[0] * h, ambientOcclusionCalculator.brightness[0] * l, 4
				);
				bufferBuilder.faceTint(
					ambientOcclusionCalculator.brightness[1] * g, ambientOcclusionCalculator.brightness[1] * h, ambientOcclusionCalculator.brightness[1] * l, 3
				);
				bufferBuilder.faceTint(
					ambientOcclusionCalculator.brightness[2] * g, ambientOcclusionCalculator.brightness[2] * h, ambientOcclusionCalculator.brightness[2] * l, 2
				);
				bufferBuilder.faceTint(
					ambientOcclusionCalculator.brightness[3] * g, ambientOcclusionCalculator.brightness[3] * h, ambientOcclusionCalculator.brightness[3] * l, 1
				);
			} else {
				bufferBuilder.faceTint(ambientOcclusionCalculator.brightness[0], ambientOcclusionCalculator.brightness[0], ambientOcclusionCalculator.brightness[0], 4);
				bufferBuilder.faceTint(ambientOcclusionCalculator.brightness[1], ambientOcclusionCalculator.brightness[1], ambientOcclusionCalculator.brightness[1], 3);
				bufferBuilder.faceTint(ambientOcclusionCalculator.brightness[2], ambientOcclusionCalculator.brightness[2], ambientOcclusionCalculator.brightness[2], 2);
				bufferBuilder.faceTint(ambientOcclusionCalculator.brightness[3], ambientOcclusionCalculator.brightness[3], ambientOcclusionCalculator.brightness[3], 1);
			}

			bufferBuilder.postProcessFacePosition(d, e, f);
		}
	}

	private void method_9964(BlockState blockState, int[] is, Direction direction, @Nullable float[] fs, BitSet bitSet) {
		float f = 32.0F;
		float g = 32.0F;
		float h = 32.0F;
		float i = -32.0F;
		float j = -32.0F;
		float k = -32.0F;

		for (int l = 0; l < 4; l++) {
			float m = Float.intBitsToFloat(is[l * 7]);
			float n = Float.intBitsToFloat(is[l * 7 + 1]);
			float o = Float.intBitsToFloat(is[l * 7 + 2]);
			f = Math.min(f, m);
			g = Math.min(g, n);
			h = Math.min(h, o);
			i = Math.max(i, m);
			j = Math.max(j, n);
			k = Math.max(k, o);
		}

		if (fs != null) {
			fs[Direction.WEST.getId()] = f;
			fs[Direction.EAST.getId()] = i;
			fs[Direction.DOWN.getId()] = g;
			fs[Direction.UP.getId()] = j;
			fs[Direction.NORTH.getId()] = h;
			fs[Direction.SOUTH.getId()] = k;
			int p = Direction.values().length;
			fs[Direction.WEST.getId() + p] = 1.0F - f;
			fs[Direction.EAST.getId() + p] = 1.0F - i;
			fs[Direction.DOWN.getId() + p] = 1.0F - g;
			fs[Direction.UP.getId() + p] = 1.0F - j;
			fs[Direction.NORTH.getId() + p] = 1.0F - h;
			fs[Direction.SOUTH.getId() + p] = 1.0F - k;
		}

		float q = 1.0E-4F;
		float r = 0.9999F;
		switch (direction) {
			case DOWN:
				bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (g < 1.0E-4F || blockState.method_16897()) && g == j);
				break;
			case UP:
				bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (j > 0.9999F || blockState.method_16897()) && g == j);
				break;
			case NORTH:
				bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				bitSet.set(0, (h < 1.0E-4F || blockState.method_16897()) && h == k);
				break;
			case SOUTH:
				bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				bitSet.set(0, (k > 0.9999F || blockState.method_16897()) && h == k);
				break;
			case WEST:
				bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (f < 1.0E-4F || blockState.method_16897()) && f == i);
				break;
			case EAST:
				bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (i > 0.9999F || blockState.method_16897()) && f == i);
		}
	}

	private void method_19196(
		class_3600 arg, BlockState blockState, BlockPos blockPos, int i, boolean bl, BufferBuilder bufferBuilder, List<BakedQuad> list, BitSet bitSet
	) {
		Vec3d vec3d = blockState.getOffsetPos(arg, blockPos);
		double d = (double)blockPos.getX() + vec3d.x;
		double e = (double)blockPos.getY() + vec3d.y;
		double f = (double)blockPos.getZ() + vec3d.z;
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(j);
			if (bl) {
				this.method_9964(blockState, bakedQuad.getVertexData(), bakedQuad.getFace(), null, bitSet);
				BlockPos blockPos2 = bitSet.get(0) ? blockPos.offset(bakedQuad.getFace()) : blockPos;
				i = blockState.method_16878(arg, blockPos2);
			}

			bufferBuilder.putArray(bakedQuad.getVertexData());
			bufferBuilder.faceTexture2(i, i, i, i);
			if (bakedQuad.hasColor()) {
				int l = this.field_13551.method_18332(blockState, arg, blockPos, bakedQuad.getColorIndex());
				float g = (float)(l >> 16 & 0xFF) / 255.0F;
				float h = (float)(l >> 8 & 0xFF) / 255.0F;
				float m = (float)(l & 0xFF) / 255.0F;
				bufferBuilder.faceTint(g, h, m, 4);
				bufferBuilder.faceTint(g, h, m, 3);
				bufferBuilder.faceTint(g, h, m, 2);
				bufferBuilder.faceTint(g, h, m, 1);
			}

			bufferBuilder.postProcessFacePosition(d, e, f);
		}
	}

	public void method_12350(BakedModel bakedModel, float f, float g, float h, float i) {
		this.method_9965(null, bakedModel, f, g, h, i);
	}

	public void method_9965(@Nullable BlockState blockState, BakedModel bakedModel, float f, float g, float h, float i) {
		Random random = new Random();
		long l = 42L;

		for (Direction direction : Direction.values()) {
			random.setSeed(42L);
			this.renderQuads(f, g, h, i, bakedModel.method_19561(blockState, direction, random));
		}

		random.setSeed(42L);
		this.renderQuads(f, g, h, i, bakedModel.method_19561(blockState, null, random));
	}

	public void render(BakedModel model, BlockState state, float light, boolean bl) {
		GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
		int i = this.field_13551.method_18332(state, null, null, 0);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		if (!bl) {
			GlStateManager.color(light, light, light, 1.0F);
		}

		this.method_9965(state, model, light, f, g, h);
	}

	private void renderQuads(float light, float red, float green, float blue, List<BakedQuad> quads) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		int i = 0;

		for (int j = quads.size(); i < j; i++) {
			BakedQuad bakedQuad = (BakedQuad)quads.get(i);
			bufferBuilder.begin(7, VertexFormats.BLOCK_NORMALS);
			bufferBuilder.putArray(bakedQuad.getVertexData());
			if (bakedQuad.hasColor()) {
				bufferBuilder.putQuadColor(red * light, green * light, blue * light);
			} else {
				bufferBuilder.putQuadColor(light, light, light);
			}

			Vec3i vec3i = bakedQuad.getFace().getVector();
			bufferBuilder.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
			tessellator.draw();
		}
	}

	public static void method_19195() {
		field_20773.set(true);
	}

	public static void method_19199() {
		((Object2IntLinkedOpenHashMap)field_20772.get()).clear();
		field_20773.set(false);
	}

	private static int method_19200(BlockState state, class_3600 arg, BlockPos pos) {
		Boolean boolean_ = (Boolean)field_20773.get();
		Object2IntLinkedOpenHashMap<BlockPos> object2IntLinkedOpenHashMap = null;
		if (boolean_) {
			object2IntLinkedOpenHashMap = (Object2IntLinkedOpenHashMap<BlockPos>)field_20772.get();
			int i = object2IntLinkedOpenHashMap.getInt(pos);
			if (i != Integer.MAX_VALUE) {
				return i;
			}
		}

		int j = state.method_16878(arg, pos);
		if (object2IntLinkedOpenHashMap != null) {
			if (object2IntLinkedOpenHashMap.size() == 50) {
				object2IntLinkedOpenHashMap.removeFirstInt();
			}

			object2IntLinkedOpenHashMap.put(pos.toImmutable(), j);
		}

		return j;
	}

	class AmbientOcclusionCalculator {
		private final float[] brightness = new float[4];
		private final int[] light = new int[4];

		public AmbientOcclusionCalculator() {
		}

		public void method_9971(class_3600 arg, BlockState blockState, BlockPos blockPos, Direction direction, float[] fs, BitSet bitSet) {
			BlockPos blockPos2 = bitSet.get(0) ? blockPos.offset(direction) : blockPos;
			BlockModelRenderer.NeighborData neighborData = BlockModelRenderer.NeighborData.getData(direction);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			mutable.set(blockPos2).move(neighborData.field_14954[0]);
			int i = BlockModelRenderer.method_19200(blockState, arg, mutable);
			float f = arg.getBlockState(mutable).getAmbientOcclusionLightLevel();
			mutable.set(blockPos2).move(neighborData.field_14954[1]);
			int j = BlockModelRenderer.method_19200(blockState, arg, mutable);
			float g = arg.getBlockState(mutable).getAmbientOcclusionLightLevel();
			mutable.set(blockPos2).move(neighborData.field_14954[2]);
			int k = BlockModelRenderer.method_19200(blockState, arg, mutable);
			float h = arg.getBlockState(mutable).getAmbientOcclusionLightLevel();
			mutable.set(blockPos2).move(neighborData.field_14954[3]);
			int l = BlockModelRenderer.method_19200(blockState, arg, mutable);
			float m = arg.getBlockState(mutable).getAmbientOcclusionLightLevel();
			mutable.set(blockPos2).move(neighborData.field_14954[0]).move(direction);
			boolean bl = arg.getBlockState(mutable).method_16885(arg, mutable) == 0;
			mutable.set(blockPos2).move(neighborData.field_14954[1]).move(direction);
			boolean bl2 = arg.getBlockState(mutable).method_16885(arg, mutable) == 0;
			mutable.set(blockPos2).move(neighborData.field_14954[2]).move(direction);
			boolean bl3 = arg.getBlockState(mutable).method_16885(arg, mutable) == 0;
			mutable.set(blockPos2).move(neighborData.field_14954[3]).move(direction);
			boolean bl4 = arg.getBlockState(mutable).method_16885(arg, mutable) == 0;
			float p;
			int q;
			if (!bl3 && !bl) {
				p = f;
				q = i;
			} else {
				mutable.set(blockPos2).move(neighborData.field_14954[0]).move(neighborData.field_14954[2]);
				p = arg.getBlockState(mutable).getAmbientOcclusionLightLevel();
				q = BlockModelRenderer.method_19200(blockState, arg, mutable);
			}

			float t;
			int u;
			if (!bl4 && !bl) {
				t = f;
				u = i;
			} else {
				mutable.set(blockPos2).move(neighborData.field_14954[0]).move(neighborData.field_14954[3]);
				t = arg.getBlockState(mutable).getAmbientOcclusionLightLevel();
				u = BlockModelRenderer.method_19200(blockState, arg, mutable);
			}

			float x;
			int y;
			if (!bl3 && !bl2) {
				x = g;
				y = j;
			} else {
				mutable.set(blockPos2).move(neighborData.field_14954[1]).move(neighborData.field_14954[2]);
				x = arg.getBlockState(mutable).getAmbientOcclusionLightLevel();
				y = BlockModelRenderer.method_19200(blockState, arg, mutable);
			}

			float ab;
			int ac;
			if (!bl4 && !bl2) {
				ab = g;
				ac = j;
			} else {
				mutable.set(blockPos2).move(neighborData.field_14954[1]).move(neighborData.field_14954[3]);
				ab = arg.getBlockState(mutable).getAmbientOcclusionLightLevel();
				ac = BlockModelRenderer.method_19200(blockState, arg, mutable);
			}

			int ad = BlockModelRenderer.method_19200(blockState, arg, blockPos);
			mutable.set(blockPos).move(direction);
			if (bitSet.get(0) || !arg.getBlockState(mutable).isFullOpaque(arg, mutable)) {
				ad = BlockModelRenderer.method_19200(blockState, arg, mutable);
			}

			float ae = bitSet.get(0) ? arg.getBlockState(blockPos2).getAmbientOcclusionLightLevel() : arg.getBlockState(blockPos).getAmbientOcclusionLightLevel();
			BlockModelRenderer.Translation translation = BlockModelRenderer.Translation.getTranslations(direction);
			if (bitSet.get(1) && neighborData.field_14955) {
				float aj = (m + f + t + ae) * 0.25F;
				float ak = (h + f + p + ae) * 0.25F;
				float al = (h + g + x + ae) * 0.25F;
				float am = (m + g + ab + ae) * 0.25F;
				float an = fs[neighborData.field_14956[0].field_14962] * fs[neighborData.field_14956[1].field_14962];
				float ao = fs[neighborData.field_14956[2].field_14962] * fs[neighborData.field_14956[3].field_14962];
				float ap = fs[neighborData.field_14956[4].field_14962] * fs[neighborData.field_14956[5].field_14962];
				float aq = fs[neighborData.field_14956[6].field_14962] * fs[neighborData.field_14956[7].field_14962];
				float ar = fs[neighborData.field_14958[0].field_14962] * fs[neighborData.field_14958[1].field_14962];
				float as = fs[neighborData.field_14958[2].field_14962] * fs[neighborData.field_14958[3].field_14962];
				float at = fs[neighborData.field_14958[4].field_14962] * fs[neighborData.field_14958[5].field_14962];
				float au = fs[neighborData.field_14958[6].field_14962] * fs[neighborData.field_14958[7].field_14962];
				float av = fs[neighborData.field_14959[0].field_14962] * fs[neighborData.field_14959[1].field_14962];
				float aw = fs[neighborData.field_14959[2].field_14962] * fs[neighborData.field_14959[3].field_14962];
				float ax = fs[neighborData.field_14959[4].field_14962] * fs[neighborData.field_14959[5].field_14962];
				float ay = fs[neighborData.field_14959[6].field_14962] * fs[neighborData.field_14959[7].field_14962];
				float az = fs[neighborData.field_14957[0].field_14962] * fs[neighborData.field_14957[1].field_14962];
				float ba = fs[neighborData.field_14957[2].field_14962] * fs[neighborData.field_14957[3].field_14962];
				float bb = fs[neighborData.field_14957[4].field_14962] * fs[neighborData.field_14957[5].field_14962];
				float bc = fs[neighborData.field_14957[6].field_14962] * fs[neighborData.field_14957[7].field_14962];
				this.brightness[translation.fourthCorner] = aj * an + ak * ao + al * ap + am * aq;
				this.brightness[translation.field_14960] = aj * ar + ak * as + al * at + am * au;
				this.brightness[translation.field_14961] = aj * av + ak * aw + al * ax + am * ay;
				this.brightness[translation.thirdCorner] = aj * az + ak * ba + al * bb + am * bc;
				int bd = this.getAmbientOcclusionBrightness(l, i, u, ad);
				int be = this.getAmbientOcclusionBrightness(k, i, q, ad);
				int bf = this.getAmbientOcclusionBrightness(k, j, y, ad);
				int bg = this.getAmbientOcclusionBrightness(l, j, ac, ad);
				this.light[translation.fourthCorner] = this.getBrightness(bd, be, bf, bg, an, ao, ap, aq);
				this.light[translation.field_14960] = this.getBrightness(bd, be, bf, bg, ar, as, at, au);
				this.light[translation.field_14961] = this.getBrightness(bd, be, bf, bg, av, aw, ax, ay);
				this.light[translation.thirdCorner] = this.getBrightness(bd, be, bf, bg, az, ba, bb, bc);
			} else {
				float af = (m + f + t + ae) * 0.25F;
				float ag = (h + f + p + ae) * 0.25F;
				float ah = (h + g + x + ae) * 0.25F;
				float ai = (m + g + ab + ae) * 0.25F;
				this.light[translation.fourthCorner] = this.getAmbientOcclusionBrightness(l, i, u, ad);
				this.light[translation.field_14960] = this.getAmbientOcclusionBrightness(k, i, q, ad);
				this.light[translation.field_14961] = this.getAmbientOcclusionBrightness(k, j, y, ad);
				this.light[translation.thirdCorner] = this.getAmbientOcclusionBrightness(l, j, ac, ad);
				this.brightness[translation.fourthCorner] = af;
				this.brightness[translation.field_14960] = ag;
				this.brightness[translation.field_14961] = ah;
				this.brightness[translation.thirdCorner] = ai;
			}
		}

		private int getAmbientOcclusionBrightness(int i, int j, int k, int l) {
			if (i == 0) {
				i = l;
			}

			if (j == 0) {
				j = l;
			}

			if (k == 0) {
				k = l;
			}

			return i + j + k + l >> 2 & 16711935;
		}

		private int getBrightness(int i, int j, int k, int l, float f, float g, float h, float m) {
			int n = (int)((float)(i >> 16 & 0xFF) * f + (float)(j >> 16 & 0xFF) * g + (float)(k >> 16 & 0xFF) * h + (float)(l >> 16 & 0xFF) * m) & 0xFF;
			int o = (int)((float)(i & 0xFF) * f + (float)(j & 0xFF) * g + (float)(k & 0xFF) * h + (float)(l & 0xFF) * m) & 0xFF;
			return n << 16 | o;
		}
	}

	public static enum NeighborData {
		DOWN(
			new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH},
			0.5F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.SOUTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.SOUTH
			}
		),
		UP(
			new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH},
			1.0F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.SOUTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.SOUTH
			}
		),
		NORTH(
			new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST},
			0.8F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST
			}
		),
		SOUTH(
			new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP},
			0.8F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.WEST
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_WEST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.WEST,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.WEST
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.EAST
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_EAST,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.EAST,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.EAST
			}
		),
		WEST(
			new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH},
			0.6F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.SOUTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.SOUTH
			}
		),
		EAST(
			new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH},
			0.6F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.SOUTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.DOWN,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.NORTH,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_NORTH,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.NORTH
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.SOUTH,
				BlockModelRenderer.NeighborOrientation.FLIP_UP,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.FLIP_SOUTH,
				BlockModelRenderer.NeighborOrientation.UP,
				BlockModelRenderer.NeighborOrientation.SOUTH
			}
		);

		private final Direction[] field_14954;
		private final boolean field_14955;
		private final BlockModelRenderer.NeighborOrientation[] field_14956;
		private final BlockModelRenderer.NeighborOrientation[] field_14958;
		private final BlockModelRenderer.NeighborOrientation[] field_14959;
		private final BlockModelRenderer.NeighborOrientation[] field_14957;
		private static final BlockModelRenderer.NeighborData[] field_20775 = Util.make(new BlockModelRenderer.NeighborData[6], neighborDatas -> {
			neighborDatas[Direction.DOWN.getId()] = DOWN;
			neighborDatas[Direction.UP.getId()] = UP;
			neighborDatas[Direction.NORTH.getId()] = NORTH;
			neighborDatas[Direction.SOUTH.getId()] = SOUTH;
			neighborDatas[Direction.WEST.getId()] = WEST;
			neighborDatas[Direction.EAST.getId()] = EAST;
		});

		private NeighborData(
			Direction[] directions,
			float f,
			boolean bl,
			BlockModelRenderer.NeighborOrientation[] neighborOrientations,
			BlockModelRenderer.NeighborOrientation[] neighborOrientations2,
			BlockModelRenderer.NeighborOrientation[] neighborOrientations3,
			BlockModelRenderer.NeighborOrientation[] neighborOrientations4
		) {
			this.field_14954 = directions;
			this.field_14955 = bl;
			this.field_14956 = neighborOrientations;
			this.field_14958 = neighborOrientations2;
			this.field_14959 = neighborOrientations3;
			this.field_14957 = neighborOrientations4;
		}

		public static BlockModelRenderer.NeighborData getData(Direction direction) {
			return field_20775[direction.getId()];
		}
	}

	public static enum NeighborOrientation {
		DOWN(Direction.DOWN, false),
		UP(Direction.UP, false),
		NORTH(Direction.NORTH, false),
		SOUTH(Direction.SOUTH, false),
		WEST(Direction.WEST, false),
		EAST(Direction.EAST, false),
		FLIP_DOWN(Direction.DOWN, true),
		FLIP_UP(Direction.UP, true),
		FLIP_NORTH(Direction.NORTH, true),
		FLIP_SOUTH(Direction.SOUTH, true),
		FLIP_WEST(Direction.WEST, true),
		FLIP_EAST(Direction.EAST, true);

		private final int field_14962;

		private NeighborOrientation(Direction direction, boolean bl) {
			this.field_14962 = direction.getId() + (bl ? Direction.values().length : 0);
		}
	}

	static enum Translation {
		DOWN(0, 1, 2, 3),
		UP(2, 3, 0, 1),
		NORTH(3, 0, 1, 2),
		SOUTH(0, 1, 2, 3),
		WEST(3, 0, 1, 2),
		EAST(1, 2, 3, 0);

		private final int fourthCorner;
		private final int field_14960;
		private final int field_14961;
		private final int thirdCorner;
		private static final BlockModelRenderer.Translation[] field_20776 = Util.make(new BlockModelRenderer.Translation[6], translations -> {
			translations[Direction.DOWN.getId()] = DOWN;
			translations[Direction.UP.getId()] = UP;
			translations[Direction.NORTH.getId()] = NORTH;
			translations[Direction.SOUTH.getId()] = SOUTH;
			translations[Direction.WEST.getId()] = WEST;
			translations[Direction.EAST.getId()] = EAST;
		});

		private Translation(int j, int k, int l, int m) {
			this.fourthCorner = j;
			this.field_14960 = k;
			this.field_14961 = l;
			this.thirdCorner = m;
		}

		public static BlockModelRenderer.Translation getTranslations(Direction face) {
			return field_20776[face.getId()];
		}
	}
}
