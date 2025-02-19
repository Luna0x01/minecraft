package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.apache.commons.lang3.tuple.Pair;

public class MooshroomEntity extends CowEntity implements Shearable {
	private static final TrackedData<String> TYPE = DataTracker.registerData(MooshroomEntity.class, TrackedDataHandlerRegistry.STRING);
	private static final int MUTATION_CHANCE = 1024;
	private StatusEffect stewEffect;
	private int stewEffectDuration;
	private UUID lightningId;

	public MooshroomEntity(EntityType<? extends MooshroomEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return world.getBlockState(pos.down()).isOf(Blocks.MYCELIUM) ? 10.0F : world.getBrightness(pos) - 0.5F;
	}

	public static boolean canSpawn(EntityType<MooshroomEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isOf(Blocks.MYCELIUM) && world.getBaseLightLevel(pos, 0) > 8;
	}

	@Override
	public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
		UUID uUID = lightning.getUuid();
		if (!uUID.equals(this.lightningId)) {
			this.setType(this.getMooshroomType() == MooshroomEntity.Type.RED ? MooshroomEntity.Type.BROWN : MooshroomEntity.Type.RED);
			this.lightningId = uUID;
			this.playSound(SoundEvents.ENTITY_MOOSHROOM_CONVERT, 2.0F, 1.0F);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TYPE, MooshroomEntity.Type.RED.name);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.BOWL) && !this.isBaby()) {
			boolean bl = false;
			ItemStack itemStack2;
			if (this.stewEffect != null) {
				bl = true;
				itemStack2 = new ItemStack(Items.SUSPICIOUS_STEW);
				SuspiciousStewItem.addEffectToStew(itemStack2, this.stewEffect, this.stewEffectDuration);
				this.stewEffect = null;
				this.stewEffectDuration = 0;
			} else {
				itemStack2 = new ItemStack(Items.MUSHROOM_STEW);
			}

			ItemStack itemStack4 = ItemUsage.exchangeStack(itemStack, player, itemStack2, false);
			player.setStackInHand(hand, itemStack4);
			SoundEvent soundEvent;
			if (bl) {
				soundEvent = SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK;
			} else {
				soundEvent = SoundEvents.ENTITY_MOOSHROOM_MILK;
			}

			this.playSound(soundEvent, 1.0F, 1.0F);
			return ActionResult.success(this.world.isClient);
		} else if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
			this.sheared(SoundCategory.PLAYERS);
			this.emitGameEvent(GameEvent.SHEAR, player);
			if (!this.world.isClient) {
				itemStack.damage(1, player, playerx -> playerx.sendToolBreakStatus(hand));
			}

			return ActionResult.success(this.world.isClient);
		} else if (this.getMooshroomType() == MooshroomEntity.Type.BROWN && itemStack.isIn(ItemTags.SMALL_FLOWERS)) {
			if (this.stewEffect != null) {
				for (int i = 0; i < 2; i++) {
					this.world
						.addParticle(
							ParticleTypes.SMOKE,
							this.getX() + this.random.nextDouble() / 2.0,
							this.getBodyY(0.5),
							this.getZ() + this.random.nextDouble() / 2.0,
							0.0,
							this.random.nextDouble() / 5.0,
							0.0
						);
				}
			} else {
				Optional<Pair<StatusEffect, Integer>> optional = this.getStewEffectFrom(itemStack);
				if (!optional.isPresent()) {
					return ActionResult.PASS;
				}

				Pair<StatusEffect, Integer> pair = (Pair<StatusEffect, Integer>)optional.get();
				if (!player.getAbilities().creativeMode) {
					itemStack.decrement(1);
				}

				for (int j = 0; j < 4; j++) {
					this.world
						.addParticle(
							ParticleTypes.EFFECT,
							this.getX() + this.random.nextDouble() / 2.0,
							this.getBodyY(0.5),
							this.getZ() + this.random.nextDouble() / 2.0,
							0.0,
							this.random.nextDouble() / 5.0,
							0.0
						);
				}

				this.stewEffect = (StatusEffect)pair.getLeft();
				this.stewEffectDuration = (Integer)pair.getRight();
				this.playSound(SoundEvents.ENTITY_MOOSHROOM_EAT, 2.0F, 1.0F);
			}

			return ActionResult.success(this.world.isClient);
		} else {
			return super.interactMob(player, hand);
		}
	}

	@Override
	public void sheared(SoundCategory shearedSoundCategory) {
		this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_MOOSHROOM_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
		if (!this.world.isClient()) {
			((ServerWorld)this.world).spawnParticles(ParticleTypes.EXPLOSION, this.getX(), this.getBodyY(0.5), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
			this.discard();
			CowEntity cowEntity = EntityType.COW.create(this.world);
			cowEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
			cowEntity.setHealth(this.getHealth());
			cowEntity.bodyYaw = this.bodyYaw;
			if (this.hasCustomName()) {
				cowEntity.setCustomName(this.getCustomName());
				cowEntity.setCustomNameVisible(this.isCustomNameVisible());
			}

			if (this.isPersistent()) {
				cowEntity.setPersistent();
			}

			cowEntity.setInvulnerable(this.isInvulnerable());
			this.world.spawnEntity(cowEntity);

			for (int i = 0; i < 5; i++) {
				this.world
					.spawnEntity(new ItemEntity(this.world, this.getX(), this.getBodyY(1.0), this.getZ(), new ItemStack(this.getMooshroomType().mushroom.getBlock())));
			}
		}
	}

	@Override
	public boolean isShearable() {
		return this.isAlive() && !this.isBaby();
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putString("Type", this.getMooshroomType().name);
		if (this.stewEffect != null) {
			nbt.putByte("EffectId", (byte)StatusEffect.getRawId(this.stewEffect));
			nbt.putInt("EffectDuration", this.stewEffectDuration);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setType(MooshroomEntity.Type.fromName(nbt.getString("Type")));
		if (nbt.contains("EffectId", 1)) {
			this.stewEffect = StatusEffect.byRawId(nbt.getByte("EffectId"));
		}

		if (nbt.contains("EffectDuration", 3)) {
			this.stewEffectDuration = nbt.getInt("EffectDuration");
		}
	}

	private Optional<Pair<StatusEffect, Integer>> getStewEffectFrom(ItemStack flower) {
		Item item = flower.getItem();
		return item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof FlowerBlock flowerBlock
			? Optional.of(Pair.of(flowerBlock.getEffectInStew(), flowerBlock.getEffectInStewDuration()))
			: Optional.empty();
	}

	private void setType(MooshroomEntity.Type type) {
		this.dataTracker.set(TYPE, type.name);
	}

	public MooshroomEntity.Type getMooshroomType() {
		return MooshroomEntity.Type.fromName(this.dataTracker.get(TYPE));
	}

	public MooshroomEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		MooshroomEntity mooshroomEntity = EntityType.MOOSHROOM.create(serverWorld);
		mooshroomEntity.setType(this.chooseBabyType((MooshroomEntity)passiveEntity));
		return mooshroomEntity;
	}

	private MooshroomEntity.Type chooseBabyType(MooshroomEntity mooshroom) {
		MooshroomEntity.Type type = this.getMooshroomType();
		MooshroomEntity.Type type2 = mooshroom.getMooshroomType();
		MooshroomEntity.Type type3;
		if (type == type2 && this.random.nextInt(1024) == 0) {
			type3 = type == MooshroomEntity.Type.BROWN ? MooshroomEntity.Type.RED : MooshroomEntity.Type.BROWN;
		} else {
			type3 = this.random.nextBoolean() ? type : type2;
		}

		return type3;
	}

	public static enum Type {
		RED("red", Blocks.RED_MUSHROOM.getDefaultState()),
		BROWN("brown", Blocks.BROWN_MUSHROOM.getDefaultState());

		final String name;
		final BlockState mushroom;

		private Type(String name, BlockState mushroom) {
			this.name = name;
			this.mushroom = mushroom;
		}

		public BlockState getMushroomState() {
			return this.mushroom;
		}

		static MooshroomEntity.Type fromName(String name) {
			for (MooshroomEntity.Type type : values()) {
				if (type.name.equals(name)) {
					return type;
				}
			}

			return RED;
		}
	}
}
