package net.minecraft.entity.projectile;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class FishingBobberEntity extends Entity {
	private static final TrackedData<Integer> HOOK_ENTITY_ID = DataTracker.registerData(FishingBobberEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private boolean stuckOnBlock;
	private int removalTimer;
	private final PlayerEntity owner;
	private int selfHitTimer;
	private int hookCountdown;
	private int waitCountdown;
	private int fishTravelCountdown;
	private float fishAngle;
	public Entity hookedEntity;
	private FishingBobberEntity.State state = FishingBobberEntity.State.field_7180;
	private final int luckOfTheSeaLevel;
	private final int lureLevel;

	private FishingBobberEntity(World world, PlayerEntity playerEntity, int i, int j) {
		super(EntityType.field_6103, world);
		this.ignoreCameraFrustum = true;
		this.owner = playerEntity;
		this.owner.fishHook = this;
		this.luckOfTheSeaLevel = Math.max(0, i);
		this.lureLevel = Math.max(0, j);
	}

	public FishingBobberEntity(World world, PlayerEntity playerEntity, double d, double e, double f) {
		this(world, playerEntity, 0, 0);
		this.updatePosition(d, e, f);
		this.prevX = this.getX();
		this.prevY = this.getY();
		this.prevZ = this.getZ();
	}

	public FishingBobberEntity(PlayerEntity playerEntity, World world, int i, int j) {
		this(world, playerEntity, i, j);
		float f = this.owner.pitch;
		float g = this.owner.yaw;
		float h = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float k = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float l = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
		float m = MathHelper.sin(-f * (float) (Math.PI / 180.0));
		double d = this.owner.getX() - (double)k * 0.3;
		double e = this.owner.getEyeY();
		double n = this.owner.getZ() - (double)h * 0.3;
		this.refreshPositionAndAngles(d, e, n, g, f);
		Vec3d vec3d = new Vec3d((double)(-k), (double)MathHelper.clamp(-(m / l), -5.0F, 5.0F), (double)(-h));
		double o = vec3d.length();
		vec3d = vec3d.multiply(
			0.6 / o + 0.5 + this.random.nextGaussian() * 0.0045,
			0.6 / o + 0.5 + this.random.nextGaussian() * 0.0045,
			0.6 / o + 0.5 + this.random.nextGaussian() * 0.0045
		);
		this.setVelocity(vec3d);
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)MathHelper.sqrt(squaredHorizontalLength(vec3d))) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(HOOK_ENTITY_ID, 0);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (HOOK_ENTITY_ID.equals(trackedData)) {
			int i = this.getDataTracker().get(HOOK_ENTITY_ID);
			this.hookedEntity = i > 0 ? this.world.getEntityById(i - 1) : null;
		}

		super.onTrackedDataSet(trackedData);
	}

	@Override
	public boolean shouldRender(double d) {
		double e = 64.0;
		return d < 4096.0;
	}

	@Override
	public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
	}

	@Override
	public void tick() {
		super.tick();
		if (this.owner == null) {
			this.remove();
		} else if (this.world.isClient || !this.removeIfInvalid()) {
			if (this.stuckOnBlock) {
				this.removalTimer++;
				if (this.removalTimer >= 1200) {
					this.remove();
					return;
				}
			}

			float f = 0.0F;
			BlockPos blockPos = new BlockPos(this);
			FluidState fluidState = this.world.getFluidState(blockPos);
			if (fluidState.matches(FluidTags.field_15517)) {
				f = fluidState.getHeight(this.world, blockPos);
			}

			if (this.state == FishingBobberEntity.State.field_7180) {
				if (this.hookedEntity != null) {
					this.setVelocity(Vec3d.ZERO);
					this.state = FishingBobberEntity.State.field_7178;
					return;
				}

				if (f > 0.0F) {
					this.setVelocity(this.getVelocity().multiply(0.3, 0.2, 0.3));
					this.state = FishingBobberEntity.State.field_7179;
					return;
				}

				if (!this.world.isClient) {
					this.checkForCollision();
				}

				if (!this.stuckOnBlock && !this.onGround && !this.horizontalCollision) {
					this.selfHitTimer++;
				} else {
					this.selfHitTimer = 0;
					this.setVelocity(Vec3d.ZERO);
				}
			} else {
				if (this.state == FishingBobberEntity.State.field_7178) {
					if (this.hookedEntity != null) {
						if (this.hookedEntity.removed) {
							this.hookedEntity = null;
							this.state = FishingBobberEntity.State.field_7180;
						} else {
							this.updatePosition(this.hookedEntity.getX(), this.hookedEntity.getBodyY(0.8), this.hookedEntity.getZ());
						}
					}

					return;
				}

				if (this.state == FishingBobberEntity.State.field_7179) {
					Vec3d vec3d = this.getVelocity();
					double d = this.getY() + vec3d.y - (double)blockPos.getY() - (double)f;
					if (Math.abs(d) < 0.01) {
						d += Math.signum(d) * 0.1;
					}

					this.setVelocity(vec3d.x * 0.9, vec3d.y - d * (double)this.random.nextFloat() * 0.2, vec3d.z * 0.9);
					if (!this.world.isClient && f > 0.0F) {
						this.tickFishingLogic(blockPos);
					}
				}
			}

			if (!fluidState.matches(FluidTags.field_15517)) {
				this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
			}

			this.move(MovementType.field_6308, this.getVelocity());
			this.smoothenMovement();
			double e = 0.92;
			this.setVelocity(this.getVelocity().multiply(0.92));
			this.refreshPosition();
		}
	}

	private boolean removeIfInvalid() {
		ItemStack itemStack = this.owner.getMainHandStack();
		ItemStack itemStack2 = this.owner.getOffHandStack();
		boolean bl = itemStack.getItem() == Items.field_8378;
		boolean bl2 = itemStack2.getItem() == Items.field_8378;
		if (!this.owner.removed && this.owner.isAlive() && (bl || bl2) && !(this.squaredDistanceTo(this.owner) > 1024.0)) {
			return false;
		} else {
			this.remove();
			return true;
		}
	}

	private void smoothenMovement() {
		Vec3d vec3d = this.getVelocity();
		float f = MathHelper.sqrt(squaredHorizontalLength(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 180.0F / (float)Math.PI);

		while (this.pitch - this.prevPitch < -180.0F) {
			this.prevPitch -= 360.0F;
		}

		while (this.pitch - this.prevPitch >= 180.0F) {
			this.prevPitch += 360.0F;
		}

		while (this.yaw - this.prevYaw < -180.0F) {
			this.prevYaw -= 360.0F;
		}

		while (this.yaw - this.prevYaw >= 180.0F) {
			this.prevYaw += 360.0F;
		}

		this.pitch = MathHelper.lerp(0.2F, this.prevPitch, this.pitch);
		this.yaw = MathHelper.lerp(0.2F, this.prevYaw, this.yaw);
	}

	private void checkForCollision() {
		HitResult hitResult = ProjectileUtil.getCollision(
			this,
			this.getBoundingBox().stretch(this.getVelocity()).expand(1.0),
			entity -> !entity.isSpectator() && (entity.collides() || entity instanceof ItemEntity) && (entity != this.owner || this.selfHitTimer >= 5),
			RayTraceContext.ShapeType.field_17558,
			true
		);
		if (hitResult.getType() != HitResult.Type.field_1333) {
			if (hitResult.getType() == HitResult.Type.field_1331) {
				this.hookedEntity = ((EntityHitResult)hitResult).getEntity();
				this.updateHookedEntityId();
			} else {
				this.stuckOnBlock = true;
			}
		}
	}

	private void updateHookedEntityId() {
		this.getDataTracker().set(HOOK_ENTITY_ID, this.hookedEntity.getEntityId() + 1);
	}

	private void tickFishingLogic(BlockPos blockPos) {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		//
		// Bytecode:
		// 000: aload 0
		// 001: getfield net/minecraft/entity/projectile/FishingBobberEntity.world Lnet/minecraft/world/World;
		// 004: checkcast net/minecraft/server/world/ServerWorld
		// 007: astore 2
		// 008: bipush 1
		// 009: istore 3
		// 00a: aload 1
		// 00b: invokevirtual net/minecraft/util/math/BlockPos.up ()Lnet/minecraft/util/math/BlockPos;
		// 00e: astore 4
		// 010: aload 0
		// 011: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 014: invokevirtual java/util/Random.nextFloat ()F
		// 017: ldc_w 0.25
		// 01a: fcmpg
		// 01b: ifge 02d
		// 01e: aload 0
		// 01f: getfield net/minecraft/entity/projectile/FishingBobberEntity.world Lnet/minecraft/world/World;
		// 022: aload 4
		// 024: invokevirtual net/minecraft/world/World.hasRain (Lnet/minecraft/util/math/BlockPos;)Z
		// 027: ifeq 02d
		// 02a: iinc 3 1
		// 02d: aload 0
		// 02e: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 031: invokevirtual java/util/Random.nextFloat ()F
		// 034: ldc_w 0.5
		// 037: fcmpg
		// 038: ifge 04a
		// 03b: aload 0
		// 03c: getfield net/minecraft/entity/projectile/FishingBobberEntity.world Lnet/minecraft/world/World;
		// 03f: aload 4
		// 041: invokevirtual net/minecraft/world/World.isSkyVisible (Lnet/minecraft/util/math/BlockPos;)Z
		// 044: ifne 04a
		// 047: iinc 3 -1
		// 04a: aload 0
		// 04b: getfield net/minecraft/entity/projectile/FishingBobberEntity.hookCountdown I
		// 04e: ifle 094
		// 051: aload 0
		// 052: dup
		// 053: getfield net/minecraft/entity/projectile/FishingBobberEntity.hookCountdown I
		// 056: bipush 1
		// 057: isub
		// 058: putfield net/minecraft/entity/projectile/FishingBobberEntity.hookCountdown I
		// 05b: aload 0
		// 05c: getfield net/minecraft/entity/projectile/FishingBobberEntity.hookCountdown I
		// 05f: ifgt 06f
		// 062: aload 0
		// 063: bipush 0
		// 064: putfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 067: aload 0
		// 068: bipush 0
		// 069: putfield net/minecraft/entity/projectile/FishingBobberEntity.fishTravelCountdown I
		// 06c: goto 3b0
		// 06f: aload 0
		// 070: aload 0
		// 071: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 074: dconst_0
		// 075: ldc2_w -0.2
		// 078: aload 0
		// 079: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 07c: invokevirtual java/util/Random.nextFloat ()F
		// 07f: f2d
		// 080: dmul
		// 081: aload 0
		// 082: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 085: invokevirtual java/util/Random.nextFloat ()F
		// 088: f2d
		// 089: dmul
		// 08a: dconst_0
		// 08b: invokevirtual net/minecraft/util/math/Vec3d.add (DDD)Lnet/minecraft/util/math/Vec3d;
		// 08e: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.setVelocity (Lnet/minecraft/util/math/Vec3d;)V
		// 091: goto 3b0
		// 094: aload 0
		// 095: getfield net/minecraft/entity/projectile/FishingBobberEntity.fishTravelCountdown I
		// 098: ifle 255
		// 09b: aload 0
		// 09c: dup
		// 09d: getfield net/minecraft/entity/projectile/FishingBobberEntity.fishTravelCountdown I
		// 0a0: iload 3
		// 0a1: isub
		// 0a2: putfield net/minecraft/entity/projectile/FishingBobberEntity.fishTravelCountdown I
		// 0a5: aload 0
		// 0a6: getfield net/minecraft/entity/projectile/FishingBobberEntity.fishTravelCountdown I
		// 0a9: ifle 1a0
		// 0ac: aload 0
		// 0ad: dup
		// 0ae: getfield net/minecraft/entity/projectile/FishingBobberEntity.fishAngle F
		// 0b1: f2d
		// 0b2: aload 0
		// 0b3: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 0b6: invokevirtual java/util/Random.nextGaussian ()D
		// 0b9: ldc2_w 4.0
		// 0bc: dmul
		// 0bd: dadd
		// 0be: d2f
		// 0bf: putfield net/minecraft/entity/projectile/FishingBobberEntity.fishAngle F
		// 0c2: aload 0
		// 0c3: getfield net/minecraft/entity/projectile/FishingBobberEntity.fishAngle F
		// 0c6: ldc 0.017453292
		// 0c8: fmul
		// 0c9: fstore 5
		// 0cb: fload 5
		// 0cd: invokestatic net/minecraft/util/math/MathHelper.sin (F)F
		// 0d0: fstore 6
		// 0d2: fload 5
		// 0d4: invokestatic net/minecraft/util/math/MathHelper.cos (F)F
		// 0d7: fstore 7
		// 0d9: aload 0
		// 0da: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getX ()D
		// 0dd: fload 6
		// 0df: aload 0
		// 0e0: getfield net/minecraft/entity/projectile/FishingBobberEntity.fishTravelCountdown I
		// 0e3: i2f
		// 0e4: fmul
		// 0e5: ldc_w 0.1
		// 0e8: fmul
		// 0e9: f2d
		// 0ea: dadd
		// 0eb: dstore 8
		// 0ed: aload 0
		// 0ee: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getY ()D
		// 0f1: invokestatic net/minecraft/util/math/MathHelper.floor (D)I
		// 0f4: i2f
		// 0f5: fconst_1
		// 0f6: fadd
		// 0f7: f2d
		// 0f8: dstore 10
		// 0fa: aload 0
		// 0fb: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getZ ()D
		// 0fe: fload 7
		// 100: aload 0
		// 101: getfield net/minecraft/entity/projectile/FishingBobberEntity.fishTravelCountdown I
		// 104: i2f
		// 105: fmul
		// 106: ldc_w 0.1
		// 109: fmul
		// 10a: f2d
		// 10b: dadd
		// 10c: dstore 12
		// 10e: aload 2
		// 10f: new net/minecraft/util/math/BlockPos
		// 112: dup
		// 113: dload 8
		// 115: dload 10
		// 117: dconst_1
		// 118: dsub
		// 119: dload 12
		// 11b: invokespecial net/minecraft/util/math/BlockPos.<init> (DDD)V
		// 11e: invokevirtual net/minecraft/server/world/ServerWorld.getBlockState (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;
		// 121: invokevirtual net/minecraft/block/BlockState.getBlock ()Lnet/minecraft/block/Block;
		// 124: astore 14
		// 126: aload 14
		// 128: getstatic net/minecraft/block/Blocks.field_10382 Lnet/minecraft/block/Block;
		// 12b: if_acmpne 19d
		// 12e: aload 0
		// 12f: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 132: invokevirtual java/util/Random.nextFloat ()F
		// 135: ldc_w 0.15
		// 138: fcmpg
		// 139: ifge 159
		// 13c: aload 2
		// 13d: getstatic net/minecraft/particle/ParticleTypes.field_11247 Lnet/minecraft/particle/DefaultParticleType;
		// 140: dload 8
		// 142: dload 10
		// 144: ldc2_w 0.10000000149011612
		// 147: dsub
		// 148: dload 12
		// 14a: bipush 1
		// 14b: fload 6
		// 14d: f2d
		// 14e: ldc2_w 0.1
		// 151: fload 7
		// 153: f2d
		// 154: dconst_0
		// 155: invokevirtual net/minecraft/server/world/ServerWorld.spawnParticles (Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I
		// 158: pop
		// 159: fload 6
		// 15b: ldc_w 0.04
		// 15e: fmul
		// 15f: fstore 15
		// 161: fload 7
		// 163: ldc_w 0.04
		// 166: fmul
		// 167: fstore 16
		// 169: aload 2
		// 16a: getstatic net/minecraft/particle/ParticleTypes.field_11244 Lnet/minecraft/particle/DefaultParticleType;
		// 16d: dload 8
		// 16f: dload 10
		// 171: dload 12
		// 173: bipush 0
		// 174: fload 16
		// 176: f2d
		// 177: ldc2_w 0.01
		// 17a: fload 15
		// 17c: fneg
		// 17d: f2d
		// 17e: dconst_1
		// 17f: invokevirtual net/minecraft/server/world/ServerWorld.spawnParticles (Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I
		// 182: pop
		// 183: aload 2
		// 184: getstatic net/minecraft/particle/ParticleTypes.field_11244 Lnet/minecraft/particle/DefaultParticleType;
		// 187: dload 8
		// 189: dload 10
		// 18b: dload 12
		// 18d: bipush 0
		// 18e: fload 16
		// 190: fneg
		// 191: f2d
		// 192: ldc2_w 0.01
		// 195: fload 15
		// 197: f2d
		// 198: dconst_1
		// 199: invokevirtual net/minecraft/server/world/ServerWorld.spawnParticles (Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I
		// 19c: pop
		// 19d: goto 3b0
		// 1a0: aload 0
		// 1a1: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 1a4: astore 5
		// 1a6: aload 0
		// 1a7: aload 5
		// 1a9: getfield net/minecraft/util/math/Vec3d.x D
		// 1ac: ldc_w -0.4
		// 1af: aload 0
		// 1b0: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 1b3: ldc_w 0.6
		// 1b6: fconst_1
		// 1b7: invokestatic net/minecraft/util/math/MathHelper.nextFloat (Ljava/util/Random;FF)F
		// 1ba: fmul
		// 1bb: f2d
		// 1bc: aload 5
		// 1be: getfield net/minecraft/util/math/Vec3d.z D
		// 1c1: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.setVelocity (DDD)V
		// 1c4: aload 0
		// 1c5: getstatic net/minecraft/sound/SoundEvents.field_14660 Lnet/minecraft/sound/SoundEvent;
		// 1c8: ldc_w 0.25
		// 1cb: fconst_1
		// 1cc: aload 0
		// 1cd: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 1d0: invokevirtual java/util/Random.nextFloat ()F
		// 1d3: aload 0
		// 1d4: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 1d7: invokevirtual java/util/Random.nextFloat ()F
		// 1da: fsub
		// 1db: ldc_w 0.4
		// 1de: fmul
		// 1df: fadd
		// 1e0: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.playSound (Lnet/minecraft/sound/SoundEvent;FF)V
		// 1e3: aload 0
		// 1e4: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getY ()D
		// 1e7: ldc2_w 0.5
		// 1ea: dadd
		// 1eb: dstore 6
		// 1ed: aload 2
		// 1ee: getstatic net/minecraft/particle/ParticleTypes.field_11247 Lnet/minecraft/particle/DefaultParticleType;
		// 1f1: aload 0
		// 1f2: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getX ()D
		// 1f5: dload 6
		// 1f7: aload 0
		// 1f8: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getZ ()D
		// 1fb: fconst_1
		// 1fc: aload 0
		// 1fd: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getWidth ()F
		// 200: ldc_w 20.0
		// 203: fmul
		// 204: fadd
		// 205: f2i
		// 206: aload 0
		// 207: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getWidth ()F
		// 20a: f2d
		// 20b: dconst_0
		// 20c: aload 0
		// 20d: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getWidth ()F
		// 210: f2d
		// 211: ldc2_w 0.20000000298023224
		// 214: invokevirtual net/minecraft/server/world/ServerWorld.spawnParticles (Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I
		// 217: pop
		// 218: aload 2
		// 219: getstatic net/minecraft/particle/ParticleTypes.field_11244 Lnet/minecraft/particle/DefaultParticleType;
		// 21c: aload 0
		// 21d: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getX ()D
		// 220: dload 6
		// 222: aload 0
		// 223: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getZ ()D
		// 226: fconst_1
		// 227: aload 0
		// 228: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getWidth ()F
		// 22b: ldc_w 20.0
		// 22e: fmul
		// 22f: fadd
		// 230: f2i
		// 231: aload 0
		// 232: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getWidth ()F
		// 235: f2d
		// 236: dconst_0
		// 237: aload 0
		// 238: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getWidth ()F
		// 23b: f2d
		// 23c: ldc2_w 0.20000000298023224
		// 23f: invokevirtual net/minecraft/server/world/ServerWorld.spawnParticles (Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I
		// 242: pop
		// 243: aload 0
		// 244: aload 0
		// 245: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 248: bipush 20
		// 24a: bipush 40
		// 24c: invokestatic net/minecraft/util/math/MathHelper.nextInt (Ljava/util/Random;II)I
		// 24f: putfield net/minecraft/entity/projectile/FishingBobberEntity.hookCountdown I
		// 252: goto 3b0
		// 255: aload 0
		// 256: getfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 259: ifle 38e
		// 25c: aload 0
		// 25d: dup
		// 25e: getfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 261: iload 3
		// 262: isub
		// 263: putfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 266: ldc_w 0.15
		// 269: fstore 5
		// 26b: aload 0
		// 26c: getfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 26f: bipush 20
		// 271: if_icmpge 28a
		// 274: fload 5
		// 276: f2d
		// 277: bipush 20
		// 279: aload 0
		// 27a: getfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 27d: isub
		// 27e: i2d
		// 27f: ldc2_w 0.05
		// 282: dmul
		// 283: dadd
		// 284: d2f
		// 285: fstore 5
		// 287: goto 2c5
		// 28a: aload 0
		// 28b: getfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 28e: bipush 40
		// 290: if_icmpge 2a9
		// 293: fload 5
		// 295: f2d
		// 296: bipush 40
		// 298: aload 0
		// 299: getfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 29c: isub
		// 29d: i2d
		// 29e: ldc2_w 0.02
		// 2a1: dmul
		// 2a2: dadd
		// 2a3: d2f
		// 2a4: fstore 5
		// 2a6: goto 2c5
		// 2a9: aload 0
		// 2aa: getfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 2ad: bipush 60
		// 2af: if_icmpge 2c5
		// 2b2: fload 5
		// 2b4: f2d
		// 2b5: bipush 60
		// 2b7: aload 0
		// 2b8: getfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 2bb: isub
		// 2bc: i2d
		// 2bd: ldc2_w 0.01
		// 2c0: dmul
		// 2c1: dadd
		// 2c2: d2f
		// 2c3: fstore 5
		// 2c5: aload 0
		// 2c6: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 2c9: invokevirtual java/util/Random.nextFloat ()F
		// 2cc: fload 5
		// 2ce: fcmpg
		// 2cf: ifge 366
		// 2d2: aload 0
		// 2d3: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 2d6: fconst_0
		// 2d7: ldc_w 360.0
		// 2da: invokestatic net/minecraft/util/math/MathHelper.nextFloat (Ljava/util/Random;FF)F
		// 2dd: ldc 0.017453292
		// 2df: fmul
		// 2e0: fstore 6
		// 2e2: aload 0
		// 2e3: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 2e6: ldc_w 25.0
		// 2e9: ldc_w 60.0
		// 2ec: invokestatic net/minecraft/util/math/MathHelper.nextFloat (Ljava/util/Random;FF)F
		// 2ef: fstore 7
		// 2f1: aload 0
		// 2f2: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getX ()D
		// 2f5: fload 6
		// 2f7: invokestatic net/minecraft/util/math/MathHelper.sin (F)F
		// 2fa: fload 7
		// 2fc: fmul
		// 2fd: ldc_w 0.1
		// 300: fmul
		// 301: f2d
		// 302: dadd
		// 303: dstore 8
		// 305: aload 0
		// 306: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getY ()D
		// 309: invokestatic net/minecraft/util/math/MathHelper.floor (D)I
		// 30c: i2f
		// 30d: fconst_1
		// 30e: fadd
		// 30f: f2d
		// 310: dstore 10
		// 312: aload 0
		// 313: invokevirtual net/minecraft/entity/projectile/FishingBobberEntity.getZ ()D
		// 316: fload 6
		// 318: invokestatic net/minecraft/util/math/MathHelper.cos (F)F
		// 31b: fload 7
		// 31d: fmul
		// 31e: ldc_w 0.1
		// 321: fmul
		// 322: f2d
		// 323: dadd
		// 324: dstore 12
		// 326: aload 2
		// 327: new net/minecraft/util/math/BlockPos
		// 32a: dup
		// 32b: dload 8
		// 32d: dload 10
		// 32f: dconst_1
		// 330: dsub
		// 331: dload 12
		// 333: invokespecial net/minecraft/util/math/BlockPos.<init> (DDD)V
		// 336: invokevirtual net/minecraft/server/world/ServerWorld.getBlockState (Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;
		// 339: invokevirtual net/minecraft/block/BlockState.getBlock ()Lnet/minecraft/block/Block;
		// 33c: astore 14
		// 33e: aload 14
		// 340: getstatic net/minecraft/block/Blocks.field_10382 Lnet/minecraft/block/Block;
		// 343: if_acmpne 366
		// 346: aload 2
		// 347: getstatic net/minecraft/particle/ParticleTypes.field_11202 Lnet/minecraft/particle/DefaultParticleType;
		// 34a: dload 8
		// 34c: dload 10
		// 34e: dload 12
		// 350: bipush 2
		// 351: aload 0
		// 352: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 355: bipush 2
		// 356: invokevirtual java/util/Random.nextInt (I)I
		// 359: iadd
		// 35a: ldc2_w 0.10000000149011612
		// 35d: dconst_0
		// 35e: ldc2_w 0.10000000149011612
		// 361: dconst_0
		// 362: invokevirtual net/minecraft/server/world/ServerWorld.spawnParticles (Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I
		// 365: pop
		// 366: aload 0
		// 367: getfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 36a: ifgt 38b
		// 36d: aload 0
		// 36e: aload 0
		// 36f: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 372: fconst_0
		// 373: ldc_w 360.0
		// 376: invokestatic net/minecraft/util/math/MathHelper.nextFloat (Ljava/util/Random;FF)F
		// 379: putfield net/minecraft/entity/projectile/FishingBobberEntity.fishAngle F
		// 37c: aload 0
		// 37d: aload 0
		// 37e: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 381: bipush 20
		// 383: bipush 80
		// 385: invokestatic net/minecraft/util/math/MathHelper.nextInt (Ljava/util/Random;II)I
		// 388: putfield net/minecraft/entity/projectile/FishingBobberEntity.fishTravelCountdown I
		// 38b: goto 3b0
		// 38e: aload 0
		// 38f: aload 0
		// 390: getfield net/minecraft/entity/projectile/FishingBobberEntity.random Ljava/util/Random;
		// 393: bipush 100
		// 395: sipush 600
		// 398: invokestatic net/minecraft/util/math/MathHelper.nextInt (Ljava/util/Random;II)I
		// 39b: putfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 39e: aload 0
		// 39f: dup
		// 3a0: getfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 3a3: aload 0
		// 3a4: getfield net/minecraft/entity/projectile/FishingBobberEntity.lureLevel I
		// 3a7: bipush 20
		// 3a9: imul
		// 3aa: bipush 5
		// 3ab: imul
		// 3ac: isub
		// 3ad: putfield net/minecraft/entity/projectile/FishingBobberEntity.waitCountdown I
		// 3b0: return
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
	}

	public int use(ItemStack itemStack) {
		if (!this.world.isClient && this.owner != null) {
			int i = 0;
			if (this.hookedEntity != null) {
				this.pullHookedEntity();
				Criterions.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)this.owner, itemStack, this, Collections.emptyList());
				this.world.sendEntityStatus(this, (byte)31);
				i = this.hookedEntity instanceof ItemEntity ? 3 : 5;
			} else if (this.hookCountdown > 0) {
				LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
					.put(LootContextParameters.field_1232, new BlockPos(this))
					.put(LootContextParameters.field_1229, itemStack)
					.setRandom(this.random)
					.setLuck((float)this.luckOfTheSeaLevel + this.owner.getLuck());
				LootTable lootTable = this.world.getServer().getLootManager().getSupplier(LootTables.field_353);
				List<ItemStack> list = lootTable.getDrops(builder.build(LootContextTypes.field_1176));
				Criterions.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)this.owner, itemStack, this, list);

				for (ItemStack itemStack2 : list) {
					ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), itemStack2);
					double d = this.owner.getX() - this.getX();
					double e = this.owner.getY() - this.getY();
					double f = this.owner.getZ() - this.getZ();
					double g = 0.1;
					itemEntity.setVelocity(d * 0.1, e * 0.1 + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08, f * 0.1);
					this.world.spawnEntity(itemEntity);
					this.owner
						.world
						.spawnEntity(new ExperienceOrbEntity(this.owner.world, this.owner.getX(), this.owner.getY() + 0.5, this.owner.getZ() + 0.5, this.random.nextInt(6) + 1));
					if (itemStack2.getItem().isIn(ItemTags.field_15527)) {
						this.owner.increaseStat(Stats.field_15391, 1);
					}
				}

				i = 1;
			}

			if (this.stuckOnBlock) {
				i = 2;
			}

			this.remove();
			return i;
		} else {
			return 0;
		}
	}

	@Override
	public void handleStatus(byte b) {
		if (b == 31 && this.world.isClient && this.hookedEntity instanceof PlayerEntity && ((PlayerEntity)this.hookedEntity).isMainPlayer()) {
			this.pullHookedEntity();
		}

		super.handleStatus(b);
	}

	protected void pullHookedEntity() {
		if (this.owner != null) {
			Vec3d vec3d = new Vec3d(this.owner.getX() - this.getX(), this.owner.getY() - this.getY(), this.owner.getZ() - this.getZ()).multiply(0.1);
			this.hookedEntity.setVelocity(this.hookedEntity.getVelocity().add(vec3d));
		}
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	public void remove() {
		super.remove();
		if (this.owner != null) {
			this.owner.fishHook = null;
		}
	}

	@Nullable
	public PlayerEntity getOwner() {
		return this.owner;
	}

	@Override
	public boolean canUsePortals() {
		return false;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		Entity entity = this.getOwner();
		return new EntitySpawnS2CPacket(this, entity == null ? this.getEntityId() : entity.getEntityId());
	}

	static enum State {
		field_7180,
		field_7178,
		field_7179;
	}
}
