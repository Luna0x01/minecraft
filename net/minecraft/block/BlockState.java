package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import java.util.Random;
import net.minecraft.class_3600;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.PropertyContainer;
import net.minecraft.tag.Tag;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public interface BlockState extends PropertyContainer<BlockState> {
	ThreadLocal<Object2ByteMap<BlockState>> field_18689 = ThreadLocal.withInitial(() -> {
		Object2ByteOpenHashMap<BlockState> object2ByteOpenHashMap = new Object2ByteOpenHashMap();
		object2ByteOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteOpenHashMap;
	});
	ThreadLocal<Object2ByteMap<BlockState>> field_18690 = ThreadLocal.withInitial(() -> {
		Object2ByteOpenHashMap<BlockState> object2ByteOpenHashMap = new Object2ByteOpenHashMap();
		object2ByteOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteOpenHashMap;
	});
	ThreadLocal<Object2ByteMap<BlockState>> field_18691 = ThreadLocal.withInitial(() -> {
		Object2ByteOpenHashMap<BlockState> object2ByteOpenHashMap = new Object2ByteOpenHashMap();
		object2ByteOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteOpenHashMap;
	});

	Block getBlock();

	default Material getMaterial() {
		return this.getBlock().getMaterial(this);
	}

	default boolean method_16859(Entity entity) {
		return this.getBlock().method_13315(this, entity);
	}

	default boolean isTranslucent(BlockView blockView, BlockPos blockPos) {
		Block block = this.getBlock();
		Object2ByteMap<BlockState> object2ByteMap = block.hasStats() ? null : (Object2ByteMap)field_18689.get();
		if (object2ByteMap != null) {
			byte b = object2ByteMap.getByte(this);
			if (b != object2ByteMap.defaultReturnValue()) {
				return b != 0;
			}
		}

		boolean bl = block.isTranslucent(this, blockView, blockPos);
		if (object2ByteMap != null) {
			object2ByteMap.put(this, (byte)(bl ? 1 : 0));
		}

		return bl;
	}

	default int method_16885(BlockView blockView, BlockPos blockPos) {
		Block block = this.getBlock();
		Object2ByteMap<BlockState> object2ByteMap = block.hasStats() ? null : (Object2ByteMap)field_18690.get();
		if (object2ByteMap != null) {
			byte b = object2ByteMap.getByte(this);
			if (b != object2ByteMap.defaultReturnValue()) {
				return b;
			}
		}

		int i = block.getLightSubtracted(this, blockView, blockPos);
		if (object2ByteMap != null) {
			object2ByteMap.put(this, (byte)Math.min(i, blockView.getMaxLightLevel()));
		}

		return i;
	}

	default int getLuminance() {
		return this.getBlock().getLuminance(this);
	}

	default boolean isAir() {
		return this.getBlock().isAir(this);
	}

	default boolean method_16889(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_16599(this, blockView, blockPos);
	}

	default MaterialColor getMaterialColor(BlockView world, BlockPos pos) {
		return this.getBlock().getMaterialColor(this, world, pos);
	}

	default BlockState rotate(BlockRotation rotation) {
		return this.getBlock().withRotation(this, rotation);
	}

	default BlockState mirror(BlockMirror mirror) {
		return this.getBlock().withMirror(this, mirror);
	}

	default boolean method_16897() {
		return this.getBlock().method_11562(this);
	}

	default boolean method_16899() {
		return this.getBlock().method_13704(this);
	}

	default BlockRenderType getRenderType() {
		return this.getBlock().getRenderType(this);
	}

	default int method_16878(class_3600 arg, BlockPos blockPos) {
		return this.getBlock().method_11564(this, arg, blockPos);
	}

	default float getAmbientOcclusionLightLevel() {
		return this.getBlock().getAmbientOcclusionLightLevel(this);
	}

	default boolean method_16905() {
		return this.getBlock().method_11575(this);
	}

	default boolean method_16907() {
		return this.getBlock().method_11576(this);
	}

	default boolean emitsRedstonePower() {
		return this.getBlock().emitsRedstonePower(this);
	}

	default int getWeakRedstonePower(BlockView world, BlockPos pos, Direction direction) {
		return this.getBlock().getWeakRedstonePower(this, world, pos, direction);
	}

	default boolean method_16910() {
		return this.getBlock().method_11577(this);
	}

	default int getComparatorOutput(World world, BlockPos pos) {
		return this.getBlock().getComparatorOutput(this, world, pos);
	}

	default float getHardness(BlockView world, BlockPos pos) {
		return this.getBlock().getHardness(this, world, pos);
	}

	default float method_16860(PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_16566(this, playerEntity, blockView, blockPos);
	}

	default int getStrongRedstonePower(BlockView world, BlockPos pos, Direction direction) {
		return this.getBlock().getStrongRedstonePower(this, world, pos, direction);
	}

	default PistonBehavior getPistonBehavior() {
		return this.getBlock().getPistonBehavior(this);
	}

	default boolean isFullOpaque(BlockView world, BlockPos pos) {
		Block block = this.getBlock();
		Object2ByteMap<BlockState> object2ByteMap = block.hasStats() ? null : (Object2ByteMap)field_18691.get();
		if (object2ByteMap != null) {
			byte b = object2ByteMap.getByte(this);
			if (b != object2ByteMap.defaultReturnValue()) {
				return b != 0;
			}
		}

		boolean bl = block.method_16596(this, world, pos);
		if (object2ByteMap != null) {
			object2ByteMap.put(this, (byte)(bl ? 1 : 0));
		}

		return bl;
	}

	default boolean isFullBoundsCubeForCulling() {
		return this.getBlock().isFullBoundsCubeForCulling(this);
	}

	default boolean method_16881(BlockState blockState, Direction direction) {
		return this.getBlock().method_16573(this, blockState, direction);
	}

	default VoxelShape getOutlineShape(BlockView world, BlockPos pos) {
		return this.getBlock().getOutlineShape(this, world, pos);
	}

	default VoxelShape getCollisionShape(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().getCollisionShape(this, blockView, blockPos);
	}

	default VoxelShape method_16902(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_16593(this, blockView, blockPos);
	}

	default VoxelShape getRayTraceShape(BlockView world, BlockPos pos) {
		return this.getBlock().getRayTraceShape(this, world, pos);
	}

	default boolean method_16913() {
		return this.getBlock().method_11568(this);
	}

	default Vec3d getOffsetPos(BlockView world, BlockPos pos) {
		return this.getBlock().getOffsetPos(this, world, pos);
	}

	default boolean method_16868(World world, BlockPos blockPos, int i, int j) {
		return this.getBlock().onSyncedBlockEvent(this, world, blockPos, i, j);
	}

	default void neighborUpdate(World world, BlockPos pos, Block block, BlockPos neighborPos) {
		this.getBlock().neighborUpdate(this, world, pos, block, neighborPos);
	}

	default void method_16876(IWorld iWorld, BlockPos blockPos, int i) {
		this.getBlock().method_16569(this, iWorld, blockPos, i);
	}

	default void method_16888(IWorld iWorld, BlockPos blockPos, int i) {
		this.getBlock().method_16584(this, iWorld, blockPos, i);
	}

	default void onBlockAdded(World world, BlockPos pos, BlockState state) {
		this.getBlock().onBlockAdded(this, world, pos, state);
	}

	default void onStateReplaced(World world, BlockPos pos, BlockState state, boolean moved) {
		this.getBlock().onStateReplaced(this, world, pos, state, moved);
	}

	default void scheduledTick(World world, BlockPos pos, Random random) {
		this.getBlock().scheduledTick(this, world, pos, random);
	}

	default void method_16887(World world, BlockPos blockPos, Random random) {
		this.getBlock().method_16582(this, world, blockPos, random);
	}

	default void onEntityCollision(World world, BlockPos pos, Entity entity) {
		this.getBlock().onEntityCollision(this, world, pos, entity);
	}

	default void method_16867(World world, BlockPos blockPos, int i) {
		this.method_16866(world, blockPos, 1.0F, i);
	}

	default void method_16866(World world, BlockPos blockPos, float f, int i) {
		this.getBlock().method_410(this, world, blockPos, f, i);
	}

	default boolean onUse(World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ) {
		return this.getBlock().onUse(this, world, pos, player, hand, direction, distanceX, distanceY, distanceZ);
	}

	default void method_16870(World world, BlockPos blockPos, PlayerEntity playerEntity) {
		this.getBlock().method_420(this, world, blockPos, playerEntity);
	}

	default boolean method_16914() {
		return this.getBlock().method_13703(this);
	}

	default BlockRenderLayer getRenderLayer(BlockView world, BlockPos pos, Direction direction) {
		return this.getBlock().getRenderLayer(world, this, pos, direction);
	}

	default BlockState getStateForNeighborUpdate(Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return this.getBlock().getStateForNeighborUpdate(this, direction, neighborState, world, pos, neighborPos);
	}

	default boolean canPlaceAtSide(BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return this.getBlock().canPlaceAtSide(this, world, pos, environment);
	}

	default boolean canReplace(ItemPlacementContext context) {
		return this.getBlock().canReplace(this, context);
	}

	default boolean canPlaceAt(RenderBlockView world, BlockPos pos) {
		return this.getBlock().canPlaceAt(this, world, pos);
	}

	default boolean method_16908(BlockView blockView, BlockPos blockPos) {
		return this.getBlock().method_16592(this, blockView, blockPos);
	}

	default boolean isIn(Tag<Block> tag) {
		return this.getBlock().isIn(tag);
	}

	default FluidState getFluidState() {
		return this.getBlock().getFluidState(this);
	}

	default boolean hasRandomTicks() {
		return this.getBlock().hasRandomTicks(this);
	}

	default long getRenderingSeed(BlockPos pos) {
		return this.getBlock().getRenderingSeed(this, pos);
	}
}
