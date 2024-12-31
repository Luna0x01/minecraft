package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sound;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MusicDiscItem extends Item {
	private static final Map<Sound, MusicDiscItem> records = Maps.newHashMap();
	private final Sound field_12330;
	private final String field_12331;

	protected MusicDiscItem(String string, Sound sound) {
		this.field_12331 = "item.record." + string + ".desc";
		this.field_12330 = sound;
		this.maxCount = 1;
		this.setItemGroup(ItemGroup.MISC);
		records.put(this.field_12330, this);
	}

	@Override
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == Blocks.JUKEBOX && !(Boolean)blockState.get(JukeboxBlock.HAS_RECORD)) {
			if (!world.isClient) {
				ItemStack itemStack = player.getStackInHand(hand);
				((JukeboxBlock)Blocks.JUKEBOX).setRecord(world, pos, blockState, itemStack);
				world.syncWorldEvent(null, 1010, pos, Item.getRawId(this));
				itemStack.decrement(1);
				player.incrementStat(Stats.RECORD_PLAYED);
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<String> tooltip, TooltipContext tooltipContext) {
		tooltip.add(this.getDescription());
	}

	public String getDescription() {
		return CommonI18n.translate(this.field_12331);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}

	@Nullable
	public static MusicDiscItem method_11401(Sound sound) {
		return (MusicDiscItem)records.get(sound);
	}

	public Sound method_11402() {
		return this.field_12330;
	}
}
