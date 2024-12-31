package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BeehiveBlock extends BlockWithEntity {
	public static final Direction[] GENERATE_DIRECTIONS = new Direction[]{Direction.field_11039, Direction.field_11034, Direction.field_11035};
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final IntProperty HONEY_LEVEL = Properties.HONEY_LEVEL;

	public BeehiveBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(HONEY_LEVEL, Integer.valueOf(0)).with(FACING, Direction.field_11043));
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return (Integer)blockState.get(HONEY_LEVEL);
	}

	@Override
	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.afterBreak(world, playerEntity, blockPos, blockState, blockEntity, itemStack);
		if (!world.isClient && blockEntity instanceof BeehiveBlockEntity) {
			BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
			if (EnchantmentHelper.getLevel(Enchantments.field_9099, itemStack) == 0) {
				beehiveBlockEntity.angerBees(playerEntity, blockState, BeehiveBlockEntity.BeeState.field_21052);
				world.updateHorizontalAdjacent(blockPos, this);
				this.angerNearbyBees(world, blockPos);
			}

			Criterions.BEE_NEST_DESTROYED.test((ServerPlayerEntity)playerEntity, blockState.getBlock(), itemStack, beehiveBlockEntity.getBeeCount());
		}
	}

	private void angerNearbyBees(World world, BlockPos blockPos) {
		List<BeeEntity> list = world.getNonSpectatingEntities(BeeEntity.class, new Box(blockPos).expand(8.0, 6.0, 8.0));
		if (!list.isEmpty()) {
			List<PlayerEntity> list2 = world.getNonSpectatingEntities(PlayerEntity.class, new Box(blockPos).expand(8.0, 6.0, 8.0));
			int i = list2.size();

			for (BeeEntity beeEntity : list) {
				if (beeEntity.getTarget() == null) {
					beeEntity.setBeeAttacker((Entity)list2.get(world.random.nextInt(i)));
				}
			}
		}
	}

	public static void dropHoneycomb(World world, BlockPos blockPos) {
		dropStack(world, blockPos, new ItemStack(Items.field_20414, 3));
	}

	@Override
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		ItemStack itemStack2 = itemStack.copy();
		int i = (Integer)blockState.get(HONEY_LEVEL);
		boolean bl = false;
		if (i >= 5) {
			if (itemStack.getItem() == Items.field_8868) {
				world.playSound(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.field_20611, SoundCategory.field_15254, 1.0F, 1.0F);
				dropHoneycomb(world, blockPos);
				itemStack.damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(hand));
				bl = true;
			} else if (itemStack.getItem() == Items.field_8469) {
				itemStack.decrement(1);
				world.playSound(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.field_14779, SoundCategory.field_15254, 1.0F, 1.0F);
				if (itemStack.isEmpty()) {
					playerEntity.setStackInHand(hand, new ItemStack(Items.field_20417));
				} else if (!playerEntity.inventory.insertStack(new ItemStack(Items.field_20417))) {
					playerEntity.dropItem(new ItemStack(Items.field_20417), false);
				}

				bl = true;
			}
		}

		if (bl) {
			if (!CampfireBlock.isLitCampfireInRange(world, blockPos, 5)) {
				if (this.hasBees(world, blockPos)) {
					this.angerNearbyBees(world, blockPos);
				}

				this.takeHoney(world, blockState, blockPos, playerEntity, BeehiveBlockEntity.BeeState.field_21052);
			} else {
				this.takeHoney(world, blockState, blockPos);
				if (playerEntity instanceof ServerPlayerEntity) {
					Criterions.SAFELY_HARVEST_HONEY.test((ServerPlayerEntity)playerEntity, blockPos, itemStack2);
				}
			}

			return ActionResult.field_5812;
		} else {
			return super.onUse(blockState, world, blockPos, playerEntity, hand, blockHitResult);
		}
	}

	private boolean hasBees(World world, BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof BeehiveBlockEntity) {
			BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
			return !beehiveBlockEntity.hasNoBees();
		} else {
			return false;
		}
	}

	public void takeHoney(World world, BlockState blockState, BlockPos blockPos, @Nullable PlayerEntity playerEntity, BeehiveBlockEntity.BeeState beeState) {
		this.takeHoney(world, blockState, blockPos);
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof BeehiveBlockEntity) {
			BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
			beehiveBlockEntity.angerBees(playerEntity, blockState, beeState);
		}
	}

	public void takeHoney(World world, BlockState blockState, BlockPos blockPos) {
		world.setBlockState(blockPos, blockState.with(HONEY_LEVEL, Integer.valueOf(0)), 3);
	}

	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Integer)blockState.get(HONEY_LEVEL) >= 5) {
			for (int i = 0; i < random.nextInt(1) + 1; i++) {
				this.spawnHoneyParticles(world, blockPos, blockState);
			}
		}
	}

	private void spawnHoneyParticles(World world, BlockPos blockPos, BlockState blockState) {
		if (blockState.getFluidState().isEmpty() && !(world.random.nextFloat() < 0.3F)) {
			VoxelShape voxelShape = blockState.getCollisionShape(world, blockPos);
			double d = voxelShape.getMaximum(Direction.Axis.field_11052);
			if (d >= 1.0 && !blockState.matches(BlockTags.field_15490)) {
				double e = voxelShape.getMinimum(Direction.Axis.field_11052);
				if (e > 0.0) {
					this.addHoneyParticle(world, blockPos, voxelShape, (double)blockPos.getY() + e - 0.05);
				} else {
					BlockPos blockPos2 = blockPos.down();
					BlockState blockState2 = world.getBlockState(blockPos2);
					VoxelShape voxelShape2 = blockState2.getCollisionShape(world, blockPos2);
					double f = voxelShape2.getMaximum(Direction.Axis.field_11052);
					if ((f < 1.0 || !blockState2.isFullCube(world, blockPos2)) && blockState2.getFluidState().isEmpty()) {
						this.addHoneyParticle(world, blockPos, voxelShape, (double)blockPos.getY() - 0.05);
					}
				}
			}
		}
	}

	private void addHoneyParticle(World world, BlockPos blockPos, VoxelShape voxelShape, double d) {
		this.addHoneyParticle(
			world,
			(double)blockPos.getX() + voxelShape.getMinimum(Direction.Axis.field_11048),
			(double)blockPos.getX() + voxelShape.getMaximum(Direction.Axis.field_11048),
			(double)blockPos.getZ() + voxelShape.getMinimum(Direction.Axis.field_11051),
			(double)blockPos.getZ() + voxelShape.getMaximum(Direction.Axis.field_11051),
			d
		);
	}

	private void addHoneyParticle(World world, double d, double e, double f, double g, double h) {
		world.addParticle(
			ParticleTypes.field_20534, MathHelper.lerp(world.random.nextDouble(), d, e), h, MathHelper.lerp(world.random.nextDouble(), f, g), 0.0, 0.0, 0.0
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HONEY_LEVEL, FACING);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new BeehiveBlockEntity();
	}

	@Override
	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!world.isClient && playerEntity.isCreative() && world.getGameRules().getBoolean(GameRules.field_19392)) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof BeehiveBlockEntity) {
				BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
				ItemStack itemStack = new ItemStack(this);
				int i = (Integer)blockState.get(HONEY_LEVEL);
				boolean bl = !beehiveBlockEntity.hasNoBees();
				if (!bl && i == 0) {
					return;
				}

				if (bl) {
					CompoundTag compoundTag = new CompoundTag();
					compoundTag.put("Bees", beehiveBlockEntity.getBees());
					itemStack.putSubTag("BlockEntityTag", compoundTag);
				}

				CompoundTag compoundTag2 = new CompoundTag();
				compoundTag2.putInt("honey_level", i);
				itemStack.putSubTag("BlockStateTag", compoundTag2);
				ItemEntity itemEntity = new ItemEntity(world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack);
				itemEntity.setToDefaultPickupDelay();
				world.spawnEntity(itemEntity);
			}
		}

		super.onBreak(world, blockPos, blockState, playerEntity);
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState blockState, LootContext.Builder builder) {
		Entity entity = builder.getNullable(LootContextParameters.field_1226);
		if (entity instanceof TntEntity
			|| entity instanceof CreeperEntity
			|| entity instanceof WitherSkullEntity
			|| entity instanceof WitherEntity
			|| entity instanceof TntMinecartEntity) {
			BlockEntity blockEntity = builder.getNullable(LootContextParameters.field_1228);
			if (blockEntity instanceof BeehiveBlockEntity) {
				BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
				beehiveBlockEntity.angerBees(null, blockState, BeehiveBlockEntity.BeeState.field_21052);
			}
		}

		return super.getDroppedStacks(blockState, builder);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (iWorld.getBlockState(blockPos2).getBlock() instanceof FireBlock) {
			BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
			if (blockEntity instanceof BeehiveBlockEntity) {
				BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
				beehiveBlockEntity.angerBees(null, blockState, BeehiveBlockEntity.BeeState.field_21052);
			}
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}
}
