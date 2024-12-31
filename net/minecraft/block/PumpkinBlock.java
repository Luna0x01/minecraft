package net.minecraft.block;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.predicate.block.BlockMaterialPredicate;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PumpkinBlock extends HorizontalFacingBlock {
	private BlockPattern snowGolemDispenserPattern;
	private BlockPattern snowGolemPattern;
	private BlockPattern ironGolemDispenserPattern;
	private BlockPattern ironGolemPattern;
	private static final Predicate<BlockState> IS_GOLEM_HEAD_PREDICATE = new Predicate<BlockState>() {
		public boolean apply(@Nullable BlockState blockState) {
			return blockState != null && (blockState.getBlock() == Blocks.PUMPKIN || blockState.getBlock() == Blocks.JACK_O_LANTERN);
		}
	};

	protected PumpkinBlock() {
		super(Material.PUMPKIN, MaterialColor.ORANGE);
		this.setDefaultState(this.stateManager.getDefaultState().with(DIRECTION, Direction.NORTH));
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		super.onCreation(world, pos, state);
		this.trySpawnEntity(world, pos);
	}

	public boolean canDispense(World world, BlockPos pos) {
		return this.getSnowGolemDispenserPattern().searchAround(world, pos) != null || this.getIronGolemDispenserPattern().searchAround(world, pos) != null;
	}

	private void trySpawnEntity(World world, BlockPos pos) {
		BlockPattern.Result result = this.getSnowGolemPattern().searchAround(world, pos);
		if (result != null) {
			for (int i = 0; i < this.getSnowGolemPattern().getHeight(); i++) {
				CachedBlockPosition cachedBlockPosition = result.translate(0, i, 0);
				world.setBlockState(cachedBlockPosition.getPos(), Blocks.AIR.getDefaultState(), 2);
			}

			SnowGolemEntity snowGolemEntity = new SnowGolemEntity(world);
			BlockPos blockPos = result.translate(0, 2, 0).getPos();
			snowGolemEntity.refreshPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.05, (double)blockPos.getZ() + 0.5, 0.0F, 0.0F);
			world.spawnEntity(snowGolemEntity);

			for (ServerPlayerEntity serverPlayerEntity : world.getEntitiesInBox(ServerPlayerEntity.class, snowGolemEntity.getBoundingBox().expand(5.0))) {
				AchievementsAndCriterions.field_16341.method_14397(serverPlayerEntity, snowGolemEntity);
			}

			for (int j = 0; j < 120; j++) {
				world.addParticle(
					ParticleType.SNOW_SHOVEL,
					(double)blockPos.getX() + world.random.nextDouble(),
					(double)blockPos.getY() + world.random.nextDouble() * 2.5,
					(double)blockPos.getZ() + world.random.nextDouble(),
					0.0,
					0.0,
					0.0
				);
			}

			for (int k = 0; k < this.getSnowGolemPattern().getHeight(); k++) {
				CachedBlockPosition cachedBlockPosition2 = result.translate(0, k, 0);
				world.method_8531(cachedBlockPosition2.getPos(), Blocks.AIR, false);
			}
		} else {
			result = this.getIronGolemPattern().searchAround(world, pos);
			if (result != null) {
				for (int l = 0; l < this.getIronGolemPattern().getWidth(); l++) {
					for (int m = 0; m < this.getIronGolemPattern().getHeight(); m++) {
						world.setBlockState(result.translate(l, m, 0).getPos(), Blocks.AIR.getDefaultState(), 2);
					}
				}

				BlockPos blockPos2 = result.translate(1, 2, 0).getPos();
				IronGolemEntity ironGolemEntity = new IronGolemEntity(world);
				ironGolemEntity.setPlayerCreated(true);
				ironGolemEntity.refreshPositionAndAngles((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.05, (double)blockPos2.getZ() + 0.5, 0.0F, 0.0F);
				world.spawnEntity(ironGolemEntity);

				for (ServerPlayerEntity serverPlayerEntity2 : world.getEntitiesInBox(ServerPlayerEntity.class, ironGolemEntity.getBoundingBox().expand(5.0))) {
					AchievementsAndCriterions.field_16341.method_14397(serverPlayerEntity2, ironGolemEntity);
				}

				for (int n = 0; n < 120; n++) {
					world.addParticle(
						ParticleType.SNOWBALL,
						(double)blockPos2.getX() + world.random.nextDouble(),
						(double)blockPos2.getY() + world.random.nextDouble() * 3.9,
						(double)blockPos2.getZ() + world.random.nextDouble(),
						0.0,
						0.0,
						0.0
					);
				}

				for (int o = 0; o < this.getIronGolemPattern().getWidth(); o++) {
					for (int p = 0; p < this.getIronGolemPattern().getHeight(); p++) {
						CachedBlockPosition cachedBlockPosition3 = result.translate(o, p, 0);
						world.method_8531(cachedBlockPosition3.getPos(), Blocks.AIR, false);
					}
				}
			}
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock().material.isReplaceable() && world.getBlockState(pos.down()).method_11739();
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(DIRECTION, rotation.rotate(state.get(DIRECTION)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(DIRECTION)));
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(DIRECTION, entity.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(DIRECTION, Direction.fromHorizontal(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((Direction)state.get(DIRECTION)).getHorizontal();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, DIRECTION);
	}

	protected BlockPattern getSnowGolemDispenserPattern() {
		if (this.snowGolemDispenserPattern == null) {
			this.snowGolemDispenserPattern = BlockPatternBuilder.start()
				.aisle(" ", "#", "#")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.create(Blocks.SNOW)))
				.build();
		}

		return this.snowGolemDispenserPattern;
	}

	protected BlockPattern getSnowGolemPattern() {
		if (this.snowGolemPattern == null) {
			this.snowGolemPattern = BlockPatternBuilder.start()
				.aisle("^", "#", "#")
				.where('^', CachedBlockPosition.matchesBlockState(IS_GOLEM_HEAD_PREDICATE))
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.create(Blocks.SNOW)))
				.build();
		}

		return this.snowGolemPattern;
	}

	protected BlockPattern getIronGolemDispenserPattern() {
		if (this.ironGolemDispenserPattern == null) {
			this.ironGolemDispenserPattern = BlockPatternBuilder.start()
				.aisle("~ ~", "###", "~#~")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.create(Blocks.IRON_BLOCK)))
				.where('~', CachedBlockPosition.matchesBlockState(BlockMaterialPredicate.create(Material.AIR)))
				.build();
		}

		return this.ironGolemDispenserPattern;
	}

	protected BlockPattern getIronGolemPattern() {
		if (this.ironGolemPattern == null) {
			this.ironGolemPattern = BlockPatternBuilder.start()
				.aisle("~^~", "###", "~#~")
				.where('^', CachedBlockPosition.matchesBlockState(IS_GOLEM_HEAD_PREDICATE))
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.create(Blocks.IRON_BLOCK)))
				.where('~', CachedBlockPosition.matchesBlockState(BlockMaterialPredicate.create(Material.AIR)))
				.build();
		}

		return this.ironGolemPattern;
	}
}
