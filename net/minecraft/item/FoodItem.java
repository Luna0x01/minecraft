package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class FoodItem extends Item {
	public final int eatingTime = 32;
	private final int hungerPoints;
	private final float saturation;
	private final boolean meat;
	private boolean alwaysEdible;
	private int statusEffectId;
	private int duration;
	private int multiplier;
	private float effectChance;

	public FoodItem(int i, float f, boolean bl) {
		this.hungerPoints = i;
		this.meat = bl;
		this.saturation = f;
		this.setItemGroup(ItemGroup.FOOD);
	}

	public FoodItem(int i, boolean bl) {
		this(i, 0.6F, bl);
	}

	@Override
	public ItemStack onFinishUse(ItemStack stack, World world, PlayerEntity player) {
		stack.count--;
		player.getHungerManager().incrementStat(this, stack);
		world.playSound((Entity)player, "random.burp", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		this.eat(stack, world, player);
		player.incrementStat(Stats.USED[Item.getRawId(this)]);
		return stack;
	}

	protected void eat(ItemStack stack, World world, PlayerEntity player) {
		if (!world.isClient && this.statusEffectId > 0 && world.random.nextFloat() < this.effectChance) {
			player.addStatusEffect(new StatusEffectInstance(this.statusEffectId, this.duration * 20, this.multiplier));
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.EAT;
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		if (player.canConsume(this.alwaysEdible)) {
			player.setUseItem(stack, this.getMaxUseTime(stack));
		}

		return stack;
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

	public FoodItem setStatusEffect(int id, int duration, int multiplier, float effectChance) {
		this.statusEffectId = id;
		this.duration = duration;
		this.multiplier = multiplier;
		this.effectChance = effectChance;
		return this;
	}

	public FoodItem alwaysEdible() {
		this.alwaysEdible = true;
		return this;
	}
}
