package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MusicDiscItem extends Item {
	private static final Map<String, MusicDiscItem> records = Maps.newHashMap();
	public final String recordType;

	protected MusicDiscItem(String string) {
		this.recordType = string;
		this.maxCount = 1;
		this.setItemGroup(ItemGroup.MISC);
		records.put("records." + string, this);
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() != Blocks.JUKEBOX || (Boolean)blockState.get(JukeboxBlock.HAS_RECORD)) {
			return false;
		} else if (world.isClient) {
			return true;
		} else {
			((JukeboxBlock)Blocks.JUKEBOX).setRecord(world, pos, blockState, itemStack);
			world.syncWorldEvent(null, 1005, pos, Item.getRawId(this));
			itemStack.count--;
			player.incrementStat(Stats.RECORD_PLAYED);
			return true;
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		lines.add(this.getDescription());
	}

	public String getDescription() {
		return CommonI18n.translate("item.record." + this.recordType + ".desc");
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}

	public static MusicDiscItem getByName(String name) {
		return (MusicDiscItem)records.get(name);
	}
}
