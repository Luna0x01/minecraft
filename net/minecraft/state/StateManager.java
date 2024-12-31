package net.minecraft.state;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.CollectionBuilders;
import net.minecraft.util.collection.MapUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StateManager {
	private static final Pattern VALID_PROPERTY_NAME = Pattern.compile("^[a-z0-9_]+$");
	private static final Function<Property<?>, String> PROPERTY_STRING_FUNCTION = new Function<Property<?>, String>() {
		@Nullable
		public String apply(@Nullable Property<?> property) {
			return property == null ? "<NULL>" : property.getName();
		}
	};
	private final Block parentBlock;
	private final ImmutableSortedMap<String, Property<?>> properties;
	private final ImmutableList<BlockState> states;

	public StateManager(Block block, Property<?>... propertys) {
		this.parentBlock = block;
		Map<String, Property<?>> map = Maps.newHashMap();

		for (Property<?> property : propertys) {
			verify(block, property);
			map.put(property.getName(), property);
		}

		this.properties = ImmutableSortedMap.copyOf(map);
		Map<Map<Property<?>, Comparable<?>>, StateManager.BlockStateImpl> map2 = Maps.newLinkedHashMap();
		List<StateManager.BlockStateImpl> list = Lists.newArrayList();

		for (List<Comparable<?>> list2 : CollectionBuilders.method_10516(this.method_11743())) {
			Map<Property<?>, Comparable<?>> map3 = MapUtil.createMap(this.properties.values(), list2);
			StateManager.BlockStateImpl blockStateImpl = new StateManager.BlockStateImpl(block, ImmutableMap.copyOf(map3));
			map2.put(map3, blockStateImpl);
			list.add(blockStateImpl);
		}

		for (StateManager.BlockStateImpl blockStateImpl2 : list) {
			blockStateImpl2.method_9036(map2);
		}

		this.states = ImmutableList.copyOf(list);
	}

	public static <T extends Comparable<T>> String verify(Block block, Property<T> property) {
		String string = property.getName();
		if (!VALID_PROPERTY_NAME.matcher(string).matches()) {
			throw new IllegalArgumentException("Block: " + block.getClass() + " has invalidly named property: " + string);
		} else {
			for (T comparable : property.getValues()) {
				String string2 = property.name(comparable);
				if (!VALID_PROPERTY_NAME.matcher(string2).matches()) {
					throw new IllegalArgumentException("Block: " + block.getClass() + " has property: " + string + " with invalidly named value: " + string2);
				}
			}

			return string;
		}
	}

	public ImmutableList<BlockState> getBlockStates() {
		return this.states;
	}

	private List<Iterable<Comparable<?>>> method_11743() {
		List<Iterable<Comparable<?>>> list = Lists.newArrayList();
		ImmutableCollection<Property<?>> immutableCollection = this.properties.values();
		UnmodifiableIterator var3 = immutableCollection.iterator();

		while (var3.hasNext()) {
			Property<?> property = (Property<?>)var3.next();
			list.add(property.getValues());
		}

		return list;
	}

	public BlockState getDefaultState() {
		return (BlockState)this.states.get(0);
	}

	public Block getBlock() {
		return this.parentBlock;
	}

	public Collection<Property<?>> getProperties() {
		return this.properties.values();
	}

	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("block", Block.REGISTRY.getIdentifier(this.parentBlock))
			.add("properties", Iterables.transform(this.properties.values(), PROPERTY_STRING_FUNCTION))
			.toString();
	}

	@Nullable
	public Property<?> getProperty(String name) {
		return (Property<?>)this.properties.get(name);
	}

	static class BlockStateImpl extends AbstractBlockState {
		private final Block block;
		private final ImmutableMap<Property<?>, Comparable<?>> map;
		private ImmutableTable<Property<?>, Comparable<?>, BlockState> table;

		private BlockStateImpl(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
			this.block = block;
			this.map = immutableMap;
		}

		@Override
		public Collection<Property<?>> getProperties() {
			return Collections.unmodifiableCollection(this.map.keySet());
		}

		@Override
		public <T extends Comparable<T>> T get(Property<T> property) {
			Comparable<?> comparable = (Comparable<?>)this.map.get(property);
			if (comparable == null) {
				throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.block.getStateManager());
			} else {
				return (T)property.getType().cast(comparable);
			}
		}

		@Override
		public <T extends Comparable<T>, V extends T> BlockState with(Property<T> property, V comparable) {
			Comparable<?> comparable2 = (Comparable<?>)this.map.get(property);
			if (comparable2 == null) {
				throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.block.getStateManager());
			} else if (comparable2 == comparable) {
				return this;
			} else {
				BlockState blockState = (BlockState)this.table.get(property, comparable);
				if (blockState == null) {
					throw new IllegalArgumentException(
						"Cannot set property " + property + " to " + comparable + " on block " + Block.REGISTRY.getIdentifier(this.block) + ", it is not an allowed value"
					);
				} else {
					return blockState;
				}
			}
		}

		@Override
		public ImmutableMap<Property<?>, Comparable<?>> getPropertyMap() {
			return this.map;
		}

		@Override
		public Block getBlock() {
			return this.block;
		}

		public boolean equals(Object object) {
			return this == object;
		}

		public int hashCode() {
			return this.map.hashCode();
		}

		public void method_9036(Map<Map<Property<?>, Comparable<?>>, StateManager.BlockStateImpl> map) {
			if (this.table != null) {
				throw new IllegalStateException();
			} else {
				Table<Property<?>, Comparable<?>, BlockState> table = HashBasedTable.create();
				UnmodifiableIterator var3 = this.map.entrySet().iterator();

				while (var3.hasNext()) {
					Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var3.next();
					Property<?> property = (Property<?>)entry.getKey();

					for (Comparable<?> comparable : property.getValues()) {
						if (comparable != entry.getValue()) {
							table.put(property, comparable, map.get(this.method_9037(property, comparable)));
						}
					}
				}

				this.table = ImmutableTable.copyOf(table);
			}
		}

		private Map<Property<?>, Comparable<?>> method_9037(Property<?> property, Comparable<?> comparable) {
			Map<Property<?>, Comparable<?>> map = Maps.newHashMap(this.map);
			map.put(property, comparable);
			return map;
		}

		@Override
		public Material getMaterial() {
			return this.block.getMaterial(this);
		}

		@Override
		public boolean isFullBlock() {
			return this.block.isFullBlock(this);
		}

		@Override
		public boolean method_13361(Entity entity) {
			return this.block.method_13315(this, entity);
		}

		@Override
		public int getOpacity() {
			return this.block.getOpacity(this);
		}

		@Override
		public int getLuminance() {
			return this.block.getLuminance(this);
		}

		@Override
		public boolean isTranslucent() {
			return this.block.isTranslucent(this);
		}

		@Override
		public boolean useNeighbourLight() {
			return this.block.useNeighbourLight(this);
		}

		@Override
		public MaterialColor getMaterialColor(BlockView view, BlockPos pos) {
			return this.block.getMaterialColor(this, view, pos);
		}

		@Override
		public BlockState withRotation(BlockRotation rotation) {
			return this.block.withRotation(this, rotation);
		}

		@Override
		public BlockState withMirror(BlockMirror mirror) {
			return this.block.withMirror(this, mirror);
		}

		@Override
		public boolean method_11730() {
			return this.block.method_11562(this);
		}

		@Override
		public boolean method_13762() {
			return this.block.method_13704(this);
		}

		@Override
		public BlockRenderType getRenderType() {
			return this.block.getRenderType(this);
		}

		@Override
		public int method_11712(BlockView view, BlockPos pos) {
			return this.block.method_11564(this, view, pos);
		}

		@Override
		public float getAmbientOcclusionLightLevel() {
			return this.block.getAmbientOcclusionLightLevel(this);
		}

		@Override
		public boolean method_11733() {
			return this.block.method_11575(this);
		}

		@Override
		public boolean method_11734() {
			return this.block.method_11576(this);
		}

		@Override
		public boolean emitsRedstonePower() {
			return this.block.emitsRedstonePower(this);
		}

		@Override
		public int getWeakRedstonePower(BlockView view, BlockPos pos, Direction direction) {
			return this.block.getWeakRedstonePower(this, view, pos, direction);
		}

		@Override
		public boolean method_11736() {
			return this.block.method_11577(this);
		}

		@Override
		public int getComparatorOutput(World world, BlockPos pos) {
			return this.block.getComparatorOutput(this, world, pos);
		}

		@Override
		public float getHardness(World world, BlockPos pos) {
			return this.block.getHardness(this, world, pos);
		}

		@Override
		public float method_11716(PlayerEntity player, World world, BlockPos pos) {
			return this.block.method_11557(this, player, world, pos);
		}

		@Override
		public int getStrongRedstonePower(BlockView view, BlockPos pos, Direction direction) {
			return this.block.getStrongRedstonePower(this, view, pos, direction);
		}

		@Override
		public PistonBehavior getPistonBehavior() {
			return this.block.getPistonBehavior(this);
		}

		@Override
		public BlockState getBlockState(BlockView view, BlockPos pos) {
			return this.block.getBlockState(this, view, pos);
		}

		@Override
		public Box method_11722(World world, BlockPos pos) {
			return this.block.method_11563(this, world, pos);
		}

		@Override
		public boolean method_11724(BlockView view, BlockPos pos, Direction direction) {
			return this.block.method_8654(this, view, pos, direction);
		}

		@Override
		public boolean isFullBoundsCubeForCulling() {
			return this.block.isFullBoundsCubeForCulling(this);
		}

		@Nullable
		@Override
		public Box method_11726(BlockView view, BlockPos pos) {
			return this.block.method_8640(this, view, pos);
		}

		@Override
		public void appendCollisionBoxes(World world, BlockPos pos, Box box, List<Box> boxes, @Nullable Entity entity, boolean isActualState) {
			this.block.appendCollisionBoxes(this, world, pos, box, boxes, entity, isActualState);
		}

		@Override
		public Box getCollisionBox(BlockView view, BlockPos pos) {
			return this.block.getCollisionBox(this, view, pos);
		}

		@Override
		public BlockHitResult method_11711(World world, BlockPos pos, Vec3d vec3d, Vec3d vec3d2) {
			return this.block.method_414(this, world, pos, vec3d, vec3d2);
		}

		@Override
		public boolean method_11739() {
			return this.block.method_11568(this);
		}

		@Override
		public Vec3d method_13761(BlockView view, BlockPos pos) {
			return this.block.method_13702(this, view, pos);
		}

		@Override
		public boolean onSyncedBlockEvent(World world, BlockPos pos, int type, int data) {
			return this.block.onSyncedBlockEvent(this, world, pos, type, data);
		}

		@Override
		public void neighbourUpdate(World world, BlockPos pos, Block block, BlockPos sourcePos) {
			this.block.neighborUpdate(this, world, pos, block, sourcePos);
		}

		@Override
		public boolean method_13763() {
			return this.block.method_13703(this);
		}

		@Override
		public BlockRenderLayer getRenderLayer(BlockView view, BlockPos pos, Direction direction) {
			return this.block.getRenderLayer(view, this, pos, direction);
		}
	}
}
