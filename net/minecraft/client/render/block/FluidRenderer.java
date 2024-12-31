package net.minecraft.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

public class FluidRenderer {
	private final Sprite[] lavaSprites = new Sprite[2];
	private final Sprite[] waterSprites = new Sprite[2];
	private Sprite waterOverlaySprite;

	protected void onResourceReload() {
		this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.field_10164.getDefaultState()).getSprite();
		this.lavaSprites[1] = ModelLoader.LAVA_FLOW.getSprite();
		this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.field_10382.getDefaultState()).getSprite();
		this.waterSprites[1] = ModelLoader.WATER_FLOW.getSprite();
		this.waterOverlaySprite = ModelLoader.WATER_OVERLAY.getSprite();
	}

	private static boolean isSameFluid(BlockView blockView, BlockPos blockPos, Direction direction, FluidState fluidState) {
		BlockPos blockPos2 = blockPos.offset(direction);
		FluidState fluidState2 = blockView.getFluidState(blockPos2);
		return fluidState2.getFluid().matchesType(fluidState.getFluid());
	}

	private static boolean isSideCovered(BlockView blockView, BlockPos blockPos, Direction direction, float f) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState = blockView.getBlockState(blockPos2);
		if (blockState.isOpaque()) {
			VoxelShape voxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)f, 1.0);
			VoxelShape voxelShape2 = blockState.getCullingShape(blockView, blockPos2);
			return VoxelShapes.isSideCovered(voxelShape, voxelShape2, direction);
		} else {
			return false;
		}
	}

	public boolean render(BlockRenderView blockRenderView, BlockPos blockPos, VertexConsumer vertexConsumer, FluidState fluidState) {
		boolean bl = fluidState.matches(FluidTags.field_15518);
		Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
		int i = bl ? 16777215 : BiomeColors.getWaterColor(blockRenderView, blockPos);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		boolean bl2 = !isSameFluid(blockRenderView, blockPos, Direction.field_11036, fluidState);
		boolean bl3 = !isSameFluid(blockRenderView, blockPos, Direction.field_11033, fluidState)
			&& !isSideCovered(blockRenderView, blockPos, Direction.field_11033, 0.8888889F);
		boolean bl4 = !isSameFluid(blockRenderView, blockPos, Direction.field_11043, fluidState);
		boolean bl5 = !isSameFluid(blockRenderView, blockPos, Direction.field_11035, fluidState);
		boolean bl6 = !isSameFluid(blockRenderView, blockPos, Direction.field_11039, fluidState);
		boolean bl7 = !isSameFluid(blockRenderView, blockPos, Direction.field_11034, fluidState);
		if (!bl2 && !bl3 && !bl7 && !bl6 && !bl4 && !bl5) {
			return false;
		} else {
			boolean bl8 = false;
			float j = 0.5F;
			float k = 1.0F;
			float l = 0.8F;
			float m = 0.6F;
			float n = this.getNorthWestCornerFluidHeight(blockRenderView, blockPos, fluidState.getFluid());
			float o = this.getNorthWestCornerFluidHeight(blockRenderView, blockPos.south(), fluidState.getFluid());
			float p = this.getNorthWestCornerFluidHeight(blockRenderView, blockPos.east().south(), fluidState.getFluid());
			float q = this.getNorthWestCornerFluidHeight(blockRenderView, blockPos.east(), fluidState.getFluid());
			double d = (double)(blockPos.getX() & 15);
			double e = (double)(blockPos.getY() & 15);
			double r = (double)(blockPos.getZ() & 15);
			float s = 0.001F;
			float t = bl3 ? 0.001F : 0.0F;
			if (bl2 && !isSideCovered(blockRenderView, blockPos, Direction.field_11036, Math.min(Math.min(n, o), Math.min(p, q)))) {
				bl8 = true;
				n -= 0.001F;
				o -= 0.001F;
				p -= 0.001F;
				q -= 0.001F;
				Vec3d vec3d = fluidState.getVelocity(blockRenderView, blockPos);
				float u;
				float w;
				float y;
				float aa;
				float v;
				float x;
				float z;
				float ab;
				if (vec3d.x == 0.0 && vec3d.z == 0.0) {
					Sprite sprite = sprites[0];
					u = sprite.getFrameU(0.0);
					v = sprite.getFrameV(0.0);
					w = u;
					x = sprite.getFrameV(16.0);
					y = sprite.getFrameU(16.0);
					z = x;
					aa = y;
					ab = v;
				} else {
					Sprite sprite2 = sprites[1];
					float ac = (float)MathHelper.atan2(vec3d.z, vec3d.x) - (float) (Math.PI / 2);
					float ad = MathHelper.sin(ac) * 0.25F;
					float ae = MathHelper.cos(ac) * 0.25F;
					float af = 8.0F;
					u = sprite2.getFrameU((double)(8.0F + (-ae - ad) * 16.0F));
					v = sprite2.getFrameV((double)(8.0F + (-ae + ad) * 16.0F));
					w = sprite2.getFrameU((double)(8.0F + (-ae + ad) * 16.0F));
					x = sprite2.getFrameV((double)(8.0F + (ae + ad) * 16.0F));
					y = sprite2.getFrameU((double)(8.0F + (ae + ad) * 16.0F));
					z = sprite2.getFrameV((double)(8.0F + (ae - ad) * 16.0F));
					aa = sprite2.getFrameU((double)(8.0F + (ae - ad) * 16.0F));
					ab = sprite2.getFrameV((double)(8.0F + (-ae - ad) * 16.0F));
				}

				float ao = (u + w + y + aa) / 4.0F;
				float ap = (v + x + z + ab) / 4.0F;
				float aq = (float)sprites[0].getWidth() / (sprites[0].getMaxU() - sprites[0].getMinU());
				float ar = (float)sprites[0].getHeight() / (sprites[0].getMaxV() - sprites[0].getMinV());
				float as = 4.0F / Math.max(ar, aq);
				u = MathHelper.lerp(as, u, ao);
				w = MathHelper.lerp(as, w, ao);
				y = MathHelper.lerp(as, y, ao);
				aa = MathHelper.lerp(as, aa, ao);
				v = MathHelper.lerp(as, v, ap);
				x = MathHelper.lerp(as, x, ap);
				z = MathHelper.lerp(as, z, ap);
				ab = MathHelper.lerp(as, ab, ap);
				int at = this.getLight(blockRenderView, blockPos);
				float au = 1.0F * f;
				float av = 1.0F * g;
				float aw = 1.0F * h;
				this.vertex(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, au, av, aw, u, v, at);
				this.vertex(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, au, av, aw, w, x, at);
				this.vertex(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, au, av, aw, y, z, at);
				this.vertex(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, au, av, aw, aa, ab, at);
				if (fluidState.method_15756(blockRenderView, blockPos.up())) {
					this.vertex(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, au, av, aw, u, v, at);
					this.vertex(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, au, av, aw, aa, ab, at);
					this.vertex(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, au, av, aw, y, z, at);
					this.vertex(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, au, av, aw, w, x, at);
				}
			}

			if (bl3) {
				float ax = sprites[0].getMinU();
				float ay = sprites[0].getMaxU();
				float az = sprites[0].getMinV();
				float ba = sprites[0].getMaxV();
				int bb = this.getLight(blockRenderView, blockPos.down());
				float bc = 0.5F * f;
				float bd = 0.5F * g;
				float be = 0.5F * h;
				this.vertex(vertexConsumer, d, e + (double)t, r + 1.0, bc, bd, be, ax, ba, bb);
				this.vertex(vertexConsumer, d, e + (double)t, r, bc, bd, be, ax, az, bb);
				this.vertex(vertexConsumer, d + 1.0, e + (double)t, r, bc, bd, be, ay, az, bb);
				this.vertex(vertexConsumer, d + 1.0, e + (double)t, r + 1.0, bc, bd, be, ay, ba, bb);
				bl8 = true;
			}

			for (int bf = 0; bf < 4; bf++) {
				float bg;
				float bh;
				double bi;
				double bk;
				double bj;
				double bm;
				Direction direction;
				boolean bl9;
				if (bf == 0) {
					bg = n;
					bh = q;
					bi = d;
					bj = d + 1.0;
					bk = r + 0.001F;
					bm = r + 0.001F;
					direction = Direction.field_11043;
					bl9 = bl4;
				} else if (bf == 1) {
					bg = p;
					bh = o;
					bi = d + 1.0;
					bj = d;
					bk = r + 1.0 - 0.001F;
					bm = r + 1.0 - 0.001F;
					direction = Direction.field_11035;
					bl9 = bl5;
				} else if (bf == 2) {
					bg = o;
					bh = n;
					bi = d + 0.001F;
					bj = d + 0.001F;
					bk = r + 1.0;
					bm = r;
					direction = Direction.field_11039;
					bl9 = bl6;
				} else {
					bg = q;
					bh = p;
					bi = d + 1.0 - 0.001F;
					bj = d + 1.0 - 0.001F;
					bk = r;
					bm = r + 1.0;
					direction = Direction.field_11034;
					bl9 = bl7;
				}

				if (bl9 && !isSideCovered(blockRenderView, blockPos, direction, Math.max(bg, bh))) {
					bl8 = true;
					BlockPos blockPos2 = blockPos.offset(direction);
					Sprite sprite3 = sprites[1];
					if (!bl) {
						Block block = blockRenderView.getBlockState(blockPos2).getBlock();
						if (block == Blocks.field_10033 || block instanceof StainedGlassBlock) {
							sprite3 = this.waterOverlaySprite;
						}
					}

					float cf = sprite3.getFrameU(0.0);
					float cg = sprite3.getFrameU(8.0);
					float ch = sprite3.getFrameV((double)((1.0F - bg) * 16.0F * 0.5F));
					float ci = sprite3.getFrameV((double)((1.0F - bh) * 16.0F * 0.5F));
					float cj = sprite3.getFrameV(8.0);
					int ck = this.getLight(blockRenderView, blockPos2);
					float cl = bf < 2 ? 0.8F : 0.6F;
					float cm = 1.0F * cl * f;
					float cn = 1.0F * cl * g;
					float co = 1.0F * cl * h;
					this.vertex(vertexConsumer, bi, e + (double)bg, bk, cm, cn, co, cf, ch, ck);
					this.vertex(vertexConsumer, bj, e + (double)bh, bm, cm, cn, co, cg, ci, ck);
					this.vertex(vertexConsumer, bj, e + (double)t, bm, cm, cn, co, cg, cj, ck);
					this.vertex(vertexConsumer, bi, e + (double)t, bk, cm, cn, co, cf, cj, ck);
					if (sprite3 != this.waterOverlaySprite) {
						this.vertex(vertexConsumer, bi, e + (double)t, bk, cm, cn, co, cf, cj, ck);
						this.vertex(vertexConsumer, bj, e + (double)t, bm, cm, cn, co, cg, cj, ck);
						this.vertex(vertexConsumer, bj, e + (double)bh, bm, cm, cn, co, cg, ci, ck);
						this.vertex(vertexConsumer, bi, e + (double)bg, bk, cm, cn, co, cf, ch, ck);
					}
				}
			}

			return bl8;
		}
	}

	private void vertex(VertexConsumer vertexConsumer, double d, double e, double f, float g, float h, float i, float j, float k, int l) {
		vertexConsumer.vertex(d, e, f).color(g, h, i, 1.0F).texture(j, k).light(l).normal(0.0F, 1.0F, 0.0F).next();
	}

	private int getLight(BlockRenderView blockRenderView, BlockPos blockPos) {
		int i = WorldRenderer.getLightmapCoordinates(blockRenderView, blockPos);
		int j = WorldRenderer.getLightmapCoordinates(blockRenderView, blockPos.up());
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
