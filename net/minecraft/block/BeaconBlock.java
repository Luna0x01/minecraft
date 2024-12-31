package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BeaconBlock extends BlockWithEntity {
	public BeaconBlock() {
		super(Material.GLASS, MaterialColor.DIAMOND);
		this.setStrength(3.0F);
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new BeaconBlockEntity();
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof BeaconBlockEntity) {
				playerEntity.openInventory((BeaconBlockEntity)blockEntity);
				playerEntity.incrementStat(Stats.INTERACTIONS_WITH_BEACON);
			}

			return true;
		}
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
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
		super.onPlaced(world, pos, state, placer, itemStack);
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BeaconBlockEntity) {
				((BeaconBlockEntity)blockEntity).setCustomName(itemStack.getCustomName());
			}
		}
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof BeaconBlockEntity) {
			((BeaconBlockEntity)blockEntity).tickBeacon();
			world.addBlockAction(blockPos, this, 1, 0);
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	public static void updateState(World world, BlockPos pos) {
		NetworkUtils.downloadExecutor.submit(new Runnable() {
			public void run() {
				Chunk chunk = world.getChunk(pos);

				for (int i = pos.getY() - 1; i >= 0; i--) {
					final BlockPos blockPos = new BlockPos(pos.getX(), i, pos.getZ());
					if (!chunk.hasDirectSunlight(blockPos)) {
						break;
					}

					BlockState blockState = world.getBlockState(blockPos);
					if (blockState.getBlock() == Blocks.BEACON) {
						((ServerWorld)world).submit(new Runnable() {
							public void run() {
								BlockEntity blockEntity = world.getBlockEntity(blockPos);
								if (blockEntity instanceof BeaconBlockEntity) {
									((BeaconBlockEntity)blockEntity).tickBeacon();
									world.addBlockAction(blockPos, Blocks.BEACON, 1, 0);
								}
							}
						});
					}
				}
			}
		});
	}
}
