package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HoeItem extends MiningToolItem {
	protected static final Map<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>> TILLED_BLOCKS = Maps.newHashMap(
		ImmutableMap.of(
			Blocks.GRASS_BLOCK,
			Pair.of(HoeItem::usagePredicate, getTillingConsumer(Blocks.FARMLAND.getDefaultState())),
			Blocks.DIRT_PATH,
			Pair.of(HoeItem::usagePredicate, getTillingConsumer(Blocks.FARMLAND.getDefaultState())),
			Blocks.DIRT,
			Pair.of(HoeItem::usagePredicate, getTillingConsumer(Blocks.FARMLAND.getDefaultState())),
			Blocks.COARSE_DIRT,
			Pair.of(HoeItem::usagePredicate, getTillingConsumer(Blocks.DIRT.getDefaultState())),
			Blocks.ROOTED_DIRT,
			Pair.of((Predicate)itemUsageContext -> true, getTillingConsumer(Blocks.DIRT.getDefaultState(), Items.HANGING_ROOTS))
		)
	);

	protected HoeItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
		super((float)attackDamage, attackSpeed, material, BlockTags.HOE_MINEABLE, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>> pair = (Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>)TILLED_BLOCKS.get(
			world.getBlockState(blockPos).getBlock()
		);
		if (pair == null) {
			return ActionResult.PASS;
		} else {
			Predicate<ItemUsageContext> predicate = (Predicate<ItemUsageContext>)pair.getFirst();
			Consumer<ItemUsageContext> consumer = (Consumer<ItemUsageContext>)pair.getSecond();
			if (predicate.test(context)) {
				PlayerEntity playerEntity = context.getPlayer();
				world.playSound(playerEntity, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (!world.isClient) {
					consumer.accept(context);
					if (playerEntity != null) {
						context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
					}
				}

				return ActionResult.success(world.isClient);
			} else {
				return ActionResult.PASS;
			}
		}
	}

	public static Consumer<ItemUsageContext> getTillingConsumer(BlockState state) {
		return context -> context.getWorld().setBlockState(context.getBlockPos(), state, 11);
	}

	public static Consumer<ItemUsageContext> getTillingConsumer(BlockState state, ItemConvertible dropItem) {
		return context -> {
			context.getWorld().setBlockState(context.getBlockPos(), state, 11);
			Block.dropStack(context.getWorld(), context.getBlockPos(), context.getSide(), new ItemStack(dropItem));
		};
	}

	public static boolean usagePredicate(ItemUsageContext context) {
		return context.getSide() != Direction.DOWN && context.getWorld().getBlockState(context.getBlockPos().up()).isAir();
	}
}
