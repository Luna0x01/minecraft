package net.minecraft.server.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_3781;
import net.minecraft.class_3798;
import net.minecraft.class_3801;
import net.minecraft.class_3845;
import net.minecraft.class_3902;
import net.minecraft.class_3992;
import net.minecraft.class_4441;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;

public interface ChunkGenerator<C extends class_3798> {
	void method_17016(class_3781 arg);

	void method_17019(class_4441 arg, class_3801.class_3802 arg2);

	void method_17018(class_4441 arg);

	void method_17023(class_4441 arg);

	List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos);

	@Nullable
	BlockPos method_3866(World world, String string, BlockPos blockPos, int i, boolean bl);

	C method_17013();

	int method_17014(World world, boolean bl, boolean bl2);

	boolean method_17015(Biome biome, class_3902<? extends class_3845> arg);

	@Nullable
	class_3845 method_17021(Biome biome, class_3902<? extends class_3845> arg);

	Long2ObjectMap<class_3992> method_17017(class_3902<? extends class_3845> arg);

	Long2ObjectMap<LongSet> method_17022(class_3902<? extends class_3845> arg);

	SingletonBiomeSource method_17020();

	long method_17024();

	int method_17025();

	int method_17026();
}
