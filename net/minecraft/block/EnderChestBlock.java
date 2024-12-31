package net.minecraft.block;

import java.util.Random;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class EnderChestBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);

	protected EnderChestBlock() {
		super(Material.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
		this.setItemGroup(ItemGroup.DECORATIONS);
		this.setBoundingBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
		return 2;
	}

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
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		EnderChestInventory enderChestInventory = player.getEnderChestInventory();
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (enderChestInventory == null || !(blockEntity instanceof EnderChestBlockEntity)) {
			return true;
		} else if (world.getBlockState(pos.up()).getBlock().isFullCube()) {
			return true;
		} else if (world.isClient) {
			return true;
		} else {
			enderChestInventory.setBlockEntity((EnderChestBlockEntity)blockEntity);
			player.openInventory(enderChestInventory);
			player.incrementStat(Stats.ENDERCHEST_OPENED);
			return true;
		}
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new EnderChestBlockEntity();
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		for (int i = 0; i < 3; i++) {
			int j = rand.nextInt(2) * 2 - 1;
			int k = rand.nextInt(2) * 2 - 1;
			double d = (double)pos.getX() + 0.5 + 0.25 * (double)j;
			double e = (double)((float)pos.getY() + rand.nextFloat());
			double f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
			double g = (double)(rand.nextFloat() * (float)j);
			double h = ((double)rand.nextFloat() - 0.5) * 0.125;
			double l = (double)(rand.nextFloat() * (float)k);
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
	protected StateManager appendProperties() {
		return new StateManager(this, FACING);
	}
}
