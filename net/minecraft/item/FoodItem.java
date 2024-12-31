package net.minecraft.item;

import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class FoodItem extends Item {
	private final int hungerPoints;
	private final float saturation;
	private final boolean meat;
	private boolean alwaysEdible;
	private boolean field_17184;
	private StatusEffectInstance field_12297;
	private float effectChance;

	public FoodItem(int i, float f, boolean bl, Item.Settings settings) {
		super(settings);
		this.hungerPoints = i;
		this.meat = bl;
		this.saturation = f;
	}

	@Override
	public ItemStack method_3367(ItemStack stack, World world, LivingEntity entity) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)entity;
			playerEntity.getHungerManager().incrementStat(this, stack);
			world.playSound(
				null, playerEntity.x, playerEntity.y, playerEntity.z, Sounds.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F
			);
			this.eat(stack, world, playerEntity);
			playerEntity.method_15932(Stats.USED.method_21429(this));
			if (playerEntity instanceof ServerPlayerEntity) {
				AchievementsAndCriterions.field_16353.method_15090((ServerPlayerEntity)playerEntity, stack);
			}
		}

		stack.decrement(1);
		return stack;
	}

	protected void eat(ItemStack stack, World world, PlayerEntity player) {
		if (!world.isClient && this.field_12297 != null && world.random.nextFloat() < this.effectChance) {
			player.method_2654(new StatusEffectInstance(this.field_12297));
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return this.field_17184 ? 16 : 32;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.EAT;
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (player.canConsume(this.alwaysEdible)) {
			player.method_13050(hand);
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else {
			return new TypedActionResult<>(ActionResult.FAIL, itemStack);
		}
	}

	public int getHungerPoints(ItemStack stack) {
		return this.hungerPoints;
	}

	public float getSaturation(ItemStack stack) {
		return this.saturation;
	}

	public boolean isMeat() {
		return this.meat;
	}

	public FoodItem method_11371(StatusEffectInstance statusEffectInstance, float f) {
		this.field_12297 = statusEffectInstance;
		this.effectChance = f;
		return this;
	}

	public FoodItem alwaysEdible() {
		this.alwaysEdible = true;
		return this;
	}

	public FoodItem method_16065() {
		this.field_17184 = true;
		return this;
	}
}
