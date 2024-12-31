package net.minecraft.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.ExtendedBlockView;

public class FluidRenderer {
	private final Sprite[] lavaSprites = new Sprite[2];
	private final Sprite[] waterSprites = new Sprite[2];
	private Sprite waterOverlaySprite;

	protected void onResourceReload() {
		SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlas();
		this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockStateMaps().getModel(Blocks.field_10164.getDefaultState()).getSprite();
		this.lavaSprites[1] = spriteAtlasTexture.getSprite(ModelLoader.LAVA_FLOW);
		this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockStateMaps().getModel(Blocks.field_10382.getDefaultState()).getSprite();
		this.waterSprites[1] = spriteAtlasTexture.getSprite(ModelLoader.WATER_FLOW);
		this.waterOverlaySprite = spriteAtlasTexture.getSprite(ModelLoader.WATER_OVERLAY);
	}

	private static boolean isSameFluid(BlockView blockView, BlockPos blockPos, Direction direction, FluidState fluidState) {
		BlockPos blockPos2 = blockPos.offset(direction);
		FluidState fluidState2 = blockView.getFluidState(blockPos2);
		return fluidState2.getFluid().matchesType(fluidState.getFluid());
	}

	private static boolean method_3344(BlockView blockView, BlockPos blockPos, Direction direction, float f) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState = blockView.getBlockState(blockPos2);
		if (blockState.isOpaque()) {
			VoxelShape voxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)f, 1.0);
			VoxelShape voxelShape2 = blockState.method_11615(blockView, blockPos2);
			return VoxelShapes.method_1083(voxelShape, voxelShape2, direction);
		} else {
			return false;
		}
	}

	public boolean tesselate(ExtendedBlockView extendedBlockView, BlockPos blockPos, BufferBuilder bufferBuilder, FluidState fluidState) {
		boolean bl = fluidState.matches(FluidTags.field_15518);
		Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
		int i = bl ? 16777215 : BiomeColors.getWaterColor(extendedBlockView, blockPos);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		boolean bl2 = !isSameFluid(extendedBlockView, blockPos, Direction.field_11036, fluidState);
		boolean bl3 = !isSameFluid(extendedBlockView, blockPos, Direction.field_11033, fluidState)
			&& !method_3344(extendedBlockView, blockPos, Direction.field_11033, 0.8888889F);
		boolean bl4 = !isSameFluid(extendedBlockView, blockPos, Direction.field_11043, fluidState);
		boolean bl5 = !isSameFluid(extendedBlockView, blockPos, Direction.field_11035, fluidState);
		boolean bl6 = !isSameFluid(extendedBlockView, blockPos, Direction.field_11039, fluidState);
		boolean bl7 = !isSameFluid(extendedBlockView, blockPos, Direction.field_11034, fluidState);
		if (!bl2 && !bl3 && !bl7 && !bl6 && !bl4 && !bl5) {
			return false;
		} else {
			boolean bl8 = false;
			float j = 0.5F;
			float k = 1.0F;
			float l = 0.8F;
			float m = 0.6F;
			float n = this.getNorthWestCornerFluidHeight(extendedBlockView, blockPos, fluidState.getFluid());
			float o = this.getNorthWestCornerFluidHeight(extendedBlockView, blockPos.south(), fluidState.getFluid());
			float p = this.getNorthWestCornerFluidHeight(extendedBlockView, blockPos.east().south(), fluidState.getFluid());
			float q = this.getNorthWestCornerFluidHeight(extendedBlockView, blockPos.east(), fluidState.getFluid());
			double d = (double)blockPos.getX();
			double e = (double)blockPos.getY();
			double r = (double)blockPos.getZ();
			float s = 0.001F;
			if (bl2 && !method_3344(extendedBlockView, blockPos, Direction.field_11036, Math.min(Math.min(n, o), Math.min(p, q)))) {
				bl8 = true;
				n -= 0.001F;
				o -= 0.001F;
				p -= 0.001F;
				q -= 0.001F;
				Vec3d vec3d = fluidState.getVelocity(extendedBlockView, blockPos);
				float t;
				float v;
				float x;
				float z;
				float u;
				float w;
				float y;
				float aa;
				if (vec3d.x == 0.0 && vec3d.z == 0.0) {
					Sprite sprite = sprites[0];
					t = sprite.getU(0.0);
					u = sprite.getV(0.0);
					v = t;
					w = sprite.getV(16.0);
					x = sprite.getU(16.0);
					y = w;
					z = x;
					aa = u;
				} else {
					Sprite sprite2 = sprites[1];
					float ab = (float)MathHelper.atan2(vec3d.z, vec3d.x) - (float) (Math.PI / 2);
					float ac = MathHelper.sin(ab) * 0.25F;
					float ad = MathHelper.cos(ab) * 0.25F;
					float ae = 8.0F;
					t = sprite2.getU((double)(8.0F + (-ad - ac) * 16.0F));
					u = sprite2.getV((double)(8.0F + (-ad + ac) * 16.0F));
					v = sprite2.getU((double)(8.0F + (-ad + ac) * 16.0F));
					w = sprite2.getV((double)(8.0F + (ad + ac) * 16.0F));
					x = sprite2.getU((double)(8.0F + (ad + ac) * 16.0F));
					y = sprite2.getV((double)(8.0F + (ad - ac) * 16.0F));
					z = sprite2.getU((double)(8.0F + (ad - ac) * 16.0F));
					aa = sprite2.getV((double)(8.0F + (-ad - ac) * 16.0F));
				}

				float an = (t + v + x + z) / 4.0F;
				float ao = (u + w + y + aa) / 4.0F;
				float ap = (float)sprites[0].getWidth() / (sprites[0].getMaxU() - sprites[0].getMinU());
				float aq = (float)sprites[0].getHeight() / (sprites[0].getMaxV() - sprites[0].getMinV());
				float ar = 4.0F / Math.max(aq, ap);
				t = MathHelper.lerp(ar, t, an);
				v = MathHelper.lerp(ar, v, an);
				x = MathHelper.lerp(ar, x, an);
				z = MathHelper.lerp(ar, z, an);
				u = MathHelper.lerp(ar, u, ao);
				w = MathHelper.lerp(ar, w, ao);
				y = MathHelper.lerp(ar, y, ao);
				aa = MathHelper.lerp(ar, aa, ao);
				int as = this.method_3343(extendedBlockView, blockPos);
				int at = as >> 16 & 65535;
				int au = as & 65535;
				float av = 1.0F * f;
				float aw = 1.0F * g;
				float ax = 1.0F * h;
				bufferBuilder.vertex(d + 0.0, e + (double)n, r + 0.0).color(av, aw, ax, 1.0F).texture((double)t, (double)u).texture(at, au).next();
				bufferBuilder.vertex(d + 0.0, e + (double)o, r + 1.0).color(av, aw, ax, 1.0F).texture((double)v, (double)w).texture(at, au).next();
				bufferBuilder.vertex(d + 1.0, e + (double)p, r + 1.0).color(av, aw, ax, 1.0F).texture((double)x, (double)y).texture(at, au).next();
				bufferBuilder.vertex(d + 1.0, e + (double)q, r + 0.0).color(av, aw, ax, 1.0F).texture((double)z, (double)aa).texture(at, au).next();
				if (fluidState.method_15756(extendedBlockView, blockPos.up())) {
					bufferBuilder.vertex(d + 0.0, e + (double)n, r + 0.0).color(av, aw, ax, 1.0F).texture((double)t, (double)u).texture(at, au).next();
					bufferBuilder.vertex(d + 1.0, e + (double)q, r + 0.0).color(av, aw, ax, 1.0F).texture((double)z, (double)aa).texture(at, au).next();
					bufferBuilder.vertex(d + 1.0, e + (double)p, r + 1.0).color(av, aw, ax, 1.0F).texture((double)x, (double)y).texture(at, au).next();
					bufferBuilder.vertex(d + 0.0, e + (double)o, r + 1.0).color(av, aw, ax, 1.0F).texture((double)v, (double)w).texture(at, au).next();
				}
			}

			if (bl3) {
				float ay = sprites[0].getMinU();
				float az = sprites[0].getMaxU();
				float ba = sprites[0].getMinV();
				float bb = sprites[0].getMaxV();
				int bc = this.method_3343(extendedBlockView, blockPos.down());
				int bd = bc >> 16 & 65535;
				int be = bc & 65535;
				float bf = 0.5F * f;
				float bg = 0.5F * g;
				float bh = 0.5F * h;
				bufferBuilder.vertex(d, e, r + 1.0).color(bf, bg, bh, 1.0F).texture((double)ay, (double)bb).texture(bd, be).next();
				bufferBuilder.vertex(d, e, r).color(bf, bg, bh, 1.0F).texture((double)ay, (double)ba).texture(bd, be).next();
				bufferBuilder.vertex(d + 1.0, e, r).color(bf, bg, bh, 1.0F).texture((double)az, (double)ba).texture(bd, be).next();
				bufferBuilder.vertex(d + 1.0, e, r + 1.0).color(bf, bg, bh, 1.0F).texture((double)az, (double)bb).texture(bd, be).next();
				bl8 = true;
			}

			for (int bi = 0; bi < 4; bi++) {
				float bj;
				float bk;
				double bm;
				double bo;
				double bn;
				double bp;
				Direction direction;
				boolean bl9;
				if (bi == 0) {
					bj = n;
					bk = q;
					bm = d;
					bn = d + 1.0;
					bo = r + 0.001F;
					bp = r + 0.001F;
					direction = Direction.field_11043;
					bl9 = bl4;
				} else if (bi == 1) {
					bj = p;
					bk = o;
					bm = d + 1.0;
					bn = d;
					bo = r + 1.0 - 0.001F;
					bp = r + 1.0 - 0.001F;
					direction = Direction.field_11035;
					bl9 = bl5;
				} else if (bi == 2) {
					bj = o;
					bk = n;
					bm = d + 0.001F;
					bn = d + 0.001F;
					bo = r + 1.0;
					bp = r;
					direction = Direction.field_11039;
					bl9 = bl6;
				} else {
					bj = q;
					bk = p;
					bm = d + 1.0 - 0.001F;
					bn = d + 1.0 - 0.001F;
					bo = r;
					bp = r + 1.0;
					direction = Direction.field_11034;
					bl9 = bl7;
				}

				if (bl9 && !method_3344(extendedBlockView, blockPos, direction, Math.max(bj, bk))) {
					bl8 = true;
					BlockPos blockPos2 = blockPos.offset(direction);
					Sprite sprite3 = sprites[1];
					if (!bl) {
						Block block = extendedBlockView.getBlockState(blockPos2).getBlock();
						if (block == Blocks.field_10033 || block instanceof StainedGlassBlock) {
							sprite3 = this.waterOverlaySprite;
						}
					}

					float ci = sprite3.getU(0.0);
					float cj = sprite3.getU(8.0);
					float ck = sprite3.getV((double)((1.0F - bj) * 16.0F * 0.5F));
					float cl = sprite3.getV((double)((1.0F - bk) * 16.0F * 0.5F));
					float cm = sprite3.getV(8.0);
					int cn = this.method_3343(extendedBlockView, blockPos2);
					int co = cn >> 16 & 65535;
					int cp = cn & 65535;
					float cq = bi < 2 ? 0.8F : 0.6F;
					float cr = 1.0F * cq * f;
					float cs = 1.0F * cq * g;
					float ct = 1.0F * cq * h;
					bufferBuilder.vertex(bm, e + (double)bj, bo).color(cr, cs, ct, 1.0F).texture((double)ci, (double)ck).texture(co, cp).next();
					bufferBuilder.vertex(bn, e + (double)bk, bp).color(cr, cs, ct, 1.0F).texture((double)cj, (double)cl).texture(co, cp).next();
					bufferBuilder.vertex(bn, e + 0.0, bp).color(cr, cs, ct, 1.0F).texture((double)cj, (double)cm).texture(co, cp).next();
					bufferBuilder.vertex(bm, e + 0.0, bo).color(cr, cs, ct, 1.0F).texture((double)ci, (double)cm).texture(co, cp).next();
					if (sprite3 != this.waterOverlaySprite) {
						bufferBuilder.vertex(bm, e + 0.0, bo).color(cr, cs, ct, 1.0F).texture((double)ci, (double)cm).texture(co, cp).next();
						bufferBuilder.vertex(bn, e + 0.0, bp).color(cr, cs, ct, 1.0F).texture((double)cj, (double)cm).texture(co, cp).next();
						bufferBuilder.vertex(bn, e + (double)bk, bp).color(cr, cs, ct, 1.0F).texture((double)cj, (double)cl).texture(co, cp).next();
						bufferBuilder.vertex(bm, e + (double)bj, bo).color(cr, cs, ct, 1.0F).texture((double)ci, (double)ck).texture(co, cp).next();
					}
				}
			}

			return bl8;
		}
	}

	private int method_3343(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
		int i = extendedBlockView.getLightmapIndex(blockPos, 0);
		int j = extendedBlockView.getLightmapIndex(blockPos.up(), 0);
		int k = i & 0xFF;
		int l = j & 0xFF;
		int m = i >> 16 & 0xFF;
		int n = j >> 16 & 0xFF;
		return (k > l ? k : l) | (m > n ? m : n) << 16;
	}

	private float getNorthWestCornerFluidHeight(BlockView blockView, BlockPos blockPos, Fluid fluid) {
		int i = 0;
		float f = 0.0F;

		for (int j = 0; j < 4; j++) {
			BlockPos blockPos2 = blockPos.add(-(j & 1), 0, -(j >> 1 & 1));
			if (blockView.getFluidState(blockPos2.up()).getFluid().matchesType(fluid)) {
				return 1.0F;
			}

			FluidState fluidState = blockView.getFluidState(blockPos2);
			if (fluidState.getFluid().matchesType(fluid)) {
				float g = fluidState.getHeight(blockView, blockPos2);
				if (g >= 0.8F) {
					f += g * 10.0F;
					i += 10;
				} else {
					f += g;
					i++;
				}
			} else if (!blockView.getBlockState(blockPos2).getMaterial().isSolid()) {
				i++;
			}
		}

		return f / (float)i;
	}
}
