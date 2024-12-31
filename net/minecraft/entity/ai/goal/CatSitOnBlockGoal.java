package net.minecraft.entity.ai.goal;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CatSitOnBlockGoal extends MoveToTargetPosGoal {
	private final OcelotEntity ocelot;

	public CatSitOnBlockGoal(OcelotEntity ocelotEntity, double d) {
		super(ocelotEntity, d, 8);
		this.ocelot = ocelotEntity;
	}

	@Override
	public boolean canStart() {
		return this.ocelot.isTamed() && !this.ocelot.isSitting() && super.canStart();
	}

	@Override
	public boolean shouldContinue() {
		return super.shouldContinue();
	}

	@Override
	public void start() {
		super.start();
		this.ocelot.getSitGoal().setEnabledWithOwner(false);
	}

	@Override
	public void stop() {
		super.stop();
		this.ocelot.setSitting(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.ocelot.getSitGoal().setEnabledWithOwner(false);
		if (!this.hasReached()) {
			this.ocelot.setSitting(false);
		} else if (!this.ocelot.isSitting()) {
			this.ocelot.setSitting(true);
		}
	}

	@Override
	protected boolean isTargetPos(World world, BlockPos pos) {
		if (!world.isAir(pos.up())) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (block == Blocks.CHEST) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof ChestBlockEntity && ((ChestBlockEntity)blockEntity).viewerCount < 1) {
					return true;
				}
			} else {
				if (block == Blocks.LIT_FURNACE) {
					return true;
				}

				if (block == Blocks.BED && blockState.get(BedBlock.BED_TYPE) != BedBlock.BedBlockType.HEAD) {
					return true;
				}
			}

			return false;
		}
	}
}
