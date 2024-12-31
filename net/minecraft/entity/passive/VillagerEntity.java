package net.minecraft.entity.passive;

import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3082;
import net.minecraft.class_3133;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.datafixer.DataFixer;
import net.minecraft.datafixer.DataFixerFactory;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.Schema;
import net.minecraft.datafixer.schema.ItemListSchema;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EvocationIllagerEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tradable;
import net.minecraft.entity.VexEntity;
import net.minecraft.entity.VindicationIllagerEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowGolemGoal;
import net.minecraft.entity.ai.goal.FormCaravanGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.HarvestCropsGoal;
import net.minecraft.entity.ai.goal.LongDoorInteractGoal;
import net.minecraft.entity.ai.goal.LookAtCustomerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RestrictOpenDoorGoal;
import net.minecraft.entity.ai.goal.StayIndoorsGoal;
import net.minecraft.entity.ai.goal.StopAndLookAtEntityGoal;
import net.minecraft.entity.ai.goal.StopFollowingCustomerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.VillagerInteractGoal;
import net.minecraft.entity.ai.goal.VillagerMatingGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.village.Village;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VillagerEntity extends PassiveEntity implements Tradable, Trader {
	private static final Logger VILLAGER_LOGGER = LogManager.getLogger();
	private static final TrackedData<Integer> field_14789 = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private int field_3951;
	private boolean field_3952;
	private boolean field_3953;
	Village field_3950;
	private PlayerEntity customer;
	@Nullable
	private TraderOfferList offers;
	private int field_3956;
	private boolean field_3948;
	private boolean willingToMate;
	private int riches;
	private String field_5395;
	private int career;
	private int careerLevel;
	private boolean field_5396;
	private boolean field_12106;
	private final SimpleInventory villagerInventory = new SimpleInventory("Items", false, 8);
	private static final VillagerEntity.TradeProvider[][][][] TRADES = new VillagerEntity.TradeProvider[][][][]{
		{
				{
						{
								new VillagerEntity.ItemTradeEntry(Items.WHEAT, new VillagerEntity.Cost(18, 22)),
								new VillagerEntity.ItemTradeEntry(Items.POTATO, new VillagerEntity.Cost(15, 19)),
								new VillagerEntity.ItemTradeEntry(Items.CARROT, new VillagerEntity.Cost(15, 19)),
								new VillagerEntity.ItemStackTradeEntry(Items.BREAD, new VillagerEntity.Cost(-4, -2))
						},
						{
								new VillagerEntity.ItemTradeEntry(Item.fromBlock(Blocks.PUMPKIN), new VillagerEntity.Cost(8, 13)),
								new VillagerEntity.ItemStackTradeEntry(Items.PUMPKIN_PIE, new VillagerEntity.Cost(-3, -2))
						},
						{
								new VillagerEntity.ItemTradeEntry(Item.fromBlock(Blocks.MELON_BLOCK), new VillagerEntity.Cost(7, 12)),
								new VillagerEntity.ItemStackTradeEntry(Items.APPLE, new VillagerEntity.Cost(-7, -5))
						},
						{
								new VillagerEntity.ItemStackTradeEntry(Items.COOKIE, new VillagerEntity.Cost(-10, -6)),
								new VillagerEntity.ItemStackTradeEntry(Items.CAKE, new VillagerEntity.Cost(1, 1))
						}
				},
				{
						{
								new VillagerEntity.ItemTradeEntry(Items.STRING, new VillagerEntity.Cost(15, 20)),
								new VillagerEntity.ItemTradeEntry(Items.COAL, new VillagerEntity.Cost(16, 24)),
								new VillagerEntity.EmeraldToItem(Items.RAW_FISH, new VillagerEntity.Cost(6, 6), Items.COOKED_FISH, new VillagerEntity.Cost(6, 6))
						},
						{new VillagerEntity.EnchantedItemStackTradeEntry(Items.FISHING_ROD, new VillagerEntity.Cost(7, 8))}
				},
				{
						{
								new VillagerEntity.ItemTradeEntry(Item.fromBlock(Blocks.WOOL), new VillagerEntity.Cost(16, 22)),
								new VillagerEntity.ItemStackTradeEntry(Items.SHEARS, new VillagerEntity.Cost(3, 4))
						},
						{
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL)), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 1), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 2), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 3), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 4), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 5), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 6), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 7), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 8), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 9), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 10), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 11), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 12), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 13), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 14), new VillagerEntity.Cost(1, 2)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Item.fromBlock(Blocks.WOOL), 1, 15), new VillagerEntity.Cost(1, 2))
						}
				},
				{
						{
								new VillagerEntity.ItemTradeEntry(Items.STRING, new VillagerEntity.Cost(15, 20)),
								new VillagerEntity.ItemStackTradeEntry(Items.ARROW, new VillagerEntity.Cost(-12, -8))
						},
						{
								new VillagerEntity.ItemStackTradeEntry(Items.BOW, new VillagerEntity.Cost(2, 3)),
								new VillagerEntity.EmeraldToItem(Item.fromBlock(Blocks.GRAVEL), new VillagerEntity.Cost(10, 10), Items.FLINT, new VillagerEntity.Cost(6, 10))
						}
				}
		},
		{
				{
						{new VillagerEntity.ItemTradeEntry(Items.PAPER, new VillagerEntity.Cost(24, 36)), new VillagerEntity.EnchantedBook()},
						{
								new VillagerEntity.ItemTradeEntry(Items.BOOK, new VillagerEntity.Cost(8, 10)),
								new VillagerEntity.ItemStackTradeEntry(Items.COMPASS, new VillagerEntity.Cost(10, 12)),
								new VillagerEntity.ItemStackTradeEntry(Item.fromBlock(Blocks.BOOKSHELF), new VillagerEntity.Cost(3, 4))
						},
						{
								new VillagerEntity.ItemTradeEntry(Items.WRITTEN_BOOK, new VillagerEntity.Cost(2, 2)),
								new VillagerEntity.ItemStackTradeEntry(Items.CLOCK, new VillagerEntity.Cost(10, 12)),
								new VillagerEntity.ItemStackTradeEntry(Item.fromBlock(Blocks.GLASS), new VillagerEntity.Cost(-5, -3))
						},
						{new VillagerEntity.EnchantedBook()},
						{new VillagerEntity.EnchantedBook()},
						{new VillagerEntity.ItemStackTradeEntry(Items.NAME_TAG, new VillagerEntity.Cost(20, 22))}
				},
				{
						{new VillagerEntity.ItemTradeEntry(Items.PAPER, new VillagerEntity.Cost(24, 36))},
						{new VillagerEntity.ItemTradeEntry(Items.COMPASS, new VillagerEntity.Cost(1, 1))},
						{new VillagerEntity.ItemStackTradeEntry(Items.MAP, new VillagerEntity.Cost(7, 11))},
						{
								new VillagerEntity.class_3051(new VillagerEntity.Cost(12, 20), "Monument", class_3082.class_3083.MONUMENT),
								new VillagerEntity.class_3051(new VillagerEntity.Cost(16, 28), "Mansion", class_3082.class_3083.MANSION)
						}
				}
		},
		{
				{
						{
								new VillagerEntity.ItemTradeEntry(Items.ROTTEN_FLESH, new VillagerEntity.Cost(36, 40)),
								new VillagerEntity.ItemTradeEntry(Items.GOLD_INGOT, new VillagerEntity.Cost(8, 10))
						},
						{
								new VillagerEntity.ItemStackTradeEntry(Items.REDSTONE, new VillagerEntity.Cost(-4, -1)),
								new VillagerEntity.ItemStackTradeEntry(new ItemStack(Items.DYE, 1, DyeColor.BLUE.getSwappedId()), new VillagerEntity.Cost(-2, -1))
						},
						{
								new VillagerEntity.ItemStackTradeEntry(Items.ENDER_PEARL, new VillagerEntity.Cost(4, 7)),
								new VillagerEntity.ItemStackTradeEntry(Item.fromBlock(Blocks.GLOWSTONE), new VillagerEntity.Cost(-3, -1))
						},
						{new VillagerEntity.ItemStackTradeEntry(Items.EXPERIENCE_BOTTLE, new VillagerEntity.Cost(3, 11))}
				}
		},
		{
				{
						{
								new VillagerEntity.ItemTradeEntry(Items.COAL, new VillagerEntity.Cost(16, 24)),
								new VillagerEntity.ItemStackTradeEntry(Items.IRON_HELMET, new VillagerEntity.Cost(4, 6))
						},
						{
								new VillagerEntity.ItemTradeEntry(Items.IRON_INGOT, new VillagerEntity.Cost(7, 9)),
								new VillagerEntity.ItemStackTradeEntry(Items.IRON_CHESTPLATE, new VillagerEntity.Cost(10, 14))
						},
						{
								new VillagerEntity.ItemTradeEntry(Items.DIAMOND, new VillagerEntity.Cost(3, 4)),
								new VillagerEntity.EnchantedItemStackTradeEntry(Items.DIAMOND_CHESTPLATE, new VillagerEntity.Cost(16, 19))
						},
						{
								new VillagerEntity.ItemStackTradeEntry(Items.CHAINMAIL_BOOTS, new VillagerEntity.Cost(5, 7)),
								new VillagerEntity.ItemStackTradeEntry(Items.CHAINMAIL_LEGGINGS, new VillagerEntity.Cost(9, 11)),
								new VillagerEntity.ItemStackTradeEntry(Items.CHAINMAIL_HELMET, new VillagerEntity.Cost(5, 7)),
								new VillagerEntity.ItemStackTradeEntry(Items.CHAINMAIL_CHESTPLATE, new VillagerEntity.Cost(11, 15))
						}
				},
				{
						{
								new VillagerEntity.ItemTradeEntry(Items.COAL, new VillagerEntity.Cost(16, 24)),
								new VillagerEntity.ItemStackTradeEntry(Items.IRON_AXE, new VillagerEntity.Cost(6, 8))
						},
						{
								new VillagerEntity.ItemTradeEntry(Items.IRON_INGOT, new VillagerEntity.Cost(7, 9)),
								new VillagerEntity.EnchantedItemStackTradeEntry(Items.IRON_SWORD, new VillagerEntity.Cost(9, 10))
						},
						{
								new VillagerEntity.ItemTradeEntry(Items.DIAMOND, new VillagerEntity.Cost(3, 4)),
								new VillagerEntity.EnchantedItemStackTradeEntry(Items.DIAMOND_SWORD, new VillagerEntity.Cost(12, 15)),
								new VillagerEntity.EnchantedItemStackTradeEntry(Items.DIAMOND_AXE, new VillagerEntity.Cost(9, 12))
						}
				},
				{
						{
								new VillagerEntity.ItemTradeEntry(Items.COAL, new VillagerEntity.Cost(16, 24)),
								new VillagerEntity.EnchantedItemStackTradeEntry(Items.IRON_SHOVEL, new VillagerEntity.Cost(5, 7))
						},
						{
								new VillagerEntity.ItemTradeEntry(Items.IRON_INGOT, new VillagerEntity.Cost(7, 9)),
								new VillagerEntity.EnchantedItemStackTradeEntry(Items.IRON_PICKAXE, new VillagerEntity.Cost(9, 11))
						},
						{
								new VillagerEntity.ItemTradeEntry(Items.DIAMOND, new VillagerEntity.Cost(3, 4)),
								new VillagerEntity.EnchantedItemStackTradeEntry(Items.DIAMOND_PICKAXE, new VillagerEntity.Cost(12, 15))
						}
				}
		},
		{
				{
						{
								new VillagerEntity.ItemTradeEntry(Items.RAW_PORKCHOP, new VillagerEntity.Cost(14, 18)),
								new VillagerEntity.ItemTradeEntry(Items.CHICKEN, new VillagerEntity.Cost(14, 18))
						},
						{
								new VillagerEntity.ItemTradeEntry(Items.COAL, new VillagerEntity.Cost(16, 24)),
								new VillagerEntity.ItemStackTradeEntry(Items.COOKED_PORKCHOP, new VillagerEntity.Cost(-7, -5)),
								new VillagerEntity.ItemStackTradeEntry(Items.COOKED_CHICKEN, new VillagerEntity.Cost(-8, -6))
						}
				},
				{
						{
								new VillagerEntity.ItemTradeEntry(Items.LEATHER, new VillagerEntity.Cost(9, 12)),
								new VillagerEntity.ItemStackTradeEntry(Items.LEATHER_LEGGINGS, new VillagerEntity.Cost(2, 4))
						},
						{new VillagerEntity.EnchantedItemStackTradeEntry(Items.LEATHER_CHESTPLATE, new VillagerEntity.Cost(7, 12))},
						{new VillagerEntity.ItemStackTradeEntry(Items.SADDLE, new VillagerEntity.Cost(8, 10))}
				}
		},
		{new VillagerEntity.TradeProvider[0][]}
	};

	public VillagerEntity(World world) {
		this(world, 0);
	}

	public VillagerEntity(World world, int i) {
		super(world);
		this.setProfession(i);
		this.setBounds(0.6F, 1.95F);
		((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		this.setCanPickUpLoot(true);
	}

	@Override
	protected void initGoals() {
		this.goals.add(0, new SwimGoal(this));
		this.goals.add(1, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 0.6, 0.6));
		this.goals.add(1, new FleeEntityGoal(this, EvocationIllagerEntity.class, 12.0F, 0.8, 0.8));
		this.goals.add(1, new FleeEntityGoal(this, VindicationIllagerEntity.class, 8.0F, 0.8, 0.8));
		this.goals.add(1, new FleeEntityGoal(this, VexEntity.class, 8.0F, 0.6, 0.6));
		this.goals.add(1, new StopFollowingCustomerGoal(this));
		this.goals.add(1, new LookAtCustomerGoal(this));
		this.goals.add(2, new StayIndoorsGoal(this));
		this.goals.add(3, new RestrictOpenDoorGoal(this));
		this.goals.add(4, new LongDoorInteractGoal(this, true));
		this.goals.add(5, new GoToWalkTargetGoal(this, 0.6));
		this.goals.add(6, new VillagerMatingGoal(this));
		this.goals.add(7, new FollowGolemGoal(this));
		this.goals.add(9, new StopAndLookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goals.add(9, new VillagerInteractGoal(this));
		this.goals.add(9, new class_3133(this, 0.6));
		this.goals.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
	}

	private void method_11225() {
		if (!this.field_12106) {
			this.field_12106 = true;
			if (this.isBaby()) {
				this.goals.add(8, new FormCaravanGoal(this, 0.32));
			} else if (this.profession() == 0) {
				this.goals.add(6, new HarvestCropsGoal(this, 0.6));
			}
		}
	}

	@Override
	protected void method_10926() {
		if (this.profession() == 0) {
			this.goals.add(8, new HarvestCropsGoal(this, 0.6));
		}

		super.method_10926();
	}

	@Override
	protected void initializeAttributes() {
		super.initializeAttributes();
		this.initializeAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
	}

	@Override
	protected void mobTick() {
		if (--this.field_3951 <= 0) {
			BlockPos blockPos = new BlockPos(this);
			this.world.getVillageState().method_11061(blockPos);
			this.field_3951 = 70 + this.random.nextInt(50);
			this.field_3950 = this.world.getVillageState().method_11062(blockPos, 32);
			if (this.field_3950 == null) {
				this.method_6173();
			} else {
				BlockPos blockPos2 = this.field_3950.getMinPos();
				this.setPositionTarget(blockPos2, this.field_3950.getRadius());
				if (this.field_5396) {
					this.field_5396 = false;
					this.field_3950.method_4507(5);
				}
			}
		}

		if (!this.hasCustomer() && this.field_3956 > 0) {
			this.field_3956--;
			if (this.field_3956 <= 0) {
				if (this.field_3948) {
					for (TradeOffer tradeOffer : this.offers) {
						if (tradeOffer.isDisabled()) {
							tradeOffer.increaseSpecialPrice(this.random.nextInt(6) + this.random.nextInt(6) + 2);
						}
					}

					this.getOffers();
					this.field_3948 = false;
					if (this.field_3950 != null && this.field_5395 != null) {
						this.world.sendEntityStatus(this, (byte)14);
						this.field_3950.method_4505(this.field_5395, 1);
					}
				}

				this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
			}
		}

		super.mobTick();
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		boolean bl = itemStack.getItem() == Items.NAME_TAG;
		if (bl) {
			itemStack.method_6329(playerEntity, this, hand);
			return true;
		} else if (!this.method_13929(itemStack, this.getClass()) && this.isAlive() && !this.hasCustomer() && !this.isBaby()) {
			if (this.offers == null) {
				this.getOffers();
			}

			if (hand == Hand.MAIN_HAND) {
				playerEntity.incrementStat(Stats.TALKED_TO_VILLAGER);
			}

			if (!this.world.isClient && !this.offers.isEmpty()) {
				this.setCurrentCustomer(playerEntity);
				playerEntity.openTradingScreen(this);
			} else if (this.offers.isEmpty()) {
				return super.interactMob(playerEntity, hand);
			}

			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_14789, 0);
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		MobEntity.registerDataFixes(dataFixer, VillagerEntity.class);
		dataFixer.addSchema(LevelDataType.ENTITY, new ItemListSchema(VillagerEntity.class, "Inventory"));
		dataFixer.addSchema(LevelDataType.ENTITY, new Schema() {
			@Override
			public NbtCompound fixData(DataFixer dataFixer, NbtCompound tag, int dataVersion) {
				if (EntityType.getId(VillagerEntity.class).equals(new Identifier(tag.getString("id"))) && tag.contains("Offers", 10)) {
					NbtCompound nbtCompound = tag.getCompound("Offers");
					if (nbtCompound.contains("Recipes", 9)) {
						NbtList nbtList = nbtCompound.getList("Recipes", 10);

						for (int i = 0; i < nbtList.size(); i++) {
							NbtCompound nbtCompound2 = nbtList.getCompound(i);
							DataFixerFactory.updateItem(dataFixer, nbtCompound2, dataVersion, "buy");
							DataFixerFactory.updateItem(dataFixer, nbtCompound2, dataVersion, "buyB");
							DataFixerFactory.updateItem(dataFixer, nbtCompound2, dataVersion, "sell");
							nbtList.set(i, nbtCompound2);
						}
					}
				}

				return tag;
			}
		});
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Profession", this.profession());
		nbt.putInt("Riches", this.riches);
		nbt.putInt("Career", this.career);
		nbt.putInt("CareerLevel", this.careerLevel);
		nbt.putBoolean("Willing", this.willingToMate);
		if (this.offers != null) {
			nbt.put("Offers", this.offers.toNbt());
		}

		NbtList nbtList = new NbtList();

		for (int i = 0; i < this.villagerInventory.getInvSize(); i++) {
			ItemStack itemStack = this.villagerInventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				nbtList.add(itemStack.toNbt(new NbtCompound()));
			}
		}

		nbt.put("Inventory", nbtList);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setProfession(nbt.getInt("Profession"));
		this.riches = nbt.getInt("Riches");
		this.career = nbt.getInt("Career");
		this.careerLevel = nbt.getInt("CareerLevel");
		this.willingToMate = nbt.getBoolean("Willing");
		if (nbt.contains("Offers", 10)) {
			NbtCompound nbtCompound = nbt.getCompound("Offers");
			this.offers = new TraderOfferList(nbtCompound);
		}

		NbtList nbtList = nbt.getList("Inventory", 10);

		for (int i = 0; i < nbtList.size(); i++) {
			ItemStack itemStack = new ItemStack(nbtList.getCompound(i));
			if (!itemStack.isEmpty()) {
				this.villagerInventory.fillInventoryWith(itemStack);
			}
		}

		this.setCanPickUpLoot(true);
		this.method_11225();
	}

	@Override
	protected boolean canImmediatelyDespawn() {
		return false;
	}

	@Override
	protected Sound ambientSound() {
		return this.hasCustomer() ? Sounds.ENTITY_VILLAGER_TRADING : Sounds.ENTITY_VILLAGER_AMBIENT;
	}

	@Override
	protected Sound method_13048() {
		return Sounds.ENTITY_VILLAGER_HURT;
	}

	@Override
	protected Sound deathSound() {
		return Sounds.ENTITY_VILLAGER_DEATH;
	}

	@Nullable
	@Override
	protected Identifier getLootTableId() {
		return LootTables.VILLAGER_ENTITIE;
	}

	public void setProfession(int profession) {
		this.dataTracker.set(field_14789, profession);
	}

	public int profession() {
		return Math.max(this.dataTracker.get(field_14789) % 6, 0);
	}

	public boolean method_3116() {
		return this.field_3952;
	}

	public void method_3113(boolean bl) {
		this.field_3952 = bl;
	}

	public void method_3114(boolean bl) {
		this.field_3953 = bl;
	}

	public boolean method_3117() {
		return this.field_3953;
	}

	@Override
	public void setAttacker(@Nullable LivingEntity entity) {
		super.setAttacker(entity);
		if (this.field_3950 != null && entity != null) {
			this.field_3950.addAttacker(entity);
			if (entity instanceof PlayerEntity) {
				int i = -1;
				if (this.isBaby()) {
					i = -3;
				}

				this.field_3950.method_4505(entity.getTranslationKey(), i);
				if (this.isAlive()) {
					this.world.sendEntityStatus(this, (byte)13);
				}
			}
		}
	}

	@Override
	public void onKilled(DamageSource source) {
		if (this.field_3950 != null) {
			Entity entity = source.getAttacker();
			if (entity != null) {
				if (entity instanceof PlayerEntity) {
					this.field_3950.method_4505(entity.getTranslationKey(), -2);
				} else if (entity instanceof Monster) {
					this.field_3950.method_4511();
				}
			} else {
				PlayerEntity playerEntity = this.world.getClosestPlayer(this, 16.0);
				if (playerEntity != null) {
					this.field_3950.method_4511();
				}
			}
		}

		super.onKilled(source);
	}

	@Override
	public void setCurrentCustomer(PlayerEntity player) {
		this.customer = player;
	}

	@Override
	public PlayerEntity getCurrentCustomer() {
		return this.customer;
	}

	public boolean hasCustomer() {
		return this.customer != null;
	}

	public boolean method_11227(boolean bl) {
		if (!this.willingToMate && bl && this.method_11221()) {
			boolean bl2 = false;

			for (int i = 0; i < this.villagerInventory.getInvSize(); i++) {
				ItemStack itemStack = this.villagerInventory.getInvStack(i);
				if (!itemStack.isEmpty()) {
					if (itemStack.getItem() == Items.BREAD && itemStack.getCount() >= 3) {
						bl2 = true;
						this.villagerInventory.takeInvStack(i, 3);
					} else if ((itemStack.getItem() == Items.POTATO || itemStack.getItem() == Items.CARROT) && itemStack.getCount() >= 12) {
						bl2 = true;
						this.villagerInventory.takeInvStack(i, 12);
					}
				}

				if (bl2) {
					this.world.sendEntityStatus(this, (byte)18);
					this.willingToMate = true;
					break;
				}
			}
		}

		return this.willingToMate;
	}

	public void method_11228(boolean bl) {
		this.willingToMate = bl;
	}

	@Override
	public void trade(TradeOffer offer) {
		offer.use();
		this.ambientSoundChance = -this.getMinAmbientSoundDelay();
		this.playSound(Sounds.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
		int i = 3 + this.random.nextInt(4);
		if (offer.getUses() == 1 || this.random.nextInt(5) == 0) {
			this.field_3956 = 40;
			this.field_3948 = true;
			this.willingToMate = true;
			if (this.customer != null) {
				this.field_5395 = this.customer.getTranslationKey();
			} else {
				this.field_5395 = null;
			}

			i += 5;
		}

		if (offer.getFirstStack().getItem() == Items.EMERALD) {
			this.riches = this.riches + offer.getFirstStack().getCount();
		}

		if (offer.shouldRewardPlayerExperience()) {
			this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y + 0.5, this.z, i));
		}
	}

	@Override
	public void method_5501(ItemStack stack) {
		if (!this.world.isClient && this.ambientSoundChance > -this.getMinAmbientSoundDelay() + 20) {
			this.ambientSoundChance = -this.getMinAmbientSoundDelay();
			this.playSound(stack.isEmpty() ? Sounds.ENTITY_VILLAGER_NO : Sounds.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Nullable
	@Override
	public TraderOfferList getOffers(PlayerEntity player) {
		if (this.offers == null) {
			this.getOffers();
		}

		return this.offers;
	}

	private void getOffers() {
		VillagerEntity.TradeProvider[][][] tradeProviders = TRADES[this.profession()];
		if (this.career != 0 && this.careerLevel != 0) {
			this.careerLevel++;
		} else {
			this.career = this.random.nextInt(tradeProviders.length) + 1;
			this.careerLevel = 1;
		}

		if (this.offers == null) {
			this.offers = new TraderOfferList();
		}

		int i = this.career - 1;
		int j = this.careerLevel - 1;
		if (i >= 0 && i < tradeProviders.length) {
			VillagerEntity.TradeProvider[][] tradeProviders2 = tradeProviders[i];
			if (j >= 0 && j < tradeProviders2.length) {
				VillagerEntity.TradeProvider[] tradeProviders3 = tradeProviders2[j];

				for (VillagerEntity.TradeProvider tradeProvider : tradeProviders3) {
					tradeProvider.method_11230(this, this.offers, this.random);
				}
			}
		}
	}

	@Override
	public void setTraderOfferList(@Nullable TraderOfferList list) {
	}

	@Override
	public World method_13682() {
		return this.world;
	}

	@Override
	public BlockPos method_13683() {
		return new BlockPos(this);
	}

	@Override
	public Text getName() {
		AbstractTeam abstractTeam = this.getScoreboardTeam();
		String string = this.getCustomName();
		if (string != null && !string.isEmpty()) {
			LiteralText literalText = new LiteralText(Team.decorateName(abstractTeam, string));
			literalText.getStyle().setHoverEvent(this.getHoverEvent());
			literalText.getStyle().setInsertion(this.getEntityName());
			return literalText;
		} else {
			if (this.offers == null) {
				this.getOffers();
			}

			String string2 = null;
			switch (this.profession()) {
				case 0:
					if (this.career == 1) {
						string2 = "farmer";
					} else if (this.career == 2) {
						string2 = "fisherman";
					} else if (this.career == 3) {
						string2 = "shepherd";
					} else if (this.career == 4) {
						string2 = "fletcher";
					}
					break;
				case 1:
					if (this.career == 1) {
						string2 = "librarian";
					} else if (this.career == 2) {
						string2 = "cartographer";
					}
					break;
				case 2:
					string2 = "cleric";
					break;
				case 3:
					if (this.career == 1) {
						string2 = "armor";
					} else if (this.career == 2) {
						string2 = "weapon";
					} else if (this.career == 3) {
						string2 = "tool";
					}
					break;
				case 4:
					if (this.career == 1) {
						string2 = "butcher";
					} else if (this.career == 2) {
						string2 = "leather";
					}
					break;
				case 5:
					string2 = "nitwit";
			}

			if (string2 != null) {
				Text text = new TranslatableText("entity.Villager." + string2);
				text.getStyle().setHoverEvent(this.getHoverEvent());
				text.getStyle().setInsertion(this.getEntityName());
				if (abstractTeam != null) {
					text.getStyle().setFormatting(abstractTeam.method_12130());
				}

				return text;
			} else {
				return super.getName();
			}
		}
	}

	@Override
	public float getEyeHeight() {
		return this.isBaby() ? 0.81F : 1.62F;
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 12) {
			this.addParticle(ParticleType.HEART);
		} else if (status == 13) {
			this.addParticle(ParticleType.ANGRY_VILLAGER);
		} else if (status == 14) {
			this.addParticle(ParticleType.HAPPY_VILLAGER);
		} else {
			super.handleStatus(status);
		}
	}

	private void addParticle(ParticleType particleType) {
		for (int i = 0; i < 5; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.addParticle(
					particleType,
					this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					this.y + 1.0 + (double)(this.random.nextFloat() * this.height),
					this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					d,
					e,
					f
				);
		}
	}

	@Nullable
	@Override
	public EntityData initialize(LocalDifficulty difficulty, @Nullable EntityData data) {
		return this.initialize(difficulty, data, true);
	}

	public EntityData initialize(LocalDifficulty localDifficulty, @Nullable EntityData entityData, boolean bl) {
		entityData = super.initialize(localDifficulty, entityData);
		if (bl) {
			this.setProfession(this.world.random.nextInt(6));
		}

		this.method_11225();
		this.getOffers();
		return entityData;
	}

	public void method_4567() {
		this.field_5396 = true;
	}

	public VillagerEntity breed(PassiveEntity passiveEntity) {
		VillagerEntity villagerEntity = new VillagerEntity(this.world);
		villagerEntity.initialize(this.world.getLocalDifficulty(new BlockPos(villagerEntity)), null);
		return villagerEntity;
	}

	@Override
	public boolean method_2537(PlayerEntity playerEntity) {
		return false;
	}

	@Override
	public void onLightningStrike(LightningBoltEntity lightning) {
		if (!this.world.isClient && !this.removed) {
			WitchEntity witchEntity = new WitchEntity(this.world);
			witchEntity.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
			witchEntity.initialize(this.world.getLocalDifficulty(new BlockPos(witchEntity)), null);
			witchEntity.setAiDisabled(this.hasNoAi());
			if (this.hasCustomName()) {
				witchEntity.setCustomName(this.getCustomName());
				witchEntity.setCustomNameVisible(this.isCustomNameVisible());
			}

			this.world.spawnEntity(witchEntity);
			this.remove();
		}
	}

	public SimpleInventory method_11220() {
		return this.villagerInventory;
	}

	@Override
	protected void loot(ItemEntity item) {
		ItemStack itemStack = item.getItemStack();
		Item item2 = itemStack.getItem();
		if (this.canPickUp(item2)) {
			ItemStack itemStack2 = this.villagerInventory.fillInventoryWith(itemStack);
			if (itemStack2.isEmpty()) {
				item.remove();
			} else {
				itemStack.setCount(itemStack2.getCount());
			}
		}
	}

	private boolean canPickUp(Item item) {
		return item == Items.BREAD
			|| item == Items.POTATO
			|| item == Items.CARROT
			|| item == Items.WHEAT
			|| item == Items.WHEAT_SEEDS
			|| item == Items.BEETROOT
			|| item == Items.BEETROOT_SEED;
	}

	public boolean method_11221() {
		return this.method_11229(1);
	}

	public boolean method_11222() {
		return this.method_11229(2);
	}

	public boolean method_11223() {
		boolean bl = this.profession() == 0;
		return bl ? !this.method_11229(5) : !this.method_11229(1);
	}

	private boolean method_11229(int i) {
		boolean bl = this.profession() == 0;

		for (int j = 0; j < this.villagerInventory.getInvSize(); j++) {
			ItemStack itemStack = this.villagerInventory.getInvStack(j);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() == Items.BREAD && itemStack.getCount() >= 3 * i
					|| itemStack.getItem() == Items.POTATO && itemStack.getCount() >= 12 * i
					|| itemStack.getItem() == Items.CARROT && itemStack.getCount() >= 12 * i
					|| itemStack.getItem() == Items.BEETROOT && itemStack.getCount() >= 12 * i) {
					return true;
				}

				if (bl && itemStack.getItem() == Items.WHEAT && itemStack.getCount() >= 9 * i) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasSeedToPlant() {
		for (int i = 0; i < this.villagerInventory.getInvSize(); i++) {
			ItemStack itemStack = this.villagerInventory.getInvStack(i);
			if (!itemStack.isEmpty()
				&& (
					itemStack.getItem() == Items.WHEAT_SEEDS
						|| itemStack.getItem() == Items.POTATO
						|| itemStack.getItem() == Items.CARROT
						|| itemStack.getItem() == Items.BEETROOT_SEED
				)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean equip(int slot, ItemStack item) {
		if (super.equip(slot, item)) {
			return true;
		} else {
			int i = slot - 300;
			if (i >= 0 && i < this.villagerInventory.getInvSize()) {
				this.villagerInventory.setInvStack(i, item);
				return true;
			} else {
				return false;
			}
		}
	}

	static class Cost extends Pair<Integer, Integer> {
		public Cost(int i, int j) {
			super(i, j);
			if (j < i) {
				VillagerEntity.VILLAGER_LOGGER.warn("PriceRange({}, {}) invalid, {} smaller than {}", new Object[]{i, j, j, i});
			}
		}

		public int getCost(Random random) {
			return this.getLeft() >= this.getRight() ? this.getLeft() : this.getLeft() + random.nextInt(this.getRight() - this.getLeft() + 1);
		}
	}

	static class EmeraldToItem implements VillagerEntity.TradeProvider {
		public ItemStack stack1;
		public VillagerEntity.Cost field_12117;
		public ItemStack field_12118;
		public VillagerEntity.Cost field_12119;

		public EmeraldToItem(Item item, VillagerEntity.Cost cost, Item item2, VillagerEntity.Cost cost2) {
			this.stack1 = new ItemStack(item);
			this.field_12117 = cost;
			this.field_12118 = new ItemStack(item2);
			this.field_12119 = cost2;
		}

		@Override
		public void method_11230(Trader trader, TraderOfferList traderOfferList, Random random) {
			int i = this.field_12117.getCost(random);
			int j = this.field_12119.getCost(random);
			traderOfferList.add(
				new TradeOffer(
					new ItemStack(this.stack1.getItem(), i, this.stack1.getData()),
					new ItemStack(Items.EMERALD),
					new ItemStack(this.field_12118.getItem(), j, this.field_12118.getData())
				)
			);
		}
	}

	static class EnchantedBook implements VillagerEntity.TradeProvider {
		public EnchantedBook() {
		}

		@Override
		public void method_11230(Trader trader, TraderOfferList traderOfferList, Random random) {
			Enchantment enchantment = Enchantment.REGISTRY.method_12584(random);
			int i = MathHelper.nextInt(random, enchantment.getMinimumLevel(), enchantment.getMaximumLevel());
			ItemStack itemStack = Items.ENCHANTED_BOOK.getAsItemStack(new EnchantmentLevelEntry(enchantment, i));
			int j = 2 + random.nextInt(5 + i * 10) + 3 * i;
			if (enchantment.isTreasure()) {
				j *= 2;
			}

			if (j > 64) {
				j = 64;
			}

			traderOfferList.add(new TradeOffer(new ItemStack(Items.BOOK), new ItemStack(Items.EMERALD, j), itemStack));
		}
	}

	static class EnchantedItemStackTradeEntry implements VillagerEntity.TradeProvider {
		public ItemStack item;
		public VillagerEntity.Cost cost;

		public EnchantedItemStackTradeEntry(Item item, VillagerEntity.Cost cost) {
			this.item = new ItemStack(item);
			this.cost = cost;
		}

		@Override
		public void method_11230(Trader trader, TraderOfferList traderOfferList, Random random) {
			int i = 1;
			if (this.cost != null) {
				i = this.cost.getCost(random);
			}

			ItemStack itemStack = new ItemStack(Items.EMERALD, i, 0);
			ItemStack itemStack2 = EnchantmentHelper.enchant(random, new ItemStack(this.item.getItem(), 1, this.item.getData()), 5 + random.nextInt(15), false);
			traderOfferList.add(new TradeOffer(itemStack, itemStack2));
		}
	}

	static class ItemStackTradeEntry implements VillagerEntity.TradeProvider {
		public ItemStack item;
		public VillagerEntity.Cost cost;

		public ItemStackTradeEntry(Item item, VillagerEntity.Cost cost) {
			this.item = new ItemStack(item);
			this.cost = cost;
		}

		public ItemStackTradeEntry(ItemStack itemStack, VillagerEntity.Cost cost) {
			this.item = itemStack;
			this.cost = cost;
		}

		@Override
		public void method_11230(Trader trader, TraderOfferList traderOfferList, Random random) {
			int i = 1;
			if (this.cost != null) {
				i = this.cost.getCost(random);
			}

			ItemStack itemStack;
			ItemStack itemStack2;
			if (i < 0) {
				itemStack = new ItemStack(Items.EMERALD);
				itemStack2 = new ItemStack(this.item.getItem(), -i, this.item.getData());
			} else {
				itemStack = new ItemStack(Items.EMERALD, i, 0);
				itemStack2 = new ItemStack(this.item.getItem(), 1, this.item.getData());
			}

			traderOfferList.add(new TradeOffer(itemStack, itemStack2));
		}
	}

	static class ItemTradeEntry implements VillagerEntity.TradeProvider {
		public Item item;
		public VillagerEntity.Cost cost;

		public ItemTradeEntry(Item item, VillagerEntity.Cost cost) {
			this.item = item;
			this.cost = cost;
		}

		@Override
		public void method_11230(Trader trader, TraderOfferList traderOfferList, Random random) {
			int i = 1;
			if (this.cost != null) {
				i = this.cost.getCost(random);
			}

			traderOfferList.add(new TradeOffer(new ItemStack(this.item, i, 0), Items.EMERALD));
		}
	}

	interface TradeProvider {
		void method_11230(Trader trader, TraderOfferList traderOfferList, Random random);
	}

	static class class_3051 implements VillagerEntity.TradeProvider {
		public VillagerEntity.Cost field_15079;
		public String field_15080;
		public class_3082.class_3083 field_15081;

		public class_3051(VillagerEntity.Cost cost, String string, class_3082.class_3083 arg) {
			this.field_15079 = cost;
			this.field_15080 = string;
			this.field_15081 = arg;
		}

		@Override
		public void method_11230(Trader trader, TraderOfferList traderOfferList, Random random) {
			int i = this.field_15079.getCost(random);
			World world = trader.method_13682();
			BlockPos blockPos = world.method_13688(this.field_15080, trader.method_13683(), true);
			if (blockPos != null) {
				ItemStack itemStack = FilledMapItem.method_13663(world, (double)blockPos.getX(), (double)blockPos.getZ(), (byte)2, true, true);
				FilledMapItem.method_13664(world, itemStack);
				MapState.method_13830(itemStack, blockPos, "+", this.field_15081);
				itemStack.method_13661("filled_map." + this.field_15080.toLowerCase(Locale.ROOT));
				traderOfferList.add(new TradeOffer(new ItemStack(Items.EMERALD, i), new ItemStack(Items.COMPASS), itemStack));
			}
		}
	}
}
