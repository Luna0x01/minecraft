package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.util.math.MathHelper;

public class VillagerResemblingModel<T extends Entity> extends CompositeEntityModel<T> implements ModelWithHead, ModelWithHat {
	protected ModelPart head;
	protected ModelPart field_17141;
	protected final ModelPart field_17142;
	protected final ModelPart torso;
	protected final ModelPart robe;
	protected final ModelPart arms;
	protected final ModelPart rightLeg;
	protected final ModelPart leftLeg;
	protected final ModelPart nose;

	public VillagerResemblingModel(float f) {
		this(f, 64, 64);
	}

	public VillagerResemblingModel(float f, int i, int j) {
		float g = 0.5F;
		this.head = new ModelPart(this).setTextureSize(i, j);
		this.head.setPivot(0.0F, 0.0F, 0.0F);
		this.head.setTextureOffset(0, 0).addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, f);
		this.field_17141 = new ModelPart(this).setTextureSize(i, j);
		this.field_17141.setPivot(0.0F, 0.0F, 0.0F);
		this.field_17141.setTextureOffset(32, 0).addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, f + 0.5F);
		this.head.addChild(this.field_17141);
		this.field_17142 = new ModelPart(this).setTextureSize(i, j);
		this.field_17142.setPivot(0.0F, 0.0F, 0.0F);
		this.field_17142.setTextureOffset(30, 47).addCuboid(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, f);
		this.field_17142.pitch = (float) (-Math.PI / 2);
		this.field_17141.addChild(this.field_17142);
		this.nose = new ModelPart(this).setTextureSize(i, j);
		this.nose.setPivot(0.0F, -2.0F, 0.0F);
		this.nose.setTextureOffset(24, 0).addCuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, f);
		this.head.addChild(this.nose);
		this.torso = new ModelPart(this).setTextureSize(i, j);
		this.torso.setPivot(0.0F, 0.0F, 0.0F);
		this.torso.setTextureOffset(16, 20).addCuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, f);
		this.robe = new ModelPart(this).setTextureSize(i, j);
		this.robe.setPivot(0.0F, 0.0F, 0.0F);
		this.robe.setTextureOffset(0, 38).addCuboid(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, f + 0.5F);
		this.torso.addChild(this.robe);
		this.arms = new ModelPart(this).setTextureSize(i, j);
		this.arms.setPivot(0.0F, 2.0F, 0.0F);
		this.arms.setTextureOffset(44, 22).addCuboid(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, f);
		this.arms.setTextureOffset(44, 22).addCuboid(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, f, true);
		this.arms.setTextureOffset(40, 38).addCuboid(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, f);
		this.rightLeg = new ModelPart(this, 0, 22).setTextureSize(i, j);
		this.rightLeg.setPivot(-2.0F, 12.0F, 0.0F);
		this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.leftLeg = new ModelPart(this, 0, 22).setTextureSize(i, j);
		this.leftLeg.mirror = true;
		this.leftLeg.setPivot(2.0F, 12.0F, 0.0F);
		this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.of(this.head, this.torso, this.rightLeg, this.leftLeg, this.arms);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j) {
		boolean bl = false;
		if (entity instanceof AbstractTraderEntity) {
			bl = ((AbstractTraderEntity)entity).getHeadRollingTimeLeft() > 0;
		}

		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		if (bl) {
			this.head.roll = 0.3F * MathHelper.sin(0.45F * h);
			this.head.pitch = 0.4F;
		} else {
			this.head.roll = 0.0F;
		}

		this.arms.pivotY = 3.0F;
		this.arms.pivotZ = -1.0F;
		this.arms.pitch = -0.75F;
		this.rightLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g * 0.5F;
		this.leftLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g * 0.5F;
		this.rightLeg.yaw = 0.0F;
		this.leftLeg.yaw = 0.0F;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public void setHatVisible(boolean bl) {
		this.head.visible = bl;
		this.field_17141.visible = bl;
		this.field_17142.visible = bl;
	}
}
