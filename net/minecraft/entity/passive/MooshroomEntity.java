package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class MooshroomEntity extends CowEntity {
	public MooshroomEntity(World world) {
		super(world);
		this.setBounds(0.9F, 1.3F);
		this.field_11973 = Blocks.MYCELIUM;
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		ItemStack itemStack = playerEntity.inventory.getMainHandStack();
		if (itemStack != null && itemStack.getItem() == Items.BOWL && this.age() >= 0) {
			if (itemStack.count == 1) {
				playerEntity.inventory.setInvStack(playerEntity.inventory.selectedSlot, new ItemStack(Items.MUSHROOM_STEW));
				return true;
			}

			if (playerEntity.inventory.insertStack(new ItemStack(Items.MUSHROOM_STEW)) && !playerEntity.abilities.creativeMode) {
				playerEntity.inventory.takeInvStack(playerEntity.inventory.selectedSlot, 1);
				return true;
			}
		}

		if (itemStack != null && itemStack.getItem() == Items.SHEARS && this.age() >= 0) {
			this.remove();
			this.world.addParticle(ParticleType.LARGE_EXPLOSION, this.x, this.y + (double)(this.height / 2.0F), this.z, 0.0, 0.0, 0.0);
			if (!this.world.isClient) {
				CowEntity cowEntity = new CowEntity(this.world);
				cowEntity.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
				cowEntity.setHealth(this.getHealth());
				cowEntity.bodyYaw = this.bodyYaw;
				if (this.hasCustomName()) {
					cowEntity.setCustomName(this.getCustomName());
				}

				this.world.spawnEntity(cowEntity);

				for (int i = 0; i < 5; i++) {
					this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y + (double)this.height, this.z, new ItemStack(Blocks.RED_MUSHROOM)));
				}

				itemStack.damage(1, playerEntity);
				this.playSound("mob.sheep.shear", 1.0F, 1.0F);
			}

			return true;
		} else {
			return super.method_2537(playerEntity);
		}
	}

	public MooshroomEntity breed(PassiveEntity passiveEntity) {
		return new MooshroomEntity(this.world);
	}
}
