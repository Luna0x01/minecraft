package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WritableBookItem extends Item {
	public WritableBookItem() {
		this.setMaxCount(1);
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		player.method_3201(itemStack, hand);
		player.incrementStat(Stats.used(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
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
				if (string.length() > 32767) {
					return false;
				}
			}

			return true;
		}
	}
}
