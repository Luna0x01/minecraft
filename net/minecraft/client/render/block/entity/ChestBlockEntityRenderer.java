package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Calendar;
import net.minecraft.class_3743;
import net.minecraft.class_3746;
import net.minecraft.class_4239;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.entity.model.ChestBlockEntityModel;
import net.minecraft.client.render.entity.model.LargeChestBlockEntityModel;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ChestBlockEntityRenderer<T extends BlockEntity & class_3743> extends class_4239<T> {
	private static final Identifier TRAPPED_DOUBLE_TEXTURE = new Identifier("textures/entity/chest/trapped_double.png");
	private static final Identifier CHRISTMAS_DOUBLE_TEXTURE = new Identifier("textures/entity/chest/christmas_double.png");
	private static final Identifier NORMAL_DOUBLE_TEXTURE = new Identifier("textures/entity/chest/normal_double.png");
	private static final Identifier TRAPPED_TEXTURE = new Identifier("textures/entity/chest/trapped.png");
	private static final Identifier CHRISTMAS_TEXTURE = new Identifier("textures/entity/chest/christmas.png");
	private static final Identifier NORMAL_TEXTURE = new Identifier("textures/entity/chest/normal.png");
	private static final Identifier field_20848 = new Identifier("textures/entity/chest/ender.png");
	private final ChestBlockEntityModel model = new ChestBlockEntityModel();
	private final ChestBlockEntityModel largeModel = new LargeChestBlockEntityModel();
	private boolean christmas;

	public ChestBlockEntityRenderer() {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.christmas = true;
		}
	}

	@Override
	public void method_1631(T blockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		BlockState blockState = blockEntity.hasWorld() ? blockEntity.method_16783() : Blocks.CHEST.getDefaultState().withProperty(ChestBlock.FACING, Direction.SOUTH);
		ChestType chestType = blockState.method_16933((Property<T>)ChestBlock.CHEST_TYPE) ? blockState.getProperty(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
		if (chestType != ChestType.LEFT) {
			boolean bl = chestType != ChestType.SINGLE;
			ChestBlockEntityModel chestBlockEntityModel = this.method_19331(blockEntity, i, bl);
			if (i >= 0) {
				GlStateManager.matrixMode(5890);
				GlStateManager.pushMatrix();
				GlStateManager.scale(bl ? 8.0F : 4.0F, 4.0F, 1.0F);
				GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
				GlStateManager.matrixMode(5888);
			} else {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}

			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();
			GlStateManager.translate((float)d, (float)e + 1.0F, (float)f + 1.0F);
			GlStateManager.scale(1.0F, -1.0F, -1.0F);
			float h = ((Direction)blockState.getProperty(ChestBlock.FACING)).method_12578();
			if ((double)Math.abs(h) > 1.0E-5) {
				GlStateManager.translate(0.5F, 0.5F, 0.5F);
				GlStateManager.rotate(h, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			}

			this.method_19330(blockEntity, g, chestBlockEntityModel);
			chestBlockEntityModel.renderParts();
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

	private ChestBlockEntityModel method_19331(T blockEntity, int i, boolean bl) {
		Identifier identifier;
		if (i >= 0) {
			identifier = field_20846[i];
		} else if (this.christmas) {
			identifier = bl ? CHRISTMAS_DOUBLE_TEXTURE : CHRISTMAS_TEXTURE;
		} else if (blockEntity instanceof class_3746) {
			identifier = bl ? TRAPPED_DOUBLE_TEXTURE : TRAPPED_TEXTURE;
		} else if (blockEntity instanceof EnderChestBlockEntity) {
			identifier = field_20848;
		} else {
			identifier = bl ? NORMAL_DOUBLE_TEXTURE : NORMAL_TEXTURE;
		}

		this.method_19327(identifier);
		return bl ? this.largeModel : this.model;
	}

	private void method_19330(T blockEntity, float f, ChestBlockEntityModel chestBlockEntityModel) {
		float g = blockEntity.method_16830(f);
		g = 1.0F - g;
		g = 1.0F - g * g * g;
		chestBlockEntityModel.method_18912().posX = -(g * (float) (Math.PI / 2));
	}
}
