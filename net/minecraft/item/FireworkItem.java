package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.FireworkRocketEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FireworkItem extends Item {
	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (!world.isClient) {
			FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(
				world, (double)((float)pos.getX() + facingX), (double)((float)pos.getY() + facingY), (double)((float)pos.getZ() + facingZ), itemStack
			);
			world.spawnEntity(fireworkRocketEntity);
			if (!player.abilities.creativeMode) {
				itemStack.count--;
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		if (stack.hasNbt()) {
			NbtCompound nbtCompound = stack.getNbt().getCompound("Fireworks");
			if (nbtCompound != null) {
				if (nbtCompound.contains("Flight", 99)) {
					lines.add(CommonI18n.translate("item.fireworks.flight") + " " + nbtCompound.getByte("Flight"));
				}

				NbtList nbtList = nbtCompound.getList("Explosions", 10);
				if (nbtList != null && nbtList.size() > 0) {
					for (int i = 0; i < nbtList.size(); i++) {
						NbtCompound nbtCompound2 = nbtList.getCompound(i);
						List<String> list = Lists.newArrayList();
						FireworkChargeItem.addExplosionInfo(nbtCompound2, list);
						if (list.size() > 0) {
							for (int j = 1; j < list.size(); j++) {
								list.set(j, "  " + (String)list.get(j));
							}

							lines.addAll(list);
						}
					}
				}
			}
		}
	}
}
