package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.AdvancementUpdatePacket;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3295 {
	private static final Logger field_16126 = LogManager.getLogger();
	private final MinecraftClient field_16127;
	private final class_3328 field_16128 = new class_3328();
	private final Map<SimpleAdvancement, AdvancementProgress> field_16129 = Maps.newHashMap();
	@Nullable
	private class_3295.class_3296 field_16130;
	@Nullable
	private SimpleAdvancement field_16131;

	public class_3295(MinecraftClient minecraftClient) {
		this.field_16127 = minecraftClient;
	}

	public void onProgressUpdate(AdvancementUpdatePacket advancementUpdatePacket) {
		if (advancementUpdatePacket.method_14855()) {
			this.field_16128.method_14809();
			this.field_16129.clear();
		}

		this.field_16128.method_14813(advancementUpdatePacket.getUpdatedAdvancementIdentifiers());
		this.field_16128.method_14812(advancementUpdatePacket.getTasks());

		for (Entry<Identifier, AdvancementProgress> entry : advancementUpdatePacket.getAdvancementProgresses().entrySet()) {
			SimpleAdvancement simpleAdvancement = this.field_16128.method_14814((Identifier)entry.getKey());
			if (simpleAdvancement != null) {
				AdvancementProgress advancementProgress = (AdvancementProgress)entry.getValue();
				advancementProgress.method_14836(simpleAdvancement.getCriteria(), simpleAdvancement.getRequirements());
				this.field_16129.put(simpleAdvancement, advancementProgress);
				if (this.field_16130 != null) {
					this.field_16130.method_14668(simpleAdvancement, advancementProgress);
				}

				if (!advancementUpdatePacket.method_14855()
					&& advancementProgress.method_14833()
					&& simpleAdvancement.getDisplay() != null
					&& simpleAdvancement.getDisplay().method_15014()) {
					this.field_16127.method_14462().method_14491(new class_3258(simpleAdvancement));
				}
			} else {
				field_16126.warn("Server informed client about progress for unknown advancement {}", entry.getKey());
			}
		}
	}

	public class_3328 method_14664() {
		return this.field_16128;
	}

	public void method_14666(@Nullable SimpleAdvancement advancement, boolean bl) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.field_16127.getNetworkHandler();
		if (clientPlayNetworkHandler != null && advancement != null && bl) {
			clientPlayNetworkHandler.sendPacket(AdvancementTabC2SPacket.openedTab(advancement));
		}

		if (this.field_16131 != advancement) {
			this.field_16131 = advancement;
			if (this.field_16130 != null) {
				this.field_16130.method_14669(advancement);
			}
		}
	}

	public void method_14665(@Nullable class_3295.class_3296 arg) {
		this.field_16130 = arg;
		this.field_16128.method_14811(arg);
		if (arg != null) {
			for (Entry<SimpleAdvancement, AdvancementProgress> entry : this.field_16129.entrySet()) {
				arg.method_14668((SimpleAdvancement)entry.getKey(), (AdvancementProgress)entry.getValue());
			}

			arg.method_14669(this.field_16131);
		}
	}

	public interface class_3296 extends class_3328.class_3329 {
		void method_14668(SimpleAdvancement advancement, AdvancementProgress progress);

		void method_14669(@Nullable SimpleAdvancement advancement);
	}
}
