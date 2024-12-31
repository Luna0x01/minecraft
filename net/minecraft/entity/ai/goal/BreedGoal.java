package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.Random;
import net.minecraft.class_4342;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class BreedGoal extends Goal {
	protected final AnimalEntity animal;
	private final Class<? extends AnimalEntity> field_15477;
	protected World world;
	protected AnimalEntity mate;
	private int timer;
	private final double chance;

	public BreedGoal(AnimalEntity animalEntity, double d) {
		this(animalEntity, d, animalEntity.getClass());
	}

	public BreedGoal(AnimalEntity animalEntity, double d, Class<? extends AnimalEntity> class_) {
		this.animal = animalEntity;
		this.world = animalEntity.world;
		this.field_15477 = class_;
		this.chance = d;
		this.setCategoryBits(3);
	}

	@Override
	public boolean canStart() {
		if (!this.animal.isInLove()) {
			return false;
		} else {
			this.mate = this.findMate();
			return this.mate != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.mate.isAlive() && this.mate.isInLove() && this.timer < 60;
	}

	@Override
	public void stop() {
		this.mate = null;
		this.timer = 0;
	}

	@Override
	public void tick() {
		this.animal.getLookControl().lookAt(this.mate, 10.0F, (float)this.animal.getLookPitchSpeed());
		this.animal.getNavigation().startMovingTo(this.mate, this.chance);
		this.timer++;
		if (this.timer >= 60 && this.animal.squaredDistanceTo(this.mate) < 9.0) {
			this.breed();
		}
	}

	private AnimalEntity findMate() {
		List<AnimalEntity> list = this.world.getEntitiesInBox(this.field_15477, this.animal.getBoundingBox().expand(8.0));
		double d = Double.MAX_VALUE;
		AnimalEntity animalEntity = null;

		for (AnimalEntity animalEntity2 : list) {
			if (this.animal.canBreedWith(animalEntity2) && this.animal.squaredDistanceTo(animalEntity2) < d) {
				animalEntity = animalEntity2;
				d = this.animal.squaredDistanceTo(animalEntity2);
			}
		}

		return animalEntity;
	}

	protected void breed() {
		PassiveEntity passiveEntity = this.animal.breed(this.mate);
		if (passiveEntity != null) {
			ServerPlayerEntity serverPlayerEntity = this.animal.method_15103();
			if (serverPlayerEntity == null && this.mate.method_15103() != null) {
				serverPlayerEntity = this.mate.method_15103();
			}

			if (serverPlayerEntity != null) {
				serverPlayerEntity.method_15928(Stats.ANIMALS_BRED);
				AchievementsAndCriterions.field_16342.method_15041(serverPlayerEntity, this.animal, this.mate, passiveEntity);
			}

			this.animal.setAge(6000);
			this.mate.setAge(6000);
			this.animal.resetLoveTicks();
			this.mate.resetLoveTicks();
			passiveEntity.setAge(-24000);
			passiveEntity.refreshPositionAndAngles(this.animal.x, this.animal.y, this.animal.z, 0.0F, 0.0F);
			this.world.method_3686(passiveEntity);
			Random random = this.animal.getRandom();

			for (int i = 0; i < 7; i++) {
				double d = random.nextGaussian() * 0.02;
				double e = random.nextGaussian() * 0.02;
				double f = random.nextGaussian() * 0.02;
				double g = random.nextDouble() * (double)this.animal.width * 2.0 - (double)this.animal.width;
				double h = 0.5 + random.nextDouble() * (double)this.animal.height;
				double j = random.nextDouble() * (double)this.animal.width * 2.0 - (double)this.animal.width;
				this.world.method_16343(class_4342.field_21351, this.animal.x + g, this.animal.y + h, this.animal.z + j, d, e, f);
			}

			if (this.world.getGameRules().getBoolean("doMobLoot")) {
				this.world.method_3686(new ExperienceOrbEntity(this.world, this.animal.x, this.animal.y, this.animal.z, random.nextInt(7) + 1));
			}
		}
	}
}
