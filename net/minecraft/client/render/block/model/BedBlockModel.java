package net.minecraft.client.render.block.model;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;

public class BedBlockModel extends EntityModel {
	public ModelPart field_16090;
	public ModelPart field_16091;
	public ModelPart[] field_16092 = new ModelPart[4];

	public BedBlockModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_16090 = new ModelPart(this, 0, 0);
		this.field_16090.addCuboid(0.0F, 0.0F, 0.0F, 16, 16, 6, 0.0F);
		this.field_16091 = new ModelPart(this, 0, 22);
		this.field_16091.addCuboid(0.0F, 0.0F, 0.0F, 16, 16, 6, 0.0F);
		this.field_16092[0] = new ModelPart(this, 50, 0);
		this.field_16092[1] = new ModelPart(this, 50, 6);
		this.field_16092[2] = new ModelPart(this, 50, 12);
		this.field_16092[3] = new ModelPart(this, 50, 18);
		this.field_16092[0].addCuboid(0.0F, 6.0F, -16.0F, 3, 3, 3);
		this.field_16092[1].addCuboid(0.0F, 6.0F, 0.0F, 3, 3, 3);
		this.field_16092[2].addCuboid(-16.0F, 6.0F, -16.0F, 3, 3, 3);
		this.field_16092[3].addCuboid(-16.0F, 6.0F, 0.0F, 3, 3, 3);
		this.field_16092[0].posX = (float) (Math.PI / 2);
		this.field_16092[1].posX = (float) (Math.PI / 2);
		this.field_16092[2].posX = (float) (Math.PI / 2);
		this.field_16092[3].posX = (float) (Math.PI / 2);
		this.field_16092[0].posZ = 0.0F;
		this.field_16092[1].posZ = (float) (Math.PI / 2);
		this.field_16092[2].posZ = (float) (Math.PI * 3.0 / 2.0);
		this.field_16092[3].posZ = (float) Math.PI;
	}

	public int method_14644() {
		return 51;
	}

	public void method_14646() {
		this.field_16090.render(0.0625F);
		this.field_16091.render(0.0625F);
		this.field_16092[0].render(0.0625F);
		this.field_16092[1].render(0.0625F);
		this.field_16092[2].render(0.0625F);
		this.field_16092[3].render(0.0625F);
	}

	public void method_14645(boolean visible) {
		this.field_16090.visible = visible;
		this.field_16091.visible = !visible;
		this.field_16092[0].visible = !visible;
		this.field_16092[1].visible = visible;
		this.field_16092[2].visible = !visible;
		this.field_16092[3].visible = visible;
	}
}
