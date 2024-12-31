package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.GameRules;

public class FarmerVillagerTask extends Task<VillagerEntity> {
	@Nullable
	private BlockPos currentTarget;
	private boolean ableToPlant;
	private boolean ableToPickUpSeed;
	private long nextResponseTime;
	private int ticksRan;
	private final List<BlockPos> targetPositions = Lists.newArrayList();

	public FarmerVillagerTask() {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_18446,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_18873,
				MemoryModuleState.field_18456
			)
		);
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		if (!serverWorld.getGameRules().getBoolean(GameRules.field_19388)) {
			return false;
		} else if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.field_17056) {
			return false;
		} else {
			this.ableToPlant = villagerEntity.hasSeedToPlant();
			this.ableToPickUpSeed = false;
			BasicInventory basicInventory = villagerEntity.getInventory();
			int i = basicInventory.getInvSize();

			for (int j = 0; j < i; j++) {
				ItemStack itemStack = basicInventory.getInvStack(j);
				if (itemStack.isEmpty()) {
					this.ableToPickUpSeed = true;
					break;
				}

				if (itemStack.getItem() == Items.field_8317 || itemStack.getItem() == Items.field_8309) {
					this.ableToPickUpSeed = true;
					break;
				}
			}

			BlockPos.Mutable mutable = new BlockPos.Mutable(villagerEntity);
			this.targetPositions.clear();

			for (int k = -1; k <= 1; k++) {
				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						mutable.set(villagerEntity.getX() + (double)k, villagerEntity.getY() + (double)l, villagerEntity.getZ() + (double)m);
						if (this.isSuitableTarget(mutable, serverWorld)) {
							this.targetPositions.add(new BlockPos(mutable));
						}
					}
				}
			}

			this.currentTarget = this.chooseRandomTarget(serverWorld);
			return (this.ableToPlant || this.ableToPickUpSeed) && this.currentTarget != null;
		}
	}

	@Nullable
	private BlockPos chooseRandomTarget(ServerWorld serverWorld) {
		return this.targetPositions.isEmpty() ? null : (BlockPos)this.targetPositions.get(serverWorld.getRandom().nextInt(this.targetPositions.size()));
	}

	private boolean isSuitableTarget(BlockPos blockPos, ServerWorld serverWorld) {
		BlockState blockState = serverWorld.getBlockState(blockPos);
		Block block = blockState.getBlock();
		Block block2 = serverWorld.getBlockState(blockPos.down()).getBlock();
		return block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.ableToPickUpSeed
			|| blockState.isAir() && block2 instanceof FarmlandBlock && this.ableToPlant;
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (l > this.nextResponseTime && this.currentTarget != null) {
			villagerEntity.getBrain().putMemory(MemoryModuleType.field_18446, new BlockPosLookTarget(this.currentTarget));
			villagerEntity.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));
		}
	}

	protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.getBrain().forget(MemoryModuleType.field_18446);
		villagerEntity.getBrain().forget(MemoryModuleType.field_18445);
		this.ticksRan = 0;
		this.nextResponseTime = l + 40L;
	}

	protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (this.currentTarget != null && l > this.nextResponseTime) {
			BlockState blockState = serverWorld.getBlockState(this.currentTarget);
			Block block = blockState.getBlock();
			Block block2 = serverWorld.getBlockState(this.currentTarget.down()).getBlock();
			if (block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.ableToPickUpSeed) {
				serverWorld.breakBlock(this.currentTarget, true, villagerEntity);
			}

			if (blockState.isAir() && block2 instanceof FarmlandBlock && this.ableToPlant) {
				BasicInventory basicInventory = villagerEntity.getInventory();

				for (int i = 0; i < basicInventory.getInvSize(); i++) {
					ItemStack itemStack = basicInventory.getInvStack(i);
					boolean bl = false;
					if (!itemStack.isEmpty()) {
						if (itemStack.getItem() == Items.field_8317) {
							serverWorld.setBlockState(this.currentTarget, Blocks.field_10293.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.field_8567) {
							serverWorld.setBlockState(this.currentTarget, Blocks.field_10247.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.field_8179) {
							serverWorld.setBlockState(this.currentTarget, Blocks.field_10609.getDefaultState(), 3);
							bl = true;
						} else if (itemStack.getItem() == Items.field_8309) {
							serverWorld.setBlockState(this.currentTarget, Blocks.field_10341.getDefaultState(), 3);
							bl = true;
						}
					}

					if (bl) {
						serverWorld.playSound(
							null,
							(double)this.currentTarget.getX(),
							(double)this.currentTarget.getY(),
							(double)this.currentTarget.getZ(),
							SoundEvents.field_17611,
							SoundCategory.field_15245,
							1.0F,
							1.0F
						);
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							basicInventory.setInvStack(i, ItemStack.EMPTY);
						}
						break;
					}
				}
			}

			if (block instanceof CropBlock && !((CropBlock)block).isMature(blockState)) {
				this.targetPositions.remove(this.currentTarget);
				this.currentTarget = this.chooseRandomTarget(serverWorld);
				if (this.currentTarget != null) {
					this.nextResponseTime = l + 20L;
					villagerEntity.getBrain().putMemory(MemoryModuleType.field_18445, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));
					villagerEntity.getBrain().putMemory(MemoryModuleType.field_18446, new BlockPosLookTarget(this.currentTarget));
				}
			}
		}

		this.ticksRan++;
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		return this.ticksRan < 200;
	}
}
