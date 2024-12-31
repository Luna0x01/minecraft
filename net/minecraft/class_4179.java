package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IdentifiableBooleanConsumer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorageAccess;

public class class_4179 extends Screen {
	private static final Object2IntMap<DimensionType> field_20493 = Util.make(
		new Object2IntOpenCustomHashMap(Util.method_20233()), object2IntOpenCustomHashMap -> {
			object2IntOpenCustomHashMap.put(DimensionType.OVERWORLD, -13408734);
			object2IntOpenCustomHashMap.put(DimensionType.THE_NETHER, -10075085);
			object2IntOpenCustomHashMap.put(DimensionType.THE_END, -8943531);
			object2IntOpenCustomHashMap.defaultReturnValue(-2236963);
		}
	);
	private final IdentifiableBooleanConsumer field_20494;
	private final class_3457 field_20495;

	public class_4179(IdentifiableBooleanConsumer identifiableBooleanConsumer, String string, LevelStorageAccess levelStorageAccess) {
		this.field_20494 = identifiableBooleanConsumer;
		this.field_20495 = new class_3457(string, levelStorageAccess, levelStorageAccess.getLevelProperties(string));
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 150, I18n.translate("gui.cancel")) {
			@Override
			public void method_18374(double d, double e) {
				class_4179.this.field_20495.method_15520();
				class_4179.this.field_20494.confirmResult(false, 0);
			}
		});
	}

	@Override
	public void tick() {
		if (this.field_20495.method_15524()) {
			this.field_20494.confirmResult(true, 0);
		}
	}

	@Override
	public void removed() {
		this.field_20495.method_15520();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, I18n.translate("optimizeWorld.title", this.field_20495.method_15530()), this.width / 2, 20, 16777215);
		int i = this.width / 2 - 150;
		int j = this.width / 2 + 150;
		int k = this.height / 4 + 100;
		int l = k + 10;
		this.drawCenteredString(
			this.textRenderer, this.field_20495.method_15529().asFormattedString(), this.width / 2, k - this.textRenderer.fontHeight - 2, 10526880
		);
		if (this.field_20495.method_15526() > 0) {
			fill(i - 1, k - 1, j + 1, l + 1, -16777216);
			this.drawWithShadow(this.textRenderer, I18n.translate("optimizeWorld.info.converted", this.field_20495.method_15527()), i, 40, 10526880);
			this.drawWithShadow(
				this.textRenderer, I18n.translate("optimizeWorld.info.skipped", this.field_20495.method_15528()), i, 40 + this.textRenderer.fontHeight + 3, 10526880
			);
			this.drawWithShadow(
				this.textRenderer, I18n.translate("optimizeWorld.info.total", this.field_20495.method_15526()), i, 40 + (this.textRenderer.fontHeight + 3) * 2, 10526880
			);
			int m = 0;

			for (DimensionType dimensionType : DimensionType.method_17200()) {
				int n = MathHelper.floor(this.field_20495.method_15522(dimensionType) * (float)(j - i));
				fill(i + m, k, i + m + n, l, field_20493.getInt(dimensionType));
				m += n;
			}

			int o = this.field_20495.method_15527() + this.field_20495.method_15528();
			this.drawCenteredString(this.textRenderer, o + " / " + this.field_20495.method_15526(), this.width / 2, k + 2 * this.textRenderer.fontHeight + 2, 10526880);
			this.drawCenteredString(
				this.textRenderer,
				MathHelper.floor(this.field_20495.method_15525() * 100.0F) + "%",
				this.width / 2,
				k + ((l - k) / 2 - this.textRenderer.fontHeight / 2),
				10526880
			);
		}

		super.render(mouseX, mouseY, tickDelta);
	}
}
