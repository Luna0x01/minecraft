package net.minecraft;

import com.mojang.authlib.GameProfile;
import java.io.PrintStream;
import java.util.Random;
import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FireworkRocketEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectStrings;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.potion.Potion;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.DyeColor;
import net.minecraft.util.PrintStreamLogger;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
	public static final PrintStream SYSOUT = System.out;
	private static boolean initialized = false;
	private static final Logger LOGGER = LogManager.getLogger();

	public static boolean isInitialized() {
		return initialized;
	}

	static void setupDispenserBehavior() {
		DispenserBlock.SPECIAL_ITEMS.put(Items.ARROW, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
				ArrowEntity arrowEntity = new ArrowEntity(world, pos.getX(), pos.getY(), pos.getZ());
				arrowEntity.pickupType = AbstractArrowEntity.PickupPermission.ALLOWED;
				return arrowEntity;
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.TIPPED_ARROW, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
				ArrowEntity arrowEntity = new ArrowEntity(world, pos.getX(), pos.getY(), pos.getZ());
				arrowEntity.initFromStack(stack);
				arrowEntity.pickupType = AbstractArrowEntity.PickupPermission.ALLOWED;
				return arrowEntity;
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.SPECTRAL_ARROW, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
				AbstractArrowEntity abstractArrowEntity = new SpectralArrowEntity(world, pos.getX(), pos.getY(), pos.getZ());
				abstractArrowEntity.pickupType = AbstractArrowEntity.PickupPermission.ALLOWED;
				return abstractArrowEntity;
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.EGG, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
				return new EggEntity(world, pos.getX(), pos.getY(), pos.getZ());
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.SNOWBALL, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
				return new SnowballEntity(world, pos.getX(), pos.getY(), pos.getZ());
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.EXPERIENCE_BOTTLE, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
				return new ExperienceBottleEntity(world, pos.getX(), pos.getY(), pos.getZ());
			}

			@Override
			protected float getVariation() {
				return super.getVariation() * 0.5F;
			}

			@Override
			protected float getForce() {
				return super.getForce() * 1.25F;
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.SPLASH_POTION, new DispenserBehavior() {
			@Override
			public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
				return (new ProjectileDispenserBehavior() {
					@Override
					protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
						return new PotionEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copy());
					}

					@Override
					protected float getVariation() {
						return super.getVariation() * 0.5F;
					}

					@Override
					protected float getForce() {
						return super.getForce() * 1.25F;
					}
				}).dispense(pointer, stack);
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.LINGERING_POTION, new DispenserBehavior() {
			@Override
			public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
				return (new ProjectileDispenserBehavior() {
					@Override
					protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
						return new PotionEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copy());
					}

					@Override
					protected float getVariation() {
						return super.getVariation() * 0.5F;
					}

					@Override
					protected float getForce() {
						return super.getForce() * 1.25F;
					}
				}).dispense(pointer, stack);
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.SPAWN_EGG, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = DispenserBlock.getDirection(pointer.getBlockStateData());
				double d = pointer.getX() + (double)direction.getOffsetX();
				double e = (double)((float)pointer.getBlockPos().getY() + 0.2F);
				double f = pointer.getZ() + (double)direction.getOffsetZ();
				Entity entity = SpawnEggItem.method_4628(pointer.getWorld(), SpawnEggItem.method_11407(stack), d, e, f);
				if (entity instanceof LivingEntity && stack.hasCustomName()) {
					entity.setCustomName(stack.getCustomName());
				}

				SpawnEggItem.method_11406(pointer.getWorld(), null, stack, entity);
				stack.split(1);
				return stack;
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.FIREWORKS, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = DispenserBlock.getDirection(pointer.getBlockStateData());
				double d = pointer.getX() + (double)direction.getOffsetX();
				double e = (double)((float)pointer.getBlockPos().getY() + 0.2F);
				double f = pointer.getZ() + (double)direction.getOffsetZ();
				FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(pointer.getWorld(), d, e, f, stack);
				pointer.getWorld().spawnEntity(fireworkRocketEntity);
				stack.split(1);
				return stack;
			}

			@Override
			protected void playSound(BlockPointer pointer) {
				pointer.getWorld().syncGlobalEvent(1004, pointer.getBlockPos(), 0);
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.FIRE_CHARGE, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = DispenserBlock.getDirection(pointer.getBlockStateData());
				Position position = DispenserBlock.getPosition(pointer);
				double d = position.getX() + (double)((float)direction.getOffsetX() * 0.3F);
				double e = position.getY() + (double)((float)direction.getOffsetY() * 0.3F);
				double f = position.getZ() + (double)((float)direction.getOffsetZ() * 0.3F);
				World world = pointer.getWorld();
				Random random = world.random;
				double g = random.nextGaussian() * 0.05 + (double)direction.getOffsetX();
				double h = random.nextGaussian() * 0.05 + (double)direction.getOffsetY();
				double i = random.nextGaussian() * 0.05 + (double)direction.getOffsetZ();
				world.spawnEntity(new SmallFireballEntity(world, d, e, f, g, h, i));
				stack.split(1);
				return stack;
			}

			@Override
			protected void playSound(BlockPointer pointer) {
				pointer.getWorld().syncGlobalEvent(1018, pointer.getBlockPos(), 0);
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.BOAT, new Bootstrap.BoatDispenserBehavior(BoatEntity.Type.OAK));
		DispenserBlock.SPECIAL_ITEMS.put(Items.SPRUCE_BOAT, new Bootstrap.BoatDispenserBehavior(BoatEntity.Type.SPRUCE));
		DispenserBlock.SPECIAL_ITEMS.put(Items.BIRCH_BOAT, new Bootstrap.BoatDispenserBehavior(BoatEntity.Type.BIRCH));
		DispenserBlock.SPECIAL_ITEMS.put(Items.JUNGLE_BOAT, new Bootstrap.BoatDispenserBehavior(BoatEntity.Type.JUNGLE));
		DispenserBlock.SPECIAL_ITEMS.put(Items.DARK_OAK_BOAT, new Bootstrap.BoatDispenserBehavior(BoatEntity.Type.DARK_OAK));
		DispenserBlock.SPECIAL_ITEMS.put(Items.ACACIA_BOAT, new Bootstrap.BoatDispenserBehavior(BoatEntity.Type.ACACIA));
		DispenserBehavior dispenserBehavior = new ItemDispenserBehavior() {
			private final ItemDispenserBehavior field_11739 = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				BucketItem bucketItem = (BucketItem)stack.getItem();
				BlockPos blockPos = pointer.getBlockPos().offset(DispenserBlock.getDirection(pointer.getBlockStateData()));
				if (bucketItem.method_11365(null, pointer.getWorld(), blockPos)) {
					stack.setItem(Items.BUCKET);
					stack.count = 1;
					return stack;
				} else {
					return this.field_11739.dispense(pointer, stack);
				}
			}
		};
		DispenserBlock.SPECIAL_ITEMS.put(Items.LAVA_BUCKET, dispenserBehavior);
		DispenserBlock.SPECIAL_ITEMS.put(Items.WATER_BUCKET, dispenserBehavior);
		DispenserBlock.SPECIAL_ITEMS.put(Items.BUCKET, new ItemDispenserBehavior() {
			private final ItemDispenserBehavior field_13839 = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(DispenserBlock.getDirection(pointer.getBlockStateData()));
				BlockState blockState = world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				Material material = blockState.getMaterial();
				Item item;
				if (Material.WATER.equals(material) && block instanceof AbstractFluidBlock && (Integer)blockState.get(AbstractFluidBlock.LEVEL) == 0) {
					item = Items.WATER_BUCKET;
				} else {
					if (!Material.LAVA.equals(material) || !(block instanceof AbstractFluidBlock) || (Integer)blockState.get(AbstractFluidBlock.LEVEL) != 0) {
						return super.dispenseSilently(pointer, stack);
					}

					item = Items.LAVA_BUCKET;
				}

				world.setAir(blockPos);
				if (--stack.count == 0) {
					stack.setItem(item);
					stack.count = 1;
				} else if (pointer.<DispenserBlockEntity>getBlockEntity().addToFirstFreeSlot(new ItemStack(item)) < 0) {
					this.field_13839.dispense(pointer, new ItemStack(item));
				}

				return stack;
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.FLINT_AND_STEEL, new ItemDispenserBehavior() {
			private boolean field_13840 = true;

			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(DispenserBlock.getDirection(pointer.getBlockStateData()));
				if (world.isAir(blockPos)) {
					world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
					if (stack.damage(1, world.random)) {
						stack.count = 0;
					}
				} else if (world.getBlockState(blockPos).getBlock() == Blocks.TNT) {
					Blocks.TNT.onBreakByPlayer(world, blockPos, Blocks.TNT.getDefaultState().with(TntBlock.EXPLODE, true));
					world.setAir(blockPos);
				} else {
					this.field_13840 = false;
				}

				return stack;
			}

			@Override
			protected void playSound(BlockPointer pointer) {
				if (this.field_13840) {
					pointer.getWorld().syncGlobalEvent(1000, pointer.getBlockPos(), 0);
				} else {
					pointer.getWorld().syncGlobalEvent(1001, pointer.getBlockPos(), 0);
				}
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.DYE, new ItemDispenserBehavior() {
			private boolean field_13841 = true;

			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				if (DyeColor.WHITE == DyeColor.getById(stack.getData())) {
					World world = pointer.getWorld();
					BlockPos blockPos = pointer.getBlockPos().offset(DispenserBlock.getDirection(pointer.getBlockStateData()));
					if (DyeItem.fertilize(stack, world, blockPos)) {
						if (!world.isClient) {
							world.syncGlobalEvent(2005, blockPos, 0);
						}
					} else {
						this.field_13841 = false;
					}

					return stack;
				} else {
					return super.dispenseSilently(pointer, stack);
				}
			}

			@Override
			protected void playSound(BlockPointer pointer) {
				if (this.field_13841) {
					pointer.getWorld().syncGlobalEvent(1000, pointer.getBlockPos(), 0);
				} else {
					pointer.getWorld().syncGlobalEvent(1001, pointer.getBlockPos(), 0);
				}
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Item.fromBlock(Blocks.TNT), new ItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(DispenserBlock.getDirection(pointer.getBlockStateData()));
				TntEntity tntEntity = new TntEntity(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, null);
				world.spawnEntity(tntEntity);
				world.playSound(null, tntEntity.x, tntEntity.y, tntEntity.z, Sounds.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
				stack.count--;
				return stack;
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Items.SKULL, new ItemDispenserBehavior() {
			private boolean field_13842 = true;

			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				Direction direction = DispenserBlock.getDirection(pointer.getBlockStateData());
				BlockPos blockPos = pointer.getBlockPos().offset(direction);
				SkullBlock skullBlock = Blocks.SKULL;
				if (world.isAir(blockPos) && skullBlock.canDispense(world, blockPos, stack)) {
					if (!world.isClient) {
						world.setBlockState(blockPos, skullBlock.getDefaultState().with(SkullBlock.FACING, Direction.UP), 3);
						BlockEntity blockEntity = world.getBlockEntity(blockPos);
						if (blockEntity instanceof SkullBlockEntity) {
							if (stack.getData() == 3) {
								GameProfile gameProfile = null;
								if (stack.hasNbt()) {
									NbtCompound nbtCompound = stack.getNbt();
									if (nbtCompound.contains("SkullOwner", 10)) {
										gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
									} else if (nbtCompound.contains("SkullOwner", 8)) {
										String string = nbtCompound.getString("SkullOwner");
										if (!ChatUtil.isEmpty(string)) {
											gameProfile = new GameProfile(null, string);
										}
									}
								}

								((SkullBlockEntity)blockEntity).setOwnerAndType(gameProfile);
							} else {
								((SkullBlockEntity)blockEntity).setSkullType(stack.getData());
							}

							((SkullBlockEntity)blockEntity).setRotation(direction.getOpposite().getHorizontal() * 4);
							Blocks.SKULL.trySpawnEntity(world, blockPos, (SkullBlockEntity)blockEntity);
						}

						stack.count--;
					}
				} else if (ArmorItem.method_11353(pointer, stack) == null) {
					this.field_13842 = false;
				}

				return stack;
			}

			@Override
			protected void playSound(BlockPointer pointer) {
				if (this.field_13842) {
					pointer.getWorld().syncGlobalEvent(1000, pointer.getBlockPos(), 0);
				} else {
					pointer.getWorld().syncGlobalEvent(1001, pointer.getBlockPos(), 0);
				}
			}
		});
		DispenserBlock.SPECIAL_ITEMS.put(Item.fromBlock(Blocks.PUMPKIN), new ItemDispenserBehavior() {
			private boolean field_13835 = true;

			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(DispenserBlock.getDirection(pointer.getBlockStateData()));
				PumpkinBlock pumpkinBlock = (PumpkinBlock)Blocks.PUMPKIN;
				if (world.isAir(blockPos) && pumpkinBlock.canDispense(world, blockPos)) {
					if (!world.isClient) {
						world.setBlockState(blockPos, pumpkinBlock.getDefaultState(), 3);
					}

					stack.count--;
				} else {
					ItemStack itemStack = ArmorItem.method_11353(pointer, stack);
					if (itemStack == null) {
						this.field_13835 = false;
					}
				}

				return stack;
			}

			@Override
			protected void playSound(BlockPointer pointer) {
				if (this.field_13835) {
					pointer.getWorld().syncGlobalEvent(1000, pointer.getBlockPos(), 0);
				} else {
					pointer.getWorld().syncGlobalEvent(1001, pointer.getBlockPos(), 0);
				}
			}
		});
	}

	public static void initialize() {
		if (!initialized) {
			initialized = true;
			if (LOGGER.isDebugEnabled()) {
				setOutputStreams();
			}

			Sound.register();
			Block.setup();
			FireBlock.registerDefaultFlammables();
			StatusEffect.register();
			Enchantment.register();
			Item.setup();
			Potion.register();
			StatusEffectStrings.method_11416();
			Stats.setup();
			Biome.register();
			setupDispenserBehavior();
		}
	}

	private static void setOutputStreams() {
		System.setErr(new PrintStreamLogger("STDERR", System.err));
		System.setOut(new PrintStreamLogger("STDOUT", SYSOUT));
	}

	public static void println(String str) {
		SYSOUT.println(str);
	}

	public static class BoatDispenserBehavior extends ItemDispenserBehavior {
		private final ItemDispenserBehavior field_13843 = new ItemDispenserBehavior();
		private final BoatEntity.Type field_13844;

		public BoatDispenserBehavior(BoatEntity.Type type) {
			this.field_13844 = type;
		}

		@Override
		public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			Direction direction = DispenserBlock.getDirection(pointer.getBlockStateData());
			World world = pointer.getWorld();
			double d = pointer.getX() + (double)((float)direction.getOffsetX() * 1.125F);
			double e = pointer.getY() + (double)((float)direction.getOffsetY() * 1.125F);
			double f = pointer.getZ() + (double)((float)direction.getOffsetZ() * 1.125F);
			BlockPos blockPos = pointer.getBlockPos().offset(direction);
			Material material = world.getBlockState(blockPos).getMaterial();
			double g;
			if (Material.WATER.equals(material)) {
				g = 1.0;
			} else {
				if (!Material.AIR.equals(material) || !Material.WATER.equals(world.getBlockState(blockPos.down()).getMaterial())) {
					return this.field_13843.dispense(pointer, stack);
				}

				g = 0.0;
			}

			BoatEntity boatEntity = new BoatEntity(world, d, e + g, f);
			boatEntity.setBoatType(this.field_13844);
			boatEntity.yaw = direction.getOpposite().method_12578();
			world.spawnEntity(boatEntity);
			stack.split(1);
			return stack;
		}

		@Override
		protected void playSound(BlockPointer pointer) {
			pointer.getWorld().syncGlobalEvent(1000, pointer.getBlockPos(), 0);
		}
	}
}
