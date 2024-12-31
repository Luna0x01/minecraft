package net.minecraft.client.render.block;

import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class FluidRenderer {
	private Sprite[] lavaSprites = new Sprite[2];
	private Sprite[] waterSprites = new Sprite[2];

	public FluidRenderer() {
		this.onResourceReload();
	}

	protected void onResourceReload() {
		SpriteAtlasTexture spriteAtlasTexture = MinecraftClient.getInstance().getSpriteAtlasTexture();
		this.lavaSprites[0] = spriteAtlasTexture.getSprite("minecraft:blocks/lava_still");
		this.lavaSprites[1] = spriteAtlasTexture.getSprite("minecraft:blocks/lava_flow");
		this.waterSprites[0] = spriteAtlasTexture.getSprite("minecraft:blocks/water_still");
		this.waterSprites[1] = spriteAtlasTexture.getSprite("minecraft:blocks/water_flow");
	}

	public boolean render(BlockView world, BlockState state, BlockPos pos, BufferBuilder buffer) {
		AbstractFluidBlock abstractFluidBlock = (AbstractFluidBlock)state.getBlock();
		abstractFluidBlock.setBoundingBox(world, pos);
		Sprite[] sprites = abstractFluidBlock.getMaterial() == Material.LAVA ? this.lavaSprites : this.waterSprites;
		int i = abstractFluidBlock.getBlendColor(world, pos);
		float f = (float)(i >> 16 & 0xFF) / 255.0F;
		float g = (float)(i >> 8 & 0xFF) / 255.0F;
		float h = (float)(i & 0xFF) / 255.0F;
		boolean bl = abstractFluidBlock.isSideInvisible(world, pos.up(), Direction.UP);
		boolean bl2 = abstractFluidBlock.isSideInvisible(world, pos.down(), Direction.DOWN);
		boolean[] bls = new boolean[]{
			abstractFluidBlock.isSideInvisible(world, pos.north(), Direction.NORTH),
			abstractFluidBlock.isSideInvisible(world, pos.south(), Direction.SOUTH),
			abstractFluidBlock.isSideInvisible(world, pos.west(), Direction.WEST),
			abstractFluidBlock.isSideInvisible(world, pos.east(), Direction.EAST)
		};
		if (!bl && !bl2 && !bls[0] && !bls[1] && !bls[2] && !bls[3]) {
			return false;
		} else {
			boolean bl3 = false;
			float j = 0.5F;
			float k = 1.0F;
			float l = 0.8F;
			float m = 0.6F;
			Material material = abstractFluidBlock.getMaterial();
			float n = this.getFluidHeight(world, pos, material);
			float o = this.getFluidHeight(world, pos.south(), material);
			float p = this.getFluidHeight(world, pos.east().south(), material);
			float q = this.getFluidHeight(world, pos.east(), material);
			double d = (double)pos.getX();
			double e = (double)pos.getY();
			double r = (double)pos.getZ();
			float s = 0.001F;
			if (bl) {
				bl3 = true;
				Sprite sprite = sprites[0];
				float t = (float)AbstractFluidBlock.getDirection(world, pos, material);
				if (t > -999.0F) {
					sprite = sprites[1];
				}

				n -= s;
				o -= s;
				p -= s;
				q -= s;
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

				int an = abstractFluidBlock.getBrightness(world, pos);
				int ao = an >> 16 & 65535;
				int ap = an & 65535;
				float aq = k * f;
				float ar = k * g;
				float as = k * h;
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

			if (bl2) {
				float at = sprites[0].getMinU();
				float au = sprites[0].getMaxU();
				float av = sprites[0].getMinV();
				float aw = sprites[0].getMaxV();
				int ax = abstractFluidBlock.getBrightness(world, pos.down());
				int ay = ax >> 16 & 65535;
				int az = ax & 65535;
				buffer.vertex(d, e, r + 1.0).color(j, j, j, 1.0F).texture((double)at, (double)aw).texture2(ay, az).next();
				buffer.vertex(d, e, r).color(j, j, j, 1.0F).texture((double)at, (double)av).texture2(ay, az).next();
				buffer.vertex(d + 1.0, e, r).color(j, j, j, 1.0F).texture((double)au, (double)av).texture2(ay, az).next();
				buffer.vertex(d + 1.0, e, r + 1.0).color(j, j, j, 1.0F).texture((double)au, (double)aw).texture2(ay, az).next();
				bl3 = true;
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
						bh = r + (double)s;
						bi = r + (double)s;
					} else if (ba == 1) {
						bd = p;
						be = o;
						bf = d + 1.0;
						bg = d;
						bh = r + 1.0 - (double)s;
						bi = r + 1.0 - (double)s;
					} else if (ba == 2) {
						bd = o;
						be = n;
						bf = d + (double)s;
						bg = d + (double)s;
						bh = r + 1.0;
						bi = r;
					} else {
						bd = q;
						be = p;
						bf = d + 1.0 - (double)s;
						bg = d + 1.0 - (double)s;
						bh = r;
						bi = r + 1.0;
					}

					bl3 = true;
					float cc = sprite2.getFrameU(0.0);
					float cd = sprite2.getFrameU(8.0);
					float ce = sprite2.getFrameV((double)((1.0F - bd) * 16.0F * 0.5F));
					float cf = sprite2.getFrameV((double)((1.0F - be) * 16.0F * 0.5F));
					float cg = sprite2.getFrameV(8.0);
					int ch = abstractFluidBlock.getBrightness(world, blockPos);
					int ci = ch >> 16 & 65535;
					int cj = ch & 65535;
					float ck = ba < 2 ? l : m;
					float cl = k * ck * f;
					float cm = k * ck * g;
					float cn = k * ck * h;
					buffer.vertex(bf, e + (double)bd, bh).color(cl, cm, cn, 1.0F).texture((double)cc, (double)ce).texture2(ci, cj).next();
					buffer.vertex(bg, e + (double)be, bi).color(cl, cm, cn, 1.0F).texture((double)cd, (double)cf).texture2(ci, cj).next();
					buffer.vertex(bg, e + 0.0, bi).color(cl, cm, cn, 1.0F).texture((double)cd, (double)cg).texture2(ci, cj).next();
					buffer.vertex(bf, e + 0.0, bh).color(cl, cm, cn, 1.0F).texture((double)cc, (double)cg).texture2(ci, cj).next();
					buffer.vertex(bf, e + 0.0, bh).color(cl, cm, cn, 1.0F).texture((double)cc, (double)cg).texture2(ci, cj).next();
					buffer.vertex(bg, e + 0.0, bi).color(cl, cm, cn, 1.0F).texture((double)cd, (double)cg).texture2(ci, cj).next();
					buffer.vertex(bg, e + (double)be, bi).color(cl, cm, cn, 1.0F).texture((double)cd, (double)cf).texture2(ci, cj).next();
					buffer.vertex(bf, e + (double)bd, bh).color(cl, cm, cn, 1.0F).texture((double)cc, (double)ce).texture2(ci, cj).next();
				}
			}

			return bl3;
		}
	}

	private float getFluidHeight(BlockView world, BlockPos pos, Material material) {
		int i = 0;
		float f = 0.0F;

		for (int j = 0; j < 4; j++) {
			BlockPos blockPos = pos.add(-(j & 1), 0, -(j >> 1 & 1));
			if (world.getBlockState(blockPos.up()).getBlock().getMaterial() == material) {
				return 1.0F;
			}

			BlockState blockState = world.getBlockState(blockPos);
			Material material2 = blockState.getBlock().getMaterial();
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
