package net.minecraft;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.util.Identifier;

public class class_4197 extends EntityModel {
	public static final Identifier field_20592 = new Identifier("textures/entity/trident.png");
	private final ModelPart field_20593;

	public class_4197() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.field_20593 = new ModelPart(this, 0, 0);
		this.field_20593.addCuboid(-0.5F, -4.0F, -0.5F, 1, 31, 1, 0.0F);
		ModelPart modelPart = new ModelPart(this, 4, 0);
		modelPart.addCuboid(-1.5F, 0.0F, -0.5F, 3, 2, 1);
		this.field_20593.add(modelPart);
		ModelPart modelPart2 = new ModelPart(this, 4, 3);
		modelPart2.addCuboid(-2.5F, -3.0F, -0.5F, 1, 4, 1);
		this.field_20593.add(modelPart2);
		ModelPart modelPart3 = new ModelPart(this, 4, 3);
		modelPart3.mirror = true;
		modelPart3.addCuboid(1.5F, -3.0F, -0.5F, 1, 4, 1);
		this.field_20593.add(modelPart3);
	}

	public void method_18943() {
		this.field_20593.render(0.0625F);
	}
}
