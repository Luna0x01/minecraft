package net.minecraft.entity.projectile;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_4079;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class ProjectileUtil {
	public static BlockHitResult method_13286(Entity entity, boolean bl, boolean bl2, @Nullable Entity entity2) {
		double d = entity.x;
		double e = entity.y;
		double f = entity.z;
		double g = entity.velocityX;
		double h = entity.velocityY;
		double i = entity.velocityZ;
		World world = entity.world;
		Vec3d vec3d = new Vec3d(d, e, f);
		if (!world.method_16366(entity, entity.getBoundingBox(), (Set<Entity>)(!bl2 && entity2 != null ? method_15954(entity2) : ImmutableSet.of()))) {
			return new BlockHitResult(BlockHitResult.Type.BLOCK, vec3d, Direction.getFacing(g, h, i), new BlockPos(entity));
		} else {
			Vec3d vec3d2 = new Vec3d(d + g, e + h, f + i);
			BlockHitResult blockHitResult = world.method_3615(vec3d, vec3d2, class_4079.NEVER, true, false);
			if (bl) {
				if (blockHitResult != null) {
					vec3d2 = new Vec3d(blockHitResult.pos.x, blockHitResult.pos.y, blockHitResult.pos.z);
				}

				Entity entity3 = null;
				List<Entity> list = world.getEntities(entity, entity.getBoundingBox().stretch(g, h, i).expand(1.0));
				double j = 0.0;

				for (int k = 0; k < list.size(); k++) {
					Entity entity4 = (Entity)list.get(k);
					if (entity4.collides() && (bl2 || !entity4.isPartOf(entity2)) && !entity4.noClip) {
						Box box = entity4.getBoundingBox().expand(0.3F);
						BlockHitResult blockHitResult2 = box.method_585(vec3d, vec3d2);
						if (blockHitResult2 != null) {
							double l = vec3d.squaredDistanceTo(blockHitResult2.pos);
							if (l < j || j == 0.0) {
								entity3 = entity4;
								j = l;
							}
						}
					}
				}

				if (entity3 != null) {
					blockHitResult = new BlockHitResult(entity3);
				}
			}

			return blockHitResult;
		}
	}

	private static Set<Entity> method_15954(Entity entity) {
		Entity entity2 = entity.getVehicle();
		return entity2 != null ? ImmutableSet.of(entity, entity2) : ImmutableSet.of(entity);
	}

	public static final void setRotationFromVelocity(Entity entity, float delta) {
		double d = entity.velocityX;
		double e = entity.velocityY;
		double f = entity.velocityZ;
		float g = MathHelper.sqrt(d * d + f * f);
		entity.yaw = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) + 90.0F;
		entity.pitch = (float)(MathHelper.atan2((double)g, e) * 180.0F / (float)Math.PI) - 90.0F;

		while (entity.pitch - entity.prevPitch < -180.0F) {
			entity.prevPitch -= 360.0F;
		}

		while (entity.pitch - entity.prevPitch >= 180.0F) {
			entity.prevPitch += 360.0F;
		}

		while (entity.yaw - entity.prevYaw < -180.0F) {
			entity.prevYaw -= 360.0F;
		}

		while (entity.yaw - entity.prevYaw >= 180.0F) {
			entity.prevYaw += 360.0F;
		}

		entity.pitch = entity.prevPitch + (entity.pitch - entity.prevPitch) * delta;
		entity.yaw = entity.prevYaw + (entity.yaw - entity.prevYaw) * delta;
	}
}
