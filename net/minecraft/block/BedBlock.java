package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;

public class BedBlock extends HorizontalFacingBlock implements BlockEntityProvider {
	public static final EnumProperty<BedBlock.BedBlockType> BED_TYPE = EnumProperty.of("part", BedBlock.BedBlockType.class);
	public static final BooleanProperty OCCUPIED = BooleanProperty.of("occupied");
	protected static final Box BOUNDING_BOX = new Box(0.0, 0.0, 0.0, 1.0, 0.5625, 1.0);

	public BedBlock() {
		super(Material.WOOL);
		this.setDefaultState(this.stateManager.getDefaultState().with(BED_TYPE, BedBlock.BedBlockType.FOOT).with(OCCUPIED, false));
		this.blockEntity = true;
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state, BlockView view, BlockPos pos) {
		if (state.get(BED_TYPE) == BedBlock.BedBlockType.FOOT) {
			BlockEntity blockEntity = view.getBlockEntity(pos);
			if (blockEntity instanceof BedBlockEntity) {
				DyeColor dyeColor = ((BedBlockEntity)blockEntity).getColor();
				return MaterialColor.fromDye(dyeColor);
			}
		}

		return MaterialColor.WEB;
	}

	@Override
	public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction direction, float f, float g, float h) {
		if (world.isClient) {
			return true;
		} else {
			if (state.get(BED_TYPE) != BedBlock.BedBlockType.HEAD) {
				pos = pos.offset(state.get(DIRECTION));
				state = world.getBlockState(pos);
				if (state.getBlock() != this) {
					return true;
				}
			}

			if (world.dimension.containsWorldSpawn() && world.getBiome(pos) != Biomes.NETHER) {
				if ((Boolean)state.get(OCCUPIED)) {
					PlayerEntity playerEntity = this.getPlayer(world, pos);
					if (playerEntity != null) {
						player.sendMessage(new TranslatableText("tile.bed.occupied"), true);
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
						player.sendMessage(new TranslatableText("tile.bed.noSleep"), true);
					} else if (sleepStatus == PlayerEntity.SleepStatus.NOT_SAFE) {
						player.sendMessage(new TranslatableText("tile.bed.notSafe"), true);
					} else if (sleepStatus == PlayerEntity.SleepStatus.TOO_FAR_AWAY) {
						player.sendMessage(new TranslatableText("tile.bed.tooFarAway"), true);
					}

					return true;
				}
			} else {
				world.setAir(pos);
				BlockPos blockPos = pos.offset(((Direction)state.get(DIRECTION)).getOpposite());
				if (world.getBlockState(blockPos).getBlock() == this) {
					world.setAir(blockPos);
				}

				world.createExplosion(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 5.0F, true, true);
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
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		super.onLandedUpon(world, pos, entity, distance * 0.5F);
	}

	@Override
	public void setEntityVelocity(World world, Entity entity) {
		if (entity.isSneaking()) {
			super.setEntityVelocity(world, entity);
		} else if (entity.velocityY < 0.0) {
			entity.velocityY = -entity.velocityY * 0.66F;
			if (!(entity instanceof LivingEntity)) {
				entity.velocityY *= 0.8;
			}
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		Direction direction = state.get(DIRECTION);
		if (state.get(BED_TYPE) == BedBlock.BedBlockType.FOOT) {
			if (world.getBlockState(pos.offset(direction)).getBlock() != this) {
				world.setAir(pos);
			}
		} else if (world.getBlockState(pos.offset(direction.getOpposite())).getBlock() != this) {
			if (!world.isClient) {
				this.dropAsItem(world, pos, state, 0);
			}

			world.setAir(pos);
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return state.get(BED_TYPE) == BedBlock.BedBlockType.FOOT ? Items.AIR : Items.BED;
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return BOUNDING_BOX;
	}

	@Override
	public boolean method_13704(BlockState state) {
		return true;
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
		if (state.get(BED_TYPE) == BedBlock.BedBlockType.HEAD) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			DyeColor dyeColor = blockEntity instanceof BedBlockEntity ? ((BedBlockEntity)blockEntity).getColor() : DyeColor.RED;
			onBlockBreak(world, pos, new ItemStack(Items.BED, 1, dyeColor.getId()));
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
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		BlockPos blockPos2 = blockPos;
		if (blockState.get(BED_TYPE) == BedBlock.BedBlockType.FOOT) {
			blockPos2 = blockPos.offset(blockState.get(DIRECTION));
		}

		BlockEntity blockEntity = world.getBlockEntity(blockPos2);
		DyeColor dyeColor = blockEntity instanceof BedBlockEntity ? ((BedBlockEntity)blockEntity).getColor() : DyeColor.RED;
		return new ItemStack(Items.BED, 1, dyeColor.getId());
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (player.abilities.creativeMode && state.get(BED_TYPE) == BedBlock.BedBlockType.FOOT) {
			BlockPos blockPos = pos.offset(state.get(DIRECTION));
			if (world.getBlockState(blockPos).getBlock() == this) {
				world.setAir(blockPos);
			}
		}
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
		if (state.get(BED_TYPE) == BedBlock.BedBlockType.HEAD && blockEntity instanceof BedBlockEntity) {
			BedBlockEntity bedBlockEntity = (BedBlockEntity)blockEntity;
			ItemStack itemStack = bedBlockEntity.toItemStack();
			onBlockBreak(world, pos, itemStack);
		} else {
			super.method_8651(world, player, pos, state, null, stack);
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		super.onBreaking(world, pos, state);
		world.removeBlockEntity(pos);
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
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, DIRECTION, BED_TYPE, OCCUPIED);
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new BedBlockEntity();
	}

	public static boolean method_14304(int i) {
		return (i & 8) != 0;
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
