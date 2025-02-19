package net.minecraft.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Wearable;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.function.MaterialPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class CarvedPumpkinBlock extends HorizontalFacingBlock implements Wearable {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	@Nullable
	private BlockPattern snowGolemDispenserPattern;
	@Nullable
	private BlockPattern snowGolemPattern;
	@Nullable
	private BlockPattern ironGolemDispenserPattern;
	@Nullable
	private BlockPattern ironGolemPattern;
	private static final Predicate<BlockState> IS_GOLEM_HEAD_PREDICATE = state -> state != null
			&& (state.isOf(Blocks.CARVED_PUMPKIN) || state.isOf(Blocks.JACK_O_LANTERN));

	protected CarvedPumpkinBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.isOf(state.getBlock())) {
			this.trySpawnEntity(world, pos);
		}
	}

	public boolean canDispense(WorldView world, BlockPos pos) {
		return this.getSnowGolemDispenserPattern().searchAround(world, pos) != null || this.getIronGolemDispenserPattern().searchAround(world, pos) != null;
	}

	private void trySpawnEntity(World world, BlockPos pos) {
		BlockPattern.Result result = this.getSnowGolemPattern().searchAround(world, pos);
		if (result != null) {
			for (int i = 0; i < this.getSnowGolemPattern().getHeight(); i++) {
				CachedBlockPosition cachedBlockPosition = result.translate(0, i, 0);
				world.setBlockState(cachedBlockPosition.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
				world.syncWorldEvent(2001, cachedBlockPosition.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition.getBlockState()));
			}

			SnowGolemEntity snowGolemEntity = EntityType.SNOW_GOLEM.create(world);
			BlockPos blockPos = result.translate(0, 2, 0).getBlockPos();
			snowGolemEntity.refreshPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.05, (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
			world.spawnEntity(snowGolemEntity);

			for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, snowGolemEntity.getBoundingBox().expand(5.0))) {
				Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, snowGolemEntity);
			}

			for (int j = 0; j < this.getSnowGolemPattern().getHeight(); j++) {
				CachedBlockPosition cachedBlockPosition2 = result.translate(0, j, 0);
				world.updateNeighbors(cachedBlockPosition2.getBlockPos(), Blocks.AIR);
			}
		} else {
			result = this.getIronGolemPattern().searchAround(world, pos);
			if (result != null) {
				for (int k = 0; k < this.getIronGolemPattern().getWidth(); k++) {
					for (int l = 0; l < this.getIronGolemPattern().getHeight(); l++) {
						CachedBlockPosition cachedBlockPosition3 = result.translate(k, l, 0);
						world.setBlockState(cachedBlockPosition3.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
						world.syncWorldEvent(2001, cachedBlockPosition3.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition3.getBlockState()));
					}
				}

				BlockPos blockPos2 = result.translate(1, 2, 0).getBlockPos();
				IronGolemEntity ironGolemEntity = EntityType.IRON_GOLEM.create(world);
				ironGolemEntity.setPlayerCreated(true);
				ironGolemEntity.refreshPositionAndAngles((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.05, (double)blockPos2.getZ() + 0.5, 0.0F, 0.0F);
				world.spawnEntity(ironGolemEntity);

				for (ServerPlayerEntity serverPlayerEntity2 : world.getNonSpectatingEntities(ServerPlayerEntity.class, ironGolemEntity.getBoundingBox().expand(5.0))) {
					Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity2, ironGolemEntity);
				}

				for (int m = 0; m < this.getIronGolemPattern().getWidth(); m++) {
					for (int n = 0; n < this.getIronGolemPattern().getHeight(); n++) {
						CachedBlockPosition cachedBlockPosition4 = result.translate(m, n, 0);
						world.updateNeighbors(cachedBlockPosition4.getBlockPos(), Blocks.AIR);
					}
				}
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	private BlockPattern getSnowGolemDispenserPattern() {
		if (this.snowGolemDispenserPattern == null) {
			this.snowGolemDispenserPattern = BlockPatternBuilder.start()
				.aisle(" ", "#", "#")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK)))
				.build();
		}

		return this.snowGolemDispenserPattern;
	}

	private BlockPattern getSnowGolemPattern() {
		if (this.snowGolemPattern == null) {
			this.snowGolemPattern = BlockPatternBuilder.start()
				.aisle("^", "#", "#")
				.where('^', CachedBlockPosition.matchesBlockState(IS_GOLEM_HEAD_PREDICATE))
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK)))
				.build();
		}

		return this.snowGolemPattern;
	}

	private BlockPattern getIronGolemDispenserPattern() {
		if (this.ironGolemDispenserPattern == null) {
			this.ironGolemDispenserPattern = BlockPatternBuilder.start()
				.aisle("~ ~", "###", "~#~")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK)))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
				.build();
		}

		return this.ironGolemDispenserPattern;
	}

	private BlockPattern getIronGolemPattern() {
		if (this.ironGolemPattern == null) {
			this.ironGolemPattern = BlockPatternBuilder.start()
				.aisle("~^~", "###", "~#~")
				.where('^', CachedBlockPosition.matchesBlockState(IS_GOLEM_HEAD_PREDICATE))
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK)))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
				.build();
		}

		return this.ironGolemPattern;
	}
}
