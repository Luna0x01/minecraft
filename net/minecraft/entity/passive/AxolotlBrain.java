package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.AquaticStrollTask;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTarget;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.PlayDeadTask;
import net.minecraft.entity.ai.brain.task.PlayDeadTimerTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.SeekWaterTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.TimeLimitedTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkTowardClosestAdultTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

public class AxolotlBrain {
	private static final UniformIntProvider WALK_TOWARD_ADULT_RANGE = UniformIntProvider.create(5, 16);
	private static final float field_30394 = 0.2F;
	private static final float field_30395 = 0.15F;
	private static final float field_30396 = 0.5F;
	private static final float field_30397 = 0.6F;
	private static final float field_30398 = 0.6F;

	protected static Brain<?> create(Brain<AxolotlEntity> brain) {
		addCoreActivities(brain);
		addIdleActivities(brain);
		addFightActivities(brain);
		addPlayDeadActivities(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	private static void addPlayDeadActivities(Brain<AxolotlEntity> brain) {
		brain.setTaskList(
			Activity.PLAY_DEAD,
			ImmutableList.of(Pair.of(0, new PlayDeadTask()), Pair.of(1, new ForgetTask(AxolotlBrain::hasBreedTarget, MemoryModuleType.PLAY_DEAD_TICKS))),
			ImmutableSet.of(Pair.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleState.VALUE_PRESENT)),
			ImmutableSet.of(MemoryModuleType.PLAY_DEAD_TICKS)
		);
	}

	private static void addFightActivities(Brain<AxolotlEntity> brain) {
		brain.setTaskList(
			Activity.FIGHT,
			0,
			ImmutableList.of(
				new ForgetAttackTargetTask(AxolotlEntity::appreciatePlayer),
				new RangedApproachTask(AxolotlBrain::method_33242),
				new MeleeAttackTask(20),
				new ForgetTask(AxolotlBrain::hasBreedTarget, MemoryModuleType.ATTACK_TARGET)
			),
			MemoryModuleType.ATTACK_TARGET
		);
	}

	private static void addCoreActivities(Brain<AxolotlEntity> brain) {
		brain.setTaskList(
			Activity.CORE,
			0,
			ImmutableList.of(
				new LookAroundTask(45, 90), new WanderAroundTask(), new PlayDeadTimerTask(), new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
			)
		);
	}

	private static void addIdleActivities(Brain<AxolotlEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			ImmutableList.of(
				Pair.of(0, new TimeLimitedTask<>(new FollowMobTask(EntityType.PLAYER, 6.0F), UniformIntProvider.create(30, 60))),
				Pair.of(1, new BreedTask(EntityType.AXOLOTL, 0.2F)),
				Pair.of(
					2,
					new RandomTask(
						ImmutableList.of(
							Pair.of(new TemptTask(AxolotlBrain::method_33248), 1), Pair.of(new WalkTowardClosestAdultTask(WALK_TOWARD_ADULT_RANGE, AxolotlBrain::method_33245), 1)
						)
					)
				),
				Pair.of(3, new UpdateAttackTargetTask(AxolotlBrain::getAttackTarget)),
				Pair.of(3, new SeekWaterTask(6, 0.15F)),
				Pair.of(
					4,
					new CompositeTask(
						ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT),
						ImmutableSet.of(),
						CompositeTask.Order.ORDERED,
						CompositeTask.RunMode.TRY_ALL,
						ImmutableList.of(
							Pair.of(new AquaticStrollTask(0.5F), 2),
							Pair.of(new StrollTask(0.15F, false), 2),
							Pair.of(new GoTowardsLookTarget(AxolotlBrain::canGoToLookTarget, AxolotlBrain::method_33248, 3), 3),
							Pair.of(new ConditionalTask<>(Entity::isInsideWaterOrBubbleColumn, new WaitTask(30, 60)), 5),
							Pair.of(new ConditionalTask<>(Entity::isOnGround, new WaitTask(200, 400)), 5)
						)
					)
				)
			)
		);
	}

	private static boolean canGoToLookTarget(LivingEntity entity) {
		World world = entity.world;
		Optional<LookTarget> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.LOOK_TARGET);
		if (optional.isPresent()) {
			BlockPos blockPos = ((LookTarget)optional.get()).getBlockPos();
			return world.isWater(blockPos) == entity.isInsideWaterOrBubbleColumn();
		} else {
			return false;
		}
	}

	public static void updateActivities(AxolotlEntity axolotl) {
		Brain<AxolotlEntity> brain = axolotl.getBrain();
		Activity activity = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		if (activity != Activity.PLAY_DEAD) {
			brain.resetPossibleActivities(ImmutableList.of(Activity.PLAY_DEAD, Activity.FIGHT, Activity.IDLE));
			if (activity == Activity.FIGHT && brain.getFirstPossibleNonCoreActivity().orElse(null) != Activity.FIGHT) {
				brain.remember(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
			}
		}
	}

	private static float method_33242(LivingEntity entity) {
		return entity.isInsideWaterOrBubbleColumn() ? 0.6F : 0.15F;
	}

	private static float method_33245(LivingEntity entity) {
		return entity.isInsideWaterOrBubbleColumn() ? 0.6F : 0.15F;
	}

	private static float method_33248(LivingEntity entity) {
		return entity.isInsideWaterOrBubbleColumn() ? 0.5F : 0.15F;
	}

	private static Optional<? extends LivingEntity> getAttackTarget(AxolotlEntity axolotl) {
		return hasBreedTarget(axolotl) ? Optional.empty() : axolotl.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE);
	}

	private static boolean hasBreedTarget(AxolotlEntity axolotl) {
		return axolotl.getBrain().hasMemoryModule(MemoryModuleType.BREED_TARGET);
	}

	public static Ingredient getTemptItems() {
		return Ingredient.fromTag(ItemTags.AXOLOTL_TEMPT_ITEMS);
	}
}
