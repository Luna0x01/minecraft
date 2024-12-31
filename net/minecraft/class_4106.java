package net.minecraft;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class class_4106 {
	private final ClientPlayNetworkHandler field_19905;
	private int field_19906 = -1;
	@Nullable
	private Consumer<NbtCompound> field_19907;

	public class_4106(ClientPlayNetworkHandler clientPlayNetworkHandler) {
		this.field_19905 = clientPlayNetworkHandler;
	}

	public boolean method_18149(int i, @Nullable NbtCompound nbtCompound) {
		if (this.field_19906 == i && this.field_19907 != null) {
			this.field_19907.accept(nbtCompound);
			this.field_19907 = null;
			return true;
		} else {
			return false;
		}
	}

	private int method_18152(Consumer<NbtCompound> consumer) {
		this.field_19907 = consumer;
		return ++this.field_19906;
	}

	public void method_18150(int i, Consumer<NbtCompound> consumer) {
		int j = this.method_18152(consumer);
		this.field_19905.sendPacket(new class_4386(j, i));
	}

	public void method_18151(BlockPos blockPos, Consumer<NbtCompound> consumer) {
		int i = this.method_18152(consumer);
		this.field_19905.sendPacket(new class_4384(i, blockPos));
	}
}
