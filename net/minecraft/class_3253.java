package net.minecraft;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatMessageType;

public class class_3253 implements class_3252 {
	public static final class_3253 field_15887 = new class_3253();
	private final Narrator field_15888 = Narrator.getNarrator();

	@Override
	public void method_14472(ChatMessageType chatMessageType, Text text) {
		int i = MinecraftClient.getInstance().options.field_15879;
		if (i != 0 && this.field_15888.active()) {
			if (i == 1 || i == 2 && chatMessageType == ChatMessageType.CHAT || i == 3 && chatMessageType == ChatMessageType.SYSTEM) {
				if (text instanceof TranslatableText && "chat.type.text".equals(((TranslatableText)text).getKey())) {
					this.field_15888.say(new TranslatableText("chat.type.text.narrate", ((TranslatableText)text).getArgs()).getString());
				} else {
					this.field_15888.say(text.getString());
				}
			}
		}
	}

	public void method_14474(int i) {
		this.field_15888.clear();
		this.field_15888.say(new TranslatableText("options.narrator").getString() + " : " + new TranslatableText(GameOptions.field_15883[i]).getString());
		class_3264 lv = MinecraftClient.getInstance().method_14462();
		if (this.field_15888.active()) {
			if (i == 0) {
				class_3260.method_14484(lv, class_3260.class_3261.NARRATOR_TOGGLE, new TranslatableText("narrator.toast.disabled"), null);
			} else {
				class_3260.method_14484(
					lv, class_3260.class_3261.NARRATOR_TOGGLE, new TranslatableText("narrator.toast.enabled"), new TranslatableText(GameOptions.field_15883[i])
				);
			}
		} else {
			class_3260.method_14484(
				lv, class_3260.class_3261.NARRATOR_TOGGLE, new TranslatableText("narrator.toast.disabled"), new TranslatableText("options.narrator.notavailable")
			);
		}
	}

	public boolean method_14473() {
		return this.field_15888.active();
	}

	public void method_14475() {
		this.field_15888.clear();
	}
}
