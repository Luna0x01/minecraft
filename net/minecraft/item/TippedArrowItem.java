package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class TippedArrowItem extends ArrowItem {
	@Override
	public ItemStack getDefaultStack() {
		return PotionUtil.setPotion(super.getDefaultStack(), Potions.POISON);
	}

	@Override
	public AbstractArrowEntity method_11358(World world, ItemStack itemStack, LivingEntity livingEntity) {
		ArrowEntity arrowEntity = new ArrowEntity(world, livingEntity);
		arrowEntity.initFromStack(itemStack);
		return arrowEntity;
	}

	@Override
	public void appendToItemGroup(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.canAddTo(group)) {
			for (Potion potion : Potion.REGISTRY) {
				if (!potion.getEffects().isEmpty()) {
					stacks.add(PotionUtil.setPotion(new ItemStack(this), potion));
				}
			}
		}
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<String> tooltip, TooltipContext tooltipContext) {
		PotionUtil.buildTooltip(stack, tooltip, 0.125F);
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		return CommonI18n.translate(PotionUtil.getPotion(stack).method_11414("tipped_arrow.effect."));
	}
}
