package net.minecraft.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TransparentBlock;
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
	private static final float field_32781 = 0.8888889F;
	private final Sprite[] lavaSprites = new Sprite[2];
	private final Sprite[] waterSprites = new Sprite[2];
	private Sprite waterOverlaySprite;

	protected void onResourceReload() {
		this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.LAVA.getDefaultState()).getSprite();
		this.lavaSprites[1] = ModelLoader.LAVA_FLOW.getSprite();
		this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).getSprite();
		this.waterSprites[1] = ModelLoader.WATER_FLOW.getSprite();
		this.waterOverlaySprite = ModelLoader.WATER_OVERLAY.getSprite();
	}

	private static boolean isSameFluid(BlockView world, BlockPos pos, Direction side, FluidState state) {
		BlockPos blockPos = pos.offset(side);
		FluidState fluidState = world.getFluidState(blockPos);
		return fluidState.getFluid().matchesType(state.getFluid());
	}

	private static boolean isSideCovered(BlockView world, Direction direction, float f, BlockPos pos, BlockState state) {
		if (state.isOpaque()) {
			VoxelShape voxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)f, 1.0);
			VoxelShape voxelShape2 = state.getCullingShape(world, pos);
			return VoxelShapes.isSideCovered(voxelShape, voxelShape2, direction);
		} else {
			return false;
		}
	}

	private static boolean isSideCovered(BlockView world, BlockPos pos, Direction direction, float maxDeviation) {
		BlockPos blockPos = pos.offset(direction);
		BlockState blockState = world.getBlockState(blockPos);
		return isSideCovered(world, direction, maxDeviation, blockPos, blockState);
	}

	private static boolean isOppositeSideCovered(BlockView world, BlockPos pos, BlockState state, Direction direction) {
		return isSideCovered(world, direction.getOpposite(), 1.0F, pos, state);
	}

	public static boolean method_29708(BlockRenderView blockRenderView, BlockPos blockPos, FluidState fluidState, BlockState blockState, Direction direction) {
		return !isOppositeSideCovered(blockRenderView, blockPos, blockState, direction) && !isSameFluid(blockRenderView, blockPos, direction, fluidState);
	}

	public boolean render(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, FluidState state) {
		boolean bl = state.isIn(FluidTags.LAVA);
		Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
		BlockState blockState = world.getBlockState(pos);
		int i = bl ? 16777215 : BiomeColors.getWaterColor(world, pos);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		boolean bl2 = !isSameFluid(world, pos, Direction.UP, state);
		boolean bl3 = method_29708(world, pos, state, blockState, Direction.DOWN) && !isSideCovered(world, pos, Direction.DOWN, 0.8888889F);
		boolean bl4 = method_29708(world, pos, state, blockState, Direction.NORTH);
		boolean bl5 = method_29708(world, pos, state, blockState, Direction.SOUTH);
		boolean bl6 = method_29708(world, pos, state, blockState, Direction.WEST);
		boolean bl7 = method_29708(world, pos, state, blockState, Direction.EAST);
		if (!bl2 && !bl3 && !bl7 && !bl6 && !bl4 && !bl5) {
			return false;
		} else {
			boolean bl8 = false;
			float j = world.getBrightness(Direction.DOWN, true);
			float k = world.getBrightness(Direction.UP, true);
			float l = world.getBrightness(Direction.NORTH, true);
			float m = world.getBrightness(Direction.WEST, true);
			float n = this.getNorthWestCornerFluidHeight(world, pos, state.getFluid());
			float o = this.getNorthWestCornerFluidHeight(world, pos.south(), state.getFluid());
			float p = this.getNorthWestCornerFluidHeight(world, pos.east().south(), state.getFluid());
			float q = this.getNorthWestCornerFluidHeight(world, pos.east(), state.getFluid());
			double d = (double)(pos.getX() & 15);
			double e = (double)(pos.getY() & 15);
			double r = (double)(pos.getZ() & 15);
			float s = 0.001F;
			float t = bl3 ? 0.001F : 0.0F;
			if (bl2 && !isSideCovered(world, pos, Direction.UP, Math.min(Math.min(n, o), Math.min(p, q)))) {
				bl8 = true;
				n -= 0.001F;
				o -= 0.001F;
				p -= 0.001F;
				q -= 0.001F;
				Vec3d vec3d = state.getVelocity(world, pos);
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
				int at = this.getLight(world, pos);
				float au = k * f;
				float av = k * g;
				float aw = k * h;
				this.vertex(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, au, av, aw, u, v, at);
				this.vertex(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, au, av, aw, w, x, at);
				this.vertex(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, au, av, aw, y, z, at);
				this.vertex(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, au, av, aw, aa, ab, at);
				if (state.method_15756(world, pos.up())) {
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
				int bb = this.getLight(world, pos.down());
				float bc = j * f;
				float bd = j * g;
				float be = j * h;
				this.vertex(vertexConsumer, d, e + (double)t, r + 1.0, bc, bd, be, ax, ba, bb);
				this.vertex(vertexConsumer, d, e + (double)t, r, bc, bd, be, ax, az, bb);
				this.vertex(vertexConsumer, d + 1.0, e + (double)t, r, bc, bd, be, ay, az, bb);
				this.vertex(vertexConsumer, d + 1.0, e + (double)t, r + 1.0, bc, bd, be, ay, ba, bb);
				bl8 = true;
			}

			int bf = this.getLight(world, pos);

			for (int bg = 0; bg < 4; bg++) {
				float bh;
				float bi;
				double bj;
				double bm;
				double bk;
				double bn;
				Direction direction;
				boolean bl9;
				if (bg == 0) {
					bh = n;
					bi = q;
					bj = d;
					bk = d + 1.0;
					bm = r + 0.001F;
					bn = r + 0.001F;
					direction = Direction.NORTH;
					bl9 = bl4;
				} else if (bg == 1) {
					bh = p;
					bi = o;
					bj = d + 1.0;
					bk = d;
					bm = r + 1.0 - 0.001F;
					bn = r + 1.0 - 0.001F;
					direction = Direction.SOUTH;
					bl9 = bl5;
				} else if (bg == 2) {
					bh = o;
					bi = n;
					bj = d + 0.001F;
					bk = d + 0.001F;
					bm = r + 1.0;
					bn = r;
					direction = Direction.WEST;
					bl9 = bl6;
				} else {
					bh = q;
					bi = p;
					bj = d + 1.0 - 0.001F;
					bk = d + 1.0 - 0.001F;
					bm = r;
					bn = r + 1.0;
					direction = Direction.EAST;
					bl9 = bl7;
				}

				if (bl9 && !isSideCovered(world, pos, direction, Math.max(bh, bi))) {
					bl8 = true;
					BlockPos blockPos = pos.offset(direction);
					Sprite sprite3 = sprites[1];
					if (!bl) {
						Block block = world.getBlockState(blockPos).getBlock();
						if (block instanceof TransparentBlock || block instanceof LeavesBlock) {
							sprite3 = this.waterOverlaySprite;
						}
					}

					float cg = sprite3.getFrameU(0.0);
					float ch = sprite3.getFrameU(8.0);
					float ci = sprite3.getFrameV((double)((1.0F - bh) * 16.0F * 0.5F));
					float cj = sprite3.getFrameV((double)((1.0F - bi) * 16.0F * 0.5F));
					float ck = sprite3.getFrameV(8.0);
					float cl = bg < 2 ? l : m;
					float cm = k * cl * f;
					float cn = k * cl * g;
					float co = k * cl * h;
					this.vertex(vertexConsumer, bj, e + (double)bh, bm, cm, cn, co, cg, ci, bf);
					this.vertex(vertexConsumer, bk, e + (double)bi, bn, cm, cn, co, ch, cj, bf);
					this.vertex(vertexConsumer, bk, e + (double)t, bn, cm, cn, co, ch, ck, bf);
					this.vertex(vertexConsumer, bj, e + (double)t, bm, cm, cn, co, cg, ck, bf);
					if (sprite3 != this.waterOverlaySprite) {
						this.vertex(vertexConsumer, bj, e + (double)t, bm, cm, cn, co, cg, ck, bf);
						this.vertex(vertexConsumer, bk, e + (double)t, bn, cm, cn, co, ch, ck, bf);
						this.vertex(vertexConsumer, bk, e + (double)bi, bn, cm, cn, co, ch, cj, bf);
						this.vertex(vertexConsumer, bj, e + (double)bh, bm, cm, cn, co, cg, ci, bf);
					}
				}
			}

			return bl8;
		}
	}

	private void vertex(VertexConsumer vertexConsumer, double x, double y, double z, float red, float green, float blue, float u, float v, int light) {
		vertexConsumer.vertex(x, y, z).color(red, green, blue, 1.0F).texture(u, v).light(light).normal(0.0F, 1.0F, 0.0F).next();
	}

	private int getLight(BlockRenderView world, BlockPos pos) {
		int i = WorldRenderer.getLightmapCoordinates(world, pos);
		int j = WorldRenderer.getLightmapCoordinates(world, pos.up());
		int k = i & 0xFF;
		int l = j & 0xFF;
		int m = i >> 16 & 0xFF;
		int n = j >> 16 & 0xFF;
		return (k > l ? k : l) | (m > n ? m : n) << 16;
	}

	private float getNorthWestCornerFluidHeight(BlockView world, BlockPos pos, Fluid fluid) {
		int i = 0;
		float f = 0.0F;

		for (int j = 0; j < 4; j++) {
			BlockPos blockPos = pos.add(-(j & 1), 0, -(j >> 1 & 1));
			if (world.getFluidState(blockPos.up()).getFluid().matchesType(fluid)) {
				return 1.0F;
			}

			FluidState fluidState = world.getFluidState(blockPos);
			if (fluidState.getFluid().matchesType(fluid)) {
				float g = fluidState.getHeight(world, blockPos);
				if (g >= 0.8F) {
					f += g * 10.0F;
					i += 10;
				} else {
					f += g;
					i++;
				}
			} else if (!world.getBlockState(blockPos).getMaterial().isSolid()) {
				i++;
			}
		}

		return f / (float)i;
	}
}
