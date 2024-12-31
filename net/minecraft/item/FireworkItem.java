package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.TooltipContext;
import net.minecraft.entity.FireworkRocketEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireworkItem extends Item {
	public FireworkItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		if (!world.isClient) {
			BlockPos blockPos = itemUsageContext.getBlockPos();
			ItemStack itemStack = itemUsageContext.getItemStack();
			FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(
				world,
				(double)((float)blockPos.getX() + itemUsageContext.method_16152()),
				(double)((float)blockPos.getY() + itemUsageContext.method_16153()),
				(double)((float)blockPos.getZ() + itemUsageContext.method_16154()),
				itemStack
			);
			world.method_3686(fireworkRocketEntity);
			itemStack.decrement(1);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		if (player.method_13055()) {
			ItemStack itemStack = player.getStackInHand(hand);
			if (!world.isClient) {
				FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(world, itemStack, player);
				world.method_3686(fireworkRocketEntity);
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
	public void appendTooltips(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext tooltipContext) {
		NbtCompound nbtCompound = stack.getNbtCompound("Fireworks");
		if (nbtCompound != null) {
			if (nbtCompound.contains("Flight", 99)) {
				tooltip.add(
					new TranslatableText("item.minecraft.firework_rocket.flight").append(" ").append(String.valueOf(nbtCompound.getByte("Flight"))).formatted(Formatting.GRAY)
				);
			}

			NbtList nbtList = nbtCompound.getList("Explosions", 10);
			if (!nbtList.isEmpty()) {
				for (int i = 0; i < nbtList.size(); i++) {
					NbtCompound nbtCompound2 = nbtList.getCompound(i);
					List<Text> list = Lists.newArrayList();
					FireworkChargeItem.method_16059(nbtCompound2, list);
					if (!list.isEmpty()) {
						for (int j = 1; j < list.size(); j++) {
							list.set(j, new LiteralText("  ").append((Text)list.get(j)).formatted(Formatting.GRAY));
						}

						tooltip.addAll(list);
					}
				}
			}
		}
	}

	public static enum class_3551 {
		SMALL_BALL(0, "small_ball"),
		LARGE_BALL(1, "large_ball"),
		STAR(2, "star"),
		CREEPER(3, "creeper"),
		BURST(4, "burst");

		private static final FireworkItem.class_3551[] field_17174 = (FireworkItem.class_3551[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(arg -> arg.field_17175))
			.toArray(FireworkItem.class_3551[]::new);
		private final int field_17175;
		private final String field_17176;

		private class_3551(int j, String string2) {
			this.field_17175 = j;
			this.field_17176 = string2;
		}

		public int method_16053() {
			return this.field_17175;
		}

		public String method_16056() {
			return this.field_17176;
		}

		public static FireworkItem.class_3551 method_16054(int i) {
			return i >= 0 && i < field_17174.length ? field_17174[i] : SMALL_BALL;
		}
	}
}
