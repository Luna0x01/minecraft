package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.itemgroup.ItemGroup;

public class GlassBlock extends TransparentBlock {
	public GlassBlock(Material material, boolean bl) {
		super(material, bl);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}
}
