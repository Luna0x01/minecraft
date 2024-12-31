package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;

public class BoneBlock extends PillarBlock {
	public BoneBlock() {
		super(Material.STONE, MaterialColor.SAND);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
		this.setStrength(2.0F);
		this.setBlockSoundGroup(BlockSoundGroup.STONE);
	}
}
