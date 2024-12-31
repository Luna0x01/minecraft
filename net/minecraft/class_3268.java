package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AchievementNotification;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.AdvancementsScreen;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class class_3268 extends DrawableHelper {
	private final MinecraftClient client;
	private final AdvancementsScreen field_15955;
	private final class_3269 field_15956;
	private final int field_15957;
	private final SimpleAdvancement advancement;
	private final AdvancementDisplay display;
	private final ItemStack icon;
	private final String title;
	private final AchievementNotification notification;
	private final Map<SimpleAdvancement, AchievementNotification> field_15963 = Maps.newLinkedHashMap();
	private double field_20346;
	private double field_20347;
	private int field_15966 = Integer.MAX_VALUE;
	private int field_15967 = Integer.MAX_VALUE;
	private int field_15968 = Integer.MIN_VALUE;
	private int field_15969 = Integer.MIN_VALUE;
	private float field_15970;
	private boolean field_15971;

	public class_3268(
		MinecraftClient minecraftClient,
		AdvancementsScreen advancementsScreen,
		class_3269 arg,
		int i,
		SimpleAdvancement simpleAdvancement,
		AdvancementDisplay advancementDisplay
	) {
		this.client = minecraftClient;
		this.field_15955 = advancementsScreen;
		this.field_15956 = arg;
		this.field_15957 = i;
		this.advancement = simpleAdvancement;
		this.display = advancementDisplay;
		this.icon = advancementDisplay.getDisplayStack();
		this.title = advancementDisplay.getTitle().asFormattedString();
		this.notification = new AchievementNotification(this, minecraftClient, simpleAdvancement, advancementDisplay);
		this.method_14510(this.notification, simpleAdvancement);
	}

	public SimpleAdvancement method_14513() {
		return this.advancement;
	}

	public String method_14515() {
		return this.title;
	}

	public void method_14508(int i, int j, boolean bl) {
		this.field_15956.method_14523(this, i, j, bl, this.field_15957);
	}

	public void method_14507(int i, int j, HeldItemRenderer heldItemRenderer) {
		this.field_15956.method_14522(i, j, this.field_15957, heldItemRenderer, this.icon);
	}

	public void method_14517() {
		if (!this.field_15971) {
			this.field_20346 = (double)(117 - (this.field_15968 + this.field_15966) / 2);
			this.field_20347 = (double)(56 - (this.field_15969 + this.field_15967) / 2);
			this.field_15971 = true;
		}

		GlStateManager.depthFunc(518);
		fill(0, 0, 234, 113, -16777216);
		GlStateManager.depthFunc(515);
		Identifier identifier = this.display.method_15010();
		if (identifier != null) {
			this.client.getTextureManager().bindTexture(identifier);
		} else {
			this.client.getTextureManager().bindTexture(TextureManager.field_16165);
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int i = MathHelper.floor(this.field_20346);
		int j = MathHelper.floor(this.field_20347);
		int k = i % 16;
		int l = j % 16;

		for (int m = -1; m <= 15; m++) {
			for (int n = -1; n <= 8; n++) {
				drawTexture(k + 16 * m, l + 16 * n, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
			}
		}

		this.notification.method_14529(i, j, true);
		this.notification.method_14529(i, j, false);
		this.notification.method_14525(i, j);
	}

	public void method_14514(int i, int j, int k, int l) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, 0.0F, 200.0F);
		fill(0, 0, 234, 113, MathHelper.floor(this.field_15970 * 255.0F) << 24);
		boolean bl = false;
		int m = MathHelper.floor(this.field_20346);
		int n = MathHelper.floor(this.field_20347);
		if (i > 0 && i < 234 && j > 0 && j < 113) {
			for (AchievementNotification achievementNotification : this.field_15963.values()) {
				if (achievementNotification.method_14536(m, n, i, j)) {
					bl = true;
					achievementNotification.method_14526(m, n, this.field_15970, k, l);
					break;
				}
			}
		}

		GlStateManager.popMatrix();
		if (bl) {
			this.field_15970 = MathHelper.clamp(this.field_15970 + 0.02F, 0.0F, 0.3F);
		} else {
			this.field_15970 = MathHelper.clamp(this.field_15970 - 0.04F, 0.0F, 1.0F);
		}
	}

	public boolean method_14516(int i, int j, double d, double e) {
		return this.field_15956.method_14521(i, j, this.field_15957, d, e);
	}

	@Nullable
	public static class_3268 method_14509(MinecraftClient minecraftClient, AdvancementsScreen advancementsScreen, int i, SimpleAdvancement simpleAdvancement) {
		if (simpleAdvancement.getDisplay() == null) {
			return null;
		} else {
			for (class_3269 lv : class_3269.values()) {
				if (i < lv.method_14519()) {
					return new class_3268(minecraftClient, advancementsScreen, lv, i, simpleAdvancement, simpleAdvancement.getDisplay());
				}

				i -= lv.method_14519();
			}

			return null;
		}
	}

	public void method_18643(double d, double e) {
		if (this.field_15968 - this.field_15966 > 234) {
			this.field_20346 = MathHelper.clamp(this.field_20346 + d, (double)(-(this.field_15968 - 234)), 0.0);
		}

		if (this.field_15969 - this.field_15967 > 113) {
			this.field_20347 = MathHelper.clamp(this.field_20347 + e, (double)(-(this.field_15969 - 113)), 0.0);
		}
	}

	public void method_14511(SimpleAdvancement simpleAdvancement) {
		if (simpleAdvancement.getDisplay() != null) {
			AchievementNotification achievementNotification = new AchievementNotification(this, this.client, simpleAdvancement, simpleAdvancement.getDisplay());
			this.method_14510(achievementNotification, simpleAdvancement);
		}
	}

	private void method_14510(AchievementNotification achievementNotification, SimpleAdvancement simpleAdvancement) {
		this.field_15963.put(simpleAdvancement, achievementNotification);
		int i = achievementNotification.method_14537();
		int j = i + 28;
		int k = achievementNotification.method_14535();
		int l = k + 27;
		this.field_15966 = Math.min(this.field_15966, i);
		this.field_15968 = Math.max(this.field_15968, j);
		this.field_15967 = Math.min(this.field_15967, k);
		this.field_15969 = Math.max(this.field_15969, l);

		for (AchievementNotification achievementNotification2 : this.field_15963.values()) {
			achievementNotification2.method_14534();
		}
	}

	@Nullable
	public AchievementNotification method_14512(SimpleAdvancement simpleAdvancement) {
		return (AchievementNotification)this.field_15963.get(simpleAdvancement);
	}

	public AdvancementsScreen method_14518() {
		return this.field_15955;
	}
}
