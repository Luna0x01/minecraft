package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class GuardianEntity extends HostileEntity {
	private static final TrackedData<Boolean> SPIKES_RETRACTED = DataTracker.registerData(GuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> BEAM_TARGET_ID = DataTracker.registerData(GuardianEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected float spikesExtension;
	protected float prevSpikesExtension;
	protected float spikesExtensionRate;
	protected float tailAngle;
	protected float prevTailAngle;
	private LivingEntity cachedBeamTarget;
	private int beamTicks;
	private boolean flopping;
	protected WanderAroundGoal wanderGoal;

	public GuardianEntity(EntityType<? extends GuardianEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 10;
		this.setPathfindingPenalty(PathNodeType.field_18, 0.0F);
		this.moveControl = new GuardianEntity.GuardianMoveControl(this);
		this.spikesExtension = this.random.nextFloat();
		this.prevSpikesExtension = this.spikesExtension;
	}

	@Override
	protected void initGoals() {
		GoToWalkTargetGoal goToWalkTargetGoal = new GoToWalkTargetGoal(this, 1.0);
		this.wanderGoal = new WanderAroundGoal(this, 1.0, 80);
		this.goalSelector.add(4, new GuardianEntity.FireBeamGoal(this));
		this.goalSelector.add(5, goToWalkTargetGoal);
		this.goalSelector.add(7, this.wanderGoal);
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new LookAtEntityGoal(this, GuardianEntity.class, 12.0F, 0.01F));
		this.goalSelector.add(9, new LookAroundGoal(this));
		this.wanderGoal.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
		goToWalkTargetGoal.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
		this.targetSelector.add(1, new FollowTargetGoal(this, LivingEntity.class, 10, true, false, new GuardianEntity.GuardianTargetPredicate(this)));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(16.0);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SPIKES_RETRACTED, false);
		this.dataTracker.startTracking(BEAM_TARGET_ID, 0);
	}

	@Override
	public boolean canBreatheInWater() {
		return true;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.AQUATIC;
	}

	public boolean areSpikesRetracted() {
		return this.dataTracker.get(SPIKES_RETRACTED);
	}

	private void setSpikesRetracted(boolean bl) {
		this.dataTracker.set(SPIKES_RETRACTED, bl);
	}

	public int getWarmupTime() {
		return 80;
	}

	private void setBeamTarget(int i) {
		this.dataTracker.set(BEAM_TARGET_ID, i);
	}

	public boolean hasBeamTarget() {
		return this.dataTracker.get(BEAM_TARGET_ID) != 0;
	}

	@Nullable
	public LivingEntity getBeamTarget() {
		if (!this.hasBeamTarget()) {
			return null;
		} else if (this.world.isClient) {
			if (this.cachedBeamTarget != null) {
				return this.cachedBeamTarget;
			} else {
				Entity entity = this.world.getEntityById(this.dataTracker.get(BEAM_TARGET_ID));
				if (entity instanceof LivingEntity) {
					this.cachedBeamTarget = (LivingEntity)entity;
					return this.cachedBeamTarget;
				} else {
					return null;
				}
			}
		} else {
			return this.getTarget();
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		super.onTrackedDataSet(trackedData);
		if (BEAM_TARGET_ID.equals(trackedData)) {
			this.beamTicks = 0;
			this.cachedBeamTarget = null;
		}
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 160;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isInsideWaterOrBubbleColumn() ? SoundEvents.field_14714 : SoundEvents.field_14968;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return this.isInsideWaterOrBubbleColumn() ? SoundEvents.field_14679 : SoundEvents.field_14758;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isInsideWaterOrBubbleColumn() ? SoundEvents.field_15138 : SoundEvents.field_15232;
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return entityDimensions.height * 0.5F;
	}

	@Override
	public float getPathfindingFavor(BlockPos blockPos, WorldView worldView) {
		return worldView.getFluidState(blockPos).matches(FluidTags.field_15517)
			? 10.0F + worldView.getBrightness(blockPos) - 0.5F
			: super.getPathfindingFavor(blockPos, worldView);
	}

	@Override
	public void tickMovement() {
		// $VF: Couldn't be decompiled
		// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
		//
		// Bytecode:
		// 000: aload 0
		// 001: invokevirtual net/minecraft/entity/mob/GuardianEntity.isAlive ()Z
		// 004: ifeq 2dd
		// 007: aload 0
		// 008: getfield net/minecraft/entity/mob/GuardianEntity.world Lnet/minecraft/world/World;
		// 00b: getfield net/minecraft/world/World.isClient Z
		// 00e: ifeq 26f
		// 011: aload 0
		// 012: aload 0
		// 013: getfield net/minecraft/entity/mob/GuardianEntity.spikesExtension F
		// 016: putfield net/minecraft/entity/mob/GuardianEntity.prevSpikesExtension F
		// 019: aload 0
		// 01a: invokevirtual net/minecraft/entity/mob/GuardianEntity.isTouchingWater ()Z
		// 01d: ifne 08a
		// 020: aload 0
		// 021: fconst_2
		// 022: putfield net/minecraft/entity/mob/GuardianEntity.spikesExtensionRate F
		// 025: aload 0
		// 026: invokevirtual net/minecraft/entity/mob/GuardianEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 029: astore 1
		// 02a: aload 1
		// 02b: getfield net/minecraft/util/math/Vec3d.y D
		// 02e: dconst_0
		// 02f: dcmpl
		// 030: ifle 05f
		// 033: aload 0
		// 034: getfield net/minecraft/entity/mob/GuardianEntity.flopping Z
		// 037: ifeq 05f
		// 03a: aload 0
		// 03b: invokevirtual net/minecraft/entity/mob/GuardianEntity.isSilent ()Z
		// 03e: ifne 05f
		// 041: aload 0
		// 042: getfield net/minecraft/entity/mob/GuardianEntity.world Lnet/minecraft/world/World;
		// 045: aload 0
		// 046: invokevirtual net/minecraft/entity/mob/GuardianEntity.getX ()D
		// 049: aload 0
		// 04a: invokevirtual net/minecraft/entity/mob/GuardianEntity.getY ()D
		// 04d: aload 0
		// 04e: invokevirtual net/minecraft/entity/mob/GuardianEntity.getZ ()D
		// 051: aload 0
		// 052: invokevirtual net/minecraft/entity/mob/GuardianEntity.getFlopSound ()Lnet/minecraft/sound/SoundEvent;
		// 055: aload 0
		// 056: invokevirtual net/minecraft/entity/mob/GuardianEntity.getSoundCategory ()Lnet/minecraft/sound/SoundCategory;
		// 059: fconst_1
		// 05a: fconst_1
		// 05b: bipush 0
		// 05c: invokevirtual net/minecraft/world/World.playSound (DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V
		// 05f: aload 0
		// 060: aload 1
		// 061: getfield net/minecraft/util/math/Vec3d.y D
		// 064: dconst_0
		// 065: dcmpg
		// 066: ifge 083
		// 069: aload 0
		// 06a: getfield net/minecraft/entity/mob/GuardianEntity.world Lnet/minecraft/world/World;
		// 06d: new net/minecraft/util/math/BlockPos
		// 070: dup
		// 071: aload 0
		// 072: invokespecial net/minecraft/util/math/BlockPos.<init> (Lnet/minecraft/entity/Entity;)V
		// 075: invokevirtual net/minecraft/util/math/BlockPos.down ()Lnet/minecraft/util/math/BlockPos;
		// 078: aload 0
		// 079: invokevirtual net/minecraft/world/World.isTopSolid (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)Z
		// 07c: ifeq 083
		// 07f: bipush 1
		// 080: goto 084
		// 083: bipush 0
		// 084: putfield net/minecraft/entity/mob/GuardianEntity.flopping Z
		// 087: goto 0d3
		// 08a: aload 0
		// 08b: invokevirtual net/minecraft/entity/mob/GuardianEntity.areSpikesRetracted ()Z
		// 08e: ifeq 0be
		// 091: aload 0
		// 092: getfield net/minecraft/entity/mob/GuardianEntity.spikesExtensionRate F
		// 095: ldc_w 0.5
		// 098: fcmpg
		// 099: ifge 0a6
		// 09c: aload 0
		// 09d: ldc_w 4.0
		// 0a0: putfield net/minecraft/entity/mob/GuardianEntity.spikesExtensionRate F
		// 0a3: goto 0d3
		// 0a6: aload 0
		// 0a7: dup
		// 0a8: getfield net/minecraft/entity/mob/GuardianEntity.spikesExtensionRate F
		// 0ab: ldc_w 0.5
		// 0ae: aload 0
		// 0af: getfield net/minecraft/entity/mob/GuardianEntity.spikesExtensionRate F
		// 0b2: fsub
		// 0b3: ldc_w 0.1
		// 0b6: fmul
		// 0b7: fadd
		// 0b8: putfield net/minecraft/entity/mob/GuardianEntity.spikesExtensionRate F
		// 0bb: goto 0d3
		// 0be: aload 0
		// 0bf: dup
		// 0c0: getfield net/minecraft/entity/mob/GuardianEntity.spikesExtensionRate F
		// 0c3: ldc_w 0.125
		// 0c6: aload 0
		// 0c7: getfield net/minecraft/entity/mob/GuardianEntity.spikesExtensionRate F
		// 0ca: fsub
		// 0cb: ldc_w 0.2
		// 0ce: fmul
		// 0cf: fadd
		// 0d0: putfield net/minecraft/entity/mob/GuardianEntity.spikesExtensionRate F
		// 0d3: aload 0
		// 0d4: dup
		// 0d5: getfield net/minecraft/entity/mob/GuardianEntity.spikesExtension F
		// 0d8: aload 0
		// 0d9: getfield net/minecraft/entity/mob/GuardianEntity.spikesExtensionRate F
		// 0dc: fadd
		// 0dd: putfield net/minecraft/entity/mob/GuardianEntity.spikesExtension F
		// 0e0: aload 0
		// 0e1: aload 0
		// 0e2: getfield net/minecraft/entity/mob/GuardianEntity.tailAngle F
		// 0e5: putfield net/minecraft/entity/mob/GuardianEntity.prevTailAngle F
		// 0e8: aload 0
		// 0e9: invokevirtual net/minecraft/entity/mob/GuardianEntity.isInsideWaterOrBubbleColumn ()Z
		// 0ec: ifne 0fd
		// 0ef: aload 0
		// 0f0: aload 0
		// 0f1: getfield net/minecraft/entity/mob/GuardianEntity.random Ljava/util/Random;
		// 0f4: invokevirtual java/util/Random.nextFloat ()F
		// 0f7: putfield net/minecraft/entity/mob/GuardianEntity.tailAngle F
		// 0fa: goto 12d
		// 0fd: aload 0
		// 0fe: invokevirtual net/minecraft/entity/mob/GuardianEntity.areSpikesRetracted ()Z
		// 101: ifeq 11a
		// 104: aload 0
		// 105: dup
		// 106: getfield net/minecraft/entity/mob/GuardianEntity.tailAngle F
		// 109: fconst_0
		// 10a: aload 0
		// 10b: getfield net/minecraft/entity/mob/GuardianEntity.tailAngle F
		// 10e: fsub
		// 10f: ldc_w 0.25
		// 112: fmul
		// 113: fadd
		// 114: putfield net/minecraft/entity/mob/GuardianEntity.tailAngle F
		// 117: goto 12d
		// 11a: aload 0
		// 11b: dup
		// 11c: getfield net/minecraft/entity/mob/GuardianEntity.tailAngle F
		// 11f: fconst_1
		// 120: aload 0
		// 121: getfield net/minecraft/entity/mob/GuardianEntity.tailAngle F
		// 124: fsub
		// 125: ldc_w 0.06
		// 128: fmul
		// 129: fadd
		// 12a: putfield net/minecraft/entity/mob/GuardianEntity.tailAngle F
		// 12d: aload 0
		// 12e: invokevirtual net/minecraft/entity/mob/GuardianEntity.areSpikesRetracted ()Z
		// 131: ifeq 188
		// 134: aload 0
		// 135: invokevirtual net/minecraft/entity/mob/GuardianEntity.isTouchingWater ()Z
		// 138: ifeq 188
		// 13b: aload 0
		// 13c: fconst_0
		// 13d: invokevirtual net/minecraft/entity/mob/GuardianEntity.getRotationVec (F)Lnet/minecraft/util/math/Vec3d;
		// 140: astore 1
		// 141: bipush 0
		// 142: istore 2
		// 143: iload 2
		// 144: bipush 2
		// 145: if_icmpge 188
		// 148: aload 0
		// 149: getfield net/minecraft/entity/mob/GuardianEntity.world Lnet/minecraft/world/World;
		// 14c: getstatic net/minecraft/particle/ParticleTypes.field_11247 Lnet/minecraft/particle/DefaultParticleType;
		// 14f: aload 0
		// 150: ldc2_w 0.5
		// 153: invokevirtual net/minecraft/entity/mob/GuardianEntity.getParticleX (D)D
		// 156: aload 1
		// 157: getfield net/minecraft/util/math/Vec3d.x D
		// 15a: ldc2_w 1.5
		// 15d: dmul
		// 15e: dsub
		// 15f: aload 0
		// 160: invokevirtual net/minecraft/entity/mob/GuardianEntity.getRandomBodyY ()D
		// 163: aload 1
		// 164: getfield net/minecraft/util/math/Vec3d.y D
		// 167: ldc2_w 1.5
		// 16a: dmul
		// 16b: dsub
		// 16c: aload 0
		// 16d: ldc2_w 0.5
		// 170: invokevirtual net/minecraft/entity/mob/GuardianEntity.getParticleZ (D)D
		// 173: aload 1
		// 174: getfield net/minecraft/util/math/Vec3d.z D
		// 177: ldc2_w 1.5
		// 17a: dmul
		// 17b: dsub
		// 17c: dconst_0
		// 17d: dconst_0
		// 17e: dconst_0
		// 17f: invokevirtual net/minecraft/world/World.addParticle (Lnet/minecraft/particle/ParticleEffect;DDDDDD)V
		// 182: iinc 2 1
		// 185: goto 143
		// 188: aload 0
		// 189: invokevirtual net/minecraft/entity/mob/GuardianEntity.hasBeamTarget ()Z
		// 18c: ifeq 26f
		// 18f: aload 0
		// 190: getfield net/minecraft/entity/mob/GuardianEntity.beamTicks I
		// 193: aload 0
		// 194: invokevirtual net/minecraft/entity/mob/GuardianEntity.getWarmupTime ()I
		// 197: if_icmpge 1a4
		// 19a: aload 0
		// 19b: dup
		// 19c: getfield net/minecraft/entity/mob/GuardianEntity.beamTicks I
		// 19f: bipush 1
		// 1a0: iadd
		// 1a1: putfield net/minecraft/entity/mob/GuardianEntity.beamTicks I
		// 1a4: aload 0
		// 1a5: invokevirtual net/minecraft/entity/mob/GuardianEntity.getBeamTarget ()Lnet/minecraft/entity/LivingEntity;
		// 1a8: astore 1
		// 1a9: aload 1
		// 1aa: ifnull 26f
		// 1ad: aload 0
		// 1ae: invokevirtual net/minecraft/entity/mob/GuardianEntity.getLookControl ()Lnet/minecraft/entity/ai/control/LookControl;
		// 1b1: aload 1
		// 1b2: ldc_w 90.0
		// 1b5: ldc_w 90.0
		// 1b8: invokevirtual net/minecraft/entity/ai/control/LookControl.lookAt (Lnet/minecraft/entity/Entity;FF)V
		// 1bb: aload 0
		// 1bc: invokevirtual net/minecraft/entity/mob/GuardianEntity.getLookControl ()Lnet/minecraft/entity/ai/control/LookControl;
		// 1bf: invokevirtual net/minecraft/entity/ai/control/LookControl.tick ()V
		// 1c2: aload 0
		// 1c3: fconst_0
		// 1c4: invokevirtual net/minecraft/entity/mob/GuardianEntity.getBeamProgress (F)F
		// 1c7: f2d
		// 1c8: dstore 2
		// 1c9: aload 1
		// 1ca: invokevirtual net/minecraft/entity/LivingEntity.getX ()D
		// 1cd: aload 0
		// 1ce: invokevirtual net/minecraft/entity/mob/GuardianEntity.getX ()D
		// 1d1: dsub
		// 1d2: dstore 4
		// 1d4: aload 1
		// 1d5: ldc2_w 0.5
		// 1d8: invokevirtual net/minecraft/entity/LivingEntity.getBodyY (D)D
		// 1db: aload 0
		// 1dc: invokevirtual net/minecraft/entity/mob/GuardianEntity.getEyeY ()D
		// 1df: dsub
		// 1e0: dstore 6
		// 1e2: aload 1
		// 1e3: invokevirtual net/minecraft/entity/LivingEntity.getZ ()D
		// 1e6: aload 0
		// 1e7: invokevirtual net/minecraft/entity/mob/GuardianEntity.getZ ()D
		// 1ea: dsub
		// 1eb: dstore 8
		// 1ed: dload 4
		// 1ef: dload 4
		// 1f1: dmul
		// 1f2: dload 6
		// 1f4: dload 6
		// 1f6: dmul
		// 1f7: dadd
		// 1f8: dload 8
		// 1fa: dload 8
		// 1fc: dmul
		// 1fd: dadd
		// 1fe: invokestatic java/lang/Math.sqrt (D)D
		// 201: dstore 10
		// 203: dload 4
		// 205: dload 10
		// 207: ddiv
		// 208: dstore 4
		// 20a: dload 6
		// 20c: dload 10
		// 20e: ddiv
		// 20f: dstore 6
		// 211: dload 8
		// 213: dload 10
		// 215: ddiv
		// 216: dstore 8
		// 218: aload 0
		// 219: getfield net/minecraft/entity/mob/GuardianEntity.random Ljava/util/Random;
		// 21c: invokevirtual java/util/Random.nextDouble ()D
		// 21f: dstore 12
		// 221: dload 12
		// 223: dload 10
		// 225: dcmpg
		// 226: ifge 26f
		// 229: dload 12
		// 22b: ldc2_w 1.8
		// 22e: dload 2
		// 22f: dsub
		// 230: aload 0
		// 231: getfield net/minecraft/entity/mob/GuardianEntity.random Ljava/util/Random;
		// 234: invokevirtual java/util/Random.nextDouble ()D
		// 237: ldc2_w 1.7
		// 23a: dload 2
		// 23b: dsub
		// 23c: dmul
		// 23d: dadd
		// 23e: dadd
		// 23f: dstore 12
		// 241: aload 0
		// 242: getfield net/minecraft/entity/mob/GuardianEntity.world Lnet/minecraft/world/World;
		// 245: getstatic net/minecraft/particle/ParticleTypes.field_11247 Lnet/minecraft/particle/DefaultParticleType;
		// 248: aload 0
		// 249: invokevirtual net/minecraft/entity/mob/GuardianEntity.getX ()D
		// 24c: dload 4
		// 24e: dload 12
		// 250: dmul
		// 251: dadd
		// 252: aload 0
		// 253: invokevirtual net/minecraft/entity/mob/GuardianEntity.getEyeY ()D
		// 256: dload 6
		// 258: dload 12
		// 25a: dmul
		// 25b: dadd
		// 25c: aload 0
		// 25d: invokevirtual net/minecraft/entity/mob/GuardianEntity.getZ ()D
		// 260: dload 8
		// 262: dload 12
		// 264: dmul
		// 265: dadd
		// 266: dconst_0
		// 267: dconst_0
		// 268: dconst_0
		// 269: invokevirtual net/minecraft/world/World.addParticle (Lnet/minecraft/particle/ParticleEffect;DDDDDD)V
		// 26c: goto 221
		// 26f: aload 0
		// 270: invokevirtual net/minecraft/entity/mob/GuardianEntity.isInsideWaterOrBubbleColumn ()Z
		// 273: ifeq 280
		// 276: aload 0
		// 277: sipush 300
		// 27a: invokevirtual net/minecraft/entity/mob/GuardianEntity.setAir (I)V
		// 27d: goto 2ce
		// 280: aload 0
		// 281: getfield net/minecraft/entity/mob/GuardianEntity.onGround Z
		// 284: ifeq 2ce
		// 287: aload 0
		// 288: aload 0
		// 289: invokevirtual net/minecraft/entity/mob/GuardianEntity.getVelocity ()Lnet/minecraft/util/math/Vec3d;
		// 28c: aload 0
		// 28d: getfield net/minecraft/entity/mob/GuardianEntity.random Ljava/util/Random;
		// 290: invokevirtual java/util/Random.nextFloat ()F
		// 293: fconst_2
		// 294: fmul
		// 295: fconst_1
		// 296: fsub
		// 297: ldc_w 0.4
		// 29a: fmul
		// 29b: f2d
		// 29c: ldc2_w 0.5
		// 29f: aload 0
		// 2a0: getfield net/minecraft/entity/mob/GuardianEntity.random Ljava/util/Random;
		// 2a3: invokevirtual java/util/Random.nextFloat ()F
		// 2a6: fconst_2
		// 2a7: fmul
		// 2a8: fconst_1
		// 2a9: fsub
		// 2aa: ldc_w 0.4
		// 2ad: fmul
		// 2ae: f2d
		// 2af: invokevirtual net/minecraft/util/math/Vec3d.add (DDD)Lnet/minecraft/util/math/Vec3d;
		// 2b2: invokevirtual net/minecraft/entity/mob/GuardianEntity.setVelocity (Lnet/minecraft/util/math/Vec3d;)V
		// 2b5: aload 0
		// 2b6: aload 0
		// 2b7: getfield net/minecraft/entity/mob/GuardianEntity.random Ljava/util/Random;
		// 2ba: invokevirtual java/util/Random.nextFloat ()F
		// 2bd: ldc_w 360.0
		// 2c0: fmul
		// 2c1: putfield net/minecraft/entity/mob/GuardianEntity.yaw F
		// 2c4: aload 0
		// 2c5: bipush 0
		// 2c6: putfield net/minecraft/entity/mob/GuardianEntity.onGround Z
		// 2c9: aload 0
		// 2ca: bipush 1
		// 2cb: putfield net/minecraft/entity/mob/GuardianEntity.velocityDirty Z
		// 2ce: aload 0
		// 2cf: invokevirtual net/minecraft/entity/mob/GuardianEntity.hasBeamTarget ()Z
		// 2d2: ifeq 2dd
		// 2d5: aload 0
		// 2d6: aload 0
		// 2d7: getfield net/minecraft/entity/mob/GuardianEntity.headYaw F
		// 2da: putfield net/minecraft/entity/mob/GuardianEntity.yaw F
		// 2dd: aload 0
		// 2de: invokespecial net/minecraft/entity/mob/HostileEntity.tickMovement ()V
		// 2e1: return
	}

	protected SoundEvent getFlopSound() {
		return SoundEvents.field_14584;
	}

	public float getSpikesExtension(float f) {
		return MathHelper.lerp(f, this.prevSpikesExtension, this.spikesExtension);
	}

	public float getTailAngle(float f) {
		return MathHelper.lerp(f, this.prevTailAngle, this.tailAngle);
	}

	public float getBeamProgress(float f) {
		return ((float)this.beamTicks + f) / (float)this.getWarmupTime();
	}

	@Override
	public boolean canSpawn(WorldView worldView) {
		return worldView.intersectsEntities(this);
	}

	public static boolean canSpawn(EntityType<? extends GuardianEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return (random.nextInt(20) == 0 || !iWorld.isSkyVisibleAllowingSea(blockPos))
			&& iWorld.getDifficulty() != Difficulty.field_5801
			&& (spawnType == SpawnType.field_16469 || iWorld.getFluidState(blockPos).matches(FluidTags.field_15517));
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (!this.areSpikesRetracted() && !damageSource.getMagic() && damageSource.getSource() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)damageSource.getSource();
			if (!damageSource.isExplosive()) {
				livingEntity.damage(DamageSource.thorns(this), 2.0F);
			}
		}

		if (this.wanderGoal != null) {
			this.wanderGoal.ignoreChanceOnce();
		}

		return super.damage(damageSource, f);
	}

	@Override
	public int getLookPitchSpeed() {
		return 180;
	}

	@Override
	public void travel(Vec3d vec3d) {
		if (this.canMoveVoluntarily() && this.isTouchingWater()) {
			this.updateVelocity(0.1F, vec3d);
			this.move(MovementType.field_6308, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.9));
			if (!this.areSpikesRetracted() && this.getTarget() == null) {
				this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
			}
		} else {
			super.travel(vec3d);
		}
	}

	static class FireBeamGoal extends Goal {
		private final GuardianEntity guardian;
		private int beamTicks;
		private final boolean elder;

		public FireBeamGoal(GuardianEntity guardianEntity) {
			this.guardian = guardianEntity;
			this.elder = guardianEntity instanceof ElderGuardianEntity;
			this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.guardian.getTarget();
			return livingEntity != null && livingEntity.isAlive();
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && (this.elder || this.guardian.squaredDistanceTo(this.guardian.getTarget()) > 9.0);
		}

		@Override
		public void start() {
			this.beamTicks = -10;
			this.guardian.getNavigation().stop();
			this.guardian.getLookControl().lookAt(this.guardian.getTarget(), 90.0F, 90.0F);
			this.guardian.velocityDirty = true;
		}

		@Override
		public void stop() {
			this.guardian.setBeamTarget(0);
			this.guardian.setTarget(null);
			this.guardian.wanderGoal.ignoreChanceOnce();
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = this.guardian.getTarget();
			this.guardian.getNavigation().stop();
			this.guardian.getLookControl().lookAt(livingEntity, 90.0F, 90.0F);
			if (!this.guardian.canSee(livingEntity)) {
				this.guardian.setTarget(null);
			} else {
				this.beamTicks++;
				if (this.beamTicks == 0) {
					this.guardian.setBeamTarget(this.guardian.getTarget().getEntityId());
					this.guardian.world.sendEntityStatus(this.guardian, (byte)21);
				} else if (this.beamTicks >= this.guardian.getWarmupTime()) {
					float f = 1.0F;
					if (this.guardian.world.getDifficulty() == Difficulty.field_5807) {
						f += 2.0F;
					}

					if (this.elder) {
						f += 2.0F;
					}

					livingEntity.damage(DamageSource.magic(this.guardian, this.guardian), f);
					livingEntity.damage(DamageSource.mob(this.guardian), (float)this.guardian.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue());
					this.guardian.setTarget(null);
				}

				super.tick();
			}
		}
	}

	static class GuardianMoveControl extends MoveControl {
		private final GuardianEntity guardian;

		public GuardianMoveControl(GuardianEntity guardianEntity) {
			super(guardianEntity);
			this.guardian = guardianEntity;
		}

		@Override
		public void tick() {
			if (this.state == MoveControl.State.field_6378 && !this.guardian.getNavigation().isIdle()) {
				Vec3d vec3d = new Vec3d(this.targetX - this.guardian.getX(), this.targetY - this.guardian.getY(), this.targetZ - this.guardian.getZ());
				double d = vec3d.length();
				double e = vec3d.x / d;
				double f = vec3d.y / d;
				double g = vec3d.z / d;
				float h = (float)(MathHelper.atan2(vec3d.z, vec3d.x) * 180.0F / (float)Math.PI) - 90.0F;
				this.guardian.yaw = this.changeAngle(this.guardian.yaw, h, 90.0F);
				this.guardian.bodyYaw = this.guardian.yaw;
				float i = (float)(this.speed * this.guardian.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
				float j = MathHelper.lerp(0.125F, this.guardian.getMovementSpeed(), i);
				this.guardian.setMovementSpeed(j);
				double k = Math.sin((double)(this.guardian.age + this.guardian.getEntityId()) * 0.5) * 0.05;
				double l = Math.cos((double)(this.guardian.yaw * (float) (Math.PI / 180.0)));
				double m = Math.sin((double)(this.guardian.yaw * (float) (Math.PI / 180.0)));
				double n = Math.sin((double)(this.guardian.age + this.guardian.getEntityId()) * 0.75) * 0.05;
				this.guardian.setVelocity(this.guardian.getVelocity().add(k * l, n * (m + l) * 0.25 + (double)j * f * 0.1, k * m));
				LookControl lookControl = this.guardian.getLookControl();
				double o = this.guardian.getX() + e * 2.0;
				double p = this.guardian.getEyeY() + f / d;
				double q = this.guardian.getZ() + g * 2.0;
				double r = lookControl.getLookX();
				double s = lookControl.getLookY();
				double t = lookControl.getLookZ();
				if (!lookControl.isActive()) {
					r = o;
					s = p;
					t = q;
				}

				this.guardian.getLookControl().lookAt(MathHelper.lerp(0.125, r, o), MathHelper.lerp(0.125, s, p), MathHelper.lerp(0.125, t, q), 10.0F, 40.0F);
				this.guardian.setSpikesRetracted(true);
			} else {
				this.guardian.setMovementSpeed(0.0F);
				this.guardian.setSpikesRetracted(false);
			}
		}
	}

	static class GuardianTargetPredicate implements Predicate<LivingEntity> {
		private final GuardianEntity owner;

		public GuardianTargetPredicate(GuardianEntity guardianEntity) {
			this.owner = guardianEntity;
		}

		public boolean test(@Nullable LivingEntity livingEntity) {
			return (livingEntity instanceof PlayerEntity || livingEntity instanceof SquidEntity) && livingEntity.squaredDistanceTo(this.owner) > 9.0;
		}
	}
}
