package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EnderChestBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = HorizontalFacingBlock.DIRECTION;
	protected static final Box field_12662 = new Box(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);

	protected EnderChestBlock() {
		super(Material.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12662;
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
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.OBSIDIAN);
	}

	@Override
	public int getDropCount(Random rand) {
		return 8;
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, entity.getHorizontalDirection().getOpposite());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos, state.with(FACING, placer.getHorizontalDirection().getOpposite()), 2);
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
		EnderChestInventory enderChestInventory = playerEntity.getEnderChestInventory();
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (enderChestInventory == null || !(blockEntity instanceof EnderChestBlockEntity)) {
			return true;
		} else if (world.getBlockState(blockPos.up()).method_11734()) {
			return true;
		} else if (world.isClient) {
			return true;
		} else {
			enderChestInventory.setBlockEntity((EnderChestBlockEntity)blockEntity);
			playerEntity.openInventory(enderChestInventory);
			playerEntity.incrementStat(Stats.ENDERCHEST_OPENED);
			return true;
		}
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new EnderChestBlockEntity();
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		for (int i = 0; i < 3; i++) {
			int j = random.nextInt(2) * 2 - 1;
			int k = random.nextInt(2) * 2 - 1;
			double d = (double)pos.getX() + 0.5 + 0.25 * (double)j;
			double e = (double)((float)pos.getY() + random.nextFloat());
			double f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
			double g = (double)(random.nextFloat() * (float)j);
			double h = ((double)random.nextFloat() - 0.5) * 0.125;
			double l = (double)(random.nextFloat() * (float)k);
			world.addParticle(ParticleType.NETHER_PORTAL, d, e, f, g, h, l);
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		Direction direction = Direction.getById(data);
		if (direction.getAxis() == Direction.Axis.Y) {
			direction = Direction.NORTH;
		}

		return this.getDefaultState().with(FACING, direction);
	}

	@Override
	public int getData(BlockState state) {
		return ((Direction)state.get(FACING)).getId();
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING);
	}
}
