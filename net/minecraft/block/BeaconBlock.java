package net.minecraft.block;

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
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BeaconBlockEntity) {
				player.openInventory((BeaconBlockEntity)blockEntity);
				player.incrementStat(Stats.INTERACTIONS_WITH_BEACON);
			}

			return true;
		}
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getBlockType() {
		return 3;
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
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BeaconBlockEntity) {
			((BeaconBlockEntity)blockEntity).tickBeacon();
			world.addBlockAction(pos, this, 1, 0);
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
