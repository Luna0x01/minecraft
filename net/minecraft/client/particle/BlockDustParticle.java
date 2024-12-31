package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDustParticle extends Particle {
	private final BlockState state;
	private BlockPos pos;

	protected BlockDustParticle(World world, double d, double e, double f, double g, double h, double i, BlockState blockState) {
		super(world, d, e, f, g, h, i);
		this.state = blockState;
		this.setTexture(MinecraftClient.getInstance().getBlockRenderManager().getModels().getParticleSprite(blockState));
		this.gravityStrength = blockState.getBlock().particleGravity;
		this.red = 0.6F;
		this.green = 0.6F;
		this.blue = 0.6F;
		this.scale /= 2.0F;
	}

	public BlockDustParticle method_12260(BlockPos blockPos) {
		this.pos = blockPos;
		if (this.state.getBlock() == Blocks.GRASS) {
			return this;
		} else {
			this.method_12261(blockPos);
			return this;
		}
	}

	public BlockDustParticle method_12262() {
		this.pos = new BlockPos(this.field_13428, this.field_13429, this.field_13430);
		Block block = this.state.getBlock();
		if (block == Blocks.GRASS) {
			return this;
		} else {
			this.method_12261(this.pos);
			return this;
		}
	}

	protected void method_12261(@Nullable BlockPos blockPos) {
		int i = MinecraftClient.getInstance().method_12144().method_12157(this.state, this.field_13424, blockPos, 0);
		this.red *= (float)(i >> 16 & 0xFF) / 255.0F;
		this.green *= (float)(i >> 8 & 0xFF) / 255.0F;
		this.blue *= (float)(i & 0xFF) / 255.0F;
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

		float p = (float)(this.field_13425 + (this.field_13428 - this.field_13425) * (double)tickDelta - field_1722);
		float q = (float)(this.field_13426 + (this.field_13429 - this.field_13426) * (double)tickDelta - field_1723);
		float r = (float)(this.field_13427 + (this.field_13430 - this.field_13427) * (double)tickDelta - field_1724);
		int s = this.method_12243(tickDelta);
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
	public int method_12243(float f) {
		int i = super.method_12243(f);
		int j = 0;
		if (this.field_13424.blockExists(this.pos)) {
			j = this.field_13424.getLight(this.pos, 0);
		}

		return i == 0 ? j : i;
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new BlockDustParticle(world, x, y, z, velocityX, velocityY, velocityZ, Block.getStateFromRawId(arr[0])).method_12262();
		}
	}
}
