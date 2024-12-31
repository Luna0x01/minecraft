package net.minecraft.entity.mob;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ZombieVillagerEntity extends ZombieEntity {
	private static final TrackedData<Boolean> field_15075 = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_15077 = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private int conversionTimer;
	private UUID converter;

	public ZombieVillagerEntity(World world) {
		super(world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_15075, false);
		this.dataTracker.startTracking(field_15077, 0);
	}

	public void method_13606(int i) {
		this.dataTracker.set(field_15077, i);
	}

	public int getVillagerData() {
		return Math.max(this.dataTracker.get(field_15077) % 6, 0);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, ZombieVillagerEntity.class);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Profession", this.getVillagerData());
		nbt.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : -1);
		if (this.converter != null) {
			nbt.putUuid("ConversionPlayer", this.converter);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.method_13606(nbt.getInt("Profession"));
		if (nbt.contains("ConversionTime", 99) && nbt.getInt("ConversionTime") > -1) {
			this.setConverting(nbt.containsUuid("ConversionPlayer") ? nbt.getUuid("ConversionPlayer") : null, nbt.getInt("ConversionTime"));
		}
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		this.method_13606(this.world.random.nextInt(6));
		return super.initialize(difficulty, data);
	}

	@Override
	public void tick() {
		if (!this.world.isClient && this.isConverting()) {
			int i = this.getConversionRate();
			this.conversionTimer -= i;
			if (this.conversionTimer <= 0) {
				this.finishConversion();
			}
		}

		super.tick();
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.GOLDEN_APPLE && itemStack.getData() == 0 && this.hasStatusEffect(StatusEffects.WEAKNESS)) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.decrement(1);
			}

			if (!this.world.isClient) {
				this.setConverting(playerEntity.getUuid(), this.random.nextInt(2401) + 3600);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean canImmediatelyDespawn() {
		return !this.isConverting();
	}

	public boolean isConverting() {
		return this.getDataTracker().get(field_15075);
	}

	protected void setConverting(@Nullable UUID uuid, int delay) {
		this.converter = uuid;
		this.conversionTimer = delay;
		this.getDataTracker().set(field_15075, true);
		this.removeStatusEffect(StatusEffects.WEAKNESS);
		this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, delay, Math.min(this.world.getGlobalDifficulty().getId() - 1, 0)));
		this.world.sendEntityStatus(this, (byte)16);
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 16) {
			if (!this.isSilent()) {
				this.world
					.playSound(
						this.x + 0.5,
						this.y + 0.5,
						this.z + 0.5,
						Sounds.ENTITY_ZOMBIE_VILLAGER_CURE,
						this.getSoundCategory(),
						1.0F + this.random.nextFloat(),
						this.random.nextFloat() * 0.7F + 0.3F,
						false
					);
			}
		} else {
			super.handleStatus(status);
		}
	}

	protected void finishConversion() {
		VillagerEntity villagerEntity = new VillagerEntity(this.world);
		villagerEntity.copyPosition(this);
		villagerEntity.setProfession(this.getVillagerData());
		villagerEntity.initialize(this.world.getLocalDifficulty(new BlockPos(villagerEntity)), null, false);
		villagerEntity.method_4567();
		if (this.isBaby()) {
			villagerEntity.setAge(-24000);
		}

		this.world.removeEntity(this);
		villagerEntity.setAiDisabled(this.hasNoAi());
		if (this.hasCustomName()) {
			villagerEntity.setCustomName(this.getCustomName());
			villagerEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		this.world.spawnEntity(villagerEntity);
		if (this.converter != null) {
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.converter);
			if (playerEntity instanceof ServerPlayerEntity) {
				AchievementsAndCriterions.CURED_ZOMBIE_VILLAGER.grant((ServerPlayerEntity)playerEntity, this, villagerEntity);
			}
		}

		villagerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
		this.world.syncWorldEvent(null, 1027, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
	}

	protected int getConversionRate() {
		int i = 1;
		if (this.random.nextFloat() < 0.01F) {
			int j = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = (int)this.x - 4; k < (int)this.x + 4 && j < 14; k++) {
				for (int l = (int)this.y - 4; l < (int)this.y + 4 && j < 14; l++) {
					for (int m = (int)this.z - 4; m < (int)this.z + 4 && j < 14; m++) {
						Block block = this.world.getBlockState(mutable.setPosition(k, l, m)).getBlock();
						if (block == Blocks.IRON_BARS || block == Blocks.BED) {
							if (this.random.nextFloat() < 0.3F) {
								i++;
							}

							j++;
						}
					}
				}
			}
		}

		return i;
	}

	@Override
	protected float getSoundPitch() {
		return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
	}

	@Override
	public Sound ambientSound() {
		return Sounds.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
	}

	@Override
	public Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_ZOMBIE_VILLAGER_HURT;
	}

	@Override
	public Sound deathSound() {
		return Sounds.ENTITY_ZOMBIE_VILLAGER_DEATH;
	}

	@Override
	public Sound getStepSound() {
		return Sounds.ENTITY_ZOMBIE_VILLAGER_STEP;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.ZOMBIE_VILLAGER_ENTITIE;
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}
}
