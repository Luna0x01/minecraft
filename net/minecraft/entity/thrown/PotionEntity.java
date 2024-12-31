package net.minecraft.entity.thrown;

import com.google.common.base.Optional;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.ItemSchema;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
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
import net.minecraft.world.level.storage.LevelDataType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionEntity extends ThrowableEntity {
	private static final TrackedData<Optional<ItemStack>> ITEM = DataTracker.registerData(PotionEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final Logger LOGGER = LogManager.getLogger();

	public PotionEntity(World world) {
		super(world);
	}

	public PotionEntity(World world, LivingEntity livingEntity, ItemStack itemStack) {
		super(world, livingEntity);
		this.setItem(itemStack);
	}

	public PotionEntity(World world, double d, double e, double f, @Nullable ItemStack itemStack) {
		super(world, d, e, f);
		if (itemStack != null) {
			this.setItem(itemStack);
		}
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM, Optional.absent());
	}

	public ItemStack getItem() {
		ItemStack itemStack = (ItemStack)this.getDataTracker().get(ITEM).orNull();
		if (itemStack == null || itemStack.getItem() != Items.SPLASH_POTION && itemStack.getItem() != Items.LINGERING_POTION) {
			if (this.world != null) {
				LOGGER.error("ThrownPotion entity {} has no item?!", new Object[]{this.getEntityId()});
			}

			return new ItemStack(Items.SPLASH_POTION);
		} else {
			return itemStack;
		}
	}

	public void setItem(@Nullable ItemStack item) {
		this.getDataTracker().set(ITEM, Optional.fromNullable(item));
		this.getDataTracker().method_12754(ITEM);
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
			if (result.type == BlockHitResult.Type.BLOCK && potion == Potions.WATER && list.isEmpty()) {
				BlockPos blockPos = result.getBlockPos().offset(result.direction);
				this.method_11316(blockPos);

				for (Direction direction : Direction.DirectionType.HORIZONTAL) {
					this.method_11316(blockPos.offset(direction));
				}

				this.world.syncGlobalEvent(2002, new BlockPos(this), Potion.getId(potion));
				this.remove();
			} else {
				if (!list.isEmpty()) {
					if (this.isLingering()) {
						AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.x, this.y, this.z);
						areaEffectCloudEntity.method_12954(this.getOwner());
						areaEffectCloudEntity.setRadius(3.0F);
						areaEffectCloudEntity.method_12956(-0.5F);
						areaEffectCloudEntity.method_12959(10);
						areaEffectCloudEntity.method_12958(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
						areaEffectCloudEntity.setPotion(potion);

						for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(itemStack)) {
							areaEffectCloudEntity.addEffect(
								new StatusEffectInstance(statusEffectInstance.getStatusEffect(), statusEffectInstance.getDuration(), statusEffectInstance.getAmplifier())
							);
						}

						this.world.spawnEntity(areaEffectCloudEntity);
					} else {
						Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
						List<LivingEntity> list2 = this.world.getEntitiesInBox(LivingEntity.class, box);
						if (!list2.isEmpty()) {
							for (LivingEntity livingEntity : list2) {
								if (livingEntity.method_13057()) {
									double d = this.squaredDistanceTo(livingEntity);
									if (d < 16.0) {
										double e = 1.0 - Math.sqrt(d) / 4.0;
										if (livingEntity == result.entity) {
											e = 1.0;
										}

										for (StatusEffectInstance statusEffectInstance2 : list) {
											StatusEffect statusEffect = statusEffectInstance2.getStatusEffect();
											if (statusEffect.isInstant()) {
												statusEffect.method_6088(this, this.getOwner(), livingEntity, statusEffectInstance2.getAmplifier(), e);
											} else {
												int i = (int)(e * (double)statusEffectInstance2.getDuration() + 0.5);
												if (i > 20) {
													livingEntity.addStatusEffect(new StatusEffectInstance(statusEffect, i, statusEffectInstance2.getAmplifier()));
												}
											}
										}
									}
								}
							}
						}
					}
				}

				this.world.syncGlobalEvent(2002, new BlockPos(this), Potion.getId(potion));
				this.remove();
			}
		}
	}

	private boolean isLingering() {
		return this.getItem().getItem() == Items.LINGERING_POTION;
	}

	private void method_11316(BlockPos blockPos) {
		if (this.world.getBlockState(blockPos).getBlock() == Blocks.FIRE) {
			this.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
		}
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		ThrowableEntity.registerDataFixes(dataFixer, "ThrownPotion");
		dataFixer.addSchema(LevelDataType.ENTITY, new ItemSchema("ThrownPotion", "Potion"));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		ItemStack itemStack = ItemStack.fromNbt(nbt.getCompound("Potion"));
		if (itemStack == null) {
			this.remove();
		} else {
			this.setItem(itemStack);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		ItemStack itemStack = this.getItem();
		if (itemStack != null) {
			nbt.put("Potion", itemStack.toNbt(new NbtCompound()));
		}
	}
}
