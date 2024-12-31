package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class ProjectileUtil {
	public static BlockHitResult method_13286(Entity entity, boolean bl, boolean bl2, Entity entity2) {
		double d = entity.x;
		double e = entity.y;
		double f = entity.z;
		double g = entity.velocityX;
		double h = entity.velocityY;
		double i = entity.velocityZ;
		World world = entity.world;
		Vec3d vec3d = new Vec3d(d, e, f);
		Vec3d vec3d2 = new Vec3d(d + g, e + h, f + i);
		BlockHitResult blockHitResult = world.rayTrace(vec3d, vec3d2, false, true, false);
		if (bl) {
			if (blockHitResult != null) {
				vec3d2 = new Vec3d(blockHitResult.pos.x, blockHitResult.pos.y, blockHitResult.pos.z);
			}

			Entity entity3 = null;
			List<Entity> list = world.getEntitiesIn(entity, entity.getBoundingBox().stretch(g, h, i).expand(1.0));
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
