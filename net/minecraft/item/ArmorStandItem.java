package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ArmorStandItem extends Item {
	public ArmorStandItem() {
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (direction == Direction.DOWN) {
			return false;
		} else {
			boolean bl = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
			BlockPos blockPos = bl ? pos : pos.offset(direction);
			if (!player.canModify(blockPos, direction, itemStack)) {
				return false;
			} else {
				BlockPos blockPos2 = blockPos.up();
				boolean bl2 = !world.isAir(blockPos) && !world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos);
				bl2 |= !world.isAir(blockPos2) && !world.getBlockState(blockPos2).getBlock().isReplaceable(world, blockPos2);
				if (bl2) {
					return false;
				} else {
					double d = (double)blockPos.getX();
					double e = (double)blockPos.getY();
					double f = (double)blockPos.getZ();
					List<Entity> list = world.getEntitiesIn(null, Box.createNewBox(d, e, f, d + 1.0, e + 2.0, f + 1.0));
					if (list.size() > 0) {
						return false;
					} else {
						if (!world.isClient) {
							world.setAir(blockPos);
							world.setAir(blockPos2);
							ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, d + 0.5, e, f + 0.5);
							float g = (float)MathHelper.floor((MathHelper.wrapDegrees(player.yaw - 180.0F) + 22.5F) / 45.0F) * 45.0F;
							armorStandEntity.refreshPositionAndAngles(d + 0.5, e, f + 0.5, g, 0.0F);
							this.place(armorStandEntity, world.random);
							NbtCompound nbtCompound = itemStack.getNbt();
							if (nbtCompound != null && nbtCompound.contains("EntityTag", 10)) {
								NbtCompound nbtCompound2 = new NbtCompound();
								armorStandEntity.saveToNbt(nbtCompound2);
								nbtCompound2.copyFrom(nbtCompound.getCompound("EntityTag"));
								armorStandEntity.fromNbt(nbtCompound2);
							}

							world.spawnEntity(armorStandEntity);
						}

						itemStack.count--;
						return true;
					}
				}
			}
		}
	}

	private void place(ArmorStandEntity armorStand, Random random) {
		EulerAngle eulerAngle = armorStand.getHeadAngle();
		float f = random.nextFloat() * 5.0F;
		float g = random.nextFloat() * 20.0F - 10.0F;
		EulerAngle eulerAngle2 = new EulerAngle(eulerAngle.getPitch() + f, eulerAngle.getYaw() + g, eulerAngle.getRoll());
		armorStand.setHeadAngle(eulerAngle2);
		eulerAngle = armorStand.getBodyAngle();
		f = random.nextFloat() * 10.0F - 5.0F;
		eulerAngle2 = new EulerAngle(eulerAngle.getPitch(), eulerAngle.getYaw() + f, eulerAngle.getRoll());
		armorStand.setBodyAngle(eulerAngle2);
	}
}
