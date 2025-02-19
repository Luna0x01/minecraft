package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

public class DonkeyEntityModel<T extends AbstractDonkeyEntity> extends HorseEntityModel<T> {
	private final ModelPart leftChest = this.body.getChild("left_chest");
	private final ModelPart rightChest = this.body.getChild("right_chest");

	public DonkeyEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = HorseEntityModel.getModelData(Dilation.NONE);
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.getChild("body");
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(26, 21).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 3.0F);
		modelPartData2.addChild("left_chest", modelPartBuilder, ModelTransform.of(6.0F, -8.0F, 0.0F, 0.0F, (float) (-Math.PI / 2), 0.0F));
		modelPartData2.addChild("right_chest", modelPartBuilder, ModelTransform.of(-6.0F, -8.0F, 0.0F, 0.0F, (float) (Math.PI / 2), 0.0F));
		ModelPartData modelPartData3 = modelPartData.getChild("head_parts").getChild("head");
		ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 12).cuboid(-1.0F, -7.0F, 0.0F, 2.0F, 7.0F, 1.0F);
		modelPartData3.addChild("left_ear", modelPartBuilder2, ModelTransform.of(1.25F, -10.0F, 4.0F, (float) (Math.PI / 12), 0.0F, (float) (Math.PI / 12)));
		modelPartData3.addChild("right_ear", modelPartBuilder2, ModelTransform.of(-1.25F, -10.0F, 4.0F, (float) (Math.PI / 12), 0.0F, (float) (-Math.PI / 12)));
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void setAngles(T abstractDonkeyEntity, float f, float g, float h, float i, float j) {
		super.setAngles(abstractDonkeyEntity, f, g, h, i, j);
		if (abstractDonkeyEntity.hasChest()) {
			this.leftChest.visible = true;
			this.rightChest.visible = true;
		} else {
			this.leftChest.visible = false;
			this.rightChest.visible = false;
		}
	}
}
