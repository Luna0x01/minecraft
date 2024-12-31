package net.minecraft.container;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StonecutterContainer extends Container {
	static final ImmutableList<Item> INGREDIENTS = ImmutableList.of(
		Items.STONE,
		Items.SANDSTONE,
		Items.RED_SANDSTONE,
		Items.QUARTZ_BLOCK,
		Items.COBBLESTONE,
		Items.STONE_BRICKS,
		Items.BRICKS,
		Items.NETHER_BRICKS,
		Items.RED_NETHER_BRICKS,
		Items.PURPUR_BLOCK,
		Items.PRISMARINE,
		Items.PRISMARINE_BRICKS,
		new Item[]{
			Items.DARK_PRISMARINE,
			Items.ANDESITE,
			Items.POLISHED_ANDESITE,
			Items.GRANITE,
			Items.POLISHED_GRANITE,
			Items.DIORITE,
			Items.POLISHED_DIORITE,
			Items.MOSSY_STONE_BRICKS,
			Items.MOSSY_COBBLESTONE,
			Items.SMOOTH_SANDSTONE,
			Items.SMOOTH_RED_SANDSTONE,
			Items.SMOOTH_QUARTZ,
			Items.END_STONE,
			Items.END_STONE_BRICKS,
			Items.SMOOTH_STONE,
			Items.CUT_SANDSTONE,
			Items.CUT_RED_SANDSTONE
		}
	);
	private final BlockContext context;
	private final Property selectedRecipe = Property.create();
	private final World world;
	private List<StonecuttingRecipe> availableRecipes = Lists.newArrayList();
	private ItemStack inputStack = ItemStack.EMPTY;
	private long lastTakeTime;
	final Slot inputSlot;
	final Slot outputSlot;
	private Runnable contentsChangedListener = () -> {
	};
	public final Inventory inventory = new BasicInventory(1) {
		@Override
		public void markDirty() {
			super.markDirty();
			StonecutterContainer.this.onContentChanged(this);
			StonecutterContainer.this.contentsChangedListener.run();
		}
	};
	private final CraftingResultInventory field_19173 = new CraftingResultInventory();

	public StonecutterContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, BlockContext.EMPTY);
	}

	public StonecutterContainer(int i, PlayerInventory playerInventory, BlockContext blockContext) {
		super(ContainerType.field_17625, i);
		this.context = blockContext;
		this.world = playerInventory.player.world;
		this.inputSlot = this.addSlot(new Slot(this.inventory, 0, 20, 33));
		this.outputSlot = this.addSlot(new Slot(this.field_19173, 1, 143, 33) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return false;
			}

			@Override
			public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
				ItemStack itemStack2 = StonecutterContainer.this.inputSlot.takeStack(1);
				if (!itemStack2.isEmpty()) {
					StonecutterContainer.this.populateResult();
				}

				itemStack.getItem().onCraft(itemStack, playerEntity.world, playerEntity);
				blockContext.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
					long l = world.getTime();
					if (StonecutterContainer.this.lastTakeTime != l) {
						world.playSound(null, blockPos, SoundEvents.field_17710, SoundCategory.field_15245, 1.0F, 1.0F);
						StonecutterContainer.this.lastTakeTime = l;
					}
				}));
				return super.onTakeItem(playerEntity, itemStack);
			}
		});

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
		}

		this.addProperty(this.selectedRecipe);
	}

	public int getSelectedRecipe() {
		return this.selectedRecipe.get();
	}

	public List<StonecuttingRecipe> getAvailableRecipes() {
		return this.availableRecipes;
	}

	public int getAvailableRecipeCount() {
		return this.availableRecipes.size();
	}

	public boolean canCraft() {
		return this.inputSlot.hasStack() && !this.availableRecipes.isEmpty();
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return canUse(this.context, playerEntity, Blocks.field_16335);
	}

	@Override
	public boolean onButtonClick(PlayerEntity playerEntity, int i) {
		if (i >= 0 && i < this.availableRecipes.size()) {
			this.selectedRecipe.set(i);
			this.populateResult();
		}

		return true;
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		ItemStack itemStack = this.inputSlot.getStack();
		if (itemStack.getItem() != this.inputStack.getItem()) {
			this.inputStack = itemStack.copy();
			this.updateInput(inventory, itemStack);
		}
	}

	private void updateInput(Inventory inventory, ItemStack itemStack) {
		this.availableRecipes.clear();
		this.selectedRecipe.set(-1);
		this.outputSlot.setStack(ItemStack.EMPTY);
		if (!itemStack.isEmpty()) {
			this.availableRecipes = this.world.getRecipeManager().getAllMatches(RecipeType.field_17641, inventory, this.world);
		}
	}

	private void populateResult() {
		if (!this.availableRecipes.isEmpty()) {
			StonecuttingRecipe stonecuttingRecipe = (StonecuttingRecipe)this.availableRecipes.get(this.selectedRecipe.get());
			this.outputSlot.setStack(stonecuttingRecipe.craft(this.inventory));
		} else {
			this.outputSlot.setStack(ItemStack.EMPTY);
		}

		this.sendContentUpdates();
	}

	@Override
	public ContainerType<?> getType() {
		return ContainerType.field_17625;
	}

	public void setContentsChangedListener(Runnable runnable) {
		this.contentsChangedListener = runnable;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack itemStack, Slot slot) {
		return false;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			Item item = itemStack2.getItem();
			itemStack = itemStack2.copy();
			if (i == 1) {
				item.onCraft(itemStack2, playerEntity.world, playerEntity);
				if (!this.insertItem(itemStack2, 2, 38, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (i == 0) {
				if (!this.insertItem(itemStack2, 2, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (INGREDIENTS.contains(item)) {
				if (!this.insertItem(itemStack2, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i >= 2 && i < 29) {
				if (!this.insertItem(itemStack2, 29, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i >= 29 && i < 38 && !this.insertItem(itemStack2, 2, 29, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			}

			slot.markDirty();
			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(playerEntity, itemStack2);
			this.sendContentUpdates();
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.field_19173.removeInvStack(1);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.dropInventory(playerEntity, playerEntity.world, this.inventory)));
	}
}
