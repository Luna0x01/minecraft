package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class EntityBucketItem extends BucketItem {
	private final EntityType<?> entityType;
	private final SoundEvent emptyingSound;

	public EntityBucketItem(EntityType<?> type, Fluid fluid, SoundEvent emptyingSound, Item.Settings settings) {
		super(fluid, settings);
		this.entityType = type;
		this.emptyingSound = emptyingSound;
	}

	@Override
	public void onEmptied(@Nullable PlayerEntity player, World world, ItemStack stack, BlockPos pos) {
		if (world instanceof ServerWorld) {
			this.spawnEntity((ServerWorld)world, stack, pos);
			world.emitGameEvent(player, GameEvent.ENTITY_PLACE, pos);
		}
	}

	@Override
	protected void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
		world.playSound(player, pos, this.emptyingSound, SoundCategory.NEUTRAL, 1.0F, 1.0F);
	}

	private void spawnEntity(ServerWorld world, ItemStack stack, BlockPos pos) {
		if (this.entityType.spawnFromItemStack(world, stack, null, pos, SpawnReason.BUCKET, true, false) instanceof Bucketable bucketable) {
			bucketable.copyDataFromNbt(stack.getOrCreateTag());
			bucketable.setFromBucket(true);
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (this.entityType == EntityType.TROPICAL_FISH) {
			NbtCompound nbtCompound = stack.getTag();
			if (nbtCompound != null && nbtCompound.contains("BucketVariantTag", 3)) {
				int i = nbtCompound.getInt("BucketVariantTag");
				Formatting[] formattings = new Formatting[]{Formatting.ITALIC, Formatting.GRAY};
				String string = "color.minecraft." + TropicalFishEntity.getBaseDyeColor(i);
				String string2 = "color.minecraft." + TropicalFishEntity.getPatternDyeColor(i);

				for (int j = 0; j < TropicalFishEntity.COMMON_VARIANTS.length; j++) {
					if (i == TropicalFishEntity.COMMON_VARIANTS[j]) {
						tooltip.add(new TranslatableText(TropicalFishEntity.getToolTipForVariant(j)).formatted(formattings));
						return;
					}
				}

				tooltip.add(new TranslatableText(TropicalFishEntity.getTranslationKey(i)).formatted(formattings));
				MutableText mutableText = new TranslatableText(string);
				if (!string.equals(string2)) {
					mutableText.append(", ").append(new TranslatableText(string2));
				}

				mutableText.formatted(formattings);
				tooltip.add(mutableText);
			}
		}
	}
}
