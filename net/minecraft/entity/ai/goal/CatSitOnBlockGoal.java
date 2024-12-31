package net.minecraft.entity.ai.goal;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RenderBlockView;

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
	protected boolean method_11012(RenderBlockView renderBlockView, BlockPos blockPos) {
		if (!renderBlockView.method_8579(blockPos.up())) {
			return false;
		} else {
			BlockState blockState = renderBlockView.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (block == Blocks.CHEST) {
				return ChestBlockEntity.method_16792(renderBlockView, blockPos) < 1;
			} else {
				return block == Blocks.FURNACE && blockState.getProperty(FurnaceBlock.field_18348)
					? true
					: block instanceof BedBlock && blockState.getProperty(BedBlock.PART) != BedPart.HEAD;
			}
		}
	}
}
