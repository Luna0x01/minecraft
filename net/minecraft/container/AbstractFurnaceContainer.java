package net.minecraft.container;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.FurnaceInputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public abstract class AbstractFurnaceContainer extends CraftingContainer<Inventory> {
	private final Inventory inventory;
	private final PropertyDelegate propertyDelegate;
	protected final World world;
	private final RecipeType<? extends AbstractCookingRecipe> recipeType;

	protected AbstractFurnaceContainer(
		ContainerType<?> containerType, RecipeType<? extends AbstractCookingRecipe> recipeType, int i, PlayerInventory playerInventory
	) {
		this(containerType, recipeType, i, playerInventory, new BasicInventory(3), new ArrayPropertyDelegate(4));
	}

	protected AbstractFurnaceContainer(
		ContainerType<?> containerType,
		RecipeType<? extends AbstractCookingRecipe> recipeType,
		int i,
		PlayerInventory playerInventory,
		Inventory inventory,
		PropertyDelegate propertyDelegate
	) {
		super(containerType, i);
		this.recipeType = recipeType;
		checkContainerSize(inventory, 3);
		checkContainerDataCount(propertyDelegate, 4);
		this.inventory = inventory;
		this.propertyDelegate = propertyDelegate;
		this.world = playerInventory.player.world;
		this.addSlot(new Slot(inventory, 0, 56, 17));
		this.addSlot(new FurnaceFuelSlot(this, inventory, 1, 56, 53));
		this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 2, 116, 35));

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
		}

		this.addProperties(propertyDelegate);
	}

	@Override
	public void populateRecipeFinder(RecipeFinder recipeFinder) {
		if (this.inventory instanceof RecipeInputProvider) {
			((RecipeInputProvider)this.inventory).provideRecipeInputs(recipeFinder);
		}
	}

	@Override
	public void clearCraftingSlots() {
		this.inventory.clear();
	}

	@Override
	public void fillInputSlots(boolean bl, Recipe<?> recipe, ServerPlayerEntity serverPlayerEntity) {
		new FurnaceInputSlotFiller<>(this).fillInputSlots(serverPlayerEntity, (Recipe<Inventory>)recipe, bl);
	}

	@Override
	public boolean matches(Recipe<? super Inventory> recipe) {
		return recipe.matches(this.inventory, this.world);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 2;
	}

	@Override
	public int getCraftingWidth() {
		return 1;
	}

	@Override
	public int getCraftingHeight() {
		return 1;
	}

	@Override
	public int getCraftingSlotCount() {
		return 3;
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return this.inventory.canPlayerUseInv(playerEntity);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (i == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (i != 1 && i != 0) {
				if (this.isSmeltable(itemStack2)) {
					if (!this.insertItem(itemStack2, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (this.isFuel(itemStack2)) {
					if (!this.insertItem(itemStack2, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 3 && i < 30) {
					if (!this.insertItem(itemStack2, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (i >= 30 && i < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(playerEntity, itemStack2);
		}

		return itemStack;
	}

	protected boolean isSmeltable(ItemStack itemStack) {
		return this.world.getRecipeManager().getFirstMatch(this.recipeType, new BasicInventory(itemStack), this.world).isPresent();
	}

	protected boolean isFuel(ItemStack itemStack) {
		return AbstractFurnaceBlockEntity.canUseAsFuel(itemStack);
	}

	public int getCookProgress() {
		int i = this.propertyDelegate.get(2);
		int j = this.propertyDelegate.get(3);
		return j != 0 && i != 0 ? i * 24 / j : 0;
	}

	public int getFuelProgress() {
		int i = this.propertyDelegate.get(1);
		if (i == 0) {
			i = 200;
		}

		return this.propertyDelegate.get(0) * 13 / i;
	}

	public boolean isBurning() {
		return this.propertyDelegate.get(0) > 0;
	}
}
