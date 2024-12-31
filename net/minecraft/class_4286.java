package net.minecraft;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class class_4286 extends class_4465 {
	@Nullable
	private final class_4277 field_21044;
	@Nullable
	private Identifier field_21045;

	public class_4286(String string, boolean bl, Supplier<class_4454> supplier, class_4454 arg, class_4458 arg2, class_4465.class_4466 arg3) {
		super(string, bl, supplier, arg, arg2, arg3);
		class_4277 lv = null;

		try {
			InputStream inputStream = arg.method_21330("pack.png");
			Throwable var9 = null;

			try {
				lv = class_4277.method_19472(inputStream);
			} catch (Throwable var19) {
				var9 = var19;
				throw var19;
			} finally {
				if (inputStream != null) {
					if (var9 != null) {
						try {
							inputStream.close();
						} catch (Throwable var18) {
							var9.addSuppressed(var18);
						}
					} else {
						inputStream.close();
					}
				}
			}
		} catch (IllegalArgumentException | IOException var21) {
		}

		this.field_21044 = lv;
	}

	public class_4286(
		String string,
		boolean bl,
		Supplier<class_4454> supplier,
		Text text,
		Text text2,
		class_4461 arg,
		class_4465.class_4466 arg2,
		boolean bl2,
		@Nullable class_4277 arg3
	) {
		super(string, bl, supplier, text, text2, arg, arg2, bl2);
		this.field_21044 = arg3;
	}

	public void method_19556(TextureManager textureManager) {
		if (this.field_21045 == null) {
			if (this.field_21044 == null) {
				this.field_21045 = new Identifier("textures/misc/unknown_pack.png");
			} else {
				this.field_21045 = textureManager.registerDynamicTexture("texturepackicon", new NativeImageBackedTexture(this.field_21044));
			}
		}

		textureManager.bindTexture(this.field_21045);
	}
}
