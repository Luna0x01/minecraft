package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;

public class BedBlock extends HorizontalFacingBlock {
	public static final EnumProperty<BedBlock.BedBlockType> BED_TYPE = EnumProperty.of("part", BedBlock.BedBlockType.class);
	public static final BooleanProperty OCCUPIED = BooleanProperty.of("occupied");
	protected static final Box BOUNDING_BOX = new Box(0.0, 0.0, 0.0, 1.0, 0.5625, 1.0);

	public BedBlock() {
		super(Material.WOOL);
		this.setDefaultState(this.stateManager.getDefaultState().with(BED_TYPE, BedBlock.BedBlockType.FOOT).with(OCCUPIED, false));
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
		if (world.isClient) {
			return true;
		} else {
			if (blockState.get(BED_TYPE) != BedBlock.BedBlockType.HEAD) {
				blockPos = blockPos.offset(blockState.get(DIRECTION));
				blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() != this) {
					return true;
				}
			}

			if (world.dimension.containsWorldSpawn() && world.getBiome(blockPos) != Biomes.NETHER) {
				if ((Boolean)blockState.get(OCCUPIED)) {
					PlayerEntity playerEntity2 = this.getPlayer(world, blockPos);
					if (playerEntity2 != null) {
						playerEntity.addMessage(new TranslatableText("tile.bed.occupied"));
						return true;
					}

					blockState = blockState.with(OCCUPIED, false);
					world.setBlockState(blockPos, blockState, 4);
				}

				PlayerEntity.SleepStatus sleepStatus = playerEntity.attemptSleep(blockPos);
				if (sleepStatus == PlayerEntity.SleepStatus.OK) {
					blockState = blockState.with(OCCUPIED, true);
					world.setBlockState(blockPos, blockState, 4);
					return true;
				} else {
					if (sleepStatus == PlayerEntity.SleepStatus.NOT_POSSIBLE_NOW) {
						playerEntity.addMessage(new TranslatableText("tile.bed.noSleep"));
					} else if (sleepStatus == PlayerEntity.SleepStatus.NOT_SAFE) {
						playerEntity.addMessage(new TranslatableText("tile.bed.notSafe"));
					}

					return true;
				}
			} else {
				world.setAir(blockPos);
				BlockPos blockPos2 = blockPos.offset(((Direction)blockState.get(DIRECTION)).getOpposite());
				if (world.getBlockState(blockPos2).getBlock() == this) {
					world.setAir(blockPos2);
				}

				world.createExplosion(null, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 5.0F, true, true);
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
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		Direction direction = blockState.get(DIRECTION);
		if (blockState.get(BED_TYPE) == BedBlock.BedBlockType.HEAD) {
			if (world.getBlockState(blockPos.offset(direction.getOpposite())).getBlock() != this) {
				world.setAir(blockPos);
			}
		} else if (world.getBlockState(blockPos.offset(direction)).getBlock() != this) {
			world.setAir(blockPos);
			if (!world.isClient) {
				this.dropAsItem(world, blockPos, blockState, 0);
			}
		}
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return state.get(BED_TYPE) == BedBlock.BedBlockType.HEAD ? null : Items.BED;
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return BOUNDING_BOX;
	}

	@Nullable
	public static BlockPos findSpawnablePos(World world, BlockPos pos, int attemptsRemaining) {
		Direction direction = world.getBlockState(pos).get(DIRECTION);
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();

		for (int l = 0; l <= 1; l++) {
			int m = i - direction.getOffsetX() * l - 1;
			int n = k - direction.getOffsetZ() * l - 1;
			int o = m + 2;
			int p = n + 2;

			for (int q = m; q <= o; q++) {
				for (int r = n; r <= p; r++) {
					BlockPos blockPos = new BlockPos(q, j, r);
					if (canPlayerSpawnAt(world, blockPos)) {
						if (attemptsRemaining <= 0) {
							return blockPos;
						}

						attemptsRemaining--;
					}
				}
			}
		}

		return null;
	}

	protected static boolean canPlayerSpawnAt(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).method_11739()
			&& !world.getBlockState(pos).getMaterial().isSolid()
			&& !world.getBlockState(pos.up()).getMaterial().isSolid();
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		if (state.get(BED_TYPE) == BedBlock.BedBlockType.FOOT) {
			super.randomDropAsItem(world, pos, state, chance, 0);
		}
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
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.BED);
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (player.abilities.creativeMode && state.get(BED_TYPE) == BedBlock.BedBlockType.HEAD) {
			BlockPos blockPos = pos.offset(((Direction)state.get(DIRECTION)).getOpposite());
			if (world.getBlockState(blockPos).getBlock() == this) {
				world.setAir(blockPos);
			}
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		Direction direction = Direction.fromHorizontal(data);
		return (data & 8) > 0
			? this.getDefaultState().with(BED_TYPE, BedBlock.BedBlockType.HEAD).with(DIRECTION, direction).with(OCCUPIED, (data & 4) > 0)
			: this.getDefaultState().with(BED_TYPE, BedBlock.BedBlockType.FOOT).with(DIRECTION, direction);
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		if (state.get(BED_TYPE) == BedBlock.BedBlockType.FOOT) {
			BlockState blockState = view.getBlockState(pos.offset(state.get(DIRECTION)));
			if (blockState.getBlock() == this) {
				state = state.with(OCCUPIED, blockState.get(OCCUPIED));
			}
		}

		return state;
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
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(DIRECTION)).getHorizontal();
		if (state.get(BED_TYPE) == BedBlock.BedBlockType.HEAD) {
			i |= 8;
			if ((Boolean)state.get(OCCUPIED)) {
				i |= 4;
			}
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, DIRECTION, BED_TYPE, OCCUPIED);
	}

	public static enum BedBlockType implements StringIdentifiable {
		HEAD("head"),
		FOOT("foot");

		private final String name;

		private BedBlockType(String string2) {
			this.name = string2;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
