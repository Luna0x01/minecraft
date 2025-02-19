package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public interface BlockEntityRenderer<T extends BlockEntity> {
	void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);

	default boolean rendersOutsideBoundingBox(T blockEntity) {
		return false;
	}

	default int getRenderDistance() {
		return 64;
	}

	default boolean method_33892(T blockEntity, Vec3d vec3d) {
		return Vec3d.ofCenter(blockEntity.getPos()).isInRange(vec3d, (double)this.getRenderDistance());
	}
}
