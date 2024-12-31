package net.minecraft.entity.player;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.FoodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;

public class HungerManager {
	private int foodLevel = 20;
	private float foodSaturationLevel;
	private float exhaustion;
	private int foodStarvationTimer;
	private int prevFoodLevel = 20;

	public HungerManager() {
		this.foodSaturationLevel = 5.0F;
	}

	public void add(int food, float saturationModifier) {
		this.foodLevel = Math.min(food + this.foodLevel, 20);
		this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)food * saturationModifier * 2.0F, (float)this.foodLevel);
	}

	public void incrementStat(FoodItem food, ItemStack stack) {
		this.add(food.getHungerPoints(stack), food.getSaturation(stack));
	}

	public void update(PlayerEntity player) {
		Difficulty difficulty = player.world.getGlobalDifficulty();
		this.prevFoodLevel = this.foodLevel;
		if (this.exhaustion > 4.0F) {
			this.exhaustion -= 4.0F;
			if (this.foodSaturationLevel > 0.0F) {
				this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
			} else if (difficulty != Difficulty.PEACEFUL) {
				this.foodLevel = Math.max(this.foodLevel - 1, 0);
			}
		}

		boolean bl = player.world.getGameRules().getBoolean("naturalRegeneration");
		if (bl && this.foodSaturationLevel > 0.0F && player.canFoodHeal() && this.foodLevel >= 20) {
			this.foodStarvationTimer++;
			if (this.foodStarvationTimer >= 10) {
				float f = Math.min(this.foodSaturationLevel, 4.0F);
				player.heal(f / 4.0F);
				this.addExhaustion(f);
				this.foodStarvationTimer = 0;
			}
		} else if (bl && this.foodLevel >= 18 && player.canFoodHeal()) {
			this.foodStarvationTimer++;
			if (this.foodStarvationTimer >= 80) {
				player.heal(1.0F);
				this.addExhaustion(4.0F);
				this.foodStarvationTimer = 0;
			}
		} else if (this.foodLevel <= 0) {
			this.foodStarvationTimer++;
			if (this.foodStarvationTimer >= 80) {
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.damage(DamageSource.STARVE, 1.0F);
				}

				this.foodStarvationTimer = 0;
			}
		} else {
			this.foodStarvationTimer = 0;
		}
	}

	public void deserialize(NbtCompound nbt) {
		if (nbt.contains("foodLevel", 99)) {
			this.foodLevel = nbt.getInt("foodLevel");
			this.foodStarvationTimer = nbt.getInt("foodTickTimer");
			this.foodSaturationLevel = nbt.getFloat("foodSaturationLevel");
			this.exhaustion = nbt.getFloat("foodExhaustionLevel");
		}
	}

	public void serialize(NbtCompound nbt) {
		nbt.putInt("foodLevel", this.foodLevel);
		nbt.putInt("foodTickTimer", this.foodStarvationTimer);
		nbt.putFloat("foodSaturationLevel", this.foodSaturationLevel);
		nbt.putFloat("foodExhaustionLevel", this.exhaustion);
	}

	public int getFoodLevel() {
		return this.foodLevel;
	}

	public int getPrevFoodLevel() {
		return this.prevFoodLevel;
	}

	public boolean isNotFull() {
		return this.foodLevel < 20;
	}

	public void addExhaustion(float exhaustion) {
		this.exhaustion = Math.min(this.exhaustion + exhaustion, 40.0F);
	}

	public float getSaturationLevel() {
		return this.foodSaturationLevel;
	}

	public void setFoodLevel(int foodLevel) {
		this.foodLevel = foodLevel;
	}

	public void setSaturationLevelClient(float saturationLevel) {
		this.foodSaturationLevel = saturationLevel;
	}
}
