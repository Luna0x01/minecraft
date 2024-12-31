package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDustParticle extends Particle {
	private BlockState state;
	private BlockPos pos;

	protected BlockDustParticle(World world, double d, double e, double f, double g, double h, double i, BlockState blockState) {
		super(world, d, e, f, g, h, i);
		this.state = blockState;
		this.setTexture(MinecraftClient.getInstance().getBlockRenderManager().getModels().getParticleSprite(blockState));
		this.gravityStrength = blockState.getBlock().particleGravity;
		this.red = this.green = this.blue = 0.6F;
		this.scale /= 2.0F;
	}

	public BlockDustParticle setBlockPos(BlockPos pos) {
		this.pos = pos;
		if (this.state.getBlock() == Blocks.GRASS) {
			return this;
		} else {
			int i = this.state.getBlock().getBlendColor(this.world, pos);
			this.red *= (float)(i >> 16 & 0xFF) / 255.0F;
			this.green *= (float)(i >> 8 & 0xFF) / 255.0F;
			this.blue *= (float)(i & 0xFF) / 255.0F;
			return this;
		}
	}

	public BlockDustParticle setBlockPosFromPosition() {
		this.pos = new BlockPos(this.x, this.y, this.z);
		Block block = this.state.getBlock();
		if (block == Blocks.GRASS) {
			return this;
		} else {
			int i = block.getColor(this.state);
			this.red *= (float)(i >> 16 & 0xFF) / 255.0F;
			this.green *= (float)(i >> 8 & 0xFF) / 255.0F;
			this.blue *= (float)(i & 0xFF) / 255.0F;
			return this;
		}
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.field_5935 + this.field_1725 / 4.0F) / 16.0F;
		float l = f + 0.015609375F;
		float m = ((float)this.field_5936 + this.field_1726 / 4.0F) / 16.0F;
		float n = m + 0.015609375F;
		float o = 0.1F * this.scale;
		if (this.sprite != null) {
			f = this.sprite.getFrameU((double)(this.field_1725 / 4.0F * 16.0F));
			l = this.sprite.getFrameU((double)((this.field_1725 + 1.0F) / 4.0F * 16.0F));
			m = this.sprite.getFrameV((double)(this.field_1726 / 4.0F * 16.0F));
			n = this.sprite.getFrameV((double)((this.field_1726 + 1.0F) / 4.0F * 16.0F));
		}

		float p = (float)(this.prevX + (this.x - this.prevX) * (double)tickDelta - field_1722);
		float q = (float)(this.prevY + (this.y - this.prevY) * (double)tickDelta - field_1723);
		float r = (float)(this.prevZ + (this.z - this.prevZ) * (double)tickDelta - field_1724);
		int s = this.getLightmapCoordinates(tickDelta);
		int t = s >> 16 & 65535;
		int u = s & 65535;
		builder.vertex((double)(p - g * o - j * o), (double)(q - h * o), (double)(r - i * o - k * o))
			.texture((double)f, (double)n)
			.color(this.red, this.green, this.blue, 1.0F)
			.texture2(t, u)
			.next();
		builder.vertex((double)(p - g * o + j * o), (double)(q + h * o), (double)(r - i * o + k * o))
			.texture((double)f, (double)m)
			.color(this.red, this.green, this.blue, 1.0F)
			.texture2(t, u)
			.next();
		builder.vertex((double)(p + g * o + j * o), (double)(q + h * o), (double)(r + i * o + k * o))
			.texture((double)l, (double)m)
			.color(this.red, this.green, this.blue, 1.0F)
			.texture2(t, u)
			.next();
		builder.vertex((double)(p + g * o - j * o), (double)(q - h * o), (double)(r + i * o - k * o))
			.texture((double)l, (double)n)
			.color(this.red, this.green, this.blue, 1.0F)
			.texture2(t, u)
			.next();
	}

	@Override
	public int getLightmapCoordinates(float f) {
		int i = super.getLightmapCoordinates(f);
		int j = 0;
		if (this.world.blockExists(this.pos)) {
			j = this.world.getLight(this.pos, 0);
		}

		return i == 0 ? j : i;
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new BlockDustParticle(world, x, y, z, velocityX, velocityY, velocityZ, Block.getStateFromRawId(arr[0])).setBlockPosFromPosition();
		}
	}
}
