package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BedBlock extends FacingBlock {
	public static final EnumProperty<BedBlock.BedBlockType> BED_TYPE = EnumProperty.of("part", BedBlock.BedBlockType.class);
	public static final BooleanProperty OCCUPIED = BooleanProperty.of("occupied");

	public BedBlock() {
		super(Material.WOOL);
		this.setDefaultState(this.stateManager.getDefaultState().with(BED_TYPE, BedBlock.BedBlockType.FOOT).with(OCCUPIED, false));
		this.setBoundingBox();
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			if (state.get(BED_TYPE) != BedBlock.BedBlockType.HEAD) {
				pos = pos.offset(state.get(FACING));
				state = world.getBlockState(pos);
				if (state.getBlock() != this) {
					return true;
				}
			}

			if (world.dimension.containsWorldSpawn() && world.getBiome(pos) != Biome.HELL) {
				if ((Boolean)state.get(OCCUPIED)) {
					PlayerEntity playerEntity = this.getPlayer(world, pos);
					if (playerEntity != null) {
						player.addMessage(new TranslatableText("tile.bed.occupied"));
						return true;
					}

					state = state.with(OCCUPIED, false);
					world.setBlockState(pos, state, 4);
				}

				PlayerEntity.SleepStatus sleepStatus = player.attemptSleep(pos);
				if (sleepStatus == PlayerEntity.SleepStatus.OK) {
					state = state.with(OCCUPIED, true);
					world.setBlockState(pos, state, 4);
					return true;
				} else {
					if (sleepStatus == PlayerEntity.SleepStatus.NOT_POSSIBLE_NOW) {
						player.addMessage(new TranslatableText("tile.bed.noSleep"));
					} else if (sleepStatus == PlayerEntity.SleepStatus.NOT_SAFE) {
						player.addMessage(new TranslatableText("tile.bed.notSafe"));
					}

					return true;
				}
			} else {
				world.setAir(pos);
				BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
				if (world.getBlockState(blockPos).getBlock() == this) {
					world.setAir(blockPos);
				}

				world.createExplosion(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 5.0F, true, true);
				return true;
			}
		}
	}

	private PlayerEntity getPlayer(World world, BlockPos pos) {
		for (PlayerEntity playerEntity : world.playerEntities) {
			if (playerEntity.isSleeping() && playerEntity.pos.equals(pos)) {
				return playerEntity;
			}
		}

		return null;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		this.setBoundingBox();
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		Direction direction = state.get(FACING);
		if (state.get(BED_TYPE) == BedBlock.BedBlockType.HEAD) {
			if (world.getBlockState(pos.offset(direction.getOpposite())).getBlock() != this) {
				world.setAir(pos);
			}
		} else if (world.getBlockState(pos.offset(direction)).getBlock() != this) {
			world.setAir(pos);
			if (!world.isClient) {
				this.dropAsItem(world, pos, state, 0);
			}
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return state.get(BED_TYPE) == BedBlock.BedBlockType.HEAD ? null : Items.BED;
	}

	private void setBoundingBox() {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
	}

	public static BlockPos findSpawnablePos(World world, BlockPos pos, int attemptsRemaining) {
		Direction direction = world.getBlockState(pos).get(FACING);
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
		return World.isOpaque(world, pos.down())
			&& !world.getBlockState(pos).getBlock().getMaterial().isSolid()
			&& !world.getBlockState(pos.up()).getBlock().getMaterial().isSolid();
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		if (state.get(BED_TYPE) == BedBlock.BedBlockType.FOOT) {
			super.randomDropAsItem(world, pos, state, chance, 0);
		}
	}

	@Override
	public int getPistonInteractionType() {
		return 1;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Items.BED;
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (player.abilities.creativeMode && state.get(BED_TYPE) == BedBlock.BedBlockType.HEAD) {
			BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
			if (world.getBlockState(blockPos).getBlock() == this) {
				world.setAir(blockPos);
			}
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		Direction direction = Direction.fromHorizontal(data);
		return (data & 8) > 0
			? this.getDefaultState().with(BED_TYPE, BedBlock.BedBlockType.HEAD).with(FACING, direction).with(OCCUPIED, (data & 4) > 0)
			: this.getDefaultState().with(BED_TYPE, BedBlock.BedBlockType.FOOT).with(FACING, direction);
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		if (state.get(BED_TYPE) == BedBlock.BedBlockType.FOOT) {
			BlockState blockState = view.getBlockState(pos.offset(state.get(FACING)));
			if (blockState.getBlock() == this) {
				state = state.with(OCCUPIED, blockState.get(OCCUPIED));
			}
		}

		return state;
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getHorizontal();
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
		return new StateManager(this, FACING, BED_TYPE, OCCUPIED);
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
