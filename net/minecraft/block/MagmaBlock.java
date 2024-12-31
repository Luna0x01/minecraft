package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_3600;
import net.minecraft.class_3694;
import net.minecraft.class_4342;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.Sounds;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class MagmaBlock extends Block {
	public MagmaBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		if (!entity.isFireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
			entity.damage(DamageSource.HOT_FLOOR, 1.0F);
		}

		super.onSteppedOn(world, pos, entity);
	}

	@Override
	public int method_11564(BlockState blockState, class_3600 arg, BlockPos blockPos) {
		return 15728880;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		class_3694.method_16630(world, pos.up(), true);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (direction == Direction.UP && neighborState.getBlock() == Blocks.WATER) {
			world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void method_16582(BlockState blockState, World world, BlockPos blockPos, Random random) {
		BlockPos blockPos2 = blockPos.up();
		if (world.getFluidState(blockPos).matches(FluidTags.WATER)) {
			world.playSound(
				null, blockPos, Sounds.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F
			);
			if (world instanceof ServerWorld) {
				((ServerWorld)world)
					.method_21261(
						class_4342.field_21356, (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.25, (double)blockPos2.getZ() + 0.5, 8, 0.5, 0.25, 0.5, 0.0
					);
			}
		}
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return 20;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world));
	}

	@Override
	public boolean method_13315(BlockState blockState, Entity entity) {
		return entity.isFireImmune();
	}

	@Override
	public boolean method_16592(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}
}
