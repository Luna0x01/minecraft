package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ShieldItem extends Item {
	public ShieldItem(Item.Settings settings) {
		super(settings);
		this.addProperty(
			new Identifier("blocking"),
			(itemStack, world, livingEntity) -> livingEntity != null && livingEntity.method_13061() && livingEntity.method_13064() == itemStack ? 1.0F : 0.0F
		);
		DispenserBlock.method_16665(this, ArmorItem.ARMOR_DISPENSER_BEHAVIOR);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return stack.getNbtCompound("BlockEntityTag") != null
			? this.getTranslationKey() + '.' + method_16122(stack).getTranslationKey()
			: super.getTranslationKey(stack);
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext tooltipContext) {
		BannerItem.method_11359(stack, tooltip);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		player.method_13050(hand);
		return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ItemTags.PLANKS.contains(ingredient.getItem()) || super.canRepair(stack, ingredient);
	}

	public static DyeColor method_16122(ItemStack itemStack) {
		return DyeColor.byId(itemStack.getOrCreateNbtCompound("BlockEntityTag").getInt("Base"));
	}
}
