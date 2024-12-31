package net.minecraft.inventory.slot;

import java.util.Map.Entry;
import net.minecraft.class_3537;
import net.minecraft.class_3584;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
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
			for (Entry<Identifier, Integer> entry : ((FurnaceBlockEntity)this.inventory).method_16818().entrySet()) {
				class_3584 lv = (class_3584)this.player.world.method_16313().method_16207((Identifier)entry.getKey());
				float f;
				if (lv != null) {
					f = lv.method_16245();
				} else {
					f = 0.0F;
				}

				int i = (Integer)entry.getValue();
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
					this.player.world.method_3686(new ExperienceOrbEntity(this.player.world, this.player.x, this.player.y + 0.5, this.player.z + 0.5, k));
				}
			}

			((class_3537)this.inventory).method_15986(this.player);
		}

		this.amount = 0;
	}
}
