package net.minecraft.inventory.slot;

import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.SmeltingRecipeRegistry;
import net.minecraft.util.math.MathHelper;

public class FurnaceOutputSlot extends Slot {
	private final PlayerEntity player;
	private int amount;

	public FurnaceOutputSlot(PlayerEntity playerEntity, Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
		this.player = playerEntity;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int amount) {
		if (this.hasStack()) {
			this.amount = this.amount + Math.min(amount, this.getStack().getCount());
		}

		return super.takeStack(amount);
	}

	@Override
	public ItemStack method_3298(PlayerEntity playerEntity, ItemStack itemStack) {
		this.onCrafted(itemStack);
		super.method_3298(playerEntity, itemStack);
		return itemStack;
	}

	@Override
	protected void onCrafted(ItemStack stack, int amount) {
		this.amount += amount;
		this.onCrafted(stack);
	}

	@Override
	protected void onCrafted(ItemStack stack) {
		stack.onCraft(this.player.world, this.player, this.amount);
		if (!this.player.world.isClient) {
			int i = this.amount;
			float f = SmeltingRecipeRegistry.getInstance().getXp(stack);
			if (f == 0.0F) {
				i = 0;
			} else if (f < 1.0F) {
				int j = MathHelper.floor((float)i * f);
				if (j < MathHelper.ceil((float)i * f) && Math.random() < (double)((float)i * f - (float)j)) {
					j++;
				}

				i = j;
			}

			while (i > 0) {
				int k = ExperienceOrbEntity.roundToOrbSize(i);
				i -= k;
				this.player.world.spawnEntity(new ExperienceOrbEntity(this.player.world, this.player.x, this.player.y + 0.5, this.player.z + 0.5, k));
			}
		}

		this.amount = 0;
		if (stack.getItem() == Items.IRON_INGOT) {
			this.player.incrementStat(AchievementsAndCriterions.ACQUIRE_IRON);
		}

		if (stack.getItem() == Items.COOKED_FISH) {
			this.player.incrementStat(AchievementsAndCriterions.COOK_FISH);
		}
	}
}
