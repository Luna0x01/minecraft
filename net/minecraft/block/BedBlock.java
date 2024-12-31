package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;

public class BedBlock extends HorizontalFacingBlock implements BlockEntityProvider {
	public static final EnumProperty<BedPart> PART = Properties.BED_PART;
	public static final BooleanProperty OCCUPIED = Properties.OCCUPIED;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
	private final DyeColor color;

	public BedBlock(DyeColor dyeColor, Block.Builder builder) {
		super(builder);
		this.color = dyeColor;
		this.setDefaultState(this.stateManager.method_16923().withProperty(PART, BedPart.FOOT).withProperty(OCCUPIED, Boolean.valueOf(false)));
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state, BlockView view, BlockPos pos) {
		return state.getProperty(PART) == BedPart.FOOT ? this.color.getColorOfMaterial() : MaterialColor.WEB;
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (world.isClient) {
			return true;
		} else {
			if (state.getProperty(PART) != BedPart.HEAD) {
				pos = pos.offset(state.getProperty(FACING));
				state = world.getBlockState(pos);
				if (state.getBlock() != this) {
					return true;
				}
			}

			if (world.dimension.containsWorldSpawn() && world.method_8577(pos) != Biomes.NETHER) {
				if ((Boolean)state.getProperty(OCCUPIED)) {
					PlayerEntity playerEntity = this.getPlayer(world, pos);
					if (playerEntity != null) {
						player.sendMessage(new TranslatableText("block.minecraft.bed.occupied"), true);
						return true;
					}

					state = state.withProperty(OCCUPIED, Boolean.valueOf(false));
					world.setBlockState(pos, state, 4);
				}

				PlayerEntity.SleepStatus sleepStatus = player.attemptSleep(pos);
				if (sleepStatus == PlayerEntity.SleepStatus.OK) {
					state = state.withProperty(OCCUPIED, Boolean.valueOf(true));
					world.setBlockState(pos, state, 4);
					return true;
				} else {
					if (sleepStatus == PlayerEntity.SleepStatus.NOT_POSSIBLE_NOW) {
						player.sendMessage(new TranslatableText("block.minecraft.bed.no_sleep"), true);
					} else if (sleepStatus == PlayerEntity.SleepStatus.NOT_SAFE) {
						player.sendMessage(new TranslatableText("block.minecraft.bed.not_safe"), true);
					} else if (sleepStatus == PlayerEntity.SleepStatus.TOO_FAR_AWAY) {
						player.sendMessage(new TranslatableText("block.minecraft.bed.too_far_away"), true);
					}

					return true;
				}
			} else {
				world.method_8553(pos);
				BlockPos blockPos = pos.offset(((Direction)state.getProperty(FACING)).getOpposite());
				if (world.getBlockState(blockPos).getBlock() == this) {
					world.method_8553(blockPos);
				}

				world.method_16320(null, DamageSource.method_15545(), (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 5.0F, true, true);
				return true;
			}
		}
	}

	@Nullable
	private PlayerEntity getPlayer(World world, BlockPos pos) {
		for (PlayerEntity playerEntity : world.playerEntities) {
			if (playerEntity.isSleeping() && playerEntity.pos.equals(pos)) {
				return playerEntity;
			}
		}

		return null;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		super.onLandedUpon(world, pos, entity, distance * 0.5F);
	}

	@Override
	public void onEntityLand(BlockView world, Entity entity) {
		if (entity.isSneaking()) {
			super.onEntityLand(world, entity);
		} else if (entity.velocityY < 0.0) {
			entity.velocityY = -entity.velocityY * 0.66F;
			if (!(entity instanceof LivingEntity)) {
				entity.velocityY *= 0.8;
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (direction == getDirectionTowardsOtherPart(state.getProperty(PART), state.getProperty(FACING))) {
			return neighborState.getBlock() == this && neighborState.getProperty(PART) != state.getProperty(PART)
				? state.withProperty(OCCUPIED, neighborState.getProperty(OCCUPIED))
				: Blocks.AIR.getDefaultState();
		} else {
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		}
	}

	private static Direction getDirectionTowardsOtherPart(BedPart part, Direction direction) {
		return part == BedPart.FOOT ? direction : direction.getOpposite();
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.method_8651(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			super.onStateReplaced(state, world, pos, newState, moved);
			world.removeBlockEntity(pos);
		}
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		BedPart bedPart = state.getProperty(PART);
		boolean bl = bedPart == BedPart.HEAD;
		BlockPos blockPos = pos.offset(getDirectionTowardsOtherPart(bedPart, state.getProperty(FACING)));
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == this && blockState.getProperty(PART) != bedPart) {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
			world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
			if (!world.isClient && !player.isCreative()) {
				if (bl) {
					state.method_16867(world, pos, 0);
				} else {
					blockState.method_16867(world, blockPos, 0);
				}
			}

			player.method_15932(Stats.MINED.method_21429(this));
		}

		super.onBreakByPlayer(world, pos, state, player);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction direction = context.method_16145();
		BlockPos blockPos = context.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(direction);
		return context.getWorld().getBlockState(blockPos2).canReplace(context) ? this.getDefaultState().withProperty(FACING, direction) : null;
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return (Itemable)(state.getProperty(PART) == BedPart.FOOT ? Items.AIR : super.getDroppedItem(state, world, pos, fortuneLevel));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public boolean method_13704(BlockState state) {
		return true;
	}

	@Nullable
	public static BlockPos method_8629(BlockView blockView, BlockPos blockPos, int i) {
		Direction direction = blockView.getBlockState(blockPos).getProperty(FACING);
		int j = blockPos.getX();
		int k = blockPos.getY();
		int l = blockPos.getZ();

		for (int m = 0; m <= 1; m++) {
			int n = j - direction.getOffsetX() * m - 1;
			int o = l - direction.getOffsetZ() * m - 1;
			int p = n + 2;
			int q = o + 2;

			for (int r = n; r <= p; r++) {
				for (int s = o; s <= q; s++) {
					BlockPos blockPos2 = new BlockPos(r, k, s);
					if (method_16558(blockView, blockPos2)) {
						if (i <= 0) {
							return blockPos2;
						}

						i--;
					}
				}
			}
		}

		return null;
	}

	protected static boolean method_16558(BlockView blockView, BlockPos blockPos) {
		return blockView.getBlockState(blockPos.down()).method_16913()
			&& !blockView.getBlockState(blockPos).getMaterial().isSolid()
			&& !blockView.getBlockState(blockPos.up()).getMaterial().isSolid();
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, PART, OCCUPIED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new BedBlockEntity(this.color);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		if (!world.isClient) {
			BlockPos blockPos = pos.offset(state.getProperty(FACING));
			world.setBlockState(blockPos, state.withProperty(PART, BedPart.HEAD), 3);
			world.method_16342(pos, Blocks.AIR);
			state.method_16876(world, pos, 3);
		}
	}

	public DyeColor getColor() {
		return this.color;
	}

	@Override
	public long getRenderingSeed(BlockState state, BlockPos pos) {
		BlockPos blockPos = pos.offset(state.getProperty(FACING), state.getProperty(PART) == BedPart.HEAD ? 0 : 1);
		return MathHelper.hashCode(blockPos.getX(), pos.getY(), blockPos.getZ());
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
