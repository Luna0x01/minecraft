package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class WritableBookItem extends Item {
	public WritableBookItem() {
		this.setMaxCount(1);
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		player.openBookEditScreen(stack);
		player.incrementStat(Stats.USED[Item.getRawId(this)]);
		return stack;
	}

	public static boolean isValid(NbtCompound nbt) {
		if (nbt == null) {
			return false;
		} else if (!nbt.contains("pages", 9)) {
			return false;
		} else {
			NbtList nbtList = nbt.getList("pages", 8);

			for (int i = 0; i < nbtList.size(); i++) {
				String string = nbtList.getString(i);
				if (string == null) {
					return false;
				}

				if (string.length() > 32767) {
					return false;
				}
			}

			return true;
		}
	}
}
