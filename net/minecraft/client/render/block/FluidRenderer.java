package net.minecraft.client.render.block;

import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.BlockColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class FluidRenderer {
	private final BlockColors field_13549;
	private final Sprite[] lavaSprites = new Sprite[2];
	private final Sprite[] waterSprites = new Sprite[2];
	private Sprite field_13550;

	public FluidRenderer(BlockColors blockColors) {
		this.field_13549 = blockColors;
		this.onResourceReload();
	}

	protected void onResourceReload() {
		SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlasTexture();
		this.lavaSprites[0] = spriteAtlasTexture.getSprite("minecraft:blocks/lava_still");
		this.lavaSprites[1] = spriteAtlasTexture.getSprite("minecraft:blocks/lava_flow");
		this.waterSprites[0] = spriteAtlasTexture.getSprite("minecraft:blocks/water_still");
		this.waterSprites[1] = spriteAtlasTexture.getSprite("minecraft:blocks/water_flow");
		this.field_13550 = spriteAtlasTexture.getSprite("minecraft:blocks/water_overlay");
	}

	public boolean render(BlockView world, BlockState state, BlockPos pos, BufferBuilder buffer) {
		AbstractFluidBlock abstractFluidBlock = (AbstractFluidBlock)state.getBlock();
		boolean bl = state.getMaterial() == Material.LAVA;
		Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
		int i = this.field_13549.method_12157(state, world, pos, 0);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		boolean bl2 = state.method_11724(world, pos, Direction.UP);
		boolean bl3 = state.method_11724(world, pos, Direction.DOWN);
		boolean[] bls = new boolean[]{
			state.method_11724(world, pos, Direction.NORTH),
			state.method_11724(world, pos, Direction.SOUTH),
			state.method_11724(world, pos, Direction.WEST),
			state.method_11724(world, pos, Direction.EAST)
		};
		if (!bl2 && !bl3 && !bls[0] && !bls[1] && !bls[2] && !bls[3]) {
			return false;
		} else {
			boolean bl4 = false;
			float j = 0.5F;
			float k = 1.0F;
			float l = 0.8F;
			float m = 0.6F;
			Material material = state.getMaterial();
			float n = this.getFluidHeight(world, pos, material);
			float o = this.getFluidHeight(world, pos.south(), material);
			float p = this.getFluidHeight(world, pos.east().south(), material);
			float q = this.getFluidHeight(world, pos.east(), material);
			double d = (double)pos.getX();
			double e = (double)pos.getY();
			double r = (double)pos.getZ();
			float s = 0.001F;
			if (bl2) {
				bl4 = true;
				float t = AbstractFluidBlock.method_11618(world, pos, material, state);
				Sprite sprite = t > -999.0F ? sprites[1] : sprites[0];
				n -= 0.001F;
				o -= 0.001F;
				p -= 0.001F;
				q -= 0.001F;
				float u;
				float w;
				float y;
				float aa;
				float v;
				float x;
				float z;
				float ab;
				if (t < -999.0F) {
					u = sprite.getFrameU(0.0);
					v = sprite.getFrameV(0.0);
					w = u;
					x = sprite.getFrameV(16.0);
					y = sprite.getFrameU(16.0);
					z = x;
					aa = y;
					ab = v;
				} else {
					float ac = MathHelper.sin(t) * 0.25F;
					float ad = MathHelper.cos(t) * 0.25F;
					float ae = 8.0F;
					u = sprite.getFrameU((double)(8.0F + (-ad - ac) * 16.0F));
					v = sprite.getFrameV((double)(8.0F + (-ad + ac) * 16.0F));
					w = sprite.getFrameU((double)(8.0F + (-ad + ac) * 16.0F));
					x = sprite.getFrameV((double)(8.0F + (ad + ac) * 16.0F));
					y = sprite.getFrameU((double)(8.0F + (ad + ac) * 16.0F));
					z = sprite.getFrameV((double)(8.0F + (ad - ac) * 16.0F));
					aa = sprite.getFrameU((double)(8.0F + (ad - ac) * 16.0F));
					ab = sprite.getFrameV((double)(8.0F + (-ad - ac) * 16.0F));
				}

				int an = state.method_11712(world, pos);
				int ao = an >> 16 & 65535;
				int ap = an & 65535;
				float aq = 1.0F * f;
				float ar = 1.0F * g;
				float as = 1.0F * h;
				buffer.vertex(d + 0.0, e + (double)n, r + 0.0).color(aq, ar, as, 1.0F).texture((double)u, (double)v).texture2(ao, ap).next();
				buffer.vertex(d + 0.0, e + (double)o, r + 1.0).color(aq, ar, as, 1.0F).texture((double)w, (double)x).texture2(ao, ap).next();
				buffer.vertex(d + 1.0, e + (double)p, r + 1.0).color(aq, ar, as, 1.0F).texture((double)y, (double)z).texture2(ao, ap).next();
				buffer.vertex(d + 1.0, e + (double)q, r + 0.0).color(aq, ar, as, 1.0F).texture((double)aa, (double)ab).texture2(ao, ap).next();
				if (abstractFluidBlock.shouldDisableCullingSides(world, pos.up())) {
					buffer.vertex(d + 0.0, e + (double)n, r + 0.0).color(aq, ar, as, 1.0F).texture((double)u, (double)v).texture2(ao, ap).next();
					buffer.vertex(d + 1.0, e + (double)q, r + 0.0).color(aq, ar, as, 1.0F).texture((double)aa, (double)ab).texture2(ao, ap).next();
					buffer.vertex(d + 1.0, e + (double)p, r + 1.0).color(aq, ar, as, 1.0F).texture((double)y, (double)z).texture2(ao, ap).next();
					buffer.vertex(d + 0.0, e + (double)o, r + 1.0).color(aq, ar, as, 1.0F).texture((double)w, (double)x).texture2(ao, ap).next();
				}
			}

			if (bl3) {
				float at = sprites[0].getMinU();
				float au = sprites[0].getMaxU();
				float av = sprites[0].getMinV();
				float aw = sprites[0].getMaxV();
				int ax = state.method_11712(world, pos.down());
				int ay = ax >> 16 & 65535;
				int az = ax & 65535;
				buffer.vertex(d, e, r + 1.0).color(0.5F, 0.5F, 0.5F, 1.0F).texture((double)at, (double)aw).texture2(ay, az).next();
				buffer.vertex(d, e, r).color(0.5F, 0.5F, 0.5F, 1.0F).texture((double)at, (double)av).texture2(ay, az).next();
				buffer.vertex(d + 1.0, e, r).color(0.5F, 0.5F, 0.5F, 1.0F).texture((double)au, (double)av).texture2(ay, az).next();
				buffer.vertex(d + 1.0, e, r + 1.0).color(0.5F, 0.5F, 0.5F, 1.0F).texture((double)au, (double)aw).texture2(ay, az).next();
				bl4 = true;
			}

			for (int ba = 0; ba < 4; ba++) {
				int bb = 0;
				int bc = 0;
				if (ba == 0) {
					bc--;
				}

				if (ba == 1) {
					bc++;
				}

				if (ba == 2) {
					bb--;
				}

				if (ba == 3) {
					bb++;
				}

				BlockPos blockPos = pos.add(bb, 0, bc);
				Sprite sprite2 = sprites[1];
				if (!bl) {
					Block block = world.getBlockState(blockPos).getBlock();
					if (block == Blocks.GLASS || block == Blocks.STAINED_GLASS) {
						sprite2 = this.field_13550;
					}
				}

				if (bls[ba]) {
					float bd;
					float be;
					double bf;
					double bh;
					double bg;
					double bi;
					if (ba == 0) {
						bd = n;
						be = q;
						bf = d;
						bg = d + 1.0;
						bh = r + 0.001F;
						bi = r + 0.001F;
					} else if (ba == 1) {
						bd = p;
						be = o;
						bf = d + 1.0;
						bg = d;
						bh = r + 1.0 - 0.001F;
						bi = r + 1.0 - 0.001F;
					} else if (ba == 2) {
						bd = o;
						be = n;
						bf = d + 0.001F;
						bg = d + 0.001F;
						bh = r + 1.0;
						bi = r;
					} else {
						bd = q;
						be = p;
						bf = d + 1.0 - 0.001F;
						bg = d + 1.0 - 0.001F;
						bh = r;
						bi = r + 1.0;
					}

					bl4 = true;
					float cc = sprite2.getFrameU(0.0);
					float cd = sprite2.getFrameU(8.0);
					float ce = sprite2.getFrameV((double)((1.0F - bd) * 16.0F * 0.5F));
					float cf = sprite2.getFrameV((double)((1.0F - be) * 16.0F * 0.5F));
					float cg = sprite2.getFrameV(8.0);
					int ch = state.method_11712(world, blockPos);
					int ci = ch >> 16 & 65535;
					int cj = ch & 65535;
					float ck = ba < 2 ? 0.8F : 0.6F;
					float cl = 1.0F * ck * f;
					float cm = 1.0F * ck * g;
					float cn = 1.0F * ck * h;
					buffer.vertex(bf, e + (double)bd, bh).color(cl, cm, cn, 1.0F).texture((double)cc, (double)ce).texture2(ci, cj).next();
					buffer.vertex(bg, e + (double)be, bi).color(cl, cm, cn, 1.0F).texture((double)cd, (double)cf).texture2(ci, cj).next();
					buffer.vertex(bg, e + 0.0, bi).color(cl, cm, cn, 1.0F).texture((double)cd, (double)cg).texture2(ci, cj).next();
					buffer.vertex(bf, e + 0.0, bh).color(cl, cm, cn, 1.0F).texture((double)cc, (double)cg).texture2(ci, cj).next();
					if (sprite2 != this.field_13550) {
						buffer.vertex(bf, e + 0.0, bh).color(cl, cm, cn, 1.0F).texture((double)cc, (double)cg).texture2(ci, cj).next();
						buffer.vertex(bg, e + 0.0, bi).color(cl, cm, cn, 1.0F).texture((double)cd, (double)cg).texture2(ci, cj).next();
						buffer.vertex(bg, e + (double)be, bi).color(cl, cm, cn, 1.0F).texture((double)cd, (double)cf).texture2(ci, cj).next();
						buffer.vertex(bf, e + (double)bd, bh).color(cl, cm, cn, 1.0F).texture((double)cc, (double)ce).texture2(ci, cj).next();
					}
				}
			}

			return bl4;
		}
	}

	private float getFluidHeight(BlockView world, BlockPos pos, Material material) {
		int i = 0;
		float f = 0.0F;

		for (int j = 0; j < 4; j++) {
			BlockPos blockPos = pos.add(-(j & 1), 0, -(j >> 1 & 1));
			if (world.getBlockState(blockPos.up()).getMaterial() == material) {
				return 1.0F;
			}

			BlockState blockState = world.getBlockState(blockPos);
			Material material2 = blockState.getMaterial();
			if (material2 == material) {
				int k = (Integer)blockState.get(AbstractFluidBlock.LEVEL);
				if (k >= 8 || k == 0) {
					f += AbstractFluidBlock.getHeightPercent(k) * 10.0F;
					i += 10;
				}

				f += AbstractFluidBlock.getHeightPercent(k);
				i++;
			} else if (!material2.isSolid()) {
				f++;
				i++;
			}
		}

		return 1.0F - f / (float)i;
	}
}
