package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;

public class HarvestCropsGoal extends MoveToTargetPosGoal {
	private final VillagerEntity entity;
	private boolean hasSeed;
	private boolean field_11935;
	private int field_11936;

	public HarvestCropsGoal(VillagerEntity villagerEntity, double d) {
		super(villagerEntity, d, 16);
		this.entity = villagerEntity;
	}

	@Override
	public boolean canStart() {
		if (this.cooldown <= 0) {
			if (!this.entity.world.getGameRules().getBoolean("mobGriefing")) {
				return false;
			}

			this.field_11936 = -1;
			this.hasSeed = this.entity.hasSeedToPlant();
			this.field_11935 = this.entity.method_11223();
		}

		return super.canStart();
	}

	@Override
	public boolean shouldContinue() {
		return this.field_11936 >= 0 && super.shouldContinue();
	}

	@Override
	public void tick() {
		super.tick();
		this.entity
			.getLookControl()
			.lookAt(
				(double)this.targetPos.getX() + 0.5,
				(double)(this.targetPos.getY() + 1),
				(double)this.targetPos.getZ() + 0.5,
				10.0F,
				(float)this.entity.getLookPitchSpeed()
			);
		if (this.hasReached()) {
			IWorld iWorld = this.entity.world;
			BlockPos blockPos = this.targetPos.up();
			BlockState blockState = iWorld.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (this.field_11936 == 0 && block instanceof CropBlock && ((CropBlock)block).isMature(blockState)) {
				iWorld.method_8535(blockPos, true);
			} else if (this.field_11936 == 1 && blockState.isAir()) {
				SimpleInventory simpleInventory = this.entity.method_11220();

				for (int i = 0; i < simpleInventory.getInvSize(); i++) {
					ItemStack itemStack = simpleInventory.getInvStack(i);
					boolean bl = false;
					if (!itemStack.isEmpty()) {
						if (itemStack.getItem() == Items.WHEAT_SEEDS) {
							iWorld.setBlockState(blockPos, Blocks.WHEAT.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.POTATO) {
							iWorld.setBlockState(blockPos, Blocks.POTATOES.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.CARROT) {
							iWorld.setBlockState(blockPos, Blocks.CARROTS.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.BEETROOT_SEED) {
							iWorld.setBlockState(blockPos, Blocks.BEETROOTS.getDefaultState(), 3);
							bl = true;
						}
					}

					if (bl) {
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							simpleInventory.setInvStack(i, ItemStack.EMPTY);
						}
						break;
					}
				}
			}

			this.field_11936 = -1;
			this.cooldown = 10;
		}
	}

	@Override
	protected boolean method_11012(RenderBlockView renderBlockView, BlockPos blockPos) {
		Block block = renderBlockView.getBlockState(blockPos).getBlock();
		if (block == Blocks.FARMLAND) {
			blockPos = blockPos.up();
			BlockState blockState = renderBlockView.getBlockState(blockPos);
			block = blockState.getBlock();
			if (block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.field_11935 && (this.field_11936 == 0 || this.field_11936 < 0)) {
				this.field_11936 = 0;
				return true;
			}

			if (blockState.isAir() && this.hasSeed && (this.field_11936 == 1 || this.field_11936 < 0)) {
				this.field_11936 = 1;
				return true;
			}
		}

		return false;
	}
}
