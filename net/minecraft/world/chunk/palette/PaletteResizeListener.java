package net.minecraft.world.chunk.palette;

interface PaletteResizeListener<T> {
	int onResize(int i, T object);
}
