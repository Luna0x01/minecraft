package net.minecraft.entity.player;

import javax.annotation.Nullable;
import net.minecraft.class_2971;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.ChestScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CraftingTableScreen;
import net.minecraft.client.gui.screen.ingame.DispenserScreen;
import net.minecraft.client.gui.screen.ingame.EnchantingScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.screen.ingame.VillagerTradingScreen;
import net.minecraft.client.gui.screen.ingame.class_2845;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.ElytraSoundInstance;
import net.minecraft.client.sound.MinecartSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class ClientPlayerEntity extends AbstractClientPlayerEntity {
	public final ClientPlayNetworkHandler networkHandler;
	private final StatHandler statHandler;
	private int field_13456 = 0;
	private double lastX;
	private double lastBaseY;
	private double lastZ;
	private float lastYaw;
	private float lastPitch;
	private boolean field_13457;
	private boolean lastIsHoldingSneakKey;
	private boolean lastSprinting;
	private int ticksSinceLastPositionPacketSent;
	private boolean field_10636;
	private String serverBrand;
	public Input input;
	protected MinecraftClient client;
	protected int field_1763;
	public int ticksSinceSprintingChanged;
	public float renderYaw;
	public float renderPitch;
	public float lastRenderYaw;
	public float lastRenderPitch;
	private int field_6449;
	private float field_6450;
	public float timeInPortal;
	public float lastTimeInPortal;
	private boolean field_13458;
	private Hand field_13459;
	private boolean field_13460;

	public ClientPlayerEntity(MinecraftClient minecraftClient, World world, ClientPlayNetworkHandler clientPlayNetworkHandler, StatHandler statHandler) {
		super(world, clientPlayNetworkHandler.getProfile());
		this.networkHandler = clientPlayNetworkHandler;
		this.statHandler = statHandler;
		this.client = minecraftClient;
		this.dimension = 0;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return false;
	}

	@Override
	public void heal(float f) {
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		if (!super.startRiding(entity, force)) {
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
		this.field_13460 = false;
	}

	@Override
	public void tick() {
		if (this.world.blockExists(new BlockPos(this.x, 0.0, this.z))) {
			super.tick();
			if (this.hasMount()) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(this.yaw, this.pitch, this.onGround));
				this.networkHandler.sendPacket(new PlayerInputC2SPacket(this.sidewaysSpeed, this.forwardSpeed, this.input.jumping, this.input.sneaking));
				Entity entity = this.getRootVehicle();
				if (entity != this && entity.method_13003()) {
					this.networkHandler.sendPacket(new VehicleMoveC2SPacket(entity));
				}
			} else {
				this.sendMovementPackets();
			}
		}
	}

	public void sendMovementPackets() {
		boolean bl = this.isSprinting();
		if (bl != this.lastSprinting) {
			if (bl) {
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_SPRINTING));
			} else {
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
			}

			this.lastSprinting = bl;
		}

		boolean bl2 = this.isSneaking();
		if (bl2 != this.lastIsHoldingSneakKey) {
			if (bl2) {
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_SNEAKING));
			} else {
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.STOP_SNEAKING));
			}

			this.lastIsHoldingSneakKey = bl2;
		}

		if (this.isCamera()) {
			Box box = this.getBoundingBox();
			double d = this.x - this.lastX;
			double e = box.minY - this.lastBaseY;
			double f = this.z - this.lastZ;
			double g = (double)(this.yaw - this.lastYaw);
			double h = (double)(this.pitch - this.lastPitch);
			this.ticksSinceLastPositionPacketSent++;
			boolean bl3 = d * d + e * e + f * f > 9.0E-4 || this.ticksSinceLastPositionPacketSent >= 20;
			boolean bl4 = g != 0.0 || h != 0.0;
			if (this.hasMount()) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(this.velocityX, -999.0, this.velocityZ, this.yaw, this.pitch, this.onGround));
				bl3 = false;
			} else if (bl3 && bl4) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(this.x, box.minY, this.z, this.yaw, this.pitch, this.onGround));
			} else if (bl3) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(this.x, box.minY, this.z, this.onGround));
			} else if (bl4) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(this.yaw, this.pitch, this.onGround));
			} else if (this.field_13457 != this.onGround) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket(this.onGround));
			}

			if (bl3) {
				this.lastX = this.x;
				this.lastBaseY = box.minY;
				this.lastZ = this.z;
				this.ticksSinceLastPositionPacketSent = 0;
			}

			if (bl4) {
				this.lastYaw = this.yaw;
				this.lastPitch = this.pitch;
			}

			this.field_13457 = this.onGround;
		}
	}

	@Nullable
	@Override
	public ItemEntity dropSelectedItem(boolean dropAll) {
		PlayerActionC2SPacket.Action action = dropAll ? PlayerActionC2SPacket.Action.DROP_ALL_ITEMS : PlayerActionC2SPacket.Action.DROP_ITEM;
		this.networkHandler.sendPacket(new PlayerActionC2SPacket(action, BlockPos.ORIGIN, Direction.DOWN));
		return null;
	}

	@Nullable
	@Override
	protected ItemStack method_3164(ItemEntity itemEntity) {
		return null;
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
		this.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
	}

	@Override
	protected void applyDamage(DamageSource source, float damage) {
		if (!this.isInvulnerableTo(source)) {
			this.setHealth(this.getHealth() - damage);
		}
	}

	@Override
	public void closeHandledScreen() {
		this.networkHandler.sendPacket(new GuiCloseC2SPacket(this.openScreenHandler.syncId));
		this.closeScreen();
	}

	public void closeScreen() {
		this.inventory.setCursorStack(null);
		super.closeHandledScreen();
		this.client.setScreen(null);
	}

	public void updateHealth(float health) {
		if (this.field_10636) {
			float f = this.getHealth() - health;
			if (f <= 0.0F) {
				this.setHealth(health);
				if (f < 0.0F) {
					this.timeUntilRegen = this.defaultMaxHealth / 2;
				}
			} else {
				this.field_6778 = f;
				this.setHealth(this.getHealth());
				this.timeUntilRegen = this.defaultMaxHealth;
				this.applyDamage(DamageSource.GENERIC, f);
				this.hurtTime = this.maxHurtTime = 10;
			}
		} else {
			this.setHealth(health);
			this.field_10636 = true;
		}
	}

	@Override
	public void incrementStat(Stat stat, int amount) {
		if (stat != null) {
			if (stat.localOnly) {
				super.incrementStat(stat, amount);
			}
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
		this.networkHandler
			.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_RIDING_JUMP, MathHelper.floor(this.getMountJumpStrength() * 100.0F)));
	}

	public void openRidingInventory() {
		this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
	}

	public void setServerBrand(String serverBrand) {
		this.serverBrand = serverBrand;
	}

	public String getServerBrand() {
		return this.serverBrand;
	}

	public StatHandler getStatHandler() {
		return this.statHandler;
	}

	public int method_12265() {
		return this.field_13456;
	}

	public void method_12267(int i) {
		this.field_13456 = i;
	}

	@Override
	public void addMessage(Text text) {
		this.client.inGameHud.getChatHud().addMessage(text);
	}

	@Override
	protected boolean pushOutOfBlocks(double x, double y, double z) {
		if (this.noClip) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos(x, y, z);
			double d = x - (double)blockPos.getX();
			double e = z - (double)blockPos.getZ();
			if (!this.method_9719(blockPos)) {
				int i = -1;
				double f = 9999.0;
				if (this.method_9719(blockPos.west()) && d < f) {
					f = d;
					i = 0;
				}

				if (this.method_9719(blockPos.east()) && 1.0 - d < f) {
					f = 1.0 - d;
					i = 1;
				}

				if (this.method_9719(blockPos.north()) && e < f) {
					f = e;
					i = 4;
				}

				if (this.method_9719(blockPos.south()) && 1.0 - e < f) {
					f = 1.0 - e;
					i = 5;
				}

				float g = 0.1F;
				if (i == 0) {
					this.velocityX = (double)(-g);
				}

				if (i == 1) {
					this.velocityX = (double)g;
				}

				if (i == 4) {
					this.velocityZ = (double)(-g);
				}

				if (i == 5) {
					this.velocityZ = (double)g;
				}
			}

			return false;
		}
	}

	private boolean method_9719(BlockPos blockPos) {
		return !this.world.getBlockState(blockPos).method_11734() && !this.world.getBlockState(blockPos.up()).method_11734();
	}

	@Override
	public void setSprinting(boolean sprinting) {
		super.setSprinting(sprinting);
		this.ticksSinceSprintingChanged = 0;
	}

	public void setExperience(float progress, int total, int level) {
		this.experienceProgress = progress;
		this.totalExperience = total;
		this.experienceLevel = level;
	}

	@Override
	public void sendMessage(Text text) {
		this.client.inGameHud.getChatHud().addMessage(text);
	}

	@Override
	public boolean canUseCommand(int permissionLevel, String commandLiteral) {
		return permissionLevel <= this.method_12265();
	}

	@Override
	public void handleStatus(byte status) {
		if (status >= 24 && status <= 28) {
			this.method_12267(status - 24);
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	public BlockPos getBlockPos() {
		return new BlockPos(this.x + 0.5, this.y + 0.5, this.z + 0.5);
	}

	@Override
	public void playSound(Sound event, float volume, float pitch) {
		this.world.playSound(this.x, this.y, this.z, event, this.getSoundCategory(), volume, pitch, false);
	}

	@Override
	public boolean canMoveVoluntarily() {
		return true;
	}

	@Override
	public void method_13050(Hand hand) {
		ItemStack itemStack = this.getStackInHand(hand);
		if (itemStack != null && !this.method_13061()) {
			super.method_13050(hand);
			this.field_13458 = true;
			this.field_13459 = hand;
		}
	}

	@Override
	public boolean method_13061() {
		return this.field_13458;
	}

	@Override
	public void method_13053() {
		super.method_13053();
		this.field_13458 = false;
	}

	@Override
	public Hand method_13062() {
		return this.field_13459;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (field_14543.equals(data)) {
			boolean bl = (this.dataTracker.get(field_14543) & 1) > 0;
			Hand hand = (this.dataTracker.get(field_14543) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
			if (bl && !this.field_13458) {
				this.method_13050(hand);
			} else if (!bl && this.field_13458) {
				this.method_13053();
			}
		}
	}

	public boolean isRidingHorse() {
		Entity entity = this.getVehicle();
		return this.hasMount() && entity instanceof class_2971 && ((class_2971)entity).method_13089();
	}

	public float getMountJumpStrength() {
		return this.field_6450;
	}

	@Override
	public void openEditSignScreen(SignBlockEntity sign) {
		this.client.setScreen(new SignEditScreen(sign));
	}

	@Override
	public void openCommandBlockScreen(CommandBlockExecutor executor) {
		if (this.canUseCommand(2, "")) {
			this.client.setScreen(new class_2845(executor));
		}
	}

	@Override
	public void method_13260(CommandBlockBlockEntity commandBlockBlockEntity) {
		if (this.canUseCommand(2, "")) {
			this.client.setScreen(new CommandBlockScreen(commandBlockBlockEntity));
		}
	}

	@Override
	public void method_3201(ItemStack stack, Hand hand) {
		Item item = stack.getItem();
		if (item == Items.WRITABLE_BOOK) {
			this.client.setScreen(new BookEditScreen(this, stack, true));
		}
	}

	@Override
	public void openInventory(Inventory inventory) {
		String string = inventory instanceof NamedScreenHandlerFactory ? ((NamedScreenHandlerFactory)inventory).getId() : "minecraft:container";
		if ("minecraft:chest".equals(string)) {
			this.client.setScreen(new ChestScreen(this.inventory, inventory));
		} else if ("minecraft:hopper".equals(string)) {
			this.client.setScreen(new HopperScreen(this.inventory, inventory));
		} else if ("minecraft:furnace".equals(string)) {
			this.client.setScreen(new FurnaceScreen(this.inventory, inventory));
		} else if ("minecraft:brewing_stand".equals(string)) {
			this.client.setScreen(new BrewingStandScreen(this.inventory, inventory));
		} else if ("minecraft:beacon".equals(string)) {
			this.client.setScreen(new BeaconScreen(this.inventory, inventory));
		} else if (!"minecraft:dispenser".equals(string) && !"minecraft:dropper".equals(string)) {
			this.client.setScreen(new ChestScreen(this.inventory, inventory));
		} else {
			this.client.setScreen(new DispenserScreen(this.inventory, inventory));
		}
	}

	@Override
	public void openHorseInventory(HorseBaseEntity horse, Inventory inventory) {
		this.client.setScreen(new HorseScreen(this.inventory, inventory, horse));
	}

	@Override
	public void openHandledScreen(NamedScreenHandlerFactory screenHandlerFactory) {
		String string = screenHandlerFactory.getId();
		if ("minecraft:crafting_table".equals(string)) {
			this.client.setScreen(new CraftingTableScreen(this.inventory, this.world));
		} else if ("minecraft:enchanting_table".equals(string)) {
			this.client.setScreen(new EnchantingScreen(this.inventory, this.world, screenHandlerFactory));
		} else if ("minecraft:anvil".equals(string)) {
			this.client.setScreen(new AnvilScreen(this.inventory, this.world));
		}
	}

	@Override
	public void openTradingScreen(Trader trader) {
		this.client.setScreen(new VillagerTradingScreen(this.inventory, trader, this.world));
	}

	@Override
	public void addCritParticles(Entity target) {
		this.client.particleManager.addEmitter(target, ParticleType.CRIT);
	}

	@Override
	public void addEnchantedHitParticles(Entity target) {
		this.client.particleManager.addEmitter(target, ParticleType.CRIT_MAGIC);
	}

	@Override
	public boolean isSneaking() {
		boolean bl = this.input != null ? this.input.sneaking : false;
		return bl && !this.inBed;
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
		this.ticksSinceSprintingChanged++;
		if (this.field_1763 > 0) {
			this.field_1763--;
		}

		this.lastTimeInPortal = this.timeInPortal;
		if (this.changingDimension) {
			if (this.client.currentScreen != null && !this.client.currentScreen.shouldPauseGame()) {
				this.client.setScreen(null);
			}

			if (this.timeInPortal == 0.0F) {
				this.client.getSoundManager().play(PositionedSoundInstance.method_12521(Sounds.BLOCK_PORTAL_TRIGGER, this.random.nextFloat() * 0.4F + 0.8F));
			}

			this.timeInPortal += 0.0125F;
			if (this.timeInPortal >= 1.0F) {
				this.timeInPortal = 1.0F;
			}

			this.changingDimension = false;
		} else if (this.hasStatusEffect(StatusEffects.NAUSEA) && this.getEffectInstance(StatusEffects.NAUSEA).getDuration() > 60) {
			this.timeInPortal += 0.006666667F;
			if (this.timeInPortal > 1.0F) {
				this.timeInPortal = 1.0F;
			}
		} else {
			if (this.timeInPortal > 0.0F) {
				this.timeInPortal -= 0.05F;
			}

			if (this.timeInPortal < 0.0F) {
				this.timeInPortal = 0.0F;
			}
		}

		if (this.netherPortalCooldown > 0) {
			this.netherPortalCooldown--;
		}

		boolean bl = this.input.jumping;
		boolean bl2 = this.input.sneaking;
		float f = 0.8F;
		boolean bl3 = this.input.movementForward >= f;
		this.input.tick();
		if (this.method_13061() && !this.hasMount()) {
			this.input.movementSideways *= 0.2F;
			this.input.movementForward *= 0.2F;
			this.field_1763 = 0;
		}

		Box box = this.getBoundingBox();
		this.pushOutOfBlocks(this.x - (double)this.width * 0.35, box.minY + 0.5, this.z + (double)this.width * 0.35);
		this.pushOutOfBlocks(this.x - (double)this.width * 0.35, box.minY + 0.5, this.z - (double)this.width * 0.35);
		this.pushOutOfBlocks(this.x + (double)this.width * 0.35, box.minY + 0.5, this.z - (double)this.width * 0.35);
		this.pushOutOfBlocks(this.x + (double)this.width * 0.35, box.minY + 0.5, this.z + (double)this.width * 0.35);
		boolean bl4 = (float)this.getHungerManager().getFoodLevel() > 6.0F || this.abilities.allowFlying;
		if (this.onGround
			&& !bl2
			&& !bl3
			&& this.input.movementForward >= f
			&& !this.isSprinting()
			&& bl4
			&& !this.method_13061()
			&& !this.hasStatusEffect(StatusEffects.BLINDNESS)) {
			if (this.field_1763 <= 0 && !this.client.options.sprintKey.isPressed()) {
				this.field_1763 = 7;
			} else {
				this.setSprinting(true);
			}
		}

		if (!this.isSprinting()
			&& this.input.movementForward >= f
			&& bl4
			&& !this.method_13061()
			&& !this.hasStatusEffect(StatusEffects.BLINDNESS)
			&& this.client.options.sprintKey.isPressed()) {
			this.setSprinting(true);
		}

		if (this.isSprinting() && (this.input.movementForward < f || this.horizontalCollision || !bl4)) {
			this.setSprinting(false);
		}

		if (this.abilities.allowFlying) {
			if (this.client.interactionManager.isFlyingLocked()) {
				if (!this.abilities.flying) {
					this.abilities.flying = true;
					this.sendAbilitiesUpdate();
				}
			} else if (!bl && this.input.jumping) {
				if (this.abilityResyncCountdown == 0) {
					this.abilityResyncCountdown = 7;
				} else {
					this.abilities.flying = !this.abilities.flying;
					this.sendAbilitiesUpdate();
					this.abilityResyncCountdown = 0;
				}
			}
		}

		if (this.input.jumping && !bl && !this.onGround && this.velocityY < 0.0 && !this.method_13055() && !this.abilities.flying) {
			ItemStack itemStack = this.getStack(EquipmentSlot.CHEST);
			if (itemStack != null && itemStack.getItem() == Items.ELYTRA && ElytraItem.method_11370(itemStack)) {
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
				this.client.getSoundManager().play(new ElytraSoundInstance(this));
			}
		}

		if (this.abilities.flying && this.isCamera()) {
			if (this.input.sneaking) {
				this.input.movementSideways = (float)((double)this.input.movementSideways / 0.3);
				this.input.movementForward = (float)((double)this.input.movementForward / 0.3);
				this.velocityY = this.velocityY - (double)(this.abilities.getFlySpeed() * 3.0F);
			}

			if (this.input.jumping) {
				this.velocityY = this.velocityY + (double)(this.abilities.getFlySpeed() * 3.0F);
			}
		}

		if (this.isRidingHorse()) {
			class_2971 lv = (class_2971)this.getVehicle();
			if (this.field_6449 < 0) {
				this.field_6449++;
				if (this.field_6449 == 0) {
					this.field_6450 = 0.0F;
				}
			}

			if (bl && !this.input.jumping) {
				this.field_6449 = -10;
				lv.method_6299(MathHelper.floor(this.getMountJumpStrength() * 100.0F));
				this.startRidingJump();
			} else if (!bl && this.input.jumping) {
				this.field_6449 = 0;
				this.field_6450 = 0.0F;
			} else if (bl) {
				this.field_6449++;
				if (this.field_6449 < 10) {
					this.field_6450 = (float)this.field_6449 * 0.1F;
				} else {
					this.field_6450 = 0.8F + 2.0F / (float)(this.field_6449 - 9) * 0.1F;
				}
			}
		} else {
			this.field_6450 = 0.0F;
		}

		super.tickMovement();
		if (this.onGround && this.abilities.flying && !this.client.interactionManager.isFlyingLocked()) {
			this.abilities.flying = false;
			this.sendAbilitiesUpdate();
		}
	}

	@Override
	public void tickRiding() {
		super.tickRiding();
		this.field_13460 = false;
		if (this.getVehicle() instanceof BoatEntity) {
			BoatEntity boatEntity = (BoatEntity)this.getVehicle();
			boatEntity.setInputs(this.input.pressingLeft, this.input.pressingRight, this.input.pressingForward, this.input.pressingBack);
			this.field_13460 = this.field_13460 | (this.input.pressingLeft || this.input.pressingRight || this.input.pressingForward || this.input.pressingBack);
		}
	}

	public boolean method_12266() {
		return this.field_13460;
	}

	@Nullable
	@Override
	public StatusEffectInstance method_13052(@Nullable StatusEffect effect) {
		if (effect == StatusEffects.NAUSEA) {
			this.lastTimeInPortal = 0.0F;
			this.timeInPortal = 0.0F;
		}

		return super.method_13052(effect);
	}
}
