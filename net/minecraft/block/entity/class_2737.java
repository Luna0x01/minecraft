package net.minecraft.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_2782;
import net.minecraft.class_2960;
import net.minecraft.class_2964;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.class_2780;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public abstract class class_2737 extends LockableContainerBlockEntity implements class_2964 {
	protected Identifier field_12852;
	protected long field_12853;
	protected Text field_18643;

	protected class_2737(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public static void method_16833(BlockView blockView, Random random, BlockPos blockPos, Identifier identifier) {
		BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
		if (blockEntity instanceof class_2737) {
			((class_2737)blockEntity).method_11660(identifier, random.nextLong());
		}
	}

	protected boolean method_11661(NbtCompound nbtCompound) {
		if (nbtCompound.contains("LootTable", 8)) {
			this.field_12852 = new Identifier(nbtCompound.getString("LootTable"));
			this.field_12853 = nbtCompound.getLong("LootTableSeed");
			return true;
		} else {
			return false;
		}
	}

	protected boolean method_11663(NbtCompound nbtCompound) {
		if (this.field_12852 == null) {
			return false;
		} else {
			nbtCompound.putString("LootTable", this.field_12852.toString());
			if (this.field_12853 != 0L) {
				nbtCompound.putLong("LootTableSeed", this.field_12853);
			}

			return true;
		}
	}

	public void method_11662(@Nullable PlayerEntity playerEntity) {
		if (this.field_12852 != null && this.world.getServer() != null) {
			class_2780 lv = this.world.getServer().method_20334().method_12006(this.field_12852);
			this.field_12852 = null;
			Random random;
			if (this.field_12853 == 0L) {
				random = new Random();
			} else {
				random = new Random(this.field_12853);
			}

			class_2782.class_2783 lv2 = new class_2782.class_2783((ServerWorld)this.world);
			lv2.method_17981(this.pos);
			if (playerEntity != null) {
				lv2.method_11995(playerEntity.method_13271());
			}

			lv.method_11983(this, random, lv2.method_11994());
		}
	}

	@Override
	public Identifier getLootTableId() {
		return this.field_12852;
	}

	public void method_11660(Identifier identifier, long l) {
		this.field_12852 = identifier;
		this.field_12853 = l;
	}

	@Override
	public boolean hasCustomName() {
		return this.field_18643 != null;
	}

	public void method_16835(@Nullable Text text) {
		this.field_18643 = text;
	}

	@Nullable
	@Override
	public Text method_15541() {
		return this.field_18643;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		this.method_11662(null);
		return this.method_13730().get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		this.method_11662(null);
		ItemStack itemStack = class_2960.method_13926(this.method_13730(), slot, amount);
		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		this.method_11662(null);
		return class_2960.method_13925(this.method_13730(), slot);
	}

	@Override
	public void setInvStack(int slot, @Nullable ItemStack stack) {
		this.method_11662(null);
		this.method_13730().set(slot, stack);
		if (stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
		}

		this.markDirty();
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
		return true;
	}

	@Override
	public int getProperty(int key) {
		return 0;
	}

	@Override
	public void setProperty(int id, int value) {
	}

	@Override
	public int getProperties() {
		return 0;
	}

	@Override
	public void clear() {
		this.method_13730().clear();
	}

	protected abstract DefaultedList<ItemStack> method_13730();

	protected abstract void method_16834(DefaultedList<ItemStack> defaultedList);
}
