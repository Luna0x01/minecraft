package net.minecraft.block;

import com.google.common.collect.UnmodifiableIterator;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_3600;
import net.minecraft.class_3693;
import net.minecraft.class_3694;
import net.minecraft.class_3697;
import net.minecraft.class_3698;
import net.minecraft.class_3706;
import net.minecraft.class_3708;
import net.minecraft.class_3709;
import net.minecraft.class_3710;
import net.minecraft.class_3714;
import net.minecraft.class_3715;
import net.minecraft.class_3717;
import net.minecraft.class_3719;
import net.minecraft.class_3720;
import net.minecraft.class_3721;
import net.minecraft.class_3724;
import net.minecraft.class_3725;
import net.minecraft.class_3729;
import net.minecraft.class_3730;
import net.minecraft.class_3732;
import net.minecraft.class_3733;
import net.minecraft.class_3734;
import net.minecraft.class_3735;
import net.minecraft.class_3736;
import net.minecraft.class_3737;
import net.minecraft.class_3738;
import net.minecraft.class_3749;
import net.minecraft.class_3750;
import net.minecraft.class_3751;
import net.minecraft.class_3752;
import net.minecraft.class_3753;
import net.minecraft.class_3754;
import net.minecraft.class_3756;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.TooltipContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block implements Itemable {
	protected static final Logger LOGGER = LogManager.getLogger();
	public static final IdList<BlockState> BLOCK_STATES = new IdList<>();
	private static final Direction[] FACINGS = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
	protected final int lightLevel;
	protected final float hardness;
	protected final float blastResistance;
	protected final boolean randomTicks;
	protected final BlockSoundGroup blockSoundGroup;
	protected final Material material;
	protected final MaterialColor materialColor;
	private final float slipperiness;
	protected final StateManager<Block, BlockState> stateManager;
	private BlockState defaultState;
	protected final boolean collidable;
	private final boolean dynamicBounds;
	@Nullable
	private String translationKey;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> FACE_CULL_MAP = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(200) {
			protected void rehash(int i) {
			}
		};
		object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteLinkedOpenHashMap;
	});

	public static int getRawIdFromState(@Nullable BlockState state) {
		if (state == null) {
			return 0;
		} else {
			int i = BLOCK_STATES.getId(state);
			return i == -1 ? 0 : i;
		}
	}

	public static BlockState getStateByRawId(int rawId) {
		BlockState blockState = BLOCK_STATES.fromId(rawId);
		return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
	}

	public static Block getBlockFromItem(@Nullable Item item) {
		return item instanceof BlockItem ? ((BlockItem)item).getBlock() : Blocks.AIR;
	}

	public static BlockState pushEntitiesUpBeforeBlockChange(BlockState state, BlockState state2, World world, BlockPos pos) {
		VoxelShape voxelShape = VoxelShapes.combine(state.getCollisionShape(world, pos), state2.getCollisionShape(world, pos), BooleanBiFunction.ONLY_SECOND)
			.offset((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());

		for (Entity entity : world.getEntities(null, voxelShape.getBoundingBox())) {
			double d = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entity.getBoundingBox().offset(0.0, 1.0, 0.0), Stream.of(voxelShape), -1.0);
			entity.refreshPositionAfterTeleport(entity.x, entity.y + 1.0 + d, entity.z);
		}

		return state2;
	}

	public static VoxelShape createCuboidShape(double d, double e, double f, double g, double h, double i) {
		return VoxelShapes.cuboid(d / 16.0, e / 16.0, f / 16.0, g / 16.0, h / 16.0, i / 16.0);
	}

	@Deprecated
	public boolean method_13315(BlockState blockState, Entity entity) {
		return true;
	}

	@Deprecated
	public boolean isAir(BlockState state) {
		return false;
	}

	@Deprecated
	public int getLuminance(BlockState state) {
		return this.lightLevel;
	}

	@Deprecated
	public Material getMaterial(BlockState state) {
		return this.material;
	}

	@Deprecated
	public MaterialColor getMaterialColor(BlockState state, BlockView view, BlockPos pos) {
		return this.materialColor;
	}

	@Deprecated
	public void method_16569(BlockState blockState, IWorld iWorld, BlockPos blockPos, int i) {
		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			for (Direction direction : FACINGS) {
				pooled.set(blockPos).move(direction);
				BlockState blockState2 = iWorld.getBlockState(pooled);
				BlockState blockState3 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), blockState, iWorld, pooled, blockPos);
				method_16572(blockState2, blockState3, iWorld, pooled, i);
			}
		}
	}

	public boolean isIn(Tag<Block> tag) {
		return tag.contains(this);
	}

	public static BlockState method_16583(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		BlockState blockState2 = blockState;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : FACINGS) {
			mutable.set(blockPos).move(direction);
			blockState2 = blockState2.getStateForNeighborUpdate(direction, iWorld.getBlockState(mutable), iWorld, blockPos, mutable);
		}

		return blockState2;
	}

	public static void method_16572(BlockState blockState, BlockState blockState2, IWorld iWorld, BlockPos blockPos, int i) {
		if (blockState2 != blockState) {
			if (blockState2.isAir()) {
				if (!iWorld.method_16390()) {
					iWorld.method_8535(blockPos, (i & 32) == 0);
				}
			} else {
				iWorld.setBlockState(blockPos, blockState2, i & -33);
			}
		}
	}

	@Deprecated
	public void method_16584(BlockState blockState, IWorld iWorld, BlockPos blockPos, int i) {
	}

	@Deprecated
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return state;
	}

	@Deprecated
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state;
	}

	@Deprecated
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state;
	}

	public Block(Block.Builder builder) {
		StateManager.Builder<Block, BlockState> builder2 = new StateManager.Builder<>(this);
		this.appendProperties(builder2);
		this.stateManager = builder2.build(class_3756::new);
		this.setDefaultState(this.stateManager.method_16923());
		this.material = builder.material;
		this.materialColor = builder.materialColor;
		this.collidable = builder.collidable;
		this.blockSoundGroup = builder.blockSoundGroup;
		this.lightLevel = builder.lightLevel;
		this.blastResistance = builder.blastResistance;
		this.hardness = builder.strength;
		this.randomTicks = builder.randomTicks;
		this.slipperiness = builder.slipperiness;
		this.dynamicBounds = builder.dynamicBounds;
	}

	protected static boolean method_14308(Block block) {
		return block instanceof ShulkerBoxBlock
			|| block instanceof LeavesBlock
			|| block.isIn(BlockTags.TRAPDOORS)
			|| block instanceof StainedGlassBlock
			|| block == Blocks.BEACON
			|| block == Blocks.CAULDRON
			|| block == Blocks.GLASS
			|| block == Blocks.GLOWSTONE
			|| block == Blocks.ICE
			|| block == Blocks.SEA_LANTERN
			|| block == Blocks.CONDUIT;
	}

	public static boolean method_14309(Block block) {
		return method_14308(block) || block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD;
	}

	@Deprecated
	public boolean method_11575(BlockState state) {
		return state.getMaterial().blocksMovement() && state.method_16897();
	}

	@Deprecated
	public boolean method_11576(BlockState state) {
		return state.getMaterial().isOpaque() && state.method_16897() && !state.emitsRedstonePower();
	}

	@Deprecated
	public boolean method_13703(BlockState state) {
		return this.material.blocksMovement() && state.method_16897();
	}

	@Deprecated
	public boolean method_11562(BlockState state) {
		return true;
	}

	@Deprecated
	public boolean method_11568(BlockState state) {
		return state.getMaterial().isOpaque() && state.method_16897();
	}

	@Deprecated
	public boolean method_13704(BlockState state) {
		return false;
	}

	@Deprecated
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		switch (environment) {
			case LAND:
				return !isShapeFullCube(this.getCollisionShape(state, world, pos));
			case WATER:
				return world.getFluidState(pos).matches(FluidTags.WATER);
			case AIR:
				return !isShapeFullCube(this.getCollisionShape(state, world, pos));
			default:
				return false;
		}
	}

	@Deprecated
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Deprecated
	public boolean canReplace(BlockState state, ItemPlacementContext itemPlacementContext) {
		return this.material.isReplaceable() && itemPlacementContext.getItemStack().getItem() != this.getItem();
	}

	@Deprecated
	public float getHardness(BlockState state, BlockView world, BlockPos pos) {
		return this.hardness;
	}

	public boolean hasRandomTicks(BlockState state) {
		return this.randomTicks;
	}

	public boolean hasBlockEntity() {
		return this instanceof BlockEntityProvider;
	}

	@Deprecated
	public boolean method_16592(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Deprecated
	public int method_11564(BlockState blockState, class_3600 arg, BlockPos blockPos) {
		int i = arg.method_8578(blockPos, blockState.getLuminance());
		if (i == 0 && blockState.getBlock() instanceof SlabBlock) {
			blockPos = blockPos.down();
			blockState = arg.getBlockState(blockPos);
			return arg.method_8578(blockPos, blockState.getLuminance());
		} else {
			return i;
		}
	}

	public static boolean method_16586(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState2 = blockView.getBlockState(blockPos2);
		if (blockState.method_16881(blockState2, direction)) {
			return false;
		} else if (blockState2.isFullBoundsCubeForCulling()) {
			Block.NeighborGroup neighborGroup = new Block.NeighborGroup(blockState, blockState2, direction);
			Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = (Object2ByteLinkedOpenHashMap<Block.NeighborGroup>)FACE_CULL_MAP.get();
			byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
			if (b != 127) {
				return b != 0;
			} else {
				VoxelShape voxelShape = blockState.method_16902(blockView, blockPos);
				VoxelShape voxelShape2 = blockState2.method_16902(blockView, blockPos2);
				boolean bl = !VoxelShapes.method_18056(voxelShape, voxelShape2, direction);
				if (object2ByteLinkedOpenHashMap.size() == 200) {
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
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return this.collidable && blockState.getBlock().getRenderLayerType() == RenderLayer.SOLID;
	}

	@Deprecated
	public boolean method_16573(BlockState blockState, BlockState blockState2, Direction direction) {
		return false;
	}

	@Deprecated
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.SOLID;
	}

	@Deprecated
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.matchesAnywhere();
	}

	@Deprecated
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos) {
		return this.collidable ? state.getOutlineShape(world, pos) : VoxelShapes.empty();
	}

	@Deprecated
	public VoxelShape method_16593(BlockState state, BlockView world, BlockPos pos) {
		return state.getOutlineShape(world, pos);
	}

	@Deprecated
	public VoxelShape getRayTraceShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	public static boolean isFaceFullSquare(VoxelShape voxelShape, Direction direction) {
		VoxelShape voxelShape2 = voxelShape.getFace(direction);
		return isShapeFullCube(voxelShape2);
	}

	public static boolean isShapeFullCube(VoxelShape shape) {
		return !VoxelShapes.matchesAnywhere(VoxelShapes.matchesAnywhere(), shape, BooleanBiFunction.ONLY_FIRST);
	}

	@Deprecated
	public final boolean method_16596(BlockState state, BlockView world, BlockPos pos) {
		boolean bl = state.isFullBoundsCubeForCulling();
		VoxelShape voxelShape = bl ? state.method_16902(world, pos) : VoxelShapes.empty();
		return isShapeFullCube(voxelShape);
	}

	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
		return !isShapeFullCube(state.getOutlineShape(world, pos)) && state.getFluidState().isEmpty();
	}

	@Deprecated
	public int getLightSubtracted(BlockState state, BlockView world, BlockPos pos) {
		if (state.isFullOpaque(world, pos)) {
			return world.getMaxLightLevel();
		} else {
			return state.isTranslucent(world, pos) ? 0 : 1;
		}
	}

	@Deprecated
	public final boolean method_16599(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !blockState.isFullOpaque(blockView, blockPos) && blockState.method_16885(blockView, blockPos) == blockView.getMaxLightLevel();
	}

	public boolean method_400(BlockState blockState) {
		return this.hasCollision();
	}

	public boolean hasCollision() {
		return true;
	}

	@Deprecated
	public void method_16582(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.scheduledTick(blockState, world, blockPos, random);
	}

	@Deprecated
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
	}

	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
	}

	public void method_8674(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
	}

	@Deprecated
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
	}

	public int getTickDelay(RenderBlockView world) {
		return 10;
	}

	@Deprecated
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
	}

	@Deprecated
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
	}

	public int getDropCount(BlockState state, Random random) {
		return 1;
	}

	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return this;
	}

	@Deprecated
	public float method_16566(BlockState blockState, PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		float f = blockState.getHardness(blockView, blockPos);
		if (f == -1.0F) {
			return 0.0F;
		} else {
			int i = playerEntity.method_13265(blockState) ? 30 : 100;
			return playerEntity.method_13261(blockState) / f / (float)i;
		}
	}

	@Deprecated
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		if (!world.isClient) {
			int j = this.method_397(blockState, i, world, blockPos, world.random);

			for (int k = 0; k < j; k++) {
				if (!(f < 1.0F) || !(world.random.nextFloat() > f)) {
					Item item = this.getDroppedItem(blockState, world, blockPos, i).getItem();
					if (item != Items.AIR) {
						onBlockBreak(world, blockPos, new ItemStack(item));
					}
				}
			}
		}
	}

	public static void onBlockBreak(World world, BlockPos pos, ItemStack item) {
		if (!world.isClient && !item.isEmpty() && world.getGameRules().getBoolean("doTileDrops")) {
			float f = 0.5F;
			double d = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			double e = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			double g = (double)(world.random.nextFloat() * 0.5F) + 0.25;
			ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, item);
			itemEntity.setToDefaultPickupDelay();
			world.method_3686(itemEntity);
		}
	}

	protected void dropExperience(World world, BlockPos pos, int size) {
		if (!world.isClient && world.getGameRules().getBoolean("doTileDrops")) {
			while (size > 0) {
				int i = ExperienceOrbEntity.roundToOrbSize(size);
				size -= i;
				world.method_3686(new ExperienceOrbEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, i));
			}
		}
	}

	public float getBlastResistance() {
		return this.blastResistance;
	}

	@Nullable
	public static BlockHitResult method_414(BlockState blockState, World world, BlockPos blockPos, Vec3d vec3d, Vec3d vec3d2) {
		BlockHitResult blockHitResult = blockState.getOutlineShape(world, blockPos).rayTrace(vec3d, vec3d2, blockPos);
		if (blockHitResult != null) {
			BlockHitResult blockHitResult2 = blockState.getRayTraceShape(world, blockPos).rayTrace(vec3d, vec3d2, blockPos);
			if (blockHitResult2 != null && blockHitResult2.pos.subtract(vec3d).squaredLength() < blockHitResult.pos.subtract(vec3d).squaredLength()) {
				blockHitResult.direction = blockHitResult2.direction;
			}
		}

		return blockHitResult;
	}

	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
	}

	public RenderLayer getRenderLayerType() {
		return RenderLayer.SOLID;
	}

	@Deprecated
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		return true;
	}

	@Deprecated
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		return false;
	}

	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
	}

	@Nullable
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState();
	}

	@Deprecated
	public void method_420(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
	}

	@Deprecated
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return 0;
	}

	@Deprecated
	public boolean emitsRedstonePower(BlockState state) {
		return false;
	}

	@Deprecated
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
	}

	@Deprecated
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return 0;
	}

	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		player.method_15932(Stats.MINED.method_21429(this));
		player.addExhaustion(0.005F);
		if (this.requiresSilkTouch() && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) > 0) {
			ItemStack itemStack = this.createStackFromBlock(state);
			onBlockBreak(world, pos, itemStack);
		} else {
			int i = EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack);
			state.method_16867(world, pos, i);
		}
	}

	protected boolean requiresSilkTouch() {
		return this.getDefaultState().method_16897() && !this.hasBlockEntity();
	}

	protected ItemStack createStackFromBlock(BlockState state) {
		return new ItemStack(this);
	}

	public int method_397(BlockState blockState, int i, World world, BlockPos blockPos, Random random) {
		return this.getDropCount(blockState, random);
	}

	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
	}

	public boolean canMobSpawnInside() {
		return !this.material.isSolid() && !this.material.isFluid();
	}

	public Text method_16600() {
		return new TranslatableText(this.getTranslationKey());
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("block", Registry.BLOCK.getId(this));
		}

		return this.translationKey;
	}

	@Deprecated
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		return false;
	}

	@Deprecated
	public PistonBehavior getPistonBehavior(BlockState state) {
		return this.material.getPistonBehavior();
	}

	@Deprecated
	public float getAmbientOcclusionLightLevel(BlockState state) {
		return state.method_16905() ? 0.2F : 1.0F;
	}

	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		entity.handleFallDamage(distance, 1.0F);
	}

	public void onEntityLand(BlockView world, Entity entity) {
		entity.velocityY = 0.0;
	}

	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(this);
	}

	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> stacks) {
		stacks.add(new ItemStack(this));
	}

	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return Fluids.EMPTY.getDefaultState();
	}

	public float getSlipperiness() {
		return this.slipperiness;
	}

	@Deprecated
	public long getRenderingSeed(BlockState state, BlockPos pos) {
		return MathHelper.hashCode(pos);
	}

	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		world.syncWorldEvent(player, 2001, pos, getRawIdFromState(state));
	}

	public void onRainTick(World world, BlockPos pos) {
	}

	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return true;
	}

	@Deprecated
	public boolean method_11577(BlockState state) {
		return false;
	}

	@Deprecated
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return 0;
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
	}

	public StateManager<Block, BlockState> getStateManager() {
		return this.stateManager;
	}

	protected final void setDefaultState(BlockState state) {
		this.defaultState = state;
	}

	public final BlockState getDefaultState() {
		return this.defaultState;
	}

	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.NONE;
	}

	@Deprecated
	public Vec3d getOffsetPos(BlockState state, BlockView world, BlockPos pos) {
		Block.OffsetType offsetType = this.getOffsetType();
		if (offsetType == Block.OffsetType.NONE) {
			return Vec3d.ZERO;
		} else {
			long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
			return new Vec3d(
				((double)((float)(l & 15L) / 15.0F) - 0.5) * 0.5,
				offsetType == Block.OffsetType.XYZ ? ((double)((float)(l >> 4 & 15L) / 15.0F) - 1.0) * 0.2 : 0.0,
				((double)((float)(l >> 8 & 15L) / 15.0F) - 0.5) * 0.5
			);
		}
	}

	public BlockSoundGroup getSoundGroup() {
		return this.blockSoundGroup;
	}

	@Override
	public Item getItem() {
		return Item.fromBlock(this);
	}

	public boolean hasStats() {
		return this.dynamicBounds;
	}

	public String toString() {
		return "Block{" + Registry.BLOCK.getId(this) + "}";
	}

	public void method_16564(ItemStack itemStack, @Nullable BlockView blockView, List<Text> list, TooltipContext tooltipContext) {
	}

	public static boolean method_16585(Block block) {
		return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
	}

	public static boolean method_16588(Block block) {
		return block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL;
	}

	public static void setup() {
		Block block = new AirBlock(Block.Builder.setMaterial(Material.AIR).setNotCollidable());
		add(Registry.BLOCK.getDefaultId(), block);
		Block block2 = new StoneBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.STONE).setStrengthAndResistance(1.5F, 6.0F));
		register("stone", block2);
		register("granite", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.DIRT).setStrengthAndResistance(1.5F, 6.0F)));
		register("polished_granite", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.DIRT).setStrengthAndResistance(1.5F, 6.0F)));
		register("diorite", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19528).setStrengthAndResistance(1.5F, 6.0F)));
		register("polished_diorite", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19528).setStrengthAndResistance(1.5F, 6.0F)));
		register("andesite", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.STONE).setStrengthAndResistance(1.5F, 6.0F)));
		register("polished_andesite", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.STONE).setStrengthAndResistance(1.5F, 6.0F)));
		register(
			"grass_block",
			new GrassBlock(Block.Builder.setMaterial(Material.GRASS).setRandomTicks().setDurability(0.6F).setBlockSoundGroup(BlockSoundGroup.field_12761))
		);
		register(
			"dirt",
			new Block(Block.Builder.setMaterialAndMapColor(Material.DIRT, MaterialColor.DIRT).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12760))
		);
		register(
			"coarse_dirt",
			new Block(Block.Builder.setMaterialAndMapColor(Material.DIRT, MaterialColor.DIRT).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12760))
		);
		register(
			"podzol",
			new class_3725(
				Block.Builder.setMaterialAndMapColor(Material.DIRT, MaterialColor.field_19511).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12760)
			)
		);
		Block block3 = new Block(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(2.0F, 6.0F));
		register("cobblestone", block3);
		Block block4 = new Block(
			Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.WOOD).setStrengthAndResistance(2.0F, 3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
		);
		Block block5 = new Block(
			Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.field_19511)
				.setStrengthAndResistance(2.0F, 3.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
		);
		Block block6 = new Block(
			Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.SAND).setStrengthAndResistance(2.0F, 3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
		);
		Block block7 = new Block(
			Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.DIRT).setStrengthAndResistance(2.0F, 3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
		);
		Block block8 = new Block(
			Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.ORANGE)
				.setStrengthAndResistance(2.0F, 3.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
		);
		Block block9 = new Block(
			Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.BROWN)
				.setStrengthAndResistance(2.0F, 3.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
		);
		register("oak_planks", block4);
		register("spruce_planks", block5);
		register("birch_planks", block6);
		register("jungle_planks", block7);
		register("acacia_planks", block8);
		register("dark_oak_planks", block9);
		Block block10 = new SaplingBlock(
			new class_3753(),
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block11 = new SaplingBlock(
			new class_3754(),
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block12 = new SaplingBlock(
			new class_3750(),
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block13 = new SaplingBlock(
			new class_3752(),
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block14 = new SaplingBlock(
			new class_3749(),
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block15 = new SaplingBlock(
			new class_3751(),
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		register("oak_sapling", block10);
		register("spruce_sapling", block11);
		register("birch_sapling", block12);
		register("jungle_sapling", block13);
		register("acacia_sapling", block14);
		register("dark_oak_sapling", block15);
		register("bedrock", new BedrockBlock(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(-1.0F, 3600000.0F)));
		register("water", new class_3710(Fluids.WATER, Block.Builder.setMaterial(Material.WATER).setNotCollidable().setDurability(100.0F)));
		register(
			"lava", new class_3710(Fluids.LAVA, Block.Builder.setMaterial(Material.LAVA).setNotCollidable().setRandomTicks().setDurability(100.0F).setLightLevel(15))
		);
		register(
			"sand",
			new SandBlock(
				14406560, Block.Builder.setMaterialAndMapColor(Material.SAND, MaterialColor.SAND).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"red_sand",
			new SandBlock(
				11098145, Block.Builder.setMaterialAndMapColor(Material.SAND, MaterialColor.ORANGE).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"gravel",
			new GravelBlock(Block.Builder.setMaterialAndMapColor(Material.SAND, MaterialColor.STONE).setDurability(0.6F).setBlockSoundGroup(BlockSoundGroup.field_12760))
		);
		register("gold_ore", new OreBlock(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(3.0F, 3.0F)));
		register("iron_ore", new OreBlock(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(3.0F, 3.0F)));
		register("coal_ore", new OreBlock(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(3.0F, 3.0F)));
		register(
			"oak_log",
			new LogBlock(
				MaterialColor.WOOD,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.field_19511).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"spruce_log",
			new LogBlock(
				MaterialColor.field_19511,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.BROWN).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"birch_log",
			new LogBlock(
				MaterialColor.SAND,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.field_19528).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"jungle_log",
			new LogBlock(
				MaterialColor.DIRT,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.field_19511).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"acacia_log",
			new LogBlock(
				MaterialColor.ORANGE,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.STONE).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"dark_oak_log",
			new LogBlock(
				MaterialColor.BROWN,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.BROWN).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"stripped_spruce_log",
			new LogBlock(
				MaterialColor.field_19511,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.field_19511).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"stripped_birch_log",
			new LogBlock(
				MaterialColor.SAND,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.SAND).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"stripped_jungle_log",
			new LogBlock(
				MaterialColor.DIRT,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.DIRT).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"stripped_acacia_log",
			new LogBlock(
				MaterialColor.ORANGE,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.ORANGE).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"stripped_dark_oak_log",
			new LogBlock(
				MaterialColor.BROWN,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.BROWN).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"stripped_oak_log",
			new LogBlock(
				MaterialColor.WOOD,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.WOOD).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"oak_wood",
			new PillarBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.WOOD).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"spruce_wood",
			new PillarBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.field_19511).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"birch_wood",
			new PillarBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.SAND).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"jungle_wood",
			new PillarBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.DIRT).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"acacia_wood",
			new PillarBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.ORANGE).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"dark_oak_wood",
			new PillarBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.BROWN).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"stripped_oak_wood",
			new PillarBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.WOOD).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"stripped_spruce_wood",
			new PillarBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.field_19511).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"stripped_birch_wood",
			new PillarBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.SAND).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"stripped_jungle_wood",
			new PillarBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.DIRT).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"stripped_acacia_wood",
			new PillarBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.ORANGE).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"stripped_dark_oak_wood",
			new PillarBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.BROWN).setDurability(2.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"oak_leaves",
			new LeavesBlock(Block.Builder.setMaterial(Material.FOLIAGE).setDurability(0.2F).setRandomTicks().setBlockSoundGroup(BlockSoundGroup.field_12761))
		);
		register(
			"spruce_leaves",
			new LeavesBlock(Block.Builder.setMaterial(Material.FOLIAGE).setDurability(0.2F).setRandomTicks().setBlockSoundGroup(BlockSoundGroup.field_12761))
		);
		register(
			"birch_leaves",
			new LeavesBlock(Block.Builder.setMaterial(Material.FOLIAGE).setDurability(0.2F).setRandomTicks().setBlockSoundGroup(BlockSoundGroup.field_12761))
		);
		register(
			"jungle_leaves",
			new LeavesBlock(Block.Builder.setMaterial(Material.FOLIAGE).setDurability(0.2F).setRandomTicks().setBlockSoundGroup(BlockSoundGroup.field_12761))
		);
		register(
			"acacia_leaves",
			new LeavesBlock(Block.Builder.setMaterial(Material.FOLIAGE).setDurability(0.2F).setRandomTicks().setBlockSoundGroup(BlockSoundGroup.field_12761))
		);
		register(
			"dark_oak_leaves",
			new LeavesBlock(Block.Builder.setMaterial(Material.FOLIAGE).setDurability(0.2F).setRandomTicks().setBlockSoundGroup(BlockSoundGroup.field_12761))
		);
		register("sponge", new SpongeBlock(Block.Builder.setMaterial(Material.SPONGE).setDurability(0.6F).setBlockSoundGroup(BlockSoundGroup.field_12761)));
		register("wet_sponge", new class_3736(Block.Builder.setMaterial(Material.SPONGE).setDurability(0.6F).setBlockSoundGroup(BlockSoundGroup.field_12761)));
		register("glass", new GlassBlock(Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)));
		register("lapis_ore", new OreBlock(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(3.0F, 3.0F)));
		register("lapis_block", new Block(Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.LAPIS).setStrengthAndResistance(3.0F, 3.0F)));
		register("dispenser", new DispenserBlock(Block.Builder.setMaterial(Material.STONE).setDurability(3.5F)));
		Block block16 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.SAND).setDurability(0.8F));
		register("sandstone", block16);
		register("chiseled_sandstone", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.SAND).setDurability(0.8F)));
		register("cut_sandstone", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.SAND).setDurability(0.8F)));
		register("note_block", new NoteBlock(Block.Builder.setMaterial(Material.WOOD).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.8F)));
		register(
			"white_bed", new BedBlock(DyeColor.WHITE, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"orange_bed", new BedBlock(DyeColor.ORANGE, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"magenta_bed", new BedBlock(DyeColor.MAGENTA, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"light_blue_bed",
			new BedBlock(DyeColor.LIGHT_BLUE, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"yellow_bed", new BedBlock(DyeColor.YELLOW, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"lime_bed", new BedBlock(DyeColor.LIME, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"pink_bed", new BedBlock(DyeColor.PINK, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"gray_bed", new BedBlock(DyeColor.GRAY, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"light_gray_bed",
			new BedBlock(DyeColor.LIGHT_GRAY, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"cyan_bed", new BedBlock(DyeColor.CYAN, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"purple_bed", new BedBlock(DyeColor.PURPLE, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"blue_bed", new BedBlock(DyeColor.BLUE, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"brown_bed", new BedBlock(DyeColor.BROWN, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"green_bed", new BedBlock(DyeColor.GREEN, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register("red_bed", new BedBlock(DyeColor.RED, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F)));
		register(
			"black_bed", new BedBlock(DyeColor.BLACK, Block.Builder.setMaterial(Material.WOOL).setBlockSoundGroup(BlockSoundGroup.field_12759).setDurability(0.2F))
		);
		register(
			"powered_rail",
			new PoweredRailBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setDurability(0.7F).setBlockSoundGroup(BlockSoundGroup.field_12763))
		);
		register(
			"detector_rail",
			new DetectorRailBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setDurability(0.7F).setBlockSoundGroup(BlockSoundGroup.field_12763))
		);
		register("sticky_piston", new PistonBlock(true, Block.Builder.setMaterial(Material.PISTON).setDurability(0.5F)));
		register("cobweb", new CobwebBlock(Block.Builder.setMaterial(Material.COBWEB).setNotCollidable().setDurability(4.0F)));
		Block block17 = new TallPlantBlock(
			Block.Builder.setMaterial(Material.REPLACEABLE_PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block18 = new TallPlantBlock(
			Block.Builder.setMaterial(Material.REPLACEABLE_PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block19 = new DeadBushBlock(
			Block.Builder.setMaterialAndMapColor(Material.REPLACEABLE_PLANT, MaterialColor.WOOD)
				.setNotCollidable()
				.setNoDurability()
				.setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		register("grass", block17);
		register("fern", block18);
		register("dead_bush", block19);
		Block block20 = new class_3720(
			Block.Builder.setMaterial(Material.field_19499).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_18497)
		);
		register("seagrass", block20);
		register(
			"tall_seagrass",
			new class_3730(block20, Block.Builder.setMaterial(Material.field_19499).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_18497))
		);
		register("piston", new PistonBlock(false, Block.Builder.setMaterial(Material.PISTON).setDurability(0.5F)));
		register("piston_head", new PistonHeadBlock(Block.Builder.setMaterial(Material.PISTON).setDurability(0.5F)));
		register(
			"white_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.WHITE).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"orange_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.ORANGE).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"magenta_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.MAGENTA).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"light_blue_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.field_19529).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"yellow_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.YELLOW).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"lime_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.field_19530).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"pink_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.field_19531).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"gray_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.field_19532).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"light_gray_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.field_19533).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"cyan_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.field_19534).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"purple_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.PURPLE).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"blue_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.field_19510).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"brown_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.BROWN).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"green_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.GREEN).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"red_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.RED).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register(
			"black_wool",
			new Block(Block.Builder.setMaterialAndMapColor(Material.WOOL, MaterialColor.BLACK).setDurability(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765))
		);
		register("moving_piston", new PistonExtensionBlock(Block.Builder.setMaterial(Material.PISTON).setDurability(-1.0F).setHasDynamicBounds()));
		Block block21 = new FlowerBlock(
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block22 = new FlowerBlock(
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block23 = new FlowerBlock(
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block24 = new FlowerBlock(
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block25 = new FlowerBlock(
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block26 = new FlowerBlock(
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block27 = new FlowerBlock(
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block28 = new FlowerBlock(
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block29 = new FlowerBlock(
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		Block block30 = new FlowerBlock(
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		register("dandelion", block21);
		register("poppy", block22);
		register("blue_orchid", block23);
		register("allium", block24);
		register("azure_bluet", block25);
		register("red_tulip", block26);
		register("orange_tulip", block27);
		register("white_tulip", block28);
		register("pink_tulip", block29);
		register("oxeye_daisy", block30);
		Block block31 = new MushroomPlantBlock(
			Block.Builder.setMaterial(Material.PLANT)
				.setNotCollidable()
				.setRandomTicks()
				.setNoDurability()
				.setBlockSoundGroup(BlockSoundGroup.field_12761)
				.setLightLevel(1)
		);
		Block block32 = new MushroomPlantBlock(
			Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
		);
		register("brown_mushroom", block31);
		register("red_mushroom", block32);
		register(
			"gold_block",
			new Block(
				Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.GOLD)
					.setStrengthAndResistance(3.0F, 6.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12763)
			)
		);
		register(
			"iron_block",
			new Block(
				Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.IRON)
					.setStrengthAndResistance(5.0F, 6.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12763)
			)
		);
		Block block33 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.RED).setStrengthAndResistance(2.0F, 6.0F));
		register("bricks", block33);
		register("tnt", new TntBlock(Block.Builder.setMaterial(Material.TNT).setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)));
		register("bookshelf", new BookshelfBlock(Block.Builder.setMaterial(Material.WOOD).setDurability(1.5F).setBlockSoundGroup(BlockSoundGroup.field_12759)));
		register("mossy_cobblestone", new Block(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(2.0F, 6.0F)));
		register("obsidian", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.BLACK).setStrengthAndResistance(50.0F, 1200.0F)));
		register(
			"torch",
			new TorchBlock(
				Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setNoDurability().setLightLevel(14).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"wall_torch",
			new class_3735(
				Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setNoDurability().setLightLevel(14).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"fire",
			new FireBlock(
				Block.Builder.setMaterialAndMapColor(Material.FIRE, MaterialColor.LAVA)
					.setNotCollidable()
					.setRandomTicks()
					.setNoDurability()
					.setLightLevel(15)
					.setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register("spawner", new MobSpawnerBlock(Block.Builder.setMaterial(Material.STONE).setDurability(5.0F).setBlockSoundGroup(BlockSoundGroup.field_12763)));
		register("oak_stairs", new StairsBlock(block4.getDefaultState(), Block.Builder.fromBlock(block4)));
		register("chest", new ChestBlock(Block.Builder.setMaterial(Material.WOOD).setDurability(2.5F).setBlockSoundGroup(BlockSoundGroup.field_12759)));
		register("redstone_wire", new RedstoneWireBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setNoDurability()));
		register("diamond_ore", new OreBlock(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(3.0F, 3.0F)));
		register(
			"diamond_block",
			new Block(
				Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.DIAMOND)
					.setStrengthAndResistance(5.0F, 6.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12763)
			)
		);
		register(
			"crafting_table", new CraftingTableBlock(Block.Builder.setMaterial(Material.WOOD).setDurability(2.5F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"wheat",
			new CropBlock(
				Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
			)
		);
		Block block34 = new FarmlandBlock(
			Block.Builder.setMaterial(Material.DIRT).setRandomTicks().setDurability(0.6F).setBlockSoundGroup(BlockSoundGroup.field_12760)
		);
		register("farmland", block34);
		register("furnace", new FurnaceBlock(Block.Builder.setMaterial(Material.STONE).setDurability(3.5F).setLightLevel(13)));
		register(
			"sign",
			new StandingSignBlock(Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"oak_door",
			new DoorBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, block4.materialColor).setDurability(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register("ladder", new LadderBlock(Block.Builder.setMaterial(Material.DECORATION).setDurability(0.4F).setBlockSoundGroup(BlockSoundGroup.field_12768)));
		register(
			"rail", new RailBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setDurability(0.7F).setBlockSoundGroup(BlockSoundGroup.field_12763))
		);
		register("cobblestone_stairs", new StairsBlock(block3.getDefaultState(), Block.Builder.fromBlock(block3)));
		register(
			"wall_sign",
			new WallSignBlock(Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"lever",
			new LeverBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"stone_pressure_plate",
			new PressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, Block.Builder.setMaterial(Material.STONE).setNotCollidable().setDurability(0.5F))
		);
		register(
			"iron_door",
			new DoorBlock(Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.IRON).setDurability(5.0F).setBlockSoundGroup(BlockSoundGroup.field_12763))
		);
		register(
			"oak_pressure_plate",
			new PressurePlateBlock(
				PressurePlateBlock.ActivationRule.ALL,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block4.materialColor)
					.setNotCollidable()
					.setDurability(0.5F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"spruce_pressure_plate",
			new PressurePlateBlock(
				PressurePlateBlock.ActivationRule.ALL,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block5.materialColor)
					.setNotCollidable()
					.setDurability(0.5F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"birch_pressure_plate",
			new PressurePlateBlock(
				PressurePlateBlock.ActivationRule.ALL,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block6.materialColor)
					.setNotCollidable()
					.setDurability(0.5F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"jungle_pressure_plate",
			new PressurePlateBlock(
				PressurePlateBlock.ActivationRule.ALL,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block7.materialColor)
					.setNotCollidable()
					.setDurability(0.5F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"acacia_pressure_plate",
			new PressurePlateBlock(
				PressurePlateBlock.ActivationRule.ALL,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block8.materialColor)
					.setNotCollidable()
					.setDurability(0.5F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"dark_oak_pressure_plate",
			new PressurePlateBlock(
				PressurePlateBlock.ActivationRule.ALL,
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block9.materialColor)
					.setNotCollidable()
					.setDurability(0.5F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"redstone_ore", new RedstoneOreBlock(Block.Builder.setMaterial(Material.STONE).setRandomTicks().setLightLevel(9).setStrengthAndResistance(3.0F, 3.0F))
		);
		register(
			"redstone_torch",
			new RedstoneTorchBlock(
				Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setNoDurability().setLightLevel(7).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"redstone_wall_torch",
			new class_3717(
				Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setNoDurability().setLightLevel(7).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register("stone_button", new StoneButtonBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setDurability(0.5F)));
		register(
			"snow", new class_3724(Block.Builder.setMaterial(Material.SNOW_LAYER).setRandomTicks().setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12767))
		);
		register(
			"ice",
			new IceBlock(
				Block.Builder.setMaterial(Material.ICE).setSlipperiness(0.98F).setRandomTicks().setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"snow_block",
			new SnowLayerBlock(Block.Builder.setMaterial(Material.SNOW).setRandomTicks().setDurability(0.2F).setBlockSoundGroup(BlockSoundGroup.field_12767))
		);
		Block block35 = new CactusBlock(
			Block.Builder.setMaterial(Material.CACTUS).setRandomTicks().setDurability(0.4F).setBlockSoundGroup(BlockSoundGroup.field_12765)
		);
		register("cactus", block35);
		register("clay", new ClayBlock(Block.Builder.setMaterial(Material.CLAY).setDurability(0.6F).setBlockSoundGroup(BlockSoundGroup.field_12760)));
		register(
			"sugar_cane",
			new SugarCaneBlock(
				Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
			)
		);
		register("jukebox", new JukeboxBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.DIRT).setStrengthAndResistance(2.0F, 6.0F)));
		register(
			"oak_fence",
			new FenceBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block4.materialColor)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		GourdBlock gourdBlock = new PumpkinBlock(
			Block.Builder.setMaterialAndMapColor(Material.PUMPKIN, MaterialColor.ORANGE).setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
		);
		register("pumpkin", gourdBlock);
		register("netherrack", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.NETHER).setDurability(0.4F)));
		register(
			"soul_sand",
			new SoulSandBlock(
				Block.Builder.setMaterialAndMapColor(Material.SAND, MaterialColor.BROWN)
					.setRandomTicks()
					.setDurability(0.5F)
					.setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"glowstone",
			new GlowstoneBlock(
				Block.Builder.setMaterialAndMapColor(Material.GLASS, MaterialColor.SAND)
					.setDurability(0.3F)
					.setBlockSoundGroup(BlockSoundGroup.field_12764)
					.setLightLevel(15)
			)
		);
		register(
			"nether_portal",
			new NetherPortalBlock(
				Block.Builder.setMaterial(Material.PORTAL)
					.setNotCollidable()
					.setRandomTicks()
					.setDurability(-1.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12764)
					.setLightLevel(11)
			)
		);
		register(
			"carved_pumpkin",
			new class_3697(
				Block.Builder.setMaterialAndMapColor(Material.PUMPKIN, MaterialColor.ORANGE).setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"jack_o_lantern",
			new class_3697(
				Block.Builder.setMaterialAndMapColor(Material.PUMPKIN, MaterialColor.ORANGE)
					.setDurability(1.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
					.setLightLevel(15)
			)
		);
		register("cake", new CakeBlock(Block.Builder.setMaterial(Material.CAKE).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12765)));
		register("repeater", new RepeaterBlock(Block.Builder.setMaterial(Material.DECORATION).setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12759)));
		register(
			"white_stained_glass",
			new StainedGlassBlock(
				DyeColor.WHITE, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.WHITE).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"orange_stained_glass",
			new StainedGlassBlock(
				DyeColor.ORANGE, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.ORANGE).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"magenta_stained_glass",
			new StainedGlassBlock(
				DyeColor.MAGENTA,
				Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.MAGENTA).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"light_blue_stained_glass",
			new StainedGlassBlock(
				DyeColor.LIGHT_BLUE,
				Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.LIGHT_BLUE).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"yellow_stained_glass",
			new StainedGlassBlock(
				DyeColor.YELLOW, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.YELLOW).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"lime_stained_glass",
			new StainedGlassBlock(
				DyeColor.LIME, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.LIME).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"pink_stained_glass",
			new StainedGlassBlock(
				DyeColor.PINK, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.PINK).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"gray_stained_glass",
			new StainedGlassBlock(
				DyeColor.GRAY, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.GRAY).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"light_gray_stained_glass",
			new StainedGlassBlock(
				DyeColor.LIGHT_GRAY,
				Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.LIGHT_GRAY).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"cyan_stained_glass",
			new StainedGlassBlock(
				DyeColor.CYAN, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.CYAN).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"purple_stained_glass",
			new StainedGlassBlock(
				DyeColor.PURPLE, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.PURPLE).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"blue_stained_glass",
			new StainedGlassBlock(
				DyeColor.BLUE, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.BLUE).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"brown_stained_glass",
			new StainedGlassBlock(
				DyeColor.BROWN, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.BROWN).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"green_stained_glass",
			new StainedGlassBlock(
				DyeColor.GREEN, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.GREEN).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"red_stained_glass",
			new StainedGlassBlock(
				DyeColor.RED, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.RED).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"black_stained_glass",
			new StainedGlassBlock(
				DyeColor.BLACK, Block.Builder.setMaterialAndDyeColor(Material.GLASS, DyeColor.BLACK).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"oak_trapdoor",
			new TrapdoorBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.WOOD).setDurability(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"spruce_trapdoor",
			new TrapdoorBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.field_19511).setDurability(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"birch_trapdoor",
			new TrapdoorBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.SAND).setDurability(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"jungle_trapdoor",
			new TrapdoorBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.DIRT).setDurability(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"acacia_trapdoor",
			new TrapdoorBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.ORANGE).setDurability(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"dark_oak_trapdoor",
			new TrapdoorBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.BROWN).setDurability(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		Block block36 = new Block(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(1.5F, 6.0F));
		Block block37 = new Block(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(1.5F, 6.0F));
		Block block38 = new Block(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(1.5F, 6.0F));
		Block block39 = new Block(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(1.5F, 6.0F));
		register("infested_stone", new InfestedBlock(block2, Block.Builder.setMaterial(Material.CLAY).setStrengthAndResistance(0.0F, 0.75F)));
		register("infested_cobblestone", new InfestedBlock(block3, Block.Builder.setMaterial(Material.CLAY).setStrengthAndResistance(0.0F, 0.75F)));
		register("infested_stone_bricks", new InfestedBlock(block36, Block.Builder.setMaterial(Material.CLAY).setStrengthAndResistance(0.0F, 0.75F)));
		register("infested_mossy_stone_bricks", new InfestedBlock(block37, Block.Builder.setMaterial(Material.CLAY).setStrengthAndResistance(0.0F, 0.75F)));
		register("infested_cracked_stone_bricks", new InfestedBlock(block38, Block.Builder.setMaterial(Material.CLAY).setStrengthAndResistance(0.0F, 0.75F)));
		register("infested_chiseled_stone_bricks", new InfestedBlock(block39, Block.Builder.setMaterial(Material.CLAY).setStrengthAndResistance(0.0F, 0.75F)));
		register("stone_bricks", block36);
		register("mossy_stone_bricks", block37);
		register("cracked_stone_bricks", block38);
		register("chiseled_stone_bricks", block39);
		Block block40 = new MushroomBlock(
			block31, Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.DIRT).setDurability(0.2F).setBlockSoundGroup(BlockSoundGroup.field_12759)
		);
		register("brown_mushroom_block", block40);
		Block block41 = new MushroomBlock(
			block32, Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.RED).setDurability(0.2F).setBlockSoundGroup(BlockSoundGroup.field_12759)
		);
		register("red_mushroom_block", block41);
		register(
			"mushroom_stem",
			new MushroomBlock(
				null, Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.field_19512).setDurability(0.2F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"iron_bars",
			new PaneBlock(
				Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.AIR).setStrengthAndResistance(5.0F, 6.0F).setBlockSoundGroup(BlockSoundGroup.field_12763)
			)
		);
		register("glass_pane", new class_3706(Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)));
		GourdBlock gourdBlock2 = new MelonBlock(
			Block.Builder.setMaterialAndMapColor(Material.PUMPKIN, MaterialColor.field_19530).setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
		);
		register("melon", gourdBlock2);
		register(
			"attached_pumpkin_stem",
			new AttachedStemBlock(
				gourdBlock, Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"attached_melon_stem",
			new AttachedStemBlock(
				gourdBlock2, Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"pumpkin_stem",
			new StemBlock(
				gourdBlock, Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"melon_stem",
			new StemBlock(
				gourdBlock2,
				Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"vine",
			new VineBlock(
				Block.Builder.setMaterial(Material.REPLACEABLE_PLANT)
					.setNotCollidable()
					.setRandomTicks()
					.setDurability(0.2F)
					.setBlockSoundGroup(BlockSoundGroup.field_12761)
			)
		);
		register(
			"oak_fence_gate",
			new FenceGateBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block4.materialColor)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register("brick_stairs", new StairsBlock(block33.getDefaultState(), Block.Builder.fromBlock(block33)));
		register("stone_brick_stairs", new StairsBlock(block36.getDefaultState(), Block.Builder.fromBlock(block36)));
		register(
			"mycelium",
			new MyceliumBlock(
				Block.Builder.setMaterialAndMapColor(Material.GRASS, MaterialColor.PURPLE)
					.setRandomTicks()
					.setDurability(0.6F)
					.setBlockSoundGroup(BlockSoundGroup.field_12761)
			)
		);
		register("lily_pad", new LilyPadBlock(Block.Builder.setMaterial(Material.PLANT).setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)));
		Block block42 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.NETHER).setStrengthAndResistance(2.0F, 6.0F));
		register("nether_bricks", block42);
		register(
			"nether_brick_fence", new FenceBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.NETHER).setStrengthAndResistance(2.0F, 6.0F))
		);
		register("nether_brick_stairs", new StairsBlock(block42.getDefaultState(), Block.Builder.fromBlock(block42)));
		register("nether_wart", new NetherWartBlock(Block.Builder.setMaterialAndMapColor(Material.PLANT, MaterialColor.RED).setNotCollidable().setRandomTicks()));
		register(
			"enchanting_table",
			new EnchantingTableBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.RED).setStrengthAndResistance(5.0F, 1200.0F))
		);
		register("brewing_stand", new BrewingStandBlock(Block.Builder.setMaterial(Material.IRON).setDurability(0.5F).setLightLevel(1)));
		register("cauldron", new CauldronBlock(Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.STONE).setDurability(2.0F)));
		register(
			"end_portal",
			new EndPortalBlock(
				Block.Builder.setMaterialAndMapColor(Material.PORTAL, MaterialColor.BLACK).setNotCollidable().setLightLevel(15).setStrengthAndResistance(-1.0F, 3600000.0F)
			)
		);
		register(
			"end_portal_frame",
			new EndPortalFrameBlock(
				Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.GREEN)
					.setBlockSoundGroup(BlockSoundGroup.field_12764)
					.setLightLevel(1)
					.setStrengthAndResistance(-1.0F, 3600000.0F)
			)
		);
		register("end_stone", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.SAND).setStrengthAndResistance(3.0F, 9.0F)));
		register(
			"dragon_egg",
			new DragonEggBlock(Block.Builder.setMaterialAndMapColor(Material.EGG, MaterialColor.BLACK).setStrengthAndResistance(3.0F, 9.0F).setLightLevel(1))
		);
		register(
			"redstone_lamp",
			new RedstoneLampBlock(
				Block.Builder.setMaterial(Material.REDSTONE_LAMP).setLightLevel(15).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"cocoa",
			new CocoaBlock(
				Block.Builder.setMaterial(Material.PLANT).setRandomTicks().setStrengthAndResistance(0.2F, 3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register("sandstone_stairs", new StairsBlock(block16.getDefaultState(), Block.Builder.fromBlock(block16)));
		register("emerald_ore", new OreBlock(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(3.0F, 3.0F)));
		register("ender_chest", new EnderChestBlock(Block.Builder.setMaterial(Material.STONE).setStrengthAndResistance(22.5F, 600.0F).setLightLevel(7)));
		TripwireHookBlock tripwireHookBlock = new TripwireHookBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable());
		register("tripwire_hook", tripwireHookBlock);
		register("tripwire", new TripwireBlock(tripwireHookBlock, Block.Builder.setMaterial(Material.DECORATION).setNotCollidable()));
		register(
			"emerald_block",
			new Block(
				Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.EMERALD)
					.setStrengthAndResistance(5.0F, 6.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12763)
			)
		);
		register("spruce_stairs", new StairsBlock(block5.getDefaultState(), Block.Builder.fromBlock(block5)));
		register("birch_stairs", new StairsBlock(block6.getDefaultState(), Block.Builder.fromBlock(block6)));
		register("jungle_stairs", new StairsBlock(block7.getDefaultState(), Block.Builder.fromBlock(block7)));
		register(
			"command_block", new CommandBlock(Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.BROWN).setStrengthAndResistance(-1.0F, 3600000.0F))
		);
		register("beacon", new BeaconBlock(Block.Builder.setMaterialAndMapColor(Material.GLASS, MaterialColor.DIAMOND).setDurability(3.0F).setLightLevel(15)));
		register("cobblestone_wall", new WallBlock(Block.Builder.fromBlock(block3)));
		register("mossy_cobblestone_wall", new WallBlock(Block.Builder.fromBlock(block3)));
		register("flower_pot", new FlowerPotBlock(block, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_oak_sapling", new FlowerPotBlock(block10, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_spruce_sapling", new FlowerPotBlock(block11, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_birch_sapling", new FlowerPotBlock(block12, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_jungle_sapling", new FlowerPotBlock(block13, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_acacia_sapling", new FlowerPotBlock(block14, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_dark_oak_sapling", new FlowerPotBlock(block15, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_fern", new FlowerPotBlock(block18, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_dandelion", new FlowerPotBlock(block21, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_poppy", new FlowerPotBlock(block22, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_blue_orchid", new FlowerPotBlock(block23, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_allium", new FlowerPotBlock(block24, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_azure_bluet", new FlowerPotBlock(block25, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_red_tulip", new FlowerPotBlock(block26, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_orange_tulip", new FlowerPotBlock(block27, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_white_tulip", new FlowerPotBlock(block28, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_pink_tulip", new FlowerPotBlock(block29, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_oxeye_daisy", new FlowerPotBlock(block30, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_red_mushroom", new FlowerPotBlock(block32, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_brown_mushroom", new FlowerPotBlock(block31, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_dead_bush", new FlowerPotBlock(block19, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register("potted_cactus", new FlowerPotBlock(block35, Block.Builder.setMaterial(Material.DECORATION).setNoDurability()));
		register(
			"carrots",
			new CarrotsBlock(
				Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
			)
		);
		register(
			"potatoes",
			new PotatoesBlock(
				Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
			)
		);
		register(
			"oak_button",
			new WoodButtonBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"spruce_button",
			new WoodButtonBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"birch_button",
			new WoodButtonBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"jungle_button",
			new WoodButtonBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"acacia_button",
			new WoodButtonBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"dark_oak_button",
			new WoodButtonBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register("skeleton_wall_skull", new class_3734(SkullBlock.class_3723.SKELETON, Block.Builder.setMaterial(Material.DECORATION).setDurability(1.0F)));
		register("skeleton_skull", new SkullBlock(SkullBlock.class_3723.SKELETON, Block.Builder.setMaterial(Material.DECORATION).setDurability(1.0F)));
		register("wither_skeleton_wall_skull", new class_3738(Block.Builder.setMaterial(Material.DECORATION).setDurability(1.0F)));
		register("wither_skeleton_skull", new class_3737(Block.Builder.setMaterial(Material.DECORATION).setDurability(1.0F)));
		register("zombie_wall_head", new class_3734(SkullBlock.class_3723.ZOMBIE, Block.Builder.setMaterial(Material.DECORATION).setDurability(1.0F)));
		register("zombie_head", new SkullBlock(SkullBlock.class_3723.ZOMBIE, Block.Builder.setMaterial(Material.DECORATION).setDurability(1.0F)));
		register("player_wall_head", new class_3715(Block.Builder.setMaterial(Material.DECORATION).setDurability(1.0F)));
		register("player_head", new class_3714(Block.Builder.setMaterial(Material.DECORATION).setDurability(1.0F)));
		register("creeper_wall_head", new class_3734(SkullBlock.class_3723.CREEPER, Block.Builder.setMaterial(Material.DECORATION).setDurability(1.0F)));
		register("creeper_head", new SkullBlock(SkullBlock.class_3723.CREEPER, Block.Builder.setMaterial(Material.DECORATION).setDurability(1.0F)));
		register("dragon_wall_head", new class_3734(SkullBlock.class_3723.DRAGON, Block.Builder.setMaterial(Material.DECORATION).setDurability(1.0F)));
		register("dragon_head", new SkullBlock(SkullBlock.class_3723.DRAGON, Block.Builder.setMaterial(Material.DECORATION).setDurability(1.0F)));
		register(
			"anvil",
			new AnvilBlock(
				Block.Builder.setMaterialAndMapColor(Material.ANVIL, MaterialColor.IRON)
					.setStrengthAndResistance(5.0F, 1200.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12769)
			)
		);
		register(
			"chipped_anvil",
			new AnvilBlock(
				Block.Builder.setMaterialAndMapColor(Material.ANVIL, MaterialColor.IRON)
					.setStrengthAndResistance(5.0F, 1200.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12769)
			)
		);
		register(
			"damaged_anvil",
			new AnvilBlock(
				Block.Builder.setMaterialAndMapColor(Material.ANVIL, MaterialColor.IRON)
					.setStrengthAndResistance(5.0F, 1200.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12769)
			)
		);
		register("trapped_chest", new TrappedChestBlock(Block.Builder.setMaterial(Material.WOOD).setDurability(2.5F).setBlockSoundGroup(BlockSoundGroup.field_12759)));
		register(
			"light_weighted_pressure_plate",
			new WeightedPressurePlateBlock(
				15,
				Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.GOLD)
					.setNotCollidable()
					.setDurability(0.5F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"heavy_weighted_pressure_plate",
			new WeightedPressurePlateBlock(
				150, Block.Builder.setMaterial(Material.IRON).setNotCollidable().setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register("comparator", new ComparatorBlock(Block.Builder.setMaterial(Material.DECORATION).setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12759)));
		register(
			"daylight_detector", new DaylightDetectorBlock(Block.Builder.setMaterial(Material.WOOD).setDurability(0.2F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"redstone_block",
			new RedstoneBlock(
				Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.LAVA)
					.setStrengthAndResistance(5.0F, 6.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12763)
			)
		);
		register("nether_quartz_ore", new OreBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.NETHER).setStrengthAndResistance(3.0F, 3.0F)));
		register(
			"hopper",
			new HopperBlock(
				Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.STONE)
					.setStrengthAndResistance(3.0F, 4.8F)
					.setBlockSoundGroup(BlockSoundGroup.field_12763)
			)
		);
		Block block43 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19528).setDurability(0.8F));
		register("quartz_block", block43);
		register("chiseled_quartz_block", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19528).setDurability(0.8F)));
		register("quartz_pillar", new PillarBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19528).setDurability(0.8F)));
		register("quartz_stairs", new StairsBlock(block43.getDefaultState(), Block.Builder.fromBlock(block43)));
		register(
			"activator_rail",
			new PoweredRailBlock(Block.Builder.setMaterial(Material.DECORATION).setNotCollidable().setDurability(0.7F).setBlockSoundGroup(BlockSoundGroup.field_12763))
		);
		register("dropper", new DropperBlock(Block.Builder.setMaterial(Material.STONE).setDurability(3.5F)));
		register("white_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19512).setStrengthAndResistance(1.25F, 4.2F)));
		register(
			"orange_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19513).setStrengthAndResistance(1.25F, 4.2F))
		);
		register(
			"magenta_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19514).setStrengthAndResistance(1.25F, 4.2F))
		);
		register(
			"light_blue_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19515).setStrengthAndResistance(1.25F, 4.2F))
		);
		register(
			"yellow_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19516).setStrengthAndResistance(1.25F, 4.2F))
		);
		register("lime_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19517).setStrengthAndResistance(1.25F, 4.2F)));
		register("pink_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19518).setStrengthAndResistance(1.25F, 4.2F)));
		register("gray_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19519).setStrengthAndResistance(1.25F, 4.2F)));
		register(
			"light_gray_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19520).setStrengthAndResistance(1.25F, 4.2F))
		);
		register("cyan_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19521).setStrengthAndResistance(1.25F, 4.2F)));
		register(
			"purple_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19522).setStrengthAndResistance(1.25F, 4.2F))
		);
		register("blue_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19523).setStrengthAndResistance(1.25F, 4.2F)));
		register("brown_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19524).setStrengthAndResistance(1.25F, 4.2F)));
		register("green_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19525).setStrengthAndResistance(1.25F, 4.2F)));
		register("red_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19526).setStrengthAndResistance(1.25F, 4.2F)));
		register("black_terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19527).setStrengthAndResistance(1.25F, 4.2F)));
		register(
			"white_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.WHITE, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"orange_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.ORANGE, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"magenta_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.MAGENTA, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"light_blue_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.LIGHT_BLUE, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"yellow_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.YELLOW, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"lime_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.LIME, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"pink_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.PINK, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"gray_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.GRAY, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"light_gray_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.LIGHT_GRAY, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"cyan_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.CYAN, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"purple_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.PURPLE, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"blue_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.BLUE, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"brown_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.BROWN, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"green_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.GREEN, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"red_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.RED, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"black_stained_glass_pane",
			new StainedGlassPaneBlock(DyeColor.BLACK, Block.Builder.setMaterial(Material.GLASS).setDurability(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register("acacia_stairs", new StairsBlock(block8.getDefaultState(), Block.Builder.fromBlock(block8)));
		register("dark_oak_stairs", new StairsBlock(block9.getDefaultState(), Block.Builder.fromBlock(block9)));
		register(
			"slime_block",
			new SlimeBlock(
				Block.Builder.setMaterialAndMapColor(Material.CLAY, MaterialColor.GRASS).setSlipperiness(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12770)
			)
		);
		register("barrier", new BarrierBlock(Block.Builder.setMaterial(Material.BARRIER).setStrengthAndResistance(-1.0F, 3600000.8F)));
		register("iron_trapdoor", new TrapdoorBlock(Block.Builder.setMaterial(Material.IRON).setDurability(5.0F).setBlockSoundGroup(BlockSoundGroup.field_12763)));
		Block block44 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19534).setStrengthAndResistance(1.5F, 6.0F));
		register("prismarine", block44);
		Block block45 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.DIAMOND).setStrengthAndResistance(1.5F, 6.0F));
		register("prismarine_bricks", block45);
		Block block46 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.DIAMOND).setStrengthAndResistance(1.5F, 6.0F));
		register("dark_prismarine", block46);
		register("prismarine_stairs", new StairsBlock(block44.getDefaultState(), Block.Builder.fromBlock(block44)));
		register("prismarine_brick_stairs", new StairsBlock(block45.getDefaultState(), Block.Builder.fromBlock(block45)));
		register("dark_prismarine_stairs", new StairsBlock(block46.getDefaultState(), Block.Builder.fromBlock(block46)));
		register(
			"prismarine_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19534).setStrengthAndResistance(1.5F, 6.0F))
		);
		register(
			"prismarine_brick_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.DIAMOND).setStrengthAndResistance(1.5F, 6.0F))
		);
		register(
			"dark_prismarine_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.DIAMOND).setStrengthAndResistance(1.5F, 6.0F))
		);
		register(
			"sea_lantern",
			new SeaLanternBlock(
				Block.Builder.setMaterialAndMapColor(Material.GLASS, MaterialColor.field_19528)
					.setDurability(0.3F)
					.setBlockSoundGroup(BlockSoundGroup.field_12764)
					.setLightLevel(15)
			)
		);
		register(
			"hay_block",
			new HayBlock(Block.Builder.setMaterialAndMapColor(Material.GRASS, MaterialColor.YELLOW).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12761))
		);
		register(
			"white_carpet",
			new CarpetBlock(
				DyeColor.WHITE,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.WHITE).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"orange_carpet",
			new CarpetBlock(
				DyeColor.ORANGE,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.ORANGE).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"magenta_carpet",
			new CarpetBlock(
				DyeColor.MAGENTA,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.MAGENTA).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"light_blue_carpet",
			new CarpetBlock(
				DyeColor.LIGHT_BLUE,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.field_19529).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"yellow_carpet",
			new CarpetBlock(
				DyeColor.YELLOW,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.YELLOW).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"lime_carpet",
			new CarpetBlock(
				DyeColor.LIME,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.field_19530).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"pink_carpet",
			new CarpetBlock(
				DyeColor.PINK,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.field_19531).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"gray_carpet",
			new CarpetBlock(
				DyeColor.GRAY,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.field_19532).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"light_gray_carpet",
			new CarpetBlock(
				DyeColor.LIGHT_GRAY,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.field_19533).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"cyan_carpet",
			new CarpetBlock(
				DyeColor.CYAN,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.field_19534).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"purple_carpet",
			new CarpetBlock(
				DyeColor.PURPLE,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.PURPLE).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"blue_carpet",
			new CarpetBlock(
				DyeColor.BLUE,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.field_19510).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"brown_carpet",
			new CarpetBlock(
				DyeColor.BROWN,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.BROWN).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"green_carpet",
			new CarpetBlock(
				DyeColor.GREEN,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.GREEN).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"red_carpet",
			new CarpetBlock(
				DyeColor.RED, Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.RED).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register(
			"black_carpet",
			new CarpetBlock(
				DyeColor.BLACK,
				Block.Builder.setMaterialAndMapColor(Material.CARPET, MaterialColor.BLACK).setDurability(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765)
			)
		);
		register("terracotta", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.ORANGE).setStrengthAndResistance(1.25F, 4.2F)));
		register("coal_block", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.BLACK).setStrengthAndResistance(5.0F, 6.0F)));
		register(
			"packed_ice",
			new PackedIceBlock(Block.Builder.setMaterial(Material.PACKED_ICE).setSlipperiness(0.98F).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register(
			"sunflower",
			new class_3729(Block.Builder.setMaterial(Material.REPLACEABLE_PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761))
		);
		register(
			"lilac",
			new class_3729(Block.Builder.setMaterial(Material.REPLACEABLE_PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761))
		);
		register(
			"rose_bush",
			new class_3729(Block.Builder.setMaterial(Material.REPLACEABLE_PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761))
		);
		register(
			"peony",
			new class_3729(Block.Builder.setMaterial(Material.REPLACEABLE_PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761))
		);
		register(
			"tall_grass",
			new class_3721(
				block17, Block.Builder.setMaterial(Material.REPLACEABLE_PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
			)
		);
		register(
			"large_fern",
			new class_3721(
				block18, Block.Builder.setMaterial(Material.REPLACEABLE_PLANT).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
			)
		);
		register(
			"white_banner",
			new BannerBlock(
				DyeColor.WHITE, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"orange_banner",
			new BannerBlock(
				DyeColor.ORANGE, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"magenta_banner",
			new BannerBlock(
				DyeColor.MAGENTA, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"light_blue_banner",
			new BannerBlock(
				DyeColor.LIGHT_BLUE, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"yellow_banner",
			new BannerBlock(
				DyeColor.YELLOW, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"lime_banner",
			new BannerBlock(
				DyeColor.LIME, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"pink_banner",
			new BannerBlock(
				DyeColor.PINK, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"gray_banner",
			new BannerBlock(
				DyeColor.GRAY, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"light_gray_banner",
			new BannerBlock(
				DyeColor.LIGHT_GRAY, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"cyan_banner",
			new BannerBlock(
				DyeColor.CYAN, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"purple_banner",
			new BannerBlock(
				DyeColor.PURPLE, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"blue_banner",
			new BannerBlock(
				DyeColor.BLUE, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"brown_banner",
			new BannerBlock(
				DyeColor.BROWN, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"green_banner",
			new BannerBlock(
				DyeColor.GREEN, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"red_banner",
			new BannerBlock(
				DyeColor.RED, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"black_banner",
			new BannerBlock(
				DyeColor.BLACK, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"white_wall_banner",
			new class_3733(
				DyeColor.WHITE, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"orange_wall_banner",
			new class_3733(
				DyeColor.ORANGE, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"magenta_wall_banner",
			new class_3733(
				DyeColor.MAGENTA, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"light_blue_wall_banner",
			new class_3733(
				DyeColor.LIGHT_BLUE, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"yellow_wall_banner",
			new class_3733(
				DyeColor.YELLOW, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"lime_wall_banner",
			new class_3733(
				DyeColor.LIME, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"pink_wall_banner",
			new class_3733(
				DyeColor.PINK, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"gray_wall_banner",
			new class_3733(
				DyeColor.GRAY, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"light_gray_wall_banner",
			new class_3733(
				DyeColor.LIGHT_GRAY, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"cyan_wall_banner",
			new class_3733(
				DyeColor.CYAN, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"purple_wall_banner",
			new class_3733(
				DyeColor.PURPLE, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"blue_wall_banner",
			new class_3733(
				DyeColor.BLUE, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"brown_wall_banner",
			new class_3733(
				DyeColor.BROWN, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"green_wall_banner",
			new class_3733(
				DyeColor.GREEN, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"red_wall_banner",
			new class_3733(DyeColor.RED, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"black_wall_banner",
			new class_3733(
				DyeColor.BLACK, Block.Builder.setMaterial(Material.WOOD).setNotCollidable().setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		Block block47 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.ORANGE).setDurability(0.8F));
		register("red_sandstone", block47);
		register("chiseled_red_sandstone", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.ORANGE).setDurability(0.8F)));
		register("cut_red_sandstone", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.ORANGE).setDurability(0.8F)));
		register("red_sandstone_stairs", new StairsBlock(block47.getDefaultState(), Block.Builder.fromBlock(block47)));
		register(
			"oak_slab",
			new SlabBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.WOOD)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"spruce_slab",
			new SlabBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.field_19511)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"birch_slab",
			new SlabBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.SAND)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"jungle_slab",
			new SlabBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.DIRT)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"acacia_slab",
			new SlabBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.ORANGE)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"dark_oak_slab",
			new SlabBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, MaterialColor.BROWN)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register("stone_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.STONE).setStrengthAndResistance(2.0F, 6.0F)));
		register("sandstone_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.SAND).setStrengthAndResistance(2.0F, 6.0F)));
		register("petrified_oak_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.WOOD).setStrengthAndResistance(2.0F, 6.0F)));
		register("cobblestone_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.STONE).setStrengthAndResistance(2.0F, 6.0F)));
		register("brick_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.RED).setStrengthAndResistance(2.0F, 6.0F)));
		register("stone_brick_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.STONE).setStrengthAndResistance(2.0F, 6.0F)));
		register("nether_brick_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.NETHER).setStrengthAndResistance(2.0F, 6.0F)));
		register("quartz_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19528).setStrengthAndResistance(2.0F, 6.0F)));
		register("red_sandstone_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.ORANGE).setStrengthAndResistance(2.0F, 6.0F)));
		register("purpur_slab", new SlabBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.MAGENTA).setStrengthAndResistance(2.0F, 6.0F)));
		register("smooth_stone", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.STONE).setStrengthAndResistance(2.0F, 6.0F)));
		register("smooth_sandstone", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.SAND).setStrengthAndResistance(2.0F, 6.0F)));
		register("smooth_quartz", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19528).setStrengthAndResistance(2.0F, 6.0F)));
		register("smooth_red_sandstone", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.ORANGE).setStrengthAndResistance(2.0F, 6.0F)));
		register(
			"spruce_fence_gate",
			new FenceGateBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block5.materialColor)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"birch_fence_gate",
			new FenceGateBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block6.materialColor)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"jungle_fence_gate",
			new FenceGateBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block7.materialColor)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"acacia_fence_gate",
			new FenceGateBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block8.materialColor)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"dark_oak_fence_gate",
			new FenceGateBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block9.materialColor)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"spruce_fence",
			new FenceBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block5.materialColor)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"birch_fence",
			new FenceBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block6.materialColor)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"jungle_fence",
			new FenceBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block7.materialColor)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"acacia_fence",
			new FenceBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block8.materialColor)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"dark_oak_fence",
			new FenceBlock(
				Block.Builder.setMaterialAndMapColor(Material.WOOD, block9.materialColor)
					.setStrengthAndResistance(2.0F, 3.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		register(
			"spruce_door",
			new DoorBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, block5.materialColor).setDurability(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"birch_door",
			new DoorBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, block6.materialColor).setDurability(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"jungle_door",
			new DoorBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, block7.materialColor).setDurability(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"acacia_door",
			new DoorBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, block8.materialColor).setDurability(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"dark_oak_door",
			new DoorBlock(Block.Builder.setMaterialAndMapColor(Material.WOOD, block9.materialColor).setDurability(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register(
			"end_rod",
			new EndRodBlock(Block.Builder.setMaterial(Material.DECORATION).setNoDurability().setLightLevel(14).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		ChorusPlantBlock chorusPlantBlock = new ChorusPlantBlock(
			Block.Builder.setMaterialAndMapColor(Material.PLANT, MaterialColor.PURPLE).setDurability(0.4F).setBlockSoundGroup(BlockSoundGroup.field_12759)
		);
		register("chorus_plant", chorusPlantBlock);
		register(
			"chorus_flower",
			new ChorusFlowerBlock(
				chorusPlantBlock,
				Block.Builder.setMaterialAndMapColor(Material.PLANT, MaterialColor.PURPLE)
					.setRandomTicks()
					.setDurability(0.4F)
					.setBlockSoundGroup(BlockSoundGroup.field_12759)
			)
		);
		Block block48 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.MAGENTA).setStrengthAndResistance(1.5F, 6.0F));
		register("purpur_block", block48);
		register("purpur_pillar", new PillarBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.MAGENTA).setStrengthAndResistance(1.5F, 6.0F)));
		register("purpur_stairs", new StairsBlock(block48.getDefaultState(), Block.Builder.fromBlock(block48)));
		register("end_stone_bricks", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.SAND).setDurability(0.8F)));
		register(
			"beetroots",
			new BeetrootsBlock(
				Block.Builder.setMaterial(Material.PLANT).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_12761)
			)
		);
		Block block49 = new GrassPathBlock(Block.Builder.setMaterial(Material.DIRT).setDurability(0.65F).setBlockSoundGroup(BlockSoundGroup.field_12761));
		register("grass_path", block49);
		register(
			"end_gateway",
			new EndGatewayBlock(
				Block.Builder.setMaterialAndMapColor(Material.PORTAL, MaterialColor.BLACK).setNotCollidable().setLightLevel(15).setStrengthAndResistance(-1.0F, 3600000.0F)
			)
		);
		register(
			"repeating_command_block",
			new CommandBlock(Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.PURPLE).setStrengthAndResistance(-1.0F, 3600000.0F))
		);
		register(
			"chain_command_block",
			new CommandBlock(Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.GREEN).setStrengthAndResistance(-1.0F, 3600000.0F))
		);
		register(
			"frosted_ice",
			new FrostedIceBlock(
				Block.Builder.setMaterial(Material.ICE).setSlipperiness(0.98F).setRandomTicks().setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12764)
			)
		);
		register(
			"magma_block",
			new MagmaBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.NETHER).setLightLevel(3).setRandomTicks().setDurability(0.5F))
		);
		register(
			"nether_wart_block",
			new Block(Block.Builder.setMaterialAndMapColor(Material.GRASS, MaterialColor.RED).setDurability(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759))
		);
		register("red_nether_bricks", new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.NETHER).setStrengthAndResistance(2.0F, 6.0F)));
		register("bone_block", new PillarBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.SAND).setDurability(2.0F)));
		register("structure_void", new StructureVoidBlock(Block.Builder.setMaterial(Material.CAVE_AIR).setNotCollidable()));
		register("observer", new ObserverBlock(Block.Builder.setMaterial(Material.STONE).setDurability(3.0F)));
		register(
			"shulker_box",
			new ShulkerBoxBlock(null, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.PURPLE).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"white_shulker_box",
			new ShulkerBoxBlock(DyeColor.WHITE, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.WHITE).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"orange_shulker_box",
			new ShulkerBoxBlock(DyeColor.ORANGE, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.ORANGE).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"magenta_shulker_box",
			new ShulkerBoxBlock(DyeColor.MAGENTA, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.MAGENTA).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"light_blue_shulker_box",
			new ShulkerBoxBlock(
				DyeColor.LIGHT_BLUE, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19529).setDurability(2.0F).setHasDynamicBounds()
			)
		);
		register(
			"yellow_shulker_box",
			new ShulkerBoxBlock(DyeColor.YELLOW, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.YELLOW).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"lime_shulker_box",
			new ShulkerBoxBlock(DyeColor.LIME, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19530).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"pink_shulker_box",
			new ShulkerBoxBlock(DyeColor.PINK, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19531).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"gray_shulker_box",
			new ShulkerBoxBlock(DyeColor.GRAY, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"light_gray_shulker_box",
			new ShulkerBoxBlock(
				DyeColor.LIGHT_GRAY, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19533).setDurability(2.0F).setHasDynamicBounds()
			)
		);
		register(
			"cyan_shulker_box",
			new ShulkerBoxBlock(DyeColor.CYAN, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19534).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"purple_shulker_box",
			new ShulkerBoxBlock(
				DyeColor.PURPLE, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19522).setDurability(2.0F).setHasDynamicBounds()
			)
		);
		register(
			"blue_shulker_box",
			new ShulkerBoxBlock(DyeColor.BLUE, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19510).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"brown_shulker_box",
			new ShulkerBoxBlock(DyeColor.BROWN, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.BROWN).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"green_shulker_box",
			new ShulkerBoxBlock(DyeColor.GREEN, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.GREEN).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"red_shulker_box",
			new ShulkerBoxBlock(DyeColor.RED, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.RED).setDurability(2.0F).setHasDynamicBounds())
		);
		register(
			"black_shulker_box",
			new ShulkerBoxBlock(DyeColor.BLACK, Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.BLACK).setDurability(2.0F).setHasDynamicBounds())
		);
		register("white_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.WHITE).setDurability(1.4F)));
		register("orange_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.ORANGE).setDurability(1.4F)));
		register("magenta_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.MAGENTA).setDurability(1.4F)));
		register(
			"light_blue_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.LIGHT_BLUE).setDurability(1.4F))
		);
		register("yellow_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.YELLOW).setDurability(1.4F)));
		register("lime_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.LIME).setDurability(1.4F)));
		register("pink_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.PINK).setDurability(1.4F)));
		register("gray_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.GRAY).setDurability(1.4F)));
		register(
			"light_gray_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.LIGHT_GRAY).setDurability(1.4F))
		);
		register("cyan_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.CYAN).setDurability(1.4F)));
		register("purple_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.PURPLE).setDurability(1.4F)));
		register("blue_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.BLUE).setDurability(1.4F)));
		register("brown_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.BROWN).setDurability(1.4F)));
		register("green_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.GREEN).setDurability(1.4F)));
		register("red_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.RED).setDurability(1.4F)));
		register("black_glazed_terracotta", new GlazedTerracottaBlock(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.BLACK).setDurability(1.4F)));
		Block block50 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.WHITE).setDurability(1.8F));
		Block block51 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.ORANGE).setDurability(1.8F));
		Block block52 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.MAGENTA).setDurability(1.8F));
		Block block53 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.LIGHT_BLUE).setDurability(1.8F));
		Block block54 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.YELLOW).setDurability(1.8F));
		Block block55 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.LIME).setDurability(1.8F));
		Block block56 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.PINK).setDurability(1.8F));
		Block block57 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.GRAY).setDurability(1.8F));
		Block block58 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.LIGHT_GRAY).setDurability(1.8F));
		Block block59 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.CYAN).setDurability(1.8F));
		Block block60 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.PURPLE).setDurability(1.8F));
		Block block61 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.BLUE).setDurability(1.8F));
		Block block62 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.BROWN).setDurability(1.8F));
		Block block63 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.GREEN).setDurability(1.8F));
		Block block64 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.RED).setDurability(1.8F));
		Block block65 = new Block(Block.Builder.setMaterialAndDyeColor(Material.STONE, DyeColor.BLACK).setDurability(1.8F));
		register("white_concrete", block50);
		register("orange_concrete", block51);
		register("magenta_concrete", block52);
		register("light_blue_concrete", block53);
		register("yellow_concrete", block54);
		register("lime_concrete", block55);
		register("pink_concrete", block56);
		register("gray_concrete", block57);
		register("light_gray_concrete", block58);
		register("cyan_concrete", block59);
		register("purple_concrete", block60);
		register("blue_concrete", block61);
		register("brown_concrete", block62);
		register("green_concrete", block63);
		register("red_concrete", block64);
		register("black_concrete", block65);
		register(
			"white_concrete_powder",
			new ConcretePowderBlock(
				block50, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.WHITE).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"orange_concrete_powder",
			new ConcretePowderBlock(
				block51, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.ORANGE).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"magenta_concrete_powder",
			new ConcretePowderBlock(
				block52, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.MAGENTA).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"light_blue_concrete_powder",
			new ConcretePowderBlock(
				block53, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.LIGHT_BLUE).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"yellow_concrete_powder",
			new ConcretePowderBlock(
				block54, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.YELLOW).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"lime_concrete_powder",
			new ConcretePowderBlock(
				block55, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.LIME).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"pink_concrete_powder",
			new ConcretePowderBlock(
				block56, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.PINK).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"gray_concrete_powder",
			new ConcretePowderBlock(
				block57, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.GRAY).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"light_gray_concrete_powder",
			new ConcretePowderBlock(
				block58, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.LIGHT_GRAY).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"cyan_concrete_powder",
			new ConcretePowderBlock(
				block59, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.CYAN).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"purple_concrete_powder",
			new ConcretePowderBlock(
				block60, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.PURPLE).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"blue_concrete_powder",
			new ConcretePowderBlock(
				block61, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.BLUE).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"brown_concrete_powder",
			new ConcretePowderBlock(
				block62, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.BROWN).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"green_concrete_powder",
			new ConcretePowderBlock(
				block63, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.GREEN).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"red_concrete_powder",
			new ConcretePowderBlock(
				block64, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.RED).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		register(
			"black_concrete_powder",
			new ConcretePowderBlock(
				block65, Block.Builder.setMaterialAndDyeColor(Material.SAND, DyeColor.BLACK).setDurability(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766)
			)
		);
		class_3708 lv = new class_3708(
			Block.Builder.setMaterial(Material.field_19498).setNotCollidable().setRandomTicks().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_18497)
		);
		register("kelp", lv);
		register(
			"kelp_plant",
			new class_3709(lv, Block.Builder.setMaterial(Material.field_19498).setNotCollidable().setNoDurability().setBlockSoundGroup(BlockSoundGroup.field_18497))
		);
		register(
			"dried_kelp_block",
			new Block(
				Block.Builder.setMaterialAndMapColor(Material.GRASS, MaterialColor.BROWN)
					.setStrengthAndResistance(0.5F, 2.5F)
					.setBlockSoundGroup(BlockSoundGroup.field_12761)
			)
		);
		register(
			"turtle_egg",
			new class_3732(
				Block.Builder.setMaterialAndMapColor(Material.EGG, MaterialColor.field_19533)
					.setDurability(0.5F)
					.setBlockSoundGroup(BlockSoundGroup.field_12763)
					.setRandomTicks()
			)
		);
		Block block66 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setStrengthAndResistance(1.5F, 6.0F));
		Block block67 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setStrengthAndResistance(1.5F, 6.0F));
		Block block68 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setStrengthAndResistance(1.5F, 6.0F));
		Block block69 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setStrengthAndResistance(1.5F, 6.0F));
		Block block70 = new Block(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setStrengthAndResistance(1.5F, 6.0F));
		register("dead_tube_coral_block", block66);
		register("dead_brain_coral_block", block67);
		register("dead_bubble_coral_block", block68);
		register("dead_fire_coral_block", block69);
		register("dead_horn_coral_block", block70);
		register(
			"tube_coral_block",
			new CoralBlockBlock(
				block66,
				Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19510)
					.setStrengthAndResistance(1.5F, 6.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_18498)
			)
		);
		register(
			"brain_coral_block",
			new CoralBlockBlock(
				block67,
				Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19531)
					.setStrengthAndResistance(1.5F, 6.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_18498)
			)
		);
		register(
			"bubble_coral_block",
			new CoralBlockBlock(
				block68,
				Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.PURPLE)
					.setStrengthAndResistance(1.5F, 6.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_18498)
			)
		);
		register(
			"fire_coral_block",
			new CoralBlockBlock(
				block69,
				Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.RED)
					.setStrengthAndResistance(1.5F, 6.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_18498)
			)
		);
		register(
			"horn_coral_block",
			new CoralBlockBlock(
				block70,
				Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.YELLOW)
					.setStrengthAndResistance(1.5F, 6.0F)
					.setBlockSoundGroup(BlockSoundGroup.field_18498)
			)
		);
		Block block71 = new DeadCoralBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability());
		Block block72 = new DeadCoralBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability());
		Block block73 = new DeadCoralBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability());
		Block block74 = new DeadCoralBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability());
		Block block75 = new DeadCoralBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability());
		register("dead_tube_coral", block71);
		register("dead_brain_coral", block72);
		register("dead_bubble_coral", block73);
		register("dead_fire_coral", block74);
		register("dead_horn_coral", block75);
		register(
			"tube_coral",
			new CoralBlock(
				block71,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.field_19510)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"brain_coral",
			new CoralBlock(
				block72,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.field_19531)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"bubble_coral",
			new CoralBlock(
				block73,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.PURPLE)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"fire_coral",
			new CoralBlock(
				block74,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.RED)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"horn_coral",
			new CoralBlock(
				block75,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.YELLOW)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		Block block76 = new DeadCoralWallFanBlock(
			Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability()
		);
		Block block77 = new DeadCoralWallFanBlock(
			Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability()
		);
		Block block78 = new DeadCoralWallFanBlock(
			Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability()
		);
		Block block79 = new DeadCoralWallFanBlock(
			Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability()
		);
		Block block80 = new DeadCoralWallFanBlock(
			Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability()
		);
		register("dead_tube_coral_wall_fan", block76);
		register("dead_brain_coral_wall_fan", block77);
		register("dead_bubble_coral_wall_fan", block78);
		register("dead_fire_coral_wall_fan", block79);
		register("dead_horn_coral_wall_fan", block80);
		register(
			"tube_coral_wall_fan",
			new CoralWallFanBlock(
				block76,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.field_19510)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"brain_coral_wall_fan",
			new CoralWallFanBlock(
				block77,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.field_19531)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"bubble_coral_wall_fan",
			new CoralWallFanBlock(
				block78,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.PURPLE)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"fire_coral_wall_fan",
			new CoralWallFanBlock(
				block79,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.RED)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"horn_coral_wall_fan",
			new CoralWallFanBlock(
				block80,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.YELLOW)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		Block block81 = new DeadCoralFanBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability());
		Block block82 = new DeadCoralFanBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability());
		Block block83 = new DeadCoralFanBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability());
		Block block84 = new DeadCoralFanBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability());
		Block block85 = new DeadCoralFanBlock(Block.Builder.setMaterialAndMapColor(Material.STONE, MaterialColor.field_19532).setNotCollidable().setNoDurability());
		register("dead_tube_coral_fan", block81);
		register("dead_brain_coral_fan", block82);
		register("dead_bubble_coral_fan", block83);
		register("dead_fire_coral_fan", block84);
		register("dead_horn_coral_fan", block85);
		register(
			"tube_coral_fan",
			new CoralFanBlock(
				block81,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.field_19510)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"brain_coral_fan",
			new CoralFanBlock(
				block82,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.field_19531)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"bubble_coral_fan",
			new CoralFanBlock(
				block83,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.PURPLE)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"fire_coral_fan",
			new CoralFanBlock(
				block84,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.RED)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"horn_coral_fan",
			new CoralFanBlock(
				block85,
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.YELLOW)
					.setNotCollidable()
					.setNoDurability()
					.setBlockSoundGroup(BlockSoundGroup.field_18497)
			)
		);
		register(
			"sea_pickle",
			new class_3719(
				Block.Builder.setMaterialAndMapColor(Material.field_19498, MaterialColor.GREEN).setLightLevel(3).setBlockSoundGroup(BlockSoundGroup.field_12770)
			)
		);
		register(
			"blue_ice",
			new class_3693(Block.Builder.setMaterial(Material.PACKED_ICE).setDurability(2.8F).setSlipperiness(0.989F).setBlockSoundGroup(BlockSoundGroup.field_12764))
		);
		register("conduit", new class_3698(Block.Builder.setMaterialAndMapColor(Material.GLASS, MaterialColor.DIAMOND).setDurability(3.0F).setLightLevel(15)));
		register("void_air", new AirBlock(Block.Builder.setMaterial(Material.AIR).setNotCollidable()));
		register("cave_air", new AirBlock(Block.Builder.setMaterial(Material.AIR).setNotCollidable()));
		register("bubble_column", new class_3694(Block.Builder.setMaterial(Material.field_19500).setNotCollidable()));
		register(
			"structure_block",
			new StructureBlock(Block.Builder.setMaterialAndMapColor(Material.IRON, MaterialColor.field_19533).setStrengthAndResistance(-1.0F, 3600000.0F))
		);

		for (Block block86 : Registry.BLOCK) {
			UnmodifiableIterator var92 = block86.getStateManager().getBlockStates().iterator();

			while (var92.hasNext()) {
				BlockState blockState = (BlockState)var92.next();
				BLOCK_STATES.method_19952(blockState);
			}
		}
	}

	private static void add(Identifier identifier, Block block) {
		Registry.BLOCK.add(identifier, block);
	}

	private static void register(String identifier, Block block) {
		add(new Identifier(identifier), block);
	}

	public static class Builder {
		private Material material;
		private MaterialColor materialColor;
		private boolean collidable = true;
		private BlockSoundGroup blockSoundGroup = BlockSoundGroup.STONE;
		private int lightLevel;
		private float blastResistance;
		private float strength;
		private boolean randomTicks;
		private float slipperiness = 0.6F;
		private boolean dynamicBounds;

		private Builder(Material material, MaterialColor materialColor) {
			this.material = material;
			this.materialColor = materialColor;
		}

		public static Block.Builder setMaterial(Material material) {
			return setMaterialAndMapColor(material, material.getColor());
		}

		public static Block.Builder setMaterialAndDyeColor(Material material, DyeColor dyeColor) {
			return setMaterialAndMapColor(material, dyeColor.getColorOfMaterial());
		}

		public static Block.Builder setMaterialAndMapColor(Material material, MaterialColor materialColor) {
			return new Block.Builder(material, materialColor);
		}

		public static Block.Builder fromBlock(Block block) {
			Block.Builder builder = new Block.Builder(block.material, block.materialColor);
			builder.material = block.material;
			builder.strength = block.hardness;
			builder.blastResistance = block.blastResistance;
			builder.collidable = block.collidable;
			builder.randomTicks = block.randomTicks;
			builder.lightLevel = block.lightLevel;
			builder.material = block.material;
			builder.materialColor = block.materialColor;
			builder.blockSoundGroup = block.blockSoundGroup;
			builder.slipperiness = block.getSlipperiness();
			builder.dynamicBounds = block.dynamicBounds;
			return builder;
		}

		public Block.Builder setNotCollidable() {
			this.collidable = false;
			return this;
		}

		public Block.Builder setSlipperiness(float slipperiness) {
			this.slipperiness = slipperiness;
			return this;
		}

		protected Block.Builder setBlockSoundGroup(BlockSoundGroup blockSoundGroup) {
			this.blockSoundGroup = blockSoundGroup;
			return this;
		}

		protected Block.Builder setLightLevel(int lightLevel) {
			this.lightLevel = lightLevel;
			return this;
		}

		public Block.Builder setStrengthAndResistance(float strength, float blastResistance) {
			this.strength = strength;
			this.blastResistance = Math.max(0.0F, blastResistance);
			return this;
		}

		protected Block.Builder setNoDurability() {
			return this.setDurability(0.0F);
		}

		protected Block.Builder setDurability(float durability) {
			this.setStrengthAndResistance(durability, durability);
			return this;
		}

		protected Block.Builder setRandomTicks() {
			this.randomTicks = true;
			return this;
		}

		protected Block.Builder setHasDynamicBounds() {
			this.dynamicBounds = true;
			return this;
		}
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
			return Objects.hash(new Object[]{this.self, this.other, this.facing});
		}
	}

	public static enum OffsetType {
		NONE,
		XZ,
		XYZ;
	}
}
