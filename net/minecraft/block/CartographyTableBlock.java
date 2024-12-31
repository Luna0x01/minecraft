package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.container.BlockContext;
import net.minecraft.container.CartographyTableContainer;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.container.SimpleNamedContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CartographyTableBlock extends Block {
	private static final TranslatableText CONTAINER_NAME = new TranslatableText("container.cartography_table");

	protected CartographyTableBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return ActionResult.field_5812;
		} else {
			playerEntity.openContainer(blockState.createContainerFactory(world, blockPos));
			playerEntity.incrementStat(Stats.field_19252);
			return ActionResult.field_5812;
		}
	}

	@Nullable
	@Override
	public NameableContainerFactory createContainerFactory(BlockState blockState, World world, BlockPos blockPos) {
		return new SimpleNamedContainerFactory(
			(i, playerInventory, playerEntity) -> new CartographyTableContainer(i, playerInventory, BlockContext.create(world, blockPos)), CONTAINER_NAME
		);
	}
}
