package net.minecraft.world.event.listener;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

public interface GameEventDispatcher {
	GameEventDispatcher EMPTY = new GameEventDispatcher() {
		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public void addListener(GameEventListener listener) {
		}

		@Override
		public void removeListener(GameEventListener listener) {
		}

		@Override
		public void dispatch(GameEvent event, @Nullable Entity entity, BlockPos pos) {
		}
	};

	boolean isEmpty();

	void addListener(GameEventListener listener);

	void removeListener(GameEventListener listener);

	void dispatch(GameEvent event, @Nullable Entity entity, BlockPos pos);
}
