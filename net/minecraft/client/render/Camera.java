package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.util.glu.GLU;

public class Camera {
	private static final IntBuffer VIEWPORT = GlAllocationUtils.allocateIntBuffer(16);
	private static final FloatBuffer MODEL_MATRIX = GlAllocationUtils.allocateFloatBuffer(16);
	private static final FloatBuffer PROJECTION_MATRIX = GlAllocationUtils.allocateFloatBuffer(16);
	private static final FloatBuffer OBJECT_POS = GlAllocationUtils.allocateFloatBuffer(3);
	private static Vec3d position = new Vec3d(0.0, 0.0, 0.0);
	private static float rotationX;
	private static float rotationXZ;
	private static float rotationZ;
	private static float rotationYZ;
	private static float rotationXY;

	public static void update(PlayerEntity player, boolean thirdPerson) {
		GlStateManager.getFloat(2982, MODEL_MATRIX);
		GlStateManager.getFloat(2983, PROJECTION_MATRIX);
		GlStateManager.method_12283(2978, VIEWPORT);
		float f = (float)((VIEWPORT.get(0) + VIEWPORT.get(2)) / 2);
		float g = (float)((VIEWPORT.get(1) + VIEWPORT.get(3)) / 2);
		GLU.gluUnProject(f, g, 0.0F, MODEL_MATRIX, PROJECTION_MATRIX, VIEWPORT, OBJECT_POS);
		position = new Vec3d((double)OBJECT_POS.get(0), (double)OBJECT_POS.get(1), (double)OBJECT_POS.get(2));
		int i = thirdPerson ? 1 : 0;
		float h = player.pitch;
		float j = player.yaw;
		rotationX = MathHelper.cos(j * (float) (Math.PI / 180.0)) * (float)(1 - i * 2);
		rotationZ = MathHelper.sin(j * (float) (Math.PI / 180.0)) * (float)(1 - i * 2);
		rotationYZ = -rotationZ * MathHelper.sin(h * (float) (Math.PI / 180.0)) * (float)(1 - i * 2);
		rotationXY = rotationX * MathHelper.sin(h * (float) (Math.PI / 180.0)) * (float)(1 - i * 2);
		rotationXZ = MathHelper.cos(h * (float) (Math.PI / 180.0));
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

	public static BlockState method_9371(World world, Entity entity, float f) {
		Vec3d vec3d = getEntityPos(entity, (double)f);
		BlockPos blockPos = new BlockPos(vec3d);
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getMaterial().isFluid()) {
			float g = 0.0F;
			if (blockState.getBlock() instanceof AbstractFluidBlock) {
				g = AbstractFluidBlock.getHeightPercent((Integer)blockState.get(AbstractFluidBlock.LEVEL)) - 0.11111111F;
			}

			float h = (float)(blockPos.getY() + 1) - g;
			if (vec3d.y >= (double)h) {
				blockState = world.getBlockState(blockPos.up());
			}
		}

		return blockState;
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
