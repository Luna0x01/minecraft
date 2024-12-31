package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CampfireBlock extends BlockWithEntity implements Waterloggable {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
	public static final BooleanProperty LIT = Properties.LIT;
	public static final BooleanProperty SIGNAL_FIRE = Properties.SIGNAL_FIRE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	private static final VoxelShape field_21580 = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

	public CampfireBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(LIT, Boolean.valueOf(true))
				.with(SIGNAL_FIRE, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
				.with(FACING, Direction.field_11043)
		);
	}

	@Override
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if ((Boolean)blockState.get(LIT)) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof CampfireBlockEntity) {
				CampfireBlockEntity campfireBlockEntity = (CampfireBlockEntity)blockEntity;
				ItemStack itemStack = playerEntity.getStackInHand(hand);
				Optional<CampfireCookingRecipe> optional = campfireBlockEntity.getRecipeFor(itemStack);
				if (optional.isPresent()) {
					if (!world.isClient
						&& campfireBlockEntity.addItem(playerEntity.abilities.creativeMode ? itemStack.copy() : itemStack, ((CampfireCookingRecipe)optional.get()).getCookTime())
						)
					 {
						playerEntity.incrementStat(Stats.field_17486);
						return ActionResult.field_5812;
					}

					return ActionResult.field_21466;
				}
			}
		}

		return ActionResult.field_5811;
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!entity.isFireImmune() && (Boolean)blockState.get(LIT) && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
			entity.damage(DamageSource.IN_FIRE, 1.0F);
		}

		super.onEntityCollision(blockState, world, blockPos, entity);
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof CampfireBlockEntity) {
				ItemScatterer.spawn(world, blockPos, ((CampfireBlockEntity)blockEntity).getItemsBeingCooked());
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		IWorld iWorld = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		boolean bl = iWorld.getFluidState(blockPos).getFluid() == Fluids.WATER;
		return this.getDefaultState()
			.with(WATERLOGGED, Boolean.valueOf(bl))
			.with(SIGNAL_FIRE, Boolean.valueOf(this.doesBlockCauseSignalFire(iWorld.getBlockState(blockPos.down()))))
			.with(LIT, Boolean.valueOf(!bl))
			.with(FACING, itemPlacementContext.getPlayerFacing());
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return direction == Direction.field_11033
			? blockState.with(SIGNAL_FIRE, Boolean.valueOf(this.doesBlockCauseSignalFire(blockState2)))
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	private boolean doesBlockCauseSignalFire(BlockState blockState) {
		return blockState.getBlock() == Blocks.field_10359;
	}

	@Override
	public int getLuminance(BlockState blockState) {
		return blockState.get(LIT) ? super.getLuminance(blockState) : 0;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(LIT)) {
			if (random.nextInt(10) == 0) {
				world.playSound(
					(double)((float)blockPos.getX() + 0.5F),
					(double)((float)blockPos.getY() + 0.5F),
					(double)((float)blockPos.getZ() + 0.5F),
					SoundEvents.field_17483,
					SoundCategory.field_15245,
					0.5F + random.nextFloat(),
					random.nextFloat() * 0.7F + 0.6F,
					false
				);
			}

			if (random.nextInt(5) == 0) {
				for (int i = 0; i < random.nextInt(1) + 1; i++) {
					world.addParticle(
						ParticleTypes.field_11239,
						(double)((float)blockPos.getX() + 0.5F),
						(double)((float)blockPos.getY() + 0.5F),
						(double)((float)blockPos.getZ() + 0.5F),
						(double)(random.nextFloat() / 2.0F),
						5.0E-5,
						(double)(random.nextFloat() / 2.0F)
					);
				}
			}
		}
	}

	@Override
	public boolean tryFillWithFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		if (!(Boolean)blockState.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
			boolean bl = (Boolean)blockState.get(LIT);
			if (bl) {
				if (iWorld.isClient()) {
					for (int i = 0; i < 20; i++) {
						spawnSmokeParticle(iWorld.getWorld(), blockPos, (Boolean)blockState.get(SIGNAL_FIRE), true);
					}
				} else {
					iWorld.playSound(null, blockPos, SoundEvents.field_15222, SoundCategory.field_15245, 1.0F, 1.0F);
				}

				BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
				if (blockEntity instanceof CampfireBlockEntity) {
					((CampfireBlockEntity)blockEntity).spawnItemsBeingCooked();
				}
			}

			iWorld.setBlockState(blockPos, blockState.with(WATERLOGGED, Boolean.valueOf(true)).with(LIT, Boolean.valueOf(false)), 3);
			iWorld.getFluidTickScheduler().schedule(blockPos, fluidState.getFluid(), fluidState.getFluid().getTickRate(iWorld));
			return true;
		} else {
			return false;
		}
	}

	@Nullable
	private Entity method_23756(Entity entity) {
		if (entity instanceof AbstractFireballEntity) {
			return ((AbstractFireballEntity)entity).owner;
		} else {
			return entity instanceof ProjectileEntity ? ((ProjectileEntity)entity).getOwner() : null;
		}
	}

	@Override
	public void onProjectileHit(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
		if (!world.isClient) {
			boolean bl = entity instanceof AbstractFireballEntity || entity instanceof ProjectileEntity && entity.isOnFire();
			if (bl) {
				Entity entity2 = this.method_23756(entity);
				boolean bl2 = entity2 == null || entity2 instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.field_19388);
				if (bl2 && !(Boolean)blockState.get(LIT) && !(Boolean)blockState.get(WATERLOGGED)) {
					BlockPos blockPos = blockHitResult.getBlockPos();
					world.setBlockState(blockPos, blockState.with(Properties.LIT, Boolean.valueOf(true)), 11);
				}
			}
		}
	}

	public static void spawnSmokeParticle(World world, BlockPos blockPos, boolean bl, boolean bl2) {
		Random random = world.getRandom();
		DefaultParticleType defaultParticleType = bl ? ParticleTypes.field_17431 : ParticleTypes.field_17430;
		world.addImportantParticle(
			defaultParticleType,
			true,
			(double)blockPos.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
			(double)blockPos.getY() + random.nextDouble() + random.nextDouble(),
			(double)blockPos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1),
			0.0,
			0.07,
			0.0
		);
		if (bl2) {
			world.addParticle(
				ParticleTypes.field_11251,
				(double)blockPos.getX() + 0.25 + random.nextDouble() / 2.0 * (double)(random.nextBoolean() ? 1 : -1),
				(double)blockPos.getY() + 0.4,
				(double)blockPos.getZ() + 0.25 + random.nextDouble() / 2.0 * (double)(random.nextBoolean() ? 1 : -1),
				0.0,
				0.005,
				0.0
			);
		}
	}

	public static boolean isLitCampfireInRange(World world, BlockPos blockPos, int i) {
		for (int j = 1; j <= i; j++) {
			BlockPos blockPos2 = blockPos.down(j);
			BlockState blockState = world.getBlockState(blockPos2);
			if (isLitCampfire(blockState)) {
				return true;
			}

			boolean bl = VoxelShapes.matchesAnywhere(field_21580, blockState.getCollisionShape(world, blockPos, EntityContext.absent()), BooleanBiFunction.AND);
			if (bl) {
				BlockState blockState2 = world.getBlockState(blockPos2.down());
				return isLitCampfire(blockState2);
			}
		}

		return false;
	}

	private static boolean isLitCampfire(BlockState blockState) {
		return blockState.getBlock() == Blocks.field_17350 && (Boolean)blockState.get(LIT);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(blockState);
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new CampfireBlockEntity();
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
