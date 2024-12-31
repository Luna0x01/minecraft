package net.minecraft.inventory.slot;

import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.recipe.RecipeDispatcher;

public class CraftingResultSlot extends Slot {
	private final CraftingInventory craftingInv;
	private final PlayerEntity player;
	private int amount;

	public CraftingResultSlot(PlayerEntity playerEntity, CraftingInventory craftingInventory, Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
		this.player = playerEntity;
		this.craftingInv = craftingInventory;
	}

	@Override
	public boolean canInsert(@Nullable ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int amount) {
		if (this.hasStack()) {
			this.amount = this.amount + Math.min(amount, this.getStack().count);
		}

		return super.takeStack(amount);
	}

	@Override
	protected void onCrafted(ItemStack stack, int amount) {
		this.amount += amount;
		this.onCrafted(stack);
	}

	@Override
	protected void onCrafted(ItemStack stack) {
		if (this.amount > 0) {
			stack.onCraft(this.player.world, this.player, this.amount);
		}

		this.amount = 0;
		if (stack.getItem() == Item.fromBlock(Blocks.CRAFTING_TABLE)) {
			this.player.incrementStat(AchievementsAndCriterions.BUILD_WORK_BENCH);
		}

		if (stack.getItem() instanceof PickaxeItem) {
			this.player.incrementStat(AchievementsAndCriterions.BUILD_PICKAXE);
		}

		if (stack.getItem() == Item.fromBlock(Blocks.FURNACE)) {
			this.player.incrementStat(AchievementsAndCriterions.BUILD_FURNACE);
		}

		if (stack.getItem() instanceof HoeItem) {
			this.player.incrementStat(AchievementsAndCriterions.BUILD_HOE);
		}

		if (stack.getItem() == Items.BREAD) {
			this.player.incrementStat(AchievementsAndCriterions.MAKE_BREAD);
		}

		if (stack.getItem() == Items.CAKE) {
			this.player.incrementStat(AchievementsAndCriterions.BAKE_CAKE);
		}

		if (stack.getItem() instanceof PickaxeItem && ((PickaxeItem)stack.getItem()).getMaterial() != Item.ToolMaterialType.WOOD) {
			this.player.incrementStat(AchievementsAndCriterions.BUILD_BETTER_PICKAXE);
		}

		if (stack.getItem() instanceof SwordItem) {
			this.player.incrementStat(AchievementsAndCriterions.BUILD_SWORD);
		}

		if (stack.getItem() == Item.fromBlock(Blocks.ENCHANTING_TABLE)) {
			this.player.incrementStat(AchievementsAndCriterions.ENCHANTMENTS);
		}

		if (stack.getItem() == Item.fromBlock(Blocks.BOOKSHELF)) {
			this.player.incrementStat(AchievementsAndCriterions.BOOKCASE);
		}
	}

	@Override
	public void onTakeItem(PlayerEntity player, ItemStack stack) {
		this.onCrafted(stack);
		ItemStack[] itemStacks = RecipeDispatcher.getInstance().getRemainders(this.craftingInv, player.world);

		for (int i = 0; i < itemStacks.length; i++) {
			ItemStack itemStack = this.craftingInv.getInvStack(i);
			ItemStack itemStack2 = itemStacks[i];
			if (itemStack != null) {
				this.craftingInv.takeInvStack(i, 1);
				itemStack = this.craftingInv.getInvStack(i);
			}

			if (itemStack2 != null) {
				if (itemStack == null) {
					this.craftingInv.setInvStack(i, itemStack2);
				} else if (ItemStack.equalsIgnoreNbt(itemStack, itemStack2) && ItemStack.equalsIgnoreDamage(itemStack, itemStack2)) {
					itemStack2.count = itemStack2.count + itemStack.count;
					this.craftingInv.setInvStack(i, itemStack2);
				} else if (!this.player.inventory.insertStack(itemStack2)) {
					this.player.dropItem(itemStack2, false);
				}
			}
		}
	}
}
