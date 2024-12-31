package net.minecraft.world.chunk.palette;

import net.minecraft.block.BlockState;

interface PaletteResizeListener {
	int resizePalette(int bitsPerBlock, BlockState state);
}
