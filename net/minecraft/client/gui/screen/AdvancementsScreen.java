package net.minecraft.client.gui.screen;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3268;
import net.minecraft.class_3295;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.client.gui.AchievementNotification;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.util.Identifier;
import org.lwjgl.input.Mouse;

public class AdvancementsScreen extends Screen implements class_3295.class_3296 {
	private static final Identifier WINDOW_TEXTURE = new Identifier("textures/gui/advancements/window.png");
	private static final Identifier TABS_TEXTURE = new Identifier("textures/gui/advancements/tabs.png");
	private final class_3295 field_16010;
	private final Map<SimpleAdvancement, class_3268> field_16011 = Maps.newLinkedHashMap();
	private class_3268 field_16012;
	private int field_16013;
	private int field_16014;
	private boolean field_16015;

	public AdvancementsScreen(class_3295 arg) {
		this.field_16010 = arg;
	}

	@Override
	public void init() {
		this.field_16011.clear();
		this.field_16012 = null;
		this.field_16010.method_14665(this);
		if (this.field_16012 == null && !this.field_16011.isEmpty()) {
			this.field_16010.method_14666(((class_3268)this.field_16011.values().iterator().next()).method_14513(), true);
		} else {
			this.field_16010.method_14666(this.field_16012 == null ? null : this.field_16012.method_14513(), true);
		}
	}

	@Override
	public void removed() {
		this.field_16010.method_14665(null);
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.sendPacket(AdvancementTabC2SPacket.closeScreen());
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (button == 0) {
			int i = (this.width - 252) / 2;
			int j = (this.height - 140) / 2;

			for (class_3268 lv : this.field_16011.values()) {
				if (lv.method_14516(i, j, mouseX, mouseY)) {
					this.field_16010.method_14666(lv.method_14513(), true);
					break;
				}
			}
		}

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void keyPressed(char id, int code) {
		if (code == this.client.options.field_15880.getCode()) {
			this.client.setScreen(null);
			this.client.closeScreen();
		} else {
			super.keyPressed(id, code);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		int i = (this.width - 252) / 2;
		int j = (this.height - 140) / 2;
		if (Mouse.isButtonDown(0)) {
			if (!this.field_16015) {
				this.field_16015 = true;
			} else if (this.field_16012 != null) {
				this.field_16012.method_14506(mouseX - this.field_16013, mouseY - this.field_16014);
			}

			this.field_16013 = mouseX;
			this.field_16014 = mouseY;
		} else {
			this.field_16015 = false;
		}

		this.renderBackground();
		this.method_14544(mouseX, mouseY, i, j);
		this.method_14543(i, j);
		this.method_14545(mouseX, mouseY, i, j);
	}

	private void method_14544(int i, int j, int k, int l) {
		class_3268 lv = this.field_16012;
		if (lv == null) {
			fill(k + 9, l + 18, k + 9 + 234, l + 18 + 113, -16777216);
			String string = I18n.translate("advancements.empty");
			int m = this.textRenderer.getStringWidth(string);
			this.textRenderer.draw(string, k + 9 + 117 - m / 2, l + 18 + 56 - this.textRenderer.fontHeight / 2, -1);
			this.textRenderer.draw(":(", k + 9 + 117 - this.textRenderer.getStringWidth(":(") / 2, l + 18 + 113 - this.textRenderer.fontHeight, -1);
		} else {
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)(k + 9), (float)(l + 18), -400.0F);
			GlStateManager.enableDepthTest();
			lv.method_14517();
			GlStateManager.popMatrix();
			GlStateManager.depthFunc(515);
			GlStateManager.disableDepthTest();
		}
	}

	public void method_14543(int i, int j) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		DiffuseLighting.disable();
		this.client.getTextureManager().bindTexture(WINDOW_TEXTURE);
		this.drawTexture(i, j, 0, 0, 252, 140);
		if (this.field_16011.size() > 1) {
			this.client.getTextureManager().bindTexture(TABS_TEXTURE);

			for (class_3268 lv : this.field_16011.values()) {
				lv.method_14508(i, j, lv == this.field_16012);
			}

			GlStateManager.enableRescaleNormal();
			GlStateManager.method_12288(
				GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
			);
			DiffuseLighting.enable();

			for (class_3268 lv2 : this.field_16011.values()) {
				lv2.method_14507(i, j, this.itemRenderer);
			}

			GlStateManager.disableBlend();
		}

		this.textRenderer.draw(I18n.translate("gui.advancements"), i + 8, j + 6, 4210752);
	}

	private void method_14545(int i, int j, int k, int l) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if (this.field_16012 != null) {
			GlStateManager.pushMatrix();
			GlStateManager.enableDepthTest();
			GlStateManager.translate((float)(k + 9), (float)(l + 18), 400.0F);
			this.field_16012.method_14514(i - k - 9, j - l - 18, k, l);
			GlStateManager.disableDepthTest();
			GlStateManager.popMatrix();
		}

		if (this.field_16011.size() > 1) {
			for (class_3268 lv : this.field_16011.values()) {
				if (lv.method_14516(k, l, i, j)) {
					this.renderTooltip(lv.method_14515(), i, j);
				}
			}
		}
	}

	@Override
	public void method_14818(SimpleAdvancement advancement) {
		class_3268 lv = class_3268.method_14509(this.client, this, this.field_16011.size(), advancement);
		if (lv != null) {
			this.field_16011.put(advancement, lv);
		}
	}

	@Override
	public void method_14819(SimpleAdvancement advancement) {
	}

	@Override
	public void method_14820(SimpleAdvancement advancement) {
		class_3268 lv = this.method_14547(advancement);
		if (lv != null) {
			lv.method_14511(advancement);
		}
	}

	@Override
	public void method_14821(SimpleAdvancement advancement) {
	}

	@Override
	public void method_14668(SimpleAdvancement advancement, AdvancementProgress progress) {
		AchievementNotification achievementNotification = this.method_14546(advancement);
		if (achievementNotification != null) {
			achievementNotification.method_14533(progress);
		}
	}

	@Override
	public void method_14669(@Nullable SimpleAdvancement advancement) {
		this.field_16012 = (class_3268)this.field_16011.get(advancement);
	}

	@Override
	public void method_14817() {
		this.field_16011.clear();
		this.field_16012 = null;
	}

	@Nullable
	public AchievementNotification method_14546(SimpleAdvancement advancement) {
		class_3268 lv = this.method_14547(advancement);
		return lv == null ? null : lv.method_14512(advancement);
	}

	@Nullable
	private class_3268 method_14547(SimpleAdvancement advancement) {
		while (advancement.getParent() != null) {
			advancement = advancement.getParent();
		}

		return (class_3268)this.field_16011.get(advancement);
	}
}
