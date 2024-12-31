package net.minecraft.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.Sound;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MusicDiscItem extends Item {
	private static final Map<Sound, MusicDiscItem> records = Maps.newHashMap();
	private static final List<MusicDiscItem> field_17372 = Lists.newArrayList();
	private final int field_17373;
	private final Sound field_12330;

	protected MusicDiscItem(int i, Sound sound, Item.Settings settings) {
		super(settings);
		this.field_17373 = i;
		this.field_12330 = sound;
		records.put(this.field_12330, this);
		field_17372.add(this);
	}

	public static MusicDiscItem method_16118(Random random) {
		return (MusicDiscItem)field_17372.get(random.nextInt(field_17372.size()));
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == Blocks.JUKEBOX && !(Boolean)blockState.getProperty(JukeboxBlock.field_18379)) {
			ItemStack itemStack = itemUsageContext.getItemStack();
			if (!world.isClient) {
				((JukeboxBlock)Blocks.JUKEBOX).method_8801(world, blockPos, blockState, itemStack);
				world.syncWorldEvent(null, 1010, blockPos, Item.getRawId(this));
				itemStack.decrement(1);
				PlayerEntity playerEntity = itemUsageContext.getPlayer();
				if (playerEntity != null) {
					playerEntity.method_15928(Stats.PLAY_RECORD);
				}
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}

	public int method_16119() {
		return this.field_17373;
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext tooltipContext) {
		tooltip.add(this.method_16120().formatted(Formatting.GRAY));
	}

	public Text method_16120() {
		return new TranslatableText(this.getTranslationKey() + ".desc");
	}

	@Nullable
	public static MusicDiscItem method_11401(Sound sound) {
		return (MusicDiscItem)records.get(sound);
	}

	public Sound method_11402() {
		return this.field_12330;
	}
}
