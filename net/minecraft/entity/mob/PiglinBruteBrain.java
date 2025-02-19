package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.FindEntityTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAngryAtTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask;
import net.minecraft.entity.ai.brain.task.GoToNearbyPositionTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.util.dynamic.GlobalPos;

public class PiglinBruteBrain {
	private static final int field_30589 = 600;
	private static final int field_30590 = 20;
	private static final double field_30591 = 0.0125;
	private static final int field_30592 = 8;
	private static final int field_30593 = 8;
	private static final double field_30594 = 12.0;
	private static final float field_30595 = 0.6F;
	private static final int field_30596 = 2;
	private static final int field_30597 = 100;
	private static final int field_30598 = 5;

	protected static Brain<?> create(PiglinBruteEntity piglinBrute, Brain<PiglinBruteEntity> brain) {
		addCoreActivities(piglinBrute, brain);
		addIdleActivities(piglinBrute, brain);
		addFightActivities(piglinBrute, brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	protected static void setCurrentPosAsHome(PiglinBruteEntity piglinBrute) {
		GlobalPos globalPos = GlobalPos.create(piglinBrute.world.getRegistryKey(), piglinBrute.getBlockPos());
		piglinBrute.getBrain().remember(MemoryModuleType.HOME, globalPos);
	}

	private static void addCoreActivities(PiglinBruteEntity piglinBrute, Brain<PiglinBruteEntity> brain) {
		brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(), new OpenDoorsTask(), new ForgetAngryAtTargetTask()));
	}

	private static void addIdleActivities(PiglinBruteEntity piglinBrute, Brain<PiglinBruteEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			10,
			ImmutableList.of(
				new UpdateAttackTargetTask(PiglinBruteBrain::method_30247), method_30244(), method_30254(), new FindInteractionTargetTask(EntityType.PLAYER, 4)
			)
		);
	}

	private static void addFightActivities(PiglinBruteEntity piglinBrute, Brain<PiglinBruteEntity> brain) {
		brain.setTaskList(
			Activity.FIGHT,
			10,
			ImmutableList.of(
				new ForgetAttackTargetTask((Predicate<LivingEntity>)(livingEntity -> !method_30248(piglinBrute, livingEntity))),
				new RangedApproachTask(1.0F),
				new MeleeAttackTask(20)
			),
			MemoryModuleType.ATTACK_TARGET
		);
	}

	private static RandomTask<PiglinBruteEntity> method_30244() {
		return new RandomTask<>(
			ImmutableList.of(
				Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 1),
				Pair.of(new FollowMobTask(EntityType.PIGLIN, 8.0F), 1),
				Pair.of(new FollowMobTask(EntityType.PIGLIN_BRUTE, 8.0F), 1),
				Pair.of(new FollowMobTask(8.0F), 1),
				Pair.of(new WaitTask(30, 60), 1)
			)
		);
	}

	private static RandomTask<PiglinBruteEntity> method_30254() {
		return new RandomTask<>(
			ImmutableList.of(
				Pair.of(new StrollTask(0.6F), 2),
				Pair.of(FindEntityTask.create(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2),
				Pair.of(FindEntityTask.create(EntityType.PIGLIN_BRUTE, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2),
				Pair.of(new GoToNearbyPositionTask(MemoryModuleType.HOME, 0.6F, 2, 100), 2),
				Pair.of(new GoToIfNearbyTask(MemoryModuleType.HOME, 0.6F, 5), 2),
				Pair.of(new WaitTask(30, 60), 1)
			)
		);
	}

	protected static void method_30256(PiglinBruteEntity piglinBrute) {
		Brain<PiglinBruteEntity> brain = piglinBrute.getBrain();
		Activity activity = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		brain.resetPossibleActivities(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
		Activity activity2 = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		if (activity != activity2) {
			method_30261(piglinBrute);
		}

		piglinBrute.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
	}

	private static boolean method_30248(AbstractPiglinEntity piglin, LivingEntity livingEntity) {
		return method_30247(piglin).filter(livingEntity2 -> livingEntity2 == livingEntity).isPresent();
	}

	private static Optional<? extends LivingEntity> method_30247(AbstractPiglinEntity piglin) {
		Optional<LivingEntity> optional = LookTargetUtil.getEntity(piglin, MemoryModuleType.ANGRY_AT);
		if (optional.isPresent() && Sensor.testAttackableTargetPredicateIgnoreVisibility(piglin, (LivingEntity)optional.get())) {
			return optional;
		} else {
			Optional<? extends LivingEntity> optional2 = method_30249(piglin, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
			return optional2.isPresent() ? optional2 : piglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
		}
	}

	private static Optional<? extends LivingEntity> method_30249(AbstractPiglinEntity piglin, MemoryModuleType<? extends LivingEntity> memoryModuleType) {
		return piglin.getBrain().getOptionalMemory(memoryModuleType).filter(livingEntity -> livingEntity.isInRange(piglin, 12.0));
	}

	protected static void tryRevenge(PiglinBruteEntity piglinBrute, LivingEntity target) {
		if (!(target instanceof AbstractPiglinEntity)) {
			PiglinBrain.tryRevenge(piglinBrute, target);
		}
	}

	protected static void method_35198(PiglinBruteEntity piglinBruteEntity, LivingEntity livingEntity) {
		piglinBruteEntity.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		piglinBruteEntity.getBrain().remember(MemoryModuleType.ANGRY_AT, livingEntity.getUuid(), 600L);
	}

	protected static void method_30258(PiglinBruteEntity piglinBrute) {
		if ((double)piglinBrute.world.random.nextFloat() < 0.0125) {
			method_30261(piglinBrute);
		}
	}

	private static void method_30261(PiglinBruteEntity piglinBrute) {
		piglinBrute.getBrain().getFirstPossibleNonCoreActivity().ifPresent(activity -> {
			if (activity == Activity.FIGHT) {
				piglinBrute.playAngrySound();
			}
		});
	}
}
