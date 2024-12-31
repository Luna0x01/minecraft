package net.minecraft.client.render;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RayTraceContext;

public class Camera {
	private boolean ready;
	private BlockView area;
	private Entity focusedEntity;
	private Vec3d pos = Vec3d.ZERO;
	private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
	private final Vector3f horizontalPlane = new Vector3f(0.0F, 0.0F, 1.0F);
	private final Vector3f verticalPlane = new Vector3f(0.0F, 1.0F, 0.0F);
	private final Vector3f diagonalPlane = new Vector3f(1.0F, 0.0F, 0.0F);
	private float pitch;
	private float yaw;
	private final Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
	private boolean thirdPerson;
	private boolean inverseView;
	private float cameraY;
	private float lastCameraY;

	public void update(BlockView blockView, Entity entity, boolean bl, boolean bl2, float f) {
		this.ready = true;
		this.area = blockView;
		this.focusedEntity = entity;
		this.thirdPerson = bl;
		this.inverseView = bl2;
		this.setRotation(entity.getYaw(f), entity.getPitch(f));
		this.setPos(
			MathHelper.lerp((double)f, entity.prevX, entity.getX()),
			MathHelper.lerp((double)f, entity.prevY, entity.getY()) + (double)MathHelper.lerp(f, this.lastCameraY, this.cameraY),
			MathHelper.lerp((double)f, entity.prevZ, entity.getZ())
		);
		if (bl) {
			if (bl2) {
				this.setRotation(this.yaw + 180.0F, -this.pitch);
			}

			this.moveBy(-this.clipToSpace(4.0), 0.0, 0.0);
		} else if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
			Direction direction = ((LivingEntity)entity).getSleepingDirection();
			this.setRotation(direction != null ? direction.asRotation() - 180.0F : 0.0F, 0.0F);
			this.moveBy(0.0, 0.3, 0.0);
		}
	}

	public void updateEyeHeight() {
		if (this.focusedEntity != null) {
			this.lastCameraY = this.cameraY;
			this.cameraY = this.cameraY + (this.focusedEntity.getStandingEyeHeight() - this.cameraY) * 0.5F;
		}
	}

	private double clipToSpace(double d) {
		for (int i = 0; i < 8; i++) {
			float f = (float)((i & 1) * 2 - 1);
			float g = (float)((i >> 1 & 1) * 2 - 1);
			float h = (float)((i >> 2 & 1) * 2 - 1);
			f *= 0.1F;
			g *= 0.1F;
			h *= 0.1F;
			Vec3d vec3d = this.pos.add((double)f, (double)g, (double)h);
			Vec3d vec3d2 = new Vec3d(
				this.pos.x - (double)this.horizontalPlane.getX() * d + (double)f + (double)h,
				this.pos.y - (double)this.horizontalPlane.getY() * d + (double)g,
				this.pos.z - (double)this.horizontalPlane.getZ() * d + (double)h
			);
			HitResult hitResult = this.area
				.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.field_1348, this.focusedEntity));
			if (hitResult.getType() != HitResult.Type.field_1333) {
				double e = hitResult.getPos().distanceTo(this.pos);
				if (e < d) {
					d = e;
				}
			}
		}

		return d;
	}

	protected void moveBy(double d, double e, double f) {
		double g = (double)this.horizontalPlane.getX() * d + (double)this.verticalPlane.getX() * e + (double)this.diagonalPlane.getX() * f;
		double h = (double)this.horizontalPlane.getY() * d + (double)this.verticalPlane.getY() * e + (double)this.diagonalPlane.getY() * f;
		double i = (double)this.horizontalPlane.getZ() * d + (double)this.verticalPlane.getZ() * e + (double)this.diagonalPlane.getZ() * f;
		this.setPos(new Vec3d(this.pos.x + g, this.pos.y + h, this.pos.z + i));
	}

	protected void setRotation(float f, float g) {
		this.pitch = g;
		this.yaw = f;
		this.rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
		this.rotation.hamiltonProduct(Vector3f.POSITIVE_Y.getDegreesQuaternion(-f));
		this.rotation.hamiltonProduct(Vector3f.POSITIVE_X.getDegreesQuaternion(g));
		this.horizontalPlane.set(0.0F, 0.0F, 1.0F);
		this.horizontalPlane.rotate(this.rotation);
		this.verticalPlane.set(0.0F, 1.0F, 0.0F);
		this.verticalPlane.rotate(this.rotation);
		this.diagonalPlane.set(1.0F, 0.0F, 0.0F);
		this.diagonalPlane.rotate(this.rotation);
	}

	protected void setPos(double d, double e, double f) {
		this.setPos(new Vec3d(d, e, f));
	}

	protected void setPos(Vec3d vec3d) {
		this.pos = vec3d;
		this.blockPos.set(vec3d.x, vec3d.y, vec3d.z);
	}

	public Vec3d getPos() {
		return this.pos;
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public float getPitch() {
		return this.pitch;
	}

	public float getYaw() {
		return this.yaw;
	}

	public Quaternion getRotation() {
		return this.rotation;
	}

	public Entity getFocusedEntity() {
		return this.focusedEntity;
	}

	public boolean isReady() {
		return this.ready;
	}

	public boolean isThirdPerson() {
		return this.thirdPerson;
	}

	public FluidState getSubmergedFluidState() {
		if (!this.ready) {
			return Fluids.field_15906.getDefaultState();
		} else {
			FluidState fluidState = this.area.getFluidState(this.blockPos);
			return !fluidState.isEmpty() && this.pos.y >= (double)((float)this.blockPos.getY() + fluidState.getHeight(this.area, this.blockPos))
				? Fluids.field_15906.getDefaultState()
				: fluidState;
		}
	}

	public final Vector3f getHorizontalPlane() {
		return this.horizontalPlane;
	}

	public final Vector3f getVerticalPlane() {
		return this.verticalPlane;
	}

	public void reset() {
		this.area = null;
		this.focusedEntity = null;
		this.ready = false;
	}
}
