package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.TooltipContext;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class class_3552 extends BucketItem {
	private final EntityType<?> field_17178;

	public class_3552(EntityType<?> entityType, Fluid fluid, Item.Settings settings) {
		super(fluid, settings);
		this.field_17178 = entityType;
	}

	@Override
	public void method_16031(World world, ItemStack itemStack, BlockPos blockPos) {
		if (!world.isClient) {
			this.method_16061(world, itemStack, blockPos);
		}
	}

	@Override
	protected void method_16029(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos) {
		iWorld.playSound(playerEntity, blockPos, Sounds.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
	}

	private void method_16061(World world, ItemStack itemStack, BlockPos blockPos) {
		Entity entity = this.field_17178.method_15619(world, itemStack, null, blockPos, true, false);
		if (entity != null) {
			((FishEntity)entity).method_15721(true);
		}
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext tooltipContext) {
		if (this.field_17178 == EntityType.TROPICAL_FISH) {
			NbtCompound nbtCompound = stack.getNbt();
			if (nbtCompound != null && nbtCompound.contains("BucketVariantTag", 3)) {
				int i = nbtCompound.getInt("BucketVariantTag");
				Formatting[] formattings = new Formatting[]{Formatting.ITALIC, Formatting.GRAY};
				String string = "color.minecraft." + TropicalFishEntity.method_15776(i);
				String string2 = "color.minecraft." + TropicalFishEntity.method_15783(i);

				for (int j = 0; j < TropicalFishEntity.field_16931.length; j++) {
					if (i == TropicalFishEntity.field_16931[j]) {
						tooltip.add(new TranslatableText(TropicalFishEntity.method_15775(j)).formatted(formattings));
						return;
					}
				}

				tooltip.add(new TranslatableText(TropicalFishEntity.method_15784(i)).formatted(formattings));
				Text text = new TranslatableText(string);
				if (!string.equals(string2)) {
					text.append(", ").append(new TranslatableText(string2));
				}

				text.formatted(formattings);
				tooltip.add(text);
			}
		}
	}
}
