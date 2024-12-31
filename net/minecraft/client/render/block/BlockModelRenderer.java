package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.BlockColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;

public class BlockModelRenderer {
	private final BlockColors field_13551;

	public BlockModelRenderer(BlockColors blockColors) {
		this.field_13551 = blockColors;
	}

	public boolean method_12349(BlockView blockView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean bl) {
		return this.method_9963(blockView, bakedModel, blockState, blockPos, bufferBuilder, bl, MathHelper.hashCode(blockPos));
	}

	public boolean method_9963(
		BlockView blockView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean bl, long l
	) {
		boolean bl2 = MinecraftClient.isAmbientOcclusionEnabled() && blockState.getLuminance() == 0 && bakedModel.useAmbientOcclusion();

		try {
			return bl2
				? this.method_9961(blockView, bakedModel, blockState, blockPos, bufferBuilder, bl, l)
				: this.method_9967(blockView, bakedModel, blockState, blockPos, bufferBuilder, bl, l);
		} catch (Throwable var13) {
			CrashReport crashReport = CrashReport.create(var13, "Tesselating block model");
			CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
			crashReportSection.add("Using AO", bl2);
			throw new CrashException(crashReport);
		}
	}

	public boolean method_9961(
		BlockView blockView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean bl, long l
	) {
		boolean bl2 = false;
		float[] fs = new float[Direction.values().length * 2];
		BitSet bitSet = new BitSet(3);
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator = new BlockModelRenderer.AmbientOcclusionCalculator();

		for (Direction direction : Direction.values()) {
			List<BakedQuad> list = bakedModel.method_12502(blockState, direction, l);
			if (!list.isEmpty() && (!bl || blockState.method_11724(blockView, blockPos, direction))) {
				this.method_12348(blockView, blockState, blockPos, bufferBuilder, list, fs, bitSet, ambientOcclusionCalculator);
				bl2 = true;
			}
		}

		List<BakedQuad> list2 = bakedModel.method_12502(blockState, null, l);
		if (!list2.isEmpty()) {
			this.method_12348(blockView, blockState, blockPos, bufferBuilder, list2, fs, bitSet, ambientOcclusionCalculator);
			bl2 = true;
		}

		return bl2;
	}

	public boolean method_9967(
		BlockView blockView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean bl, long l
	) {
		boolean bl2 = false;
		BitSet bitSet = new BitSet(3);

		for (Direction direction : Direction.values()) {
			List<BakedQuad> list = bakedModel.method_12502(blockState, direction, l);
			if (!list.isEmpty() && (!bl || blockState.method_11724(blockView, blockPos, direction))) {
				int i = blockState.method_11712(blockView, blockPos.offset(direction));
				this.method_12347(blockView, blockState, blockPos, i, false, bufferBuilder, list, bitSet);
				bl2 = true;
			}
		}

		List<BakedQuad> list2 = bakedModel.method_12502(blockState, null, l);
		if (!list2.isEmpty()) {
			this.method_12347(blockView, blockState, blockPos, -1, true, bufferBuilder, list2, bitSet);
			bl2 = true;
		}

		return bl2;
	}

	private void method_12348(
		BlockView blockView,
		BlockState blockState,
		BlockPos blockPos,
		BufferBuilder bufferBuilder,
		List<BakedQuad> list,
		float[] fs,
		BitSet bitSet,
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator
	) {
		Vec3d vec3d = blockState.method_13761(blockView, blockPos);
		double d = (double)blockPos.getX() + vec3d.x;
		double e = (double)blockPos.getY() + vec3d.y;
		double f = (double)blockPos.getZ() + vec3d.z;
		int i = 0;

		for (int j = list.size(); i < j; i++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(i);
			this.method_9964(blockState, bakedQuad.getVertexData(), bakedQuad.getFace(), fs, bitSet);
			ambientOcclusionCalculator.method_9971(blockView, blockState, blockPos, bakedQuad.getFace(), fs, bitSet);
			bufferBuilder.putArray(bakedQuad.getVertexData());
			bufferBuilder.faceTexture2(
				ambientOcclusionCalculator.light[0], ambientOcclusionCalculator.light[1], ambientOcclusionCalculator.light[2], ambientOcclusionCalculator.light[3]
			);
			if (bakedQuad.hasColor()) {
				int k = this.field_13551.method_12157(blockState, blockView, blockPos, bakedQuad.getColorIndex());
				if (GameRenderer.anaglyphEnabled) {
					k = TextureUtil.getAnaglyphColor(k);
				}

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
				bitSet.set(0, (g < 1.0E-4F || blockState.method_11730()) && g == j);
				break;
			case UP:
				bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (j > 0.9999F || blockState.method_11730()) && g == j);
				break;
			case NORTH:
				bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				bitSet.set(0, (h < 1.0E-4F || blockState.method_11730()) && h == k);
				break;
			case SOUTH:
				bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				bitSet.set(0, (k > 0.9999F || blockState.method_11730()) && h == k);
				break;
			case WEST:
				bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (f < 1.0E-4F || blockState.method_11730()) && f == i);
				break;
			case EAST:
				bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				bitSet.set(0, (i > 0.9999F || blockState.method_11730()) && f == i);
		}
	}

	private void method_12347(
		BlockView blockView, BlockState blockState, BlockPos blockPos, int i, boolean bl, BufferBuilder bufferBuilder, List<BakedQuad> list, BitSet bitSet
	) {
		Vec3d vec3d = blockState.method_13761(blockView, blockPos);
		double d = (double)blockPos.getX() + vec3d.x;
		double e = (double)blockPos.getY() + vec3d.y;
		double f = (double)blockPos.getZ() + vec3d.z;
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(j);
			if (bl) {
				this.method_9964(blockState, bakedQuad.getVertexData(), bakedQuad.getFace(), null, bitSet);
				BlockPos blockPos2 = bitSet.get(0) ? blockPos.offset(bakedQuad.getFace()) : blockPos;
				i = blockState.method_11712(blockView, blockPos2);
			}

			bufferBuilder.putArray(bakedQuad.getVertexData());
			bufferBuilder.faceTexture2(i, i, i, i);
			if (bakedQuad.hasColor()) {
				int l = this.field_13551.method_12157(blockState, blockView, blockPos, bakedQuad.getColorIndex());
				if (GameRenderer.anaglyphEnabled) {
					l = TextureUtil.getAnaglyphColor(l);
				}

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

	public void method_9965(BlockState blockState, BakedModel bakedModel, float f, float g, float h, float i) {
		for (Direction direction : Direction.values()) {
			this.renderQuads(f, g, h, i, bakedModel.method_12502(blockState, direction, 0L));
		}

		this.renderQuads(f, g, h, i, bakedModel.method_12502(blockState, null, 0L));
	}

	public void render(BakedModel model, BlockState state, float light, boolean bl) {
		Block block = state.getBlock();
		GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
		int i = this.field_13551.method_12157(state, null, null, 0);
		if (GameRenderer.anaglyphEnabled) {
			i = TextureUtil.getAnaglyphColor(i);
		}

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

	class AmbientOcclusionCalculator {
		private final float[] brightness = new float[4];
		private final int[] light = new int[4];

		public AmbientOcclusionCalculator() {
		}

		public void method_9971(BlockView blockView, BlockState blockState, BlockPos blockPos, Direction direction, float[] fs, BitSet bitSet) {
			BlockPos blockPos2 = bitSet.get(0) ? blockPos.offset(direction) : blockPos;
			BlockPos.Pooled pooled = BlockPos.Pooled.get();
			BlockModelRenderer.NeighborData neighborData = BlockModelRenderer.NeighborData.getData(direction);
			BlockPos.Pooled pooled2 = BlockPos.Pooled.method_12573(blockPos2).move(neighborData.field_14954[0]);
			BlockPos.Pooled pooled3 = BlockPos.Pooled.method_12573(blockPos2).move(neighborData.field_14954[1]);
			BlockPos.Pooled pooled4 = BlockPos.Pooled.method_12573(blockPos2).move(neighborData.field_14954[2]);
			BlockPos.Pooled pooled5 = BlockPos.Pooled.method_12573(blockPos2).move(neighborData.field_14954[3]);
			int i = blockState.method_11712(blockView, pooled2);
			int j = blockState.method_11712(blockView, pooled3);
			int k = blockState.method_11712(blockView, pooled4);
			int l = blockState.method_11712(blockView, pooled5);
			float f = blockView.getBlockState(pooled2).getAmbientOcclusionLightLevel();
			float g = blockView.getBlockState(pooled3).getAmbientOcclusionLightLevel();
			float h = blockView.getBlockState(pooled4).getAmbientOcclusionLightLevel();
			float m = blockView.getBlockState(pooled5).getAmbientOcclusionLightLevel();
			boolean bl = blockView.getBlockState(pooled.set(pooled2).move(direction)).isTranslucent();
			boolean bl2 = blockView.getBlockState(pooled.set(pooled3).move(direction)).isTranslucent();
			boolean bl3 = blockView.getBlockState(pooled.set(pooled4).move(direction)).isTranslucent();
			boolean bl4 = blockView.getBlockState(pooled.set(pooled5).move(direction)).isTranslucent();
			float p;
			int q;
			if (!bl3 && !bl) {
				p = f;
				q = i;
			} else {
				BlockPos blockPos3 = pooled.set(pooled2).move(neighborData.field_14954[2]);
				p = blockView.getBlockState(blockPos3).getAmbientOcclusionLightLevel();
				q = blockState.method_11712(blockView, blockPos3);
			}

			float t;
			int u;
			if (!bl4 && !bl) {
				t = f;
				u = i;
			} else {
				BlockPos blockPos4 = pooled.set(pooled2).move(neighborData.field_14954[3]);
				t = blockView.getBlockState(blockPos4).getAmbientOcclusionLightLevel();
				u = blockState.method_11712(blockView, blockPos4);
			}

			float x;
			int y;
			if (!bl3 && !bl2) {
				x = g;
				y = j;
			} else {
				BlockPos blockPos5 = pooled.set(pooled3).move(neighborData.field_14954[2]);
				x = blockView.getBlockState(blockPos5).getAmbientOcclusionLightLevel();
				y = blockState.method_11712(blockView, blockPos5);
			}

			float ab;
			int ac;
			if (!bl4 && !bl2) {
				ab = g;
				ac = j;
			} else {
				BlockPos blockPos6 = pooled.set(pooled3).move(neighborData.field_14954[3]);
				ab = blockView.getBlockState(blockPos6).getAmbientOcclusionLightLevel();
				ac = blockState.method_11712(blockView, blockPos6);
			}

			int ad = blockState.method_11712(blockView, blockPos);
			if (bitSet.get(0) || !blockView.getBlockState(blockPos.offset(direction)).isFullBoundsCubeForCulling()) {
				ad = blockState.method_11712(blockView, blockPos.offset(direction));
			}

			float ae = bitSet.get(0)
				? blockView.getBlockState(blockPos2).getAmbientOcclusionLightLevel()
				: blockView.getBlockState(blockPos).getAmbientOcclusionLightLevel();
			BlockModelRenderer.Translation translation = BlockModelRenderer.Translation.getTranslations(direction);
			pooled.method_12576();
			pooled2.method_12576();
			pooled3.method_12576();
			pooled4.method_12576();
			pooled5.method_12576();
			if (bitSet.get(1) && neighborData.field_14955) {
				float aj = (m + f + t + ae) * 0.25F;
				float ak = (h + f + p + ae) * 0.25F;
				float al = (h + g + x + ae) * 0.25F;
				float am = (m + g + ab + ae) * 0.25F;
				float an = fs[neighborData.field_14956[0].field_14962] * fs[neighborData.field_14956[1].field_14962];
				float ao = fs[neighborData.field_14956[2].field_14962] * fs[neighborData.field_14956[3].field_14962];
				float ap = fs[neighborData.field_14956[4].field_14962] * fs[neighborData.field_14956[5].field_14962];
				float aq = fs[neighborData.field_14956[6].field_14962] * fs[neighborData.field_14956[7].field_14962];
				float ar = fs[neighborData.field_14957[0].field_14962] * fs[neighborData.field_14957[1].field_14962];
				float as = fs[neighborData.field_14957[2].field_14962] * fs[neighborData.field_14957[3].field_14962];
				float at = fs[neighborData.field_14957[4].field_14962] * fs[neighborData.field_14957[5].field_14962];
				float au = fs[neighborData.field_14957[6].field_14962] * fs[neighborData.field_14957[7].field_14962];
				float av = fs[neighborData.field_14958[0].field_14962] * fs[neighborData.field_14958[1].field_14962];
				float aw = fs[neighborData.field_14958[2].field_14962] * fs[neighborData.field_14958[3].field_14962];
				float ax = fs[neighborData.field_14958[4].field_14962] * fs[neighborData.field_14958[5].field_14962];
				float ay = fs[neighborData.field_14958[6].field_14962] * fs[neighborData.field_14958[7].field_14962];
				float az = fs[neighborData.field_14959[0].field_14962] * fs[neighborData.field_14959[1].field_14962];
				float ba = fs[neighborData.field_14959[2].field_14962] * fs[neighborData.field_14959[3].field_14962];
				float bb = fs[neighborData.field_14959[4].field_14962] * fs[neighborData.field_14959[5].field_14962];
				float bc = fs[neighborData.field_14959[6].field_14962] * fs[neighborData.field_14959[7].field_14962];
				this.brightness[translation.thirdCorner] = aj * an + ak * ao + al * ap + am * aq;
				this.brightness[translation.fourthCorner] = aj * ar + ak * as + al * at + am * au;
				this.brightness[translation.field_14960] = aj * av + ak * aw + al * ax + am * ay;
				this.brightness[translation.field_14961] = aj * az + ak * ba + al * bb + am * bc;
				int bd = this.getAmbientOcclusionBrightness(l, i, u, ad);
				int be = this.getAmbientOcclusionBrightness(k, i, q, ad);
				int bf = this.getAmbientOcclusionBrightness(k, j, y, ad);
				int bg = this.getAmbientOcclusionBrightness(l, j, ac, ad);
				this.light[translation.thirdCorner] = this.getBrightness(bd, be, bf, bg, an, ao, ap, aq);
				this.light[translation.fourthCorner] = this.getBrightness(bd, be, bf, bg, ar, as, at, au);
				this.light[translation.field_14960] = this.getBrightness(bd, be, bf, bg, av, aw, ax, ay);
				this.light[translation.field_14961] = this.getBrightness(bd, be, bf, bg, az, ba, bb, bc);
			} else {
				float af = (m + f + t + ae) * 0.25F;
				float ag = (h + f + p + ae) * 0.25F;
				float ah = (h + g + x + ae) * 0.25F;
				float ai = (m + g + ab + ae) * 0.25F;
				this.light[translation.thirdCorner] = this.getAmbientOcclusionBrightness(l, i, u, ad);
				this.light[translation.fourthCorner] = this.getAmbientOcclusionBrightness(k, i, q, ad);
				this.light[translation.field_14960] = this.getAmbientOcclusionBrightness(k, j, y, ad);
				this.light[translation.field_14961] = this.getAmbientOcclusionBrightness(l, j, ac, ad);
				this.brightness[translation.thirdCorner] = af;
				this.brightness[translation.fourthCorner] = ag;
				this.brightness[translation.field_14960] = ah;
				this.brightness[translation.field_14961] = ai;
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
		private final float field_10870;
		private final boolean field_14955;
		private final BlockModelRenderer.NeighborOrientation[] field_14956;
		private final BlockModelRenderer.NeighborOrientation[] field_14957;
		private final BlockModelRenderer.NeighborOrientation[] field_14958;
		private final BlockModelRenderer.NeighborOrientation[] field_14959;
		private static final BlockModelRenderer.NeighborData[] field_10876 = new BlockModelRenderer.NeighborData[6];

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
			this.field_10870 = f;
			this.field_14955 = bl;
			this.field_14956 = neighborOrientations;
			this.field_14957 = neighborOrientations2;
			this.field_14958 = neighborOrientations3;
			this.field_14959 = neighborOrientations4;
		}

		public static BlockModelRenderer.NeighborData getData(Direction direction) {
			return field_10876[direction.getId()];
		}

		static {
			field_10876[Direction.DOWN.getId()] = DOWN;
			field_10876[Direction.UP.getId()] = UP;
			field_10876[Direction.NORTH.getId()] = NORTH;
			field_10876[Direction.SOUTH.getId()] = SOUTH;
			field_10876[Direction.WEST.getId()] = WEST;
			field_10876[Direction.EAST.getId()] = EAST;
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

		private final int thirdCorner;
		private final int fourthCorner;
		private final int field_14960;
		private final int field_14961;
		private static final BlockModelRenderer.Translation[] ALL = new BlockModelRenderer.Translation[6];

		private Translation(int j, int k, int l, int m) {
			this.thirdCorner = j;
			this.fourthCorner = k;
			this.field_14960 = l;
			this.field_14961 = m;
		}

		public static BlockModelRenderer.Translation getTranslations(Direction face) {
			return ALL[face.getId()];
		}

		static {
			ALL[Direction.DOWN.getId()] = DOWN;
			ALL[Direction.UP.getId()] = UP;
			ALL[Direction.NORTH.getId()] = NORTH;
			ALL[Direction.SOUTH.getId()] = SOUTH;
			ALL[Direction.WEST.getId()] = WEST;
			ALL[Direction.EAST.getId()] = EAST;
		}
	}
}
