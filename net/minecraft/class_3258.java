package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementType;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.Sounds;
import net.minecraft.util.math.MathHelper;

public class class_3258 implements class_3262 {
	private final SimpleAdvancement field_15901;
	private boolean field_15902;

	public class_3258(SimpleAdvancement simpleAdvancement) {
		this.field_15901 = simpleAdvancement;
	}

	@Override
	public class_3262.class_3263 method_14486(class_3264 arg, long l) {
		arg.method_14494().getTextureManager().bindTexture(field_15914);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		AdvancementDisplay advancementDisplay = this.field_15901.getDisplay();
		arg.drawTexture(0, 0, 0, 0, 160, 32);
		if (advancementDisplay != null) {
			List<String> list = arg.method_14494().textRenderer.wrapLines(advancementDisplay.getTitle().asFormattedString(), 125);
			int i = advancementDisplay.getAdvancementType() == AdvancementType.CHALLENGE ? 16746751 : 16776960;
			if (list.size() == 1) {
				arg.method_14494()
					.textRenderer
					.method_18355(I18n.translate("advancements.toast." + advancementDisplay.getAdvancementType().getType()), 30.0F, 7.0F, i | 0xFF000000);
				arg.method_14494().textRenderer.method_18355(advancementDisplay.getTitle().asFormattedString(), 30.0F, 18.0F, -1);
			} else {
				int j = 1500;
				float f = 300.0F;
				if (l < 1500L) {
					int k = MathHelper.floor(MathHelper.clamp((float)(1500L - l) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
					arg.method_14494()
						.textRenderer
						.method_18355(I18n.translate("advancements.toast." + advancementDisplay.getAdvancementType().getType()), 30.0F, 11.0F, i | k);
				} else {
					int m = MathHelper.floor(MathHelper.clamp((float)(l - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
					int n = 16 - list.size() * arg.method_14494().textRenderer.fontHeight / 2;

					for (String string : list) {
						arg.method_14494().textRenderer.method_18355(string, 30.0F, (float)n, 16777215 | m);
						n += arg.method_14494().textRenderer.fontHeight;
					}
				}
			}

			if (!this.field_15902 && l > 0L) {
				this.field_15902 = true;
				if (advancementDisplay.getAdvancementType() == AdvancementType.CHALLENGE) {
					arg.method_14494().getSoundManager().play(PositionedSoundInstance.method_14699(Sounds.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
				}
			}

			DiffuseLighting.enable();
			arg.method_14494().getHeldItemRenderer().method_19374(null, advancementDisplay.getDisplayStack(), 8, 8);
			return l >= 5000L ? class_3262.class_3263.HIDE : class_3262.class_3263.SHOW;
		} else {
			return class_3262.class_3263.HIDE;
		}
	}
}
