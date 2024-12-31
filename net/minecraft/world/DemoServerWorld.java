package net.minecraft.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

public class DemoServerWorld extends ServerWorld {
	private static final long SEED = (long)"North Carolina".hashCode();
	public static final LevelInfo INFO = new LevelInfo(SEED, LevelInfo.GameMode.SURVIVAL, true, false, LevelGeneratorType.DEFAULT).setBonusChest();

	public DemoServerWorld(MinecraftServer minecraftServer, SaveHandler saveHandler, LevelProperties levelProperties, int i, Profiler profiler) {
		super(minecraftServer, saveHandler, levelProperties, i, profiler);
		this.levelProperties.copyFrom(INFO);
	}
}
