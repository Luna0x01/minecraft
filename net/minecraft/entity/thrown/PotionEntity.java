package net.minecraft.entity.thrown;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class PotionEntity extends ThrowableEntity {
	private ItemStack stack;

	public PotionEntity(World world) {
		super(world);
	}

	public PotionEntity(World world, LivingEntity livingEntity, int i) {
		this(world, livingEntity, new ItemStack(Items.POTION, 1, i));
	}

	public PotionEntity(World world, LivingEntity livingEntity, ItemStack itemStack) {
		super(world, livingEntity);
		this.stack = itemStack;
	}

	public PotionEntity(World world, double d, double e, double f, int i) {
		this(world, d, e, f, new ItemStack(Items.POTION, 1, i));
	}

	public PotionEntity(World world, double d, double e, double f, ItemStack itemStack) {
		super(world, d, e, f);
		this.stack = itemStack;
	}

	@Override
	protected float getGravity() {
		return 0.05F;
	}

	@Override
	protected float method_3234() {
		return 0.5F;
	}

	@Override
	protected float method_3235() {
		return -20.0F;
	}

	public void setPotionValue(int i) {
		if (this.stack == null) {
			this.stack = new ItemStack(Items.POTION, 1, 0);
		}

		this.stack.setDamage(i);
	}

	public int method_3237() {
		if (this.stack == null) {
			this.stack = new ItemStack(Items.POTION, 1, 0);
		}

		return this.stack.getData();
	}

	@Override
	protected void onCollision(BlockHitResult result) {
		if (!this.world.isClient) {
			List<StatusEffectInstance> list = Items.POTION.getCustomPotionEffects(this.stack);
			if (list != null && !list.isEmpty()) {
				Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
				List<LivingEntity> list2 = this.world.getEntitiesInBox(LivingEntity.class, box);
				if (!list2.isEmpty()) {
					for (LivingEntity livingEntity : list2) {
						double d = this.squaredDistanceTo(livingEntity);
						if (d < 16.0) {
							double e = 1.0 - Math.sqrt(d) / 4.0;
							if (livingEntity == result.entity) {
								e = 1.0;
							}

							for (StatusEffectInstance statusEffectInstance : list) {
								int i = statusEffectInstance.getEffectId();
								if (StatusEffect.STATUS_EFFECTS[i].isInstant()) {
									StatusEffect.STATUS_EFFECTS[i].method_6088(this, this.getOwner(), livingEntity, statusEffectInstance.getAmplifier(), e);
								} else {
									int j = (int)(e * (double)statusEffectInstance.getDuration() + 0.5);
									if (j > 20) {
										livingEntity.addStatusEffect(new StatusEffectInstance(i, j, statusEffectInstance.getAmplifier()));
									}
								}
							}
						}
					}
				}
			}

			this.world.syncGlobalEvent(2002, new BlockPos(this), this.method_3237());
			this.remove();
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Potion", 10)) {
			this.stack = ItemStack.fromNbt(nbt.getCompound("Potion"));
		} else {
			this.setPotionValue(nbt.getInt("potionValue"));
		}

		if (this.stack == null) {
			this.remove();
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.stack != null) {
			nbt.put("Potion", this.stack.toNbt(new NbtCompound()));
		}
	}
}
