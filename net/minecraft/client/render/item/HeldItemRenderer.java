package net.minecraft.client.render.item;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class HeldItemRenderer {
	private static final RenderLayer MAP_BACKGROUND = RenderLayer.getText(new Identifier("textures/map/map_background.png"));
	private static final RenderLayer MAP_BACKGROUND_CHECKERBOARD = RenderLayer.getText(new Identifier("textures/map/map_background_checkerboard.png"));
	private final MinecraftClient client;
	private ItemStack mainHand = ItemStack.EMPTY;
	private ItemStack offHand = ItemStack.EMPTY;
	private float equipProgressMainHand;
	private float prevEquipProgressMainHand;
	private float equipProgressOffHand;
	private float prevEquipProgressOffHand;
	private final EntityRenderDispatcher renderManager;
	private final ItemRenderer itemRenderer;

	public HeldItemRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
		this.renderManager = minecraftClient.getEntityRenderManager();
		this.itemRenderer = minecraftClient.getItemRenderer();
	}

	public void renderItem(
		LivingEntity livingEntity,
		ItemStack itemStack,
		ModelTransformation.Mode mode,
		boolean bl,
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i
	) {
		if (!itemStack.isEmpty()) {
			this.itemRenderer.renderItem(livingEntity, itemStack, mode, bl, matrixStack, vertexConsumerProvider, livingEntity.world, i, OverlayTexture.DEFAULT_UV);
		}
	}

	private float getMapAngle(float f) {
		float g = 1.0F - f / 45.0F + 0.1F;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		return -MathHelper.cos(g * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void renderArm(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, Arm arm) {
		this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(this.client.player);
		matrixStack.push();
		float f = arm == Arm.field_6183 ? 1.0F : -1.0F;
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(92.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(45.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * -41.0F));
		matrixStack.translate((double)(f * 0.3F), -1.1F, 0.45F);
		if (arm == Arm.field_6183) {
			playerEntityRenderer.renderRightArm(matrixStack, vertexConsumerProvider, i, this.client.player);
		} else {
			playerEntityRenderer.renderLeftArm(matrixStack, vertexConsumerProvider, i, this.client.player);
		}

		matrixStack.pop();
	}

	private void renderMapInOneHand(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, Arm arm, float g, ItemStack itemStack) {
		float h = arm == Arm.field_6183 ? 1.0F : -1.0F;
		matrixStack.translate((double)(h * 0.125F), -0.125, 0.0);
		if (!this.client.player.isInvisible()) {
			matrixStack.push();
			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(h * 10.0F));
			this.renderArmHoldingItem(matrixStack, vertexConsumerProvider, i, f, g, arm);
			matrixStack.pop();
		}

		matrixStack.push();
		matrixStack.translate((double)(h * 0.51F), (double)(-0.08F + f * -1.2F), -0.75);
		float j = MathHelper.sqrt(g);
		float k = MathHelper.sin(j * (float) Math.PI);
		float l = -0.5F * k;
		float m = 0.4F * MathHelper.sin(j * (float) (Math.PI * 2));
		float n = -0.3F * MathHelper.sin(g * (float) Math.PI);
		matrixStack.translate((double)(h * l), (double)(m - 0.3F * k), (double)n);
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(k * -45.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * k * -30.0F));
		this.renderFirstPersonMap(matrixStack, vertexConsumerProvider, i, itemStack);
		matrixStack.pop();
	}

	private void renderMapInBothHands(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, float g, float h) {
		float j = MathHelper.sqrt(h);
		float k = -0.2F * MathHelper.sin(h * (float) Math.PI);
		float l = -0.4F * MathHelper.sin(j * (float) Math.PI);
		matrixStack.translate(0.0, (double)(-k / 2.0F), (double)l);
		float m = this.getMapAngle(f);
		matrixStack.translate(0.0, (double)(0.04F + g * -1.2F + m * -0.5F), -0.72F);
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(m * -85.0F));
		if (!this.client.player.isInvisible()) {
			matrixStack.push();
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
			this.renderArm(matrixStack, vertexConsumerProvider, i, Arm.field_6183);
			this.renderArm(matrixStack, vertexConsumerProvider, i, Arm.field_6182);
			matrixStack.pop();
		}

		float n = MathHelper.sin(j * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(n * 20.0F));
		matrixStack.scale(2.0F, 2.0F, 2.0F);
		this.renderFirstPersonMap(matrixStack, vertexConsumerProvider, i, this.mainHand);
	}

	private void renderFirstPersonMap(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ItemStack itemStack) {
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
		matrixStack.scale(0.38F, 0.38F, 0.38F);
		matrixStack.translate(-0.5, -0.5, 0.0);
		matrixStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
		MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.client.world);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(mapState == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
		Matrix4f matrix4f = matrixStack.peek().getModel();
		vertexConsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(i).next();
		vertexConsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(i).next();
		vertexConsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(i).next();
		vertexConsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(i).next();
		if (mapState != null) {
			this.client.gameRenderer.getMapRenderer().draw(matrixStack, vertexConsumerProvider, mapState, false, i);
		}
	}

	private void renderArmHoldingItem(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, float g, Arm arm) {
		boolean bl = arm != Arm.field_6182;
		float h = bl ? 1.0F : -1.0F;
		float j = MathHelper.sqrt(g);
		float k = -0.3F * MathHelper.sin(j * (float) Math.PI);
		float l = 0.4F * MathHelper.sin(j * (float) (Math.PI * 2));
		float m = -0.4F * MathHelper.sin(g * (float) Math.PI);
		matrixStack.translate((double)(h * (k + 0.64000005F)), (double)(l + -0.6F + f * -0.6F), (double)(m + -0.71999997F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * 45.0F));
		float n = MathHelper.sin(g * g * (float) Math.PI);
		float o = MathHelper.sin(j * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * o * 70.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(h * n * -20.0F));
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getSkinTexture());
		matrixStack.translate((double)(h * -1.0F), 3.6F, 3.5);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(h * 120.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(200.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * -135.0F));
		matrixStack.translate((double)(h * 5.6F), 0.0, 0.0);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(abstractClientPlayerEntity);
		if (bl) {
			playerEntityRenderer.renderRightArm(matrixStack, vertexConsumerProvider, i, abstractClientPlayerEntity);
		} else {
			playerEntityRenderer.renderLeftArm(matrixStack, vertexConsumerProvider, i, abstractClientPlayerEntity);
		}
	}

	private void applyEatOrDrinkTransformation(MatrixStack matrixStack, float f, Arm arm, ItemStack itemStack) {
		float g = (float)this.client.player.getItemUseTimeLeft() - f + 1.0F;
		float h = g / (float)itemStack.getMaxUseTime();
		if (h < 0.8F) {
			float i = MathHelper.abs(MathHelper.cos(g / 4.0F * (float) Math.PI) * 0.1F);
			matrixStack.translate(0.0, (double)i, 0.0);
		}

		float j = 1.0F - (float)Math.pow((double)h, 27.0);
		int k = arm == Arm.field_6183 ? 1 : -1;
		matrixStack.translate((double)(j * 0.6F * (float)k), (double)(j * -0.5F), (double)(j * 0.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)k * j * 90.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(j * 10.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)k * j * 30.0F));
	}

	private void applySwingOffset(MatrixStack matrixStack, Arm arm, float f) {
		int i = arm == Arm.field_6183 ? 1 : -1;
		float g = MathHelper.sin(f * f * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * (45.0F + g * -20.0F)));
		float h = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)i * h * -20.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(h * -80.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * -45.0F));
	}

	private void applyEquipOffset(MatrixStack matrixStack, Arm arm, float f) {
		int i = arm == Arm.field_6183 ? 1 : -1;
		matrixStack.translate((double)((float)i * 0.56F), (double)(-0.52F + f * -0.6F), -0.72F);
	}

	public void renderItem(float f, MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate, ClientPlayerEntity clientPlayerEntity, int i) {
		float g = clientPlayerEntity.getHandSwingProgress(f);
		Hand hand = (Hand)MoreObjects.firstNonNull(clientPlayerEntity.preferredHand, Hand.field_5808);
		float h = MathHelper.lerp(f, clientPlayerEntity.prevPitch, clientPlayerEntity.pitch);
		boolean bl = true;
		boolean bl2 = true;
		if (clientPlayerEntity.isUsingItem()) {
			ItemStack itemStack = clientPlayerEntity.getActiveItem();
			if (itemStack.getItem() == Items.field_8102 || itemStack.getItem() == Items.field_8399) {
				bl = clientPlayerEntity.getActiveHand() == Hand.field_5808;
				bl2 = !bl;
			}

			Hand hand2 = clientPlayerEntity.getActiveHand();
			if (hand2 == Hand.field_5808) {
				ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
				if (itemStack2.getItem() == Items.field_8399 && CrossbowItem.isCharged(itemStack2)) {
					bl2 = false;
				}
			}
		} else {
			ItemStack itemStack3 = clientPlayerEntity.getMainHandStack();
			ItemStack itemStack4 = clientPlayerEntity.getOffHandStack();
			if (itemStack3.getItem() == Items.field_8399 && CrossbowItem.isCharged(itemStack3)) {
				bl2 = !bl;
			}

			if (itemStack4.getItem() == Items.field_8399 && CrossbowItem.isCharged(itemStack4)) {
				bl = !itemStack3.isEmpty();
				bl2 = !bl;
			}
		}

		float j = MathHelper.lerp(f, clientPlayerEntity.lastRenderPitch, clientPlayerEntity.renderPitch);
		float k = MathHelper.lerp(f, clientPlayerEntity.lastRenderYaw, clientPlayerEntity.renderYaw);
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((clientPlayerEntity.getPitch(f) - j) * 0.1F));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((clientPlayerEntity.getYaw(f) - k) * 0.1F));
		if (bl) {
			float l = hand == Hand.field_5808 ? g : 0.0F;
			float m = 1.0F - MathHelper.lerp(f, this.prevEquipProgressMainHand, this.equipProgressMainHand);
			this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.field_5808, l, this.mainHand, m, matrixStack, immediate, i);
		}

		if (bl2) {
			float n = hand == Hand.field_5810 ? g : 0.0F;
			float o = 1.0F - MathHelper.lerp(f, this.prevEquipProgressOffHand, this.equipProgressOffHand);
			this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.field_5810, n, this.offHand, o, matrixStack, immediate, i);
		}

		immediate.draw();
	}

	private void renderFirstPersonItem(
		AbstractClientPlayerEntity abstractClientPlayerEntity,
		float f,
		float g,
		Hand hand,
		float h,
		ItemStack itemStack,
		float i,
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int j
	) {
		boolean bl = hand == Hand.field_5808;
		Arm arm = bl ? abstractClientPlayerEntity.getMainArm() : abstractClientPlayerEntity.getMainArm().getOpposite();
		matrixStack.push();
		if (itemStack.isEmpty()) {
			if (bl && !abstractClientPlayerEntity.isInvisible()) {
				this.renderArmHoldingItem(matrixStack, vertexConsumerProvider, j, i, h, arm);
			}
		} else if (itemStack.getItem() == Items.field_8204) {
			if (bl && this.offHand.isEmpty()) {
				this.renderMapInBothHands(matrixStack, vertexConsumerProvider, j, g, i, h);
			} else {
				this.renderMapInOneHand(matrixStack, vertexConsumerProvider, j, i, arm, h, itemStack);
			}
		} else if (itemStack.getItem() == Items.field_8399) {
			boolean bl2 = CrossbowItem.isCharged(itemStack);
			boolean bl3 = arm == Arm.field_6183;
			int k = bl3 ? 1 : -1;
			if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
				this.applyEquipOffset(matrixStack, arm, i);
				matrixStack.translate((double)((float)k * -0.4785682F), -0.094387F, 0.05731531F);
				matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-11.935F));
				matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)k * 65.3F));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)k * -9.785F));
				float l = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
				float m = l / (float)CrossbowItem.getPullTime(itemStack);
				if (m > 1.0F) {
					m = 1.0F;
				}

				if (m > 0.1F) {
					float n = MathHelper.sin((l - 0.1F) * 1.3F);
					float o = m - 0.1F;
					float p = n * o;
					matrixStack.translate((double)(p * 0.0F), (double)(p * 0.004F), (double)(p * 0.0F));
				}

				matrixStack.translate((double)(m * 0.0F), (double)(m * 0.0F), (double)(m * 0.04F));
				matrixStack.scale(1.0F, 1.0F, 1.0F + m * 0.2F);
				matrixStack.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)k * 45.0F));
			} else {
				float q = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
				float r = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) (Math.PI * 2));
				float s = -0.2F * MathHelper.sin(h * (float) Math.PI);
				matrixStack.translate((double)((float)k * q), (double)r, (double)s);
				this.applyEquipOffset(matrixStack, arm, i);
				this.applySwingOffset(matrixStack, arm, h);
				if (bl2 && h < 0.001F) {
					matrixStack.translate((double)((float)k * -0.641864F), 0.0, 0.0);
					matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)k * 10.0F));
				}
			}

			this.renderItem(
				abstractClientPlayerEntity,
				itemStack,
				bl3 ? ModelTransformation.Mode.field_4322 : ModelTransformation.Mode.field_4321,
				!bl3,
				matrixStack,
				vertexConsumerProvider,
				j
			);
		} else {
			boolean bl4 = arm == Arm.field_6183;
			if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
				int t = bl4 ? 1 : -1;
				switch (itemStack.getUseAction()) {
					case field_8952:
						this.applyEquipOffset(matrixStack, arm, i);
						break;
					case field_8950:
					case field_8946:
						this.applyEatOrDrinkTransformation(matrixStack, f, arm, itemStack);
						this.applyEquipOffset(matrixStack, arm, i);
						break;
					case field_8949:
						this.applyEquipOffset(matrixStack, arm, i);
						break;
					case field_8953:
						this.applyEquipOffset(matrixStack, arm, i);
						matrixStack.translate((double)((float)t * -0.2785682F), 0.18344387F, 0.15731531F);
						matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-13.935F));
						matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)t * 35.3F));
						matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)t * -9.785F));
						float u = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
						float v = u / 20.0F;
						v = (v * v + v * 2.0F) / 3.0F;
						if (v > 1.0F) {
							v = 1.0F;
						}

						if (v > 0.1F) {
							float w = MathHelper.sin((u - 0.1F) * 1.3F);
							float x = v - 0.1F;
							float y = w * x;
							matrixStack.translate((double)(y * 0.0F), (double)(y * 0.004F), (double)(y * 0.0F));
						}

						matrixStack.translate((double)(v * 0.0F), (double)(v * 0.0F), (double)(v * 0.04F));
						matrixStack.scale(1.0F, 1.0F, 1.0F + v * 0.2F);
						matrixStack.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)t * 45.0F));
						break;
					case field_8951:
						this.applyEquipOffset(matrixStack, arm, i);
						matrixStack.translate((double)((float)t * -0.5F), 0.7F, 0.1F);
						matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-55.0F));
						matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)t * 35.3F));
						matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)t * -9.785F));
						float z = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0F);
						float aa = z / 10.0F;
						if (aa > 1.0F) {
							aa = 1.0F;
						}

						if (aa > 0.1F) {
							float ab = MathHelper.sin((z - 0.1F) * 1.3F);
							float ac = aa - 0.1F;
							float ad = ab * ac;
							matrixStack.translate((double)(ad * 0.0F), (double)(ad * 0.004F), (double)(ad * 0.0F));
						}

						matrixStack.translate(0.0, 0.0, (double)(aa * 0.2F));
						matrixStack.scale(1.0F, 1.0F, 1.0F + aa * 0.2F);
						matrixStack.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)t * 45.0F));
				}
			} else if (abstractClientPlayerEntity.isUsingRiptide()) {
				this.applyEquipOffset(matrixStack, arm, i);
				int ae = bl4 ? 1 : -1;
				matrixStack.translate((double)((float)ae * -0.4F), 0.8F, 0.3F);
				matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)ae * 65.0F));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)ae * -85.0F));
			} else {
				float af = -0.4F * MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
				float ag = 0.2F * MathHelper.sin(MathHelper.sqrt(h) * (float) (Math.PI * 2));
				float ah = -0.2F * MathHelper.sin(h * (float) Math.PI);
				int ai = bl4 ? 1 : -1;
				matrixStack.translate((double)((float)ai * af), (double)ag, (double)ah);
				this.applyEquipOffset(matrixStack, arm, i);
				this.applySwingOffset(matrixStack, arm, h);
			}

			this.renderItem(
				abstractClientPlayerEntity,
				itemStack,
				bl4 ? ModelTransformation.Mode.field_4322 : ModelTransformation.Mode.field_4321,
				!bl4,
				matrixStack,
				vertexConsumerProvider,
				j
			);
		}

		matrixStack.pop();
	}

	public void updateHeldItems() {
		this.prevEquipProgressMainHand = this.equipProgressMainHand;
		this.prevEquipProgressOffHand = this.equipProgressOffHand;
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		ItemStack itemStack = clientPlayerEntity.getMainHandStack();
		ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
		if (clientPlayerEntity.isRiding()) {
			this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4F, 0.0F, 1.0F);
			this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4F, 0.0F, 1.0F);
		} else {
			float f = clientPlayerEntity.getAttackCooldownProgress(1.0F);
			this.equipProgressMainHand = this.equipProgressMainHand
				+ MathHelper.clamp((Objects.equals(this.mainHand, itemStack) ? f * f * f : 0.0F) - this.equipProgressMainHand, -0.4F, 0.4F);
			this.equipProgressOffHand = this.equipProgressOffHand
				+ MathHelper.clamp((float)(Objects.equals(this.offHand, itemStack2) ? 1 : 0) - this.equipProgressOffHand, -0.4F, 0.4F);
		}

		if (this.equipProgressMainHand < 0.1F) {
			this.mainHand = itemStack;
		}

		if (this.equipProgressOffHand < 0.1F) {
			this.offHand = itemStack2;
		}
	}

	public void resetEquipProgress(Hand hand) {
		if (hand == Hand.field_5808) {
			this.equipProgressMainHand = 0.0F;
		} else {
			this.equipProgressOffHand = 0.0F;
		}
	}
}
