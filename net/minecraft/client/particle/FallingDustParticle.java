package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FallingDustParticle extends Particle {
	float field_14945;
	final float field_14946;

	protected FallingDustParticle(World world, double d, double e, double f, float g, float h, float i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.red = g;
		this.green = h;
		this.blue = i;
		float j = 0.9F;
		this.scale *= 0.75F;
		this.scale *= 0.9F;
		this.field_14945 = this.scale;
		this.maxAge = (int)(32.0 / (Math.random() * 0.8 + 0.2));
		this.maxAge = (int)((float)this.maxAge * 0.9F);
		this.field_14946 = ((float)Math.random() - 0.5F) * 0.1F;
		this.field_14947 = (float)Math.random() * (float) (Math.PI * 2);
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.age + tickDelta) / (float)this.maxAge * 32.0F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		this.scale = this.field_14945 * f;
		super.draw(builder, entity, tickDelta, g, h, i, j, k);
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		if (this.age++ >= this.maxAge) {
			this.method_12251();
		}

		this.field_14948 = this.field_14947;
		this.field_14947 = this.field_14947 + (float) Math.PI * this.field_14946 * 2.0F;
		if (this.field_13434) {
			this.field_14948 = this.field_14947 = 0.0F;
		}

		this.setMiscTexture(7 - this.age * 8 / this.maxAge);
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityY -= 0.003F;
		this.velocityY = Math.max(this.velocityY, -0.14F);
	}

	public static class Factory implements ParticleFactory {
		@Nullable
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			BlockState blockState = Block.getStateFromRawId(arr[0]);
			if (blockState.getBlock() != Blocks.AIR && blockState.getRenderType() == BlockRenderType.INVISIBLE) {
				return null;
			} else {
				int i = MinecraftClient.getInstance().method_12144().method_13410(blockState, world, new BlockPos(x, y, z));
				if (blockState.getBlock() instanceof FallingBlock) {
					i = ((FallingBlock)blockState.getBlock()).getColor(blockState);
				}

				float f = (float)(i >> 16 & 0xFF) / 255.0F;
				float g = (float)(i >> 8 & 0xFF) / 255.0F;
				float h = (float)(i & 0xFF) / 255.0F;
				return new FallingDustParticle(world, x, y, z, f, g, h);
			}
		}
	}
}
