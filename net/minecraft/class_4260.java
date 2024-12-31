package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class class_4260 extends MobEntityRenderer<PufferfishEntity> {
	private static final Identifier field_20938 = new Identifier("textures/entity/fish/pufferfish.png");
	private int field_20939;
	private final class_4193 field_20940 = new class_4193();
	private final class_4192 field_20941 = new class_4192();
	private final class_4191 field_20942 = new class_4191();

	public class_4260(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4191(), 0.1F);
		this.field_20939 = 3;
	}

	@Nullable
	protected Identifier getTexture(PufferfishEntity pufferfishEntity) {
		return field_20938;
	}

	public void render(PufferfishEntity pufferfishEntity, double d, double e, double f, float g, float h) {
		int i = pufferfishEntity.method_15766();
		if (i != this.field_20939) {
			if (i == 0) {
				this.model = this.field_20940;
			} else if (i == 1) {
				this.model = this.field_20941;
			} else {
				this.model = this.field_20942;
			}
		}

		this.field_20939 = i;
		this.shadowSize = 0.1F + 0.1F * (float)i;
		super.render(pufferfishEntity, d, e, f, g, h);
	}

	protected void method_5777(PufferfishEntity pufferfishEntity, float f, float g, float h) {
		GlStateManager.translate(0.0F, MathHelper.cos(f * 0.05F) * 0.08F, 0.0F);
		super.method_5777(pufferfishEntity, f, g, h);
	}
}
