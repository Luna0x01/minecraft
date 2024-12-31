package net.minecraft.client.color.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.ReplaceableTallPlantBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.state.property.Property;
import net.minecraft.util.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

public class BlockColors {
	private final IdList<BlockColorProvider> providers = new IdList<>(32);
	private final Map<Block, Set<Property<?>>> properties = Maps.newHashMap();

	public static BlockColors create() {
		BlockColors blockColors = new BlockColors();
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> blockRenderView != null && blockPos != null
					? BiomeColors.getGrassColor(blockRenderView, blockState.get(ReplaceableTallPlantBlock.HALF) == DoubleBlockHalf.field_12609 ? blockPos.down() : blockPos)
					: -1,
			Blocks.field_10313,
			Blocks.field_10214
		);
		blockColors.registerColorProperty(ReplaceableTallPlantBlock.HALF, Blocks.field_10313, Blocks.field_10214);
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> blockRenderView != null && blockPos != null
					? BiomeColors.getGrassColor(blockRenderView, blockPos)
					: GrassColors.getColor(0.5, 1.0),
			Blocks.field_10219,
			Blocks.field_10112,
			Blocks.field_10479,
			Blocks.field_10128
		);
		blockColors.registerColorProvider((blockState, blockRenderView, blockPos, i) -> FoliageColors.getSpruceColor(), Blocks.field_9988);
		blockColors.registerColorProvider((blockState, blockRenderView, blockPos, i) -> FoliageColors.getBirchColor(), Blocks.field_10539);
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> blockRenderView != null && blockPos != null
					? BiomeColors.getFoliageColor(blockRenderView, blockPos)
					: FoliageColors.getDefaultColor(),
			Blocks.field_10503,
			Blocks.field_10335,
			Blocks.field_10098,
			Blocks.field_10035,
			Blocks.field_10597
		);
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> blockRenderView != null && blockPos != null ? BiomeColors.getWaterColor(blockRenderView, blockPos) : -1,
			Blocks.field_10382,
			Blocks.field_10422,
			Blocks.field_10593
		);
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> RedstoneWireBlock.getWireColor((Integer)blockState.get(RedstoneWireBlock.POWER)), Blocks.field_10091
		);
		blockColors.registerColorProperty(RedstoneWireBlock.POWER, Blocks.field_10091);
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> blockRenderView != null && blockPos != null ? BiomeColors.getGrassColor(blockRenderView, blockPos) : -1,
			Blocks.field_10424
		);
		blockColors.registerColorProvider((blockState, blockRenderView, blockPos, i) -> 14731036, Blocks.field_10150, Blocks.field_10331);
		blockColors.registerColorProvider((blockState, blockRenderView, blockPos, i) -> {
			int j = (Integer)blockState.get(StemBlock.AGE);
			int k = j * 32;
			int l = 255 - j * 8;
			int m = j * 4;
			return k << 16 | l << 8 | m;
		}, Blocks.field_10168, Blocks.field_9984);
		blockColors.registerColorProperty(StemBlock.AGE, Blocks.field_10168, Blocks.field_9984);
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> blockRenderView != null && blockPos != null ? 2129968 : 7455580, Blocks.field_10588
		);
		return blockColors;
	}

	public int getColor(BlockState blockState, World world, BlockPos blockPos) {
		BlockColorProvider blockColorProvider = this.providers.get(Registry.field_11146.getRawId(blockState.getBlock()));
		if (blockColorProvider != null) {
			return blockColorProvider.getColor(blockState, null, null, 0);
		} else {
			MaterialColor materialColor = blockState.getTopMaterialColor(world, blockPos);
			return materialColor != null ? materialColor.color : -1;
		}
	}

	public int getColor(BlockState blockState, @Nullable BlockRenderView blockRenderView, @Nullable BlockPos blockPos, int i) {
		BlockColorProvider blockColorProvider = this.providers.get(Registry.field_11146.getRawId(blockState.getBlock()));
		return blockColorProvider == null ? -1 : blockColorProvider.getColor(blockState, blockRenderView, blockPos, i);
	}

	public void registerColorProvider(BlockColorProvider blockColorProvider, Block... blocks) {
		for (Block block : blocks) {
			this.providers.set(blockColorProvider, Registry.field_11146.getRawId(block));
		}
	}

	private void registerColorProperties(Set<Property<?>> set, Block... blocks) {
		for (Block block : blocks) {
			this.properties.put(block, set);
		}
	}

	private void registerColorProperty(Property<?> property, Block... blocks) {
		this.registerColorProperties(ImmutableSet.of(property), blocks);
	}

	public Set<Property<?>> getProperties(Block block) {
		return (Set<Property<?>>)this.properties.getOrDefault(block, ImmutableSet.of());
	}
}
