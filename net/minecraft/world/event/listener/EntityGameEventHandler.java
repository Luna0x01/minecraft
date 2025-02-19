package net.minecraft.world.event.listener;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

public class EntityGameEventHandler {
	private final GameEventListener listener;
	@Nullable
	private ChunkSectionPos sectionPos;

	public EntityGameEventHandler(GameEventListener listener) {
		this.listener = listener;
	}

	public void onEntityRemoval(World world) {
		this.updateDispatcher(world, this.sectionPos, dispatcher -> dispatcher.removeListener(this.listener));
	}

	public void onEntitySetPos(World world) {
		Optional<BlockPos> optional = this.listener.getPositionSource().getPos(world);
		if (optional.isPresent()) {
			long l = ChunkSectionPos.fromBlockPos(((BlockPos)optional.get()).asLong());
			if (this.sectionPos == null || this.sectionPos.asLong() != l) {
				ChunkSectionPos chunkSectionPos = this.sectionPos;
				this.sectionPos = ChunkSectionPos.from(l);
				this.updateDispatcher(world, chunkSectionPos, dispatcher -> dispatcher.removeListener(this.listener));
				this.updateDispatcher(world, this.sectionPos, dispatcher -> dispatcher.addListener(this.listener));
			}
		}
	}

	private void updateDispatcher(World world, @Nullable ChunkSectionPos sectionPos, Consumer<GameEventDispatcher> action) {
		if (sectionPos != null) {
			Chunk chunk = world.getChunk(sectionPos.getSectionX(), sectionPos.getSectionZ(), ChunkStatus.FULL, false);
			if (chunk != null) {
				action.accept(chunk.getGameEventDispatcher(sectionPos.getSectionY()));
			}
		}
	}
}
