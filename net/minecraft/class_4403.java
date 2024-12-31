package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class class_4403 {
	private final MinecraftServer field_21679;
	private final Map<Identifier, class_4402> field_21680 = Maps.newHashMap();

	public class_4403(MinecraftServer minecraftServer) {
		this.field_21679 = minecraftServer;
	}

	@Nullable
	public class_4402 method_20479(Identifier identifier) {
		return (class_4402)this.field_21680.get(identifier);
	}

	public class_4402 method_20480(Identifier identifier, Text text) {
		class_4402 lv = new class_4402(identifier, text);
		this.field_21680.put(identifier, lv);
		return lv;
	}

	public void method_20481(class_4402 arg) {
		this.field_21680.remove(arg.method_20464());
	}

	public Collection<Identifier> method_20477() {
		return this.field_21680.keySet();
	}

	public Collection<class_4402> method_20483() {
		return this.field_21680.values();
	}

	public NbtCompound method_20485() {
		NbtCompound nbtCompound = new NbtCompound();

		for (class_4402 lv : this.field_21680.values()) {
			nbtCompound.put(lv.method_20464().toString(), lv.method_20476());
		}

		return nbtCompound;
	}

	public void method_20478(NbtCompound nbtCompound) {
		for (String string : nbtCompound.getKeys()) {
			Identifier identifier = new Identifier(string);
			this.field_21680.put(identifier, class_4402.method_20466(nbtCompound.getCompound(string), identifier));
		}
	}

	public void method_20482(ServerPlayerEntity serverPlayerEntity) {
		for (class_4402 lv : this.field_21680.values()) {
			lv.method_20472(serverPlayerEntity);
		}
	}

	public void method_20484(ServerPlayerEntity serverPlayerEntity) {
		for (class_4402 lv : this.field_21680.values()) {
			lv.method_20474(serverPlayerEntity);
		}
	}
}
