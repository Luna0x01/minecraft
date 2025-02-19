package net.minecraft.client.render.block;

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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;

public class BlockModelRenderer {
	private static final int field_32782 = 0;
	private static final int field_32783 = 1;
	static final Direction[] DIRECTIONS = Direction.values();
	private final BlockColors colorMap;
	private static final int field_32784 = 100;
	static final ThreadLocal<BlockModelRenderer.BrightnessCache> brightnessCache = ThreadLocal.withInitial(BlockModelRenderer.BrightnessCache::new);

	public BlockModelRenderer(BlockColors colorMap) {
		this.colorMap = colorMap;
	}

	public boolean render(
		BlockRenderView world,
		BakedModel model,
		BlockState state,
		BlockPos pos,
		MatrixStack matrix,
		VertexConsumer vertexConsumer,
		boolean cull,
		Random random,
		long seed,
		int overlay
	) {
		boolean bl = MinecraftClient.isAmbientOcclusionEnabled() && state.getLuminance() == 0 && model.useAmbientOcclusion();
		Vec3d vec3d = state.getModelOffset(world, pos);
		matrix.translate(vec3d.x, vec3d.y, vec3d.z);

		try {
			return bl
				? this.renderSmooth(world, model, state, pos, matrix, vertexConsumer, cull, random, seed, overlay)
				: this.renderFlat(world, model, state, pos, matrix, vertexConsumer, cull, random, seed, overlay);
		} catch (Throwable var17) {
			CrashReport crashReport = CrashReport.create(var17, "Tesselating block model");
			CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, world, pos, state);
			crashReportSection.add("Using AO", bl);
			throw new CrashException(crashReport);
		}
	}

	public boolean renderSmooth(
		BlockRenderView world,
		BakedModel model,
		BlockState state,
		BlockPos pos,
		MatrixStack buffer,
		VertexConsumer vertexConsumer,
		boolean cull,
		Random random,
		long seed,
		int overlay
	) {
		boolean bl = false;
		float[] fs = new float[DIRECTIONS.length * 2];
		BitSet bitSet = new BitSet(3);
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator = new BlockModelRenderer.AmbientOcclusionCalculator();
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (Direction direction : DIRECTIONS) {
			random.setSeed(seed);
			List<BakedQuad> list = model.getQuads(state, direction, random);
			if (!list.isEmpty()) {
				mutable.set(pos, direction);
				if (!cull || Block.shouldDrawSide(state, world, pos, direction, mutable)) {
					this.renderQuadsSmooth(world, state, pos, buffer, vertexConsumer, list, fs, bitSet, ambientOcclusionCalculator, overlay);
					bl = true;
				}
			}
		}

		random.setSeed(seed);
		List<BakedQuad> list2 = model.getQuads(state, null, random);
		if (!list2.isEmpty()) {
			this.renderQuadsSmooth(world, state, pos, buffer, vertexConsumer, list2, fs, bitSet, ambientOcclusionCalculator, overlay);
			bl = true;
		}

		return bl;
	}

	public boolean renderFlat(
		BlockRenderView world,
		BakedModel model,
		BlockState state,
		BlockPos pos,
		MatrixStack buffer,
		VertexConsumer vertexConsumer,
		boolean cull,
		Random random,
		long l,
		int i
	) {
		boolean bl = false;
		BitSet bitSet = new BitSet(3);
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (Direction direction : DIRECTIONS) {
			random.setSeed(l);
			List<BakedQuad> list = model.getQuads(state, direction, random);
			if (!list.isEmpty()) {
				mutable.set(pos, direction);
				if (!cull || Block.shouldDrawSide(state, world, pos, direction, mutable)) {
					int j = WorldRenderer.getLightmapCoordinates(world, state, mutable);
					this.renderQuadsFlat(world, state, pos, j, i, false, buffer, vertexConsumer, list, bitSet);
					bl = true;
				}
			}
		}

		random.setSeed(l);
		List<BakedQuad> list2 = model.getQuads(state, null, random);
		if (!list2.isEmpty()) {
			this.renderQuadsFlat(world, state, pos, -1, i, true, buffer, vertexConsumer, list2, bitSet);
			bl = true;
		}

		return bl;
	}

	private void renderQuadsSmooth(
		BlockRenderView world,
		BlockState state,
		BlockPos pos,
		MatrixStack matrix,
		VertexConsumer vertexConsumer,
		List<BakedQuad> quads,
		float[] box,
		BitSet flags,
		BlockModelRenderer.AmbientOcclusionCalculator ambientOcclusionCalculator,
		int overlay
	) {
		for (BakedQuad bakedQuad : quads) {
			this.getQuadDimensions(world, state, pos, bakedQuad.getVertexData(), bakedQuad.getFace(), box, flags);
			ambientOcclusionCalculator.apply(world, state, pos, bakedQuad.getFace(), box, flags, bakedQuad.hasShade());
			this.renderQuad(
				world,
				state,
				pos,
				vertexConsumer,
				matrix.peek(),
				bakedQuad,
				ambientOcclusionCalculator.brightness[0],
				ambientOcclusionCalculator.brightness[1],
				ambientOcclusionCalculator.brightness[2],
				ambientOcclusionCalculator.brightness[3],
				ambientOcclusionCalculator.light[0],
				ambientOcclusionCalculator.light[1],
				ambientOcclusionCalculator.light[2],
				ambientOcclusionCalculator.light[3],
				overlay
			);
		}
	}

	private void renderQuad(
		BlockRenderView world,
		BlockState state,
		BlockPos pos,
		VertexConsumer vertexConsumer,
		MatrixStack.Entry matrixEntry,
		BakedQuad quad,
		float brightness0,
		float brightness1,
		float brightness2,
		float brightness3,
		int light0,
		int light1,
		int light2,
		int light3,
		int overlay
	) {
		float f;
		float g;
		float h;
		if (quad.hasColor()) {
			int i = this.colorMap.getColor(state, world, pos, quad.getColorIndex());
			f = (float)(i >> 16 & 0xFF) / 255.0F;
			g = (float)(i >> 8 & 0xFF) / 255.0F;
			h = (float)(i & 0xFF) / 255.0F;
		} else {
			f = 1.0F;
			g = 1.0F;
			h = 1.0F;
		}

		vertexConsumer.quad(
			matrixEntry, quad, new float[]{brightness0, brightness1, brightness2, brightness3}, f, g, h, new int[]{light0, light1, light2, light3}, overlay, true
		);
	}

	private void getQuadDimensions(BlockRenderView world, BlockState state, BlockPos pos, int[] vertexData, Direction face, @Nullable float[] box, BitSet flags) {
		float f = 32.0F;
		float g = 32.0F;
		float h = 32.0F;
		float i = -32.0F;
		float j = -32.0F;
		float k = -32.0F;

		for (int l = 0; l < 4; l++) {
			float m = Float.intBitsToFloat(vertexData[l * 8]);
			float n = Float.intBitsToFloat(vertexData[l * 8 + 1]);
			float o = Float.intBitsToFloat(vertexData[l * 8 + 2]);
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
			int p = DIRECTIONS.length;
			box[Direction.WEST.getId() + p] = 1.0F - f;
			box[Direction.EAST.getId() + p] = 1.0F - i;
			box[Direction.DOWN.getId() + p] = 1.0F - g;
			box[Direction.UP.getId() + p] = 1.0F - j;
			box[Direction.NORTH.getId() + p] = 1.0F - h;
			box[Direction.SOUTH.getId() + p] = 1.0F - k;
		}

		float q = 1.0E-4F;
		float r = 0.9999F;
		switch (face) {
			case DOWN:
				flags.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				flags.set(0, g == j && (g < 1.0E-4F || state.isFullCube(world, pos)));
				break;
			case UP:
				flags.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
				flags.set(0, g == j && (j > 0.9999F || state.isFullCube(world, pos)));
				break;
			case NORTH:
				flags.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				flags.set(0, h == k && (h < 1.0E-4F || state.isFullCube(world, pos)));
				break;
			case SOUTH:
				flags.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
				flags.set(0, h == k && (k > 0.9999F || state.isFullCube(world, pos)));
				break;
			case WEST:
				flags.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				flags.set(0, f == i && (f < 1.0E-4F || state.isFullCube(world, pos)));
				break;
			case EAST:
				flags.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
				flags.set(0, f == i && (i > 0.9999F || state.isFullCube(world, pos)));
		}
	}

	private void renderQuadsFlat(
		BlockRenderView world,
		BlockState state,
		BlockPos pos,
		int light,
		int overlay,
		boolean useWorldLight,
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		List<BakedQuad> quads,
		BitSet flags
	) {
		for (BakedQuad bakedQuad : quads) {
			if (useWorldLight) {
				this.getQuadDimensions(world, state, pos, bakedQuad.getVertexData(), bakedQuad.getFace(), null, flags);
				BlockPos blockPos = flags.get(0) ? pos.offset(bakedQuad.getFace()) : pos;
				light = WorldRenderer.getLightmapCoordinates(world, state, blockPos);
			}

			float f = world.getBrightness(bakedQuad.getFace(), bakedQuad.hasShade());
			this.renderQuad(world, state, pos, vertexConsumer, matrices.peek(), bakedQuad, f, f, f, f, light, light, light, light, overlay);
		}
	}

	public void render(
		MatrixStack.Entry entry, VertexConsumer vertexConsumer, @Nullable BlockState blockState, BakedModel bakedModel, float f, float g, float h, int i, int j
	) {
		Random random = new Random();
		long l = 42L;

		for (Direction direction : DIRECTIONS) {
			random.setSeed(42L);
			renderQuad(entry, vertexConsumer, f, g, h, bakedModel.getQuads(blockState, direction, random), i, j);
		}

		random.setSeed(42L);
		renderQuad(entry, vertexConsumer, f, g, h, bakedModel.getQuads(blockState, null, random), i, j);
	}

	private static void renderQuad(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float f, float g, float h, List<BakedQuad> list, int i, int j) {
		for (BakedQuad bakedQuad : list) {
			float k;
			float l;
			float m;
			if (bakedQuad.hasColor()) {
				k = MathHelper.clamp(f, 0.0F, 1.0F);
				l = MathHelper.clamp(g, 0.0F, 1.0F);
				m = MathHelper.clamp(h, 0.0F, 1.0F);
			} else {
				k = 1.0F;
				l = 1.0F;
				m = 1.0F;
			}

			vertexConsumer.quad(entry, bakedQuad, k, l, m, i, j);
		}
	}

	public static void enableBrightnessCache() {
		((BlockModelRenderer.BrightnessCache)brightnessCache.get()).enable();
	}

	public static void disableBrightnessCache() {
		((BlockModelRenderer.BrightnessCache)brightnessCache.get()).disable();
	}

	class AmbientOcclusionCalculator {
		final float[] brightness = new float[4];
		final int[] light = new int[4];

		public AmbientOcclusionCalculator() {
		}

		public void apply(BlockRenderView world, BlockState state, BlockPos pos, Direction direction, float[] box, BitSet flags, boolean bl) {
			BlockPos blockPos = flags.get(0) ? pos.offset(direction) : pos;
			BlockModelRenderer.NeighborData neighborData = BlockModelRenderer.NeighborData.getData(direction);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			BlockModelRenderer.BrightnessCache brightnessCache = (BlockModelRenderer.BrightnessCache)BlockModelRenderer.brightnessCache.get();
			mutable.set(blockPos, neighborData.faces[0]);
			BlockState blockState = world.getBlockState(mutable);
			int i = brightnessCache.getInt(blockState, world, mutable);
			float f = brightnessCache.getFloat(blockState, world, mutable);
			mutable.set(blockPos, neighborData.faces[1]);
			BlockState blockState2 = world.getBlockState(mutable);
			int j = brightnessCache.getInt(blockState2, world, mutable);
			float g = brightnessCache.getFloat(blockState2, world, mutable);
			mutable.set(blockPos, neighborData.faces[2]);
			BlockState blockState3 = world.getBlockState(mutable);
			int k = brightnessCache.getInt(blockState3, world, mutable);
			float h = brightnessCache.getFloat(blockState3, world, mutable);
			mutable.set(blockPos, neighborData.faces[3]);
			BlockState blockState4 = world.getBlockState(mutable);
			int l = brightnessCache.getInt(blockState4, world, mutable);
			float m = brightnessCache.getFloat(blockState4, world, mutable);
			BlockState blockState5 = world.getBlockState(mutable.set(blockPos, neighborData.faces[0]).move(direction));
			boolean bl2 = !blockState5.shouldBlockVision(world, mutable) || blockState5.getOpacity(world, mutable) == 0;
			BlockState blockState6 = world.getBlockState(mutable.set(blockPos, neighborData.faces[1]).move(direction));
			boolean bl3 = !blockState6.shouldBlockVision(world, mutable) || blockState6.getOpacity(world, mutable) == 0;
			BlockState blockState7 = world.getBlockState(mutable.set(blockPos, neighborData.faces[2]).move(direction));
			boolean bl4 = !blockState7.shouldBlockVision(world, mutable) || blockState7.getOpacity(world, mutable) == 0;
			BlockState blockState8 = world.getBlockState(mutable.set(blockPos, neighborData.faces[3]).move(direction));
			boolean bl5 = !blockState8.shouldBlockVision(world, mutable) || blockState8.getOpacity(world, mutable) == 0;
			float p;
			int q;
			if (!bl4 && !bl2) {
				p = f;
				q = i;
			} else {
				mutable.set(blockPos, neighborData.faces[0]).move(neighborData.faces[2]);
				BlockState blockState9 = world.getBlockState(mutable);
				p = brightnessCache.getFloat(blockState9, world, mutable);
				q = brightnessCache.getInt(blockState9, world, mutable);
			}

			float t;
			int u;
			if (!bl5 && !bl2) {
				t = f;
				u = i;
			} else {
				mutable.set(blockPos, neighborData.faces[0]).move(neighborData.faces[3]);
				BlockState blockState10 = world.getBlockState(mutable);
				t = brightnessCache.getFloat(blockState10, world, mutable);
				u = brightnessCache.getInt(blockState10, world, mutable);
			}

			float x;
			int y;
			if (!bl4 && !bl3) {
				x = f;
				y = i;
			} else {
				mutable.set(blockPos, neighborData.faces[1]).move(neighborData.faces[2]);
				BlockState blockState11 = world.getBlockState(mutable);
				x = brightnessCache.getFloat(blockState11, world, mutable);
				y = brightnessCache.getInt(blockState11, world, mutable);
			}

			float ab;
			int ac;
			if (!bl5 && !bl3) {
				ab = f;
				ac = i;
			} else {
				mutable.set(blockPos, neighborData.faces[1]).move(neighborData.faces[3]);
				BlockState blockState12 = world.getBlockState(mutable);
				ab = brightnessCache.getFloat(blockState12, world, mutable);
				ac = brightnessCache.getInt(blockState12, world, mutable);
			}

			int ad = brightnessCache.getInt(state, world, pos);
			mutable.set(pos, direction);
			BlockState blockState13 = world.getBlockState(mutable);
			if (flags.get(0) || !blockState13.isOpaqueFullCube(world, mutable)) {
				ad = brightnessCache.getInt(blockState13, world, mutable);
			}

			float ae = flags.get(0)
				? brightnessCache.getFloat(world.getBlockState(blockPos), world, blockPos)
				: brightnessCache.getFloat(world.getBlockState(pos), world, pos);
			BlockModelRenderer.Translation translation = BlockModelRenderer.Translation.getTranslations(direction);
			if (flags.get(1) && neighborData.nonCubicWeight) {
				float aj = (m + f + t + ae) * 0.25F;
				float ak = (h + f + p + ae) * 0.25F;
				float al = (h + g + x + ae) * 0.25F;
				float am = (m + g + ab + ae) * 0.25F;
				float an = box[neighborData.field_4192[0].shape] * box[neighborData.field_4192[1].shape];
				float ao = box[neighborData.field_4192[2].shape] * box[neighborData.field_4192[3].shape];
				float ap = box[neighborData.field_4192[4].shape] * box[neighborData.field_4192[5].shape];
				float aq = box[neighborData.field_4192[6].shape] * box[neighborData.field_4192[7].shape];
				float ar = box[neighborData.field_4185[0].shape] * box[neighborData.field_4185[1].shape];
				float as = box[neighborData.field_4185[2].shape] * box[neighborData.field_4185[3].shape];
				float at = box[neighborData.field_4185[4].shape] * box[neighborData.field_4185[5].shape];
				float au = box[neighborData.field_4185[6].shape] * box[neighborData.field_4185[7].shape];
				float av = box[neighborData.field_4180[0].shape] * box[neighborData.field_4180[1].shape];
				float aw = box[neighborData.field_4180[2].shape] * box[neighborData.field_4180[3].shape];
				float ax = box[neighborData.field_4180[4].shape] * box[neighborData.field_4180[5].shape];
				float ay = box[neighborData.field_4180[6].shape] * box[neighborData.field_4180[7].shape];
				float az = box[neighborData.field_4188[0].shape] * box[neighborData.field_4188[1].shape];
				float ba = box[neighborData.field_4188[2].shape] * box[neighborData.field_4188[3].shape];
				float bb = box[neighborData.field_4188[4].shape] * box[neighborData.field_4188[5].shape];
				float bc = box[neighborData.field_4188[6].shape] * box[neighborData.field_4188[7].shape];
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

			float bh = world.getBrightness(direction, bl);

			for (int bi = 0; bi < this.brightness.length; bi++) {
				this.brightness[bi] = this.brightness[bi] * bh;
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
		private final Long2IntLinkedOpenHashMap intCache = Util.make(() -> {
			Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = new Long2IntLinkedOpenHashMap(100, 0.25F) {
				protected void rehash(int i) {
				}
			};
			long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
			return long2IntLinkedOpenHashMap;
		});
		private final Long2FloatLinkedOpenHashMap floatCache = Util.make(() -> {
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

		public int getInt(BlockState state, BlockRenderView blockRenderView, BlockPos pos) {
			long l = pos.asLong();
			if (this.enabled) {
				int i = this.intCache.get(l);
				if (i != Integer.MAX_VALUE) {
					return i;
				}
			}

			int j = WorldRenderer.getLightmapCoordinates(blockRenderView, state, pos);
			if (this.enabled) {
				if (this.intCache.size() == 100) {
					this.intCache.removeFirstInt();
				}

				this.intCache.put(l, j);
			}

			return j;
		}

		public float getFloat(BlockState state, BlockRenderView blockView, BlockPos pos) {
			long l = pos.asLong();
			if (this.enabled) {
				float f = this.floatCache.get(l);
				if (!Float.isNaN(f)) {
					return f;
				}
			}

			float g = state.getAmbientOcclusionLightLevel(blockView, pos);
			if (this.enabled) {
				if (this.floatCache.size() == 100) {
					this.floatCache.removeFirstFloat();
				}

				this.floatCache.put(l, g);
			}

			return g;
		}
	}

	protected static enum NeighborData {
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

		final Direction[] faces;
		final boolean nonCubicWeight;
		final BlockModelRenderer.NeighborOrientation[] field_4192;
		final BlockModelRenderer.NeighborOrientation[] field_4185;
		final BlockModelRenderer.NeighborOrientation[] field_4180;
		final BlockModelRenderer.NeighborOrientation[] field_4188;
		private static final BlockModelRenderer.NeighborData[] field_4190 = Util.make(new BlockModelRenderer.NeighborData[6], neighborDatas -> {
			neighborDatas[Direction.DOWN.getId()] = DOWN;
			neighborDatas[Direction.UP.getId()] = UP;
			neighborDatas[Direction.NORTH.getId()] = NORTH;
			neighborDatas[Direction.SOUTH.getId()] = SOUTH;
			neighborDatas[Direction.WEST.getId()] = WEST;
			neighborDatas[Direction.EAST.getId()] = EAST;
		});

		private NeighborData(
			Direction[] faces,
			float f,
			boolean nonCubicWeight,
			BlockModelRenderer.NeighborOrientation[] neighborOrientations,
			BlockModelRenderer.NeighborOrientation[] neighborOrientations2,
			BlockModelRenderer.NeighborOrientation[] neighborOrientations3,
			BlockModelRenderer.NeighborOrientation[] neighborOrientations4
		) {
			this.faces = faces;
			this.nonCubicWeight = nonCubicWeight;
			this.field_4192 = neighborOrientations;
			this.field_4185 = neighborOrientations2;
			this.field_4180 = neighborOrientations3;
			this.field_4188 = neighborOrientations4;
		}

		public static BlockModelRenderer.NeighborData getData(Direction direction) {
			return field_4190[direction.getId()];
		}
	}

	protected static enum NeighborOrientation {
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

		final int shape;

		private NeighborOrientation(Direction direction, boolean bl) {
			this.shape = direction.getId() + (bl ? BlockModelRenderer.DIRECTIONS.length : 0);
		}
	}

	static enum Translation {
		DOWN(0, 1, 2, 3),
		UP(2, 3, 0, 1),
		NORTH(3, 0, 1, 2),
		SOUTH(0, 1, 2, 3),
		WEST(3, 0, 1, 2),
		EAST(1, 2, 3, 0);

		final int firstCorner;
		final int secondCorner;
		final int thirdCorner;
		final int fourthCorner;
		private static final BlockModelRenderer.Translation[] VALUES = Util.make(new BlockModelRenderer.Translation[6], translations -> {
			translations[Direction.DOWN.getId()] = DOWN;
			translations[Direction.UP.getId()] = UP;
			translations[Direction.NORTH.getId()] = NORTH;
			translations[Direction.SOUTH.getId()] = SOUTH;
			translations[Direction.WEST.getId()] = WEST;
			translations[Direction.EAST.getId()] = EAST;
		});

		private Translation(int firstCorner, int secondCorner, int thirdCorner, int fourthCorner) {
			this.firstCorner = firstCorner;
			this.secondCorner = secondCorner;
			this.thirdCorner = thirdCorner;
			this.fourthCorner = fourthCorner;
		}

		public static BlockModelRenderer.Translation getTranslations(Direction direction) {
			return VALUES[direction.getId()];
		}
	}
}
