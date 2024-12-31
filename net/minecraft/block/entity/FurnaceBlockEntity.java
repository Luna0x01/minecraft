package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.class_3175;
import net.minecraft.class_3537;
import net.minecraft.class_3538;
import net.minecraft.class_3584;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.slot.FurnaceFuelSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FurnaceBlockEntity extends LockableContainerBlockEntity implements SidedInventory, class_3537, class_3538, Tickable {
	private static final int[] inputs = new int[]{0};
	private static final int[] outputs = new int[]{2, 1};
	private static final int[] fuelInputs = new int[]{1};
	private DefaultedList<ItemStack> field_15154 = DefaultedList.ofSize(3, ItemStack.EMPTY);
	private int fuelTime;
	private int totalFuelTime;
	private int cookTime;
	private int totalCookTime;
	private Text field_18635;
	private final Map<Identifier, Integer> field_18636 = Maps.newHashMap();

	private static void method_16814(Map<Item, Integer> map, Tag<Item> tag, int i) {
		for (Item item : tag.values()) {
			map.put(item, i);
		}
	}

	private static void method_16813(Map<Item, Integer> map, Itemable itemable, int i) {
		map.put(itemable.getItem(), i);
	}

	public static Map<Item, Integer> method_16817() {
		Map<Item, Integer> map = Maps.newLinkedHashMap();
		method_16813(map, Items.LAVA_BUCKET, 20000);
		method_16813(map, Blocks.COAL_BLOCK, 16000);
		method_16813(map, Items.BLAZE_ROD, 2400);
		method_16813(map, Items.COAL, 1600);
		method_16813(map, Items.CHARCOAL, 1600);
		method_16814(map, ItemTags.LOGS, 300);
		method_16814(map, ItemTags.PLANKS, 300);
		method_16814(map, ItemTags.WOODEN_STAIRS, 300);
		method_16814(map, ItemTags.WOODEN_SLABS, 150);
		method_16814(map, ItemTags.WOODEN_TRAPDOORS, 300);
		method_16814(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
		method_16813(map, Blocks.OAK_FENCE, 300);
		method_16813(map, Blocks.BIRCH_FENCE, 300);
		method_16813(map, Blocks.SPRUCE_FENCE, 300);
		method_16813(map, Blocks.JUNGLE_FENCE, 300);
		method_16813(map, Blocks.DARK_OAK_FENCE, 300);
		method_16813(map, Blocks.ACACIA_FENCE, 300);
		method_16813(map, Blocks.OAK_FENCE_GATE, 300);
		method_16813(map, Blocks.BIRCH_FENCE_GATE, 300);
		method_16813(map, Blocks.SPRUCE_FENCE_GATE, 300);
		method_16813(map, Blocks.JUNGLE_FENCE_GATE, 300);
		method_16813(map, Blocks.DARK_OAK_FENCE_GATE, 300);
		method_16813(map, Blocks.ACACIA_FENCE_GATE, 300);
		method_16813(map, Blocks.NOTE_BLOCK, 300);
		method_16813(map, Blocks.BOOKSHELF, 300);
		method_16813(map, Blocks.JUKEBOX, 300);
		method_16813(map, Blocks.CHEST, 300);
		method_16813(map, Blocks.TRAPPED_CHEST, 300);
		method_16813(map, Blocks.CRAFTING_TABLE, 300);
		method_16813(map, Blocks.DAYLIGHT_DETECTOR, 300);
		method_16814(map, ItemTags.BANNERS, 300);
		method_16813(map, Items.BOW, 300);
		method_16813(map, Items.FISHING_ROD, 300);
		method_16813(map, Blocks.LADDER, 300);
		method_16813(map, Items.SIGN, 200);
		method_16813(map, Items.WOODEN_SHOVEL, 200);
		method_16813(map, Items.WOODEN_SWORD, 200);
		method_16813(map, Items.WOODEN_HOE, 200);
		method_16813(map, Items.WOODEN_AXE, 200);
		method_16813(map, Items.WOODEN_PICKAXE, 200);
		method_16814(map, ItemTags.WOODEN_DOORS, 200);
		method_16814(map, ItemTags.BOATS, 200);
		method_16814(map, ItemTags.WOOL, 100);
		method_16814(map, ItemTags.WOODEN_BUTTONS, 100);
		method_16813(map, Items.STICK, 100);
		method_16814(map, ItemTags.SAPLINGS, 100);
		method_16813(map, Items.BOWL, 100);
		method_16814(map, ItemTags.CARPETS, 67);
		method_16813(map, Blocks.DRIED_KELP_BLOCK, 4001);
		return map;
	}

	public FurnaceBlockEntity() {
		super(BlockEntityType.FURNACE);
	}

	@Override
	public int getInvSize() {
		return this.field_15154.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.field_15154) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return this.field_15154.get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		return class_2960.method_13926(this.field_15154, slot, amount);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return class_2960.method_13925(this.field_15154, slot);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		ItemStack itemStack = this.field_15154.get(slot);
		boolean bl = !stack.isEmpty() && stack.equalsIgnoreNbt(itemStack) && ItemStack.equalsIgnoreDamage(stack, itemStack);
		this.field_15154.set(slot, stack);
		if (stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
		}

		if (slot == 0 && !bl) {
			this.totalCookTime = this.method_16819();
			this.cookTime = 0;
			this.markDirty();
		}
	}

	@Override
	public Text method_15540() {
		return (Text)(this.field_18635 != null ? this.field_18635 : new TranslatableText("container.furnace"));
	}

	@Override
	public boolean hasCustomName() {
		return this.field_18635 != null;
	}

	@Nullable
	@Override
	public Text method_15541() {
		return this.field_18635;
	}

	public void method_16812(@Nullable Text text) {
		this.field_18635 = text;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.field_15154 = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		class_2960.method_13927(nbt, this.field_15154);
		this.fuelTime = nbt.getShort("BurnTime");
		this.cookTime = nbt.getShort("CookTime");
		this.totalCookTime = nbt.getShort("CookTimeTotal");
		this.totalFuelTime = getBurnTime(this.field_15154.get(1));
		int i = nbt.getShort("RecipesUsedSize");

		for (int j = 0; j < i; j++) {
			Identifier identifier = new Identifier(nbt.getString("RecipeLocation" + j));
			int k = nbt.getInt("RecipeAmount" + j);
			this.field_18636.put(identifier, k);
		}

		if (nbt.contains("CustomName", 8)) {
			this.field_18635 = Text.Serializer.deserializeText(nbt.getString("CustomName"));
		}
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putShort("BurnTime", (short)this.fuelTime);
		nbt.putShort("CookTime", (short)this.cookTime);
		nbt.putShort("CookTimeTotal", (short)this.totalCookTime);
		class_2960.method_13923(nbt, this.field_15154);
		nbt.putShort("RecipesUsedSize", (short)this.field_18636.size());
		int i = 0;

		for (Entry<Identifier, Integer> entry : this.field_18636.entrySet()) {
			nbt.putString("RecipeLocation" + i, ((Identifier)entry.getKey()).toString());
			nbt.putInt("RecipeAmount" + i, (Integer)entry.getValue());
			i++;
		}

		if (this.field_18635 != null) {
			nbt.putString("CustomName", Text.Serializer.serialize(this.field_18635));
		}

		return nbt;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	private boolean isFueled() {
		return this.fuelTime > 0;
	}

	public static boolean isLit(Inventory inventory) {
		return inventory.getProperty(0) > 0;
	}

	@Override
	public void tick() {
		boolean bl = this.isFueled();
		boolean bl2 = false;
		if (this.isFueled()) {
			this.fuelTime--;
		}

		if (!this.world.isClient) {
			ItemStack itemStack = this.field_15154.get(1);
			if (this.isFueled() || !itemStack.isEmpty() && !this.field_15154.get(0).isEmpty()) {
				RecipeType recipeType = this.world.method_16313().method_16209(this, this.world);
				if (!this.isFueled() && this.method_16815(recipeType)) {
					this.fuelTime = getBurnTime(itemStack);
					this.totalFuelTime = this.fuelTime;
					if (this.isFueled()) {
						bl2 = true;
						if (!itemStack.isEmpty()) {
							Item item = itemStack.getItem();
							itemStack.decrement(1);
							if (itemStack.isEmpty()) {
								Item item2 = item.getRecipeRemainder();
								this.field_15154.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
							}
						}
					}
				}

				if (this.isFueled() && this.method_16815(recipeType)) {
					this.cookTime++;
					if (this.cookTime == this.totalCookTime) {
						this.cookTime = 0;
						this.totalCookTime = this.method_16819();
						this.method_16816(recipeType);
						bl2 = true;
					}
				} else {
					this.cookTime = 0;
				}
			} else if (!this.isFueled() && this.cookTime > 0) {
				this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
			}

			if (bl != this.isFueled()) {
				bl2 = true;
				this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).withProperty(FurnaceBlock.field_18348, Boolean.valueOf(this.isFueled())), 3);
			}
		}

		if (bl2) {
			this.markDirty();
		}
	}

	private int method_16819() {
		class_3584 lv = (class_3584)this.world.method_16313().method_16209(this, this.world);
		return lv != null ? lv.method_16246() : 200;
	}

	private boolean method_16815(@Nullable RecipeType recipeType) {
		if (!this.field_15154.get(0).isEmpty() && recipeType != null) {
			ItemStack itemStack = recipeType.getOutput();
			if (itemStack.isEmpty()) {
				return false;
			} else {
				ItemStack itemStack2 = this.field_15154.get(2);
				if (itemStack2.isEmpty()) {
					return true;
				} else if (!itemStack2.equalsIgnoreNbt(itemStack)) {
					return false;
				} else {
					return itemStack2.getCount() < this.getInvMaxStackAmount() && itemStack2.getCount() < itemStack2.getMaxCount()
						? true
						: itemStack2.getCount() < itemStack.getMaxCount();
				}
			}
		} else {
			return false;
		}
	}

	private void method_16816(@Nullable RecipeType recipeType) {
		if (recipeType != null && this.method_16815(recipeType)) {
			ItemStack itemStack = this.field_15154.get(0);
			ItemStack itemStack2 = recipeType.getOutput();
			ItemStack itemStack3 = this.field_15154.get(2);
			if (itemStack3.isEmpty()) {
				this.field_15154.set(2, itemStack2.copy());
			} else if (itemStack3.getItem() == itemStack2.getItem()) {
				itemStack3.increment(1);
			}

			if (!this.world.isClient) {
				this.method_15985(this.world, null, recipeType);
			}

			if (itemStack.getItem() == Blocks.WET_SPONGE.getItem() && !this.field_15154.get(1).isEmpty() && this.field_15154.get(1).getItem() == Items.BUCKET) {
				this.field_15154.set(1, new ItemStack(Items.WATER_BUCKET));
			}

			itemStack.decrement(1);
		}
	}

	private static int getBurnTime(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		} else {
			Item item = stack.getItem();
			return (Integer)method_16817().getOrDefault(item, 0);
		}
	}

	public static boolean isFuel(ItemStack stack) {
		return method_16817().containsKey(stack.getItem());
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}

	@Override
	public void onInvOpen(PlayerEntity player) {
	}

	@Override
	public void onInvClose(PlayerEntity player) {
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		if (slot == 2) {
			return false;
		} else if (slot != 1) {
			return true;
		} else {
			ItemStack itemStack = this.field_15154.get(1);
			return isFuel(stack) || FurnaceFuelSlot.isBucket(stack) && itemStack.getItem() != Items.BUCKET;
		}
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.DOWN) {
			return outputs;
		} else {
			return side == Direction.UP ? inputs : fuelInputs;
		}
	}

	@Override
	public boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction dir) {
		return this.isValidInvStack(slot, stack);
	}

	@Override
	public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.DOWN && slot == 1) {
			Item item = stack.getItem();
			if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getId() {
		return "minecraft:furnace";
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		return new FurnaceScreenHandler(inventory, this);
	}

	@Override
	public int getProperty(int key) {
		switch (key) {
			case 0:
				return this.fuelTime;
			case 1:
				return this.totalFuelTime;
			case 2:
				return this.cookTime;
			case 3:
				return this.totalCookTime;
			default:
				return 0;
		}
	}

	@Override
	public void setProperty(int id, int value) {
		switch (id) {
			case 0:
				this.fuelTime = value;
				break;
			case 1:
				this.totalFuelTime = value;
				break;
			case 2:
				this.cookTime = value;
				break;
			case 3:
				this.totalCookTime = value;
		}
	}

	@Override
	public int getProperties() {
		return 4;
	}

	@Override
	public void clear() {
		this.field_15154.clear();
	}

	@Override
	public void method_15987(class_3175 arg) {
		for (ItemStack itemStack : this.field_15154) {
			arg.method_15943(itemStack);
		}
	}

	@Override
	public void method_14210(RecipeType recipeType) {
		if (this.field_18636.containsKey(recipeType.method_16202())) {
			this.field_18636.put(recipeType.method_16202(), (Integer)this.field_18636.get(recipeType.method_16202()) + 1);
		} else {
			this.field_18636.put(recipeType.method_16202(), 1);
		}
	}

	@Nullable
	@Override
	public RecipeType method_14211() {
		return null;
	}

	public Map<Identifier, Integer> method_16818() {
		return this.field_18636;
	}

	@Override
	public boolean method_15985(World world, ServerPlayerEntity serverPlayerEntity, @Nullable RecipeType recipeType) {
		if (recipeType != null) {
			this.method_14210(recipeType);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_15986(PlayerEntity playerEntity) {
		if (!this.world.getGameRules().getBoolean("doLimitedCrafting")) {
			List<RecipeType> list = Lists.newArrayList();

			for (Identifier identifier : this.field_18636.keySet()) {
				RecipeType recipeType = playerEntity.world.method_16313().method_16207(identifier);
				if (recipeType != null) {
					list.add(recipeType);
				}
			}

			playerEntity.method_15927(list);
		}

		this.field_18636.clear();
	}
}
