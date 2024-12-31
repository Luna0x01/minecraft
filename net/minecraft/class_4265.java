package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;

public class class_4265 extends MobEntityRenderer<TurtleEntity> {
	private static final Identifier field_20950 = new Identifier("textures/entity/turtle/big_sea_turtle.png");

	public class_4265(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4200(0.0F), 0.35F);
	}

	public void render(TurtleEntity turtleEntity, double d, double e, double f, float g, float h) {
		if (turtleEntity.isBaby()) {
			this.shadowSize *= 0.5F;
		}

		super.render(turtleEntity, d, e, f, g, h);
	}

	@Nullable
	protected Identifier getTexture(TurtleEntity turtleEntity) {
		return field_20950;
	}
}
