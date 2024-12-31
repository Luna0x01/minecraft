package net.minecraft.village;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class TradeOffer {
	private ItemStack stack1 = ItemStack.EMPTY;
	private ItemStack stack2 = ItemStack.EMPTY;
	private ItemStack result = ItemStack.EMPTY;
	private int uses;
	private int maxUses;
	private boolean rewardingPlayerExperience;

	public TradeOffer(NbtCompound nbtCompound) {
		this.readNbt(nbtCompound);
	}

	public TradeOffer(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3) {
		this(itemStack, itemStack2, itemStack3, 0, 7);
	}

	public TradeOffer(ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3, int i, int j) {
		this.stack1 = itemStack;
		this.stack2 = itemStack2;
		this.result = itemStack3;
		this.uses = i;
		this.maxUses = j;
		this.rewardingPlayerExperience = true;
	}

	public TradeOffer(ItemStack itemStack, ItemStack itemStack2) {
		this(itemStack, ItemStack.EMPTY, itemStack2);
	}

	public TradeOffer(ItemStack itemStack, Item item) {
		this(itemStack, new ItemStack(item));
	}

	public ItemStack getFirstStack() {
		return this.stack1;
	}

	public ItemStack getSecondStack() {
		return this.stack2;
	}

	public boolean hasSecondStack() {
		return !this.stack2.isEmpty();
	}

	public ItemStack getResult() {
		return this.result;
	}

	public int getUses() {
		return this.uses;
	}

	public int getMaxUses() {
		return this.maxUses;
	}

	public void use() {
		this.uses++;
	}

	public void increaseSpecialPrice(int uses) {
		this.maxUses += uses;
	}

	public boolean isDisabled() {
		return this.uses >= this.maxUses;
	}

	public void clearUses() {
		this.uses = this.maxUses;
	}

	public boolean shouldRewardPlayerExperience() {
		return this.rewardingPlayerExperience;
	}

	public void readNbt(NbtCompound nbt) {
		NbtCompound nbtCompound = nbt.getCompound("buy");
		this.stack1 = new ItemStack(nbtCompound);
		NbtCompound nbtCompound2 = nbt.getCompound("sell");
		this.result = new ItemStack(nbtCompound2);
		if (nbt.contains("buyB", 10)) {
			this.stack2 = new ItemStack(nbt.getCompound("buyB"));
		}

		if (nbt.contains("uses", 99)) {
			this.uses = nbt.getInt("uses");
		}

		if (nbt.contains("maxUses", 99)) {
			this.maxUses = nbt.getInt("maxUses");
		} else {
			this.maxUses = 7;
		}

		if (nbt.contains("rewardExp", 1)) {
			this.rewardingPlayerExperience = nbt.getBoolean("rewardExp");
		} else {
			this.rewardingPlayerExperience = true;
		}
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.put("buy", this.stack1.toNbt(new NbtCompound()));
		nbtCompound.put("sell", this.result.toNbt(new NbtCompound()));
		if (!this.stack2.isEmpty()) {
			nbtCompound.put("buyB", this.stack2.toNbt(new NbtCompound()));
		}

		nbtCompound.putInt("uses", this.uses);
		nbtCompound.putInt("maxUses", this.maxUses);
		nbtCompound.putBoolean("rewardExp", this.rewardingPlayerExperience);
		return nbtCompound;
	}
}
