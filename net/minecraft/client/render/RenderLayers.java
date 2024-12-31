package net.minecraft.client.render;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

public class RenderLayers {
	private static final Map<Block, RenderLayer> BLOCKS = Util.make(Maps.newHashMap(), hashMap -> {
		RenderLayer renderLayer = RenderLayer.getCutoutMipped();
		hashMap.put(Blocks.field_10219, renderLayer);
		hashMap.put(Blocks.field_10576, renderLayer);
		hashMap.put(Blocks.field_10285, renderLayer);
		hashMap.put(Blocks.field_10348, renderLayer);
		hashMap.put(Blocks.field_10312, renderLayer);
		hashMap.put(Blocks.field_10335, renderLayer);
		hashMap.put(Blocks.field_10503, renderLayer);
		hashMap.put(Blocks.field_9988, renderLayer);
		hashMap.put(Blocks.field_10098, renderLayer);
		hashMap.put(Blocks.field_10539, renderLayer);
		hashMap.put(Blocks.field_10035, renderLayer);
		RenderLayer renderLayer2 = RenderLayer.getCutout();
		hashMap.put(Blocks.field_10394, renderLayer2);
		hashMap.put(Blocks.field_10217, renderLayer2);
		hashMap.put(Blocks.field_10575, renderLayer2);
		hashMap.put(Blocks.field_10276, renderLayer2);
		hashMap.put(Blocks.field_10385, renderLayer2);
		hashMap.put(Blocks.field_10160, renderLayer2);
		hashMap.put(Blocks.field_10033, renderLayer2);
		hashMap.put(Blocks.field_10120, renderLayer2);
		hashMap.put(Blocks.field_10410, renderLayer2);
		hashMap.put(Blocks.field_10230, renderLayer2);
		hashMap.put(Blocks.field_10621, renderLayer2);
		hashMap.put(Blocks.field_10356, renderLayer2);
		hashMap.put(Blocks.field_10180, renderLayer2);
		hashMap.put(Blocks.field_10610, renderLayer2);
		hashMap.put(Blocks.field_10141, renderLayer2);
		hashMap.put(Blocks.field_10326, renderLayer2);
		hashMap.put(Blocks.field_10109, renderLayer2);
		hashMap.put(Blocks.field_10019, renderLayer2);
		hashMap.put(Blocks.field_10527, renderLayer2);
		hashMap.put(Blocks.field_10288, renderLayer2);
		hashMap.put(Blocks.field_10561, renderLayer2);
		hashMap.put(Blocks.field_10069, renderLayer2);
		hashMap.put(Blocks.field_10461, renderLayer2);
		hashMap.put(Blocks.field_10425, renderLayer2);
		hashMap.put(Blocks.field_10025, renderLayer2);
		hashMap.put(Blocks.field_10343, renderLayer2);
		hashMap.put(Blocks.field_10479, renderLayer2);
		hashMap.put(Blocks.field_10112, renderLayer2);
		hashMap.put(Blocks.field_10428, renderLayer2);
		hashMap.put(Blocks.field_10376, renderLayer2);
		hashMap.put(Blocks.field_10238, renderLayer2);
		hashMap.put(Blocks.field_10182, renderLayer2);
		hashMap.put(Blocks.field_10449, renderLayer2);
		hashMap.put(Blocks.field_10086, renderLayer2);
		hashMap.put(Blocks.field_10226, renderLayer2);
		hashMap.put(Blocks.field_10573, renderLayer2);
		hashMap.put(Blocks.field_10270, renderLayer2);
		hashMap.put(Blocks.field_10048, renderLayer2);
		hashMap.put(Blocks.field_10156, renderLayer2);
		hashMap.put(Blocks.field_10315, renderLayer2);
		hashMap.put(Blocks.field_10554, renderLayer2);
		hashMap.put(Blocks.field_9995, renderLayer2);
		hashMap.put(Blocks.field_10606, renderLayer2);
		hashMap.put(Blocks.field_10548, renderLayer2);
		hashMap.put(Blocks.field_10251, renderLayer2);
		hashMap.put(Blocks.field_10559, renderLayer2);
		hashMap.put(Blocks.field_10336, renderLayer2);
		hashMap.put(Blocks.field_10099, renderLayer2);
		hashMap.put(Blocks.field_10036, renderLayer2);
		hashMap.put(Blocks.field_10260, renderLayer2);
		hashMap.put(Blocks.field_10091, renderLayer2);
		hashMap.put(Blocks.field_10293, renderLayer2);
		hashMap.put(Blocks.field_10149, renderLayer2);
		hashMap.put(Blocks.field_9983, renderLayer2);
		hashMap.put(Blocks.field_10167, renderLayer2);
		hashMap.put(Blocks.field_9973, renderLayer2);
		hashMap.put(Blocks.field_10523, renderLayer2);
		hashMap.put(Blocks.field_10301, renderLayer2);
		hashMap.put(Blocks.field_10029, renderLayer2);
		hashMap.put(Blocks.field_10424, renderLayer2);
		hashMap.put(Blocks.field_10450, renderLayer2);
		hashMap.put(Blocks.field_10137, renderLayer2);
		hashMap.put(Blocks.field_10323, renderLayer2);
		hashMap.put(Blocks.field_10486, renderLayer2);
		hashMap.put(Blocks.field_10017, renderLayer2);
		hashMap.put(Blocks.field_10608, renderLayer2);
		hashMap.put(Blocks.field_10246, renderLayer2);
		hashMap.put(Blocks.field_10331, renderLayer2);
		hashMap.put(Blocks.field_10150, renderLayer2);
		hashMap.put(Blocks.field_9984, renderLayer2);
		hashMap.put(Blocks.field_10168, renderLayer2);
		hashMap.put(Blocks.field_10597, renderLayer2);
		hashMap.put(Blocks.field_10588, renderLayer2);
		hashMap.put(Blocks.field_9974, renderLayer2);
		hashMap.put(Blocks.field_10333, renderLayer2);
		hashMap.put(Blocks.field_10302, renderLayer2);
		hashMap.put(Blocks.field_10327, renderLayer2);
		hashMap.put(Blocks.field_10495, renderLayer2);
		hashMap.put(Blocks.field_10468, renderLayer2);
		hashMap.put(Blocks.field_10192, renderLayer2);
		hashMap.put(Blocks.field_10577, renderLayer2);
		hashMap.put(Blocks.field_10304, renderLayer2);
		hashMap.put(Blocks.field_10564, renderLayer2);
		hashMap.put(Blocks.field_10076, renderLayer2);
		hashMap.put(Blocks.field_10128, renderLayer2);
		hashMap.put(Blocks.field_10354, renderLayer2);
		hashMap.put(Blocks.field_10151, renderLayer2);
		hashMap.put(Blocks.field_9981, renderLayer2);
		hashMap.put(Blocks.field_10162, renderLayer2);
		hashMap.put(Blocks.field_10365, renderLayer2);
		hashMap.put(Blocks.field_10598, renderLayer2);
		hashMap.put(Blocks.field_10249, renderLayer2);
		hashMap.put(Blocks.field_10400, renderLayer2);
		hashMap.put(Blocks.field_10061, renderLayer2);
		hashMap.put(Blocks.field_10074, renderLayer2);
		hashMap.put(Blocks.field_10358, renderLayer2);
		hashMap.put(Blocks.field_10273, renderLayer2);
		hashMap.put(Blocks.field_9998, renderLayer2);
		hashMap.put(Blocks.field_10138, renderLayer2);
		hashMap.put(Blocks.field_10324, renderLayer2);
		hashMap.put(Blocks.field_10487, renderLayer2);
		hashMap.put(Blocks.field_10018, renderLayer2);
		hashMap.put(Blocks.field_10609, renderLayer2);
		hashMap.put(Blocks.field_10247, renderLayer2);
		hashMap.put(Blocks.field_10377, renderLayer2);
		hashMap.put(Blocks.field_10546, renderLayer2);
		hashMap.put(Blocks.field_10453, renderLayer2);
		hashMap.put(Blocks.field_10583, renderLayer2);
		hashMap.put(Blocks.field_10378, renderLayer2);
		hashMap.put(Blocks.field_10430, renderLayer2);
		hashMap.put(Blocks.field_10003, renderLayer2);
		hashMap.put(Blocks.field_10214, renderLayer2);
		hashMap.put(Blocks.field_10313, renderLayer2);
		hashMap.put(Blocks.field_10521, renderLayer2);
		hashMap.put(Blocks.field_10352, renderLayer2);
		hashMap.put(Blocks.field_10627, renderLayer2);
		hashMap.put(Blocks.field_10232, renderLayer2);
		hashMap.put(Blocks.field_10403, renderLayer2);
		hashMap.put(Blocks.field_10455, renderLayer2);
		hashMap.put(Blocks.field_10021, renderLayer2);
		hashMap.put(Blocks.field_10528, renderLayer2);
		hashMap.put(Blocks.field_10341, renderLayer2);
		hashMap.put(Blocks.field_9993, renderLayer2);
		hashMap.put(Blocks.field_10463, renderLayer2);
		hashMap.put(Blocks.field_10195, renderLayer2);
		hashMap.put(Blocks.field_10082, renderLayer2);
		hashMap.put(Blocks.field_10572, renderLayer2);
		hashMap.put(Blocks.field_10296, renderLayer2);
		hashMap.put(Blocks.field_10579, renderLayer2);
		hashMap.put(Blocks.field_10032, renderLayer2);
		hashMap.put(Blocks.field_10125, renderLayer2);
		hashMap.put(Blocks.field_10339, renderLayer2);
		hashMap.put(Blocks.field_10134, renderLayer2);
		hashMap.put(Blocks.field_10618, renderLayer2);
		hashMap.put(Blocks.field_10169, renderLayer2);
		hashMap.put(Blocks.field_10448, renderLayer2);
		hashMap.put(Blocks.field_10097, renderLayer2);
		hashMap.put(Blocks.field_10047, renderLayer2);
		hashMap.put(Blocks.field_10568, renderLayer2);
		hashMap.put(Blocks.field_10221, renderLayer2);
		hashMap.put(Blocks.field_10053, renderLayer2);
		hashMap.put(Blocks.field_10079, renderLayer2);
		hashMap.put(Blocks.field_10427, renderLayer2);
		hashMap.put(Blocks.field_10551, renderLayer2);
		hashMap.put(Blocks.field_10005, renderLayer2);
		hashMap.put(Blocks.field_10347, renderLayer2);
		hashMap.put(Blocks.field_10116, renderLayer2);
		hashMap.put(Blocks.field_10094, renderLayer2);
		hashMap.put(Blocks.field_10557, renderLayer2);
		hashMap.put(Blocks.field_10239, renderLayer2);
		hashMap.put(Blocks.field_10584, renderLayer2);
		hashMap.put(Blocks.field_10186, renderLayer2);
		hashMap.put(Blocks.field_10447, renderLayer2);
		hashMap.put(Blocks.field_10498, renderLayer2);
		hashMap.put(Blocks.field_9976, renderLayer2);
		hashMap.put(Blocks.field_10476, renderLayer2);
		hashMap.put(Blocks.field_10502, renderLayer2);
		hashMap.put(Blocks.field_10108, renderLayer2);
		hashMap.put(Blocks.field_10211, renderLayer2);
		hashMap.put(Blocks.field_10586, renderLayer2);
		hashMap.put(Blocks.field_16492, renderLayer2);
		hashMap.put(Blocks.field_16335, renderLayer2);
		hashMap.put(Blocks.field_16541, renderLayer2);
		hashMap.put(Blocks.field_17350, renderLayer2);
		hashMap.put(Blocks.field_16999, renderLayer2);
		RenderLayer renderLayer3 = RenderLayer.getTranslucent();
		hashMap.put(Blocks.field_10295, renderLayer3);
		hashMap.put(Blocks.field_10316, renderLayer3);
		hashMap.put(Blocks.field_10087, renderLayer3);
		hashMap.put(Blocks.field_10227, renderLayer3);
		hashMap.put(Blocks.field_10574, renderLayer3);
		hashMap.put(Blocks.field_10271, renderLayer3);
		hashMap.put(Blocks.field_10049, renderLayer3);
		hashMap.put(Blocks.field_10157, renderLayer3);
		hashMap.put(Blocks.field_10317, renderLayer3);
		hashMap.put(Blocks.field_10555, renderLayer3);
		hashMap.put(Blocks.field_9996, renderLayer3);
		hashMap.put(Blocks.field_10248, renderLayer3);
		hashMap.put(Blocks.field_10399, renderLayer3);
		hashMap.put(Blocks.field_10060, renderLayer3);
		hashMap.put(Blocks.field_10073, renderLayer3);
		hashMap.put(Blocks.field_10357, renderLayer3);
		hashMap.put(Blocks.field_10272, renderLayer3);
		hashMap.put(Blocks.field_9997, renderLayer3);
		hashMap.put(Blocks.field_10589, renderLayer3);
		hashMap.put(Blocks.field_9991, renderLayer3);
		hashMap.put(Blocks.field_10496, renderLayer3);
		hashMap.put(Blocks.field_10469, renderLayer3);
		hashMap.put(Blocks.field_10193, renderLayer3);
		hashMap.put(Blocks.field_10578, renderLayer3);
		hashMap.put(Blocks.field_10305, renderLayer3);
		hashMap.put(Blocks.field_10565, renderLayer3);
		hashMap.put(Blocks.field_10077, renderLayer3);
		hashMap.put(Blocks.field_10129, renderLayer3);
		hashMap.put(Blocks.field_10355, renderLayer3);
		hashMap.put(Blocks.field_10152, renderLayer3);
		hashMap.put(Blocks.field_9982, renderLayer3);
		hashMap.put(Blocks.field_10163, renderLayer3);
		hashMap.put(Blocks.field_10419, renderLayer3);
		hashMap.put(Blocks.field_10118, renderLayer3);
		hashMap.put(Blocks.field_10070, renderLayer3);
		hashMap.put(Blocks.field_10030, renderLayer3);
		hashMap.put(Blocks.field_21211, renderLayer3);
		hashMap.put(Blocks.field_10110, renderLayer3);
		hashMap.put(Blocks.field_10422, renderLayer3);
	});
	private static final Map<Fluid, RenderLayer> FLUIDS = Util.make(Maps.newHashMap(), hashMap -> {
		RenderLayer renderLayer = RenderLayer.getTranslucent();
		hashMap.put(Fluids.FLOWING_WATER, renderLayer);
		hashMap.put(Fluids.WATER, renderLayer);
	});
	private static boolean fancyGraphics;

	public static RenderLayer getBlockLayer(BlockState blockState) {
		Block block = blockState.getBlock();
		if (block instanceof LeavesBlock) {
			return fancyGraphics ? RenderLayer.getCutoutMipped() : RenderLayer.getSolid();
		} else {
			RenderLayer renderLayer = (RenderLayer)BLOCKS.get(block);
			return renderLayer != null ? renderLayer : RenderLayer.getSolid();
		}
	}

	public static RenderLayer getEntityBlockLayer(BlockState blockState) {
		RenderLayer renderLayer = getBlockLayer(blockState);
		return renderLayer == RenderLayer.getTranslucent() ? TexturedRenderLayers.getEntityTranslucent() : TexturedRenderLayers.getEntityCutout();
	}

	public static RenderLayer getItemLayer(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (item instanceof BlockItem) {
			Block block = ((BlockItem)item).getBlock();
			return getEntityBlockLayer(block.getDefaultState());
		} else {
			return TexturedRenderLayers.getEntityTranslucent();
		}
	}

	public static RenderLayer getFluidLayer(FluidState fluidState) {
		RenderLayer renderLayer = (RenderLayer)FLUIDS.get(fluidState.getFluid());
		return renderLayer != null ? renderLayer : RenderLayer.getSolid();
	}

	public static void setFancyGraphics(boolean bl) {
		fancyGraphics = bl;
	}
}
