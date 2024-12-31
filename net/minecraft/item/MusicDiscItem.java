package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MusicDiscItem extends Item {
	private static final Map<SoundEvent, MusicDiscItem> MUSIC_DISCS = Maps.newHashMap();
	private final int comparatorOutput;
	private final SoundEvent sound;

	protected MusicDiscItem(int i, SoundEvent soundEvent, Item.Settings settings) {
		super(settings);
		this.comparatorOutput = i;
		this.sound = soundEvent;
		MUSIC_DISCS.put(this.sound, this);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == Blocks.field_10223 && !(Boolean)blockState.get(JukeboxBlock.HAS_RECORD)) {
			ItemStack itemStack = itemUsageContext.getStack();
			if (!world.isClient) {
				((JukeboxBlock)Blocks.field_10223).setRecord(world, blockPos, blockState, itemStack);
				world.playLevelEvent(null, 1010, blockPos, Item.getRawId(this));
				itemStack.decrement(1);
				PlayerEntity playerEntity = itemUsageContext.getPlayer();
				if (playerEntity != null) {
					playerEntity.incrementStat(Stats.field_15375);
				}
			}

			return ActionResult.field_5812;
		} else {
			return ActionResult.field_5811;
		}
	}

	public int getComparatorOutput() {
		return this.comparatorOutput;
	}

	@Override
	public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
		list.add(this.getDescription().formatted(Formatting.field_1080));
	}

	public Text getDescription() {
		return new TranslatableText(this.getTranslationKey() + ".desc");
	}

	@Nullable
	public static MusicDiscItem bySound(SoundEvent soundEvent) {
		return (MusicDiscItem)MUSIC_DISCS.get(soundEvent);
	}

	public SoundEvent getSound() {
		return this.sound;
	}
}
