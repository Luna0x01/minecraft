package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.FireworkRocketEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FireworkItem extends Item {
	@Override
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		if (!world.isClient) {
			ItemStack itemStack = player.getStackInHand(hand);
			FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(
				world, (double)((float)pos.getX() + x), (double)((float)pos.getY() + y), (double)((float)pos.getZ() + z), itemStack
			);
			world.spawnEntity(fireworkRocketEntity);
			if (!player.abilities.creativeMode) {
				itemStack.decrement(1);
			}
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		if (player.method_13055()) {
			ItemStack itemStack = player.getStackInHand(hand);
			if (!world.isClient) {
				FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(world, itemStack, player);
				world.spawnEntity(fireworkRocketEntity);
				if (!player.abilities.creativeMode) {
					itemStack.decrement(1);
				}
			}

			return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
		} else {
			return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		NbtCompound nbtCompound = stack.getNbtCompound("Fireworks");
		if (nbtCompound != null) {
			if (nbtCompound.contains("Flight", 99)) {
				lines.add(CommonI18n.translate("item.fireworks.flight") + " " + nbtCompound.getByte("Flight"));
			}

			NbtList nbtList = nbtCompound.getList("Explosions", 10);
			if (!nbtList.isEmpty()) {
				for (int i = 0; i < nbtList.size(); i++) {
					NbtCompound nbtCompound2 = nbtList.getCompound(i);
					List<String> list = Lists.newArrayList();
					FireworkChargeItem.addExplosionInfo(nbtCompound2, list);
					if (!list.isEmpty()) {
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
