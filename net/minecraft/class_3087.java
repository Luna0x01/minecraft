package net.minecraft;

import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class class_3087 extends EntityModel {
	public ModelPart field_15259;
	public ModelPart field_16093;
	public ModelPart field_15260;
	public ModelPart field_15261;
	public ModelPart field_15262;
	public ModelPart field_15263;
	public ModelPart field_15264;
	public ModelPart field_15265;
	public ModelPart field_15266;

	public class_3087(float f, float g, int i, int j) {
		this.field_15259 = new ModelPart(this).setTextureSize(i, j);
		this.field_15259.setPivot(0.0F, 0.0F + g, 0.0F);
		this.field_15259.setTextureOffset(0, 0).addCuboid(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
		this.field_16093 = new ModelPart(this, 32, 0).setTextureSize(i, j);
		this.field_16093.addCuboid(-4.0F, -10.0F, -4.0F, 8, 12, 8, f + 0.45F);
		this.field_15259.add(this.field_16093);
		this.field_16093.visible = false;
		this.field_15264 = new ModelPart(this).setTextureSize(i, j);
		this.field_15264.setPivot(0.0F, g - 2.0F, 0.0F);
		this.field_15264.setTextureOffset(24, 0).addCuboid(-1.0F, -1.0F, -6.0F, 2, 4, 2, f);
		this.field_15259.add(this.field_15264);
		this.field_15260 = new ModelPart(this).setTextureSize(i, j);
		this.field_15260.setPivot(0.0F, 0.0F + g, 0.0F);
		this.field_15260.setTextureOffset(16, 20).addCuboid(-4.0F, 0.0F, -3.0F, 8, 12, 6, f);
		this.field_15260.setTextureOffset(0, 38).addCuboid(-4.0F, 0.0F, -3.0F, 8, 18, 6, f + 0.5F);
		this.field_15261 = new ModelPart(this).setTextureSize(i, j);
		this.field_15261.setPivot(0.0F, 0.0F + g + 2.0F, 0.0F);
		this.field_15261.setTextureOffset(44, 22).addCuboid(-8.0F, -2.0F, -2.0F, 4, 8, 4, f);
		ModelPart modelPart = new ModelPart(this, 44, 22).setTextureSize(i, j);
		modelPart.mirror = true;
		modelPart.addCuboid(4.0F, -2.0F, -2.0F, 4, 8, 4, f);
		this.field_15261.add(modelPart);
		this.field_15261.setTextureOffset(40, 38).addCuboid(-4.0F, 2.0F, -2.0F, 8, 4, 4, f);
		this.field_15262 = new ModelPart(this, 0, 22).setTextureSize(i, j);
		this.field_15262.setPivot(-2.0F, 12.0F + g, 0.0F);
		this.field_15262.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_15263 = new ModelPart(this, 0, 22).setTextureSize(i, j);
		this.field_15263.mirror = true;
		this.field_15263.setPivot(2.0F, 12.0F + g, 0.0F);
		this.field_15263.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_15265 = new ModelPart(this, 40, 46).setTextureSize(i, j);
		this.field_15265.addCuboid(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.field_15265.setPivot(-5.0F, 2.0F + g, 0.0F);
		this.field_15266 = new ModelPart(this, 40, 46).setTextureSize(i, j);
		this.field_15266.mirror = true;
		this.field_15266.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.field_15266.setPivot(5.0F, 2.0F + g, 0.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_15259.render(scale);
		this.field_15260.render(scale);
		this.field_15262.render(scale);
		this.field_15263.render(scale);
		class_3162 lv = (class_3162)entity;
		if (lv.method_14123() == class_3162.class_3163.CROSSED) {
			this.field_15261.render(scale);
		} else {
			this.field_15265.render(scale);
			this.field_15266.render(scale);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		this.field_15259.posY = age * (float) (Math.PI / 180.0);
		this.field_15259.posX = headPitch * (float) (Math.PI / 180.0);
		this.field_15261.pivotY = 3.0F;
		this.field_15261.pivotZ = -1.0F;
		this.field_15261.posX = -0.75F;
		this.field_15262.posX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount * 0.5F;
		this.field_15263.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount * 0.5F;
		this.field_15262.posY = 0.0F;
		this.field_15263.posY = 0.0F;
		class_3162.class_3163 lv = ((class_3162)entity).method_14123();
		if (lv == class_3162.class_3163.ATTACKING) {
			float f = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
			float g = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
			this.field_15265.posZ = 0.0F;
			this.field_15266.posZ = 0.0F;
			this.field_15265.posY = (float) (Math.PI / 20);
			this.field_15266.posY = (float) (-Math.PI / 20);
			if (((LivingEntity)entity).getDurability() == HandOption.RIGHT) {
				this.field_15265.posX = -1.8849558F + MathHelper.cos(tickDelta * 0.09F) * 0.15F;
				this.field_15266.posX = -0.0F + MathHelper.cos(tickDelta * 0.19F) * 0.5F;
				this.field_15265.posX += f * 2.2F - g * 0.4F;
				this.field_15266.posX += f * 1.2F - g * 0.4F;
			} else {
				this.field_15265.posX = -0.0F + MathHelper.cos(tickDelta * 0.19F) * 0.5F;
				this.field_15266.posX = -1.8849558F + MathHelper.cos(tickDelta * 0.09F) * 0.15F;
				this.field_15265.posX += f * 1.2F - g * 0.4F;
				this.field_15266.posX += f * 2.2F - g * 0.4F;
			}

			this.field_15265.posZ = this.field_15265.posZ + MathHelper.cos(tickDelta * 0.09F) * 0.05F + 0.05F;
			this.field_15266.posZ = this.field_15266.posZ - (MathHelper.cos(tickDelta * 0.09F) * 0.05F + 0.05F);
			this.field_15265.posX = this.field_15265.posX + MathHelper.sin(tickDelta * 0.067F) * 0.05F;
			this.field_15266.posX = this.field_15266.posX - MathHelper.sin(tickDelta * 0.067F) * 0.05F;
		} else if (lv == class_3162.class_3163.SPELLCASTING) {
			this.field_15265.pivotZ = 0.0F;
			this.field_15265.pivotX = -5.0F;
			this.field_15266.pivotZ = 0.0F;
			this.field_15266.pivotX = 5.0F;
			this.field_15265.posX = MathHelper.cos(tickDelta * 0.6662F) * 0.25F;
			this.field_15266.posX = MathHelper.cos(tickDelta * 0.6662F) * 0.25F;
			this.field_15265.posZ = (float) (Math.PI * 3.0 / 4.0);
			this.field_15266.posZ = (float) (-Math.PI * 3.0 / 4.0);
			this.field_15265.posY = 0.0F;
			this.field_15266.posY = 0.0F;
		} else if (lv == class_3162.class_3163.BOW_AND_ARROW) {
			this.field_15265.posY = -0.1F + this.field_15259.posY;
			this.field_15265.posX = (float) (-Math.PI / 2) + this.field_15259.posX;
			this.field_15266.posX = -0.9424779F + this.field_15259.posX;
			this.field_15266.posY = this.field_15259.posY - 0.4F;
			this.field_15266.posZ = (float) (Math.PI / 2);
		}
	}

	public ModelPart method_13840(HandOption handOption) {
		return handOption == HandOption.LEFT ? this.field_15266 : this.field_15265;
	}
}
