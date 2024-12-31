package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.class_4342;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MooshroomEntity extends CowEntity {
	public MooshroomEntity(World world) {
		super(EntityType.MOOSHROOM, world);
		this.setBounds(0.9F, 1.4F);
		this.field_11973 = Blocks.MYCELIUM;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.BOWL && this.age() >= 0 && !playerEntity.abilities.creativeMode) {
			itemStack.decrement(1);
			if (itemStack.isEmpty()) {
				playerEntity.equipStack(hand, new ItemStack(Items.MUSHROOM_STEW));
			} else if (!playerEntity.inventory.insertStack(new ItemStack(Items.MUSHROOM_STEW))) {
				playerEntity.dropItem(new ItemStack(Items.MUSHROOM_STEW), false);
			}

			return true;
		} else if (itemStack.getItem() == Items.SHEARS && this.age() >= 0) {
			this.world.method_16343(class_4342.field_21395, this.x, this.y + (double)(this.height / 2.0F), this.z, 0.0, 0.0, 0.0);
			if (!this.world.isClient) {
				this.remove();
				CowEntity cowEntity = new CowEntity(this.world);
				cowEntity.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
				cowEntity.setHealth(this.getHealth());
				cowEntity.bodyYaw = this.bodyYaw;
				if (this.hasCustomName()) {
					cowEntity.method_15578(this.method_15541());
				}

				this.world.method_3686(cowEntity);

				for (int i = 0; i < 5; i++) {
					this.world.method_3686(new ItemEntity(this.world, this.x, this.y + (double)this.height, this.z, new ItemStack(Blocks.RED_MUSHROOM)));
				}

				itemStack.damage(1, playerEntity);
				this.playSound(Sounds.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
			}

			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	public MooshroomEntity breed(PassiveEntity passiveEntity) {
		return new MooshroomEntity(this.world);
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.MUSHROOM_COW_ENTITIE;
	}
}
