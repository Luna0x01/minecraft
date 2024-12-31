package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WrittenBookItem extends Item {
	public WrittenBookItem(Item.Settings settings) {
		super(settings);
	}

	public static boolean isValid(@Nullable CompoundTag tag) {
		if (!WritableBookItem.isValid(tag)) {
			return false;
		} else if (!tag.contains("title", 8)) {
			return false;
		} else {
			String string = tag.getString("title");
			return string.length() > 32 ? false : tag.contains("author", 8);
		}
	}

	public static int getGeneration(ItemStack stack) {
		return stack.getTag().getInt("generation");
	}

	public static int getPageCount(ItemStack stack) {
		CompoundTag compoundTag = stack.getTag();
		return compoundTag != null ? compoundTag.getList("pages", 8).size() : 0;
	}

	@Override
	public Text getName(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag compoundTag = stack.getTag();
			String string = compoundTag.getString("title");
			if (!ChatUtil.isEmpty(string)) {
				return new LiteralText(string);
			}
		}

		return super.getName(stack);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (stack.hasTag()) {
			CompoundTag compoundTag = stack.getTag();
			String string = compoundTag.getString("author");
			if (!ChatUtil.isEmpty(string)) {
				tooltip.add(new TranslatableText("book.byAuthor", string).formatted(Formatting.GRAY));
			}

			tooltip.add(new TranslatableText("book.generation." + compoundTag.getInt("generation")).formatted(Formatting.GRAY));
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		//
		// Bytecode:
		// 00: aload 1
		// 01: invokevirtual net/minecraft/item/ItemUsageContext.getWorld ()Lnet/minecraft/world/World;
		// 04: astore 2
		// 05: aload 1
		// 06: invokevirtual net/minecraft/item/ItemUsageContext.getBlockPos ()Lnet/minecraft/util/math/BlockPos;
		// 09: astore 3
		// 0a: aload 2
		// 0b: aload 3
		// 0c: invokevirtual net/minecraft/world/World.getBlockState (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;
		// 0f: astore 4
		// 11: aload 4
		// 13: getstatic net/minecraft/block/Blocks.LECTERN Lnet/minecraft/block/Block;
		// 16: invokevirtual net/minecraft/block/BlockState.isOf (Lnet/minecraft/block/Block;)Z
		// 19: ifeq 38
		// 1c: aload 2
		// 1d: aload 3
		// 1e: aload 4
		// 20: aload 1
		// 21: invokevirtual net/minecraft/item/ItemUsageContext.getStack ()Lnet/minecraft/item/ItemStack;
		// 24: invokestatic net/minecraft/block/LecternBlock.putBookIfAbsent (Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/item/ItemStack;)Z
		// 27: ifeq 34
		// 2a: aload 2
		// 2b: getfield net/minecraft/world/World.isClient Z
		// 2e: invokestatic net/minecraft/util/ActionResult.success (Z)Lnet/minecraft/util/ActionResult;
		// 31: goto 37
		// 34: getstatic net/minecraft/util/ActionResult.PASS Lnet/minecraft/util/ActionResult;
		// 37: areturn
		// 38: getstatic net/minecraft/util/ActionResult.PASS Lnet/minecraft/util/ActionResult;
		// 3b: areturn
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		user.openEditBookScreen(itemStack, hand);
		user.incrementStat(Stats.USED.getOrCreateStat(this));
		return TypedActionResult.success(itemStack, world.isClient());
	}

	public static boolean resolve(ItemStack book, @Nullable ServerCommandSource commandSource, @Nullable PlayerEntity player) {
		CompoundTag compoundTag = book.getTag();
		if (compoundTag != null && !compoundTag.getBoolean("resolved")) {
			compoundTag.putBoolean("resolved", true);
			if (!isValid(compoundTag)) {
				return false;
			} else {
				ListTag listTag = compoundTag.getList("pages", 8);

				for (int i = 0; i < listTag.size(); i++) {
					String string = listTag.getString(i);

					Text text2;
					try {
						text2 = Text.Serializer.fromLenientJson(string);
						text2 = Texts.parse(commandSource, text2, player, 0);
					} catch (Exception var9) {
						text2 = new LiteralText(string);
					}

					listTag.set(i, (Tag)StringTag.of(Text.Serializer.toJson(text2)));
				}

				compoundTag.put("pages", listTag);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return true;
	}
}
