package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.datafixer.schema.BlockEntitySchema;
import net.minecraft.datafixer.schema.EntityTagSchema;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.stat.Stats;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelDataType;

public final class ItemStack {
	public static final DecimalFormat MODIFIER_FORMAT = new DecimalFormat("#.##");
	public int count;
	public int pickupTick;
	private Item item;
	private NbtCompound nbt;
	private int damage;
	private ItemFrameEntity itemFrame;
	private Block lastDestroyedBlock;
	private boolean lastDestroyResult;
	private Block lastPlacedOn;
	private boolean lastPlaceOnResult;

	public ItemStack(Block block) {
		this(block, 1);
	}

	public ItemStack(Block block, int i) {
		this(block, i, 0);
	}

	public ItemStack(Block block, int i, int j) {
		this(Item.fromBlock(block), i, j);
	}

	public ItemStack(Item item) {
		this(item, 1);
	}

	public ItemStack(Item item, int i) {
		this(item, i, 0);
	}

	public ItemStack(Item item, int i, int j) {
		this.item = item;
		this.count = i;
		this.damage = j;
		if (this.damage < 0) {
			this.damage = 0;
		}
	}

	public static ItemStack fromNbt(NbtCompound nbt) {
		ItemStack itemStack = new ItemStack();
		itemStack.writeNbt(nbt);
		return itemStack.getItem() != null ? itemStack : null;
	}

	private ItemStack() {
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
		dataFixer.addSchema(LevelDataType.ITEM_INSTANCE, new BlockEntitySchema());
		dataFixer.addSchema(LevelDataType.ITEM_INSTANCE, new EntityTagSchema());
	}

	public ItemStack split(int amount) {
		amount = Math.min(amount, this.count);
		ItemStack itemStack = new ItemStack(this.item, amount, this.damage);
		if (this.nbt != null) {
			itemStack.nbt = this.nbt.copy();
		}

		this.count -= amount;
		return itemStack;
	}

	public Item getItem() {
		return this.item;
	}

	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		ActionResult actionResult = this.getItem().method_3355(this, player, world, pos, hand, direction, x, y, z);
		if (actionResult == ActionResult.SUCCESS) {
			player.incrementStat(Stats.used(this.item));
		}

		return actionResult;
	}

	public float getBlockBreakingSpeed(BlockState state) {
		return this.getItem().getBlockBreakingSpeed(this, state);
	}

	public TypedActionResult<ItemStack> method_11390(World world, PlayerEntity player, Hand hand) {
		return this.getItem().method_11373(this, world, player, hand);
	}

	@Nullable
	public ItemStack method_11388(World world, LivingEntity entity) {
		return this.getItem().method_3367(this, world, entity);
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		Identifier identifier = Item.REGISTRY.getIdentifier(this.item);
		nbt.putString("id", identifier == null ? "minecraft:air" : identifier.toString());
		nbt.putByte("Count", (byte)this.count);
		nbt.putShort("Damage", (short)this.damage);
		if (this.nbt != null) {
			nbt.put("tag", this.nbt);
		}

		return nbt;
	}

	public void writeNbt(NbtCompound nbt) {
		this.item = Item.getFromId(nbt.getString("id"));
		this.count = nbt.getByte("Count");
		this.damage = nbt.getShort("Damage");
		if (this.damage < 0) {
			this.damage = 0;
		}

		if (nbt.contains("tag", 10)) {
			this.nbt = nbt.getCompound("tag");
			if (this.item != null) {
				this.item.postProcessNbt(this.nbt);
			}
		}
	}

	public int getMaxCount() {
		return this.getItem().getMaxCount();
	}

	public boolean isStackable() {
		return this.getMaxCount() > 1 && (!this.isDamageable() || !this.isDamaged());
	}

	public boolean isDamageable() {
		if (this.item == null) {
			return false;
		} else {
			return this.item.getMaxDamage() <= 0 ? false : !this.hasNbt() || !this.getNbt().getBoolean("Unbreakable");
		}
	}

	public boolean isUnbreakable() {
		return this.item.isUnbreakable();
	}

	public boolean isDamaged() {
		return this.isDamageable() && this.damage > 0;
	}

	public int getDamage() {
		return this.damage;
	}

	public int getData() {
		return this.damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
		if (this.damage < 0) {
			this.damage = 0;
		}
	}

	public int getMaxDamage() {
		return this.item == null ? 0 : this.item.getMaxDamage();
	}

	public boolean damage(int amount, Random random) {
		if (!this.isDamageable()) {
			return false;
		} else {
			if (amount > 0) {
				int i = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, this);
				int j = 0;

				for (int k = 0; i > 0 && k < amount; k++) {
					if (UnbreakingEnchantment.shouldPreventDamage(this, i, random)) {
						j++;
					}
				}

				amount -= j;
				if (amount <= 0) {
					return false;
				}
			}

			this.damage += amount;
			return this.damage > this.getMaxDamage();
		}
	}

	public void damage(int amount, LivingEntity entity) {
		if (!(entity instanceof PlayerEntity) || !((PlayerEntity)entity).abilities.creativeMode) {
			if (this.isDamageable()) {
				if (this.damage(amount, entity.getRandom())) {
					entity.method_6111(this);
					this.count--;
					if (entity instanceof PlayerEntity) {
						PlayerEntity playerEntity = (PlayerEntity)entity;
						playerEntity.incrementStat(Stats.broke(this.item));
					}

					if (this.count < 0) {
						this.count = 0;
					}

					this.damage = 0;
				}
			}
		}
	}

	public void onEntityHit(LivingEntity entity, PlayerEntity attacker) {
		boolean bl = this.item.onEntityHit(this, entity, attacker);
		if (bl) {
			attacker.incrementStat(Stats.used(this.item));
		}
	}

	public void method_11306(World world, BlockState blockState, BlockPos blockPos, PlayerEntity playerEntity) {
		boolean bl = this.item.method_3356(this, world, blockState, blockPos, playerEntity);
		if (bl) {
			playerEntity.incrementStat(Stats.used(this.item));
		}
	}

	public boolean method_11396(BlockState state) {
		return this.item.method_3346(state);
	}

	public boolean method_6329(PlayerEntity player, LivingEntity entity, Hand hand) {
		return this.item.method_3353(this, player, entity, hand);
	}

	public ItemStack copy() {
		ItemStack itemStack = new ItemStack(this.item, this.count, this.damage);
		if (this.nbt != null) {
			itemStack.nbt = this.nbt.copy();
		}

		return itemStack;
	}

	public static boolean equalsIgnoreDamage(@Nullable ItemStack left, @Nullable ItemStack right) {
		if (left == null && right == null) {
			return true;
		} else if (left == null || right == null) {
			return false;
		} else {
			return left.nbt == null && right.nbt != null ? false : left.nbt == null || left.nbt.equals(right.nbt);
		}
	}

	public static boolean equalsAll(@Nullable ItemStack left, @Nullable ItemStack right) {
		if (left == null && right == null) {
			return true;
		} else {
			return left != null && right != null ? left.equalsAll(right) : false;
		}
	}

	private boolean equalsAll(ItemStack stack) {
		if (this.count != stack.count) {
			return false;
		} else if (this.item != stack.item) {
			return false;
		} else if (this.damage != stack.damage) {
			return false;
		} else {
			return this.nbt == null && stack.nbt != null ? false : this.nbt == null || this.nbt.equals(stack.nbt);
		}
	}

	public static boolean equalsIgnoreNbt(@Nullable ItemStack left, @Nullable ItemStack right) {
		if (left == right) {
			return true;
		} else {
			return left != null && right != null ? left.equalsIgnoreNbt(right) : false;
		}
	}

	public static boolean equals(@Nullable ItemStack stack0, @Nullable ItemStack stack1) {
		if (stack0 == stack1) {
			return true;
		} else {
			return stack0 != null && stack1 != null ? stack0.equals(stack1) : false;
		}
	}

	public boolean equalsIgnoreNbt(@Nullable ItemStack stack) {
		return stack != null && this.item == stack.item && this.damage == stack.damage;
	}

	public boolean equals(@Nullable ItemStack other) {
		return !this.isDamageable() ? this.equalsIgnoreNbt(other) : other != null && this.item == other.item;
	}

	public String getTranslationKey() {
		return this.item.getTranslationKey(this);
	}

	public static ItemStack copyOf(ItemStack stack) {
		return stack == null ? null : stack.copy();
	}

	public String toString() {
		return this.count + "x" + this.item.getTranslationKey() + "@" + this.damage;
	}

	public void inventoryTick(World world, Entity entity, int slot, boolean selected) {
		if (this.pickupTick > 0) {
			this.pickupTick--;
		}

		if (this.item != null) {
			this.item.inventoryTick(this, world, entity, slot, selected);
		}
	}

	public void onCraft(World world, PlayerEntity player, int amount) {
		player.incrementStat(Stats.crafted(this.item), amount);
		this.item.onCraft(this, world, player);
	}

	public int getMaxUseTime() {
		return this.getItem().getMaxUseTime(this);
	}

	public UseAction getUseAction() {
		return this.getItem().getUseAction(this);
	}

	public void method_11389(World world, LivingEntity livingEntity, int i) {
		this.getItem().method_3359(this, world, livingEntity, i);
	}

	public boolean hasNbt() {
		return this.nbt != null;
	}

	@Nullable
	public NbtCompound getNbt() {
		return this.nbt;
	}

	public NbtCompound getSubNbt(String name, boolean createIfNull) {
		if (this.nbt != null && this.nbt.contains(name, 10)) {
			return this.nbt.getCompound(name);
		} else if (createIfNull) {
			NbtCompound nbtCompound = new NbtCompound();
			this.putSubNbt(name, nbtCompound);
			return nbtCompound;
		} else {
			return null;
		}
	}

	public NbtList getEnchantments() {
		return this.nbt == null ? null : this.nbt.getList("ench", 10);
	}

	public void setNbt(NbtCompound nbt) {
		this.nbt = nbt;
	}

	public String getCustomName() {
		String string = this.getItem().getDisplayName(this);
		if (this.nbt != null && this.nbt.contains("display", 10)) {
			NbtCompound nbtCompound = this.nbt.getCompound("display");
			if (nbtCompound.contains("Name", 8)) {
				string = nbtCompound.getString("Name");
			}
		}

		return string;
	}

	public ItemStack setCustomName(String name) {
		if (this.nbt == null) {
			this.nbt = new NbtCompound();
		}

		if (!this.nbt.contains("display", 10)) {
			this.nbt.put("display", new NbtCompound());
		}

		this.nbt.getCompound("display").putString("Name", name);
		return this;
	}

	public void removeCustomName() {
		if (this.nbt != null) {
			if (this.nbt.contains("display", 10)) {
				NbtCompound nbtCompound = this.nbt.getCompound("display");
				nbtCompound.remove("Name");
				if (nbtCompound.isEmpty()) {
					this.nbt.remove("display");
					if (this.nbt.isEmpty()) {
						this.setNbt(null);
					}
				}
			}
		}
	}

	public boolean hasCustomName() {
		if (this.nbt == null) {
			return false;
		} else {
			return !this.nbt.contains("display", 10) ? false : this.nbt.getCompound("display").contains("Name", 8);
		}
	}

	public List<String> getTooltip(PlayerEntity player, boolean advanced) {
		List<String> list = Lists.newArrayList();
		String string = this.getCustomName();
		if (this.hasCustomName()) {
			string = Formatting.ITALIC + string;
		}

		string = string + Formatting.RESET;
		if (advanced) {
			String string2 = "";
			if (!string.isEmpty()) {
				string = string + " (";
				string2 = ")";
			}

			int i = Item.getRawId(this.item);
			if (this.isUnbreakable()) {
				string = string + String.format("#%04d/%d%s", i, this.damage, string2);
			} else {
				string = string + String.format("#%04d%s", i, string2);
			}
		} else if (!this.hasCustomName() && this.item == Items.FILLED_MAP) {
			string = string + " #" + this.damage;
		}

		list.add(string);
		int j = 0;
		if (this.hasNbt() && this.nbt.contains("HideFlags", 99)) {
			j = this.nbt.getInt("HideFlags");
		}

		if ((j & 32) == 0) {
			this.item.appendTooltip(this, player, list, advanced);
		}

		if (this.hasNbt()) {
			if ((j & 1) == 0) {
				NbtList nbtList = this.getEnchantments();
				if (nbtList != null) {
					for (int k = 0; k < nbtList.size(); k++) {
						int l = nbtList.getCompound(k).getShort("id");
						int m = nbtList.getCompound(k).getShort("lvl");
						if (Enchantment.byIndex(l) != null) {
							list.add(Enchantment.byIndex(l).getTranslatedName(m));
						}
					}
				}
			}

			if (this.nbt.contains("display", 10)) {
				NbtCompound nbtCompound = this.nbt.getCompound("display");
				if (nbtCompound.contains("color", 3)) {
					if (advanced) {
						list.add("Color: #" + String.format("%06X", nbtCompound.getInt("color")));
					} else {
						list.add(Formatting.ITALIC + CommonI18n.translate("item.dyed"));
					}
				}

				if (nbtCompound.getType("Lore") == 9) {
					NbtList nbtList2 = nbtCompound.getList("Lore", 8);
					if (!nbtList2.isEmpty()) {
						for (int n = 0; n < nbtList2.size(); n++) {
							list.add(Formatting.DARK_PURPLE + "" + Formatting.ITALIC + nbtList2.getString(n));
						}
					}
				}
			}
		}

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			Multimap<String, AttributeModifier> multimap = this.getAttributes(equipmentSlot);
			if (!multimap.isEmpty() && (j & 2) == 0) {
				list.add("");
				list.add(CommonI18n.translate("item.modifiers." + equipmentSlot.getName()));

				for (Entry<String, AttributeModifier> entry : multimap.entries()) {
					AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
					double d = attributeModifier.getAmount();
					boolean bl = false;
					if (attributeModifier.getId() == Item.ATTACK_DAMAGE_MODIFIER_UUID) {
						d += player.initializeAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE).getBaseValue();
						d += (double)EnchantmentHelper.getAttackDamage(this, EntityGroup.DEFAULT);
						bl = true;
					} else if (attributeModifier.getId() == Item.ATTACK_SPEED_MODIFIER) {
						d += player.initializeAttribute(EntityAttributes.GENERIC_ATTACK_SPEED).getBaseValue();
						bl = true;
					}

					double f;
					if (attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2) {
						f = d;
					} else {
						f = d * 100.0;
					}

					if (bl) {
						list.add(
							" "
								+ CommonI18n.translate(
									"attribute.modifier.equals." + attributeModifier.getOperation(),
									MODIFIER_FORMAT.format(f),
									CommonI18n.translate("attribute.name." + (String)entry.getKey())
								)
						);
					} else if (d > 0.0) {
						list.add(
							Formatting.BLUE
								+ " "
								+ CommonI18n.translate(
									"attribute.modifier.plus." + attributeModifier.getOperation(),
									MODIFIER_FORMAT.format(f),
									CommonI18n.translate("attribute.name." + (String)entry.getKey())
								)
						);
					} else if (d < 0.0) {
						f *= -1.0;
						list.add(
							Formatting.RED
								+ " "
								+ CommonI18n.translate(
									"attribute.modifier.take." + attributeModifier.getOperation(),
									MODIFIER_FORMAT.format(f),
									CommonI18n.translate("attribute.name." + (String)entry.getKey())
								)
						);
					}
				}
			}
		}

		if (this.hasNbt() && this.getNbt().getBoolean("Unbreakable") && (j & 4) == 0) {
			list.add(Formatting.BLUE + CommonI18n.translate("item.unbreakable"));
		}

		if (this.hasNbt() && this.nbt.contains("CanDestroy", 9) && (j & 8) == 0) {
			NbtList nbtList3 = this.nbt.getList("CanDestroy", 8);
			if (!nbtList3.isEmpty()) {
				list.add("");
				list.add(Formatting.GRAY + CommonI18n.translate("item.canBreak"));

				for (int q = 0; q < nbtList3.size(); q++) {
					Block block = Block.get(nbtList3.getString(q));
					if (block != null) {
						list.add(Formatting.DARK_GRAY + block.getTranslatedName());
					} else {
						list.add(Formatting.DARK_GRAY + "missingno");
					}
				}
			}
		}

		if (this.hasNbt() && this.nbt.contains("CanPlaceOn", 9) && (j & 16) == 0) {
			NbtList nbtList4 = this.nbt.getList("CanPlaceOn", 8);
			if (!nbtList4.isEmpty()) {
				list.add("");
				list.add(Formatting.GRAY + CommonI18n.translate("item.canPlace"));

				for (int r = 0; r < nbtList4.size(); r++) {
					Block block2 = Block.get(nbtList4.getString(r));
					if (block2 != null) {
						list.add(Formatting.DARK_GRAY + block2.getTranslatedName());
					} else {
						list.add(Formatting.DARK_GRAY + "missingno");
					}
				}
			}
		}

		if (advanced) {
			if (this.isDamaged()) {
				list.add("Durability: " + (this.getMaxDamage() - this.getDamage()) + " / " + this.getMaxDamage());
			}

			list.add(Formatting.DARK_GRAY + Item.REGISTRY.getIdentifier(this.item).toString());
			if (this.hasNbt()) {
				list.add(Formatting.DARK_GRAY + "NBT: " + this.getNbt().getKeys().size() + " tag(s)");
			}
		}

		return list;
	}

	public boolean hasEnchantmentGlint() {
		return this.getItem().hasEnchantmentGlint(this);
	}

	public Rarity getRarity() {
		return this.getItem().getRarity(this);
	}

	public boolean isEnchantable() {
		return !this.getItem().isEnchantable(this) ? false : !this.hasEnchantments();
	}

	public void addEnchantment(Enchantment enchantment, int level) {
		if (this.nbt == null) {
			this.setNbt(new NbtCompound());
		}

		if (!this.nbt.contains("ench", 9)) {
			this.nbt.put("ench", new NbtList());
		}

		NbtList nbtList = this.nbt.getList("ench", 10);
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putShort("id", (short)Enchantment.getId(enchantment));
		nbtCompound.putShort("lvl", (short)((byte)level));
		nbtList.add(nbtCompound);
	}

	public boolean hasEnchantments() {
		return this.nbt != null && this.nbt.contains("ench", 9);
	}

	public void putSubNbt(String key, NbtElement nbt) {
		if (this.nbt == null) {
			this.setNbt(new NbtCompound());
		}

		this.nbt.put(key, nbt);
	}

	public boolean hasSubTypes() {
		return this.getItem().hasSubTypes();
	}

	public boolean isInItemFrame() {
		return this.itemFrame != null;
	}

	public void setInItemFrame(ItemFrameEntity itemFrame) {
		this.itemFrame = itemFrame;
	}

	@Nullable
	public ItemFrameEntity getItemFrame() {
		return this.itemFrame;
	}

	public int getRepairCost() {
		return this.hasNbt() && this.nbt.contains("RepairCost", 3) ? this.nbt.getInt("RepairCost") : 0;
	}

	public void setRepairCost(int cost) {
		if (!this.hasNbt()) {
			this.nbt = new NbtCompound();
		}

		this.nbt.putInt("RepairCost", cost);
	}

	public Multimap<String, AttributeModifier> getAttributes(EquipmentSlot slot) {
		Multimap<String, AttributeModifier> multimap;
		if (this.hasNbt() && this.nbt.contains("AttributeModifiers", 9)) {
			multimap = HashMultimap.create();
			NbtList nbtList = this.nbt.getList("AttributeModifiers", 10);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				AttributeModifier attributeModifier = EntityAttributes.fromNbt(nbtCompound);
				if (attributeModifier != null
					&& (!nbtCompound.contains("Slot", 8) || nbtCompound.getString("Slot").equals(slot.getName()))
					&& attributeModifier.getId().getLeastSignificantBits() != 0L
					&& attributeModifier.getId().getMostSignificantBits() != 0L) {
					multimap.put(nbtCompound.getString("AttributeName"), attributeModifier);
				}
			}
		} else {
			multimap = this.getItem().method_6326(slot);
		}

		return multimap;
	}

	public void setAttribute(String attributeName, AttributeModifier modifier, EquipmentSlot slot) {
		if (this.nbt == null) {
			this.nbt = new NbtCompound();
		}

		if (!this.nbt.contains("AttributeModifiers", 9)) {
			this.nbt.put("AttributeModifiers", new NbtList());
		}

		NbtList nbtList = this.nbt.getList("AttributeModifiers", 10);
		NbtCompound nbtCompound = EntityAttributes.toNbt(modifier);
		nbtCompound.putString("AttributeName", attributeName);
		if (slot != null) {
			nbtCompound.putString("Slot", slot.getName());
		}

		nbtList.add(nbtCompound);
	}

	@Deprecated
	public void setItem(Item item) {
		this.item = item;
	}

	public Text toHoverableText() {
		LiteralText literalText = new LiteralText(this.getCustomName());
		if (this.hasCustomName()) {
			literalText.getStyle().setItalic(true);
		}

		Text text = new LiteralText("[").append(literalText).append("]");
		if (this.item != null) {
			NbtCompound nbtCompound = this.toNbt(new NbtCompound());
			text.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new LiteralText(nbtCompound.toString())));
			text.getStyle().setFormatting(this.getRarity().formatting);
		}

		return text;
	}

	public boolean canDestroy(Block block) {
		if (block == this.lastDestroyedBlock) {
			return this.lastDestroyResult;
		} else {
			this.lastDestroyedBlock = block;
			if (this.hasNbt() && this.nbt.contains("CanDestroy", 9)) {
				NbtList nbtList = this.nbt.getList("CanDestroy", 8);

				for (int i = 0; i < nbtList.size(); i++) {
					Block block2 = Block.get(nbtList.getString(i));
					if (block2 == block) {
						this.lastDestroyResult = true;
						return true;
					}
				}
			}

			this.lastDestroyResult = false;
			return false;
		}
	}

	public boolean canPlaceOn(Block block) {
		if (block == this.lastPlacedOn) {
			return this.lastPlaceOnResult;
		} else {
			this.lastPlacedOn = block;
			if (this.hasNbt() && this.nbt.contains("CanPlaceOn", 9)) {
				NbtList nbtList = this.nbt.getList("CanPlaceOn", 8);

				for (int i = 0; i < nbtList.size(); i++) {
					Block block2 = Block.get(nbtList.getString(i));
					if (block2 == block) {
						this.lastPlaceOnResult = true;
						return true;
					}
				}
			}

			this.lastPlaceOnResult = false;
			return false;
		}
	}
}
