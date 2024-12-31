package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

public class VillagerInteractGoal extends StopAndLookAtEntityGoal {
	private int field_11947;
	private final VillagerEntity villager;

	public VillagerInteractGoal(VillagerEntity villagerEntity) {
		super(villagerEntity, VillagerEntity.class, 3.0F, 0.02F);
		this.villager = villagerEntity;
	}

	@Override
	public void start() {
		super.start();
		if (this.villager.method_11222() && this.target instanceof VillagerEntity && ((VillagerEntity)this.target).method_11223()) {
			this.field_11947 = 10;
		} else {
			this.field_11947 = 0;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.field_11947 > 0) {
			this.field_11947--;
			if (this.field_11947 == 0) {
				SimpleInventory simpleInventory = this.villager.method_11220();

				for (int i = 0; i < simpleInventory.getInvSize(); i++) {
					ItemStack itemStack = simpleInventory.getInvStack(i);
					ItemStack itemStack2 = ItemStack.EMPTY;
					if (!itemStack.isEmpty()) {
						Item item = itemStack.getItem();
						if ((item == Items.BREAD || item == Items.POTATO || item == Items.CARROT || item == Items.BEETROOT) && itemStack.getCount() > 3) {
							int j = itemStack.getCount() / 2;
							itemStack.decrement(j);
							itemStack2 = new ItemStack(item, j, itemStack.getData());
						} else if (item == Items.WHEAT && itemStack.getCount() > 5) {
							int k = itemStack.getCount() / 2 / 3 * 3;
							int l = k / 3;
							itemStack.decrement(k);
							itemStack2 = new ItemStack(Items.BREAD, l, 0);
						}

						if (itemStack.isEmpty()) {
							simpleInventory.setInvStack(i, ItemStack.EMPTY);
						}
					}

					if (!itemStack2.isEmpty()) {
						double d = this.villager.y - 0.3F + (double)this.villager.getEyeHeight();
						ItemEntity itemEntity = new ItemEntity(this.villager.world, this.villager.x, d, this.villager.z, itemStack2);
						float f = 0.3F;
						float g = this.villager.headYaw;
						float h = this.villager.pitch;
						itemEntity.velocityX = (double)(-MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0)) * 0.3F);
						itemEntity.velocityZ = (double)(MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0)) * 0.3F);
						itemEntity.velocityY = (double)(-MathHelper.sin(h * (float) (Math.PI / 180.0)) * 0.3F + 0.1F);
						itemEntity.setToDefaultPickupDelay();
						this.villager.world.spawnEntity(itemEntity);
						break;
					}
				}
			}
		}
	}
}
