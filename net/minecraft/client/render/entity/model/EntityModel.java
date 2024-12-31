package net.minecraft.client.render.entity.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.render.TextureOffset;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public abstract class EntityModel {
	public float handSwingProgress;
	public boolean riding;
	public boolean child = true;
	public List<ModelPart> parts = Lists.newArrayList();
	private Map<String, TextureOffset> textureMap = Maps.newHashMap();
	public int textureWidth = 64;
	public int textureHeight = 32;

	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
	}

	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
	}

	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
	}

	public ModelPart method_4273(Random random) {
		return (ModelPart)this.parts.get(random.nextInt(this.parts.size()));
	}

	protected void putTexture(String id, int x, int y) {
		this.textureMap.put(id, new TextureOffset(x, y));
	}

	public TextureOffset getTexture(String id) {
		return (TextureOffset)this.textureMap.get(id);
	}

	public static void copyModelPart(ModelPart first, ModelPart second) {
		second.posX = first.posX;
		second.posY = first.posY;
		second.posZ = first.posZ;
		second.pivotX = first.pivotX;
		second.pivotY = first.pivotY;
		second.pivotZ = first.pivotZ;
	}

	public void copy(EntityModel model) {
		this.handSwingProgress = model.handSwingProgress;
		this.riding = model.riding;
		this.child = model.child;
	}
}
