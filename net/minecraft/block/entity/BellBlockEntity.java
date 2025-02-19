package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableInt;

public class BellBlockEntity extends BlockEntity {
	private static final int field_31316 = 50;
	private static final int field_31317 = 60;
	private static final int field_31318 = 60;
	private static final int field_31319 = 40;
	private static final int field_31320 = 5;
	private static final int field_31321 = 48;
	private static final int field_31322 = 32;
	private static final int field_31323 = 48;
	private long lastRingTime;
	public int ringTicks;
	public boolean ringing;
	public Direction lastSideHit;
	private List<LivingEntity> hearingEntities;
	private boolean resonating;
	private int resonateTime;

	public BellBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.BELL, pos, state);
	}

	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		if (type == 1) {
			this.notifyMemoriesOfBell();
			this.resonateTime = 0;
			this.lastSideHit = Direction.byId(data);
			this.ringTicks = 0;
			this.ringing = true;
			return true;
		} else {
			return super.onSyncedBlockEvent(type, data);
		}
	}

	private static void tick(World world, BlockPos pos, BlockState state, BellBlockEntity blockEntity, BellBlockEntity.Effect bellEffect) {
		if (blockEntity.ringing) {
			blockEntity.ringTicks++;
		}

		if (blockEntity.ringTicks >= 50) {
			blockEntity.ringing = false;
			blockEntity.ringTicks = 0;
		}

		if (blockEntity.ringTicks >= 5 && blockEntity.resonateTime == 0 && raidersHearBell(pos, blockEntity.hearingEntities)) {
			blockEntity.resonating = true;
			world.playSound(null, pos, SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}

		if (blockEntity.resonating) {
			if (blockEntity.resonateTime < 40) {
				blockEntity.resonateTime++;
			} else {
				bellEffect.run(world, pos, blockEntity.hearingEntities);
				blockEntity.resonating = false;
			}
		}
	}

	public static void clientTick(World world, BlockPos pos, BlockState state, BellBlockEntity blockEntity) {
		tick(world, pos, state, blockEntity, BellBlockEntity::applyParticlesToRaiders);
	}

	public static void serverTick(World world, BlockPos pos, BlockState state, BellBlockEntity blockEntity) {
		tick(world, pos, state, blockEntity, BellBlockEntity::applyGlowToRaiders);
	}

	public void activate(Direction direction) {
		BlockPos blockPos = this.getPos();
		this.lastSideHit = direction;
		if (this.ringing) {
			this.ringTicks = 0;
		} else {
			this.ringing = true;
		}

		this.world.addSyncedBlockEvent(blockPos, this.getCachedState().getBlock(), 1, direction.getId());
	}

	private void notifyMemoriesOfBell() {
		BlockPos blockPos = this.getPos();
		if (this.world.getTime() > this.lastRingTime + 60L || this.hearingEntities == null) {
			this.lastRingTime = this.world.getTime();
			Box box = new Box(blockPos).expand(48.0);
			this.hearingEntities = this.world.getNonSpectatingEntities(LivingEntity.class, box);
		}

		if (!this.world.isClient) {
			for (LivingEntity livingEntity : this.hearingEntities) {
				if (livingEntity.isAlive() && !livingEntity.isRemoved() && blockPos.isWithinDistance(livingEntity.getPos(), 32.0)) {
					livingEntity.getBrain().remember(MemoryModuleType.HEARD_BELL_TIME, this.world.getTime());
				}
			}
		}
	}

	private static boolean raidersHearBell(BlockPos pos, List<LivingEntity> hearingEntities) {
		for (LivingEntity livingEntity : hearingEntities) {
			if (livingEntity.isAlive()
				&& !livingEntity.isRemoved()
				&& pos.isWithinDistance(livingEntity.getPos(), 32.0)
				&& livingEntity.getType().isIn(EntityTypeTags.RAIDERS)) {
				return true;
			}
		}

		return false;
	}

	private static void applyGlowToRaiders(World world, BlockPos pos, List<LivingEntity> hearingEntities) {
		hearingEntities.stream().filter(livingEntity -> isRaiderEntity(pos, livingEntity)).forEach(BellBlockEntity::applyGlowToEntity);
	}

	private static void applyParticlesToRaiders(World world, BlockPos pos, List<LivingEntity> hearingEntities) {
		MutableInt mutableInt = new MutableInt(16700985);
		int i = (int)hearingEntities.stream().filter(livingEntity -> pos.isWithinDistance(livingEntity.getPos(), 48.0)).count();
		hearingEntities.stream()
			.filter(livingEntity -> isRaiderEntity(pos, livingEntity))
			.forEach(
				livingEntity -> {
					float f = 1.0F;
					double d = Math.sqrt(
						(livingEntity.getX() - (double)pos.getX()) * (livingEntity.getX() - (double)pos.getX())
							+ (livingEntity.getZ() - (double)pos.getZ()) * (livingEntity.getZ() - (double)pos.getZ())
					);
					double e = (double)((float)pos.getX() + 0.5F) + 1.0 / d * (livingEntity.getX() - (double)pos.getX());
					double g = (double)((float)pos.getZ() + 0.5F) + 1.0 / d * (livingEntity.getZ() - (double)pos.getZ());
					int j = MathHelper.clamp((i - 21) / -2, 3, 15);

					for (int k = 0; k < j; k++) {
						int l = mutableInt.addAndGet(5);
						double h = (double)BackgroundHelper.ColorMixer.getRed(l) / 255.0;
						double m = (double)BackgroundHelper.ColorMixer.getGreen(l) / 255.0;
						double n = (double)BackgroundHelper.ColorMixer.getBlue(l) / 255.0;
						world.addParticle(ParticleTypes.ENTITY_EFFECT, e, (double)((float)pos.getY() + 0.5F), g, h, m, n);
					}
				}
			);
	}

	private static boolean isRaiderEntity(BlockPos pos, LivingEntity entity) {
		return entity.isAlive() && !entity.isRemoved() && pos.isWithinDistance(entity.getPos(), 48.0) && entity.getType().isIn(EntityTypeTags.RAIDERS);
	}

	private static void applyGlowToEntity(LivingEntity entity) {
		entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60));
	}

	@FunctionalInterface
	interface Effect {
		void run(World world, BlockPos pos, List<LivingEntity> hearingEntities);
	}
}
