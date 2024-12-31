package net.minecraft.client.network;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.packet.QueryBlockNbtC2SPacket;
import net.minecraft.server.network.packet.QueryEntityNbtC2SPacket;
import net.minecraft.util.math.BlockPos;

public class DataQueryHandler {
	private final ClientPlayNetworkHandler networkHandler;
	private int expectedTransactionId = -1;
	@Nullable
	private Consumer<CompoundTag> queryConsumer;

	public DataQueryHandler(ClientPlayNetworkHandler clientPlayNetworkHandler) {
		this.networkHandler = clientPlayNetworkHandler;
	}

	public boolean handleQueryResponse(int i, @Nullable CompoundTag compoundTag) {
		if (this.expectedTransactionId == i && this.queryConsumer != null) {
			this.queryConsumer.accept(compoundTag);
			this.queryConsumer = null;
			return true;
		} else {
			return false;
		}
	}

	private int setNextQueryConsumer(Consumer<CompoundTag> consumer) {
		this.queryConsumer = consumer;
		return ++this.expectedTransactionId;
	}

	public void queryEntityNbt(int i, Consumer<CompoundTag> consumer) {
		int j = this.setNextQueryConsumer(consumer);
		this.networkHandler.sendPacket(new QueryEntityNbtC2SPacket(j, i));
	}

	public void queryBlockNbt(BlockPos blockPos, Consumer<CompoundTag> consumer) {
		int i = this.setNextQueryConsumer(consumer);
		this.networkHandler.sendPacket(new QueryBlockNbtC2SPacket(i, blockPos));
	}
}
