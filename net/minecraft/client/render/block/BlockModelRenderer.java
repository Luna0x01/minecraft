package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ExtendedBlockView;

public class BlockModelRenderer {
	private final BlockColors colorMap;
	private static final ThreadLocal<BlockModelRenderer.BrightnessCache> brightnessCache = ThreadLocal.withInitial(() -> new BlockModelRenderer.BrightnessCache());

	public BlockModelRenderer(BlockColors blockColors) {
		this.colorMap = blockColors;
	}

	public boolean tesselate(
		ExtendedBlockView extendedBlockView,
		BakedModel bakedModel,
		BlockState blockState,
		BlockPos blockPos,
		BufferBuilder bufferBuilder,
		boolean bl,
		Random random,
		long l
	) {
		boolean bl2 = MinecraftClient.isAmbientOcclusionEnabled() && blockState.getLuminance() == 0 && bakedModel.useAmbientOcclusion();

		try {
			return bl2
				? this.tesselateSmooth(extendedBlockView, bakedModel, blockState, blockPos, bufferBuilder, bl, random, l)
				: this.tesselateFlat(extendedBlockView, bakedModel, blockState, blockPos, bufferBuilder, bl, random, l);
		} catch (Throwable var14) {
			CrashReport crashReport = CrashReport.create(var14, "Tesselating block model");
			CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
			crashReportSection.add("Using AO", bl2);
			throw new CrashException(crashReport);
		}
	}

	public boolean tesselateSmooth(
		ExtendedBlockView extendedBlockView,
		BakedModel bakedModel,
		BlockState blockState,
		BlockPos blockPos,
		BufferBuilder bufferBuilder,
		boolean bl,
		Random random,
		long l
	) {
		boolean bl2 = false;
		float[] fs = new float[Direction.values().length * 2];
		BitSet bitSet = new BitSet(3);
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator = new BlockModelRenderer.AmbientOcclusionCalculator();

		for (Direction direction : Direction.values()) {
			random.setSeed(l);
			List<BakedQuad> list = bakedModel.getQuads(blockState, direction, random);
			if (!list.isEmpty() && (!bl || Block.shouldDrawSide(blockState, extendedBlockView, blockPos, direction))) {
				this.tesselateQuadsSmooth(extendedBlockView, blockState, blockPos, bufferBuilder, list, fs, bitSet, ambientOcclusionCalculator);
				bl2 = true;
			}
		}

		random.setSeed(l);
		List<BakedQuad> list2 = bakedModel.getQuads(blockState, null, random);
		if (!list2.isEmpty()) {
			this.tesselateQuadsSmooth(extendedBlockView, blockState, blockPos, bufferBuilder, list2, fs, bitSet, ambientOcclusionCalculator);
			bl2 = true;
		}

		return bl2;
	}

	public boolean tesselateFlat(
		ExtendedBlockView extendedBlockView,
		BakedModel bakedModel,
		BlockState blockState,
		BlockPos blockPos,
		BufferBuilder bufferBuilder,
		boolean bl,
		Random random,
		long l
	) {
		boolean bl2 = false;
		BitSet bitSet = new BitSet(3);

		for (Direction direction : Direction.values()) {
			random.setSeed(l);
			List<BakedQuad> list = bakedModel.getQuads(blockState, direction, random);
			if (!list.isEmpty() && (!bl || Block.shouldDrawSide(blockState, extendedBlockView, blockPos, direction))) {
				int i = blockState.getBlockBrightness(extendedBlockView, blockPos.offset(direction));
				this.tesselateQuadsFlat(extendedBlockView, blockState, blockPos, i, false, bufferBuilder, list, bitSet);
				bl2 = true;
			}
		}

		random.setSeed(l);
		List<BakedQuad> list2 = bakedModel.getQuads(blockState, null, random);
		if (!list2.isEmpty()) {
			this.tesselateQuadsFlat(extendedBlockView, blockState, blockPos, -1, true, bufferBuilder, list2, bitSet);
			bl2 = true;
		}

		return bl2;
	}

	private void tesselateQuadsSmooth(
		ExtendedBlockView extendedBlockView,
		BlockState blockState,
		BlockPos blockPos,
		BufferBuilder bufferBuilder,
		List<BakedQuad> list,
		float[] fs,
		BitSet bitSet,
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator
	) {
		Vec3d vec3d = blockState.getOffsetPos(extendedBlockView, blockPos);
		double d = (double)blockPos.getX() + vec3d.x;
		double e = (double)blockPos.getY() + vec3d.y;
		double f = (double)blockPos.getZ() + vec3d.z;
		int i = 0;

		for (int j = list.size(); i < j; i++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(i);
			this.updateShape(extendedBlockView, blockState, blockPos, bakedQuad.getVertexData(), bakedQuad.getFace(), fs, bitSet);
			ambientOcclusionCalculator.apply(extendedBlockView, blockState, blockPos, bakedQuad.getFace(), fs, bitSet);
			bufferBuilder.putVertexData(bakedQuad.getVertexData());
			bufferBuilder.brightness(
				ambientOcclusionCalculator.brightness[0],
				ambientOcclusionCalculator.brightness[1],
				ambientOcclusionCalculator.brightness[2],
				ambientOcclusionCalculator.brightness[3]
			);
			if (bakedQuad.hasColor()) {
				int k = this.colorMap.getColorMultiplier(blockState, extendedBlockView, blockPos, bakedQuad.getColorIndex());
				float g = (float)(k >> 16 & 0xFF) / 255.0F;
				float h = (float)(k >> 8 & 0xFF) / 255.0F;
				float l = (float)(k & 0xFF) / 255.0F;
				bufferBuilder.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[0] * g, ambientOcclusionCalculator.colorMultiplier[0] * h, ambientOcclusionCalculator.colorMultiplier[0] * l, 4
				);
				bufferBuilder.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[1] * g, ambientOcclusionCalculator.colorMultiplier[1] * h, ambientOcclusionCalculator.colorMultiplier[1] * l, 3
				);
				bufferBuilder.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[2] * g, ambientOcclusionCalculator.colorMultiplier[2] * h, ambientOcclusionCalculator.colorMultiplier[2] * l, 2
				);
				bufferBuilder.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[3] * g, ambientOcclusionCalculator.colorMultiplier[3] * h, ambientOcclusionCalculator.colorMultiplier[3] * l, 1
				);
			} else {
				bufferBuilder.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[0], ambientOcclusionCalculator.colorMultiplier[0], 4
				);
				bufferBuilder.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[1], ambientOcclusionCalculator.colorMultiplier[1], 3
				);
				bufferBuilder.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[2], ambientOcclusionCalculator.colorMultiplier[2], 2
				);
				bufferBuilder.multiplyColor(
					ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.colorMultiplier[3], ambientOcclusionCalculator.colorMultiplier[3], 1
				);
			}

			bufferBuilder.postPosition(d, e, f);
		}
	}

	private void updateShape(
		ExtendedBlockView extendedBlockView, BlockState blockState, BlockPos blockPos, int[] is, Direction direction, @Nullable float[] fs, BitSet bitSet
	) {
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
			fs[Direction.field_11039.getId()] = f;
			fs[Direction.field_11034.getId()] = i;
			fs[Direction.field_11033.getId()] = g;
			fs[Direction.field_11036.getId()] = j;
			fs[Direction.field_11043.getId()] = h;
			fs[Direction.field_11035.getId()] = k;
			int p = Direction.values().length;
			fs[Direction.field_11039.getId() + p] = 1.0F - f;
			fs[Direction.field_11034.getId() + p] = 1.0F - i;
			fs[Direction.field_11033.getId() + p] = 1.0F - g;
			fs[Direction.field_11036.getId() + p] = 1.0F - j;
			fs[Direction.field_11043.getId() + p] = 1.0F - h;
			fs[Direction.field_11035.getId() + p] = 1.0F - k;
		}

		float q = 1.0E-4F;
		float r = 0.9999F;
		switch (direction) {
			case field_11033:
				bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, g == j && (g < 1.0E-4F || blockState.method_21743(extendedBlockView, blockPos)));
				break;
			case field_11036:
				bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, g == j && (j > 0.9999F || blockState.method_21743(extendedBlockView, blockPos)));
				break;
			case field_11043:
				bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				bitSet.set(0, h == k && (h < 1.0E-4F || blockState.method_21743(extendedBlockView, blockPos)));
				break;
			case field_11035:
				bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				bitSet.set(0, h == k && (k > 0.9999F || blockState.method_21743(extendedBlockView, blockPos)));
				break;
			case field_11039:
				bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, f == i && (f < 1.0E-4F || blockState.method_21743(extendedBlockView, blockPos)));
				break;
			case field_11034:
				bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, f == i && (i > 0.9999F || blockState.method_21743(extendedBlockView, blockPos)));
		}
	}

	private void tesselateQuadsFlat(
		ExtendedBlockView extendedBlockView,
		BlockState blockState,
		BlockPos blockPos,
		int i,
		boolean bl,
		BufferBuilder bufferBuilder,
		List<BakedQuad> list,
		BitSet bitSet
	) {
		Vec3d vec3d = blockState.getOffsetPos(extendedBlockView, blockPos);
		double d = (double)blockPos.getX() + vec3d.x;
		double e = (double)blockPos.getY() + vec3d.y;
		double f = (double)blockPos.getZ() + vec3d.z;
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(j);
			if (bl) {
				this.updateShape(extendedBlockView, blockState, blockPos, bakedQuad.getVertexData(), bakedQuad.getFace(), null, bitSet);
				BlockPos blockPos2 = bitSet.get(0) ? blockPos.offset(bakedQuad.getFace()) : blockPos;
				i = blockState.getBlockBrightness(extendedBlockView, blockPos2);
			}

			bufferBuilder.putVertexData(bakedQuad.getVertexData());
			bufferBuilder.brightness(i, i, i, i);
			if (bakedQuad.hasColor()) {
				int l = this.colorMap.getColorMultiplier(blockState, extendedBlockView, blockPos, bakedQuad.getColorIndex());
				float g = (float)(l >> 16 & 0xFF) / 255.0F;
				float h = (float)(l >> 8 & 0xFF) / 255.0F;
				float m = (float)(l & 0xFF) / 255.0F;
				bufferBuilder.multiplyColor(g, h, m, 4);
				bufferBuilder.multiplyColor(g, h, m, 3);
				bufferBuilder.multiplyColor(g, h, m, 2);
				bufferBuilder.multiplyColor(g, h, m, 1);
			}

			bufferBuilder.postPosition(d, e, f);
		}
	}

	public void render(BakedModel bakedModel, float f, float g, float h, float i) {
		this.render(null, bakedModel, f, g, h, i);
	}

	public void render(@Nullable BlockState blockState, BakedModel bakedModel, float f, float g, float h, float i) {
		Random random = new Random();
		long l = 42L;

		for (Direction direction : Direction.values()) {
			random.setSeed(42L);
			this.renderQuad(f, g, h, i, bakedModel.getQuads(blockState, direction, random));
		}

		random.setSeed(42L);
		this.renderQuad(f, g, h, i, bakedModel.getQuads(blockState, null, random));
	}

	public void render(BakedModel bakedModel, BlockState blockState, float f, boolean bl) {
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		int i = this.colorMap.getColorMultiplier(blockState, null, null, 0);
		float g = (float)(i >> 16 & 0xFF) / 255.0F;
		float h = (float)(i >> 8 & 0xFF) / 255.0F;
		float j = (float)(i & 0xFF) / 255.0F;
		if (!bl) {
			GlStateManager.color4f(f, f, f, 1.0F);
		}

		this.render(blockState, bakedModel, f, g, h, j);
	}

	private void renderQuad(float f, float g, float h, float i, List<BakedQuad> list) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(j);
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_NORMAL);
			bufferBuilder.putVertexData(bakedQuad.getVertexData());
			if (bakedQuad.hasColor()) {
				bufferBuilder.setQuadColor(g * f, h * f, i * f);
			} else {
				bufferBuilder.setQuadColor(f, f, f);
			}

			Vec3i vec3i = bakedQuad.getFace().getVector();
			bufferBuilder.postNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
			tessellator.draw();
		}
	}

	public static void enableBrightnessCache() {
		((BlockModelRenderer.BrightnessCache)brightnessCache.get()).enable();
	}

	public static void disableBrightnessCache() {
		((BlockModelRenderer.BrightnessCache)brightnessCache.get()).disable();
	}

	class AmbientOcclusionCalculator {
		private final float[] colorMultiplier = new float[4];
		private final int[] brightness = new int[4];

		public AmbientOcclusionCalculator() {
		}

		public void apply(ExtendedBlockView extendedBlockView, BlockState blockState, BlockPos blockPos, Direction direction, float[] fs, BitSet bitSet) {
			BlockPos blockPos2 = bitSet.get(0) ? blockPos.offset(direction) : blockPos;
			BlockModelRenderer.NeighborData neighborData = BlockModelRenderer.NeighborData.getData(direction);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			BlockModelRenderer.BrightnessCache brightnessCache = (BlockModelRenderer.BrightnessCache)BlockModelRenderer.brightnessCache.get();
			mutable.set(blockPos2).setOffset(neighborData.faces[0]);
			BlockState blockState2 = extendedBlockView.getBlockState(mutable);
			int i = brightnessCache.getInt(blockState2, extendedBlockView, mutable);
			float f = brightnessCache.getFloat(blockState2, extendedBlockView, mutable);
			mutable.set(blockPos2).setOffset(neighborData.faces[1]);
			BlockState blockState3 = extendedBlockView.getBlockState(mutable);
			int j = brightnessCache.getInt(blockState3, extendedBlockView, mutable);
			float g = brightnessCache.getFloat(blockState3, extendedBlockView, mutable);
			mutable.set(blockPos2).setOffset(neighborData.faces[2]);
			BlockState blockState4 = extendedBlockView.getBlockState(mutable);
			int k = brightnessCache.getInt(blockState4, extendedBlockView, mutable);
			float h = brightnessCache.getFloat(blockState4, extendedBlockView, mutable);
			mutable.set(blockPos2).setOffset(neighborData.faces[3]);
			BlockState blockState5 = extendedBlockView.getBlockState(mutable);
			int l = brightnessCache.getInt(blockState5, extendedBlockView, mutable);
			float m = brightnessCache.getFloat(blockState5, extendedBlockView, mutable);
			mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(direction);
			boolean bl = extendedBlockView.getBlockState(mutable).getLightSubtracted(extendedBlockView, mutable) == 0;
			mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(direction);
			boolean bl2 = extendedBlockView.getBlockState(mutable).getLightSubtracted(extendedBlockView, mutable) == 0;
			mutable.set(blockPos2).setOffset(neighborData.faces[2]).setOffset(direction);
			boolean bl3 = extendedBlockView.getBlockState(mutable).getLightSubtracted(extendedBlockView, mutable) == 0;
			mutable.set(blockPos2).setOffset(neighborData.faces[3]).setOffset(direction);
			boolean bl4 = extendedBlockView.getBlockState(mutable).getLightSubtracted(extendedBlockView, mutable) == 0;
			float p;
			int q;
			if (!bl3 && !bl) {
				p = f;
				q = i;
			} else {
				mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[2]);
				BlockState blockState6 = extendedBlockView.getBlockState(mutable);
				p = brightnessCache.getFloat(blockState6, extendedBlockView, mutable);
				q = brightnessCache.getInt(blockState6, extendedBlockView, mutable);
			}

			float t;
			int u;
			if (!bl4 && !bl) {
				t = f;
				u = i;
			} else {
				mutable.set(blockPos2).setOffset(neighborData.faces[0]).setOffset(neighborData.faces[3]);
				BlockState blockState7 = extendedBlockView.getBlockState(mutable);
				t = brightnessCache.getFloat(blockState7, extendedBlockView, mutable);
				u = brightnessCache.getInt(blockState7, extendedBlockView, mutable);
			}

			float x;
			int y;
			if (!bl3 && !bl2) {
				x = f;
				y = i;
			} else {
				mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[2]);
				BlockState blockState8 = extendedBlockView.getBlockState(mutable);
				x = brightnessCache.getFloat(blockState8, extendedBlockView, mutable);
				y = brightnessCache.getInt(blockState8, extendedBlockView, mutable);
			}

			float ab;
			int ac;
			if (!bl4 && !bl2) {
				ab = f;
				ac = i;
			} else {
				mutable.set(blockPos2).setOffset(neighborData.faces[1]).setOffset(neighborData.faces[3]);
				BlockState blockState9 = extendedBlockView.getBlockState(mutable);
				ab = brightnessCache.getFloat(blockState9, extendedBlockView, mutable);
				ac = brightnessCache.getInt(blockState9, extendedBlockView, mutable);
			}

			int ad = brightnessCache.getInt(blockState, extendedBlockView, blockPos);
			mutable.set(blockPos).setOffset(direction);
			BlockState blockState10 = extendedBlockView.getBlockState(mutable);
			if (bitSet.get(0) || !blockState10.isFullOpaque(extendedBlockView, mutable)) {
				ad = brightnessCache.getInt(blockState10, extendedBlockView, mutable);
			}

			float ae = bitSet.get(0)
				? brightnessCache.getFloat(extendedBlockView.getBlockState(blockPos2), extendedBlockView, blockPos2)
				: brightnessCache.getFloat(extendedBlockView.getBlockState(blockPos), extendedBlockView, blockPos);
			BlockModelRenderer.Translation translation = BlockModelRenderer.Translation.getTranslations(direction);
			if (bitSet.get(1) && neighborData.nonCubicWeight) {
				float aj = (m + f + t + ae) * 0.25F;
				float ak = (h + f + p + ae) * 0.25F;
				float al = (h + g + x + ae) * 0.25F;
				float am = (m + g + ab + ae) * 0.25F;
				float an = fs[neighborData.field_4192[0].shape] * fs[neighborData.field_4192[1].shape];
				float ao = fs[neighborData.field_4192[2].shape] * fs[neighborData.field_4192[3].shape];
				float ap = fs[neighborData.field_4192[4].shape] * fs[neighborData.field_4192[5].shape];
				float aq = fs[neighborData.field_4192[6].shape] * fs[neighborData.field_4192[7].shape];
				float ar = fs[neighborData.field_4185[0].shape] * fs[neighborData.field_4185[1].shape];
				float as = fs[neighborData.field_4185[2].shape] * fs[neighborData.field_4185[3].shape];
				float at = fs[neighborData.field_4185[4].shape] * fs[neighborData.field_4185[5].shape];
				float au = fs[neighborData.field_4185[6].shape] * fs[neighborData.field_4185[7].shape];
				float av = fs[neighborData.field_4180[0].shape] * fs[neighborData.field_4180[1].shape];
				float aw = fs[neighborData.field_4180[2].shape] * fs[neighborData.field_4180[3].shape];
				float ax = fs[neighborData.field_4180[4].shape] * fs[neighborData.field_4180[5].shape];
				float ay = fs[neighborData.field_4180[6].shape] * fs[neighborData.field_4180[7].shape];
				float az = fs[neighborData.field_4188[0].shape] * fs[neighborData.field_4188[1].shape];
				float ba = fs[neighborData.field_4188[2].shape] * fs[neighborData.field_4188[3].shape];
				float bb = fs[neighborData.field_4188[4].shape] * fs[neighborData.field_4188[5].shape];
				float bc = fs[neighborData.field_4188[6].shape] * fs[neighborData.field_4188[7].shape];
				this.colorMultiplier[translation.firstCorner] = aj * an + ak * ao + al * ap + am * aq;
				this.colorMultiplier[translation.secondCorner] = aj * ar + ak * as + al * at + am * au;
				this.colorMultiplier[translation.thirdCorner] = aj * av + ak * aw + al * ax + am * ay;
				this.colorMultiplier[translation.fourthCorner] = aj * az + ak * ba + al * bb + am * bc;
				int bd = this.getAmbientOcclusionBrightness(l, i, u, ad);
				int be = this.getAmbientOcclusionBrightness(k, i, q, ad);
				int bf = this.getAmbientOcclusionBrightness(k, j, y, ad);
				int bg = this.getAmbientOcclusionBrightness(l, j, ac, ad);
				this.brightness[translation.firstCorner] = this.getBrightness(bd, be, bf, bg, an, ao, ap, aq);
				this.brightness[translation.secondCorner] = this.getBrightness(bd, be, bf, bg, ar, as, at, au);
				this.brightness[translation.thirdCorner] = this.getBrightness(bd, be, bf, bg, av, aw, ax, ay);
				this.brightness[translation.fourthCorner] = this.getBrightness(bd, be, bf, bg, az, ba, bb, bc);
			} else {
				float af = (m + f + t + ae) * 0.25F;
				float ag = (h + f + p + ae) * 0.25F;
				float ah = (h + g + x + ae) * 0.25F;
				float ai = (m + g + ab + ae) * 0.25F;
				this.brightness[translation.firstCorner] = this.getAmbientOcclusionBrightness(l, i, u, ad);
				this.brightness[translation.secondCorner] = this.getAmbientOcclusionBrightness(k, i, q, ad);
				this.brightness[translation.thirdCorner] = this.getAmbientOcclusionBrightness(k, j, y, ad);
				this.brightness[translation.fourthCorner] = this.getAmbientOcclusionBrightness(l, j, ac, ad);
				this.colorMultiplier[translation.firstCorner] = af;
				this.colorMultiplier[translation.secondCorner] = ag;
				this.colorMultiplier[translation.thirdCorner] = ah;
				this.colorMultiplier[translation.fourthCorner] = ai;
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

	static class BrightnessCache {
		private boolean enabled;
		private final Long2IntLinkedOpenHashMap intCache = SystemUtil.get(() -> {
			Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = new Long2IntLinkedOpenHashMap(100, 0.25F) {
				protected void rehash(int i) {
				}
			};
			long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
			return long2IntLinkedOpenHashMap;
		});
		private final Long2FloatLinkedOpenHashMap floatCache = SystemUtil.get(() -> {
			Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(100, 0.25F) {
				protected void rehash(int i) {
				}
			};
			long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
			return long2FloatLinkedOpenHashMap;
		});

		private BrightnessCache() {
		}

		public void enable() {
			this.enabled = true;
		}

		public void disable() {
			this.enabled = false;
			this.intCache.clear();
			this.floatCache.clear();
		}

		public int getInt(BlockState blockState, ExtendedBlockView extendedBlockView, BlockPos blockPos) {
			long l = blockPos.asLong();
			if (this.enabled) {
				int i = this.intCache.get(l);
				if (i != Integer.MAX_VALUE) {
					return i;
				}
			}

			int j = blockState.getBlockBrightness(extendedBlockView, blockPos);
			if (this.enabled) {
				if (this.intCache.size() == 100) {
					this.intCache.removeFirstInt();
				}

				this.intCache.put(l, j);
			}

			return j;
		}

		public float getFloat(BlockState blockState, ExtendedBlockView extendedBlockView, BlockPos blockPos) {
			long l = blockPos.asLong();
			if (this.enabled) {
				float f = this.floatCache.get(l);
				if (!Float.isNaN(f)) {
					return f;
				}
			}

			float g = blockState.getAmbientOcclusionLightLevel(extendedBlockView, blockPos);
			if (this.enabled) {
				if (this.floatCache.size() == 100) {
					this.floatCache.removeFirstFloat();
				}

				this.floatCache.put(l, g);
			}

			return g;
		}
	}

	public static enum NeighborData {
		field_4181(
			new Direction[]{Direction.field_11039, Direction.field_11034, Direction.field_11043, Direction.field_11035},
			0.5F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4213,
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4213
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4211,
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4211
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4211,
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4211
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4213,
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4213
			}
		),
		field_4182(
			new Direction[]{Direction.field_11034, Direction.field_11039, Direction.field_11043, Direction.field_11035},
			1.0F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4213,
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4213
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4211,
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4211
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4211,
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4211
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4213,
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4213
			}
		),
		field_4183(
			new Direction[]{Direction.field_11036, Direction.field_11033, Direction.field_11034, Direction.field_11039},
			0.8F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4216
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4214
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4214
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4216
			}
		),
		field_4184(
			new Direction[]{Direction.field_11039, Direction.field_11034, Direction.field_11033, Direction.field_11036},
			0.8F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4215
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4216,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4215,
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4215
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4219
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4214,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4219,
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4219
			}
		),
		field_4187(
			new Direction[]{Direction.field_11036, Direction.field_11033, Direction.field_11043, Direction.field_11035},
			0.6F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4213,
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4213
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4211,
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4211
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4211,
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4211
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4213,
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4213
			}
		),
		field_4186(
			new Direction[]{Direction.field_11033, Direction.field_11036, Direction.field_11043, Direction.field_11035},
			0.6F,
			true,
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4213,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4213
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4211,
				BlockModelRenderer.NeighborOrientation.field_4220,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4210,
				BlockModelRenderer.NeighborOrientation.field_4211
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4211,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4218,
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4211
			},
			new BlockModelRenderer.NeighborOrientation[]{
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4213,
				BlockModelRenderer.NeighborOrientation.field_4217,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4221,
				BlockModelRenderer.NeighborOrientation.field_4212,
				BlockModelRenderer.NeighborOrientation.field_4213
			}
		);

		private final Direction[] faces;
		private final boolean nonCubicWeight;
		private final BlockModelRenderer.NeighborOrientation[] field_4192;
		private final BlockModelRenderer.NeighborOrientation[] field_4185;
		private final BlockModelRenderer.NeighborOrientation[] field_4180;
		private final BlockModelRenderer.NeighborOrientation[] field_4188;
		private static final BlockModelRenderer.NeighborData[] field_4190 = SystemUtil.consume(new BlockModelRenderer.NeighborData[6], neighborDatas -> {
			neighborDatas[Direction.field_11033.getId()] = field_4181;
			neighborDatas[Direction.field_11036.getId()] = field_4182;
			neighborDatas[Direction.field_11043.getId()] = field_4183;
			neighborDatas[Direction.field_11035.getId()] = field_4184;
			neighborDatas[Direction.field_11039.getId()] = field_4187;
			neighborDatas[Direction.field_11034.getId()] = field_4186;
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
			this.faces = directions;
			this.nonCubicWeight = bl;
			this.field_4192 = neighborOrientations;
			this.field_4185 = neighborOrientations2;
			this.field_4180 = neighborOrientations3;
			this.field_4188 = neighborOrientations4;
		}

		public static BlockModelRenderer.NeighborData getData(Direction direction) {
			return field_4190[direction.getId()];
		}
	}

	public static enum NeighborOrientation {
		field_4210(Direction.field_11033, false),
		field_4212(Direction.field_11036, false),
		field_4211(Direction.field_11043, false),
		field_4213(Direction.field_11035, false),
		field_4215(Direction.field_11039, false),
		field_4219(Direction.field_11034, false),
		field_4220(Direction.field_11033, true),
		field_4217(Direction.field_11036, true),
		field_4218(Direction.field_11043, true),
		field_4221(Direction.field_11035, true),
		field_4216(Direction.field_11039, true),
		field_4214(Direction.field_11034, true);

		private final int shape;

		private NeighborOrientation(Direction direction, boolean bl) {
			this.shape = direction.getId() + (bl ? Direction.values().length : 0);
		}
	}

	static enum Translation {
		field_4199(0, 1, 2, 3),
		field_4200(2, 3, 0, 1),
		field_4204(3, 0, 1, 2),
		field_4205(0, 1, 2, 3),
		field_4206(3, 0, 1, 2),
		field_4207(1, 2, 3, 0);

		private final int firstCorner;
		private final int secondCorner;
		private final int thirdCorner;
		private final int fourthCorner;
		private static final BlockModelRenderer.Translation[] VALUES = SystemUtil.consume(new BlockModelRenderer.Translation[6], translations -> {
			translations[Direction.field_11033.getId()] = field_4199;
			translations[Direction.field_11036.getId()] = field_4200;
			translations[Direction.field_11043.getId()] = field_4204;
			translations[Direction.field_11035.getId()] = field_4205;
			translations[Direction.field_11039.getId()] = field_4206;
			translations[Direction.field_11034.getId()] = field_4207;
		});

		private Translation(int j, int k, int l, int m) {
			this.firstCorner = j;
			this.secondCorner = k;
			this.thirdCorner = l;
			this.fourthCorner = m;
		}

		public static BlockModelRenderer.Translation getTranslations(Direction direction) {
			return VALUES[direction.getId()];
		}
	}
}
