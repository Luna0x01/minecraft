package net.minecraft.entity.passive;

import java.util.Random;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.commons.lang3.tuple.Pair;

public class MooshroomEntity extends CowEntity {
	private static final TrackedData<String> TYPE = DataTracker.registerData(MooshroomEntity.class, TrackedDataHandlerRegistry.STRING);
	private StatusEffect stewEffect;
	private int stewEffectDuration;
	private UUID lightningId;

	public MooshroomEntity(EntityType<? extends MooshroomEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public float getPathfindingFavor(BlockPos blockPos, WorldView worldView) {
		return worldView.getBlockState(blockPos.down()).getBlock() == Blocks.field_10402 ? 10.0F : worldView.getBrightness(blockPos) - 0.5F;
	}

	public static boolean canSpawn(EntityType<MooshroomEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return iWorld.getBlockState(blockPos.down()).getBlock() == Blocks.field_10402 && iWorld.getBaseLightLevel(blockPos, 0) > 8;
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
		UUID uUID = lightningEntity.getUuid();
		if (!uUID.equals(this.lightningId)) {
			this.setType(this.getMooshroomType() == MooshroomEntity.Type.field_18109 ? MooshroomEntity.Type.field_18110 : MooshroomEntity.Type.field_18109);
			this.lightningId = uUID;
			this.playSound(SoundEvents.field_18266, 2.0F, 1.0F);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TYPE, MooshroomEntity.Type.field_18109.name);
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8428 && !this.isBaby() && !playerEntity.abilities.creativeMode) {
			itemStack.decrement(1);
			boolean bl = false;
			ItemStack itemStack2;
			if (this.stewEffect != null) {
				bl = true;
				itemStack2 = new ItemStack(Items.field_8766);
				SuspiciousStewItem.addEffectToStew(itemStack2, this.stewEffect, this.stewEffectDuration);
				this.stewEffect = null;
				this.stewEffectDuration = 0;
			} else {
				itemStack2 = new ItemStack(Items.field_8208);
			}

			if (itemStack.isEmpty()) {
				playerEntity.setStackInHand(hand, itemStack2);
			} else if (!playerEntity.inventory.insertStack(itemStack2)) {
				playerEntity.dropItem(itemStack2, false);
			}

			SoundEvent soundEvent;
			if (bl) {
				soundEvent = SoundEvents.field_18269;
			} else {
				soundEvent = SoundEvents.field_18268;
			}

			this.playSound(soundEvent, 1.0F, 1.0F);
			return true;
		} else if (itemStack.getItem() == Items.field_8868 && !this.isBaby()) {
			this.world.addParticle(ParticleTypes.field_11236, this.getX(), this.getBodyY(0.5), this.getZ(), 0.0, 0.0, 0.0);
			if (!this.world.isClient) {
				this.remove();
				CowEntity cowEntity = EntityType.field_6085.create(this.world);
				cowEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
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

				itemStack.damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(hand));
				this.playSound(SoundEvents.field_14705, 1.0F, 1.0F);
			}

			return true;
		} else {
			if (this.getMooshroomType() == MooshroomEntity.Type.field_18110 && itemStack.getItem().isIn(ItemTags.field_15543)) {
				if (this.stewEffect != null) {
					for (int j = 0; j < 2; j++) {
						this.world
							.addParticle(
								ParticleTypes.field_11251,
								this.getX() + (double)(this.random.nextFloat() / 2.0F),
								this.getBodyY(0.5),
								this.getZ() + (double)(this.random.nextFloat() / 2.0F),
								0.0,
								(double)(this.random.nextFloat() / 5.0F),
								0.0
							);
					}
				} else {
					Pair<StatusEffect, Integer> pair = this.getStewEffectFrom(itemStack);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					for (int k = 0; k < 4; k++) {
						this.world
							.addParticle(
								ParticleTypes.field_11245,
								this.getX() + (double)(this.random.nextFloat() / 2.0F),
								this.getBodyY(0.5),
								this.getZ() + (double)(this.random.nextFloat() / 2.0F),
								0.0,
								(double)(this.random.nextFloat() / 5.0F),
								0.0
							);
					}

					this.stewEffect = (StatusEffect)pair.getLeft();
					this.stewEffectDuration = (Integer)pair.getRight();
					this.playSound(SoundEvents.field_18267, 2.0F, 1.0F);
				}
			}

			return super.interactMob(playerEntity, hand);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putString("Type", this.getMooshroomType().name);
		if (this.stewEffect != null) {
			compoundTag.putByte("EffectId", (byte)StatusEffect.getRawId(this.stewEffect));
			compoundTag.putInt("EffectDuration", this.stewEffectDuration);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setType(MooshroomEntity.Type.fromName(compoundTag.getString("Type")));
		if (compoundTag.contains("EffectId", 1)) {
			this.stewEffect = StatusEffect.byRawId(compoundTag.getByte("EffectId"));
		}

		if (compoundTag.contains("EffectDuration", 3)) {
			this.stewEffectDuration = compoundTag.getInt("EffectDuration");
		}
	}

	private Pair<StatusEffect, Integer> getStewEffectFrom(ItemStack itemStack) {
		FlowerBlock flowerBlock = (FlowerBlock)((BlockItem)itemStack.getItem()).getBlock();
		return Pair.of(flowerBlock.getEffectInStew(), flowerBlock.getEffectInStewDuration());
	}

	private void setType(MooshroomEntity.Type type) {
		this.dataTracker.set(TYPE, type.name);
	}

	public MooshroomEntity.Type getMooshroomType() {
		return MooshroomEntity.Type.fromName(this.dataTracker.get(TYPE));
	}

	public MooshroomEntity createChild(PassiveEntity passiveEntity) {
		MooshroomEntity mooshroomEntity = EntityType.field_6143.create(this.world);
		mooshroomEntity.setType(this.chooseBabyType((MooshroomEntity)passiveEntity));
		return mooshroomEntity;
	}

	private MooshroomEntity.Type chooseBabyType(MooshroomEntity mooshroomEntity) {
		MooshroomEntity.Type type = this.getMooshroomType();
		MooshroomEntity.Type type2 = mooshroomEntity.getMooshroomType();
		MooshroomEntity.Type type3;
		if (type == type2 && this.random.nextInt(1024) == 0) {
			type3 = type == MooshroomEntity.Type.field_18110 ? MooshroomEntity.Type.field_18109 : MooshroomEntity.Type.field_18110;
		} else {
			type3 = this.random.nextBoolean() ? type : type2;
		}

		return type3;
	}

	public static enum Type {
		field_18109,
		field_18110;

		private final String name;
		private final BlockState mushroom;

		private Type(String string2, BlockState blockState) {
			this.name = string2;
			this.mushroom = blockState;
		}

		public BlockState getMushroomState() {
			return this.mushroom;
		}

		private static MooshroomEntity.Type fromName(String string) {
			for (MooshroomEntity.Type type : values()) {
				if (type.name.equals(string)) {
					return type;
				}
			}

			return field_18109;
		}

		static {
			// $VF: Couldn't be decompiled
			// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
			//
			// Bytecode:
			// 00: new net/minecraft/entity/passive/MooshroomEntity$Type
			// 03: dup
			// 04: ldc "RED"
			// 06: bipush 0
			// 07: ldc "red"
			// 09: getstatic net/minecraft/block/Blocks.field_10559 Lnet/minecraft/block/Block;
			// 0c: invokevirtual net/minecraft/block/Block.getDefaultState ()Lnet/minecraft/block/BlockState;
			// 0f: invokespecial net/minecraft/entity/passive/MooshroomEntity$Type.<init> (Ljava/lang/String;ILjava/lang/String;Lnet/minecraft/block/BlockState;)V
			// 12: putstatic net/minecraft/entity/passive/MooshroomEntity$Type.field_18109 Lnet/minecraft/entity/passive/MooshroomEntity$Type;
			// 15: new net/minecraft/entity/passive/MooshroomEntity$Type
			// 18: dup
			// 19: ldc "BROWN"
			// 1b: bipush 1
			// 1c: ldc "brown"
			// 1e: getstatic net/minecraft/block/Blocks.field_10251 Lnet/minecraft/block/Block;
			// 21: invokevirtual net/minecraft/block/Block.getDefaultState ()Lnet/minecraft/block/BlockState;
			// 24: invokespecial net/minecraft/entity/passive/MooshroomEntity$Type.<init> (Ljava/lang/String;ILjava/lang/String;Lnet/minecraft/block/BlockState;)V
			// 27: putstatic net/minecraft/entity/passive/MooshroomEntity$Type.field_18110 Lnet/minecraft/entity/passive/MooshroomEntity$Type;
			// 2a: bipush 2
			// 2b: anewarray 2
			// 2e: dup
			// 2f: bipush 0
			// 30: getstatic net/minecraft/entity/passive/MooshroomEntity$Type.field_18109 Lnet/minecraft/entity/passive/MooshroomEntity$Type;
			// 33: aastore
			// 34: dup
			// 35: bipush 1
			// 36: getstatic net/minecraft/entity/passive/MooshroomEntity$Type.field_18110 Lnet/minecraft/entity/passive/MooshroomEntity$Type;
			// 39: aastore
			// 3a: putstatic net/minecraft/entity/passive/MooshroomEntity$Type.field_18113 [Lnet/minecraft/entity/passive/MooshroomEntity$Type;
			// 3d: return
		}
	}
}
