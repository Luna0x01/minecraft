package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.class_3268;
import net.minecraft.class_3275;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AchievementNotification extends DrawableHelper {
	private static final Identifier ADVANCEMENTS_TEXTURE = new Identifier("textures/gui/advancements/widgets.png");
	private static final Pattern field_15985 = Pattern.compile("(.+) \\S+");
	private final class_3268 field_15986;
	private final SimpleAdvancement advancement;
	private final AdvancementDisplay display;
	private final String field_15989;
	private final int field_15990;
	private final List<String> field_15991;
	private final MinecraftClient client;
	private AchievementNotification field_15992;
	private final List<AchievementNotification> field_15993 = Lists.newArrayList();
	private AdvancementProgress progress;
	private final int field_15995;
	private final int field_15996;

	public AchievementNotification(class_3268 arg, MinecraftClient minecraftClient, SimpleAdvancement simpleAdvancement, AdvancementDisplay advancementDisplay) {
		this.field_15986 = arg;
		this.advancement = simpleAdvancement;
		this.display = advancementDisplay;
		this.client = minecraftClient;
		this.field_15989 = minecraftClient.textRenderer.trimToWidth(advancementDisplay.getTitle().asFormattedString(), 163);
		this.field_15995 = MathHelper.floor(advancementDisplay.method_15012() * 28.0F);
		this.field_15996 = MathHelper.floor(advancementDisplay.method_15013() * 27.0F);
		int i = simpleAdvancement.getRequirementsCount();
		int j = String.valueOf(i).length();
		int k = i > 1
			? minecraftClient.textRenderer.getStringWidth("  ")
				+ minecraftClient.textRenderer.getStringWidth("0") * j * 2
				+ minecraftClient.textRenderer.getStringWidth("/")
			: 0;
		int l = 29 + minecraftClient.textRenderer.getStringWidth(this.field_15989) + k;
		String string = advancementDisplay.getDescription().asFormattedString();
		this.field_15991 = this.method_14532(string, l);

		for (String string2 : this.field_15991) {
			l = Math.max(l, minecraftClient.textRenderer.getStringWidth(string2));
		}

		this.field_15990 = l + 3 + 5;
	}

	private List<String> method_14532(String string, int i) {
		if (string.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<String> list = this.client.textRenderer.wrapLines(string, i);
			if (list.size() < 2) {
				return list;
			} else {
				String string2 = (String)list.get(0);
				String string3 = (String)list.get(1);
				int j = this.client.textRenderer.getStringWidth(string2 + ' ' + string3.split(" ")[0]);
				if (j - i <= 10) {
					return this.client.textRenderer.wrapLines(string, j);
				} else {
					Matcher matcher = field_15985.matcher(string2);
					if (matcher.matches()) {
						int k = this.client.textRenderer.getStringWidth(matcher.group(1));
						if (i - k <= 10) {
							return this.client.textRenderer.wrapLines(string, k);
						}
					}

					return list;
				}
			}
		}
	}

	@Nullable
	private AchievementNotification method_14531(SimpleAdvancement simpleAdvancement) {
		do {
			simpleAdvancement = simpleAdvancement.getParent();
		} while (simpleAdvancement != null && simpleAdvancement.getDisplay() == null);

		return simpleAdvancement != null && simpleAdvancement.getDisplay() != null ? this.field_15986.method_14512(simpleAdvancement) : null;
	}

	public void method_14529(int i, int j, boolean bl) {
		if (this.field_15992 != null) {
			int k = i + this.field_15992.field_15995 + 13;
			int l = i + this.field_15992.field_15995 + 26 + 4;
			int m = j + this.field_15992.field_15996 + 13;
			int n = i + this.field_15995 + 13;
			int o = j + this.field_15996 + 13;
			int p = bl ? -16777216 : -1;
			if (bl) {
				this.drawHorizontalLine(l, k, m - 1, p);
				this.drawHorizontalLine(l + 1, k, m, p);
				this.drawHorizontalLine(l, k, m + 1, p);
				this.drawHorizontalLine(n, l - 1, o - 1, p);
				this.drawHorizontalLine(n, l - 1, o, p);
				this.drawHorizontalLine(n, l - 1, o + 1, p);
				this.drawVerticalLine(l - 1, o, m, p);
				this.drawVerticalLine(l + 1, o, m, p);
			} else {
				this.drawHorizontalLine(l, k, m, p);
				this.drawHorizontalLine(n, l, o, p);
				this.drawVerticalLine(l, o, m, p);
			}
		}

		for (AchievementNotification achievementNotification : this.field_15993) {
			achievementNotification.method_14529(i, j, bl);
		}
	}

	public void method_14525(int i, int j) {
		if (!this.display.method_15016() || this.progress != null && this.progress.method_14833()) {
			float f = this.progress == null ? 0.0F : this.progress.method_14842();
			class_3275 lv;
			if (f >= 1.0F) {
				lv = class_3275.OBTAINED;
			} else {
				lv = class_3275.UNOBTAINED;
			}

			this.client.getTextureManager().bindTexture(ADVANCEMENTS_TEXTURE);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableBlend();
			this.drawTexture(i + this.field_15995 + 3, j + this.field_15996, this.display.getAdvancementType().getTextureOffset(), 128 + lv.method_14542() * 26, 26, 26);
			DiffuseLighting.enable();
			this.client.getHeldItemRenderer().method_19374(null, this.display.getDisplayStack(), i + this.field_15995 + 8, j + this.field_15996 + 5);
		}

		for (AchievementNotification achievementNotification : this.field_15993) {
			achievementNotification.method_14525(i, j);
		}
	}

	public void method_14533(AdvancementProgress advancementProgress) {
		this.progress = advancementProgress;
	}

	public void method_14530(AchievementNotification achievementNotification) {
		this.field_15993.add(achievementNotification);
	}

	public void method_14526(int i, int j, float f, int k, int l) {
		boolean bl = k + i + this.field_15995 + this.field_15990 + 26 >= this.field_15986.method_14518().width;
		String string = this.progress == null ? null : this.progress.method_14844();
		int m = string == null ? 0 : this.client.textRenderer.getStringWidth(string);
		boolean bl2 = 113 - j - this.field_15996 - 26 <= 6 + this.field_15991.size() * this.client.textRenderer.fontHeight;
		float g = this.progress == null ? 0.0F : this.progress.method_14842();
		int n = MathHelper.floor(g * (float)this.field_15990);
		class_3275 lv;
		class_3275 lv2;
		class_3275 lv3;
		if (g >= 1.0F) {
			n = this.field_15990 / 2;
			lv = class_3275.OBTAINED;
			lv2 = class_3275.OBTAINED;
			lv3 = class_3275.OBTAINED;
		} else if (n < 2) {
			n = this.field_15990 / 2;
			lv = class_3275.UNOBTAINED;
			lv2 = class_3275.UNOBTAINED;
			lv3 = class_3275.UNOBTAINED;
		} else if (n > this.field_15990 - 2) {
			n = this.field_15990 / 2;
			lv = class_3275.OBTAINED;
			lv2 = class_3275.OBTAINED;
			lv3 = class_3275.UNOBTAINED;
		} else {
			lv = class_3275.OBTAINED;
			lv2 = class_3275.UNOBTAINED;
			lv3 = class_3275.UNOBTAINED;
		}

		int o = this.field_15990 - n;
		this.client.getTextureManager().bindTexture(ADVANCEMENTS_TEXTURE);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		int p = j + this.field_15996;
		int q;
		if (bl) {
			q = i + this.field_15995 - this.field_15990 + 26 + 6;
		} else {
			q = i + this.field_15995;
		}

		int s = 32 + this.field_15991.size() * this.client.textRenderer.fontHeight;
		if (!this.field_15991.isEmpty()) {
			if (bl2) {
				this.method_14528(q, p + 26 - s, this.field_15990, s, 10, 200, 26, 0, 52);
			} else {
				this.method_14528(q, p, this.field_15990, s, 10, 200, 26, 0, 52);
			}
		}

		this.drawTexture(q, p, 0, lv.method_14542() * 26, n, 26);
		this.drawTexture(q + n, p, 200 - o, lv2.method_14542() * 26, o, 26);
		this.drawTexture(i + this.field_15995 + 3, j + this.field_15996, this.display.getAdvancementType().getTextureOffset(), 128 + lv3.method_14542() * 26, 26, 26);
		if (bl) {
			this.client.textRenderer.drawWithShadow(this.field_15989, (float)(q + 5), (float)(j + this.field_15996 + 9), -1);
			if (string != null) {
				this.client.textRenderer.drawWithShadow(string, (float)(i + this.field_15995 - m), (float)(j + this.field_15996 + 9), -1);
			}
		} else {
			this.client.textRenderer.drawWithShadow(this.field_15989, (float)(i + this.field_15995 + 32), (float)(j + this.field_15996 + 9), -1);
			if (string != null) {
				this.client.textRenderer.drawWithShadow(string, (float)(i + this.field_15995 + this.field_15990 - m - 5), (float)(j + this.field_15996 + 9), -1);
			}
		}

		if (bl2) {
			for (int t = 0; t < this.field_15991.size(); t++) {
				this.client
					.textRenderer
					.method_18355((String)this.field_15991.get(t), (float)(q + 5), (float)(p + 26 - s + 7 + t * this.client.textRenderer.fontHeight), -5592406);
			}
		} else {
			for (int u = 0; u < this.field_15991.size(); u++) {
				this.client
					.textRenderer
					.method_18355((String)this.field_15991.get(u), (float)(q + 5), (float)(j + this.field_15996 + 9 + 17 + u * this.client.textRenderer.fontHeight), -5592406);
			}
		}

		DiffuseLighting.enable();
		this.client.getHeldItemRenderer().method_19374(null, this.display.getDisplayStack(), i + this.field_15995 + 8, j + this.field_15996 + 5);
	}

	protected void method_14528(int i, int j, int k, int l, int m, int n, int o, int p, int q) {
		this.drawTexture(i, j, p, q, m, m);
		this.method_14527(i + m, j, k - m - m, m, p + m, q, n - m - m, o);
		this.drawTexture(i + k - m, j, p + n - m, q, m, m);
		this.drawTexture(i, j + l - m, p, q + o - m, m, m);
		this.method_14527(i + m, j + l - m, k - m - m, m, p + m, q + o - m, n - m - m, o);
		this.drawTexture(i + k - m, j + l - m, p + n - m, q + o - m, m, m);
		this.method_14527(i, j + m, m, l - m - m, p, q + m, n, o - m - m);
		this.method_14527(i + m, j + m, k - m - m, l - m - m, p + m, q + m, n - m - m, o - m - m);
		this.method_14527(i + k - m, j + m, m, l - m - m, p + n - m, q + m, n, o - m - m);
	}

	protected void method_14527(int i, int j, int k, int l, int m, int n, int o, int p) {
		int q = 0;

		while (q < k) {
			int r = i + q;
			int s = Math.min(o, k - q);
			int t = 0;

			while (t < l) {
				int u = j + t;
				int v = Math.min(p, l - t);
				this.drawTexture(r, u, m, n, s, v);
				t += p;
			}

			q += o;
		}
	}

	public boolean method_14536(int i, int j, int k, int l) {
		if (!this.display.method_15016() || this.progress != null && this.progress.method_14833()) {
			int m = i + this.field_15995;
			int n = m + 26;
			int o = j + this.field_15996;
			int p = o + 26;
			return k >= m && k <= n && l >= o && l <= p;
		} else {
			return false;
		}
	}

	public void method_14534() {
		if (this.field_15992 == null && this.advancement.getParent() != null) {
			this.field_15992 = this.method_14531(this.advancement);
			if (this.field_15992 != null) {
				this.field_15992.method_14530(this);
			}
		}
	}

	public int method_14535() {
		return this.field_15996;
	}

	public int method_14537() {
		return this.field_15995;
	}
}
