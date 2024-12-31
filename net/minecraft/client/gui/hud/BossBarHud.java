package net.minecraft.client.gui.hud;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_2957;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2840;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.util.Identifier;

public class BossBarHud extends DrawableHelper {
	private static final Identifier TEXTURE = new Identifier("textures/gui/bars.png");
	private final MinecraftClient client;
	private final Map<UUID, class_2840> field_13306 = Maps.newLinkedHashMap();

	public BossBarHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void render() {
		if (!this.field_13306.isEmpty()) {
			int i = this.client.field_19944.method_18321();
			int j = 12;

			for (class_2840 lv : this.field_13306.values()) {
				int k = i / 2 - 91;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.client.getTextureManager().bindTexture(TEXTURE);
				this.method_12169(k, j, lv);
				String string = lv.getTitle().asFormattedString();
				this.client.textRenderer.drawWithShadow(string, (float)(i / 2 - this.client.textRenderer.getStringWidth(string) / 2), (float)(j - 9), 16777215);
				j += 10 + this.client.textRenderer.fontHeight;
				if (j >= this.client.field_19944.method_18322() / 3) {
					break;
				}
			}
		}
	}

	private void method_12169(int i, int j, class_2957 arg) {
		this.drawTexture(i, j, 0, arg.getColor().ordinal() * 5 * 2, 182, 5);
		if (arg.getDivision() != class_2957.Division.PROGRESS) {
			this.drawTexture(i, j, 0, 80 + (arg.getDivision().ordinal() - 1) * 5 * 2, 182, 5);
		}

		int k = (int)(arg.getHealth() * 183.0F);
		if (k > 0) {
			this.drawTexture(i, j, 0, arg.getColor().ordinal() * 5 * 2 + 5, k, 5);
			if (arg.getDivision() != class_2957.Division.PROGRESS) {
				this.drawTexture(i, j, 0, 80 + (arg.getDivision().ordinal() - 1) * 5 * 2 + 5, k, 5);
			}
		}
	}

	public void method_12170(BossBarS2CPacket bossBarS2CPacket) {
		if (bossBarS2CPacket.getAction() == BossBarS2CPacket.Action.ADD) {
			this.field_13306.put(bossBarS2CPacket.getUuid(), new class_2840(bossBarS2CPacket));
		} else if (bossBarS2CPacket.getAction() == BossBarS2CPacket.Action.REMOVE) {
			this.field_13306.remove(bossBarS2CPacket.getUuid());
		} else {
			((class_2840)this.field_13306.get(bossBarS2CPacket.getUuid())).method_12175(bossBarS2CPacket);
		}
	}

	public void method_12171() {
		this.field_13306.clear();
	}

	public boolean method_12172() {
		if (!this.field_13306.isEmpty()) {
			for (class_2957 lv : this.field_13306.values()) {
				if (lv.method_12930()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean method_12173() {
		if (!this.field_13306.isEmpty()) {
			for (class_2957 lv : this.field_13306.values()) {
				if (lv.method_12929()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean method_12174() {
		if (!this.field_13306.isEmpty()) {
			for (class_2957 lv : this.field_13306.values()) {
				if (lv.method_12931()) {
					return true;
				}
			}
		}

		return false;
	}
}
