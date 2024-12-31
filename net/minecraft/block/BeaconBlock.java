package net.minecraft.block;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BeaconBlock extends BlockWithEntity {
	public BeaconBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new BeaconBlockEntity();
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BeaconBlockEntity) {
				player.openInventory((BeaconBlockEntity)blockEntity);
				player.method_15928(Stats.INTERACT_WITH_BEACON);
			}

			return true;
		}
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BeaconBlockEntity) {
				((BeaconBlockEntity)blockEntity).method_16778(itemStack.getName());
			}
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	public static void updateState(World world, BlockPos pos) {
		NetworkUtils.downloadExecutor.submit(() -> {
			Chunk chunk = world.getChunk(pos);

			for (int i = pos.getY() - 1; i >= 0; i--) {
				BlockPos blockPos2 = new BlockPos(pos.getX(), i, pos.getZ());
				if (!chunk.method_9148(blockPos2)) {
					break;
				}

				BlockState blockState = world.getBlockState(blockPos2);
				if (blockState.getBlock() == Blocks.BEACON) {
					((ServerWorld)world).submit(() -> {
						BlockEntity blockEntity = world.getBlockEntity(blockPos2);
						if (blockEntity instanceof BeaconBlockEntity) {
							((BeaconBlockEntity)blockEntity).tickBeacon();
							world.addBlockAction(blockPos2, Blocks.BEACON, 1, 0);
						}
					});
				}
			}
		});
	}
}
