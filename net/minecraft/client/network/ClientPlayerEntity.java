package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.JigsawBlockScreen;
import net.minecraft.client.gui.screen.ingame.MinecartCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.sound.AmbientSoundLoops;
import net.minecraft.client.sound.AmbientSoundPlayer;
import net.minecraft.client.sound.BubbleColumnSoundPlayer;
import net.minecraft.client.sound.ElytraSoundInstance;
import net.minecraft.client.sound.MinecartSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.packet.ChatMessageC2SPacket;
import net.minecraft.server.network.packet.ClientCommandC2SPacket;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.server.network.packet.GuiCloseC2SPacket;
import net.minecraft.server.network.packet.HandSwingC2SPacket;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.network.packet.PlayerInputC2SPacket;
import net.minecraft.server.network.packet.PlayerMoveC2SPacket;
import net.minecraft.server.network.packet.RecipeBookDataC2SPacket;
import net.minecraft.server.network.packet.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatHandler;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.dimension.DimensionType;

public class ClientPlayerEntity extends AbstractClientPlayerEntity {
	public final ClientPlayNetworkHandler networkHandler;
	private final StatHandler statHandler;
	private final ClientRecipeBook recipeBook;
	private final List<ClientPlayerTickable> tickables = Lists.newArrayList();
	private int clientPermissionLevel = 0;
	private double lastX;
	private double lastBaseY;
	private double lastZ;
	private float lastYaw;
	private float lastPitch;
	private boolean lastOnGround;
	private boolean lastIsHoldingSneakKey;
	private boolean lastSprinting;
	private int field_3923;
	private boolean field_3918;
	private String serverBrand;
	public Input input;
	protected final MinecraftClient client;
	protected int field_3935;
	public int field_3921;
	public float renderYaw;
	public float renderPitch;
	public float lastRenderYaw;
	public float lastRenderPitch;
	private int field_3938;
	private float field_3922;
	public float nextNauseaStrength;
	public float lastNauseaStrength;
	private boolean usingItem;
	private Hand activeHand;
	private boolean riding;
	private boolean lastAutoJump = true;
	private int field_3934;
	private boolean field_3939;
	private int field_3917;
	private boolean showsDeathScreen = true;

	public ClientPlayerEntity(
		MinecraftClient minecraftClient,
		ClientWorld clientWorld,
		ClientPlayNetworkHandler clientPlayNetworkHandler,
		StatHandler statHandler,
		ClientRecipeBook clientRecipeBook
	) {
		super(clientWorld, clientPlayNetworkHandler.getProfile());
		this.networkHandler = clientPlayNetworkHandler;
		this.statHandler = statHandler;
		this.recipeBook = clientRecipeBook;
		this.client = minecraftClient;
		this.dimension = DimensionType.field_13072;
		this.tickables.add(new AmbientSoundPlayer(this, minecraftClient.getSoundManager()));
		this.tickables.add(new BubbleColumnSoundPlayer(this));
	}

	@Override
	public boolean isGlowing() {
		return super.isGlowing() || this.client.player.isSpectator() && this.client.options.keySpectatorOutlines.isPressed();
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return false;
	}

	@Override
	public void heal(float f) {
	}

	@Override
	public boolean startRiding(Entity entity, boolean bl) {
		if (!super.startRiding(entity, bl)) {
			return false;
		} else {
			if (entity instanceof AbstractMinecartEntity) {
				this.client.getSoundManager().play(new MinecartSoundInstance(this, (AbstractMinecartEntity)entity));
			}

			if (entity instanceof BoatEntity) {
				this.prevYaw = entity.yaw;
				this.yaw = entity.yaw;
				this.setHeadYaw(entity.yaw);
			}

			return true;
		}
	}

	@Override
	public void stopRiding() {
		super.stopRiding();
		this.riding = false;
	}

	@Override
	public float getPitch(float f) {
		return this.pitch;
	}

	@Override
	public float getYaw(float f) {
		return this.hasVehicle() ? super.getYaw(f) : this.yaw;
	}

	@Override
	public void tick() {
		if (this.world.isChunkLoaded(new BlockPos(this.getX(), 0.0, this.getZ()))) {
			super.tick();
			if (this.hasVehicle()) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(this.yaw, this.pitch, this.onGround));
				this.networkHandler.sendPacket(new PlayerInputC2SPacket(this.sidewaysSpeed, this.forwardSpeed, this.input.jumping, this.input.sneaking));
				Entity entity = this.getRootVehicle();
				if (entity != this && entity.isLogicalSideForUpdatingMovement()) {
					this.networkHandler.sendPacket(new VehicleMoveC2SPacket(entity));
				}
			} else {
				this.sendMovementPackets();
			}

			for (ClientPlayerTickable clientPlayerTickable : this.tickables) {
				clientPlayerTickable.tick();
			}
		}
	}

	private void sendMovementPackets() {
		boolean bl = this.isSprinting();
		if (bl != this.lastSprinting) {
			ClientCommandC2SPacket.Mode mode = bl ? ClientCommandC2SPacket.Mode.field_12981 : ClientCommandC2SPacket.Mode.field_12985;
			this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode));
			this.lastSprinting = bl;
		}

		boolean bl2 = this.isSneaking();
		if (bl2 != this.lastIsHoldingSneakKey) {
			ClientCommandC2SPacket.Mode mode2 = bl2 ? ClientCommandC2SPacket.Mode.field_12979 : ClientCommandC2SPacket.Mode.field_12984;
			this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode2));
			this.lastIsHoldingSneakKey = bl2;
		}

		if (this.isCamera()) {
			double d = this.getX() - this.lastX;
			double e = this.getY() - this.lastBaseY;
			double f = this.getZ() - this.lastZ;
			double g = (double)(this.yaw - this.lastYaw);
			double h = (double)(this.pitch - this.lastPitch);
			this.field_3923++;
			boolean bl3 = d * d + e * e + f * f > 9.0E-4 || this.field_3923 >= 20;
			boolean bl4 = g != 0.0 || h != 0.0;
			if (this.hasVehicle()) {
				Vec3d vec3d = this.getVelocity();
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(vec3d.x, -999.0, vec3d.z, this.yaw, this.pitch, this.onGround));
				bl3 = false;
			} else if (bl3 && bl4) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch, this.onGround));
			} else if (bl3) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(this.getX(), this.getY(), this.getZ(), this.onGround));
			} else if (bl4) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(this.yaw, this.pitch, this.onGround));
			} else if (this.lastOnGround != this.onGround) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket(this.onGround));
			}

			if (bl3) {
				this.lastX = this.getX();
				this.lastBaseY = this.getY();
				this.lastZ = this.getZ();
				this.field_3923 = 0;
			}

			if (bl4) {
				this.lastYaw = this.yaw;
				this.lastPitch = this.pitch;
			}

			this.lastOnGround = this.onGround;
			this.lastAutoJump = this.client.options.autoJump;
		}
	}

	@Override
	public boolean dropSelectedItem(boolean bl) {
		PlayerActionC2SPacket.Action action = bl ? PlayerActionC2SPacket.Action.field_12970 : PlayerActionC2SPacket.Action.field_12975;
		this.networkHandler.sendPacket(new PlayerActionC2SPacket(action, BlockPos.ORIGIN, Direction.field_11033));
		return this.inventory
				.takeInvStack(this.inventory.selectedSlot, bl && !this.inventory.getMainHandStack().isEmpty() ? this.inventory.getMainHandStack().getCount() : 1)
			!= ItemStack.EMPTY;
	}

	public void sendChatMessage(String string) {
		this.networkHandler.sendPacket(new ChatMessageC2SPacket(string));
	}

	@Override
	public void swingHand(Hand hand) {
		super.swingHand(hand);
		this.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
	}

	@Override
	public void requestRespawn() {
		this.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.field_12774));
	}

	@Override
	protected void applyDamage(DamageSource damageSource, float f) {
		if (!this.isInvulnerableTo(damageSource)) {
			this.setHealth(this.getHealth() - f);
		}
	}

	@Override
	public void closeContainer() {
		this.networkHandler.sendPacket(new GuiCloseC2SPacket(this.container.syncId));
		this.closeScreen();
	}

	public void closeScreen() {
		this.inventory.setCursorStack(ItemStack.EMPTY);
		super.closeContainer();
		this.client.openScreen(null);
	}

	public void updateHealth(float f) {
		if (this.field_3918) {
			float g = this.getHealth() - f;
			if (g <= 0.0F) {
				this.setHealth(f);
				if (g < 0.0F) {
					this.timeUntilRegen = 10;
				}
			} else {
				this.lastDamageTaken = g;
				this.setHealth(this.getHealth());
				this.timeUntilRegen = 20;
				this.applyDamage(DamageSource.GENERIC, g);
				this.maxHurtTime = 10;
				this.hurtTime = this.maxHurtTime;
			}
		} else {
			this.setHealth(f);
			this.field_3918 = true;
		}
	}

	@Override
	public void sendAbilitiesUpdate() {
		this.networkHandler.sendPacket(new UpdatePlayerAbilitiesC2SPacket(this.abilities));
	}

	@Override
	public boolean isMainPlayer() {
		return true;
	}

	protected void startRidingJump() {
		this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.field_12987, MathHelper.floor(this.method_3151() * 100.0F)));
	}

	public void openRidingInventory() {
		this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.field_12988));
	}

	public void setServerBrand(String string) {
		this.serverBrand = string;
	}

	public String getServerBrand() {
		return this.serverBrand;
	}

	public StatHandler getStatHandler() {
		return this.statHandler;
	}

	public ClientRecipeBook getRecipeBook() {
		return this.recipeBook;
	}

	public void onRecipeDisplayed(Recipe<?> recipe) {
		if (this.recipeBook.shouldDisplay(recipe)) {
			this.recipeBook.onRecipeDisplayed(recipe);
			this.networkHandler.sendPacket(new RecipeBookDataC2SPacket(recipe));
		}
	}

	@Override
	protected int getPermissionLevel() {
		return this.clientPermissionLevel;
	}

	public void setClientPermissionLevel(int i) {
		this.clientPermissionLevel = i;
	}

	@Override
	public void addChatMessage(Text text, boolean bl) {
		if (bl) {
			this.client.inGameHud.setOverlayMessage(text, false);
		} else {
			this.client.inGameHud.getChatHud().addMessage(text);
		}
	}

	@Override
	protected void pushOutOfBlocks(double d, double e, double f) {
		BlockPos blockPos = new BlockPos(d, e, f);
		if (this.cannotFitAt(blockPos)) {
			double g = d - (double)blockPos.getX();
			double h = f - (double)blockPos.getZ();
			Direction direction = null;
			double i = 9999.0;
			if (!this.cannotFitAt(blockPos.west()) && g < i) {
				i = g;
				direction = Direction.field_11039;
			}

			if (!this.cannotFitAt(blockPos.east()) && 1.0 - g < i) {
				i = 1.0 - g;
				direction = Direction.field_11034;
			}

			if (!this.cannotFitAt(blockPos.north()) && h < i) {
				i = h;
				direction = Direction.field_11043;
			}

			if (!this.cannotFitAt(blockPos.south()) && 1.0 - h < i) {
				i = 1.0 - h;
				direction = Direction.field_11035;
			}

			if (direction != null) {
				Vec3d vec3d = this.getVelocity();
				switch (direction) {
					case field_11039:
						this.setVelocity(-0.1, vec3d.y, vec3d.z);
						break;
					case field_11034:
						this.setVelocity(0.1, vec3d.y, vec3d.z);
						break;
					case field_11043:
						this.setVelocity(vec3d.x, vec3d.y, -0.1);
						break;
					case field_11035:
						this.setVelocity(vec3d.x, vec3d.y, 0.1);
				}
			}
		}
	}

	private boolean cannotFitAt(BlockPos blockPos) {
		Box box = this.getBoundingBox();
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);

		for (int i = MathHelper.floor(box.y1); i < MathHelper.ceil(box.y2); i++) {
			mutable.setY(i);
			if (!this.doesNotSuffocate(mutable)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void setSprinting(boolean bl) {
		super.setSprinting(bl);
		this.field_3921 = 0;
	}

	public void setExperience(float f, int i, int j) {
		this.experienceProgress = f;
		this.totalExperience = i;
		this.experienceLevel = j;
	}

	@Override
	public void sendMessage(Text text) {
		this.client.inGameHud.getChatHud().addMessage(text);
	}

	@Override
	public void handleStatus(byte b) {
		if (b >= 24 && b <= 28) {
			this.setClientPermissionLevel(b - 24);
		} else {
			super.handleStatus(b);
		}
	}

	public void setShowsDeathScreen(boolean bl) {
		this.showsDeathScreen = bl;
	}

	public boolean showsDeathScreen() {
		return this.showsDeathScreen;
	}

	@Override
	public void playSound(SoundEvent soundEvent, float f, float g) {
		this.world.playSound(this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), f, g, false);
	}

	@Override
	public void playSound(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
		this.world.playSound(this.getX(), this.getY(), this.getZ(), soundEvent, soundCategory, f, g, false);
	}

	@Override
	public boolean canMoveVoluntarily() {
		return true;
	}

	@Override
	public void setCurrentHand(Hand hand) {
		ItemStack itemStack = this.getStackInHand(hand);
		if (!itemStack.isEmpty() && !this.isUsingItem()) {
			super.setCurrentHand(hand);
			this.usingItem = true;
			this.activeHand = hand;
		}
	}

	@Override
	public boolean isUsingItem() {
		return this.usingItem;
	}

	@Override
	public void clearActiveItem() {
		super.clearActiveItem();
		this.usingItem = false;
	}

	@Override
	public Hand getActiveHand() {
		return this.activeHand;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		super.onTrackedDataSet(trackedData);
		if (LIVING_FLAGS.equals(trackedData)) {
			boolean bl = (this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
			Hand hand = (this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.field_5810 : Hand.field_5808;
			if (bl && !this.usingItem) {
				this.setCurrentHand(hand);
			} else if (!bl && this.usingItem) {
				this.clearActiveItem();
			}
		}

		if (FLAGS.equals(trackedData) && this.isFallFlying() && !this.field_3939) {
			this.client.getSoundManager().play(new ElytraSoundInstance(this));
		}
	}

	public boolean hasJumpingMount() {
		Entity entity = this.getVehicle();
		return this.hasVehicle() && entity instanceof JumpingMount && ((JumpingMount)entity).canJump();
	}

	public float method_3151() {
		return this.field_3922;
	}

	@Override
	public void openEditSignScreen(SignBlockEntity signBlockEntity) {
		this.client.openScreen(new SignEditScreen(signBlockEntity));
	}

	@Override
	public void openCommandBlockMinecartScreen(CommandBlockExecutor commandBlockExecutor) {
		this.client.openScreen(new MinecartCommandBlockScreen(commandBlockExecutor));
	}

	@Override
	public void openCommandBlockScreen(CommandBlockBlockEntity commandBlockBlockEntity) {
		this.client.openScreen(new CommandBlockScreen(commandBlockBlockEntity));
	}

	@Override
	public void openStructureBlockScreen(StructureBlockBlockEntity structureBlockBlockEntity) {
		this.client.openScreen(new StructureBlockScreen(structureBlockBlockEntity));
	}

	@Override
	public void openJigsawScreen(JigsawBlockEntity jigsawBlockEntity) {
		this.client.openScreen(new JigsawBlockScreen(jigsawBlockEntity));
	}

	@Override
	public void openEditBookScreen(ItemStack itemStack, Hand hand) {
		Item item = itemStack.getItem();
		if (item == Items.field_8674) {
			this.client.openScreen(new BookEditScreen(this, itemStack, hand));
		}
	}

	@Override
	public void addCritParticles(Entity entity) {
		this.client.particleManager.addEmitter(entity, ParticleTypes.field_11205);
	}

	@Override
	public void addEnchantedHitParticles(Entity entity) {
		this.client.particleManager.addEmitter(entity, ParticleTypes.field_11208);
	}

	@Override
	public boolean isSneaking() {
		return this.input != null && this.input.sneaking;
	}

	@Override
	public boolean isInSneakingPose() {
		return !this.abilities.flying && !this.isSwimming() && this.wouldPoseNotCollide(EntityPose.field_18081)
			? this.isSneaking() || !this.isSleeping() && !this.wouldPoseNotCollide(EntityPose.field_18076)
			: false;
	}

	public boolean isHoldingSneakKey() {
		return this.isInSneakingPose() || this.shouldLeaveSwimmingPose();
	}

	@Override
	public void tickNewAi() {
		super.tickNewAi();
		if (this.isCamera()) {
			this.sidewaysSpeed = this.input.movementSideways;
			this.forwardSpeed = this.input.movementForward;
			this.jumping = this.input.jumping;
			this.lastRenderYaw = this.renderYaw;
			this.lastRenderPitch = this.renderPitch;
			this.renderPitch = (float)((double)this.renderPitch + (double)(this.pitch - this.renderPitch) * 0.5);
			this.renderYaw = (float)((double)this.renderYaw + (double)(this.yaw - this.renderYaw) * 0.5);
		}
	}

	protected boolean isCamera() {
		return this.client.getCameraEntity() == this;
	}

	@Override
	public void tickMovement() {
		this.field_3921++;
		if (this.field_3935 > 0) {
			this.field_3935--;
		}

		this.updateNausea();
		boolean bl = this.input.jumping;
		boolean bl2 = this.input.sneaking;
		boolean bl3 = this.method_20623();
		this.input.tick(this.isHoldingSneakKey());
		this.client.getTutorialManager().onMovement(this.input);
		if (this.isUsingItem() && !this.hasVehicle()) {
			this.input.movementSideways *= 0.2F;
			this.input.movementForward *= 0.2F;
			this.field_3935 = 0;
		}

		boolean bl4 = false;
		if (this.field_3934 > 0) {
			this.field_3934--;
			bl4 = true;
			this.input.jumping = true;
		}

		if (!this.noClip) {
			this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35, this.getY() + 0.5, this.getZ() + (double)this.getWidth() * 0.35);
			this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35, this.getY() + 0.5, this.getZ() - (double)this.getWidth() * 0.35);
			this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35, this.getY() + 0.5, this.getZ() - (double)this.getWidth() * 0.35);
			this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35, this.getY() + 0.5, this.getZ() + (double)this.getWidth() * 0.35);
		}

		boolean bl5 = (float)this.getHungerManager().getFoodLevel() > 6.0F || this.abilities.allowFlying;
		if ((this.onGround || this.isSubmergedInWater())
			&& !bl2
			&& !bl3
			&& this.method_20623()
			&& !this.isSprinting()
			&& bl5
			&& !this.isUsingItem()
			&& !this.hasStatusEffect(StatusEffects.field_5919)) {
			if (this.field_3935 <= 0 && !this.client.options.keySprint.isPressed()) {
				this.field_3935 = 7;
			} else {
				this.setSprinting(true);
			}
		}

		if (!this.isSprinting()
			&& (!this.isTouchingWater() || this.isSubmergedInWater())
			&& this.method_20623()
			&& bl5
			&& !this.isUsingItem()
			&& !this.hasStatusEffect(StatusEffects.field_5919)
			&& this.client.options.keySprint.isPressed()) {
			this.setSprinting(true);
		}

		if (this.isSprinting()) {
			boolean bl6 = !this.input.hasForwardMovement() || !bl5;
			boolean bl7 = bl6 || this.horizontalCollision || this.isTouchingWater() && !this.isSubmergedInWater();
			if (this.isSwimming()) {
				if (!this.onGround && !this.input.sneaking && bl6 || !this.isTouchingWater()) {
					this.setSprinting(false);
				}
			} else if (bl7) {
				this.setSprinting(false);
			}
		}

		boolean bl8 = false;
		if (this.abilities.allowFlying) {
			if (this.client.interactionManager.isFlyingLocked()) {
				if (!this.abilities.flying) {
					this.abilities.flying = true;
					bl8 = true;
					this.sendAbilitiesUpdate();
				}
			} else if (!bl && this.input.jumping && !bl4) {
				if (this.field_7489 == 0) {
					this.field_7489 = 7;
				} else if (!this.isSwimming()) {
					this.abilities.flying = !this.abilities.flying;
					bl8 = true;
					this.sendAbilitiesUpdate();
					this.field_7489 = 0;
				}
			}
		}

		if (this.input.jumping && !bl8 && !bl && !this.abilities.flying && !this.hasVehicle() && !this.isClimbing()) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.field_6174);
			if (itemStack.getItem() == Items.field_8833 && ElytraItem.isUsable(itemStack) && this.method_23668()) {
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.field_12982));
			}
		}

		this.field_3939 = this.isFallFlying();
		if (this.isTouchingWater() && this.input.sneaking) {
			this.knockDownwards();
		}

		if (this.isInFluid(FluidTags.field_15517)) {
			int i = this.isSpectator() ? 10 : 1;
			this.field_3917 = MathHelper.clamp(this.field_3917 + i, 0, 600);
		} else if (this.field_3917 > 0) {
			this.isInFluid(FluidTags.field_15517);
			this.field_3917 = MathHelper.clamp(this.field_3917 - 10, 0, 600);
		}

		if (this.abilities.flying && this.isCamera()) {
			int j = 0;
			if (this.input.sneaking) {
				j--;
			}

			if (this.input.jumping) {
				j++;
			}

			if (j != 0) {
				this.setVelocity(this.getVelocity().add(0.0, (double)((float)j * this.abilities.getFlySpeed() * 3.0F), 0.0));
			}
		}

		if (this.hasJumpingMount()) {
			JumpingMount jumpingMount = (JumpingMount)this.getVehicle();
			if (this.field_3938 < 0) {
				this.field_3938++;
				if (this.field_3938 == 0) {
					this.field_3922 = 0.0F;
				}
			}

			if (bl && !this.input.jumping) {
				this.field_3938 = -10;
				jumpingMount.setJumpStrength(MathHelper.floor(this.method_3151() * 100.0F));
				this.startRidingJump();
			} else if (!bl && this.input.jumping) {
				this.field_3938 = 0;
				this.field_3922 = 0.0F;
			} else if (bl) {
				this.field_3938++;
				if (this.field_3938 < 10) {
					this.field_3922 = (float)this.field_3938 * 0.1F;
				} else {
					this.field_3922 = 0.8F + 2.0F / (float)(this.field_3938 - 9) * 0.1F;
				}
			}
		} else {
			this.field_3922 = 0.0F;
		}

		super.tickMovement();
		if (this.onGround && this.abilities.flying && !this.client.interactionManager.isFlyingLocked()) {
			this.abilities.flying = false;
			this.sendAbilitiesUpdate();
		}
	}

	private void updateNausea() {
		this.lastNauseaStrength = this.nextNauseaStrength;
		if (this.inNetherPortal) {
			if (this.client.currentScreen != null && !this.client.currentScreen.isPauseScreen()) {
				if (this.client.currentScreen instanceof ContainerScreen) {
					this.closeContainer();
				}

				this.client.openScreen(null);
			}

			if (this.nextNauseaStrength == 0.0F) {
				this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.field_14669, this.random.nextFloat() * 0.4F + 0.8F));
			}

			this.nextNauseaStrength += 0.0125F;
			if (this.nextNauseaStrength >= 1.0F) {
				this.nextNauseaStrength = 1.0F;
			}

			this.inNetherPortal = false;
		} else if (this.hasStatusEffect(StatusEffects.field_5916) && this.getStatusEffect(StatusEffects.field_5916).getDuration() > 60) {
			this.nextNauseaStrength += 0.006666667F;
			if (this.nextNauseaStrength > 1.0F) {
				this.nextNauseaStrength = 1.0F;
			}
		} else {
			if (this.nextNauseaStrength > 0.0F) {
				this.nextNauseaStrength -= 0.05F;
			}

			if (this.nextNauseaStrength < 0.0F) {
				this.nextNauseaStrength = 0.0F;
			}
		}

		this.tickNetherPortalCooldown();
	}

	@Override
	public void tickRiding() {
		super.tickRiding();
		this.riding = false;
		if (this.getVehicle() instanceof BoatEntity) {
			BoatEntity boatEntity = (BoatEntity)this.getVehicle();
			boatEntity.setInputs(this.input.pressingLeft, this.input.pressingRight, this.input.pressingForward, this.input.pressingBack);
			this.riding = this.riding | (this.input.pressingLeft || this.input.pressingRight || this.input.pressingForward || this.input.pressingBack);
		}
	}

	public boolean isRiding() {
		return this.riding;
	}

	@Nullable
	@Override
	public StatusEffectInstance removeStatusEffectInternal(@Nullable StatusEffect statusEffect) {
		if (statusEffect == StatusEffects.field_5916) {
			this.lastNauseaStrength = 0.0F;
			this.nextNauseaStrength = 0.0F;
		}

		return super.removeStatusEffectInternal(statusEffect);
	}

	@Override
	public void move(MovementType movementType, Vec3d vec3d) {
		double d = this.getX();
		double e = this.getZ();
		super.move(movementType, vec3d);
		this.method_3148((float)(this.getX() - d), (float)(this.getZ() - e));
	}

	public boolean getLastAutoJump() {
		return this.lastAutoJump;
	}

	protected void method_3148(float f, float g) {
		if (this.method_22119()) {
			Vec3d vec3d = this.getPos();
			Vec3d vec3d2 = vec3d.add((double)f, 0.0, (double)g);
			Vec3d vec3d3 = new Vec3d((double)f, 0.0, (double)g);
			float h = this.getMovementSpeed();
			float i = (float)vec3d3.lengthSquared();
			if (i <= 0.001F) {
				Vec2f vec2f = this.input.getMovementInput();
				float j = h * vec2f.x;
				float k = h * vec2f.y;
				float l = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0));
				float m = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
				vec3d3 = new Vec3d((double)(j * m - k * l), vec3d3.y, (double)(k * m + j * l));
				i = (float)vec3d3.lengthSquared();
				if (i <= 0.001F) {
					return;
				}
			}

			float n = MathHelper.fastInverseSqrt(i);
			Vec3d vec3d4 = vec3d3.multiply((double)n);
			Vec3d vec3d5 = this.getRotationVecClient();
			float o = (float)(vec3d5.x * vec3d4.x + vec3d5.z * vec3d4.z);
			if (!(o < -0.15F)) {
				EntityContext entityContext = EntityContext.of(this);
				BlockPos blockPos = new BlockPos(this.getX(), this.getBoundingBox().y2, this.getZ());
				BlockState blockState = this.world.getBlockState(blockPos);
				if (blockState.getCollisionShape(this.world, blockPos, entityContext).isEmpty()) {
					blockPos = blockPos.up();
					BlockState blockState2 = this.world.getBlockState(blockPos);
					if (blockState2.getCollisionShape(this.world, blockPos, entityContext).isEmpty()) {
						float p = 7.0F;
						float q = 1.2F;
						if (this.hasStatusEffect(StatusEffects.field_5913)) {
							q += (float)(this.getStatusEffect(StatusEffects.field_5913).getAmplifier() + 1) * 0.75F;
						}

						float r = Math.max(h * 7.0F, 1.0F / n);
						Vec3d vec3d7 = vec3d2.add(vec3d4.multiply((double)r));
						float s = this.getWidth();
						float t = this.getHeight();
						Box box = new Box(vec3d, vec3d7.add(0.0, (double)t, 0.0)).expand((double)s, 0.0, (double)s);
						Vec3d vec3d6 = vec3d.add(0.0, 0.51F, 0.0);
						vec3d7 = vec3d7.add(0.0, 0.51F, 0.0);
						Vec3d vec3d8 = vec3d4.crossProduct(new Vec3d(0.0, 1.0, 0.0));
						Vec3d vec3d9 = vec3d8.multiply((double)(s * 0.5F));
						Vec3d vec3d10 = vec3d6.subtract(vec3d9);
						Vec3d vec3d11 = vec3d7.subtract(vec3d9);
						Vec3d vec3d12 = vec3d6.add(vec3d9);
						Vec3d vec3d13 = vec3d7.add(vec3d9);
						Iterator<Box> iterator = this.world
							.getCollisions(this, box, Collections.emptySet())
							.flatMap(voxelShapex -> voxelShapex.getBoundingBoxes().stream())
							.iterator();
						float u = Float.MIN_VALUE;

						while (iterator.hasNext()) {
							Box box2 = (Box)iterator.next();
							if (box2.intersects(vec3d10, vec3d11) || box2.intersects(vec3d12, vec3d13)) {
								u = (float)box2.y2;
								Vec3d vec3d14 = box2.getCenter();
								BlockPos blockPos2 = new BlockPos(vec3d14);

								for (int v = 1; (float)v < q; v++) {
									BlockPos blockPos3 = blockPos2.up(v);
									BlockState blockState3 = this.world.getBlockState(blockPos3);
									VoxelShape voxelShape;
									if (!(voxelShape = blockState3.getCollisionShape(this.world, blockPos3, entityContext)).isEmpty()) {
										u = (float)voxelShape.getMaximum(Direction.Axis.field_11052) + (float)blockPos3.getY();
										if ((double)u - this.getY() > (double)q) {
											return;
										}
									}

									if (v > 1) {
										blockPos = blockPos.up();
										BlockState blockState4 = this.world.getBlockState(blockPos);
										if (!blockState4.getCollisionShape(this.world, blockPos, entityContext).isEmpty()) {
											return;
										}
									}
								}
								break;
							}
						}

						if (u != Float.MIN_VALUE) {
							float w = (float)((double)u - this.getY());
							if (!(w <= 0.5F) && !(w > q)) {
								this.field_3934 = 1;
							}
						}
					}
				}
			}
		}
	}

	private boolean method_22119() {
		return this.getLastAutoJump()
			&& this.field_3934 <= 0
			&& this.onGround
			&& !this.clipAtLedge()
			&& !this.hasVehicle()
			&& this.hasMovementInput()
			&& (double)this.getJumpVelocityMultiplier() >= 1.0;
	}

	private boolean hasMovementInput() {
		Vec2f vec2f = this.input.getMovementInput();
		return vec2f.x != 0.0F || vec2f.y != 0.0F;
	}

	private boolean method_20623() {
		double d = 0.8;
		return this.isSubmergedInWater() ? this.input.hasForwardMovement() : (double)this.input.movementForward >= 0.8;
	}

	public float method_3140() {
		if (!this.isInFluid(FluidTags.field_15517)) {
			return 0.0F;
		} else {
			float f = 600.0F;
			float g = 100.0F;
			if ((float)this.field_3917 >= 600.0F) {
				return 1.0F;
			} else {
				float h = MathHelper.clamp((float)this.field_3917 / 100.0F, 0.0F, 1.0F);
				float i = (float)this.field_3917 < 100.0F ? 0.0F : MathHelper.clamp(((float)this.field_3917 - 100.0F) / 500.0F, 0.0F, 1.0F);
				return h * 0.6F + i * 0.39999998F;
			}
		}
	}

	@Override
	public boolean isSubmergedInWater() {
		return this.isSubmergedInWater;
	}

	@Override
	protected boolean updateWaterSubmersionState() {
		boolean bl = this.isSubmergedInWater;
		boolean bl2 = super.updateWaterSubmersionState();
		if (this.isSpectator()) {
			return this.isSubmergedInWater;
		} else {
			if (!bl && bl2) {
				this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.field_14756, SoundCategory.field_15256, 1.0F, 1.0F, false);
				this.client.getSoundManager().play(new AmbientSoundLoops.Underwater(this));
			}

			if (bl && !bl2) {
				this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.field_14828, SoundCategory.field_15256, 1.0F, 1.0F, false);
			}

			return this.isSubmergedInWater;
		}
	}
}
