package net.minecraft.entity.thrown;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionEntity extends ThrowableEntity {
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(PotionEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Predicate<LivingEntity> field_17102 = PotionEntity::isHurtByWater;

	public PotionEntity(World world) {
		super(EntityType.POTION, world);
	}

	public PotionEntity(World world, LivingEntity livingEntity, ItemStack itemStack) {
		super(EntityType.POTION, livingEntity, world);
		this.setItem(itemStack);
	}

	public PotionEntity(World world, double d, double e, double f, ItemStack itemStack) {
		super(EntityType.POTION, d, e, f, world);
		if (!itemStack.isEmpty()) {
			this.setItem(itemStack);
		}
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM, ItemStack.EMPTY);
	}

	public ItemStack getItem() {
		ItemStack itemStack = this.getDataTracker().get(ITEM);
		if (itemStack.getItem() != Items.SPLASH_POTION && itemStack.getItem() != Items.LINGERING_POTION) {
			if (this.world != null) {
				LOGGER.error("ThrownPotion entity {} has no item?!", this.getEntityId());
			}

			return new ItemStack(Items.SPLASH_POTION);
		} else {
			return itemStack;
		}
	}

	public void setItem(ItemStack item) {
		this.getDataTracker().set(ITEM, item);
	}

	@Override
	protected float getGravity() {
		return 0.05F;
	}

	@Override
	protected void onCollision(BlockHitResult result) {
		if (!this.world.isClient) {
			ItemStack itemStack = this.getItem();
			Potion potion = PotionUtil.getPotion(itemStack);
			List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
			boolean bl = potion == Potions.WATER && list.isEmpty();
			if (result.type == BlockHitResult.Type.BLOCK && bl) {
				BlockPos blockPos = result.getBlockPos().offset(result.direction);
				this.extinguishFire(blockPos, result.direction);

				for (Direction direction : Direction.DirectionType.HORIZONTAL) {
					this.extinguishFire(blockPos.offset(direction), direction);
				}
			}

			if (bl) {
				this.damageEntitiesHurtByWater();
			} else if (!list.isEmpty()) {
				if (this.isLingering()) {
					this.applyLingeringPotion(itemStack, potion);
				} else {
					this.applySplashPotion(result, list);
				}
			}

			int i = potion.method_11415() ? 2007 : 2002;
			this.world.syncGlobalEvent(i, new BlockPos(this), PotionUtil.getColor(itemStack));
			this.remove();
		}
	}

	private void damageEntitiesHurtByWater() {
		Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list = this.world.method_16325(LivingEntity.class, box, field_17102);
		if (!list.isEmpty()) {
			for (LivingEntity livingEntity : list) {
				double d = this.squaredDistanceTo(livingEntity);
				if (d < 16.0 && isHurtByWater(livingEntity)) {
					livingEntity.damage(DamageSource.DROWN, 1.0F);
				}
			}
		}
	}

	private void applySplashPotion(BlockHitResult result, List<StatusEffectInstance> effects) {
		Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list = this.world.getEntitiesInBox(LivingEntity.class, box);
		if (!list.isEmpty()) {
			for (LivingEntity livingEntity : list) {
				if (livingEntity.method_13057()) {
					double d = this.squaredDistanceTo(livingEntity);
					if (d < 16.0) {
						double e = 1.0 - Math.sqrt(d) / 4.0;
						if (livingEntity == result.entity) {
							e = 1.0;
						}

						for (StatusEffectInstance statusEffectInstance : effects) {
							StatusEffect statusEffect = statusEffectInstance.getStatusEffect();
							if (statusEffect.isInstant()) {
								statusEffect.method_6088(this, this.getOwner(), livingEntity, statusEffectInstance.getAmplifier(), e);
							} else {
								int i = (int)(e * (double)statusEffectInstance.getDuration() + 0.5);
								if (i > 20) {
									livingEntity.method_2654(
										new StatusEffectInstance(
											statusEffect, i, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()
										)
									);
								}
							}
						}
					}
				}
			}
		}
	}

	private void applyLingeringPotion(ItemStack stack, Potion potion) {
		AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.x, this.y, this.z);
		areaEffectCloudEntity.method_12954(this.getOwner());
		areaEffectCloudEntity.setRadius(3.0F);
		areaEffectCloudEntity.method_12956(-0.5F);
		areaEffectCloudEntity.method_12959(10);
		areaEffectCloudEntity.method_12958(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
		areaEffectCloudEntity.setPotion(potion);

		for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(stack)) {
			areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
		}

		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("CustomPotionColor", 99)) {
			areaEffectCloudEntity.setColor(nbtCompound.getInt("CustomPotionColor"));
		}

		this.world.method_3686(areaEffectCloudEntity);
	}

	private boolean isLingering() {
		return this.getItem().getItem() == Items.LINGERING_POTION;
	}

	private void extinguishFire(BlockPos pos, Direction direction) {
		if (this.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
			this.world.extinguishFire(null, pos.offset(direction), direction.getOpposite());
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		ItemStack itemStack = ItemStack.from(nbt.getCompound("Potion"));
		if (itemStack.isEmpty()) {
			this.remove();
		} else {
			this.setItem(itemStack);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		ItemStack itemStack = this.getItem();
		if (!itemStack.isEmpty()) {
			nbt.put("Potion", itemStack.toNbt(new NbtCompound()));
		}
	}

	private static boolean isHurtByWater(LivingEntity entity) {
		return entity instanceof EndermanEntity || entity instanceof BlazeEntity;
	}
}
