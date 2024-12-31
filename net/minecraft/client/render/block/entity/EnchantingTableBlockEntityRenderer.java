package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EnchantingTableBlockEntityRenderer extends BlockEntityRenderer<EnchantingTableBlockEntity> {
	private static final Identifier BOOK_TEXTURE = new Identifier("textures/entity/enchanting_table_book.png");
	private final BookModel bookModel = new BookModel();

	public void render(EnchantingTableBlockEntity enchantingTableBlockEntity, double d, double e, double f, float g, int i, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d + 0.5F, (float)e + 0.75F, (float)f + 0.5F);
		float j = (float)enchantingTableBlockEntity.ticks + g;
		GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(j * 0.1F) * 0.01F, 0.0F);
		float k = enchantingTableBlockEntity.openBookAngle - enchantingTableBlockEntity.openBookAnglePrev;

		while (k >= (float) Math.PI) {
			k -= (float) (Math.PI * 2);
		}

		while (k < (float) -Math.PI) {
			k += (float) (Math.PI * 2);
		}

		float l = enchantingTableBlockEntity.openBookAnglePrev + k * g;
		GlStateManager.rotate(-l * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
		this.bindTexture(BOOK_TEXTURE);
		float m = enchantingTableBlockEntity.pageAngle + (enchantingTableBlockEntity.nextPageAngle - enchantingTableBlockEntity.pageAngle) * g + 0.25F;
		float n = enchantingTableBlockEntity.pageAngle + (enchantingTableBlockEntity.nextPageAngle - enchantingTableBlockEntity.pageAngle) * g + 0.75F;
		m = (m - (float)MathHelper.fastFloor((double)m)) * 1.6F - 0.3F;
		n = (n - (float)MathHelper.fastFloor((double)n)) * 1.6F - 0.3F;
		if (m < 0.0F) {
			m = 0.0F;
		}

		if (n < 0.0F) {
			n = 0.0F;
		}

		if (m > 1.0F) {
			m = 1.0F;
		}

		if (n > 1.0F) {
			n = 1.0F;
		}

		float o = enchantingTableBlockEntity.pageTurningSpeed + (enchantingTableBlockEntity.nextPageTurningSpeed - enchantingTableBlockEntity.pageTurningSpeed) * g;
		GlStateManager.enableCull();
		this.bookModel.render(null, j, m, n, o, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}
}
