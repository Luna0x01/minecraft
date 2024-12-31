package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_4239;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EnchantingTableBlockEntityRenderer extends class_4239<EnchantingTableBlockEntity> {
	private static final Identifier BOOK_TEXTURE = new Identifier("textures/entity/enchanting_table_book.png");
	private final BookModel bookModel = new BookModel();

	public void method_1631(EnchantingTableBlockEntity enchantingTableBlockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d + 0.5F, (float)e + 0.75F, (float)f + 0.5F);
		float h = (float)enchantingTableBlockEntity.ticks + g;
		GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(h * 0.1F) * 0.01F, 0.0F);
		float j = enchantingTableBlockEntity.openBookAngle - enchantingTableBlockEntity.openBookAnglePrev;

		while (j >= (float) Math.PI) {
			j -= (float) (Math.PI * 2);
		}

		while (j < (float) -Math.PI) {
			j += (float) (Math.PI * 2);
		}

		float k = enchantingTableBlockEntity.openBookAnglePrev + j * g;
		GlStateManager.rotate(-k * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
		this.method_19327(BOOK_TEXTURE);
		float l = enchantingTableBlockEntity.pageAngle + (enchantingTableBlockEntity.nextPageAngle - enchantingTableBlockEntity.pageAngle) * g + 0.25F;
		float m = enchantingTableBlockEntity.pageAngle + (enchantingTableBlockEntity.nextPageAngle - enchantingTableBlockEntity.pageAngle) * g + 0.75F;
		l = (l - (float)MathHelper.fastFloor((double)l)) * 1.6F - 0.3F;
		m = (m - (float)MathHelper.fastFloor((double)m)) * 1.6F - 0.3F;
		if (l < 0.0F) {
			l = 0.0F;
		}

		if (m < 0.0F) {
			m = 0.0F;
		}

		if (l > 1.0F) {
			l = 1.0F;
		}

		if (m > 1.0F) {
			m = 1.0F;
		}

		float n = enchantingTableBlockEntity.pageTurningSpeed + (enchantingTableBlockEntity.nextPageTurningSpeed - enchantingTableBlockEntity.pageTurningSpeed) * g;
		GlStateManager.enableCull();
		this.bookModel.render(null, h, l, m, n, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}
}
