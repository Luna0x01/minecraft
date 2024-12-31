package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.sound.Sounds;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class class_3732 extends Block {
	private static final VoxelShape field_18561 = Block.createCuboidShape(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
	private static final VoxelShape field_18562 = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
	public static final IntProperty field_18559 = Properties.HATCH;
	public static final IntProperty field_18560 = Properties.EGGS;

	public class_3732(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18559, Integer.valueOf(0)).withProperty(field_18560, Integer.valueOf(1)));
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		this.method_16756(world, pos, entity, 100);
		super.onSteppedOn(world, pos, entity);
	}

	@Override
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		if (!(entity instanceof ZombieEntity)) {
			this.method_16756(world, pos, entity, 3);
		}

		super.onLandedUpon(world, pos, entity, distance);
	}

	private void method_16756(World world, BlockPos blockPos, Entity entity, int i) {
		if (!this.method_16755(world, entity)) {
			super.onSteppedOn(world, blockPos, entity);
		} else {
			if (!world.isClient && world.random.nextInt(i) == 0) {
				this.method_16757(world, blockPos, world.getBlockState(blockPos));
			}
		}
	}

	private void method_16757(World world, BlockPos blockPos, BlockState blockState) {
		world.playSound(null, blockPos, Sounds.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
		int i = (Integer)blockState.getProperty(field_18560);
		if (i <= 1) {
			world.method_8535(blockPos, false);
		} else {
			world.setBlockState(blockPos, blockState.withProperty(field_18560, Integer.valueOf(i - 1)), 2);
			world.syncGlobalEvent(2001, blockPos, Block.getRawIdFromState(blockState));
		}
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (this.method_16754(world) && this.method_16753(world, pos)) {
			int i = (Integer)state.getProperty(field_18559);
			if (i < 2) {
				world.playSound(null, pos, Sounds.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
				world.setBlockState(pos, state.withProperty(field_18559, Integer.valueOf(i + 1)), 2);
			} else {
				world.playSound(null, pos, Sounds.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
				world.method_8553(pos);
				if (!world.isClient) {
					for (int j = 0; j < state.getProperty(field_18560); j++) {
						world.syncGlobalEvent(2001, pos, Block.getRawIdFromState(state));
						TurtleEntity turtleEntity = new TurtleEntity(world);
						turtleEntity.setAge(-24000);
						turtleEntity.method_15817(pos);
						turtleEntity.refreshPositionAndAngles((double)pos.getX() + 0.3 + (double)j * 0.2, (double)pos.getY(), (double)pos.getZ() + 0.3, 0.0F, 0.0F);
						world.method_3686(turtleEntity);
					}
				}
			}
		}
	}

	private boolean method_16753(BlockView blockView, BlockPos blockPos) {
		return blockView.getBlockState(blockPos.down()).getBlock() == Blocks.SAND;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (this.method_16753(world, pos) && !world.isClient) {
			world.syncGlobalEvent(2005, pos, 0);
		}
	}

	private boolean method_16754(World world) {
		float f = world.method_16349(1.0F);
		return (double)f < 0.69 && (double)f > 0.65 ? true : world.random.nextInt(500) == 0;
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.method_8651(world, player, pos, state, blockEntity, stack);
		this.method_16757(world, pos, state);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext.getItemStack().getItem() == this.getItem() && state.getProperty(field_18560) < 4
			? true
			: super.canReplace(state, itemPlacementContext);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
		return blockState.getBlock() == this
			? blockState.withProperty(field_18560, Integer.valueOf(Math.min(4, (Integer)blockState.getProperty(field_18560) + 1)))
			: super.getPlacementState(context);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return state.getProperty(field_18560) > 1 ? field_18562 : field_18561;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18559, field_18560);
	}

	private boolean method_16755(World world, Entity entity) {
		if (entity instanceof TurtleEntity) {
			return false;
		} else {
			return entity instanceof LivingEntity && !(entity instanceof PlayerEntity) ? world.getGameRules().getBoolean("mobGriefing") : true;
		}
	}
}
