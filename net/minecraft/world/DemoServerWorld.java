package net.minecraft.world;

import net.minecraft.class_4070;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

public class DemoServerWorld extends ServerWorld {
	private static final long SEED = (long)"North Carolina".hashCode();
	public static final LevelInfo INFO = new LevelInfo(SEED, GameMode.SURVIVAL, true, false, LevelGeneratorType.DEFAULT).setBonusChest();

	public DemoServerWorld(
		MinecraftServer minecraftServer, SaveHandler saveHandler, class_4070 arg, LevelProperties levelProperties, DimensionType dimensionType, Profiler profiler
	) {
		super(minecraftServer, saveHandler, arg, levelProperties, dimensionType, profiler);
		this.levelProperties.copyFrom(INFO);
	}
}
