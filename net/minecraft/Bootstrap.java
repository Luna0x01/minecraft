package net.minecraft;

import java.io.PrintStream;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FireworkRocketEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.painting.Painting;
import net.minecraft.entity.effect.StatusEffect;
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
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.sound.Sound;
import net.minecraft.sound.Sounds;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Language;
import net.minecraft.util.PrintStreamLogger;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeSourceType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
	public static final PrintStream SYSOUT = System.out;
	private static boolean initialized;
	private static final Logger LOGGER = LogManager.getLogger();

	public static boolean isInitialized() {
		return initialized;
	}

	static void setupDispenserBehavior() {
		DispenserBlock.method_16665(Items.ARROW, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
				ArrowEntity arrowEntity = new ArrowEntity(world, pos.getX(), pos.getY(), pos.getZ());
				arrowEntity.pickupType = AbstractArrowEntity.PickupPermission.ALLOWED;
				return arrowEntity;
			}
		});
		DispenserBlock.method_16665(Items.TIPPED_ARROW, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
				ArrowEntity arrowEntity = new ArrowEntity(world, pos.getX(), pos.getY(), pos.getZ());
				arrowEntity.initFromStack(stack);
				arrowEntity.pickupType = AbstractArrowEntity.PickupPermission.ALLOWED;
				return arrowEntity;
			}
		});
		DispenserBlock.method_16665(Items.SPECTRAL_ARROW, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
				AbstractArrowEntity abstractArrowEntity = new SpectralArrowEntity(world, pos.getX(), pos.getY(), pos.getZ());
				abstractArrowEntity.pickupType = AbstractArrowEntity.PickupPermission.ALLOWED;
				return abstractArrowEntity;
			}
		});
		DispenserBlock.method_16665(Items.EGG, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
				return new EggEntity(world, pos.getX(), pos.getY(), pos.getZ());
			}
		});
		DispenserBlock.method_16665(Items.SNOWBALL, new ProjectileDispenserBehavior() {
			@Override
			protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
				return new SnowballEntity(world, pos.getX(), pos.getY(), pos.getZ());
			}
		});
		DispenserBlock.method_16665(Items.EXPERIENCE_BOTTLE, new ProjectileDispenserBehavior() {
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
		DispenserBlock.method_16665(Items.SPLASH_POTION, new DispenserBehavior() {
			@Override
			public ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
				return (new ProjectileDispenserBehavior() {
					@Override
					protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
						return new PotionEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack.copy());
					}

					@Override
					protected float getVariation() {
						return super.getVariation() * 0.5F;
					}

					@Override
					protected float getForce() {
						return super.getForce() * 1.25F;
					}
				}).dispense(blockPointer, itemStack);
			}
		});
		DispenserBlock.method_16665(Items.LINGERING_POTION, new DispenserBehavior() {
			@Override
			public ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack) {
				return (new ProjectileDispenserBehavior() {
					@Override
					protected Projectile createProjectile(World world, Position pos, ItemStack stack) {
						return new PotionEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack.copy());
					}

					@Override
					protected float getVariation() {
						return super.getVariation() * 0.5F;
					}

					@Override
					protected float getForce() {
						return super.getForce() * 1.25F;
					}
				}).dispense(blockPointer, itemStack);
			}
		});
		ItemDispenserBehavior itemDispenserBehavior = new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.getBlockState().getProperty(DispenserBlock.FACING);
				EntityType<?> entityType = ((class_3558)stack.getItem()).method_16128(stack.getNbt());
				if (entityType != null) {
					entityType.method_15619(pointer.getWorld(), stack, null, pointer.getBlockPos().offset(direction), direction != Direction.UP, false);
				}

				stack.decrement(1);
				return stack;
			}
		};

		for (class_3558 lv : class_3558.method_16129()) {
			DispenserBlock.method_16665(lv, itemDispenserBehavior);
		}

		DispenserBlock.method_16665(Items.FIREWORK_ROCKET, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.getBlockState().getProperty(DispenserBlock.FACING);
				double d = pointer.getX() + (double)direction.getOffsetX();
				double e = (double)((float)pointer.getBlockPos().getY() + 0.2F);
				double f = pointer.getZ() + (double)direction.getOffsetZ();
				FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(pointer.getWorld(), d, e, f, stack);
				pointer.getWorld().method_3686(fireworkRocketEntity);
				stack.decrement(1);
				return stack;
			}

			@Override
			protected void playSound(BlockPointer pointer) {
				pointer.getWorld().syncGlobalEvent(1004, pointer.getBlockPos(), 0);
			}
		});
		DispenserBlock.method_16665(Items.FIRE_CHARGE, new ItemDispenserBehavior() {
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = pointer.getBlockState().getProperty(DispenserBlock.FACING);
				Position position = DispenserBlock.getPosition(pointer);
				double d = position.getX() + (double)((float)direction.getOffsetX() * 0.3F);
				double e = position.getY() + (double)((float)direction.getOffsetY() * 0.3F);
				double f = position.getZ() + (double)((float)direction.getOffsetZ() * 0.3F);
				World world = pointer.getWorld();
				Random random = world.random;
				double g = random.nextGaussian() * 0.05 + (double)direction.getOffsetX();
				double h = random.nextGaussian() * 0.05 + (double)direction.getOffsetY();
				double i = random.nextGaussian() * 0.05 + (double)direction.getOffsetZ();
				world.method_3686(new SmallFireballEntity(world, d, e, f, g, h, i));
				stack.decrement(1);
				return stack;
			}

			@Override
			protected void playSound(BlockPointer pointer) {
				pointer.getWorld().syncGlobalEvent(1018, pointer.getBlockPos(), 0);
			}
		});
		DispenserBlock.method_16665(Items.BOAT, new Bootstrap.BoatDispenserBehavior(BoatEntity.Type.OAK));
		DispenserBlock.method_16665(Items.SPRUCE_BOAT, new Bootstrap.BoatDispenserBehavior(BoatEntity.Type.SPRUCE));
		DispenserBlock.method_16665(Items.BIRCH_BOAT, new Bootstrap.BoatDispenserBehavior(BoatEntity.Type.BIRCH));
		DispenserBlock.method_16665(Items.JUNGLE_BOAT, new Bootstrap.BoatDispenserBehavior(BoatEntity.Type.JUNGLE));
		DispenserBlock.method_16665(Items.DARK_OAK_BOAT, new Bootstrap.BoatDispenserBehavior(BoatEntity.Type.DARK_OAK));
		DispenserBlock.method_16665(Items.ACACIA_BOAT, new Bootstrap.BoatDispenserBehavior(BoatEntity.Type.ACACIA));
		DispenserBehavior dispenserBehavior = new ItemDispenserBehavior() {
			private final ItemDispenserBehavior field_11739 = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				BucketItem bucketItem = (BucketItem)stack.getItem();
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().getProperty(DispenserBlock.FACING));
				World world = pointer.getWorld();
				if (bucketItem.method_16028(null, world, blockPos, null)) {
					bucketItem.method_16031(world, stack, blockPos);
					return new ItemStack(Items.BUCKET);
				} else {
					return this.field_11739.dispense(pointer, stack);
				}
			}
		};
		DispenserBlock.method_16665(Items.LAVA_BUCKET, dispenserBehavior);
		DispenserBlock.method_16665(Items.WATER_BUCKET, dispenserBehavior);
		DispenserBlock.method_16665(Items.SALMON_BUCKET, dispenserBehavior);
		DispenserBlock.method_16665(Items.COD_BUCKET, dispenserBehavior);
		DispenserBlock.method_16665(Items.PUFFERFISH_BUCKET, dispenserBehavior);
		DispenserBlock.method_16665(Items.TROPICAL_FISH_BUCKET, dispenserBehavior);
		DispenserBlock.method_16665(Items.BUCKET, new ItemDispenserBehavior() {
			private final ItemDispenserBehavior field_13839 = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				IWorld iWorld = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().getProperty(DispenserBlock.FACING));
				BlockState blockState = iWorld.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block instanceof FluidDrainable) {
					Fluid fluid = ((FluidDrainable)block).tryDrainFluid(iWorld, blockPos, blockState);
					if (!(fluid instanceof FlowableFluid)) {
						return super.dispenseSilently(pointer, stack);
					} else {
						Item item = fluid.method_17787();
						stack.decrement(1);
						if (stack.isEmpty()) {
							return new ItemStack(item);
						} else {
							if (pointer.<DispenserBlockEntity>getBlockEntity().addToFirstFreeSlot(new ItemStack(item)) < 0) {
								this.field_13839.dispense(pointer, new ItemStack(item));
							}

							return stack;
						}
					}
				} else {
					return super.dispenseSilently(pointer, stack);
				}
			}
		});
		DispenserBlock.method_16665(Items.FLINT_AND_STEEL, new Bootstrap.class_3115() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				this.field_15356 = true;
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().getProperty(DispenserBlock.FACING));
				if (FlintAndSteelItem.method_16064(world, blockPos)) {
					world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
				} else {
					Block block = world.getBlockState(blockPos).getBlock();
					if (block instanceof TntBlock) {
						((TntBlock)block).method_16751(world, blockPos);
						world.method_8553(blockPos);
					} else {
						this.field_15356 = false;
					}
				}

				if (this.field_15356 && stack.damage(1, world.random, null)) {
					stack.setCount(0);
				}

				return stack;
			}
		});
		DispenserBlock.method_16665(Items.BONE_MEAL, new Bootstrap.class_3115() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				this.field_15356 = true;
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().getProperty(DispenserBlock.FACING));
				if (!class_3545.method_16022(stack, world, blockPos) && !class_3545.method_16023(stack, world, blockPos, null)) {
					this.field_15356 = false;
				} else if (!world.isClient) {
					world.syncGlobalEvent(2005, blockPos, 0);
				}

				return stack;
			}
		});
		DispenserBlock.method_16665(Blocks.TNT, new ItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().getProperty(DispenserBlock.FACING));
				TntEntity tntEntity = new TntEntity(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, null);
				world.method_3686(tntEntity);
				world.playSound(null, tntEntity.x, tntEntity.y, tntEntity.z, Sounds.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
				stack.decrement(1);
				return stack;
			}
		});
		Bootstrap.class_3115 lv2 = new Bootstrap.class_3115() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				this.field_15356 = !ArmorItem.method_11353(pointer, stack).isEmpty();
				return stack;
			}
		};
		DispenserBlock.method_16665(Items.CREEPER_HEAD, lv2);
		DispenserBlock.method_16665(Items.ZOMBIE_HEAD, lv2);
		DispenserBlock.method_16665(Items.DRAGON_HEAD, lv2);
		DispenserBlock.method_16665(Items.SKELETON_SKULL, lv2);
		DispenserBlock.method_16665(Items.PLAYER_HEAD, lv2);
		DispenserBlock.method_16665(
			Items.WITHER_SKELETON_SKULL,
			new Bootstrap.class_3115() {
				@Override
				protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
					World world = pointer.getWorld();
					Direction direction = pointer.getBlockState().getProperty(DispenserBlock.FACING);
					BlockPos blockPos = pointer.getBlockPos().offset(direction);
					this.field_15356 = true;
					if (world.method_8579(blockPos) && class_3737.method_16770(world, blockPos, stack)) {
						world.setBlockState(
							blockPos,
							Blocks.WITHER_SKELETON_SKULL
								.getDefaultState()
								.withProperty(SkullBlock.field_18477, Integer.valueOf(direction.getAxis() == Direction.Axis.Y ? 0 : direction.getOpposite().getHorizontal() * 4)),
							3
						);
						BlockEntity blockEntity = world.getBlockEntity(blockPos);
						if (blockEntity instanceof SkullBlockEntity) {
							class_3737.method_16769(world, blockPos, (SkullBlockEntity)blockEntity);
						}

						stack.decrement(1);
					} else if (ArmorItem.method_11353(pointer, stack).isEmpty()) {
						this.field_15356 = false;
					}

					return stack;
				}
			}
		);
		DispenserBlock.method_16665(Blocks.CARVED_PUMPKIN, new Bootstrap.class_3115() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.getWorld();
				BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().getProperty(DispenserBlock.FACING));
				class_3697 lv = (class_3697)Blocks.CARVED_PUMPKIN;
				this.field_15356 = true;
				if (world.method_8579(blockPos) && lv.method_16639(world, blockPos)) {
					if (!world.isClient) {
						world.setBlockState(blockPos, lv.getDefaultState(), 3);
					}

					stack.decrement(1);
				} else {
					ItemStack itemStack = ArmorItem.method_11353(pointer, stack);
					if (itemStack.isEmpty()) {
						this.field_15356 = false;
					}
				}

				return stack;
			}
		});
		DispenserBlock.method_16665(Blocks.SHULKER_BOX.getItem(), new Bootstrap.class_3116());

		for (DyeColor dyeColor : DyeColor.values()) {
			DispenserBlock.method_16665(ShulkerBoxBlock.of(dyeColor).getItem(), new Bootstrap.class_3116());
		}
	}

	public static void initialize() {
		if (!initialized) {
			initialized = true;
			Sound.register();
			Fluid.method_17799();
			Block.setup();
			FireBlock.registerDefaultFlammables();
			StatusEffect.register();
			Enchantment.register();
			if (EntityType.getId(EntityType.PLAYER) == null) {
				throw new IllegalStateException("Failed loading EntityTypes");
			} else {
				Item.setup();
				Potion.register();
				class_3566.method_16155();
				Biome.register();
				class_4319.method_19840();
				ParticleType.method_19985();
				setupDispenserBehavior();
				class_4323.method_19892();
				BiomeSourceType.method_16483();
				BlockEntityType.method_16784();
				ChunkGeneratorType.method_17038();
				DimensionType.method_17194();
				Painting.method_15838();
				Stats.method_21431();
				Registry.validate();
				if (SharedConstants.isDevelopment) {
					method_20447("block", Registry.BLOCK, Block::getTranslationKey);
					method_20447("biome", Registry.BIOME, Biome::getTranslationKey);
					method_20447("enchantment", Registry.ENCHANTMENT, Enchantment::getTranslationKey);
					method_20447("item", Registry.ITEM, Item::getTranslationKey);
					method_20447("effect", Registry.MOB_EFFECT, StatusEffect::getTranslationKey);
					method_20447("entity", Registry.ENTITY_TYPE, EntityType::getTranslationKey);
				}

				setOutputStreams();
			}
		}
	}

	private static <T> void method_20447(String string, Registry<T> registry, Function<T, String> function) {
		Language language = Language.getInstance();
		registry.iterator().forEachRemaining(object -> {
			String string2 = (String)function.apply(object);
			if (!language.hasTranslation(string2)) {
				LOGGER.warn("Missing translation for {}: {} (key: '{}')", string, registry.getId((T)object), string2);
			}
		});
	}

	private static void setOutputStreams() {
		if (LOGGER.isDebugEnabled()) {
			System.setErr(new class_3117("STDERR", System.err));
			System.setOut(new class_3117("STDOUT", SYSOUT));
		} else {
			System.setErr(new PrintStreamLogger("STDERR", System.err));
			System.setOut(new PrintStreamLogger("STDOUT", SYSOUT));
		}
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
			Direction direction = pointer.getBlockState().getProperty(DispenserBlock.FACING);
			World world = pointer.getWorld();
			double d = pointer.getX() + (double)((float)direction.getOffsetX() * 1.125F);
			double e = pointer.getY() + (double)((float)direction.getOffsetY() * 1.125F);
			double f = pointer.getZ() + (double)((float)direction.getOffsetZ() * 1.125F);
			BlockPos blockPos = pointer.getBlockPos().offset(direction);
			double g;
			if (world.getFluidState(blockPos).matches(FluidTags.WATER)) {
				g = 1.0;
			} else {
				if (!world.getBlockState(blockPos).isAir() || !world.getFluidState(blockPos.down()).matches(FluidTags.WATER)) {
					return this.field_13843.dispense(pointer, stack);
				}

				g = 0.0;
			}

			BoatEntity boatEntity = new BoatEntity(world, d, e + g, f);
			boatEntity.setBoatType(this.field_13844);
			boatEntity.yaw = direction.method_12578();
			world.method_3686(boatEntity);
			stack.decrement(1);
			return stack;
		}

		@Override
		protected void playSound(BlockPointer pointer) {
			pointer.getWorld().syncGlobalEvent(1000, pointer.getBlockPos(), 0);
		}
	}

	public abstract static class class_3115 extends ItemDispenserBehavior {
		protected boolean field_15356 = true;

		@Override
		protected void playSound(BlockPointer pointer) {
			pointer.getWorld().syncGlobalEvent(this.field_15356 ? 1000 : 1001, pointer.getBlockPos(), 0);
		}
	}

	static class class_3116 extends Bootstrap.class_3115 {
		private class_3116() {
		}

		@Override
		protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			this.field_15356 = false;
			Item item = stack.getItem();
			if (item instanceof BlockItem) {
				Direction direction = pointer.getBlockState().getProperty(DispenserBlock.FACING);
				BlockPos blockPos = pointer.getBlockPos().offset(direction);
				Direction direction2 = pointer.getWorld().method_8579(blockPos.down()) ? direction : Direction.UP;
				this.field_15356 = ((BlockItem)item).method_16012(new Bootstrap.class_4400(pointer.getWorld(), blockPos, direction, stack, direction2))
					== ActionResult.SUCCESS;
				if (this.field_15356) {
					stack.decrement(1);
				}
			}

			return stack;
		}
	}

	static class class_4400 extends ItemPlacementContext {
		private final Direction field_21662;

		public class_4400(World world, BlockPos blockPos, Direction direction, ItemStack itemStack, Direction direction2) {
			super(world, null, itemStack, blockPos, direction2, 0.5F, 0.0F, 0.5F);
			this.field_21662 = direction;
		}

		@Override
		public BlockPos getBlockPos() {
			return this.field_17407;
		}

		@Override
		public boolean method_16018() {
			return this.field_17405.getBlockState(this.field_17407).canReplace(this);
		}

		@Override
		public boolean method_16019() {
			return this.method_16018();
		}

		@Override
		public Direction method_16020() {
			return Direction.DOWN;
		}

		@Override
		public Direction[] method_16021() {
			switch (this.field_21662) {
				case DOWN:
				default:
					return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
				case UP:
					return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
				case NORTH:
					return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
				case SOUTH:
					return new Direction[]{Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
				case WEST:
					return new Direction[]{Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST};
				case EAST:
					return new Direction[]{Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
			}
		}

		@Override
		public Direction method_16145() {
			return this.field_21662.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.field_21662;
		}

		@Override
		public boolean method_16146() {
			return false;
		}

		@Override
		public float method_16147() {
			return (float)(this.field_21662.getHorizontal() * 90);
		}
	}
}
