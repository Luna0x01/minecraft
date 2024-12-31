package net.minecraft.client.network;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.PosAndRot;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.packet.ButtonClickC2SPacket;
import net.minecraft.server.network.packet.ClickWindowC2SPacket;
import net.minecraft.server.network.packet.CraftRequestC2SPacket;
import net.minecraft.server.network.packet.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.packet.PickFromInventoryC2SPacket;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientPlayerInteractionManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final ClientPlayNetworkHandler networkHandler;
	private BlockPos currentBreakingPos = new BlockPos(-1, -1, -1);
	private ItemStack selectedStack = ItemStack.EMPTY;
	private float currentBreakingProgress;
	private float blockBreakingSoundCooldown;
	private int blockBreakingCooldown;
	private boolean breakingBlock;
	private GameMode gameMode = GameMode.field_9215;
	private final Object2ObjectLinkedOpenHashMap<Pair<BlockPos, PlayerActionC2SPacket.Action>, PosAndRot> unacknowledgedPlayerActions = new Object2ObjectLinkedOpenHashMap();
	private int lastSelectedSlot;

	public ClientPlayerInteractionManager(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler) {
		this.client = minecraftClient;
		this.networkHandler = clientPlayNetworkHandler;
	}

	public static void breakBlockOrFire(
		MinecraftClient minecraftClient, ClientPlayerInteractionManager clientPlayerInteractionManager, BlockPos blockPos, Direction direction
	) {
		if (!minecraftClient.world.extinguishFire(minecraftClient.player, blockPos, direction)) {
			clientPlayerInteractionManager.breakBlock(blockPos);
		}
	}

	public void copyAbilities(PlayerEntity playerEntity) {
		this.gameMode.setAbilitites(playerEntity.abilities);
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
		this.gameMode.setAbilitites(this.client.player.abilities);
	}

	public boolean hasStatusBars() {
		return this.gameMode.isSurvivalLike();
	}

	public boolean breakBlock(BlockPos blockPos) {
		if (this.client.player.canMine(this.client.world, blockPos, this.gameMode)) {
			return false;
		} else {
			World world = this.client.world;
			BlockState blockState = world.getBlockState(blockPos);
			if (!this.client.player.getMainHandStack().getItem().canMine(blockState, world, blockPos, this.client.player)) {
				return false;
			} else {
				Block block = blockState.getBlock();
				if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.client.player.isCreativeLevelTwoOp()) {
					return false;
				} else if (blockState.isAir()) {
					return false;
				} else {
					block.onBreak(world, blockPos, blockState, this.client.player);
					FluidState fluidState = world.getFluidState(blockPos);
					boolean bl = world.setBlockState(blockPos, fluidState.getBlockState(), 11);
					if (bl) {
						block.onBroken(world, blockPos, blockState);
					}

					return bl;
				}
			}
		}
	}

	public boolean attackBlock(BlockPos blockPos, Direction direction) {
		if (this.client.player.canMine(this.client.world, blockPos, this.gameMode)) {
			return false;
		} else if (!this.client.world.getWorldBorder().contains(blockPos)) {
			return false;
		} else {
			if (this.gameMode.isCreative()) {
				BlockState blockState = this.client.world.getBlockState(blockPos);
				this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos, blockState, 1.0F);
				this.sendPlayerAction(PlayerActionC2SPacket.Action.field_12968, blockPos, direction);
				breakBlockOrFire(this.client, this, blockPos, direction);
				this.blockBreakingCooldown = 5;
			} else if (!this.breakingBlock || !this.isCurrentlyBreaking(blockPos)) {
				if (this.breakingBlock) {
					this.sendPlayerAction(PlayerActionC2SPacket.Action.field_12971, this.currentBreakingPos, direction);
				}

				BlockState blockState2 = this.client.world.getBlockState(blockPos);
				this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos, blockState2, 0.0F);
				this.sendPlayerAction(PlayerActionC2SPacket.Action.field_12968, blockPos, direction);
				boolean bl = !blockState2.isAir();
				if (bl && this.currentBreakingProgress == 0.0F) {
					blockState2.onBlockBreakStart(this.client.world, blockPos, this.client.player);
				}

				if (bl && blockState2.calcBlockBreakingDelta(this.client.player, this.client.player.world, blockPos) >= 1.0F) {
					this.breakBlock(blockPos);
				} else {
					this.breakingBlock = true;
					this.currentBreakingPos = blockPos;
					this.selectedStack = this.client.player.getMainHandStack();
					this.currentBreakingProgress = 0.0F;
					this.blockBreakingSoundCooldown = 0.0F;
					this.client.world.setBlockBreakingInfo(this.client.player.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0F) - 1);
				}
			}

			return true;
		}
	}

	public void cancelBlockBreaking() {
		if (this.breakingBlock) {
			BlockState blockState = this.client.world.getBlockState(this.currentBreakingPos);
			this.client.getTutorialManager().onBlockAttacked(this.client.world, this.currentBreakingPos, blockState, -1.0F);
			this.sendPlayerAction(PlayerActionC2SPacket.Action.field_12971, this.currentBreakingPos, Direction.field_11033);
			this.breakingBlock = false;
			this.currentBreakingProgress = 0.0F;
			this.client.world.setBlockBreakingInfo(this.client.player.getEntityId(), this.currentBreakingPos, -1);
			this.client.player.resetLastAttackedTicks();
		}
	}

	public boolean updateBlockBreakingProgress(BlockPos blockPos, Direction direction) {
		this.syncSelectedSlot();
		if (this.blockBreakingCooldown > 0) {
			this.blockBreakingCooldown--;
			return true;
		} else if (this.gameMode.isCreative() && this.client.world.getWorldBorder().contains(blockPos)) {
			this.blockBreakingCooldown = 5;
			BlockState blockState = this.client.world.getBlockState(blockPos);
			this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos, blockState, 1.0F);
			this.sendPlayerAction(PlayerActionC2SPacket.Action.field_12968, blockPos, direction);
			breakBlockOrFire(this.client, this, blockPos, direction);
			return true;
		} else if (this.isCurrentlyBreaking(blockPos)) {
			BlockState blockState2 = this.client.world.getBlockState(blockPos);
			if (blockState2.isAir()) {
				this.breakingBlock = false;
				return false;
			} else {
				this.currentBreakingProgress = this.currentBreakingProgress + blockState2.calcBlockBreakingDelta(this.client.player, this.client.player.world, blockPos);
				if (this.blockBreakingSoundCooldown % 4.0F == 0.0F) {
					BlockSoundGroup blockSoundGroup = blockState2.getSoundGroup();
					this.client
						.getSoundManager()
						.play(
							new PositionedSoundInstance(
								blockSoundGroup.getHitSound(), SoundCategory.field_15245, (blockSoundGroup.getVolume() + 1.0F) / 8.0F, blockSoundGroup.getPitch() * 0.5F, blockPos
							)
						);
				}

				this.blockBreakingSoundCooldown++;
				this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos, blockState2, MathHelper.clamp(this.currentBreakingProgress, 0.0F, 1.0F));
				if (this.currentBreakingProgress >= 1.0F) {
					this.breakingBlock = false;
					this.sendPlayerAction(PlayerActionC2SPacket.Action.field_12973, blockPos, direction);
					this.breakBlock(blockPos);
					this.currentBreakingProgress = 0.0F;
					this.blockBreakingSoundCooldown = 0.0F;
					this.blockBreakingCooldown = 5;
				}

				this.client.world.setBlockBreakingInfo(this.client.player.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0F) - 1);
				return true;
			}
		} else {
			return this.attackBlock(blockPos, direction);
		}
	}

	public float getReachDistance() {
		return this.gameMode.isCreative() ? 5.0F : 4.5F;
	}

	public void tick() {
		this.syncSelectedSlot();
		if (this.networkHandler.getConnection().isOpen()) {
			this.networkHandler.getConnection().tick();
		} else {
			this.networkHandler.getConnection().handleDisconnection();
		}
	}

	private boolean isCurrentlyBreaking(BlockPos blockPos) {
		ItemStack itemStack = this.client.player.getMainHandStack();
		boolean bl = this.selectedStack.isEmpty() && itemStack.isEmpty();
		if (!this.selectedStack.isEmpty() && !itemStack.isEmpty()) {
			bl = itemStack.getItem() == this.selectedStack.getItem()
				&& ItemStack.areTagsEqual(itemStack, this.selectedStack)
				&& (itemStack.isDamageable() || itemStack.getDamage() == this.selectedStack.getDamage());
		}

		return blockPos.equals(this.currentBreakingPos) && bl;
	}

	private void syncSelectedSlot() {
		int i = this.client.player.inventory.selectedSlot;
		if (i != this.lastSelectedSlot) {
			this.lastSelectedSlot = i;
			this.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.lastSelectedSlot));
		}
	}

	public ActionResult interactBlock(ClientPlayerEntity clientPlayerEntity, ClientWorld clientWorld, Hand hand, BlockHitResult blockHitResult) {
		this.syncSelectedSlot();
		BlockPos blockPos = blockHitResult.getBlockPos();
		if (!this.client.world.getWorldBorder().contains(blockPos)) {
			return ActionResult.field_5814;
		} else {
			ItemStack itemStack = clientPlayerEntity.getStackInHand(hand);
			if (this.gameMode == GameMode.field_9219) {
				this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
				return ActionResult.field_5812;
			} else {
				boolean bl = !clientPlayerEntity.getMainHandStack().isEmpty() || !clientPlayerEntity.getOffHandStack().isEmpty();
				boolean bl2 = clientPlayerEntity.shouldCancelInteraction() && bl;
				if (!bl2) {
					ActionResult actionResult = clientWorld.getBlockState(blockPos).onUse(clientWorld, clientPlayerEntity, hand, blockHitResult);
					if (actionResult.isAccepted()) {
						this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
						return actionResult;
					}
				}

				this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
				if (!itemStack.isEmpty() && !clientPlayerEntity.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
					ItemUsageContext itemUsageContext = new ItemUsageContext(clientPlayerEntity, hand, blockHitResult);
					ActionResult actionResult2;
					if (this.gameMode.isCreative()) {
						int i = itemStack.getCount();
						actionResult2 = itemStack.useOnBlock(itemUsageContext);
						itemStack.setCount(i);
					} else {
						actionResult2 = itemStack.useOnBlock(itemUsageContext);
					}

					return actionResult2;
				} else {
					return ActionResult.field_5811;
				}
			}
		}
	}

	public ActionResult interactItem(PlayerEntity playerEntity, World world, Hand hand) {
		if (this.gameMode == GameMode.field_9219) {
			return ActionResult.field_5811;
		} else {
			this.syncSelectedSlot();
			this.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(hand));
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (playerEntity.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
				return ActionResult.field_5811;
			} else {
				int i = itemStack.getCount();
				TypedActionResult<ItemStack> typedActionResult = itemStack.use(world, playerEntity, hand);
				ItemStack itemStack2 = typedActionResult.getValue();
				if (itemStack2 != itemStack || itemStack2.getCount() != i) {
					playerEntity.setStackInHand(hand, itemStack2);
				}

				return typedActionResult.getResult();
			}
		}
	}

	public ClientPlayerEntity createPlayer(ClientWorld clientWorld, StatHandler statHandler, ClientRecipeBook clientRecipeBook) {
		return new ClientPlayerEntity(this.client, clientWorld, this.networkHandler, statHandler, clientRecipeBook);
	}

	public void attackEntity(PlayerEntity playerEntity, Entity entity) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity));
		if (this.gameMode != GameMode.field_9219) {
			playerEntity.attack(entity);
			playerEntity.resetLastAttackedTicks();
		}
	}

	public ActionResult interactEntity(PlayerEntity playerEntity, Entity entity, Hand hand) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand));
		return this.gameMode == GameMode.field_9219 ? ActionResult.field_5811 : playerEntity.interact(entity, hand);
	}

	public ActionResult interactEntityAtLocation(PlayerEntity playerEntity, Entity entity, EntityHitResult entityHitResult, Hand hand) {
		this.syncSelectedSlot();
		Vec3d vec3d = entityHitResult.getPos().subtract(entity.getX(), entity.getY(), entity.getZ());
		this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand, vec3d));
		return this.gameMode == GameMode.field_9219 ? ActionResult.field_5811 : entity.interactAt(playerEntity, vec3d, hand);
	}

	public ItemStack clickSlot(int i, int j, int k, SlotActionType slotActionType, PlayerEntity playerEntity) {
		short s = playerEntity.container.getNextActionId(playerEntity.inventory);
		ItemStack itemStack = playerEntity.container.onSlotClick(j, k, slotActionType, playerEntity);
		this.networkHandler.sendPacket(new ClickWindowC2SPacket(i, j, k, slotActionType, itemStack, s));
		return itemStack;
	}

	public void clickRecipe(int i, Recipe<?> recipe, boolean bl) {
		this.networkHandler.sendPacket(new CraftRequestC2SPacket(i, recipe, bl));
	}

	public void clickButton(int i, int j) {
		this.networkHandler.sendPacket(new ButtonClickC2SPacket(i, j));
	}

	public void clickCreativeStack(ItemStack itemStack, int i) {
		if (this.gameMode.isCreative()) {
			this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(i, itemStack));
		}
	}

	public void dropCreativeStack(ItemStack itemStack) {
		if (this.gameMode.isCreative() && !itemStack.isEmpty()) {
			this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(-1, itemStack));
		}
	}

	public void stopUsingItem(PlayerEntity playerEntity) {
		this.syncSelectedSlot();
		this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.field_12974, BlockPos.ORIGIN, Direction.field_11033));
		playerEntity.stopUsingItem();
	}

	public boolean hasExperienceBar() {
		return this.gameMode.isSurvivalLike();
	}

	public boolean hasLimitedAttackSpeed() {
		return !this.gameMode.isCreative();
	}

	public boolean hasCreativeInventory() {
		return this.gameMode.isCreative();
	}

	public boolean hasExtendedReach() {
		return this.gameMode.isCreative();
	}

	public boolean hasRidingInventory() {
		return this.client.player.hasVehicle() && this.client.player.getVehicle() instanceof HorseBaseEntity;
	}

	public boolean isFlyingLocked() {
		return this.gameMode == GameMode.field_9219;
	}

	public GameMode getCurrentGameMode() {
		return this.gameMode;
	}

	public boolean isBreakingBlock() {
		return this.breakingBlock;
	}

	public void pickFromInventory(int i) {
		this.networkHandler.sendPacket(new PickFromInventoryC2SPacket(i));
	}

	private void sendPlayerAction(PlayerActionC2SPacket.Action action, BlockPos blockPos, Direction direction) {
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		this.unacknowledgedPlayerActions.put(Pair.of(blockPos, action), new PosAndRot(clientPlayerEntity.getPos(), clientPlayerEntity.pitch, clientPlayerEntity.yaw));
		this.networkHandler.sendPacket(new PlayerActionC2SPacket(action, blockPos, direction));
	}

	public void processPlayerActionResponse(ClientWorld clientWorld, BlockPos blockPos, BlockState blockState, PlayerActionC2SPacket.Action action, boolean bl) {
		PosAndRot posAndRot = (PosAndRot)this.unacknowledgedPlayerActions.remove(Pair.of(blockPos, action));
		if (posAndRot == null || !bl || action != PlayerActionC2SPacket.Action.field_12968 && clientWorld.getBlockState(blockPos) != blockState) {
			clientWorld.setBlockStateWithoutNeighborUpdates(blockPos, blockState);
			if (posAndRot != null) {
				Vec3d vec3d = posAndRot.getPos();
				this.client.player.updatePositionAndAngles(vec3d.x, vec3d.y, vec3d.z, posAndRot.getYaw(), posAndRot.getPitch());
			}
		}

		while (this.unacknowledgedPlayerActions.size() >= 50) {
			Pair<BlockPos, PlayerActionC2SPacket.Action> pair = (Pair<BlockPos, PlayerActionC2SPacket.Action>)this.unacknowledgedPlayerActions.firstKey();
			this.unacknowledgedPlayerActions.removeFirst();
			LOGGER.error("Too many unacked block actions, dropping " + pair);
		}
	}
}
