package net.minecraft.world.event.listener;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class SimpleGameEventDispatcher implements GameEventDispatcher {
	private final List<GameEventListener> listeners = Lists.newArrayList();
	private final World world;

	public SimpleGameEventDispatcher(World world) {
		this.world = world;
	}

	@Override
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}

	@Override
	public void addListener(GameEventListener listener) {
		this.listeners.add(listener);
		DebugInfoSender.sendGameEventListener(this.world, listener);
	}

	@Override
	public void removeListener(GameEventListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void dispatch(GameEvent event, @Nullable Entity entity, BlockPos pos) {
		boolean bl = false;

		for (GameEventListener gameEventListener : this.listeners) {
			if (this.dispatchTo(this.world, event, entity, pos, gameEventListener)) {
				bl = true;
			}
		}

		if (bl) {
			DebugInfoSender.sendGameEvent(this.world, event, pos);
		}
	}

	private boolean dispatchTo(World world, GameEvent event, @Nullable Entity entity, BlockPos pos, GameEventListener listener) {
		Optional<BlockPos> optional = listener.getPositionSource().getPos(world);
		if (!optional.isPresent()) {
			return false;
		} else {
			double d = ((BlockPos)optional.get()).getSquaredDistance(pos, false);
			int i = listener.getRange() * listener.getRange();
			return d <= (double)i && listener.listen(world, event, entity, pos);
		}
	}
}
