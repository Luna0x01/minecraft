package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WrittenBookItem extends Item {
	public WrittenBookItem() {
		this.setMaxCount(1);
	}

	public static boolean isValid(NbtCompound nbt) {
		if (!WritableBookItem.isValid(nbt)) {
			return false;
		} else if (!nbt.contains("title", 8)) {
			return false;
		} else {
			String string = nbt.getString("title");
			return string != null && string.length() <= 32 ? nbt.contains("author", 8) : false;
		}
	}

	public static int getGeneration(ItemStack stack) {
		return stack.getNbt().getInt("generation");
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		if (stack.hasNbt()) {
			NbtCompound nbtCompound = stack.getNbt();
			String string = nbtCompound.getString("title");
			if (!ChatUtil.isEmpty(string)) {
				return string;
			}
		}

		return super.getDisplayName(stack);
	}

	@Override
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		if (stack.hasNbt()) {
			NbtCompound nbtCompound = stack.getNbt();
			String string = nbtCompound.getString("author");
			if (!ChatUtil.isEmpty(string)) {
				lines.add(Formatting.GRAY + CommonI18n.translate("book.byAuthor", string));
			}

			lines.add(Formatting.GRAY + CommonI18n.translate("book.generation." + nbtCompound.getInt("generation")));
		}
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!world.isClient) {
			this.resolveContents(itemStack, player);
		}

		player.method_3201(itemStack, hand);
		player.incrementStat(Stats.used(this));
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}

	private void resolveContents(ItemStack stack, PlayerEntity player) {
		if (stack.getNbt() != null) {
			NbtCompound nbtCompound = stack.getNbt();
			if (!nbtCompound.getBoolean("resolved")) {
				nbtCompound.putBoolean("resolved", true);
				if (isValid(nbtCompound)) {
					NbtList nbtList = nbtCompound.getList("pages", 8);

					for (int i = 0; i < nbtList.size(); i++) {
						String string = nbtList.getString(i);

						Text text2;
						try {
							text2 = Text.Serializer.lenientDeserializeText(string);
							text2 = ChatSerializer.process(player, text2, player);
						} catch (Exception var9) {
							text2 = new LiteralText(string);
						}

						nbtList.set(i, new NbtString(Text.Serializer.serialize(text2)));
					}

					nbtCompound.put("pages", nbtList);
					if (player instanceof ServerPlayerEntity && player.getMainHandStack() == stack) {
						Slot slot = player.openScreenHandler.getSlot(player.inventory, player.inventory.selectedSlot);
						((ServerPlayerEntity)player).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(0, slot.id, stack));
					}
				}
			}
		}
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack stack) {
		return true;
	}
}
