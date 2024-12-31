package net.minecraft.client.render.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;

public class SpawnerMinecartEntityRenderer extends MinecartEntityRenderer<SpawnerMinecartEntity> {
	public SpawnerMinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	protected void method_5180(SpawnerMinecartEntity spawnerMinecartEntity, float f, BlockState blockState) {
		super.method_5180(spawnerMinecartEntity, f, blockState);
		if (blockState.getBlock() == Blocks.SPAWNER) {
			MobSpawnerBlockEntityRenderer.renderEntity(
				spawnerMinecartEntity.getSpawnerBehavior(), spawnerMinecartEntity.x, spawnerMinecartEntity.y, spawnerMinecartEntity.z, f
			);
		}
	}
}
