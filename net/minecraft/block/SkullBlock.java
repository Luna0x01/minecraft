package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.predicate.block.BlockMaterialPredicate;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class SkullBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = FacingBlock.FACING;
	public static final BooleanProperty NO_DROP = BooleanProperty.of("nodrop");
	private static final Predicate<CachedBlockPosition> IS_WITHER_SKULL_PREDICATE = new Predicate<CachedBlockPosition>() {
		public boolean apply(@Nullable CachedBlockPosition cachedBlockPosition) {
			return cachedBlockPosition.getBlockState() != null
				&& cachedBlockPosition.getBlockState().getBlock() == Blocks.SKULL
				&& cachedBlockPosition.getBlockEntity() instanceof SkullBlockEntity
				&& ((SkullBlockEntity)cachedBlockPosition.getBlockEntity()).getSkullType() == 1;
		}
	};
	protected static final Box field_12752 = new Box(0.25, 0.0, 0.25, 0.75, 0.5, 0.75);
	protected static final Box field_12753 = new Box(0.25, 0.25, 0.5, 0.75, 0.75, 1.0);
	protected static final Box field_12754 = new Box(0.25, 0.25, 0.0, 0.75, 0.75, 0.5);
	protected static final Box field_12755 = new Box(0.5, 0.25, 0.25, 1.0, 0.75, 0.75);
	protected static final Box field_12756 = new Box(0.0, 0.25, 0.25, 0.5, 0.75, 0.75);
	private BlockPattern witherSkeletonDispenserPattern;
	private BlockPattern witherSkeletonPattern;

	protected SkullBlock() {
		super(Material.DECORATION);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(NO_DROP, false));
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate("tile.skull.skeleton.name");
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		switch ((Direction)state.get(FACING)) {
			case UP:
			default:
				return field_12752;
			case NORTH:
				return field_12753;
			case SOUTH:
				return field_12754;
			case WEST:
				return field_12755;
			case EAST:
				return field_12756;
		}
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, entity.getHorizontalDirection()).with(NO_DROP, false);
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new SkullBlockEntity();
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		int i = 0;
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof SkullBlockEntity) {
			i = ((SkullBlockEntity)blockEntity).getSkullType();
		}

		return new ItemStack(Items.SKULL, 1, i);
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (player.abilities.creativeMode) {
			state = state.with(NO_DROP, true);
			world.setBlockState(pos, state, 4);
		}

		super.onBreakByPlayer(world, pos, state, player);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		if (!world.isClient) {
			if (!(Boolean)state.get(NO_DROP)) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof SkullBlockEntity) {
					SkullBlockEntity skullBlockEntity = (SkullBlockEntity)blockEntity;
					ItemStack itemStack = this.getItemStack(world, pos, state);
					if (skullBlockEntity.getSkullType() == 3 && skullBlockEntity.getOwner() != null) {
						itemStack.setNbt(new NbtCompound());
						NbtCompound nbtCompound = new NbtCompound();
						NbtHelper.fromGameProfile(nbtCompound, skullBlockEntity.getOwner());
						itemStack.getNbt().put("SkullOwner", nbtCompound);
					}

					onBlockBreak(world, pos, itemStack);
				}
			}

			super.onBreaking(world, pos, state);
		}
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.SKULL;
	}

	public boolean canDispense(World world, BlockPos pos, ItemStack stack) {
		return stack.getData() == 1 && pos.getY() >= 2 && world.getGlobalDifficulty() != Difficulty.PEACEFUL && !world.isClient
			? this.getWitherSkeletonDispenserPattern().searchAround(world, pos) != null
			: false;
	}

	public void trySpawnEntity(World world, BlockPos pos, SkullBlockEntity blockEntity) {
		if (blockEntity.getSkullType() == 1 && pos.getY() >= 2 && world.getGlobalDifficulty() != Difficulty.PEACEFUL && !world.isClient) {
			BlockPattern blockPattern = this.getWitherSkeletonPattern();
			BlockPattern.Result result = blockPattern.searchAround(world, pos);
			if (result != null) {
				for (int i = 0; i < 3; i++) {
					CachedBlockPosition cachedBlockPosition = result.translate(i, 0, 0);
					world.setBlockState(cachedBlockPosition.getPos(), cachedBlockPosition.getBlockState().with(NO_DROP, true), 2);
				}

				for (int j = 0; j < blockPattern.getWidth(); j++) {
					for (int k = 0; k < blockPattern.getHeight(); k++) {
						CachedBlockPosition cachedBlockPosition2 = result.translate(j, k, 0);
						world.setBlockState(cachedBlockPosition2.getPos(), Blocks.AIR.getDefaultState(), 2);
					}
				}

				BlockPos blockPos = result.translate(1, 0, 0).getPos();
				WitherEntity witherEntity = new WitherEntity(world);
				BlockPos blockPos2 = result.translate(1, 2, 0).getPos();
				witherEntity.refreshPositionAndAngles(
					(double)blockPos2.getX() + 0.5,
					(double)blockPos2.getY() + 0.55,
					(double)blockPos2.getZ() + 0.5,
					result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F,
					0.0F
				);
				witherEntity.bodyYaw = result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
				witherEntity.onSummoned();

				for (PlayerEntity playerEntity : world.getEntitiesInBox(PlayerEntity.class, witherEntity.getBoundingBox().expand(50.0))) {
					playerEntity.incrementStat(AchievementsAndCriterions.SPAWN_WITHER);
				}

				world.spawnEntity(witherEntity);

				for (int l = 0; l < 120; l++) {
					world.addParticle(
						ParticleType.SNOWBALL,
						(double)blockPos.getX() + world.random.nextDouble(),
						(double)(blockPos.getY() - 2) + world.random.nextDouble() * 3.9,
						(double)blockPos.getZ() + world.random.nextDouble(),
						0.0,
						0.0,
						0.0
					);
				}

				for (int m = 0; m < blockPattern.getWidth(); m++) {
					for (int n = 0; n < blockPattern.getHeight(); n++) {
						CachedBlockPosition cachedBlockPosition3 = result.translate(m, n, 0);
						world.updateNeighbors(cachedBlockPosition3.getPos(), Blocks.AIR);
					}
				}
			}
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(FACING, Direction.getById(data & 7)).with(NO_DROP, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getId();
		if ((Boolean)state.get(NO_DROP)) {
			i |= 8;
		}

		return i;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, NO_DROP);
	}

	protected BlockPattern getWitherSkeletonDispenserPattern() {
		if (this.witherSkeletonDispenserPattern == null) {
			this.witherSkeletonDispenserPattern = BlockPatternBuilder.start()
				.aisle("   ", "###", "~#~")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.create(Blocks.SOULSAND)))
				.where('~', CachedBlockPosition.matchesBlockState(BlockMaterialPredicate.create(Material.AIR)))
				.build();
		}

		return this.witherSkeletonDispenserPattern;
	}

	protected BlockPattern getWitherSkeletonPattern() {
		if (this.witherSkeletonPattern == null) {
			this.witherSkeletonPattern = BlockPatternBuilder.start()
				.aisle("^^^", "###", "~#~")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.create(Blocks.SOULSAND)))
				.where('^', IS_WITHER_SKULL_PREDICATE)
				.where('~', CachedBlockPosition.matchesBlockState(BlockMaterialPredicate.create(Material.AIR)))
				.build();
		}

		return this.witherSkeletonPattern;
	}
}
