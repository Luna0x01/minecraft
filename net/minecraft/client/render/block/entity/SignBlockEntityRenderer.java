package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.entity.model.SignBlockEntityModel;
import net.minecraft.client.util.Texts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SignBlockEntityRenderer extends BlockEntityRenderer<SignBlockEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/sign.png");
	private final SignBlockEntityModel model = new SignBlockEntityModel();

	public void render(SignBlockEntity signBlockEntity, double d, double e, double f, float g, int i) {
		Block block = signBlockEntity.getBlock();
		GlStateManager.pushMatrix();
		float h = 0.6666667F;
		if (block == Blocks.STANDING_SIGN) {
			GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			float j = (float)(signBlockEntity.getDataValue() * 360) / 16.0F;
			GlStateManager.rotate(-j, 0.0F, 1.0F, 0.0F);
			this.model.stick.visible = true;
		} else {
			int k = signBlockEntity.getDataValue();
			float l = 0.0F;
			if (k == 2) {
				l = 180.0F;
			}

			if (k == 4) {
				l = 90.0F;
			}

			if (k == 5) {
				l = -90.0F;
			}

			GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			GlStateManager.rotate(-l, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
			this.model.stick.visible = false;
		}

		if (i >= 0) {
			this.bindTexture(DESTROY_STAGE_TEXTURE[i]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 2.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			this.bindTexture(TEXTURE);
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
		this.model.render();
		GlStateManager.popMatrix();
		TextRenderer textRenderer = this.getTextRenderer();
		float m = 0.010416667F;
		GlStateManager.translate(0.0F, 0.33333334F, 0.046666667F);
		GlStateManager.scale(0.010416667F, -0.010416667F, 0.010416667F);
		GlStateManager.method_12272(0.0F, 0.0F, -0.010416667F);
		GlStateManager.depthMask(false);
		int n = 0;
		if (i < 0) {
			for (int o = 0; o < signBlockEntity.text.length; o++) {
				if (signBlockEntity.text[o] != null) {
					Text text = signBlockEntity.text[o];
					List<Text> list = Texts.wrapLines(text, 90, textRenderer, false, true);
					String string = list != null && !list.isEmpty() ? ((Text)list.get(0)).asFormattedString() : "";
					if (o == signBlockEntity.lineBeingEdited) {
						string = "> " + string + " <";
						textRenderer.draw(string, -textRenderer.getStringWidth(string) / 2, o * 10 - signBlockEntity.text.length * 5, 0);
					} else {
						textRenderer.draw(string, -textRenderer.getStringWidth(string) / 2, o * 10 - signBlockEntity.text.length * 5, 0);
					}
				}
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
		if (i >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}
}
