package net.minecraft.client.render.item;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.util.math.MatrixStack;
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
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class HeldItemRenderer {
	private static final RenderLayer MAP_BACKGROUND = RenderLayer.getText(new Identifier("textures/map/map_background.png"));
	private static final RenderLayer MAP_BACKGROUND_CHECKERBOARD = RenderLayer.getText(new Identifier("textures/map/map_background_checkerboard.png"));
	private static final float field_32735 = -0.4F;
	private static final float field_32736 = 0.2F;
	private static final float field_32737 = -0.2F;
	private static final float field_32738 = -0.6F;
	private static final float EQUIP_OFFSET_TRANSLATE_X = 0.56F;
	private static final float EQUIP_OFFSET_TRANSLATE_Y = -0.52F;
	private static final float EQUIP_OFFSET_TRANSLATE_Z = -0.72F;
	private static final float field_32742 = 45.0F;
	private static final float field_32743 = -80.0F;
	private static final float field_32744 = -20.0F;
	private static final float field_32745 = -20.0F;
	private static final float EAT_OR_DRINK_X_ANGLE_MULTIPLIER = 10.0F;
	private static final float EAT_OR_DRINK_Y_ANGLE_MULTIPLIER = 90.0F;
	private static final float EAT_OR_DRINK_Z_ANGLE_MULTIPLIER = 30.0F;
	private static final float field_32749 = 0.6F;
	private static final float field_32750 = -0.5F;
	private static final float field_32751 = 0.0F;
	private static final double field_32752 = 27.0;
	private static final float field_32753 = 0.8F;
	private static final float field_32754 = 0.1F;
	private static final float field_32755 = -0.3F;
	private static final float field_32756 = 0.4F;
	private static final float field_32757 = -0.4F;
	private static final float ARM_HOLDING_ITEM_SECOND_Y_ANGLE_MULTIPLIER = 70.0F;
	private static final float ARM_HOLDING_ITEM_FIRST_Z_ANGLE_MULTIPLIER = -20.0F;
	private static final float field_32690 = -0.6F;
	private static final float field_32691 = 0.8F;
	private static final float field_32692 = 0.8F;
	private static final float field_32693 = -0.75F;
	private static final float field_32694 = -0.9F;
	private static final float field_32695 = 45.0F;
	private static final float field_32696 = -1.0F;
	private static final float field_32697 = 3.6F;
	private static final float field_32698 = 3.5F;
	private static final float ARM_HOLDING_ITEM_TRANSLATE_X = 5.6F;
	private static final int ARM_HOLDING_ITEM_X_ANGLE_MULTIPLIER = 200;
	private static final int ARM_HOLDING_ITEM_THIRD_Y_ANGLE_MULTIPLIER = -135;
	private static final int ARM_HOLDING_ITEM_SECOND_Z_ANGLE_MULTIPLIER = 120;
	private static final float field_32703 = -0.4F;
	private static final float field_32704 = -0.2F;
	private static final float field_32705 = 0.0F;
	private static final float field_32706 = 0.04F;
	private static final float field_32707 = -0.72F;
	private static final float field_32708 = -1.2F;
	private static final float field_32709 = -0.5F;
	private static final float field_32710 = 45.0F;
	private static final float field_32711 = -85.0F;
	private static final float ARM_X_ANGLE_MULTIPLIER = 45.0F;
	private static final float ARM_Y_ANGLE_MULTIPLIER = 92.0F;
	private static final float ARM_Z_ANGLE_MULTIPLIER = -41.0F;
	private static final float ARM_TRANSLATE_X = 0.3F;
	private static final float ARM_TRANSLATE_Y = -1.1F;
	private static final float ARM_TRANSLATE_Z = 0.45F;
	private static final float field_32718 = 20.0F;
	private static final float FIRST_PERSON_MAP_FIRST_SCALE = 0.38F;
	private static final float FIRST_PERSON_MAP_TRANSLATE_X = -0.5F;
	private static final float FIRST_PERSON_MAP_TRANSLATE_Y = -0.5F;
	private static final float FIRST_PERSON_MAP_TRANSLATE_Z = 0.0F;
	private static final float FIRST_PERSON_MAP_SECOND_SCALE = 0.0078125F;
	private static final int field_32724 = 7;
	private static final int field_32725 = 128;
	private static final int field_32726 = 128;
	private static final float field_32727 = 0.0F;
	private static final float field_32728 = 0.0F;
	private static final float field_32729 = 0.04F;
	private static final float field_32730 = 0.0F;
	private static final float field_32731 = 0.004F;
	private static final float field_32732 = 0.0F;
	private static final float field_32733 = 0.2F;
	private static final float field_32734 = 0.1F;
	private final MinecraftClient client;
	private ItemStack mainHand = ItemStack.EMPTY;
	private ItemStack offHand = ItemStack.EMPTY;
	private float equipProgressMainHand;
	private float prevEquipProgressMainHand;
	private float equipProgressOffHand;
	private float prevEquipProgressOffHand;
	private final EntityRenderDispatcher renderManager;
	private final ItemRenderer itemRenderer;

	public HeldItemRenderer(MinecraftClient client) {
		this.client = client;
		this.renderManager = client.getEntityRenderDispatcher();
		this.itemRenderer = client.getItemRenderer();
	}

	public void renderItem(
		LivingEntity entity,
		ItemStack stack,
		ModelTransformation.Mode renderMode,
		boolean leftHanded,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light
	) {
		if (!stack.isEmpty()) {
			this.itemRenderer
				.renderItem(
					entity, stack, renderMode, leftHanded, matrices, vertexConsumers, entity.world, light, OverlayTexture.DEFAULT_UV, entity.getId() + renderMode.ordinal()
				);
		}
	}

	private float getMapAngle(float tickDelta) {
		float f = 1.0F - tickDelta / 45.0F + 0.1F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		return -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
	}

	private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm) {
		RenderSystem.setShaderTexture(0, this.client.player.getSkinTexture());
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(this.client.player);
		matrices.push();
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(92.0F));
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(45.0F));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(f * -41.0F));
		matrices.translate((double)(f * 0.3F), -1.1F, 0.45F);
		if (arm == Arm.RIGHT) {
			playerEntityRenderer.renderRightArm(matrices, vertexConsumers, light, this.client.player);
		} else {
			playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, this.client.player);
		}

		matrices.pop();
	}

	private void renderMapInOneHand(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, Arm arm, float swingProgress, ItemStack stack
	) {
		float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
		matrices.translate((double)(f * 0.125F), -0.125, 0.0);
		if (!this.client.player.isInvisible()) {
			matrices.push();
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(f * 10.0F));
			this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
			matrices.pop();
		}

		matrices.push();
		matrices.translate((double)(f * 0.51F), (double)(-0.08F + equipProgress * -1.2F), -0.75);
		float g = MathHelper.sqrt(swingProgress);
		float h = MathHelper.sin(g * (float) Math.PI);
		float i = -0.5F * h;
		float j = 0.4F * MathHelper.sin(g * (float) (Math.PI * 2));
		float k = -0.3F * MathHelper.sin(swingProgress * (float) Math.PI);
		matrices.translate((double)(f * i), (double)(j - 0.3F * h), (double)k);
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(h * -45.0F));
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(f * h * -30.0F));
		this.renderFirstPersonMap(matrices, vertexConsumers, light, stack);
		matrices.pop();
	}

	private void renderMapInBothHands(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float pitch, float equipProgress, float swingProgress
	) {
		float f = MathHelper.sqrt(swingProgress);
		float g = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
		float h = -0.4F * MathHelper.sin(f * (float) Math.PI);
		matrices.translate(0.0, (double)(-g / 2.0F), (double)h);
		float i = this.getMapAngle(pitch);
		matrices.translate(0.0, (double)(0.04F + equipProgress * -1.2F + i * -0.5F), -0.72F);
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(i * -85.0F));
		if (!this.client.player.isInvisible()) {
			matrices.push();
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
			this.renderArm(matrices, vertexConsumers, light, Arm.RIGHT);
			this.renderArm(matrices, vertexConsumers, light, Arm.LEFT);
			matrices.pop();
		}

		float j = MathHelper.sin(f * (float) Math.PI);
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(j * 20.0F));
		matrices.scale(2.0F, 2.0F, 2.0F);
		this.renderFirstPersonMap(matrices, vertexConsumers, light, this.mainHand);
	}

	private void renderFirstPersonMap(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int swingProgress, ItemStack stack) {
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
		matrices.scale(0.38F, 0.38F, 0.38F);
		matrices.translate(-0.5, -0.5, 0.0);
		matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);
		Integer integer = FilledMapItem.getMapId(stack);
		MapState mapState = FilledMapItem.getMapState(integer, this.client.world);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(mapState == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
		Matrix4f matrix4f = matrices.peek().getModel();
		vertexConsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(swingProgress).next();
		vertexConsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(swingProgress).next();
		vertexConsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(swingProgress).next();
		vertexConsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(swingProgress).next();
		if (mapState != null) {
			this.client.gameRenderer.getMapRenderer().draw(matrices, vertexConsumers, integer, mapState, false, swingProgress);
		}
	}

	private void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm) {
		boolean bl = arm != Arm.LEFT;
		float f = bl ? 1.0F : -1.0F;
		float g = MathHelper.sqrt(swingProgress);
		float h = -0.3F * MathHelper.sin(g * (float) Math.PI);
		float i = 0.4F * MathHelper.sin(g * (float) (Math.PI * 2));
		float j = -0.4F * MathHelper.sin(swingProgress * (float) Math.PI);
		matrices.translate((double)(f * (h + 0.64000005F)), (double)(i + -0.6F + equipProgress * -0.6F), (double)(j + -0.71999997F));
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(f * 45.0F));
		float k = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
		float l = MathHelper.sin(g * (float) Math.PI);
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(f * l * 70.0F));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(f * k * -20.0F));
		AbstractClientPlayerEntity abstractClientPlayerEntity = this.client.player;
		RenderSystem.setShaderTexture(0, abstractClientPlayerEntity.getSkinTexture());
		matrices.translate((double)(f * -1.0F), 3.6F, 3.5);
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(f * 120.0F));
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(200.0F));
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(f * -135.0F));
		matrices.translate((double)(f * 5.6F), 0.0, 0.0);
		PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.<AbstractClientPlayerEntity>getRenderer(abstractClientPlayerEntity);
		if (bl) {
			playerEntityRenderer.renderRightArm(matrices, vertexConsumers, light, abstractClientPlayerEntity);
		} else {
			playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, abstractClientPlayerEntity);
		}
	}

	private void applyEatOrDrinkTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack) {
		float f = (float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F;
		float g = f / (float)stack.getMaxUseTime();
		if (g < 0.8F) {
			float h = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
			matrices.translate(0.0, (double)h, 0.0);
		}

		float i = 1.0F - (float)Math.pow((double)g, 27.0);
		int j = arm == Arm.RIGHT ? 1 : -1;
		matrices.translate((double)(i * 0.6F * (float)j), (double)(i * -0.5F), (double)(i * 0.0F));
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)j * i * 90.0F));
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(i * 10.0F));
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)j * i * 30.0F));
	}

	private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)i * (45.0F + f * -20.0F)));
		float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)i * g * -20.0F));
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(g * -80.0F));
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)i * -45.0F));
	}

	private void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress) {
		int i = arm == Arm.RIGHT ? 1 : -1;
		matrices.translate((double)((float)i * 0.56F), (double)(-0.52F + equipProgress * -0.6F), -0.72F);
	}

	public void renderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light) {
		float f = player.getHandSwingProgress(tickDelta);
		Hand hand = (Hand)MoreObjects.firstNonNull(player.preferredHand, Hand.MAIN_HAND);
		float g = MathHelper.lerp(tickDelta, player.prevPitch, player.getPitch());
		HeldItemRenderer.HandRenderType handRenderType = getHandRenderType(player);
		float h = MathHelper.lerp(tickDelta, player.lastRenderPitch, player.renderPitch);
		float i = MathHelper.lerp(tickDelta, player.lastRenderYaw, player.renderYaw);
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((player.getPitch(tickDelta) - h) * 0.1F));
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((player.getYaw(tickDelta) - i) * 0.1F));
		if (handRenderType.renderMainHand) {
			float j = hand == Hand.MAIN_HAND ? f : 0.0F;
			float k = 1.0F - MathHelper.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand);
			this.renderFirstPersonItem(player, tickDelta, g, Hand.MAIN_HAND, j, this.mainHand, k, matrices, vertexConsumers, light);
		}

		if (handRenderType.renderOffHand) {
			float l = hand == Hand.OFF_HAND ? f : 0.0F;
			float m = 1.0F - MathHelper.lerp(tickDelta, this.prevEquipProgressOffHand, this.equipProgressOffHand);
			this.renderFirstPersonItem(player, tickDelta, g, Hand.OFF_HAND, l, this.offHand, m, matrices, vertexConsumers, light);
		}

		vertexConsumers.draw();
	}

	@VisibleForTesting
	static HeldItemRenderer.HandRenderType getHandRenderType(ClientPlayerEntity player) {
		ItemStack itemStack = player.getMainHandStack();
		ItemStack itemStack2 = player.getOffHandStack();
		boolean bl = itemStack.isOf(Items.BOW) || itemStack2.isOf(Items.BOW);
		boolean bl2 = itemStack.isOf(Items.CROSSBOW) || itemStack2.isOf(Items.CROSSBOW);
		if (!bl && !bl2) {
			return HeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
		} else if (player.isUsingItem()) {
			return getUsingItemHandRenderType(player);
		} else {
			return isChargedCrossbow(itemStack) ? HeldItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY : HeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
		}
	}

	private static HeldItemRenderer.HandRenderType getUsingItemHandRenderType(ClientPlayerEntity player) {
		ItemStack itemStack = player.getActiveItem();
		Hand hand = player.getActiveHand();
		if (!itemStack.isOf(Items.BOW) && !itemStack.isOf(Items.CROSSBOW)) {
			return hand == Hand.MAIN_HAND && isChargedCrossbow(player.getOffHandStack())
				? HeldItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY
				: HeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
		} else {
			return HeldItemRenderer.HandRenderType.shouldOnlyRender(hand);
		}
	}

	private static boolean isChargedCrossbow(ItemStack stack) {
		return stack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(stack);
	}

	private void renderFirstPersonItem(
		AbstractClientPlayerEntity player,
		float tickDelta,
		float pitch,
		Hand hand,
		float swingProgress,
		ItemStack item,
		float equipProgress,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light
	) {
		if (!player.isUsingSpyglass()) {
			boolean bl = hand == Hand.MAIN_HAND;
			Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
			matrices.push();
			if (item.isEmpty()) {
				if (bl && !player.isInvisible()) {
					this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
				}
			} else if (item.isOf(Items.FILLED_MAP)) {
				if (bl && this.offHand.isEmpty()) {
					this.renderMapInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
				} else {
					this.renderMapInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
				}
			} else if (item.isOf(Items.CROSSBOW)) {
				boolean bl2 = CrossbowItem.isCharged(item);
				boolean bl3 = arm == Arm.RIGHT;
				int i = bl3 ? 1 : -1;
				if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
					this.applyEquipOffset(matrices, arm, equipProgress);
					matrices.translate((double)((float)i * -0.4785682F), -0.094387F, 0.05731531F);
					matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-11.935F));
					matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)i * 65.3F));
					matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)i * -9.785F));
					float f = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
					float g = f / (float)CrossbowItem.getPullTime(item);
					if (g > 1.0F) {
						g = 1.0F;
					}

					if (g > 0.1F) {
						float h = MathHelper.sin((f - 0.1F) * 1.3F);
						float j = g - 0.1F;
						float k = h * j;
						matrices.translate((double)(k * 0.0F), (double)(k * 0.004F), (double)(k * 0.0F));
					}

					matrices.translate((double)(g * 0.0F), (double)(g * 0.0F), (double)(g * 0.04F));
					matrices.scale(1.0F, 1.0F, 1.0F + g * 0.2F);
					matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((float)i * 45.0F));
				} else {
					float l = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
					float m = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) (Math.PI * 2));
					float n = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
					matrices.translate((double)((float)i * l), (double)m, (double)n);
					this.applyEquipOffset(matrices, arm, equipProgress);
					this.applySwingOffset(matrices, arm, swingProgress);
					if (bl2 && swingProgress < 0.001F && bl) {
						matrices.translate((double)((float)i * -0.641864F), 0.0, 0.0);
						matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)i * 10.0F));
					}
				}

				this.renderItem(
					player,
					item,
					bl3 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND,
					!bl3,
					matrices,
					vertexConsumers,
					light
				);
			} else {
				boolean bl4 = arm == Arm.RIGHT;
				if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
					int o = bl4 ? 1 : -1;
					switch (item.getUseAction()) {
						case NONE:
							this.applyEquipOffset(matrices, arm, equipProgress);
							break;
						case EAT:
						case DRINK:
							this.applyEatOrDrinkTransformation(matrices, tickDelta, arm, item);
							this.applyEquipOffset(matrices, arm, equipProgress);
							break;
						case BLOCK:
							this.applyEquipOffset(matrices, arm, equipProgress);
							break;
						case BOW:
							this.applyEquipOffset(matrices, arm, equipProgress);
							matrices.translate((double)((float)o * -0.2785682F), 0.18344387F, 0.15731531F);
							matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-13.935F));
							matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)o * 35.3F));
							matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)o * -9.785F));
							float p = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
							float q = p / 20.0F;
							q = (q * q + q * 2.0F) / 3.0F;
							if (q > 1.0F) {
								q = 1.0F;
							}

							if (q > 0.1F) {
								float r = MathHelper.sin((p - 0.1F) * 1.3F);
								float s = q - 0.1F;
								float t = r * s;
								matrices.translate((double)(t * 0.0F), (double)(t * 0.004F), (double)(t * 0.0F));
							}

							matrices.translate((double)(q * 0.0F), (double)(q * 0.0F), (double)(q * 0.04F));
							matrices.scale(1.0F, 1.0F, 1.0F + q * 0.2F);
							matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((float)o * 45.0F));
							break;
						case SPEAR:
							this.applyEquipOffset(matrices, arm, equipProgress);
							matrices.translate((double)((float)o * -0.5F), 0.7F, 0.1F);
							matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-55.0F));
							matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)o * 35.3F));
							matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)o * -9.785F));
							float u = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0F);
							float v = u / 10.0F;
							if (v > 1.0F) {
								v = 1.0F;
							}

							if (v > 0.1F) {
								float w = MathHelper.sin((u - 0.1F) * 1.3F);
								float x = v - 0.1F;
								float y = w * x;
								matrices.translate((double)(y * 0.0F), (double)(y * 0.004F), (double)(y * 0.0F));
							}

							matrices.translate(0.0, 0.0, (double)(v * 0.2F));
							matrices.scale(1.0F, 1.0F, 1.0F + v * 0.2F);
							matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((float)o * 45.0F));
					}
				} else if (player.isUsingRiptide()) {
					this.applyEquipOffset(matrices, arm, equipProgress);
					int z = bl4 ? 1 : -1;
					matrices.translate((double)((float)z * -0.4F), 0.8F, 0.3F);
					matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float)z * 65.0F));
					matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)z * -85.0F));
				} else {
					float aa = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
					float ab = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) (Math.PI * 2));
					float ac = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
					int ad = bl4 ? 1 : -1;
					matrices.translate((double)((float)ad * aa), (double)ab, (double)ac);
					this.applyEquipOffset(matrices, arm, equipProgress);
					this.applySwingOffset(matrices, arm, swingProgress);
				}

				this.renderItem(
					player,
					item,
					bl4 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND,
					!bl4,
					matrices,
					vertexConsumers,
					light
				);
			}

			matrices.pop();
		}
	}

	public void updateHeldItems() {
		this.prevEquipProgressMainHand = this.equipProgressMainHand;
		this.prevEquipProgressOffHand = this.equipProgressOffHand;
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		ItemStack itemStack = clientPlayerEntity.getMainHandStack();
		ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
		if (ItemStack.areEqual(this.mainHand, itemStack)) {
			this.mainHand = itemStack;
		}

		if (ItemStack.areEqual(this.offHand, itemStack2)) {
			this.offHand = itemStack2;
		}

		if (clientPlayerEntity.isRiding()) {
			this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4F, 0.0F, 1.0F);
			this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4F, 0.0F, 1.0F);
		} else {
			float f = clientPlayerEntity.getAttackCooldownProgress(1.0F);
			this.equipProgressMainHand = this.equipProgressMainHand
				+ MathHelper.clamp((this.mainHand == itemStack ? f * f * f : 0.0F) - this.equipProgressMainHand, -0.4F, 0.4F);
			this.equipProgressOffHand = this.equipProgressOffHand
				+ MathHelper.clamp((float)(this.offHand == itemStack2 ? 1 : 0) - this.equipProgressOffHand, -0.4F, 0.4F);
		}

		if (this.equipProgressMainHand < 0.1F) {
			this.mainHand = itemStack;
		}

		if (this.equipProgressOffHand < 0.1F) {
			this.offHand = itemStack2;
		}
	}

	public void resetEquipProgress(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			this.equipProgressMainHand = 0.0F;
		} else {
			this.equipProgressOffHand = 0.0F;
		}
	}

	@VisibleForTesting
	static enum HandRenderType {
		RENDER_BOTH_HANDS(true, true),
		RENDER_MAIN_HAND_ONLY(true, false),
		RENDER_OFF_HAND_ONLY(false, true);

		final boolean renderMainHand;
		final boolean renderOffHand;

		private HandRenderType(boolean renderMainHand, boolean renderOffHand) {
			this.renderMainHand = renderMainHand;
			this.renderOffHand = renderOffHand;
		}

		public static HeldItemRenderer.HandRenderType shouldOnlyRender(Hand hand) {
			return hand == Hand.MAIN_HAND ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
		}
	}
}
