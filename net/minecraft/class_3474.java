package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class class_3474 extends MoveToTargetPosGoal {
	private final Block field_16883;
	private final MobEntity field_16884;
	private int field_16885;

	public class_3474(Block block, PathAwareEntity pathAwareEntity, double d, int i) {
		super(pathAwareEntity, d, 24, i);
		this.field_16883 = block;
		this.field_16884 = pathAwareEntity;
	}

	@Override
	public boolean canStart() {
		if (!this.field_16884.world.getGameRules().getBoolean("mobGriefing")) {
			return false;
		} else {
			return this.field_16884.getRandom().nextInt(20) != 0 ? false : super.canStart();
		}
	}

	@Override
	protected int method_15694(PathAwareEntity pathAwareEntity) {
		return 0;
	}

	@Override
	public boolean shouldContinue() {
		return super.shouldContinue();
	}

	@Override
	public void stop() {
		super.stop();
		this.field_16884.fallDistance = 1.0F;
	}

	@Override
	public void start() {
		super.start();
		this.field_16885 = 0;
	}

	public void method_15700(IWorld iWorld, BlockPos blockPos) {
	}

	public void method_15699(World world, BlockPos blockPos) {
	}

	@Override
	public void tick() {
		super.tick();
		World world = this.field_16884.world;
		BlockPos blockPos = new BlockPos(this.field_16884);
		BlockPos blockPos2 = this.method_15701(blockPos, world);
		Random random = this.field_16884.getRandom();
		if (this.hasReached() && blockPos2 != null) {
			if (this.field_16885 > 0) {
				this.field_16884.velocityY = 0.3;
				if (!world.isClient) {
					double d = 0.08;
					((ServerWorld)world)
						.method_21261(
							new class_4339(class_4342.ITEM, new ItemStack(Items.EGG)),
							(double)blockPos2.getX() + 0.5,
							(double)blockPos2.getY() + 0.7,
							(double)blockPos2.getZ() + 0.5,
							3,
							((double)random.nextFloat() - 0.5) * 0.08,
							((double)random.nextFloat() - 0.5) * 0.08,
							((double)random.nextFloat() - 0.5) * 0.08,
							0.15F
						);
				}
			}

			if (this.field_16885 % 2 == 0) {
				this.field_16884.velocityY = -0.3;
				if (this.field_16885 % 6 == 0) {
					this.method_15700(world, this.targetPos);
				}
			}

			if (this.field_16885 > 60) {
				world.method_8553(blockPos2);
				if (!world.isClient) {
					for (int i = 0; i < 20; i++) {
						double e = random.nextGaussian() * 0.02;
						double f = random.nextGaussian() * 0.02;
						double g = random.nextGaussian() * 0.02;
						((ServerWorld)world)
							.method_21261(class_4342.field_21360, (double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5, 1, e, f, g, 0.15F);
					}

					this.method_15699(world, this.targetPos);
				}
			}

			this.field_16885++;
		}
	}

	@Nullable
	private BlockPos method_15701(BlockPos blockPos, BlockView blockView) {
		if (blockView.getBlockState(blockPos).getBlock() == this.field_16883) {
			return blockPos;
		} else {
			BlockPos[] blockPoss = new BlockPos[]{blockPos.down(), blockPos.west(), blockPos.east(), blockPos.north(), blockPos.south(), blockPos.down().down()};

			for (BlockPos blockPos2 : blockPoss) {
				if (blockView.getBlockState(blockPos2).getBlock() == this.field_16883) {
					return blockPos2;
				}
			}

			return null;
		}
	}

	@Override
	protected boolean method_11012(RenderBlockView renderBlockView, BlockPos blockPos) {
		Block block = renderBlockView.getBlockState(blockPos).getBlock();
		return block == this.field_16883 && renderBlockView.getBlockState(blockPos.up()).isAir() && renderBlockView.getBlockState(blockPos.up(2)).isAir();
	}
}
