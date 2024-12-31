package net.minecraft.world.chunk;

import java.io.IOException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.class_3781;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.WorldSaveException;

public interface ChunkStorage {
	@Nullable
	Chunk method_17186(IWorld iWorld, int i, int j, Consumer<Chunk> consumer) throws IOException;

	@Nullable
	ChunkBlockStateStorage method_17187(IWorld iWorld, int i, int j, Consumer<class_3781> consumer) throws IOException;

	void method_17185(World world, class_3781 arg) throws IOException, WorldSaveException;

	void save();
}
