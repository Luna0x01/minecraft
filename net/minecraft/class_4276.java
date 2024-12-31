package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public final class class_4276 extends Sprite {
	private static final Identifier field_20982 = new Identifier("missingno");
	@Nullable
	private static NativeImageBackedTexture field_20983;
	private static final class_4277 field_20984 = new class_4277(16, 16, false);
	private static final class_4276 field_20985 = Util.make(() -> {
		class_4276 lv = new class_4276();
		int i = -16777216;
		int j = -524040;

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				if (k < 8 ^ l < 8) {
					field_20984.method_19460(l, k, -524040);
				} else {
					field_20984.method_19460(l, k, -16777216);
				}
			}
		}

		field_20984.method_19485();
		return lv;
	});

	private class_4276() {
		super(field_20982, 16, 16);
		this.field_21028 = new class_4277[1];
		this.field_21028[0] = field_20984;
	}

	public static class_4276 method_19454() {
		return field_20985;
	}

	public static Identifier method_19455() {
		return field_20982;
	}

	@Override
	public void clearFrames() {
		for (int i = 1; i < this.field_21028.length; i++) {
			this.field_21028[i].close();
		}

		this.field_21028 = new class_4277[1];
		this.field_21028[0] = field_20984;
	}

	public static NativeImageBackedTexture method_19456() {
		if (field_20983 == null) {
			field_20983 = new NativeImageBackedTexture(field_20984);
			MinecraftClient.getInstance().getTextureManager().loadTexture(field_20982, field_20983);
		}

		return field_20983;
	}
}
