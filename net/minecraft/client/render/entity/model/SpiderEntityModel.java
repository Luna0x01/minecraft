package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SpiderEntityModel extends EntityModel {
	private final ModelPart field_1535;
	private final ModelPart field_1536;
	private final ModelPart field_1537;
	private final ModelPart field_1538;
	private final ModelPart field_1539;
	private final ModelPart field_1540;
	private final ModelPart field_1541;
	private final ModelPart field_1542;
	private final ModelPart field_1543;
	private final ModelPart field_1544;
	private final ModelPart field_1545;

	public SpiderEntityModel() {
		float f = 0.0F;
		int i = 15;
		this.field_1535 = new ModelPart(this, 32, 4);
		this.field_1535.addCuboid(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F);
		this.field_1535.setPivot(0.0F, 15.0F, -3.0F);
		this.field_1536 = new ModelPart(this, 0, 0);
		this.field_1536.addCuboid(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F);
		this.field_1536.setPivot(0.0F, 15.0F, 0.0F);
		this.field_1537 = new ModelPart(this, 0, 12);
		this.field_1537.addCuboid(-5.0F, -4.0F, -6.0F, 10, 8, 12, 0.0F);
		this.field_1537.setPivot(0.0F, 15.0F, 9.0F);
		this.field_1538 = new ModelPart(this, 18, 0);
		this.field_1538.addCuboid(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_1538.setPivot(-4.0F, 15.0F, 2.0F);
		this.field_1539 = new ModelPart(this, 18, 0);
		this.field_1539.addCuboid(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_1539.setPivot(4.0F, 15.0F, 2.0F);
		this.field_1540 = new ModelPart(this, 18, 0);
		this.field_1540.addCuboid(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_1540.setPivot(-4.0F, 15.0F, 1.0F);
		this.field_1541 = new ModelPart(this, 18, 0);
		this.field_1541.addCuboid(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_1541.setPivot(4.0F, 15.0F, 1.0F);
		this.field_1542 = new ModelPart(this, 18, 0);
		this.field_1542.addCuboid(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_1542.setPivot(-4.0F, 15.0F, 0.0F);
		this.field_1543 = new ModelPart(this, 18, 0);
		this.field_1543.addCuboid(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_1543.setPivot(4.0F, 15.0F, 0.0F);
		this.field_1544 = new ModelPart(this, 18, 0);
		this.field_1544.addCuboid(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_1544.setPivot(-4.0F, 15.0F, -1.0F);
		this.field_1545 = new ModelPart(this, 18, 0);
		this.field_1545.addCuboid(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		this.field_1545.setPivot(4.0F, 15.0F, -1.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_1535.render(scale);
		this.field_1536.render(scale);
		this.field_1537.render(scale);
		this.field_1538.render(scale);
		this.field_1539.render(scale);
		this.field_1540.render(scale);
		this.field_1541.render(scale);
		this.field_1542.render(scale);
		this.field_1543.render(scale);
		this.field_1544.render(scale);
		this.field_1545.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		this.field_1535.posY = age * (float) (Math.PI / 180.0);
		this.field_1535.posX = headPitch * (float) (Math.PI / 180.0);
		float f = (float) (Math.PI / 4);
		this.field_1538.posZ = (float) (-Math.PI / 4);
		this.field_1539.posZ = (float) (Math.PI / 4);
		this.field_1540.posZ = -0.58119464F;
		this.field_1541.posZ = 0.58119464F;
		this.field_1542.posZ = -0.58119464F;
		this.field_1543.posZ = 0.58119464F;
		this.field_1544.posZ = (float) (-Math.PI / 4);
		this.field_1545.posZ = (float) (Math.PI / 4);
		float g = -0.0F;
		float h = (float) (Math.PI / 8);
		this.field_1538.posY = (float) (Math.PI / 4);
		this.field_1539.posY = (float) (-Math.PI / 4);
		this.field_1540.posY = (float) (Math.PI / 8);
		this.field_1541.posY = (float) (-Math.PI / 8);
		this.field_1542.posY = (float) (-Math.PI / 8);
		this.field_1543.posY = (float) (Math.PI / 8);
		this.field_1544.posY = (float) (-Math.PI / 4);
		this.field_1545.posY = (float) (Math.PI / 4);
		float i = -(MathHelper.cos(handSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * handSwingAmount;
		float j = -(MathHelper.cos(handSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * handSwingAmount;
		float k = -(MathHelper.cos(handSwing * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * handSwingAmount;
		float l = -(MathHelper.cos(handSwing * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * handSwingAmount;
		float m = Math.abs(MathHelper.sin(handSwing * 0.6662F + 0.0F) * 0.4F) * handSwingAmount;
		float n = Math.abs(MathHelper.sin(handSwing * 0.6662F + (float) Math.PI) * 0.4F) * handSwingAmount;
		float o = Math.abs(MathHelper.sin(handSwing * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * handSwingAmount;
		float p = Math.abs(MathHelper.sin(handSwing * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * handSwingAmount;
		this.field_1538.posY += i;
		this.field_1539.posY += -i;
		this.field_1540.posY += j;
		this.field_1541.posY += -j;
		this.field_1542.posY += k;
		this.field_1543.posY += -k;
		this.field_1544.posY += l;
		this.field_1545.posY += -l;
		this.field_1538.posZ += m;
		this.field_1539.posZ += -m;
		this.field_1540.posZ += n;
		this.field_1541.posZ += -n;
		this.field_1542.posZ += o;
		this.field_1543.posZ += -o;
		this.field_1544.posZ += p;
		this.field_1545.posZ += -p;
	}
}
