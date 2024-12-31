package net.minecraft.block;

import net.minecraft.container.BlockContext;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.container.SimpleNamedContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableBlock extends Block {
	private static final Text CONTAINER_NAME = new TranslatableText("container.crafting");

	protected CraftingTableBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return ActionResult.field_5812;
		} else {
			playerEntity.openContainer(blockState.createContainerFactory(world, blockPos));
			playerEntity.incrementStat(Stats.field_15368);
			return ActionResult.field_5812;
		}
	}

	@Override
	public NameableContainerFactory createContainerFactory(BlockState blockState, World world, BlockPos blockPos) {
		return new SimpleNamedContainerFactory(
			(i, playerInventory, playerEntity) -> new CraftingTableContainer(i, playerInventory, BlockContext.create(world, blockPos)), CONTAINER_NAME
		);
	}
}
