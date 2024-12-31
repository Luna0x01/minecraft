package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class FishItem extends FoodItem {
	private final boolean cooked;

	public FishItem(boolean bl) {
		super(0, 0.0F, false);
		this.cooked = bl;
	}

	@Override
	public int getHungerPoints(ItemStack stack) {
		FishItem.FishType fishType = FishItem.FishType.getByItemStack(stack);
		return this.cooked && fishType.canBeCooked() ? fishType.getCookedHungerPoints() : fishType.getUncookedHungerPoints();
	}

	@Override
	public float getSaturation(ItemStack stack) {
		FishItem.FishType fishType = FishItem.FishType.getByItemStack(stack);
		return this.cooked && fishType.canBeCooked() ? fishType.getCookedSaturation() : fishType.getUncookedSaturation();
	}

	@Override
	protected void eat(ItemStack stack, World world, PlayerEntity player) {
		FishItem.FishType fishType = FishItem.FishType.getByItemStack(stack);
		if (fishType == FishItem.FishType.PUFFERFISH) {
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 1200, 3));
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 300, 2));
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 300, 1));
		}

		super.eat(stack, world, player);
	}

	@Override
	public void method_13648(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		for (FishItem.FishType fishType : FishItem.FishType.values()) {
			if (!this.cooked || fishType.canBeCooked()) {
				defaultedList.add(new ItemStack(this, 1, fishType.getId()));
			}
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		FishItem.FishType fishType = FishItem.FishType.getByItemStack(stack);
		return this.getTranslationKey() + "." + fishType.getName() + "." + (this.cooked && fishType.canBeCooked() ? "cooked" : "raw");
	}

	public static enum FishType {
		COD(0, "cod", 2, 0.1F, 5, 0.6F),
		SALMON(1, "salmon", 2, 0.1F, 6, 0.8F),
		CLOWNFISH(2, "clownfish", 1, 0.1F),
		PUFFERFISH(3, "pufferfish", 1, 0.1F);

		private static final Map<Integer, FishItem.FishType> FISH_TYPES = Maps.newHashMap();
		private final int id;
		private final String name;
		private final int uncookedHungerPoints;
		private final float uncookedSaturation;
		private final int cookedHungerPoints;
		private final float cookedSaturation;
		private final boolean canBeCooked;

		private FishType(int j, String string2, int k, float f, int l, float g) {
			this.id = j;
			this.name = string2;
			this.uncookedHungerPoints = k;
			this.uncookedSaturation = f;
			this.cookedHungerPoints = l;
			this.cookedSaturation = g;
			this.canBeCooked = true;
		}

		private FishType(int j, String string2, int k, float f) {
			this.id = j;
			this.name = string2;
			this.uncookedHungerPoints = k;
			this.uncookedSaturation = f;
			this.cookedHungerPoints = 0;
			this.cookedSaturation = 0.0F;
			this.canBeCooked = false;
		}

		public int getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
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

		public static FishItem.FishType getById(int id) {
			FishItem.FishType fishType = (FishItem.FishType)FISH_TYPES.get(id);
			return fishType == null ? COD : fishType;
		}

		public static FishItem.FishType getByItemStack(ItemStack itemStack) {
			return itemStack.getItem() instanceof FishItem ? getById(itemStack.getData()) : COD;
		}

		static {
			for (FishItem.FishType fishType : values()) {
				FISH_TYPES.put(fishType.getId(), fishType);
			}
		}
	}
}
