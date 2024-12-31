package net.minecraft.client.render.block;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.BitSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;

public class BlockModelRenderer {
	public boolean render(BlockView world, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer) {
		Block block = state.getBlock();
		block.setBoundingBox(world, pos);
		return this.render(world, model, state, pos, buffer, true);
	}

	public boolean render(BlockView world, BakedModel model, BlockState state, BlockPos pos, BufferBuilder buffer, boolean cull) {
		boolean bl = MinecraftClient.isAmbientOcclusionEnabled() && state.getBlock().getLightLevel() == 0 && model.useAmbientOcclusion();

		try {
			Block block = state.getBlock();
			return bl ? this.renderSmooth(world, model, block, pos, buffer, cull) : this.renderFlat(world, model, block, pos, buffer, cull);
		} catch (Throwable var11) {
			CrashReport crashReport = CrashReport.create(var11, "Tesselating block model");
			CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, pos, state);
			crashReportSection.add("Using AO", bl);
			throw new CrashException(crashReport);
		}
	}

	public boolean renderSmooth(BlockView world, BakedModel model, Block block, BlockPos pos, BufferBuilder buffer, boolean cull) {
		boolean bl = false;
		float[] fs = new float[Direction.values().length * 2];
		BitSet bitSet = new BitSet(3);
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator = new BlockModelRenderer.AmbientOcclusionCalculator();

		for (Direction direction : Direction.values()) {
			List<BakedQuad> list = model.getByDirection(direction);
			if (!list.isEmpty()) {
				BlockPos blockPos = pos.offset(direction);
				if (!cull || block.isSideInvisible(world, blockPos, direction)) {
					this.renderQuadsSmooth(world, block, pos, buffer, list, fs, bitSet, ambientOcclusionCalculator);
					bl = true;
				}
			}
		}

		List<BakedQuad> list2 = model.getQuads();
		if (list2.size() > 0) {
			this.renderQuadsSmooth(world, block, pos, buffer, list2, fs, bitSet, ambientOcclusionCalculator);
			bl = true;
		}

		return bl;
	}

	public boolean renderFlat(BlockView world, BakedModel model, Block block, BlockPos pos, BufferBuilder buffer, boolean cull) {
		boolean bl = false;
		BitSet bitSet = new BitSet(3);

		for (Direction direction : Direction.values()) {
			List<BakedQuad> list = model.getByDirection(direction);
			if (!list.isEmpty()) {
				BlockPos blockPos = pos.offset(direction);
				if (!cull || block.isSideInvisible(world, blockPos, direction)) {
					int k = block.getBrightness(world, blockPos);
					this.renderQuadsFlat(world, block, pos, direction, k, false, buffer, list, bitSet);
					bl = true;
				}
			}
		}

		List<BakedQuad> list2 = model.getQuads();
		if (list2.size() > 0) {
			this.renderQuadsFlat(world, block, pos, null, -1, true, buffer, list2, bitSet);
			bl = true;
		}

		return bl;
	}

	private void renderQuadsSmooth(
		BlockView world,
		Block block,
		BlockPos pos,
		BufferBuilder buffer,
		List<BakedQuad> quads,
		float[] box,
		BitSet flags,
		BlockModelRenderer.AmbientOcclusionCalculator aoCalculator
	) {
		double d = (double)pos.getX();
		double e = (double)pos.getY();
		double f = (double)pos.getZ();
		Block.OffsetType offsetType = block.getOffsetType();
		if (offsetType != Block.OffsetType.NONE) {
			long l = MathHelper.hashCode(pos);
			d += ((double)((float)(l >> 16 & 15L) / 15.0F) - 0.5) * 0.5;
			f += ((double)((float)(l >> 24 & 15L) / 15.0F) - 0.5) * 0.5;
			if (offsetType == Block.OffsetType.XYZ) {
				e += ((double)((float)(l >> 20 & 15L) / 15.0F) - 1.0) * 0.2;
			}
		}

		for (BakedQuad bakedQuad : quads) {
			this.getQuadDimensions(block, bakedQuad.getVertexData(), bakedQuad.getFace(), box, flags);
			aoCalculator.apply(world, block, pos, bakedQuad.getFace(), box, flags);
			buffer.putArray(bakedQuad.getVertexData());
			buffer.faceTexture2(aoCalculator.light[0], aoCalculator.light[1], aoCalculator.light[2], aoCalculator.light[3]);
			if (bakedQuad.hasColor()) {
				int i = block.getBlockColor(world, pos, bakedQuad.getColorIndex());
				if (GameRenderer.anaglyphEnabled) {
					i = TextureUtil.getAnaglyphColor(i);
				}

				float g = (float)(i >> 16 & 0xFF) / 255.0F;
				float h = (float)(i >> 8 & 0xFF) / 255.0F;
				float j = (float)(i & 0xFF) / 255.0F;
				buffer.faceTint(aoCalculator.brightness[0] * g, aoCalculator.brightness[0] * h, aoCalculator.brightness[0] * j, 4);
				buffer.faceTint(aoCalculator.brightness[1] * g, aoCalculator.brightness[1] * h, aoCalculator.brightness[1] * j, 3);
				buffer.faceTint(aoCalculator.brightness[2] * g, aoCalculator.brightness[2] * h, aoCalculator.brightness[2] * j, 2);
				buffer.faceTint(aoCalculator.brightness[3] * g, aoCalculator.brightness[3] * h, aoCalculator.brightness[3] * j, 1);
			} else {
				buffer.faceTint(aoCalculator.brightness[0], aoCalculator.brightness[0], aoCalculator.brightness[0], 4);
				buffer.faceTint(aoCalculator.brightness[1], aoCalculator.brightness[1], aoCalculator.brightness[1], 3);
				buffer.faceTint(aoCalculator.brightness[2], aoCalculator.brightness[2], aoCalculator.brightness[2], 2);
				buffer.faceTint(aoCalculator.brightness[3], aoCalculator.brightness[3], aoCalculator.brightness[3], 1);
			}

			buffer.postProcessFacePosition(d, e, f);
		}
	}

	private void getQuadDimensions(Block block, int[] vertexData, Direction face, float[] box, BitSet flags) {
		float f = 32.0F;
		float g = 32.0F;
		float h = 32.0F;
		float i = -32.0F;
		float j = -32.0F;
		float k = -32.0F;

		for (int l = 0; l < 4; l++) {
			float m = Float.intBitsToFloat(vertexData[l * 7]);
			float n = Float.intBitsToFloat(vertexData[l * 7 + 1]);
			float o = Float.intBitsToFloat(vertexData[l * 7 + 2]);
			f = Math.min(f, m);
			g = Math.min(g, n);
			h = Math.min(h, o);
			i = Math.max(i, m);
			j = Math.max(j, n);
			k = Math.max(k, o);
		}

		if (box != null) {
			box[Direction.WEST.getId()] = f;
			box[Direction.EAST.getId()] = i;
			box[Direction.DOWN.getId()] = g;
			box[Direction.UP.getId()] = j;
			box[Direction.NORTH.getId()] = h;
			box[Direction.SOUTH.getId()] = k;
			box[Direction.WEST.getId() + Direction.values().length] = 1.0F - f;
			box[Direction.EAST.getId() + Direction.values().length] = 1.0F - i;
			box[Direction.DOWN.getId() + Direction.values().length] = 1.0F - g;
			box[Direction.UP.getId() + Direction.values().length] = 1.0F - j;
			box[Direction.NORTH.getId() + Direction.values().length] = 1.0F - h;
			box[Direction.SOUTH.getId() + Direction.values().length] = 1.0F - k;
		}

		float p = 1.0E-4F;
		float q = 0.9999F;
		switch (face) {
			case DOWN:
				flags.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				flags.set(0, (g < 1.0E-4F || block.renderAsNormalBlock()) && g == j);
				break;
			case UP:
				flags.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				flags.set(0, (j > 0.9999F || block.renderAsNormalBlock()) && g == j);
				break;
			case NORTH:
				flags.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				flags.set(0, (h < 1.0E-4F || block.renderAsNormalBlock()) && h == k);
				break;
			case SOUTH:
				flags.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				flags.set(0, (k > 0.9999F || block.renderAsNormalBlock()) && h == k);
				break;
			case WEST:
				flags.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				flags.set(0, (f < 1.0E-4F || block.renderAsNormalBlock()) && f == i);
				break;
			case EAST:
				flags.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				flags.set(0, (i > 0.9999F || block.renderAsNormalBlock()) && f == i);
		}
	}

	private void renderQuadsFlat(
		BlockView world, Block block, BlockPos pos, Direction face, int light, boolean useWorldLight, BufferBuilder buffer, List<BakedQuad> quads, BitSet flags
	) {
		double d = (double)pos.getX();
		double e = (double)pos.getY();
		double f = (double)pos.getZ();
		Block.OffsetType offsetType = block.getOffsetType();
		if (offsetType != Block.OffsetType.NONE) {
			int i = pos.getX();
			int j = pos.getZ();
			long l = (long)(i * 3129871) ^ (long)j * 116129781L;
			l = l * l * 42317861L + l * 11L;
			d += ((double)((float)(l >> 16 & 15L) / 15.0F) - 0.5) * 0.5;
			f += ((double)((float)(l >> 24 & 15L) / 15.0F) - 0.5) * 0.5;
			if (offsetType == Block.OffsetType.XYZ) {
				e += ((double)((float)(l >> 20 & 15L) / 15.0F) - 1.0) * 0.2;
			}
		}

		for (BakedQuad bakedQuad : quads) {
			if (useWorldLight) {
				this.getQuadDimensions(block, bakedQuad.getVertexData(), bakedQuad.getFace(), null, flags);
				light = flags.get(0) ? block.getBrightness(world, pos.offset(bakedQuad.getFace())) : block.getBrightness(world, pos);
			}

			buffer.putArray(bakedQuad.getVertexData());
			buffer.faceTexture2(light, light, light, light);
			if (bakedQuad.hasColor()) {
				int k = block.getBlockColor(world, pos, bakedQuad.getColorIndex());
				if (GameRenderer.anaglyphEnabled) {
					k = TextureUtil.getAnaglyphColor(k);
				}

				float g = (float)(k >> 16 & 0xFF) / 255.0F;
				float h = (float)(k >> 8 & 0xFF) / 255.0F;
				float m = (float)(k & 0xFF) / 255.0F;
				buffer.faceTint(g, h, m, 4);
				buffer.faceTint(g, h, m, 3);
				buffer.faceTint(g, h, m, 2);
				buffer.faceTint(g, h, m, 1);
			}

			buffer.postProcessFacePosition(d, e, f);
		}
	}

	public void render(BakedModel model, float light, float red, float green, float blue) {
		for (Direction direction : Direction.values()) {
			this.renderQuads(light, red, green, blue, model.getByDirection(direction));
		}

		this.renderQuads(light, red, green, blue, model.getQuads());
	}

	public void render(BakedModel model, BlockState state, float light, boolean bl) {
		Block block = state.getBlock();
		block.setBlockItemBounds();
		GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
		int i = block.getColor(block.getRenderState(state));
		if (GameRenderer.anaglyphEnabled) {
			i = TextureUtil.getAnaglyphColor(i);
		}

		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		if (!bl) {
			GlStateManager.color(light, light, light, 1.0F);
		}

		this.render(model, light, f, g, h);
	}

	private void renderQuads(float light, float red, float green, float blue, List<BakedQuad> quads) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		for (BakedQuad bakedQuad : quads) {
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

		public void apply(BlockView world, Block block, BlockPos pos, Direction direction, float[] fs, BitSet bitSet) {
			BlockPos blockPos = bitSet.get(0) ? pos.offset(direction) : pos;
			BlockModelRenderer.NeighborData neighborData = BlockModelRenderer.NeighborData.getData(direction);
			BlockPos blockPos2 = blockPos.offset(neighborData.field_10869[0]);
			BlockPos blockPos3 = blockPos.offset(neighborData.field_10869[1]);
			BlockPos blockPos4 = blockPos.offset(neighborData.field_10869[2]);
			BlockPos blockPos5 = blockPos.offset(neighborData.field_10869[3]);
			int i = block.getBrightness(world, blockPos2);
			int j = block.getBrightness(world, blockPos3);
			int k = block.getBrightness(world, blockPos4);
			int l = block.getBrightness(world, blockPos5);
			float f = world.getBlockState(blockPos2).getBlock().getAmbientOcclusionLightLevel();
			float g = world.getBlockState(blockPos3).getBlock().getAmbientOcclusionLightLevel();
			float h = world.getBlockState(blockPos4).getBlock().getAmbientOcclusionLightLevel();
			float m = world.getBlockState(blockPos5).getBlock().getAmbientOcclusionLightLevel();
			boolean bl = world.getBlockState(blockPos2.offset(direction)).getBlock().isTranslucent();
			boolean bl2 = world.getBlockState(blockPos3.offset(direction)).getBlock().isTranslucent();
			boolean bl3 = world.getBlockState(blockPos4.offset(direction)).getBlock().isTranslucent();
			boolean bl4 = world.getBlockState(blockPos5.offset(direction)).getBlock().isTranslucent();
			float p;
			int q;
			if (!bl3 && !bl) {
				p = f;
				q = i;
			} else {
				BlockPos blockPos6 = blockPos2.offset(neighborData.field_10869[2]);
				p = world.getBlockState(blockPos6).getBlock().getAmbientOcclusionLightLevel();
				q = block.getBrightness(world, blockPos6);
			}

			float t;
			int u;
			if (!bl4 && !bl) {
				t = f;
				u = i;
			} else {
				BlockPos blockPos7 = blockPos2.offset(neighborData.field_10869[3]);
				t = world.getBlockState(blockPos7).getBlock().getAmbientOcclusionLightLevel();
				u = block.getBrightness(world, blockPos7);
			}

			float x;
			int y;
			if (!bl3 && !bl2) {
				x = g;
				y = j;
			} else {
				BlockPos blockPos8 = blockPos3.offset(neighborData.field_10869[2]);
				x = world.getBlockState(blockPos8).getBlock().getAmbientOcclusionLightLevel();
				y = block.getBrightness(world, blockPos8);
			}

			float ab;
			int ac;
			if (!bl4 && !bl2) {
				ab = g;
				ac = j;
			} else {
				BlockPos blockPos9 = blockPos3.offset(neighborData.field_10869[3]);
				ab = world.getBlockState(blockPos9).getBlock().getAmbientOcclusionLightLevel();
				ac = block.getBrightness(world, blockPos9);
			}

			int ad = block.getBrightness(world, pos);
			if (bitSet.get(0) || !world.getBlockState(pos.offset(direction)).getBlock().hasTransparency()) {
				ad = block.getBrightness(world, pos.offset(direction));
			}

			float ae = bitSet.get(0)
				? world.getBlockState(blockPos).getBlock().getAmbientOcclusionLightLevel()
				: world.getBlockState(pos).getBlock().getAmbientOcclusionLightLevel();
			BlockModelRenderer.Translation translation = BlockModelRenderer.Translation.getTranslations(direction);
			if (bitSet.get(1) && neighborData.field_10871) {
				float aj = (m + f + t + ae) * 0.25F;
				float ak = (h + f + p + ae) * 0.25F;
				float al = (h + g + x + ae) * 0.25F;
				float am = (m + g + ab + ae) * 0.25F;
				float an = fs[neighborData.field_10872[0].shape] * fs[neighborData.field_10872[1].shape];
				float ao = fs[neighborData.field_10872[2].shape] * fs[neighborData.field_10872[3].shape];
				float ap = fs[neighborData.field_10872[4].shape] * fs[neighborData.field_10872[5].shape];
				float aq = fs[neighborData.field_10872[6].shape] * fs[neighborData.field_10872[7].shape];
				float ar = fs[neighborData.field_10873[0].shape] * fs[neighborData.field_10873[1].shape];
				float as = fs[neighborData.field_10873[2].shape] * fs[neighborData.field_10873[3].shape];
				float at = fs[neighborData.field_10873[4].shape] * fs[neighborData.field_10873[5].shape];
				float au = fs[neighborData.field_10873[6].shape] * fs[neighborData.field_10873[7].shape];
				float av = fs[neighborData.field_10874[0].shape] * fs[neighborData.field_10874[1].shape];
				float aw = fs[neighborData.field_10874[2].shape] * fs[neighborData.field_10874[3].shape];
				float ax = fs[neighborData.field_10874[4].shape] * fs[neighborData.field_10874[5].shape];
				float ay = fs[neighborData.field_10874[6].shape] * fs[neighborData.field_10874[7].shape];
				float az = fs[neighborData.field_10875[0].shape] * fs[neighborData.field_10875[1].shape];
				float ba = fs[neighborData.field_10875[2].shape] * fs[neighborData.field_10875[3].shape];
				float bb = fs[neighborData.field_10875[4].shape] * fs[neighborData.field_10875[5].shape];
				float bc = fs[neighborData.field_10875[6].shape] * fs[neighborData.field_10875[7].shape];
				this.brightness[translation.firstCorner] = aj * an + ak * ao + al * ap + am * aq;
				this.brightness[translation.secondCorner] = aj * ar + ak * as + al * at + am * au;
				this.brightness[translation.thirdCorner] = aj * av + ak * aw + al * ax + am * ay;
				this.brightness[translation.fourthCorner] = aj * az + ak * ba + al * bb + am * bc;
				int bd = this.getAmbientOcclusionBrightness(l, i, u, ad);
				int be = this.getAmbientOcclusionBrightness(k, i, q, ad);
				int bf = this.getAmbientOcclusionBrightness(k, j, y, ad);
				int bg = this.getAmbientOcclusionBrightness(l, j, ac, ad);
				this.light[translation.firstCorner] = this.getBrightness(bd, be, bf, bg, an, ao, ap, aq);
				this.light[translation.secondCorner] = this.getBrightness(bd, be, bf, bg, ar, as, at, au);
				this.light[translation.thirdCorner] = this.getBrightness(bd, be, bf, bg, av, aw, ax, ay);
				this.light[translation.fourthCorner] = this.getBrightness(bd, be, bf, bg, az, ba, bb, bc);
			} else {
				float af = (m + f + t + ae) * 0.25F;
				float ag = (h + f + p + ae) * 0.25F;
				float ah = (h + g + x + ae) * 0.25F;
				float ai = (m + g + ab + ae) * 0.25F;
				this.light[translation.firstCorner] = this.getAmbientOcclusionBrightness(l, i, u, ad);
				this.light[translation.secondCorner] = this.getAmbientOcclusionBrightness(k, i, q, ad);
				this.light[translation.thirdCorner] = this.getAmbientOcclusionBrightness(k, j, y, ad);
				this.light[translation.fourthCorner] = this.getAmbientOcclusionBrightness(l, j, ac, ad);
				this.brightness[translation.firstCorner] = af;
				this.brightness[translation.secondCorner] = ag;
				this.brightness[translation.thirdCorner] = ah;
				this.brightness[translation.fourthCorner] = ai;
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
			false,
			new BlockModelRenderer.NeighborOrientation[0],
			new BlockModelRenderer.NeighborOrientation[0],
			new BlockModelRenderer.NeighborOrientation[0],
			new BlockModelRenderer.NeighborOrientation[0]
		),
		UP(
			new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH},
			1.0F,
			false,
			new BlockModelRenderer.NeighborOrientation[0],
			new BlockModelRenderer.NeighborOrientation[0],
			new BlockModelRenderer.NeighborOrientation[0],
			new BlockModelRenderer.NeighborOrientation[0]
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

		protected final Direction[] field_10869;
		protected final float field_10870;
		protected final boolean field_10871;
		protected final BlockModelRenderer.NeighborOrientation[] field_10872;
		protected final BlockModelRenderer.NeighborOrientation[] field_10873;
		protected final BlockModelRenderer.NeighborOrientation[] field_10874;
		protected final BlockModelRenderer.NeighborOrientation[] field_10875;
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
			this.field_10869 = directions;
			this.field_10870 = f;
			this.field_10871 = bl;
			this.field_10872 = neighborOrientations;
			this.field_10873 = neighborOrientations2;
			this.field_10874 = neighborOrientations3;
			this.field_10875 = neighborOrientations4;
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

		protected final int shape;

		private NeighborOrientation(Direction direction, boolean bl) {
			this.shape = direction.getId() + (bl ? Direction.values().length : 0);
		}
	}

	static enum Translation {
		DOWN(0, 1, 2, 3),
		UP(2, 3, 0, 1),
		NORTH(3, 0, 1, 2),
		SOUTH(0, 1, 2, 3),
		WEST(3, 0, 1, 2),
		EAST(1, 2, 3, 0);

		private final int firstCorner;
		private final int secondCorner;
		private final int thirdCorner;
		private final int fourthCorner;
		private static final BlockModelRenderer.Translation[] ALL = new BlockModelRenderer.Translation[6];

		private Translation(int j, int k, int l, int m) {
			this.firstCorner = j;
			this.secondCorner = k;
			this.thirdCorner = l;
			this.fourthCorner = m;
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
