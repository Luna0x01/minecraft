package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.ModelBox;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TextureOffset;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.GlAllocationUtils;

public class ModelPart {
	public float textureWidth = 64.0F;
	public float textureHeight = 32.0F;
	private int textureOffsetU;
	private int textureOffsetV;
	public float pivotX;
	public float pivotY;
	public float pivotZ;
	public float posX;
	public float posY;
	public float posZ;
	private boolean compiledList;
	private int glList;
	public boolean mirror;
	public boolean visible = true;
	public boolean hide;
	public List<ModelBox> cuboids = Lists.newArrayList();
	public List<ModelPart> modelList;
	public final String name;
	private final EntityModel renderer;
	public float offsetX;
	public float offsetY;
	public float offsetZ;

	public ModelPart(EntityModel entityModel, String string) {
		this.renderer = entityModel;
		entityModel.parts.add(this);
		this.name = string;
		this.setTextureSize(entityModel.textureWidth, entityModel.textureHeight);
	}

	public ModelPart(EntityModel entityModel) {
		this(entityModel, null);
	}

	public ModelPart(EntityModel entityModel, int i, int j) {
		this(entityModel);
		this.setTextureOffset(i, j);
	}

	public void add(ModelPart part) {
		if (this.modelList == null) {
			this.modelList = Lists.newArrayList();
		}

		this.modelList.add(part);
	}

	public ModelPart setTextureOffset(int textureOffsetU, int textureOffsetV) {
		this.textureOffsetU = textureOffsetU;
		this.textureOffsetV = textureOffsetV;
		return this;
	}

	public ModelPart addCuboid(String name, float x, float y, float z, int sizeX, int sizeY, int sizeZ) {
		name = this.name + "." + name;
		TextureOffset textureOffset = this.renderer.getTexture(name);
		this.setTextureOffset(textureOffset.x, textureOffset.y);
		this.cuboids.add(new ModelBox(this, this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, 0.0F).setName(name));
		return this;
	}

	public ModelPart addCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ) {
		this.cuboids.add(new ModelBox(this, this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, 0.0F));
		return this;
	}

	public ModelPart addCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ, boolean mirror) {
		this.cuboids.add(new ModelBox(this, this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, 0.0F, mirror));
		return this;
	}

	public void addCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ, float reduction) {
		this.cuboids.add(new ModelBox(this, this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, reduction));
	}

	public void method_18947(float f, float g, float h, int i, int j, int k, float l, boolean bl) {
		this.cuboids.add(new ModelBox(this, this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, bl));
	}

	public void setPivot(float x, float y, float z) {
		this.pivotX = x;
		this.pivotY = y;
		this.pivotZ = z;
	}

	public void render(float scale) {
		if (!this.hide) {
			if (this.visible) {
				if (!this.compiledList) {
					this.compileList(scale);
				}

				GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);
				if (this.posX != 0.0F || this.posY != 0.0F || this.posZ != 0.0F) {
					GlStateManager.pushMatrix();
					GlStateManager.translate(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
					if (this.posZ != 0.0F) {
						GlStateManager.rotate(this.posZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
					}

					if (this.posY != 0.0F) {
						GlStateManager.rotate(this.posY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
					}

					if (this.posX != 0.0F) {
						GlStateManager.rotate(this.posX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
					}

					GlStateManager.callList(this.glList);
					if (this.modelList != null) {
						for (int i = 0; i < this.modelList.size(); i++) {
							((ModelPart)this.modelList.get(i)).render(scale);
						}
					}

					GlStateManager.popMatrix();
				} else if (this.pivotX == 0.0F && this.pivotY == 0.0F && this.pivotZ == 0.0F) {
					GlStateManager.callList(this.glList);
					if (this.modelList != null) {
						for (int k = 0; k < this.modelList.size(); k++) {
							((ModelPart)this.modelList.get(k)).render(scale);
						}
					}
				} else {
					GlStateManager.translate(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
					GlStateManager.callList(this.glList);
					if (this.modelList != null) {
						for (int j = 0; j < this.modelList.size(); j++) {
							((ModelPart)this.modelList.get(j)).render(scale);
						}
					}

					GlStateManager.translate(-this.pivotX * scale, -this.pivotY * scale, -this.pivotZ * scale);
				}

				GlStateManager.translate(-this.offsetX, -this.offsetY, -this.offsetZ);
			}
		}
	}

	public void rotateAndRender(float scale) {
		if (!this.hide) {
			if (this.visible) {
				if (!this.compiledList) {
					this.compileList(scale);
				}

				GlStateManager.pushMatrix();
				GlStateManager.translate(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
				if (this.posY != 0.0F) {
					GlStateManager.rotate(this.posY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
				}

				if (this.posX != 0.0F) {
					GlStateManager.rotate(this.posX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
				}

				if (this.posZ != 0.0F) {
					GlStateManager.rotate(this.posZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
				}

				GlStateManager.callList(this.glList);
				GlStateManager.popMatrix();
			}
		}
	}

	public void preRender(float scale) {
		if (!this.hide) {
			if (this.visible) {
				if (!this.compiledList) {
					this.compileList(scale);
				}

				if (this.posX != 0.0F || this.posY != 0.0F || this.posZ != 0.0F) {
					GlStateManager.translate(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
					if (this.posZ != 0.0F) {
						GlStateManager.rotate(this.posZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
					}

					if (this.posY != 0.0F) {
						GlStateManager.rotate(this.posY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
					}

					if (this.posX != 0.0F) {
						GlStateManager.rotate(this.posX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
					}
				} else if (this.pivotX != 0.0F || this.pivotY != 0.0F || this.pivotZ != 0.0F) {
					GlStateManager.translate(this.pivotX * scale, this.pivotY * scale, this.pivotZ * scale);
				}
			}
		}
	}

	private void compileList(float scale) {
		this.glList = GlAllocationUtils.genLists(1);
		GlStateManager.method_12312(this.glList, 4864);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

		for (int i = 0; i < this.cuboids.size(); i++) {
			((ModelBox)this.cuboids.get(i)).draw(bufferBuilder, scale);
		}

		GlStateManager.method_12270();
		this.compiledList = true;
	}

	public ModelPart setTextureSize(int width, int height) {
		this.textureWidth = (float)width;
		this.textureHeight = (float)height;
		return this;
	}
}
