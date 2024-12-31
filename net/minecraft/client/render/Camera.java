package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import net.minecraft.class_4307;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class Camera {
	private static final FloatBuffer MODEL_MATRIX = GlAllocationUtils.allocateFloatBuffer(16);
	private static Vec3d position = new Vec3d(0.0, 0.0, 0.0);
	private static float rotationX;
	private static float rotationXZ;
	private static float rotationZ;
	private static float rotationYZ;
	private static float rotationXY;

	public static void method_18134(PlayerEntity playerEntity, boolean bl, float f) {
		MODEL_MATRIX.clear();
		GlStateManager.getFloat(2982, MODEL_MATRIX);
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.method_19648(MODEL_MATRIX);
		matrix4f.method_19654();
		float g = 0.05F;
		float h = f * MathHelper.SQUARE_ROOT_OF_TWO;
		class_4307 lv = new class_4307(0.0F, 0.0F, -2.0F * h * 0.05F / (h + 0.05F), 1.0F);
		lv.method_19675(matrix4f);
		position = new Vec3d((double)lv.method_19673(), (double)lv.method_19678(), (double)lv.method_19679());
		float i = playerEntity.pitch;
		float j = playerEntity.yaw;
		int k = bl ? -1 : 1;
		rotationX = MathHelper.cos(j * (float) (Math.PI / 180.0)) * (float)k;
		rotationZ = MathHelper.sin(j * (float) (Math.PI / 180.0)) * (float)k;
		rotationYZ = -rotationZ * MathHelper.sin(i * (float) (Math.PI / 180.0)) * (float)k;
		rotationXY = rotationX * MathHelper.sin(i * (float) (Math.PI / 180.0)) * (float)k;
		rotationXZ = MathHelper.cos(i * (float) (Math.PI / 180.0));
	}

	public static Vec3d getEntityPos(Entity entity, double delta) {
		double d = entity.prevX + (entity.x - entity.prevX) * delta;
		double e = entity.prevY + (entity.y - entity.prevY) * delta;
		double f = entity.prevZ + (entity.z - entity.prevZ) * delta;
		double g = d + position.x;
		double h = e + position.y;
		double i = f + position.z;
		return new Vec3d(g, h, i);
	}

	public static BlockState method_18135(BlockView blockView, Entity entity, float f) {
		Vec3d vec3d = getEntityPos(entity, (double)f);
		BlockPos blockPos = new BlockPos(vec3d);
		BlockState blockState = blockView.getBlockState(blockPos);
		FluidState fluidState = blockView.getFluidState(blockPos);
		if (!fluidState.isEmpty()) {
			float g = (float)blockPos.getY() + fluidState.method_17810() + 0.11111111F;
			if (vec3d.y >= (double)g) {
				blockState = blockView.getBlockState(blockPos.up());
			}
		}

		return blockState;
	}

	public static FluidState method_18136(BlockView blockView, Entity entity, float f) {
		Vec3d vec3d = getEntityPos(entity, (double)f);
		BlockPos blockPos = new BlockPos(vec3d);
		FluidState fluidState = blockView.getFluidState(blockPos);
		if (!fluidState.isEmpty()) {
			float g = (float)blockPos.getY() + fluidState.method_17810() + 0.11111111F;
			if (vec3d.y >= (double)g) {
				fluidState = blockView.getFluidState(blockPos.up());
			}
		}

		return fluidState;
	}

	public static float getRotationX() {
		return rotationX;
	}

	public static float getRotationXZ() {
		return rotationXZ;
	}

	public static float getRotationZ() {
		return rotationZ;
	}

	public static float getRotationYZ() {
		return rotationYZ;
	}

	public static float getRotationXY() {
		return rotationXY;
	}
}
