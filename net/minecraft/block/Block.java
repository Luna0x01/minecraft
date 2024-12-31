package net.minecraft.block;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.BiDefaultedRegistry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class Block {
	private static final Identifier AIR_ID = new Identifier("air");
	public static final BiDefaultedRegistry<Identifier, Block> REGISTRY = new BiDefaultedRegistry<>(AIR_ID);
	public static final IdList<BlockState> BLOCK_STATES = new IdList<>();
	public static final Box collisionBox = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
	public static final Box EMPTY_BOX = null;
	private ItemGroup itemGroup;
	protected boolean fullBlock;
	protected int opacity;
	protected boolean translucent;
	protected int lightLevel;
	protected boolean useNeighbourLight;
	protected float hardness;
	protected float blastResistance;
	protected boolean stats = true;
	protected boolean randomTicks;
	protected boolean blockEntity;
	protected BlockSoundGroup blockSoundGroup = BlockSoundGroup.STONE;
	public float particleGravity = 1.0F;
	protected final Material material;
	protected final MaterialColor materialColor;
	public float slipperiness = 0.6F;
	protected final StateManager stateManager;
	private BlockState defaultState;
	private String translationKey;

	public static int getIdByBlock(Block block) {
		return REGISTRY.getRawId(block);
	}

	public static int getByBlockState(BlockState state) {
		Block block = state.getBlock();
		return getIdByBlock(block) + (block.getData(state) << 12);
	}

	public static Block getById(int id) {
		return REGISTRY.getByRawId(id);
	}

	public static BlockState getStateFromRawId(int id) {
		int i = id & 4095;
		int j = id >> 12 & 15;
		return getById(i).stateFromData(j);
	}

	public static Block getBlockFromItem(Item item) {
		return item instanceof BlockItem ? ((BlockItem)item).getBlock() : null;
	}

	@Nullable
	public static Block get(String id) {
		Identifier identifier = new Identifier(id);
		if (REGISTRY.containsKey(identifier)) {
			return REGISTRY.get(identifier);
		} else {
			try {
				return REGISTRY.getByRawId(Integer.parseInt(id));
			} catch (NumberFormatException var3) {
				return null;
			}
		}
	}

	@Deprecated
	public boolean method_11568(BlockState state) {
		return state.getMaterial().isOpaque() && state.method_11730();
	}

	@Deprecated
	public boolean isFullBlock(BlockState state) {
		return this.fullBlock;
	}

	@Deprecated
	public int getOpacity(BlockState blockState) {
		return this.opacity;
	}

	@Deprecated
	public boolean isTranslucent(BlockState blockState) {
		return this.translucent;
	}

	@Deprecated
	public int getLuminance(BlockState state) {
		return this.lightLevel;
	}

	@Deprecated
	public boolean useNeighbourLight(BlockState blockState) {
		return this.useNeighbourLight;
	}

	@Deprecated
	public Material getMaterial(BlockState state) {
		return this.material;
	}

	@Deprecated
	public MaterialColor getMaterialColor(BlockState state) {
		return this.materialColor;
	}

	@Deprecated
	public BlockState stateFromData(int data) {
		return this.getDefaultState();
	}

	public int getData(BlockState state) {
		if (state != null && !state.getProperties().isEmpty()) {
			throw new IllegalArgumentException("Don't know how to convert " + state + " back into data...");
		} else {
			return 0;
		}
	}

	@Deprecated
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
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

	public Block(Material material, MaterialColor materialColor) {
		this.material = material;
		this.materialColor = materialColor;
		this.stateManager = this.appendProperties();
		this.setDefaultState(this.stateManager.getDefaultState());
		this.fullBlock = this.getDefaultState().isFullBoundsCubeForCulling();
		this.opacity = this.fullBlock ? 255 : 0;
		this.translucent = !material.isTranslucent();
	}

	protected Block(Material material) {
		this(material, material.getColor());
	}

	protected Block setBlockSoundGroup(BlockSoundGroup blockSoundGroup) {
		this.blockSoundGroup = blockSoundGroup;
		return this;
	}

	protected Block setOpacity(int opacity) {
		this.opacity = opacity;
		return this;
	}

	protected Block setLightLevel(float lightLevel) {
		this.lightLevel = (int)(15.0F * lightLevel);
		return this;
	}

	protected Block setResistance(float resistance) {
		this.blastResistance = resistance * 3.0F;
		return this;
	}

	@Deprecated
	public boolean method_11575(BlockState state) {
		return state.getMaterial().blocksMovement() && state.method_11730();
	}

	@Deprecated
	public boolean method_11576(BlockState state) {
		return state.getMaterial().isOpaque() && state.method_11730() && !state.emitsRedstonePower();
	}

	public boolean isLeafBlock() {
		return this.material.blocksMovement() && this.getDefaultState().method_11730();
	}

	@Deprecated
	public boolean method_11562(BlockState state) {
		return true;
	}

	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return !this.material.blocksMovement();
	}

	@Deprecated
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	public boolean method_8638(BlockView blockView, BlockPos blockPos) {
		return false;
	}

	protected Block setStrength(float strength) {
		this.hardness = strength;
		if (this.blastResistance < strength * 5.0F) {
			this.blastResistance = strength * 5.0F;
		}

		return this;
	}

	protected Block setUnbreakable() {
		this.setStrength(-1.0F);
		return this;
	}

	@Deprecated
	public float getHardness(BlockState blockState, World world, BlockPos blockPos) {
		return this.hardness;
	}

	protected Block setTickRandomly(boolean tickRandomly) {
		this.randomTicks = tickRandomly;
		return this;
	}

	public boolean ticksRandomly() {
		return this.randomTicks;
	}

	public boolean hasBlockEntity() {
		return this.blockEntity;
	}

	@Deprecated
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return collisionBox;
	}

	@Deprecated
	public int method_11564(BlockState state, BlockView view, BlockPos pos) {
		int i = view.getLight(pos, state.getLuminance());
		if (i == 0 && state.getBlock() instanceof SlabBlock) {
			pos = pos.down();
			state = view.getBlockState(pos);
			return view.getLight(pos, state.getLuminance());
		} else {
			return i;
		}
	}

	@Deprecated
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		Box box = state.getCollisionBox(view, pos);
		switch (direction) {
			case DOWN:
				if (box.minY > 0.0) {
					return true;
				}
				break;
			case UP:
				if (box.maxY < 1.0) {
					return true;
				}
				break;
			case NORTH:
				if (box.minZ > 0.0) {
					return true;
				}
				break;
			case SOUTH:
				if (box.maxZ < 1.0) {
					return true;
				}
				break;
			case WEST:
				if (box.minX > 0.0) {
					return true;
				}
				break;
			case EAST:
				if (box.maxX < 1.0) {
					return true;
				}
		}

		return !view.getBlockState(pos.offset(direction)).isFullBoundsCubeForCulling();
	}

	public boolean hasCollision(BlockView blockView, BlockPos pos, Direction direction) {
		return blockView.getBlockState(pos).getMaterial().isSolid();
	}

	@Deprecated
	public Box method_11563(BlockState blockState, World world, BlockPos blockPos) {
		return blockState.getCollisionBox((BlockView)world, blockPos).offset(blockPos);
	}

	@Deprecated
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity) {
		appendCollisionBoxes(pos, entityBox, boxes, state.getCollisionBox(world, pos));
	}

	protected static void appendCollisionBoxes(BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Box boxToAdd) {
		if (boxToAdd != EMPTY_BOX) {
			Box box = boxToAdd.offset(pos);
			if (entityBox.intersects(box)) {
				boxes.add(box);
			}
		}
	}

	@Deprecated
	@Nullable
	public Box getCollisionBox(BlockState state, World world, BlockPos pos) {
		return state.getCollisionBox((BlockView)world, pos);
	}

	@Deprecated
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return true;
	}

	public boolean canCollide(BlockState state, boolean bl) {
		return this.hasCollision();
	}

	public boolean hasCollision() {
		return true;
	}

	public void onRandomTick(World world, BlockPos pos, BlockState state, Random rand) {
		this.onScheduledTick(world, pos, state, rand);
	}

	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
	}

	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
	}

	public void onBreakByPlayer(World world, BlockPos pos, BlockState state) {
	}

	@Deprecated
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
	}

	public int getTickRate(World world) {
		return 10;
	}

	public void onCreation(World world, BlockPos pos, BlockState state) {
	}

	public void onBreaking(World world, BlockPos pos, BlockState state) {
	}

	public int getDropCount(Random rand) {
		return 1;
	}

	@Nullable
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(this);
	}

	@Deprecated
	public float method_11557(BlockState blockState, PlayerEntity playerEntity, World world, BlockPos blockPos) {
		float f = blockState.getHardness(world, blockPos);
		if (f < 0.0F) {
			return 0.0F;
		} else {
			return !playerEntity.method_13265(blockState) ? playerEntity.method_13261(blockState) / f / 100.0F : playerEntity.method_13261(blockState) / f / 30.0F;
		}
	}

	public final void dropAsItem(World world, BlockPos pos, BlockState state, int id) {
		this.randomDropAsItem(world, pos, state, 1.0F, id);
	}

	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		if (!world.isClient) {
			int i = this.getBonusDrops(id, world.random);

			for (int j = 0; j < i; j++) {
				if (!(world.random.nextFloat() > chance)) {
					Item item = this.getDropItem(state, world.random, id);
					if (item != null) {
						onBlockBreak(world, pos, new ItemStack(item, 1, this.getMeta(state)));
					}
				}
			}
		}
	}

	public static void onBlockBreak(World world, BlockPos pos, ItemStack item) {
		if (!world.isClient && world.getGameRules().getBoolean("doTileDrops")) {
			float f = 0.5F;
			double d = (double)(world.random.nextFloat() * f) + (double)(1.0F - f) * 0.5;
			double e = (double)(world.random.nextFloat() * f) + (double)(1.0F - f) * 0.5;
			double g = (double)(world.random.nextFloat() * f) + (double)(1.0F - f) * 0.5;
			ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, item);
			itemEntity.setToDefaultPickupDelay();
			world.spawnEntity(itemEntity);
		}
	}

	protected void dropExperience(World world, BlockPos pos, int size) {
		if (!world.isClient && world.getGameRules().getBoolean("doTileDrops")) {
			while (size > 0) {
				int i = ExperienceOrbEntity.roundToOrbSize(size);
				size -= i;
				world.spawnEntity(new ExperienceOrbEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, i));
			}
		}
	}

	public int getMeta(BlockState state) {
		return 0;
	}

	public float getBlastResistance(Entity entity) {
		return this.blastResistance / 5.0F;
	}

	@Deprecated
	@Nullable
	public BlockHitResult method_414(BlockState blockState, World world, BlockPos blockPos, Vec3d vec3d, Vec3d vec3d2) {
		return this.method_11559(blockPos, vec3d, vec3d2, blockState.getCollisionBox((BlockView)world, blockPos));
	}

	@Nullable
	protected BlockHitResult method_11559(BlockPos blockPos, Vec3d vec3d, Vec3d vec3d2, Box box) {
		Vec3d vec3d3 = vec3d.subtract((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
		Vec3d vec3d4 = vec3d2.subtract((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
		BlockHitResult blockHitResult = box.method_585(vec3d3, vec3d4);
		return blockHitResult == null
			? null
			: new BlockHitResult(blockHitResult.pos.add((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), blockHitResult.direction, blockPos);
	}

	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
	}

	public RenderLayer getRenderLayerType() {
		return RenderLayer.SOLID;
	}

	public boolean canBeReplaced(World world, BlockPos pos, Direction dir, @Nullable ItemStack stack) {
		return this.canBePlacedAdjacent(world, pos, dir);
	}

	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		return this.canBePlacedAtPos(world, pos);
	}

	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock().material.isReplaceable();
	}

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
		return false;
	}

	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
	}

	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.stateFromData(id);
	}

	public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
	}

	public Vec3d onEntityCollision(World world, BlockPos pos, Entity entity, Vec3d velocity) {
		return velocity;
	}

	@Deprecated
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return 0;
	}

	@Deprecated
	public boolean emitsRedstonePower(BlockState state) {
		return false;
	}

	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
	}

	@Deprecated
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return 0;
	}

	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable ItemStack stack) {
		player.incrementStat(Stats.mined(this));
		player.addExhaustion(0.025F);
		if (this.requiresSilkTouch() && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) > 0) {
			ItemStack itemStack = this.createStackFromBlock(state);
			if (itemStack != null) {
				onBlockBreak(world, pos, itemStack);
			}
		} else {
			int i = EnchantmentHelper.getLevel(Enchantments.FORTUNE, stack);
			this.dropAsItem(world, pos, state, i);
		}
	}

	protected boolean requiresSilkTouch() {
		return this.getDefaultState().method_11730() && !this.blockEntity;
	}

	@Nullable
	protected ItemStack createStackFromBlock(BlockState state) {
		Item item = Item.fromBlock(this);
		if (item == null) {
			return null;
		} else {
			int i = 0;
			if (item.isUnbreakable()) {
				i = this.getData(state);
			}

			return new ItemStack(item, 1, i);
		}
	}

	public int getBonusDrops(int id, Random rand) {
		return this.getDropCount(rand);
	}

	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
	}

	public boolean canMobSpawnInside() {
		return !this.material.isSolid() && !this.material.isFluid();
	}

	public Block setTranslationKey(String key) {
		this.translationKey = key;
		return this;
	}

	public String getTranslatedName() {
		return CommonI18n.translate(this.getTranslationKey() + ".name");
	}

	public String getTranslationKey() {
		return "tile." + this.translationKey;
	}

	@Deprecated
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		return false;
	}

	public boolean hasStats() {
		return this.stats;
	}

	protected Block disableStats() {
		this.stats = false;
		return this;
	}

	@Deprecated
	public PistonBehavior getPistonBehavior(BlockState state) {
		return this.material.getPistonBehavior();
	}

	@Deprecated
	public float getAmbientOcclusionLightLevel(BlockState state) {
		return state.method_11733() ? 0.2F : 1.0F;
	}

	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
		entity.handleFallDamage(distance, 1.0F);
	}

	public void setEntityVelocity(World world, Entity entity) {
		entity.velocityY = 0.0;
	}

	@Nullable
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Item.fromBlock(this), 1, this.getMeta(blockState));
	}

	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		stacks.add(new ItemStack(item));
	}

	public ItemGroup getItemGroup() {
		return this.itemGroup;
	}

	public Block setItemGroup(ItemGroup group) {
		this.itemGroup = group;
		return this;
	}

	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
	}

	public void onRainTick(World world, BlockPos pos) {
	}

	public boolean doImmediateUpdates() {
		return true;
	}

	public boolean shouldDropItemsOnExplosion(Explosion explosion) {
		return true;
	}

	public boolean isEqualTo(Block block) {
		return this == block;
	}

	public static boolean areBlocksEqual(Block one, Block two) {
		if (one == null || two == null) {
			return false;
		} else {
			return one == two ? true : one.isEqualTo(two);
		}
	}

	@Deprecated
	public boolean method_11577(BlockState state) {
		return false;
	}

	@Deprecated
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return 0;
	}

	protected StateManager appendProperties() {
		return new StateManager(this);
	}

	public StateManager getStateManager() {
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

	public BlockSoundGroup getSoundGroup() {
		return this.blockSoundGroup;
	}

	public String toString() {
		return "Block{" + REGISTRY.getIdentifier(this) + "}";
	}

	public static void setup() {
		register(0, AIR_ID, new AirBlock().setTranslationKey("air"));
		register(1, "stone", new StoneBlock().setStrength(1.5F).setResistance(10.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("stone"));
		register(2, "grass", new GrassBlock().setStrength(0.6F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("grass"));
		register(3, "dirt", new DirtBlock().setStrength(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12760).setTranslationKey("dirt"));
		Block block = new Block(Material.STONE)
			.setStrength(2.0F)
			.setResistance(10.0F)
			.setBlockSoundGroup(BlockSoundGroup.STONE)
			.setTranslationKey("stonebrick")
			.setItemGroup(ItemGroup.BUILDING_BLOCKS);
		register(4, "cobblestone", block);
		Block block2 = new PlanksBlock().setStrength(2.0F).setResistance(5.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("wood");
		register(5, "planks", block2);
		register(6, "sapling", new SaplingBlock().setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("sapling"));
		register(
			7,
			"bedrock",
			new BedrockBlock(Material.STONE)
				.setUnbreakable()
				.setResistance(6000000.0F)
				.setBlockSoundGroup(BlockSoundGroup.STONE)
				.setTranslationKey("bedrock")
				.disableStats()
				.setItemGroup(ItemGroup.BUILDING_BLOCKS)
		);
		register(8, "flowing_water", new FlowingFluidBlock(Material.WATER).setStrength(100.0F).setOpacity(3).setTranslationKey("water").disableStats());
		register(9, "water", new FluidBlock(Material.WATER).setStrength(100.0F).setOpacity(3).setTranslationKey("water").disableStats());
		register(10, "flowing_lava", new FlowingFluidBlock(Material.LAVA).setStrength(100.0F).setLightLevel(1.0F).setTranslationKey("lava").disableStats());
		register(11, "lava", new FluidBlock(Material.LAVA).setStrength(100.0F).setLightLevel(1.0F).setTranslationKey("lava").disableStats());
		register(12, "sand", new SandBlock().setStrength(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766).setTranslationKey("sand"));
		register(13, "gravel", new GravelBlock().setStrength(0.6F).setBlockSoundGroup(BlockSoundGroup.field_12760).setTranslationKey("gravel"));
		register(14, "gold_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("oreGold"));
		register(15, "iron_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("oreIron"));
		register(16, "coal_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("oreCoal"));
		register(17, "log", new Log1Block().setTranslationKey("log"));
		register(18, "leaves", new Leaves1Block().setTranslationKey("leaves"));
		register(19, "sponge", new SpongeBlock().setStrength(0.6F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("sponge"));
		register(20, "glass", new GlassBlock(Material.GLASS, false).setStrength(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764).setTranslationKey("glass"));
		register(21, "lapis_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("oreLapis"));
		register(
			22,
			"lapis_block",
			new Block(Material.IRON, MaterialColor.LAPIS)
				.setStrength(3.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.STONE)
				.setTranslationKey("blockLapis")
				.setItemGroup(ItemGroup.BUILDING_BLOCKS)
		);
		register(23, "dispenser", new DispenserBlock().setStrength(3.5F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("dispenser"));
		Block block3 = new SandstoneBlock().setBlockSoundGroup(BlockSoundGroup.STONE).setStrength(0.8F).setTranslationKey("sandStone");
		register(24, "sandstone", block3);
		register(25, "noteblock", new NoteBlock().setBlockSoundGroup(BlockSoundGroup.field_12759).setStrength(0.8F).setTranslationKey("musicBlock"));
		register(26, "bed", new BedBlock().setBlockSoundGroup(BlockSoundGroup.field_12759).setStrength(0.2F).setTranslationKey("bed").disableStats());
		register(27, "golden_rail", new PoweredRailBlock().setStrength(0.7F).setBlockSoundGroup(BlockSoundGroup.field_12763).setTranslationKey("goldenRail"));
		register(28, "detector_rail", new DetectorRailBlock().setStrength(0.7F).setBlockSoundGroup(BlockSoundGroup.field_12763).setTranslationKey("detectorRail"));
		register(29, "sticky_piston", new PistonBlock(true).setTranslationKey("pistonStickyBase"));
		register(30, "web", new CobwebBlock().setOpacity(1).setStrength(4.0F).setTranslationKey("web"));
		register(31, "tallgrass", new TallPlantBlock().setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("tallgrass"));
		register(32, "deadbush", new DeadBushBlock().setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("deadbush"));
		register(33, "piston", new PistonBlock(false).setTranslationKey("pistonBase"));
		register(34, "piston_head", new PistonHeadBlock().setTranslationKey("pistonBase"));
		register(35, "wool", new WoolBlock(Material.WOOL).setStrength(0.8F).setBlockSoundGroup(BlockSoundGroup.field_12765).setTranslationKey("cloth"));
		register(36, "piston_extension", new PistonExtensionBlock());
		register(37, "yellow_flower", new YellowFlowerBlock().setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("flower1"));
		register(38, "red_flower", new RedFlowerBlock().setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("flower2"));
		Block block4 = new MushroomPlantBlock().setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12761).setLightLevel(0.125F).setTranslationKey("mushroom");
		register(39, "brown_mushroom", block4);
		Block block5 = new MushroomPlantBlock().setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("mushroom");
		register(40, "red_mushroom", block5);
		register(
			41,
			"gold_block",
			new Block(Material.IRON, MaterialColor.GOLD)
				.setStrength(3.0F)
				.setResistance(10.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12763)
				.setTranslationKey("blockGold")
				.setItemGroup(ItemGroup.BUILDING_BLOCKS)
		);
		register(
			42,
			"iron_block",
			new Block(Material.IRON, MaterialColor.IRON)
				.setStrength(5.0F)
				.setResistance(10.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12763)
				.setTranslationKey("blockIron")
				.setItemGroup(ItemGroup.BUILDING_BLOCKS)
		);
		register(
			43,
			"double_stone_slab",
			new DoubleStoneSlabBlock().setStrength(2.0F).setResistance(10.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("stoneSlab")
		);
		register(
			44, "stone_slab", new SingleStoneSlabBlock().setStrength(2.0F).setResistance(10.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("stoneSlab")
		);
		Block block6 = new Block(Material.STONE, MaterialColor.RED)
			.setStrength(2.0F)
			.setResistance(10.0F)
			.setBlockSoundGroup(BlockSoundGroup.STONE)
			.setTranslationKey("brick")
			.setItemGroup(ItemGroup.BUILDING_BLOCKS);
		register(45, "brick_block", block6);
		register(46, "tnt", new TntBlock().setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("tnt"));
		register(47, "bookshelf", new BookshelfBlock().setStrength(1.5F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("bookshelf"));
		register(
			48,
			"mossy_cobblestone",
			new Block(Material.STONE)
				.setStrength(2.0F)
				.setResistance(10.0F)
				.setBlockSoundGroup(BlockSoundGroup.STONE)
				.setTranslationKey("stoneMoss")
				.setItemGroup(ItemGroup.BUILDING_BLOCKS)
		);
		register(
			49, "obsidian", new ObsidianBlock().setStrength(50.0F).setResistance(2000.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("obsidian")
		);
		register(50, "torch", new TorchBlock().setStrength(0.0F).setLightLevel(0.9375F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("torch"));
		register(
			51, "fire", new FireBlock().setStrength(0.0F).setLightLevel(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12765).setTranslationKey("fire").disableStats()
		);
		register(
			52, "mob_spawner", new MobSpawnerBlock().setStrength(5.0F).setBlockSoundGroup(BlockSoundGroup.field_12763).setTranslationKey("mobSpawner").disableStats()
		);
		register(53, "oak_stairs", new StairsBlock(block2.getDefaultState().with(PlanksBlock.VARIANT, PlanksBlock.WoodType.OAK)).setTranslationKey("stairsWood"));
		register(54, "chest", new ChestBlock(ChestBlock.Type.BASIC).setStrength(2.5F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("chest"));
		register(
			55, "redstone_wire", new RedstoneWireBlock().setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("redstoneDust").disableStats()
		);
		register(56, "diamond_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("oreDiamond"));
		register(
			57,
			"diamond_block",
			new Block(Material.IRON, MaterialColor.DIAMOND)
				.setStrength(5.0F)
				.setResistance(10.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12763)
				.setTranslationKey("blockDiamond")
				.setItemGroup(ItemGroup.BUILDING_BLOCKS)
		);
		register(58, "crafting_table", new CraftingTableBlock().setStrength(2.5F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("workbench"));
		register(59, "wheat", new CropBlock().setTranslationKey("crops"));
		Block block7 = new FarmlandBlock().setStrength(0.6F).setBlockSoundGroup(BlockSoundGroup.field_12760).setTranslationKey("farmland");
		register(60, "farmland", block7);
		register(
			61,
			"furnace",
			new FurnaceBlock(false).setStrength(3.5F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("furnace").setItemGroup(ItemGroup.DECORATIONS)
		);
		register(
			62, "lit_furnace", new FurnaceBlock(true).setStrength(3.5F).setBlockSoundGroup(BlockSoundGroup.STONE).setLightLevel(0.875F).setTranslationKey("furnace")
		);
		register(
			63, "standing_sign", new StandingSignBlock().setStrength(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("sign").disableStats()
		);
		register(
			64,
			"wooden_door",
			new DoorBlock(Material.WOOD).setStrength(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("doorOak").disableStats()
		);
		register(65, "ladder", new LadderBlock().setStrength(0.4F).setBlockSoundGroup(BlockSoundGroup.field_12768).setTranslationKey("ladder"));
		register(66, "rail", new RailBlock().setStrength(0.7F).setBlockSoundGroup(BlockSoundGroup.field_12763).setTranslationKey("rail"));
		register(67, "stone_stairs", new StairsBlock(block.getDefaultState()).setTranslationKey("stairsStone"));
		register(68, "wall_sign", new WallSignBlock().setStrength(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("sign").disableStats());
		register(69, "lever", new LeverBlock().setStrength(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("lever"));
		register(
			70,
			"stone_pressure_plate",
			new PressurePlateBlock(Material.STONE, PressurePlateBlock.ActivationRule.MOBS)
				.setStrength(0.5F)
				.setBlockSoundGroup(BlockSoundGroup.STONE)
				.setTranslationKey("pressurePlateStone")
		);
		register(
			71, "iron_door", new DoorBlock(Material.IRON).setStrength(5.0F).setBlockSoundGroup(BlockSoundGroup.field_12763).setTranslationKey("doorIron").disableStats()
		);
		register(
			72,
			"wooden_pressure_plate",
			new PressurePlateBlock(Material.WOOD, PressurePlateBlock.ActivationRule.ALL)
				.setStrength(0.5F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("pressurePlateWood")
		);
		register(
			73,
			"redstone_ore",
			new RedstoneOreBlock(false)
				.setStrength(3.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.STONE)
				.setTranslationKey("oreRedstone")
				.setItemGroup(ItemGroup.BUILDING_BLOCKS)
		);
		register(
			74,
			"lit_redstone_ore",
			new RedstoneOreBlock(true)
				.setLightLevel(0.625F)
				.setStrength(3.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.STONE)
				.setTranslationKey("oreRedstone")
		);
		register(
			75, "unlit_redstone_torch", new RedstoneTorchBlock(false).setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("notGate")
		);
		register(
			76,
			"redstone_torch",
			new RedstoneTorchBlock(true)
				.setStrength(0.0F)
				.setLightLevel(0.5F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("notGate")
				.setItemGroup(ItemGroup.REDSTONE)
		);
		register(77, "stone_button", new StoneButtonBlock().setStrength(0.5F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("button"));
		register(78, "snow_layer", new SnowLayerBlock().setStrength(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12767).setTranslationKey("snow").setOpacity(0));
		register(79, "ice", new IceBlock().setStrength(0.5F).setOpacity(3).setBlockSoundGroup(BlockSoundGroup.field_12764).setTranslationKey("ice"));
		register(80, "snow", new SnowBlock().setStrength(0.2F).setBlockSoundGroup(BlockSoundGroup.field_12767).setTranslationKey("snow"));
		register(81, "cactus", new CactusBlock().setStrength(0.4F).setBlockSoundGroup(BlockSoundGroup.field_12765).setTranslationKey("cactus"));
		register(82, "clay", new ClayBlock().setStrength(0.6F).setBlockSoundGroup(BlockSoundGroup.field_12760).setTranslationKey("clay"));
		register(83, "reeds", new SugarCaneBlock().setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("reeds").disableStats());
		register(84, "jukebox", new JukeboxBlock().setStrength(2.0F).setResistance(10.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("jukebox"));
		register(
			85,
			"fence",
			new FenceBlock(Material.WOOD, PlanksBlock.WoodType.OAK.getMaterialColor())
				.setStrength(2.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("fence")
		);
		Block block8 = new PumpkinBlock().setStrength(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("pumpkin");
		register(86, "pumpkin", block8);
		register(87, "netherrack", new NetherrackBlock().setStrength(0.4F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("hellrock"));
		register(88, "soul_sand", new SoulSandBlock().setStrength(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12766).setTranslationKey("hellsand"));
		register(
			89,
			"glowstone",
			new GlowstoneBlock(Material.GLASS).setStrength(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764).setLightLevel(1.0F).setTranslationKey("lightgem")
		);
		register(
			90, "portal", new NetherPortalBlock().setStrength(-1.0F).setBlockSoundGroup(BlockSoundGroup.field_12764).setLightLevel(0.75F).setTranslationKey("portal")
		);
		register(
			91, "lit_pumpkin", new PumpkinBlock().setStrength(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setLightLevel(1.0F).setTranslationKey("litpumpkin")
		);
		register(92, "cake", new CakeBlock().setStrength(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12765).setTranslationKey("cake").disableStats());
		register(
			93,
			"unpowered_repeater",
			new RepeaterBlock(false).setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("diode").disableStats()
		);
		register(
			94, "powered_repeater", new RepeaterBlock(true).setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("diode").disableStats()
		);
		register(
			95,
			"stained_glass",
			new StainedGlassBlock(Material.GLASS).setStrength(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764).setTranslationKey("stainedGlass")
		);
		register(
			96,
			"trapdoor",
			new TrapdoorBlock(Material.WOOD).setStrength(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("trapdoor").disableStats()
		);
		register(97, "monster_egg", new InfestedBlock().setStrength(0.75F).setTranslationKey("monsterStoneEgg"));
		Block block9 = new StoneBrickBlock().setStrength(1.5F).setResistance(10.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("stonebricksmooth");
		register(98, "stonebrick", block9);
		register(
			99,
			"brown_mushroom_block",
			new MushroomBlock(Material.WOOD, MaterialColor.DIRT, block4).setStrength(0.2F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("mushroom")
		);
		register(
			100,
			"red_mushroom_block",
			new MushroomBlock(Material.WOOD, MaterialColor.RED, block5).setStrength(0.2F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("mushroom")
		);
		register(
			101,
			"iron_bars",
			new PaneBlock(Material.IRON, true).setStrength(5.0F).setResistance(10.0F).setBlockSoundGroup(BlockSoundGroup.field_12763).setTranslationKey("fenceIron")
		);
		register(
			102, "glass_pane", new PaneBlock(Material.GLASS, false).setStrength(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764).setTranslationKey("thinGlass")
		);
		Block block10 = new MelonBlock().setStrength(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("melon");
		register(103, "melon_block", block10);
		register(104, "pumpkin_stem", new StemBlock(block8).setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("pumpkinStem"));
		register(105, "melon_stem", new StemBlock(block10).setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("pumpkinStem"));
		register(106, "vine", new VineBlock().setStrength(0.2F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("vine"));
		register(
			107,
			"fence_gate",
			new FenceGateBlock(PlanksBlock.WoodType.OAK)
				.setStrength(2.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("fenceGate")
		);
		register(108, "brick_stairs", new StairsBlock(block6.getDefaultState()).setTranslationKey("stairsBrick"));
		register(
			109,
			"stone_brick_stairs",
			new StairsBlock(block9.getDefaultState().with(StoneBrickBlock.VARIANT, StoneBrickBlock.Type.DEFAULT)).setTranslationKey("stairsStoneBrickSmooth")
		);
		register(110, "mycelium", new MyceliumBlock().setStrength(0.6F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("mycel"));
		register(111, "waterlily", new LilyPadBlock().setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("waterlily"));
		Block block11 = new NetherBrickBlock()
			.setStrength(2.0F)
			.setResistance(10.0F)
			.setBlockSoundGroup(BlockSoundGroup.STONE)
			.setTranslationKey("netherBrick")
			.setItemGroup(ItemGroup.BUILDING_BLOCKS);
		register(112, "nether_brick", block11);
		register(
			113,
			"nether_brick_fence",
			new FenceBlock(Material.STONE, MaterialColor.NETHER)
				.setStrength(2.0F)
				.setResistance(10.0F)
				.setBlockSoundGroup(BlockSoundGroup.STONE)
				.setTranslationKey("netherFence")
		);
		register(114, "nether_brick_stairs", new StairsBlock(block11.getDefaultState()).setTranslationKey("stairsNetherBrick"));
		register(115, "nether_wart", new NetherWartBlock().setTranslationKey("netherStalk"));
		register(116, "enchanting_table", new EnchantingTableBlock().setStrength(5.0F).setResistance(2000.0F).setTranslationKey("enchantmentTable"));
		register(117, "brewing_stand", new BrewingStandBlock().setStrength(0.5F).setLightLevel(0.125F).setTranslationKey("brewingStand"));
		register(118, "cauldron", new CauldronBlock().setStrength(2.0F).setTranslationKey("cauldron"));
		register(119, "end_portal", new EndPortalBlock(Material.PORTAL).setStrength(-1.0F).setResistance(6000000.0F));
		register(
			120,
			"end_portal_frame",
			new EndPortalFrameBlock()
				.setBlockSoundGroup(BlockSoundGroup.field_12764)
				.setLightLevel(0.125F)
				.setStrength(-1.0F)
				.setTranslationKey("endPortalFrame")
				.setResistance(6000000.0F)
				.setItemGroup(ItemGroup.DECORATIONS)
		);
		register(
			121,
			"end_stone",
			new Block(Material.STONE, MaterialColor.SAND)
				.setStrength(3.0F)
				.setResistance(15.0F)
				.setBlockSoundGroup(BlockSoundGroup.STONE)
				.setTranslationKey("whiteStone")
				.setItemGroup(ItemGroup.BUILDING_BLOCKS)
		);
		register(
			122,
			"dragon_egg",
			new DragonEggBlock().setStrength(3.0F).setResistance(15.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setLightLevel(0.125F).setTranslationKey("dragonEgg")
		);
		register(
			123,
			"redstone_lamp",
			new RedstoneLampBlock(false)
				.setStrength(0.3F)
				.setBlockSoundGroup(BlockSoundGroup.field_12764)
				.setTranslationKey("redstoneLight")
				.setItemGroup(ItemGroup.REDSTONE)
		);
		register(
			124, "lit_redstone_lamp", new RedstoneLampBlock(true).setStrength(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764).setTranslationKey("redstoneLight")
		);
		register(
			125,
			"double_wooden_slab",
			new DoubleWoodSlabBlock().setStrength(2.0F).setResistance(5.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("woodSlab")
		);
		register(
			126,
			"wooden_slab",
			new SingleWoodSlabBlock().setStrength(2.0F).setResistance(5.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("woodSlab")
		);
		register(127, "cocoa", new CocoaBlock().setStrength(0.2F).setResistance(5.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("cocoa"));
		register(
			128,
			"sandstone_stairs",
			new StairsBlock(block3.getDefaultState().with(SandstoneBlock.VARIANT, SandstoneBlock.SandstoneType.SMOOTH)).setTranslationKey("stairsSandStone")
		);
		register(129, "emerald_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("oreEmerald"));
		register(
			130,
			"ender_chest",
			new EnderChestBlock()
				.setStrength(22.5F)
				.setResistance(1000.0F)
				.setBlockSoundGroup(BlockSoundGroup.STONE)
				.setTranslationKey("enderChest")
				.setLightLevel(0.5F)
		);
		register(131, "tripwire_hook", new TripwireHookBlock().setTranslationKey("tripWireSource"));
		register(132, "tripwire", new TripwireBlock().setTranslationKey("tripWire"));
		register(
			133,
			"emerald_block",
			new Block(Material.IRON, MaterialColor.EMERALD)
				.setStrength(5.0F)
				.setResistance(10.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12763)
				.setTranslationKey("blockEmerald")
				.setItemGroup(ItemGroup.BUILDING_BLOCKS)
		);
		register(
			134, "spruce_stairs", new StairsBlock(block2.getDefaultState().with(PlanksBlock.VARIANT, PlanksBlock.WoodType.SPRUCE)).setTranslationKey("stairsWoodSpruce")
		);
		register(
			135, "birch_stairs", new StairsBlock(block2.getDefaultState().with(PlanksBlock.VARIANT, PlanksBlock.WoodType.BIRCH)).setTranslationKey("stairsWoodBirch")
		);
		register(
			136, "jungle_stairs", new StairsBlock(block2.getDefaultState().with(PlanksBlock.VARIANT, PlanksBlock.WoodType.JUNGLE)).setTranslationKey("stairsWoodJungle")
		);
		register(137, "command_block", new CommandBlock(MaterialColor.BROWN).setUnbreakable().setResistance(6000000.0F).setTranslationKey("commandBlock"));
		register(138, "beacon", new BeaconBlock().setTranslationKey("beacon").setLightLevel(1.0F));
		register(139, "cobblestone_wall", new WallBlock(block).setTranslationKey("cobbleWall"));
		register(140, "flower_pot", new FlowerPotBlock().setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("flowerPot"));
		register(141, "carrots", new CarrotsBlock().setTranslationKey("carrots"));
		register(142, "potatoes", new PotatoesBlock().setTranslationKey("potatoes"));
		register(143, "wooden_button", new WoodButtonBlock().setStrength(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("button"));
		register(144, "skull", new SkullBlock().setStrength(1.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("skull"));
		register(145, "anvil", new AnvilBlock().setStrength(5.0F).setBlockSoundGroup(BlockSoundGroup.field_12769).setResistance(2000.0F).setTranslationKey("anvil"));
		register(
			146, "trapped_chest", new ChestBlock(ChestBlock.Type.TRAP).setStrength(2.5F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("chestTrap")
		);
		register(
			147,
			"light_weighted_pressure_plate",
			new WeightedPressurePlateBlock(Material.IRON, 15, MaterialColor.GOLD)
				.setStrength(0.5F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("weightedPlate_light")
		);
		register(
			148,
			"heavy_weighted_pressure_plate",
			new WeightedPressurePlateBlock(Material.IRON, 150)
				.setStrength(0.5F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("weightedPlate_heavy")
		);
		register(
			149,
			"unpowered_comparator",
			new ComparatorBlock(false).setStrength(0.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("comparator").disableStats()
		);
		register(
			150,
			"powered_comparator",
			new ComparatorBlock(true)
				.setStrength(0.0F)
				.setLightLevel(0.625F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("comparator")
				.disableStats()
		);
		register(151, "daylight_detector", new DaylightDetectorBlock(false));
		register(
			152,
			"redstone_block",
			new RedstoneBlock(Material.IRON, MaterialColor.LAVA)
				.setStrength(5.0F)
				.setResistance(10.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12763)
				.setTranslationKey("blockRedstone")
				.setItemGroup(ItemGroup.REDSTONE)
		);
		register(
			153,
			"quartz_ore",
			new OreBlock(MaterialColor.NETHER).setStrength(3.0F).setResistance(5.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("netherquartz")
		);
		register(154, "hopper", new HopperBlock().setStrength(3.0F).setResistance(8.0F).setBlockSoundGroup(BlockSoundGroup.field_12763).setTranslationKey("hopper"));
		Block block12 = new QuartzBlock().setBlockSoundGroup(BlockSoundGroup.STONE).setStrength(0.8F).setTranslationKey("quartzBlock");
		register(155, "quartz_block", block12);
		register(
			156, "quartz_stairs", new StairsBlock(block12.getDefaultState().with(QuartzBlock.VARIANT, QuartzBlock.QuartzType.DEFAULT)).setTranslationKey("stairsQuartz")
		);
		register(157, "activator_rail", new PoweredRailBlock().setStrength(0.7F).setBlockSoundGroup(BlockSoundGroup.field_12763).setTranslationKey("activatorRail"));
		register(158, "dropper", new DropperBlock().setStrength(3.5F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("dropper"));
		register(
			159,
			"stained_hardened_clay",
			new WoolBlock(Material.STONE).setStrength(1.25F).setResistance(7.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("clayHardenedStained")
		);
		register(
			160,
			"stained_glass_pane",
			new StainedGlassPaneBlock().setStrength(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764).setTranslationKey("thinStainedGlass")
		);
		register(161, "leaves2", new Leaves2Block().setTranslationKey("leaves"));
		register(162, "log2", new Log2Block().setTranslationKey("log"));
		register(
			163, "acacia_stairs", new StairsBlock(block2.getDefaultState().with(PlanksBlock.VARIANT, PlanksBlock.WoodType.ACACIA)).setTranslationKey("stairsWoodAcacia")
		);
		register(
			164,
			"dark_oak_stairs",
			new StairsBlock(block2.getDefaultState().with(PlanksBlock.VARIANT, PlanksBlock.WoodType.DARK_OAK)).setTranslationKey("stairsWoodDarkOak")
		);
		register(165, "slime", new SlimeBlock().setTranslationKey("slime").setBlockSoundGroup(BlockSoundGroup.field_12770));
		register(166, "barrier", new BarrierBlock().setTranslationKey("barrier"));
		register(
			167,
			"iron_trapdoor",
			new TrapdoorBlock(Material.IRON).setStrength(5.0F).setBlockSoundGroup(BlockSoundGroup.field_12763).setTranslationKey("ironTrapdoor").disableStats()
		);
		register(
			168, "prismarine", new PrismarineBlock().setStrength(1.5F).setResistance(10.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("prismarine")
		);
		register(
			169,
			"sea_lantern",
			new SeaLanternBlock(Material.GLASS).setStrength(0.3F).setBlockSoundGroup(BlockSoundGroup.field_12764).setLightLevel(1.0F).setTranslationKey("seaLantern")
		);
		register(
			170,
			"hay_block",
			new HayBlock().setStrength(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("hayBlock").setItemGroup(ItemGroup.BUILDING_BLOCKS)
		);
		register(171, "carpet", new CarpetBlock().setStrength(0.1F).setBlockSoundGroup(BlockSoundGroup.field_12765).setTranslationKey("woolCarpet").setOpacity(0));
		register(
			172,
			"hardened_clay",
			new HardenedClayBlock().setStrength(1.25F).setResistance(7.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("clayHardened")
		);
		register(
			173,
			"coal_block",
			new Block(Material.STONE, MaterialColor.BLACK)
				.setStrength(5.0F)
				.setResistance(10.0F)
				.setBlockSoundGroup(BlockSoundGroup.STONE)
				.setTranslationKey("blockCoal")
				.setItemGroup(ItemGroup.BUILDING_BLOCKS)
		);
		register(174, "packed_ice", new PackedIceBlock().setStrength(0.5F).setBlockSoundGroup(BlockSoundGroup.field_12764).setTranslationKey("icePacked"));
		register(175, "double_plant", new DoublePlantBlock());
		register(
			176,
			"standing_banner",
			new BannerBlock.StandingBannerBlock().setStrength(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("banner").disableStats()
		);
		register(
			177,
			"wall_banner",
			new BannerBlock.WallBannerBlock().setStrength(1.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("banner").disableStats()
		);
		register(178, "daylight_detector_inverted", new DaylightDetectorBlock(true));
		Block block13 = new RedSandstoneBlock().setBlockSoundGroup(BlockSoundGroup.STONE).setStrength(0.8F).setTranslationKey("redSandStone");
		register(179, "red_sandstone", block13);
		register(
			180,
			"red_sandstone_stairs",
			new StairsBlock(block13.getDefaultState().with(RedSandstoneBlock.TYPE, RedSandstoneBlock.RedSandstoneType.SMOOTH)).setTranslationKey("stairsRedSandStone")
		);
		register(
			181,
			"double_stone_slab2",
			new DoubleRedSandstoneSlabBlock().setStrength(2.0F).setResistance(10.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("stoneSlab2")
		);
		register(
			182,
			"stone_slab2",
			new SingleRedSandstoneSlabBlock().setStrength(2.0F).setResistance(10.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("stoneSlab2")
		);
		register(
			183,
			"spruce_fence_gate",
			new FenceGateBlock(PlanksBlock.WoodType.SPRUCE)
				.setStrength(2.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("spruceFenceGate")
		);
		register(
			184,
			"birch_fence_gate",
			new FenceGateBlock(PlanksBlock.WoodType.BIRCH)
				.setStrength(2.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("birchFenceGate")
		);
		register(
			185,
			"jungle_fence_gate",
			new FenceGateBlock(PlanksBlock.WoodType.JUNGLE)
				.setStrength(2.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("jungleFenceGate")
		);
		register(
			186,
			"dark_oak_fence_gate",
			new FenceGateBlock(PlanksBlock.WoodType.DARK_OAK)
				.setStrength(2.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("darkOakFenceGate")
		);
		register(
			187,
			"acacia_fence_gate",
			new FenceGateBlock(PlanksBlock.WoodType.ACACIA)
				.setStrength(2.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("acaciaFenceGate")
		);
		register(
			188,
			"spruce_fence",
			new FenceBlock(Material.WOOD, PlanksBlock.WoodType.SPRUCE.getMaterialColor())
				.setStrength(2.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("spruceFence")
		);
		register(
			189,
			"birch_fence",
			new FenceBlock(Material.WOOD, PlanksBlock.WoodType.BIRCH.getMaterialColor())
				.setStrength(2.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("birchFence")
		);
		register(
			190,
			"jungle_fence",
			new FenceBlock(Material.WOOD, PlanksBlock.WoodType.JUNGLE.getMaterialColor())
				.setStrength(2.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("jungleFence")
		);
		register(
			191,
			"dark_oak_fence",
			new FenceBlock(Material.WOOD, PlanksBlock.WoodType.DARK_OAK.getMaterialColor())
				.setStrength(2.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("darkOakFence")
		);
		register(
			192,
			"acacia_fence",
			new FenceBlock(Material.WOOD, PlanksBlock.WoodType.ACACIA.getMaterialColor())
				.setStrength(2.0F)
				.setResistance(5.0F)
				.setBlockSoundGroup(BlockSoundGroup.field_12759)
				.setTranslationKey("acaciaFence")
		);
		register(
			193,
			"spruce_door",
			new DoorBlock(Material.WOOD).setStrength(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("doorSpruce").disableStats()
		);
		register(
			194,
			"birch_door",
			new DoorBlock(Material.WOOD).setStrength(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("doorBirch").disableStats()
		);
		register(
			195,
			"jungle_door",
			new DoorBlock(Material.WOOD).setStrength(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("doorJungle").disableStats()
		);
		register(
			196,
			"acacia_door",
			new DoorBlock(Material.WOOD).setStrength(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("doorAcacia").disableStats()
		);
		register(
			197,
			"dark_oak_door",
			new DoorBlock(Material.WOOD).setStrength(3.0F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("doorDarkOak").disableStats()
		);
		register(
			198, "end_rod", new EndRodBlock().setStrength(0.0F).setLightLevel(0.9375F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("endRod")
		);
		register(199, "chorus_plant", new ChorusPlantBlock().setStrength(0.4F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("chorusPlant"));
		register(200, "chorus_flower", new ChorusFlowerBlock().setStrength(0.4F).setBlockSoundGroup(BlockSoundGroup.field_12759).setTranslationKey("chorusFlower"));
		Block block14 = new Block(Material.STONE)
			.setStrength(1.5F)
			.setResistance(10.0F)
			.setBlockSoundGroup(BlockSoundGroup.STONE)
			.setItemGroup(ItemGroup.BUILDING_BLOCKS)
			.setTranslationKey("purpurBlock");
		register(201, "purpur_block", block14);
		register(
			202,
			"purpur_pillar",
			new PillarBlock(Material.STONE)
				.setStrength(1.5F)
				.setResistance(10.0F)
				.setBlockSoundGroup(BlockSoundGroup.STONE)
				.setItemGroup(ItemGroup.BUILDING_BLOCKS)
				.setTranslationKey("purpurPillar")
		);
		register(203, "purpur_stairs", new StairsBlock(block14.getDefaultState()).setTranslationKey("stairsPurpur"));
		register(
			204,
			"purpur_double_slab",
			new PurpurSlab.Double().setStrength(2.0F).setResistance(10.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("purpurSlab")
		);
		register(
			205, "purpur_slab", new PurpurSlab.Single().setStrength(2.0F).setResistance(10.0F).setBlockSoundGroup(BlockSoundGroup.STONE).setTranslationKey("purpurSlab")
		);
		register(
			206,
			"end_bricks",
			new Block(Material.STONE).setBlockSoundGroup(BlockSoundGroup.STONE).setStrength(0.8F).setItemGroup(ItemGroup.BUILDING_BLOCKS).setTranslationKey("endBricks")
		);
		register(207, "beetroots", new BeetrootsBlock().setTranslationKey("beetroots"));
		Block block15 = new GrassPathBlock().setStrength(0.65F).setBlockSoundGroup(BlockSoundGroup.field_12761).setTranslationKey("grassPath").disableStats();
		register(208, "grass_path", block15);
		register(209, "end_gateway", new EndGatewayBlock(Material.PORTAL).setStrength(-1.0F).setResistance(6000000.0F));
		register(
			210, "repeating_command_block", new CommandBlock(MaterialColor.PURPLE).setUnbreakable().setResistance(6000000.0F).setTranslationKey("repeatingCommandBlock")
		);
		register(211, "chain_command_block", new CommandBlock(MaterialColor.GREEN).setUnbreakable().setResistance(6000000.0F).setTranslationKey("chainCommandBlock"));
		register(
			212, "frosted_ice", new FrostedIceBlock().setStrength(0.5F).setOpacity(3).setBlockSoundGroup(BlockSoundGroup.field_12764).setTranslationKey("frostedIce")
		);
		register(255, "structure_block", new StructureBlock().setUnbreakable().setResistance(6000000.0F).setTranslationKey("structureBlock").setLightLevel(1.0F));
		REGISTRY.validate();

		for (Block block16 : REGISTRY) {
			if (block16.material == Material.AIR) {
				block16.useNeighbourLight = false;
			} else {
				boolean bl = false;
				boolean bl2 = block16 instanceof StairsBlock;
				boolean bl3 = block16 instanceof SlabBlock;
				boolean bl4 = block16 == block7 || block16 == block15;
				boolean bl5 = block16.translucent;
				boolean bl6 = block16.opacity == 0;
				if (bl2 || bl3 || bl4 || bl5 || bl6) {
					bl = true;
				}

				block16.useNeighbourLight = bl;
			}
		}

		Set<Block> set = Sets.newHashSet(new Block[]{REGISTRY.get(new Identifier("tripwire"))});

		for (Block block17 : REGISTRY) {
			if (set.contains(block17)) {
				for (int i = 0; i < 15; i++) {
					int j = REGISTRY.getRawId(block17) << 4 | i;
					BLOCK_STATES.set(block17.stateFromData(i), j);
				}
			} else {
				for (BlockState blockState : block17.getStateManager().getBlockStates()) {
					int k = REGISTRY.getRawId(block17) << 4 | block17.getData(blockState);
					BLOCK_STATES.set(blockState, k);
				}
			}
		}
	}

	private static void register(int id, Identifier identifier, Block block) {
		REGISTRY.add(id, identifier, block);
	}

	private static void register(int id, String name, Block block) {
		register(id, new Identifier(name), block);
	}

	public static enum OffsetType {
		NONE,
		XZ,
		XYZ;
	}
}
