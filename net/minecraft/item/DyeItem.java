package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;

public class DyeItem extends Item {
	private static final Map<DyeColor, DyeItem> field_17167 = Maps.newEnumMap(DyeColor.class);
	private final DyeColor field_17168;

	public DyeItem(DyeColor dyeColor, Item.Settings settings) {
		super(settings);
		this.field_17168 = dyeColor;
		field_17167.put(dyeColor, this);
	}

	@Override
	public boolean method_3353(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		if (livingEntity instanceof SheepEntity) {
			SheepEntity sheepEntity = (SheepEntity)livingEntity;
			if (!sheepEntity.isSheared() && sheepEntity.getColor() != this.field_17168) {
				sheepEntity.setColor(this.field_17168);
				itemStack.decrement(1);
			}

			return true;
		} else {
			return false;
		}
	}

	public DyeColor method_16047() {
		return this.field_17168;
	}

	public static DyeItem method_16046(DyeColor dyeColor) {
		return (DyeItem)field_17167.get(dyeColor);
	}
}
