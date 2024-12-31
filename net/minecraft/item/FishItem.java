package net.minecraft.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class FishItem extends FoodItem {
	private final boolean cooked;
	private final FishItem.FishType field_17179;

	public FishItem(FishItem.FishType fishType, boolean bl, Item.Settings settings) {
		super(0, 0.0F, false, settings);
		this.field_17179 = fishType;
		this.cooked = bl;
	}

	@Override
	public int getHungerPoints(ItemStack stack) {
		FishItem.FishType fishType = FishItem.FishType.getByItemStack(stack);
		return this.cooked && fishType.canBeCooked() ? fishType.getCookedHungerPoints() : fishType.getUncookedHungerPoints();
	}

	@Override
	public float getSaturation(ItemStack stack) {
		return this.cooked && this.field_17179.canBeCooked() ? this.field_17179.getCookedSaturation() : this.field_17179.getUncookedSaturation();
	}

	@Override
	protected void eat(ItemStack stack, World world, PlayerEntity player) {
		FishItem.FishType fishType = FishItem.FishType.getByItemStack(stack);
		if (fishType == FishItem.FishType.PUFFERFISH) {
			player.method_2654(new StatusEffectInstance(StatusEffects.POISON, 1200, 3));
			player.method_2654(new StatusEffectInstance(StatusEffects.HUNGER, 300, 2));
			player.method_2654(new StatusEffectInstance(StatusEffects.NAUSEA, 300, 1));
		}

		super.eat(stack, world, player);
	}

	public static enum FishType {
		COD(2, 0.1F, 5, 0.6F),
		SALMON(2, 0.1F, 6, 0.8F),
		TROPICAL_FISH(1, 0.1F),
		PUFFERFISH(1, 0.1F);

		private final int uncookedHungerPoints;
		private final float uncookedSaturation;
		private final int cookedHungerPoints;
		private final float cookedSaturation;
		private final boolean canBeCooked;

		private FishType(int j, float f, int k, float g) {
			this.uncookedHungerPoints = j;
			this.uncookedSaturation = f;
			this.cookedHungerPoints = k;
			this.cookedSaturation = g;
			this.canBeCooked = k != 0;
		}

		private FishType(int j, float f) {
			this(j, f, 0, 0.0F);
		}

		public int getUncookedHungerPoints() {
			return this.uncookedHungerPoints;
		}

		public float getUncookedSaturation() {
			return this.uncookedSaturation;
		}

		public int getCookedHungerPoints() {
			return this.cookedHungerPoints;
		}

		public float getCookedSaturation() {
			return this.cookedSaturation;
		}

		public boolean canBeCooked() {
			return this.canBeCooked;
		}

		public static FishItem.FishType getByItemStack(ItemStack itemStack) {
			Item item = itemStack.getItem();
			return item instanceof FishItem ? ((FishItem)item).field_17179 : COD;
		}
	}
}
