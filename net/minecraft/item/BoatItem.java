package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoatItem extends Item {
	private final BoatEntity.Type field_12282;

	public BoatItem(BoatEntity.Type type) {
		this.field_12282 = type;
		this.maxCount = 1;
		this.setItemGroup(ItemGroup.TRANSPORTATION);
		this.setTranslationKey("boat." + type.getName());
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		float f = 1.0F;
		float g = player.prevPitch + (player.pitch - player.prevPitch) * 1.0F;
		float h = player.prevYaw + (player.yaw - player.prevYaw) * 1.0F;
		double d = player.prevX + (player.x - player.prevX) * 1.0;
		double e = player.prevY + (player.y - player.prevY) * 1.0 + (double)player.getEyeHeight();
		double i = player.prevZ + (player.z - player.prevZ) * 1.0;
		Vec3d vec3d = new Vec3d(d, e, i);
		float j = MathHelper.cos(-h * (float) (Math.PI / 180.0) - (float) Math.PI);
		float k = MathHelper.sin(-h * (float) (Math.PI / 180.0) - (float) Math.PI);
		float l = -MathHelper.cos(-g * (float) (Math.PI / 180.0));
		float m = MathHelper.sin(-g * (float) (Math.PI / 180.0));
		float n = k * l;
		float p = j * l;
		double q = 5.0;
		Vec3d vec3d2 = vec3d.add((double)n * 5.0, (double)m * 5.0, (double)p * 5.0);
		BlockHitResult blockHitResult = world.rayTrace(vec3d, vec3d2, true);
		if (blockHitResult == null) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			Vec3d vec3d3 = player.getRotationVector(1.0F);
			boolean bl = false;
			List<Entity> list = world.getEntitiesIn(player, player.getBoundingBox().stretch(vec3d3.x * 5.0, vec3d3.y * 5.0, vec3d3.z * 5.0).expand(1.0));

			for (int r = 0; r < list.size(); r++) {
				Entity entity = (Entity)list.get(r);
				if (entity.collides()) {
					Box box = entity.getBoundingBox().expand((double)entity.getTargetingMargin());
					if (box.contains(vec3d)) {
						bl = true;
					}
				}
			}

			if (bl) {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			} else if (blockHitResult.type != BlockHitResult.Type.BLOCK) {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			} else {
				Block block = world.getBlockState(blockHitResult.getBlockPos()).getBlock();
				boolean bl2 = block == Blocks.WATER || block == Blocks.FLOWING_WATER;
				BoatEntity boatEntity = new BoatEntity(world, blockHitResult.pos.x, bl2 ? blockHitResult.pos.y - 0.12 : blockHitResult.pos.y, blockHitResult.pos.z);
				boatEntity.setBoatType(this.field_12282);
				boatEntity.yaw = player.yaw;
				if (!world.doesBoxCollide(boatEntity, boatEntity.getBoundingBox().expand(-0.1)).isEmpty()) {
					return new TypedActionResult<>(ActionResult.FAIL, itemStack);
				} else {
					if (!world.isClient) {
						world.spawnEntity(boatEntity);
					}

					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					player.incrementStat(Stats.used(this));
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			}
		}
	}
}
