package net.minecraft.entity.passive;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_3462;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PufferfishEntity extends FishEntity {
	private static final TrackedData<Integer> field_16919 = DataTracker.registerData(PufferfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private int field_16923;
	private int field_16924;
	private static final Predicate<LivingEntity> field_16920 = livingEntity -> {
		if (livingEntity == null) {
			return false;
		} else {
			return !(livingEntity instanceof PlayerEntity) || !((PlayerEntity)livingEntity).isSpectator() && !((PlayerEntity)livingEntity).isCreative()
				? livingEntity.method_2647() != class_3462.field_16822
				: false;
		}
	};
	private float field_16921 = -1.0F;
	private float field_16922;

	public PufferfishEntity(World world) {
		super(EntityType.PUFFERFISH, world);
		this.setBounds(0.7F, 0.7F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_16919, 0);
	}

	public int method_15766() {
		return this.dataTracker.get(field_16919);
	}

	public void method_15763(int i) {
		this.dataTracker.set(field_16919, i);
		this.method_15765(i);
	}

	private void method_15765(int i) {
		float f = 1.0F;
		if (i == 1) {
			f = 0.7F;
		} else if (i == 0) {
			f = 0.5F;
		}

		this.method_15760(f);
	}

	@Override
	protected final void setBounds(float width, float height) {
		boolean bl = this.field_16921 > 0.0F;
		this.field_16921 = width;
		this.field_16922 = height;
		if (!bl) {
			this.method_15760(1.0F);
		}
	}

	private void method_15760(float f) {
		super.setBounds(this.field_16921 * f, this.field_16922 * f);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		this.method_15765(this.method_15766());
		super.onTrackedDataSet(data);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("PuffState", this.method_15766());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.method_15763(nbt.getInt("PuffState"));
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.PUFFERFISH_ENTITIE;
	}

	@Override
	protected ItemStack method_15726() {
		return new ItemStack(Items.PUFFERFISH_BUCKET);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goals.add(1, new PufferfishEntity.class_3489(this));
	}

	@Override
	public void tick() {
		if (this.isAlive() && !this.world.isClient) {
			if (this.field_16923 > 0) {
				if (this.method_15766() == 0) {
					this.playSound(Sounds.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
					this.method_15763(1);
				} else if (this.field_16923 > 40 && this.method_15766() == 1) {
					this.playSound(Sounds.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
					this.method_15763(2);
				}

				this.field_16923++;
			} else if (this.method_15766() != 0) {
				if (this.field_16924 > 60 && this.method_15766() == 2) {
					this.playSound(Sounds.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
					this.method_15763(1);
				} else if (this.field_16924 > 100 && this.method_15766() == 1) {
					this.playSound(Sounds.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
					this.method_15763(0);
				}

				this.field_16924++;
			}
		}

		super.tick();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.method_15766() > 0) {
			for (MobEntity mobEntity : this.world.method_16325(MobEntity.class, this.getBoundingBox().expand(0.3), field_16920)) {
				if (mobEntity.isAlive()) {
					this.method_15761(mobEntity);
				}
			}
		}
	}

	private void method_15761(MobEntity mobEntity) {
		int i = this.method_15766();
		if (mobEntity.damage(DamageSource.mob(this), (float)(1 + i))) {
			mobEntity.method_2654(new StatusEffectInstance(StatusEffects.POISON, 60 * i, 0));
			this.playSound(Sounds.ENTITY_PUFFER_FISH_STING, 1.0F, 1.0F);
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		int i = this.method_15766();
		if (player instanceof ServerPlayerEntity && i > 0 && player.damage(DamageSource.mob(this), (float)(1 + i))) {
			((ServerPlayerEntity)player).networkHandler.sendPacket(new GameStateChangeS2CPacket(9, 0.0F));
			player.method_2654(new StatusEffectInstance(StatusEffects.POISON, 60 * i, 0));
		}
	}

	@Override
	protected Sound ambientSound() {
		return Sounds.ENTITY_PUFFER_FISH_AMBIENT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_PUFFER_FISH_DEATH;
	}

	@Override
	protected Sound getHurtSound(DamageSource damageSource) {
		return Sounds.ENTITY_PUFFER_FISH_HURT;
	}

	@Override
	protected Sound method_15724() {
		return Sounds.ENTITY_PUFFER_FISH_FLOP;
	}

	static class class_3489 extends Goal {
		private final PufferfishEntity field_16925;

		public class_3489(PufferfishEntity pufferfishEntity) {
			this.field_16925 = pufferfishEntity;
		}

		@Override
		public boolean canStart() {
			List<LivingEntity> list = this.field_16925
				.world
				.method_16325(LivingEntity.class, this.field_16925.getBoundingBox().expand(2.0), PufferfishEntity.field_16920);
			return !list.isEmpty();
		}

		@Override
		public void start() {
			this.field_16925.field_16923 = 1;
			this.field_16925.field_16924 = 0;
		}

		@Override
		public void stop() {
			this.field_16925.field_16923 = 0;
		}

		@Override
		public boolean shouldContinue() {
			List<LivingEntity> list = this.field_16925
				.world
				.method_16325(LivingEntity.class, this.field_16925.getBoundingBox().expand(2.0), PufferfishEntity.field_16920);
			return !list.isEmpty();
		}
	}
}
