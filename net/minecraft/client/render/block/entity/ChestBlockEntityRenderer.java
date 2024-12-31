package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.render.entity.model.ChestBlockEntityModel;
import net.minecraft.client.render.entity.model.LargeChestBlockEntityModel;
import net.minecraft.util.Identifier;

public class ChestBlockEntityRenderer extends BlockEntityRenderer<ChestBlockEntity> {
	private static final Identifier TRAPPED_DOUBLE_TEXTURE = new Identifier("textures/entity/chest/trapped_double.png");
	private static final Identifier CHRISTMAS_DOUBLE_TEXTURE = new Identifier("textures/entity/chest/christmas_double.png");
	private static final Identifier NORMAL_DOUBLE_TEXTURE = new Identifier("textures/entity/chest/normal_double.png");
	private static final Identifier TRAPPED_TEXTURE = new Identifier("textures/entity/chest/trapped.png");
	private static final Identifier CHRISTMAS_TEXTURE = new Identifier("textures/entity/chest/christmas.png");
	private static final Identifier NORMAL_TEXTURE = new Identifier("textures/entity/chest/normal.png");
	private ChestBlockEntityModel model = new ChestBlockEntityModel();
	private ChestBlockEntityModel largeModel = new LargeChestBlockEntityModel();
	private boolean christmas;

	public ChestBlockEntityRenderer() {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.christmas = true;
		}
	}

	public void render(ChestBlockEntity chestBlockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		int j;
		if (!chestBlockEntity.hasWorld()) {
			j = 0;
		} else {
			Block block = chestBlockEntity.getBlock();
			j = chestBlockEntity.getDataValue();
			if (block instanceof ChestBlock && j == 0) {
				((ChestBlock)block)
					.getNearbyChest(chestBlockEntity.getEntityWorld(), chestBlockEntity.getPos(), chestBlockEntity.getEntityWorld().getBlockState(chestBlockEntity.getPos()));
				j = chestBlockEntity.getDataValue();
			}

			chestBlockEntity.checkNeighborChests();
		}

		if (chestBlockEntity.neighborChestNorth == null && chestBlockEntity.neighborChestWest == null) {
			ChestBlockEntityModel chestBlockEntityModel2;
			if (chestBlockEntity.neighborChestEast == null && chestBlockEntity.neighborChestSouth == null) {
				chestBlockEntityModel2 = this.model;
				if (i >= 0) {
					this.bindTexture(DESTROY_STAGE_TEXTURE[i]);
					GlStateManager.matrixMode(5890);
					GlStateManager.pushMatrix();
					GlStateManager.scale(4.0F, 4.0F, 1.0F);
					GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
					GlStateManager.matrixMode(5888);
				} else if (this.christmas) {
					this.bindTexture(CHRISTMAS_TEXTURE);
				} else if (chestBlockEntity.method_4806() == ChestBlock.Type.TRAP) {
					this.bindTexture(TRAPPED_TEXTURE);
				} else {
					this.bindTexture(NORMAL_TEXTURE);
				}
			} else {
				chestBlockEntityModel2 = this.largeModel;
				if (i >= 0) {
					this.bindTexture(DESTROY_STAGE_TEXTURE[i]);
					GlStateManager.matrixMode(5890);
					GlStateManager.pushMatrix();
					GlStateManager.scale(8.0F, 4.0F, 1.0F);
					GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
					GlStateManager.matrixMode(5888);
				} else if (this.christmas) {
					this.bindTexture(CHRISTMAS_DOUBLE_TEXTURE);
				} else if (chestBlockEntity.method_4806() == ChestBlock.Type.TRAP) {
					this.bindTexture(TRAPPED_DOUBLE_TEXTURE);
				} else {
					this.bindTexture(NORMAL_DOUBLE_TEXTURE);
				}
			}

			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();
			if (i < 0) {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}

			GlStateManager.translate((float)d, (float)e + 1.0F, (float)f + 1.0F);
			GlStateManager.scale(1.0F, -1.0F, -1.0F);
			GlStateManager.translate(0.5F, 0.5F, 0.5F);
			int l = 0;
			if (j == 2) {
				l = 180;
			}

			if (j == 3) {
				l = 0;
			}

			if (j == 4) {
				l = 90;
			}

			if (j == 5) {
				l = -90;
			}

			if (j == 2 && chestBlockEntity.neighborChestEast != null) {
				GlStateManager.translate(1.0F, 0.0F, 0.0F);
			}

			if (j == 5 && chestBlockEntity.neighborChestSouth != null) {
				GlStateManager.translate(0.0F, 0.0F, -1.0F);
			}

			GlStateManager.rotate((float)l, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			float h = chestBlockEntity.animationAnglePrev + (chestBlockEntity.animationAngle - chestBlockEntity.animationAnglePrev) * g;
			if (chestBlockEntity.neighborChestNorth != null) {
				float m = chestBlockEntity.neighborChestNorth.animationAnglePrev
					+ (chestBlockEntity.neighborChestNorth.animationAngle - chestBlockEntity.neighborChestNorth.animationAnglePrev) * g;
				if (m > h) {
					h = m;
				}
			}

			if (chestBlockEntity.neighborChestWest != null) {
				float n = chestBlockEntity.neighborChestWest.animationAnglePrev
					+ (chestBlockEntity.neighborChestWest.animationAngle - chestBlockEntity.neighborChestWest.animationAnglePrev) * g;
				if (n > h) {
					h = n;
				}
			}

			h = 1.0F - h;
			h = 1.0F - h * h * h;
			chestBlockEntityModel2.lid.posX = -(h * (float) (Math.PI / 2));
			chestBlockEntityModel2.renderParts();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			if (i >= 0) {
				GlStateManager.matrixMode(5890);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5888);
			}
		}
	}
}
