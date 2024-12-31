package net.minecraft.block;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SlimeBlock extends TransparentBlock {
	public SlimeBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
	}

	@Override
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		if (entity.isSneaking()) {
			super.onLandedUpon(world, pos, entity, distance);
		} else {
			entity.handleFallDamage(distance, 0.0F);
		}
	}

	@Override
	public void onEntityLand(BlockView world, Entity entity) {
		if (entity.isSneaking()) {
			super.onEntityLand(world, entity);
		} else if (entity.velocityY < 0.0) {
			entity.velocityY = -entity.velocityY;
			if (!(entity instanceof LivingEntity)) {
				entity.velocityY *= 0.8;
			}
		}
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		if (Math.abs(entity.velocityY) < 0.1 && !entity.isSneaking()) {
			double d = 0.4 + Math.abs(entity.velocityY) * 0.2;
			entity.velocityX *= d;
			entity.velocityZ *= d;
		}

		super.onSteppedOn(world, pos, entity);
	}

	@Override
	public int getLightSubtracted(BlockState state, BlockView world, BlockPos pos) {
		return 0;
	}
}
