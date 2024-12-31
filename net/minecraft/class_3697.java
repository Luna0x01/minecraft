package net.minecraft;

import java.util.function.Predicate;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.block.BlockMaterialPredicate;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class class_3697 extends HorizontalFacingBlock {
	public static final DirectionProperty field_18229 = HorizontalFacingBlock.FACING;
	private BlockPattern field_18230;
	private BlockPattern field_18231;
	private BlockPattern field_18232;
	private BlockPattern field_18233;
	private static final Predicate<BlockState> field_18234 = blockState -> blockState != null
			&& (blockState.getBlock() == Blocks.CARVED_PUMPKIN || blockState.getBlock() == Blocks.JACK_O_LANTERN);

	protected class_3697(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18229, Direction.NORTH));
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (oldState.getBlock() != state.getBlock()) {
			this.method_16638(world, pos);
		}
	}

	public boolean method_16639(RenderBlockView renderBlockView, BlockPos blockPos) {
		return this.method_16640().method_16938(renderBlockView, blockPos) != null || this.method_16642().method_16938(renderBlockView, blockPos) != null;
	}

	private void method_16638(World world, BlockPos blockPos) {
		BlockPattern.Result result = this.method_16641().method_16938(world, blockPos);
		if (result != null) {
			for (int i = 0; i < this.method_16641().getHeight(); i++) {
				CachedBlockPosition cachedBlockPosition = result.translate(0, i, 0);
				world.setBlockState(cachedBlockPosition.getPos(), Blocks.AIR.getDefaultState(), 2);
			}

			SnowGolemEntity snowGolemEntity = new SnowGolemEntity(world);
			BlockPos blockPos2 = result.translate(0, 2, 0).getPos();
			snowGolemEntity.refreshPositionAndAngles((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.05, (double)blockPos2.getZ() + 0.5, 0.0F, 0.0F);
			world.method_3686(snowGolemEntity);

			for (ServerPlayerEntity serverPlayerEntity : world.getEntitiesInBox(ServerPlayerEntity.class, snowGolemEntity.getBoundingBox().expand(5.0))) {
				AchievementsAndCriterions.field_16341.method_14397(serverPlayerEntity, snowGolemEntity);
			}

			int j = Block.getRawIdFromState(Blocks.SNOW_BLOCK.getDefaultState());
			world.syncGlobalEvent(2001, blockPos2, j);
			world.syncGlobalEvent(2001, blockPos2.up(), j);

			for (int k = 0; k < this.method_16641().getHeight(); k++) {
				CachedBlockPosition cachedBlockPosition2 = result.translate(0, k, 0);
				world.method_16342(cachedBlockPosition2.getPos(), Blocks.AIR);
			}
		} else {
			result = this.method_16643().method_16938(world, blockPos);
			if (result != null) {
				for (int l = 0; l < this.method_16643().getWidth(); l++) {
					for (int m = 0; m < this.method_16643().getHeight(); m++) {
						world.setBlockState(result.translate(l, m, 0).getPos(), Blocks.AIR.getDefaultState(), 2);
					}
				}

				BlockPos blockPos3 = result.translate(1, 2, 0).getPos();
				IronGolemEntity ironGolemEntity = new IronGolemEntity(world);
				ironGolemEntity.setPlayerCreated(true);
				ironGolemEntity.refreshPositionAndAngles((double)blockPos3.getX() + 0.5, (double)blockPos3.getY() + 0.05, (double)blockPos3.getZ() + 0.5, 0.0F, 0.0F);
				world.method_3686(ironGolemEntity);

				for (ServerPlayerEntity serverPlayerEntity2 : world.getEntitiesInBox(ServerPlayerEntity.class, ironGolemEntity.getBoundingBox().expand(5.0))) {
					AchievementsAndCriterions.field_16341.method_14397(serverPlayerEntity2, ironGolemEntity);
				}

				for (int n = 0; n < 120; n++) {
					world.method_16343(
						class_4342.field_21355,
						(double)blockPos3.getX() + world.random.nextDouble(),
						(double)blockPos3.getY() + world.random.nextDouble() * 3.9,
						(double)blockPos3.getZ() + world.random.nextDouble(),
						0.0,
						0.0,
						0.0
					);
				}

				for (int o = 0; o < this.method_16643().getWidth(); o++) {
					for (int p = 0; p < this.method_16643().getHeight(); p++) {
						CachedBlockPosition cachedBlockPosition3 = result.translate(o, p, 0);
						world.method_16342(cachedBlockPosition3.getPos(), Blocks.AIR);
					}
				}
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(field_18229, context.method_16145().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18229);
	}

	protected BlockPattern method_16640() {
		if (this.field_18230 == null) {
			this.field_18230 = BlockPatternBuilder.start()
				.aisle(" ", "#", "#")
				.method_16940('#', CachedBlockPosition.method_16935(BlockStatePredicate.create(Blocks.SNOW_BLOCK)))
				.build();
		}

		return this.field_18230;
	}

	protected BlockPattern method_16641() {
		if (this.field_18231 == null) {
			this.field_18231 = BlockPatternBuilder.start()
				.aisle("^", "#", "#")
				.method_16940('^', CachedBlockPosition.method_16935(field_18234))
				.method_16940('#', CachedBlockPosition.method_16935(BlockStatePredicate.create(Blocks.SNOW_BLOCK)))
				.build();
		}

		return this.field_18231;
	}

	protected BlockPattern method_16642() {
		if (this.field_18232 == null) {
			this.field_18232 = BlockPatternBuilder.start()
				.aisle("~ ~", "###", "~#~")
				.method_16940('#', CachedBlockPosition.method_16935(BlockStatePredicate.create(Blocks.IRON_BLOCK)))
				.method_16940('~', CachedBlockPosition.method_16935(BlockMaterialPredicate.create(Material.AIR)))
				.build();
		}

		return this.field_18232;
	}

	protected BlockPattern method_16643() {
		if (this.field_18233 == null) {
			this.field_18233 = BlockPatternBuilder.start()
				.aisle("~^~", "###", "~#~")
				.method_16940('^', CachedBlockPosition.method_16935(field_18234))
				.method_16940('#', CachedBlockPosition.method_16935(BlockStatePredicate.create(Blocks.IRON_BLOCK)))
				.method_16940('~', CachedBlockPosition.method_16935(BlockMaterialPredicate.create(Material.AIR)))
				.build();
		}

		return this.field_18233;
	}
}
