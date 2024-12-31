package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BookModel extends EntityModel {
	private final ModelPart leftCover = new ModelPart(this).setTextureOffset(0, 0).addCuboid(-6.0F, -5.0F, 0.0F, 6, 10, 0);
	private final ModelPart rightCover = new ModelPart(this).setTextureOffset(16, 0).addCuboid(0.0F, -5.0F, 0.0F, 6, 10, 0);
	private final ModelPart leftBlock;
	private final ModelPart rightBlock;
	private final ModelPart leftPage;
	private final ModelPart rightPage;
	private final ModelPart spine = new ModelPart(this).setTextureOffset(12, 0).addCuboid(-1.0F, -5.0F, 0.0F, 2, 10, 0);

	public BookModel() {
		this.leftBlock = new ModelPart(this).setTextureOffset(0, 10).addCuboid(0.0F, -4.0F, -0.99F, 5, 8, 1);
		this.rightBlock = new ModelPart(this).setTextureOffset(12, 10).addCuboid(0.0F, -4.0F, -0.01F, 5, 8, 1);
		this.leftPage = new ModelPart(this).setTextureOffset(24, 10).addCuboid(0.0F, -4.0F, 0.0F, 5, 8, 0);
		this.rightPage = new ModelPart(this).setTextureOffset(24, 10).addCuboid(0.0F, -4.0F, 0.0F, 5, 8, 0);
		this.leftCover.setPivot(0.0F, 0.0F, -1.0F);
		this.rightCover.setPivot(0.0F, 0.0F, 1.0F);
		this.spine.posY = (float) (Math.PI / 2);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.leftCover.render(scale);
		this.rightCover.render(scale);
		this.spine.render(scale);
		this.leftBlock.render(scale);
		this.rightBlock.render(scale);
		this.leftPage.render(scale);
		this.rightPage.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		float f = (MathHelper.sin(handSwing * 0.02F) * 0.1F + 1.25F) * age;
		this.leftCover.posY = (float) Math.PI + f;
		this.rightCover.posY = -f;
		this.leftBlock.posY = f;
		this.rightBlock.posY = -f;
		this.leftPage.posY = f - f * 2.0F * handSwingAmount;
		this.rightPage.posY = f - f * 2.0F * tickDelta;
		this.leftBlock.pivotX = MathHelper.sin(f);
		this.rightBlock.pivotX = MathHelper.sin(f);
		this.leftPage.pivotX = MathHelper.sin(f);
		this.rightPage.pivotX = MathHelper.sin(f);
	}
}
