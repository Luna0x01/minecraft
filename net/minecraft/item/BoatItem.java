package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoatItem extends Item {
	public BoatItem() {
		this.maxCount = 1;
		this.setItemGroup(ItemGroup.TRANSPORTATION);
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		float f = 1.0F;
		float g = player.prevPitch + (player.pitch - player.prevPitch) * f;
		float h = player.prevYaw + (player.yaw - player.prevYaw) * f;
		double d = player.prevX + (player.x - player.prevX) * (double)f;
		double e = player.prevY + (player.y - player.prevY) * (double)f + (double)player.getEyeHeight();
		double i = player.prevZ + (player.z - player.prevZ) * (double)f;
		Vec3d vec3d = new Vec3d(d, e, i);
		float j = MathHelper.cos(-h * (float) (Math.PI / 180.0) - (float) Math.PI);
		float k = MathHelper.sin(-h * (float) (Math.PI / 180.0) - (float) Math.PI);
		float l = -MathHelper.cos(-g * (float) (Math.PI / 180.0));
		float m = MathHelper.sin(-g * (float) (Math.PI / 180.0));
		float n = k * l;
		float p = j * l;
		double q = 5.0;
		Vec3d vec3d2 = vec3d.add((double)n * q, (double)m * q, (double)p * q);
		BlockHitResult blockHitResult = world.rayTrace(vec3d, vec3d2, true);
		if (blockHitResult == null) {
			return stack;
		} else {
			Vec3d vec3d3 = player.getRotationVector(f);
			boolean bl = false;
			float r = 1.0F;
			List<Entity> list = world.getEntitiesIn(
				player, player.getBoundingBox().stretch(vec3d3.x * q, vec3d3.y * q, vec3d3.z * q).expand((double)r, (double)r, (double)r)
			);

			for (int s = 0; s < list.size(); s++) {
				Entity entity = (Entity)list.get(s);
				if (entity.collides()) {
					float t = entity.getTargetingMargin();
					Box box = entity.getBoundingBox().expand((double)t, (double)t, (double)t);
					if (box.contains(vec3d)) {
						bl = true;
					}
				}
			}

			if (bl) {
				return stack;
			} else {
				if (blockHitResult.type == BlockHitResult.Type.BLOCK) {
					BlockPos blockPos = blockHitResult.getBlockPos();
					if (world.getBlockState(blockPos).getBlock() == Blocks.SNOW_LAYER) {
						blockPos = blockPos.down();
					}

					BoatEntity boatEntity = new BoatEntity(
						world, (double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 1.0F), (double)((float)blockPos.getZ() + 0.5F)
					);
					boatEntity.yaw = (float)(((MathHelper.floor((double)(player.yaw * 4.0F / 360.0F) + 0.5) & 3) - 1) * 90);
					if (!world.doesBoxCollide(boatEntity, boatEntity.getBoundingBox().expand(-0.1, -0.1, -0.1)).isEmpty()) {
						return stack;
					}

					if (!world.isClient) {
						world.spawnEntity(boatEntity);
					}

					if (!player.abilities.creativeMode) {
						stack.count--;
					}

					player.incrementStat(Stats.USED[Item.getRawId(this)]);
				}

				return stack;
			}
		}
	}
}
