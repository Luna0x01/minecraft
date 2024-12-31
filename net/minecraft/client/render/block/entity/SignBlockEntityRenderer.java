package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.class_4239;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.entity.model.SignBlockEntityModel;
import net.minecraft.client.util.Texts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignBlockEntityRenderer extends class_4239<SignBlockEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/sign.png");
	private final SignBlockEntityModel model = new SignBlockEntityModel();

	public void method_1631(SignBlockEntity signBlockEntity, double d, double e, double f, float g, int i) {
		BlockState blockState = signBlockEntity.method_16783();
		GlStateManager.pushMatrix();
		float h = 0.6666667F;
		if (blockState.getBlock() == Blocks.SIGN) {
			GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			GlStateManager.rotate(-((float)((Integer)blockState.getProperty(StandingSignBlock.field_18517) * 360) / 16.0F), 0.0F, 1.0F, 0.0F);
			this.model.method_18937().visible = true;
		} else {
			GlStateManager.translate((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			GlStateManager.rotate(-((Direction)blockState.getProperty(WallSignBlock.FACING)).method_12578(), 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
			this.model.method_18937().visible = false;
		}

		if (i >= 0) {
			this.method_19327(field_20846[i]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 2.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			this.method_19327(TEXTURE);
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
		this.model.render();
		GlStateManager.popMatrix();
		TextRenderer textRenderer = this.method_19329();
		float j = 0.010416667F;
		GlStateManager.translate(0.0F, 0.33333334F, 0.046666667F);
		GlStateManager.scale(0.010416667F, -0.010416667F, 0.010416667F);
		GlStateManager.method_12272(0.0F, 0.0F, -0.010416667F);
		GlStateManager.depthMask(false);
		if (i < 0) {
			for (int k = 0; k < 4; k++) {
				String string = signBlockEntity.method_16838(k, text -> {
					List<Text> list = Texts.wrapLines(text, 90, textRenderer, false, true);
					return list.isEmpty() ? "" : ((Text)list.get(0)).asFormattedString();
				});
				if (string != null) {
					if (k == signBlockEntity.lineBeingEdited) {
						string = "> " + string + " <";
					}

					textRenderer.method_18355(string, (float)(-textRenderer.getStringWidth(string) / 2), (float)(k * 10 - signBlockEntity.text.length * 5), 0);
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
