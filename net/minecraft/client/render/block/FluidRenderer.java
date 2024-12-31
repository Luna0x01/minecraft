package net.minecraft.client.render.block;

import net.minecraft.class_3600;
import net.minecraft.class_4288;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RenderBlockView;

public class FluidRenderer {
	private final Sprite[] lavaSprites = new Sprite[2];
	private final Sprite[] waterSprites = new Sprite[2];
	private Sprite field_13550;

	public FluidRenderer() {
		this.onResourceReload();
	}

	protected void onResourceReload() {
		SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlasTexture();
		this.lavaSprites[0] = MinecraftClient.getInstance().method_18222().getModelShapes().getBakedModel(Blocks.LAVA.getDefaultState()).getParticleSprite();
		this.lavaSprites[1] = spriteAtlasTexture.method_19509(class_4288.field_21066);
		this.waterSprites[0] = MinecraftClient.getInstance().method_18222().getModelShapes().getBakedModel(Blocks.WATER.getDefaultState()).getParticleSprite();
		this.waterSprites[1] = spriteAtlasTexture.method_19509(class_4288.field_21067);
		this.field_13550 = spriteAtlasTexture.method_19509(class_4288.field_21068);
	}

	private static boolean method_19191(BlockView blockView, BlockPos blockPos, Direction direction, FluidState fluidState) {
		BlockPos blockPos2 = blockPos.offset(direction);
		FluidState fluidState2 = blockView.getFluidState(blockPos2);
		return fluidState2.getFluid().method_17781(fluidState.getFluid());
	}

	private static boolean method_19190(BlockView blockView, BlockPos blockPos, Direction direction, float f) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState = blockView.getBlockState(blockPos2);
		if (blockState.isFullBoundsCubeForCulling()) {
			VoxelShape voxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)f, 1.0);
			VoxelShape voxelShape2 = blockState.method_16902(blockView, blockPos2);
			return VoxelShapes.method_18056(voxelShape, voxelShape2, direction);
		} else {
			return false;
		}
	}

	public boolean method_19194(class_3600 arg, BlockPos blockPos, BufferBuilder bufferBuilder, FluidState fluidState) {
		boolean bl = fluidState.matches(FluidTags.LAVA);
		Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
		int i = bl ? 16777215 : BiomeColors.method_19686(arg, blockPos);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		boolean bl2 = !method_19191(arg, blockPos, Direction.UP, fluidState);
		boolean bl3 = !method_19191(arg, blockPos, Direction.DOWN, fluidState) && !method_19190(arg, blockPos, Direction.DOWN, 0.8888889F);
		boolean bl4 = !method_19191(arg, blockPos, Direction.NORTH, fluidState);
		boolean bl5 = !method_19191(arg, blockPos, Direction.SOUTH, fluidState);
		boolean bl6 = !method_19191(arg, blockPos, Direction.WEST, fluidState);
		boolean bl7 = !method_19191(arg, blockPos, Direction.EAST, fluidState);
		if (!bl2 && !bl3 && !bl7 && !bl6 && !bl4 && !bl5) {
			return false;
		} else {
			boolean bl8 = false;
			float j = 0.5F;
			float k = 1.0F;
			float l = 0.8F;
			float m = 0.6F;
			float n = this.method_19192(arg, blockPos, fluidState.getFluid());
			float o = this.method_19192(arg, blockPos.south(), fluidState.getFluid());
			float p = this.method_19192(arg, blockPos.east().south(), fluidState.getFluid());
			float q = this.method_19192(arg, blockPos.east(), fluidState.getFluid());
			double d = (double)blockPos.getX();
			double e = (double)blockPos.getY();
			double r = (double)blockPos.getZ();
			float s = 0.001F;
			if (bl2 && !method_19190(arg, blockPos, Direction.UP, Math.min(Math.min(n, o), Math.min(p, q)))) {
				bl8 = true;
				n -= 0.001F;
				o -= 0.001F;
				p -= 0.001F;
				q -= 0.001F;
				Vec3d vec3d = fluidState.method_17803(arg, blockPos);
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
					t = sprite.getFrameU(0.0);
					u = sprite.getFrameV(0.0);
					v = t;
					w = sprite.getFrameV(16.0);
					x = sprite.getFrameU(16.0);
					y = w;
					z = x;
					aa = u;
				} else {
					Sprite sprite2 = sprites[1];
					float ab = (float)MathHelper.atan2(vec3d.z, vec3d.x) - (float) (Math.PI / 2);
					float ac = MathHelper.sin(ab) * 0.25F;
					float ad = MathHelper.cos(ab) * 0.25F;
					float ae = 8.0F;
					t = sprite2.getFrameU((double)(8.0F + (-ad - ac) * 16.0F));
					u = sprite2.getFrameV((double)(8.0F + (-ad + ac) * 16.0F));
					v = sprite2.getFrameU((double)(8.0F + (-ad + ac) * 16.0F));
					w = sprite2.getFrameV((double)(8.0F + (ad + ac) * 16.0F));
					x = sprite2.getFrameU((double)(8.0F + (ad + ac) * 16.0F));
					y = sprite2.getFrameV((double)(8.0F + (ad - ac) * 16.0F));
					z = sprite2.getFrameU((double)(8.0F + (ad - ac) * 16.0F));
					aa = sprite2.getFrameV((double)(8.0F + (-ad - ac) * 16.0F));
				}

				int an = this.method_19193(arg, blockPos);
				int ao = an >> 16 & 65535;
				int ap = an & 65535;
				float aq = 1.0F * f;
				float ar = 1.0F * g;
				float as = 1.0F * h;
				bufferBuilder.vertex(d + 0.0, e + (double)n, r + 0.0).color(aq, ar, as, 1.0F).texture((double)t, (double)u).texture2(ao, ap).next();
				bufferBuilder.vertex(d + 0.0, e + (double)o, r + 1.0).color(aq, ar, as, 1.0F).texture((double)v, (double)w).texture2(ao, ap).next();
				bufferBuilder.vertex(d + 1.0, e + (double)p, r + 1.0).color(aq, ar, as, 1.0F).texture((double)x, (double)y).texture2(ao, ap).next();
				bufferBuilder.vertex(d + 1.0, e + (double)q, r + 0.0).color(aq, ar, as, 1.0F).texture((double)z, (double)aa).texture2(ao, ap).next();
				if (fluidState.method_17800(arg, blockPos.up())) {
					bufferBuilder.vertex(d + 0.0, e + (double)n, r + 0.0).color(aq, ar, as, 1.0F).texture((double)t, (double)u).texture2(ao, ap).next();
					bufferBuilder.vertex(d + 1.0, e + (double)q, r + 0.0).color(aq, ar, as, 1.0F).texture((double)z, (double)aa).texture2(ao, ap).next();
					bufferBuilder.vertex(d + 1.0, e + (double)p, r + 1.0).color(aq, ar, as, 1.0F).texture((double)x, (double)y).texture2(ao, ap).next();
					bufferBuilder.vertex(d + 0.0, e + (double)o, r + 1.0).color(aq, ar, as, 1.0F).texture((double)v, (double)w).texture2(ao, ap).next();
				}
			}

			if (bl3) {
				float at = sprites[0].getMinU();
				float au = sprites[0].getMaxU();
				float av = sprites[0].getMinV();
				float aw = sprites[0].getMaxV();
				int ax = this.method_19193(arg, blockPos.down());
				int ay = ax >> 16 & 65535;
				int az = ax & 65535;
				float ba = 0.5F * f;
				float bb = 0.5F * g;
				float bc = 0.5F * h;
				bufferBuilder.vertex(d, e, r + 1.0).color(ba, bb, bc, 1.0F).texture((double)at, (double)aw).texture2(ay, az).next();
				bufferBuilder.vertex(d, e, r).color(ba, bb, bc, 1.0F).texture((double)at, (double)av).texture2(ay, az).next();
				bufferBuilder.vertex(d + 1.0, e, r).color(ba, bb, bc, 1.0F).texture((double)au, (double)av).texture2(ay, az).next();
				bufferBuilder.vertex(d + 1.0, e, r + 1.0).color(ba, bb, bc, 1.0F).texture((double)au, (double)aw).texture2(ay, az).next();
				bl8 = true;
			}

			for (int bd = 0; bd < 4; bd++) {
				float be;
				float bf;
				double bg;
				double bi;
				double bh;
				double bj;
				Direction direction;
				boolean bl9;
				if (bd == 0) {
					be = n;
					bf = q;
					bg = d;
					bh = d + 1.0;
					bi = r + 0.001F;
					bj = r + 0.001F;
					direction = Direction.NORTH;
					bl9 = bl4;
				} else if (bd == 1) {
					be = p;
					bf = o;
					bg = d + 1.0;
					bh = d;
					bi = r + 1.0 - 0.001F;
					bj = r + 1.0 - 0.001F;
					direction = Direction.SOUTH;
					bl9 = bl5;
				} else if (bd == 2) {
					be = o;
					bf = n;
					bg = d + 0.001F;
					bh = d + 0.001F;
					bi = r + 1.0;
					bj = r;
					direction = Direction.WEST;
					bl9 = bl6;
				} else {
					be = q;
					bf = p;
					bg = d + 1.0 - 0.001F;
					bh = d + 1.0 - 0.001F;
					bi = r;
					bj = r + 1.0;
					direction = Direction.EAST;
					bl9 = bl7;
				}

				if (bl9 && !method_19190(arg, blockPos, direction, Math.max(be, bf))) {
					bl8 = true;
					BlockPos blockPos2 = blockPos.offset(direction);
					Sprite sprite3 = sprites[1];
					if (!bl) {
						Block block = arg.getBlockState(blockPos2).getBlock();
						if (block == Blocks.GLASS || block instanceof StainedGlassBlock) {
							sprite3 = this.field_13550;
						}
					}

					float cd = sprite3.getFrameU(0.0);
					float ce = sprite3.getFrameU(8.0);
					float cf = sprite3.getFrameV((double)((1.0F - be) * 16.0F * 0.5F));
					float cg = sprite3.getFrameV((double)((1.0F - bf) * 16.0F * 0.5F));
					float ch = sprite3.getFrameV(8.0);
					int ci = this.method_19193(arg, blockPos2);
					int cj = ci >> 16 & 65535;
					int ck = ci & 65535;
					float cl = bd < 2 ? 0.8F : 0.6F;
					float cm = 1.0F * cl * f;
					float cn = 1.0F * cl * g;
					float co = 1.0F * cl * h;
					bufferBuilder.vertex(bg, e + (double)be, bi).color(cm, cn, co, 1.0F).texture((double)cd, (double)cf).texture2(cj, ck).next();
					bufferBuilder.vertex(bh, e + (double)bf, bj).color(cm, cn, co, 1.0F).texture((double)ce, (double)cg).texture2(cj, ck).next();
					bufferBuilder.vertex(bh, e + 0.0, bj).color(cm, cn, co, 1.0F).texture((double)ce, (double)ch).texture2(cj, ck).next();
					bufferBuilder.vertex(bg, e + 0.0, bi).color(cm, cn, co, 1.0F).texture((double)cd, (double)ch).texture2(cj, ck).next();
					if (sprite3 != this.field_13550) {
						bufferBuilder.vertex(bg, e + 0.0, bi).color(cm, cn, co, 1.0F).texture((double)cd, (double)ch).texture2(cj, ck).next();
						bufferBuilder.vertex(bh, e + 0.0, bj).color(cm, cn, co, 1.0F).texture((double)ce, (double)ch).texture2(cj, ck).next();
						bufferBuilder.vertex(bh, e + (double)bf, bj).color(cm, cn, co, 1.0F).texture((double)ce, (double)cg).texture2(cj, ck).next();
						bufferBuilder.vertex(bg, e + (double)be, bi).color(cm, cn, co, 1.0F).texture((double)cd, (double)cf).texture2(cj, ck).next();
					}
				}
			}

			return bl8;
		}
	}

	private int method_19193(class_3600 arg, BlockPos blockPos) {
		int i = arg.method_8578(blockPos, 0);
		int j = arg.method_8578(blockPos.up(), 0);
		int k = i & 0xFF;
		int l = j & 0xFF;
		int m = i >> 16 & 0xFF;
		int n = j >> 16 & 0xFF;
		return (k > l ? k : l) | (m > n ? m : n) << 16;
	}

	private float method_19192(RenderBlockView renderBlockView, BlockPos blockPos, Fluid fluid) {
		int i = 0;
		float f = 0.0F;

		for (int j = 0; j < 4; j++) {
			BlockPos blockPos2 = blockPos.add(-(j & 1), 0, -(j >> 1 & 1));
			if (renderBlockView.getFluidState(blockPos2.up()).getFluid().method_17781(fluid)) {
				return 1.0F;
			}

			FluidState fluidState = renderBlockView.getFluidState(blockPos2);
			if (fluidState.getFluid().method_17781(fluid)) {
				if (fluidState.method_17810() >= 0.8F) {
					f += fluidState.method_17810() * 10.0F;
					i += 10;
				} else {
					f += fluidState.method_17810();
					i++;
				}
			} else if (!renderBlockView.getBlockState(blockPos2).getMaterial().isSolid()) {
				i++;
			}
		}

		return f / (float)i;
	}
}
