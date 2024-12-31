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
import net.minecraft.world.World;

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
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
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
			World world = this.entity.world;
			BlockPos blockPos = this.targetPos.up();
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (this.field_11936 == 0 && block instanceof CropBlock && ((CropBlock)block).isMature(blockState)) {
				world.removeBlock(blockPos, true);
			} else if (this.field_11936 == 1 && block == Blocks.AIR) {
				SimpleInventory simpleInventory = this.entity.method_11220();

				for (int i = 0; i < simpleInventory.getInvSize(); i++) {
					ItemStack itemStack = simpleInventory.getInvStack(i);
					boolean bl = false;
					if (itemStack != null) {
						if (itemStack.getItem() == Items.WHEAT_SEEDS) {
							world.setBlockState(blockPos, Blocks.WHEAT.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.POTATO) {
							world.setBlockState(blockPos, Blocks.POTATOES.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.CARROT) {
							world.setBlockState(blockPos, Blocks.CARROTS.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.BEETROOT_SEED) {
							world.setBlockState(blockPos, Blocks.BEETROOTS.getDefaultState(), 3);
							bl = true;
						}
					}

					if (bl) {
						itemStack.count--;
						if (itemStack.count <= 0) {
							simpleInventory.setInvStack(i, null);
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
	protected boolean isTargetPos(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (block == Blocks.FARMLAND) {
			pos = pos.up();
			BlockState blockState = world.getBlockState(pos);
			block = blockState.getBlock();
			if (block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.field_11935 && (this.field_11936 == 0 || this.field_11936 < 0)) {
				this.field_11936 = 0;
				return true;
			}

			if (block == Blocks.AIR && this.hasSeed && (this.field_11936 == 1 || this.field_11936 < 0)) {
				this.field_11936 = 1;
				return true;
			}
		}

		return false;
	}
}
