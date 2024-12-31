package net.minecraft.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.IdList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block implements ItemConvertible {
	protected static final Logger LOGGER = LogManager.getLogger();
	public static final IdList<BlockState> STATE_IDS = new IdList<>();
	private static final Direction[] FACINGS = new Direction[]{
		Direction.field_11039, Direction.field_11034, Direction.field_11043, Direction.field_11035, Direction.field_11033, Direction.field_11036
	};
	private static final LoadingCache<VoxelShape, Boolean> FULL_CUBE_SHAPE_CACHE = CacheBuilder.newBuilder()
		.maximumSize(512L)
		.weakKeys()
		.build(new CacheLoader<VoxelShape, Boolean>() {
			public Boolean load(VoxelShape voxelShape) {
				return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), voxelShape, BooleanBiFunction.NOT_SAME);
			}
		});
	private static final VoxelShape SOLID_MEDIUM_SQUARE_SHAPE = VoxelShapes.combineAndSimplify(
		VoxelShapes.fullCube(), createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST
	);
	private static final VoxelShape SOLID_SMALL_SQUARE_SHAPE = createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);
	protected final int lightLevel;
	protected final float hardness;
	protected final float resistance;
	protected final boolean randomTicks;
	protected final BlockSoundGroup soundGroup;
	protected final Material material;
	protected final MaterialColor materialColor;
	private final float slipperiness;
	private final float velocityMultiplier;
	private final float jumpVelocityMultiplier;
	protected final StateManager<Block, BlockState> stateManager;
	private BlockState defaultState;
	protected final boolean collidable;
	private final boolean dynamicBounds;
	private final boolean opaque;
	@Nullable
	private Identifier dropTableId;
	@Nullable
	private String translationKey;
	@Nullable
	private Item cachedItem;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> FACE_CULL_MAP = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(2048, 0.25F) {
			protected void rehash(int i) {
			}
		};
		object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteLinkedOpenHashMap;
	});

	public static int getRawIdFromState(@Nullable BlockState blockState) {
		if (blockState == null) {
			return 0;
		} else {
			int i = STATE_IDS.getId(blockState);
			return i == -1 ? 0 : i;
		}
	}

	public static BlockState getStateFromRawId(int i) {
		BlockState blockState = STATE_IDS.get(i);
		return blockState == null ? Blocks.field_10124.getDefaultState() : blockState;
	}

	public static Block getBlockFromItem(@Nullable Item item) {
		return item instanceof BlockItem ? ((BlockItem)item).getBlock() : Blocks.field_10124;
	}

	public static BlockState pushEntitiesUpBeforeBlockChange(BlockState blockState, BlockState blockState2, World world, BlockPos blockPos) {
		VoxelShape voxelShape = VoxelShapes.combine(
				blockState.getCollisionShape(world, blockPos), blockState2.getCollisionShape(world, blockPos), BooleanBiFunction.ONLY_SECOND
			)
			.offset((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());

		for (Entity entity : world.getEntities(null, voxelShape.getBoundingBox())) {
			double d = VoxelShapes.calculateMaxOffset(Direction.Axis.field_11052, entity.getBoundingBox().offset(0.0, 1.0, 0.0), Stream.of(voxelShape), -1.0);
			entity.requestTeleport(entity.getX(), entity.getY() + 1.0 + d, entity.getZ());
		}

		return blockState2;
	}

	public static VoxelShape createCuboidShape(double d, double e, double f, double g, double h, double i) {
		return VoxelShapes.cuboid(d / 16.0, e / 16.0, f / 16.0, g / 16.0, h / 16.0, i / 16.0);
	}

	@Deprecated
	public boolean allowsSpawning(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return blockState.isSideSolidFullSquare(blockView, blockPos, Direction.field_11036) && this.lightLevel < 14;
	}

	@Deprecated
	public boolean isAir(BlockState blockState) {
		return false;
	}

	@Deprecated
	public int getLuminance(BlockState blockState) {
		return this.lightLevel;
	}

	@Deprecated
	public Material getMaterial(BlockState blockState) {
		return this.material;
	}

	@Deprecated
	public MaterialColor getMapColor(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.materialColor;
	}

	@Deprecated
	public void updateNeighborStates(BlockState blockState, IWorld iWorld, BlockPos blockPos, int i) {
		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : FACINGS) {
				pooledMutable.set(blockPos).setOffset(direction);
				BlockState blockState2 = iWorld.getBlockState(pooledMutable);
				BlockState blockState3 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), blockState, iWorld, pooledMutable, blockPos);
				replaceBlock(blockState2, blockState3, iWorld, pooledMutable, i);
			}
		}
	}

	public boolean matches(Tag<Block> tag) {
		return tag.contains(this);
	}

	public static BlockState getRenderingState(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		BlockState blockState2 = blockState;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : FACINGS) {
			mutable.set(blockPos).setOffset(direction);
			blockState2 = blockState2.getStateForNeighborUpdate(direction, iWorld.getBlockState(mutable), iWorld, blockPos, mutable);
		}

		return blockState2;
	}

	public static void replaceBlock(BlockState blockState, BlockState blockState2, IWorld iWorld, BlockPos blockPos, int i) {
		if (blockState2 != blockState) {
			if (blockState2.isAir()) {
				if (!iWorld.isClient()) {
					iWorld.breakBlock(blockPos, (i & 32) == 0);
				}
			} else {
				iWorld.setBlockState(blockPos, blockState2, i & -33);
			}
		}
	}

	@Deprecated
	public void method_9517(BlockState blockState, IWorld iWorld, BlockPos blockPos, int i) {
	}

	@Deprecated
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return blockState;
	}

	@Deprecated
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState;
	}

	@Deprecated
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockState;
	}

	public Block(Block.Settings settings) {
		StateManager.Builder<Block, BlockState> builder = new StateManager.Builder<>(this);
		this.appendProperties(builder);
		this.material = settings.material;
		this.materialColor = settings.materialColor;
		this.collidable = settings.collidable;
		this.soundGroup = settings.soundGroup;
		this.lightLevel = settings.luminance;
		this.resistance = settings.resistance;
		this.hardness = settings.hardness;
		this.randomTicks = settings.randomTicks;
		this.slipperiness = settings.slipperiness;
		this.velocityMultiplier = settings.slowDownMultiplier;
		this.jumpVelocityMultiplier = settings.jumpVelocityMultiplier;
		this.dynamicBounds = settings.dynamicBounds;
		this.dropTableId = settings.dropTableId;
		this.opaque = settings.opaque;
		this.stateManager = builder.build(BlockState::new);
		this.setDefaultState(this.stateManager.getDefaultState());
	}

	public static boolean cannotConnect(Block block) {
		return block instanceof LeavesBlock
			|| block == Blocks.field_10499
			|| block == Blocks.field_10147
			|| block == Blocks.field_10009
			|| block == Blocks.field_10545
			|| block == Blocks.field_10261
			|| block.matches(BlockTags.field_21490);
	}

	@Deprecated
	public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getMaterial().blocksLight() && blockState.isFullCube(blockView, blockPos) && !blockState.emitsRedstonePower();
	}

	@Deprecated
	public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.material.blocksMovement() && blockState.isFullCube(blockView, blockPos);
	}

	@Deprecated
	public boolean hasInWallOverlay(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.canSuffocate(blockView, blockPos);
	}

	@Deprecated
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return !blockState.isFullCube(blockView, blockPos);
			case field_48:
				return blockView.getFluidState(blockPos).matches(FluidTags.field_15517);
			case field_51:
				return !blockState.isFullCube(blockView, blockPos);
			default:
				return false;
		}
	}

	@Deprecated
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Deprecated
	public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		return this.material.isReplaceable() && (itemPlacementContext.getStack().isEmpty() || itemPlacementContext.getStack().getItem() != this.asItem());
	}

	@Deprecated
	public boolean canBucketPlace(BlockState blockState, Fluid fluid) {
		return this.material.isReplaceable() || !this.material.isSolid();
	}

	@Deprecated
	public float getHardness(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return this.hardness;
	}

	public boolean hasRandomTicks(BlockState blockState) {
		return this.randomTicks;
	}

	public boolean hasBlockEntity() {
		return this instanceof BlockEntityProvider;
	}

	@Deprecated
	public boolean shouldPostProcess(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Deprecated
	public boolean hasEmissiveLighting(BlockState blockState) {
		return false;
	}

	public static boolean shouldDrawSide(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState2 = blockView.getBlockState(blockPos2);
		if (blockState.isSideInvisible(blockState2, direction)) {
			return false;
		} else if (blockState2.isOpaque()) {
			Block.NeighborGroup neighborGroup = new Block.NeighborGroup(blockState, blockState2, direction);
			Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = (Object2ByteLinkedOpenHashMap<Block.NeighborGroup>)FACE_CULL_MAP.get();
			byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
			if (b != 127) {
				return b != 0;
			} else {
				VoxelShape voxelShape = blockState.getCullingFace(blockView, blockPos, direction);
				VoxelShape voxelShape2 = blockState2.getCullingFace(blockView, blockPos2, direction.getOpposite());
				boolean bl = VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.ONLY_FIRST);
				if (object2ByteLinkedOpenHashMap.size() == 2048) {
					object2ByteLinkedOpenHashMap.removeLastByte();
				}

				object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
				return bl;
			}
		} else {
			return true;
		}
	}

	@Deprecated
	public final boolean isOpaque(BlockState blockState) {
		return this.opaque;
	}

	@Deprecated
	public boolean isSideInvisible(BlockState blockState, BlockState blockState2, Direction direction) {
		return false;
	}

	@Deprecated
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return VoxelShapes.fullCube();
	}

	@Deprecated
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return this.collidable ? blockState.getOutlineShape(blockView, blockPos) : VoxelShapes.empty();
	}

	@Deprecated
	public VoxelShape getCullingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getOutlineShape(blockView, blockPos);
	}

	@Deprecated
	public VoxelShape getRayTraceShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return VoxelShapes.empty();
	}

	public static boolean topCoversMediumSquare(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos);
		return !blockState.matches(BlockTags.field_15503)
			&& !VoxelShapes.matchesAnywhere(
				blockState.getCollisionShape(blockView, blockPos).getFace(Direction.field_11036), SOLID_MEDIUM_SQUARE_SHAPE, BooleanBiFunction.ONLY_SECOND
			);
	}

	public static boolean sideCoversSmallSquare(WorldView worldView, BlockPos blockPos, Direction direction) {
		BlockState blockState = worldView.getBlockState(blockPos);
		return !blockState.matches(BlockTags.field_15503)
			&& !VoxelShapes.matchesAnywhere(
				blockState.getCollisionShape(worldView, blockPos).getFace(direction), SOLID_SMALL_SQUARE_SHAPE, BooleanBiFunction.ONLY_SECOND
			);
	}

	public static boolean isSideSolidFullSquare(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return !blockState.matches(BlockTags.field_15503) && isFaceFullSquare(blockState.getCollisionShape(blockView, blockPos), direction);
	}

	public static boolean isFaceFullSquare(VoxelShape voxelShape, Direction direction) {
		VoxelShape voxelShape2 = voxelShape.getFace(direction);
		return isShapeFullCube(voxelShape2);
	}

	public static boolean isShapeFullCube(VoxelShape voxelShape) {
		return (Boolean)FULL_CUBE_SHAPE_CACHE.getUnchecked(voxelShape);
	}

	@Deprecated
	public final boolean isFullOpaque(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.isOpaque() ? isShapeFullCube(blockState.getCullingShape(blockView, blockPos)) : false;
	}

	public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !isShapeFullCube(blockState.getOutlineShape(blockView, blockPos)) && blockState.getFluidState().isEmpty();
	}

	@Deprecated
	public int getOpacity(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if (blockState.isFullOpaque(blockView, blockPos)) {
			return blockView.getMaxLightLevel();
		} else {
			return blockState.isTranslucent(blockView, blockPos) ? 0 : 1;
		}
	}

	@Deprecated
	public boolean hasSidedTransparency(BlockState blockState) {
		return false;
	}

	@Deprecated
	public void randomTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		this.scheduledTick(blockState, serverWorld, blockPos, random);
	}

	@Deprecated
	public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
	}

	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
	}

	public void onBroken(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
	}

	@Deprecated
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		DebugRendererInfoManager.sendNeighborUpdate(world, blockPos);
	}

	public int getTickRate(WorldView worldView) {
		return 10;
	}

	@Nullable
	@Deprecated
	public NameableContainerFactory createContainerFactory(BlockState blockState, World world, BlockPos blockPos) {
		return null;
	}

	@Deprecated
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
	}

	@Deprecated
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (this.hasBlockEntity() && blockState.getBlock() != blockState2.getBlock()) {
			world.removeBlockEntity(blockPos);
		}
	}

	@Deprecated
	public float calcBlockBreakingDelta(BlockState blockState, PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		float f = blockState.getHardness(blockView, blockPos);
		if (f == -1.0F) {
			return 0.0F;
		} else {
			int i = playerEntity.isUsingEffectiveTool(blockState) ? 30 : 100;
			return playerEntity.getBlockBreakingSpeed(blockState) / f / (float)i;
		}
	}

	@Deprecated
	public void onStacksDropped(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
	}

	public Identifier getDropTableId() {
		if (this.dropTableId == null) {
			Identifier identifier = Registry.field_11146.getId(this);
			this.dropTableId = new Identifier(identifier.getNamespace(), "blocks/" + identifier.getPath());
		}

		return this.dropTableId;
	}

	@Deprecated
	public List<ItemStack> getDroppedStacks(BlockState blockState, LootContext.Builder builder) {
		Identifier identifier = this.getDropTableId();
		if (identifier == LootTables.EMPTY) {
			return Collections.emptyList();
		} else {
			LootContext lootContext = builder.put(LootContextParameters.field_1224, blockState).build(LootContextTypes.field_1172);
			ServerWorld serverWorld = lootContext.getWorld();
			LootTable lootTable = serverWorld.getServer().getLootManager().getSupplier(identifier);
			return lootTable.getDrops(lootContext);
		}
	}

	public static List<ItemStack> getDroppedStacks(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
		LootContext.Builder builder = new LootContext.Builder(serverWorld)
			.setRandom(serverWorld.random)
			.put(LootContextParameters.field_1232, blockPos)
			.put(LootContextParameters.field_1229, ItemStack.EMPTY)
			.putNullable(LootContextParameters.field_1228, blockEntity);
		return blockState.getDroppedStacks(builder);
	}

	public static List<ItemStack> getDroppedStacks(
		BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack itemStack
	) {
		LootContext.Builder builder = new LootContext.Builder(serverWorld)
			.setRandom(serverWorld.random)
			.put(LootContextParameters.field_1232, blockPos)
			.put(LootContextParameters.field_1229, itemStack)
			.putNullable(LootContextParameters.field_1226, entity)
			.putNullable(LootContextParameters.field_1228, blockEntity);
		return blockState.getDroppedStacks(builder);
	}

	public static void dropStacks(BlockState blockState, World world, BlockPos blockPos) {
		if (world instanceof ServerWorld) {
			getDroppedStacks(blockState, (ServerWorld)world, blockPos, null).forEach(itemStack -> dropStack(world, blockPos, itemStack));
		}

		blockState.onStacksDropped(world, blockPos, ItemStack.EMPTY);
	}

	public static void dropStacks(BlockState blockState, World world, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
		if (world instanceof ServerWorld) {
			getDroppedStacks(blockState, (ServerWorld)world, blockPos, blockEntity).forEach(itemStack -> dropStack(world, blockPos, itemStack));
		}

		blockState.onStacksDropped(world, blockPos, ItemStack.EMPTY);
	}

	public static void dropStacks(BlockState blockState, World world, BlockPos blockPos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack itemStack) {
		if (world instanceof ServerWorld) {
			getDroppedStacks(blockState, (ServerWorld)world, blockPos, blockEntity, entity, itemStack).forEach(itemStackx -> dropStack(world, blockPos, itemStackx));
		}

		blockState.onStacksDropped(world, blockPos, itemStack);
	}

	public static void dropStack(World world, BlockPos blockPos, ItemStack itemStack) {
		if (!world.isClient && !itemStack.isEmpty() && world.getGameRules().getBoolean(GameRules.field_19392)) {
			float f = 0.5F;
			double d = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			double e = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			double g = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			ItemEntity itemEntity = new ItemEntity(world, (double)blockPos.getX() + d, (double)blockPos.getY() + e, (double)blockPos.getZ() + g, itemStack);
			itemEntity.setToDefaultPickupDelay();
			world.spawnEntity(itemEntity);
		}
	}

	protected void dropExperience(World world, BlockPos blockPos, int i) {
		if (!world.isClient && world.getGameRules().getBoolean(GameRules.field_19392)) {
			while (i > 0) {
				int j = ExperienceOrbEntity.roundToOrbSize(i);
				i -= j;
				world.spawnEntity(new ExperienceOrbEntity(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, j));
			}
		}
	}

	public float getBlastResistance() {
		return this.resistance;
	}

	public void onDestroyedByExplosion(World world, BlockPos blockPos, Explosion explosion) {
	}

	@Deprecated
	public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
		return true;
	}

	@Deprecated
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		return ActionResult.field_5811;
	}

	public void onSteppedOn(World world, BlockPos blockPos, Entity entity) {
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState();
	}

	@Deprecated
	public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
	}

	@Deprecated
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return 0;
	}

	@Deprecated
	public boolean emitsRedstonePower(BlockState blockState) {
		return false;
	}

	@Deprecated
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
	}

	@Deprecated
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return 0;
	}

	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		playerEntity.incrementStat(Stats.field_15427.getOrCreateStat(this));
		playerEntity.addExhaustion(0.005F);
		dropStacks(blockState, world, blockPos, blockEntity, playerEntity, itemStack);
	}

	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
	}

	public boolean canMobSpawnInside() {
		return !this.material.isSolid() && !this.material.isLiquid();
	}

	public Text getName() {
		return new TranslatableText(this.getTranslationKey());
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("block", Registry.field_11146.getId(this));
		}

		return this.translationKey;
	}

	@Deprecated
	public boolean onBlockAction(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
		return false;
	}

	@Deprecated
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return this.material.getPistonBehavior();
	}

	@Deprecated
	public float getAmbientOcclusionLightLevel(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.isFullCube(blockView, blockPos) ? 0.2F : 1.0F;
	}

	public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
		entity.handleFallDamage(f, 1.0F);
	}

	public void onEntityLand(BlockView blockView, Entity entity) {
		entity.setVelocity(entity.getVelocity().multiply(1.0, 0.0, 1.0));
	}

	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this);
	}

	public void addStacksForDisplay(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		defaultedList.add(new ItemStack(this));
	}

	@Deprecated
	public FluidState getFluidState(BlockState blockState) {
		return Fluids.field_15906.getDefaultState();
	}

	public float getSlipperiness() {
		return this.slipperiness;
	}

	public float getVelocityMultiplier() {
		return this.velocityMultiplier;
	}

	public float getJumpVelocityMultiplier() {
		return this.jumpVelocityMultiplier;
	}

	@Deprecated
	public long getRenderingSeed(BlockState blockState, BlockPos blockPos) {
		return MathHelper.hashCode(blockPos);
	}

	public void onProjectileHit(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
	}

	public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		world.playLevelEvent(playerEntity, 2001, blockPos, getRawIdFromState(blockState));
	}

	public void rainTick(World world, BlockPos blockPos) {
	}

	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return true;
	}

	@Deprecated
	public boolean hasComparatorOutput(BlockState blockState) {
		return false;
	}

	@Deprecated
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return 0;
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
	}

	public StateManager<Block, BlockState> getStateManager() {
		return this.stateManager;
	}

	protected final void setDefaultState(BlockState blockState) {
		this.defaultState = blockState;
	}

	public final BlockState getDefaultState() {
		return this.defaultState;
	}

	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.field_10656;
	}

	@Deprecated
	public Vec3d getOffsetPos(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Block.OffsetType offsetType = this.getOffsetType();
		if (offsetType == Block.OffsetType.field_10656) {
			return Vec3d.ZERO;
		} else {
			long l = MathHelper.hashCode(blockPos.getX(), 0, blockPos.getZ());
			return new Vec3d(
				((double)((float)(l & 15L) / 15.0F) - 0.5) * 0.5,
				offsetType == Block.OffsetType.field_10655 ? ((double)((float)(l >> 4 & 15L) / 15.0F) - 1.0) * 0.2 : 0.0,
				((double)((float)(l >> 8 & 15L) / 15.0F) - 0.5) * 0.5
			);
		}
	}

	public BlockSoundGroup getSoundGroup(BlockState blockState) {
		return this.soundGroup;
	}

	@Override
	public Item asItem() {
		if (this.cachedItem == null) {
			this.cachedItem = Item.fromBlock(this);
		}

		return this.cachedItem;
	}

	public boolean hasDynamicBounds() {
		return this.dynamicBounds;
	}

	public String toString() {
		return "Block{" + Registry.field_11146.getId(this) + "}";
	}

	public void buildTooltip(ItemStack itemStack, @Nullable BlockView blockView, List<Text> list, TooltipContext tooltipContext) {
	}

	public static final class NeighborGroup {
		private final BlockState self;
		private final BlockState other;
		private final Direction facing;

		public NeighborGroup(BlockState blockState, BlockState blockState2, Direction direction) {
			this.self = blockState;
			this.other = blockState2;
			this.facing = direction;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof Block.NeighborGroup)) {
				return false;
			} else {
				Block.NeighborGroup neighborGroup = (Block.NeighborGroup)object;
				return this.self == neighborGroup.self && this.other == neighborGroup.other && this.facing == neighborGroup.facing;
			}
		}

		public int hashCode() {
			int i = this.self.hashCode();
			i = 31 * i + this.other.hashCode();
			return 31 * i + this.facing.hashCode();
		}
	}

	public static enum OffsetType {
		field_10656,
		field_10657,
		field_10655;
	}

	public static class Settings {
		private Material material;
		private MaterialColor materialColor;
		private boolean collidable = true;
		private BlockSoundGroup soundGroup = BlockSoundGroup.STONE;
		private int luminance;
		private float resistance;
		private float hardness;
		private boolean randomTicks;
		private float slipperiness = 0.6F;
		private float slowDownMultiplier = 1.0F;
		private float jumpVelocityMultiplier = 1.0F;
		private Identifier dropTableId;
		private boolean opaque = true;
		private boolean dynamicBounds;

		private Settings(Material material, MaterialColor materialColor) {
			this.material = material;
			this.materialColor = materialColor;
		}

		public static Block.Settings of(Material material) {
			return of(material, material.getColor());
		}

		public static Block.Settings of(Material material, DyeColor dyeColor) {
			return of(material, dyeColor.getMaterialColor());
		}

		public static Block.Settings of(Material material, MaterialColor materialColor) {
			return new Block.Settings(material, materialColor);
		}

		public static Block.Settings copy(Block block) {
			Block.Settings settings = new Block.Settings(block.material, block.materialColor);
			settings.material = block.material;
			settings.hardness = block.hardness;
			settings.resistance = block.resistance;
			settings.collidable = block.collidable;
			settings.randomTicks = block.randomTicks;
			settings.luminance = block.lightLevel;
			settings.materialColor = block.materialColor;
			settings.soundGroup = block.soundGroup;
			settings.slipperiness = block.getSlipperiness();
			settings.slowDownMultiplier = block.getVelocityMultiplier();
			settings.dynamicBounds = block.dynamicBounds;
			settings.opaque = block.opaque;
			return settings;
		}

		public Block.Settings noCollision() {
			this.collidable = false;
			this.opaque = false;
			return this;
		}

		public Block.Settings nonOpaque() {
			this.opaque = false;
			return this;
		}

		public Block.Settings slipperiness(float f) {
			this.slipperiness = f;
			return this;
		}

		public Block.Settings velocityMultiplier(float f) {
			this.slowDownMultiplier = f;
			return this;
		}

		public Block.Settings jumpVelocityMultiplier(float f) {
			this.jumpVelocityMultiplier = f;
			return this;
		}

		protected Block.Settings sounds(BlockSoundGroup blockSoundGroup) {
			this.soundGroup = blockSoundGroup;
			return this;
		}

		protected Block.Settings lightLevel(int i) {
			this.luminance = i;
			return this;
		}

		public Block.Settings strength(float f, float g) {
			this.hardness = f;
			this.resistance = Math.max(0.0F, g);
			return this;
		}

		protected Block.Settings breakInstantly() {
			return this.strength(0.0F);
		}

		protected Block.Settings strength(float f) {
			this.strength(f, f);
			return this;
		}

		protected Block.Settings ticksRandomly() {
			this.randomTicks = true;
			return this;
		}

		protected Block.Settings hasDynamicBounds() {
			this.dynamicBounds = true;
			return this;
		}

		protected Block.Settings dropsNothing() {
			this.dropTableId = LootTables.EMPTY;
			return this;
		}

		public Block.Settings dropsLike(Block block) {
			this.dropTableId = block.getDropTableId();
			return this;
		}
	}
}
