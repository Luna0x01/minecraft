package net.minecraft;

import java.nio.ByteBuffer;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ChatOptionsScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

public class class_4110 {
	private final MinecraftClient field_19926;
	private boolean field_19927;
	private long field_19928 = -1L;
	private long field_19929 = -1L;
	private long field_19930 = -1L;
	private boolean field_19931;
	private final ByteBuffer field_19932 = ByteBuffer.allocateDirect(1024);

	public class_4110(MinecraftClient minecraftClient) {
		this.field_19926 = minecraftClient;
	}

	private void method_18188(String string, Object... objects) {
		this.field_19926
			.inGameHud
			.getChatHud()
			.addMessage(
				new LiteralText("")
					.append(new TranslatableText("debug.prefix").formatted(new Formatting[]{Formatting.YELLOW, Formatting.BOLD}))
					.append(" ")
					.append(new TranslatableText(string, objects))
			);
	}

	private void method_18196(String string, Object... objects) {
		this.field_19926
			.inGameHud
			.getChatHud()
			.addMessage(
				new LiteralText("")
					.append(new TranslatableText("debug.prefix").formatted(new Formatting[]{Formatting.RED, Formatting.BOLD}))
					.append(" ")
					.append(new TranslatableText(string, objects))
			);
	}

	private boolean method_18198(int i) {
		if (this.field_19928 > 0L && this.field_19928 < Util.method_20227() - 100L) {
			return true;
		} else {
			switch (i) {
				case 65:
					this.field_19926.worldRenderer.reload();
					this.method_18188("debug.reload_chunks.message");
					return true;
				case 66:
					boolean bl = !this.field_19926.getEntityRenderManager().getRenderHitboxes();
					this.field_19926.getEntityRenderManager().setRenderHitboxes(bl);
					this.method_18188(bl ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
					return true;
				case 67:
					if (this.field_19926.player.getReducedDebugInfo()) {
						return false;
					}

					this.method_18188("debug.copy_location.message");
					this.method_18187(
						String.format(
							Locale.ROOT,
							"/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f",
							DimensionType.method_17196(this.field_19926.player.world.dimension.method_11789()),
							this.field_19926.player.x,
							this.field_19926.player.y,
							this.field_19926.player.z,
							this.field_19926.player.yaw,
							this.field_19926.player.pitch
						)
					);
					return true;
				case 68:
					if (this.field_19926.inGameHud != null) {
						this.field_19926.inGameHud.getChatHud().clear(false);
					}

					return true;
				case 69:
				case 74:
				case 75:
				case 76:
				case 77:
				case 79:
				case 82:
				case 83:
				default:
					return false;
				case 70:
					this.field_19926.options.method_18258(GameOptions.Option.RENDER_DISTANCE, Screen.hasShiftDown() ? -1 : 1);
					this.method_18188("debug.cycle_renderdistance.message", this.field_19926.options.viewDistance);
					return true;
				case 71:
					boolean bl2 = this.field_19926.debugRenderer.toggleChunkBorders();
					this.method_18188(bl2 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
					return true;
				case 72:
					this.field_19926.options.field_19992 = !this.field_19926.options.field_19992;
					this.method_18188(this.field_19926.options.field_19992 ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
					this.field_19926.options.save();
					return true;
				case 73:
					if (!this.field_19926.player.getReducedDebugInfo()) {
						this.method_18192(this.field_19926.player.method_15592(2), !Screen.hasShiftDown());
					}

					return true;
				case 78:
					if (!this.field_19926.player.method_15592(2)) {
						this.method_18188("debug.creative_spectator.error");
					} else if (this.field_19926.player.isCreative()) {
						this.field_19926.player.sendChatMessage("/gamemode spectator");
					} else if (this.field_19926.player.isSpectator()) {
						this.field_19926.player.sendChatMessage("/gamemode creative");
					}

					return true;
				case 80:
					this.field_19926.options.field_19973 = !this.field_19926.options.field_19973;
					this.field_19926.options.save();
					this.method_18188(this.field_19926.options.field_19973 ? "debug.pause_focus.on" : "debug.pause_focus.off");
					return true;
				case 81:
					this.method_18188("debug.help.message");
					ChatHud chatHud = this.field_19926.inGameHud.getChatHud();
					chatHud.addMessage(new TranslatableText("debug.reload_chunks.help"));
					chatHud.addMessage(new TranslatableText("debug.show_hitboxes.help"));
					chatHud.addMessage(new TranslatableText("debug.copy_location.help"));
					chatHud.addMessage(new TranslatableText("debug.clear_chat.help"));
					chatHud.addMessage(new TranslatableText("debug.cycle_renderdistance.help"));
					chatHud.addMessage(new TranslatableText("debug.chunk_boundaries.help"));
					chatHud.addMessage(new TranslatableText("debug.advanced_tooltips.help"));
					chatHud.addMessage(new TranslatableText("debug.inspect.help"));
					chatHud.addMessage(new TranslatableText("debug.creative_spectator.help"));
					chatHud.addMessage(new TranslatableText("debug.pause_focus.help"));
					chatHud.addMessage(new TranslatableText("debug.help.help"));
					chatHud.addMessage(new TranslatableText("debug.reload_resourcepacks.help"));
					return true;
				case 84:
					this.method_18188("debug.reload_resourcepacks.message");
					this.field_19926.reloadResources();
					return true;
			}
		}
	}

	private void method_18192(boolean bl, boolean bl2) {
		if (this.field_19926.result != null) {
			switch (this.field_19926.result.type) {
				case BLOCK:
					BlockPos blockPos = this.field_19926.result.getBlockPos();
					BlockState blockState = this.field_19926.player.world.getBlockState(blockPos);
					if (bl) {
						if (bl2) {
							this.field_19926.player.networkHandler.method_18966().method_18151(blockPos, nbtCompound -> {
								this.method_18183(blockState, blockPos, nbtCompound);
								this.method_18188("debug.inspect.server.block");
							});
						} else {
							BlockEntity blockEntity = this.field_19926.player.world.getBlockEntity(blockPos);
							NbtCompound nbtCompound = blockEntity != null ? blockEntity.toNbt(new NbtCompound()) : null;
							this.method_18183(blockState, blockPos, nbtCompound);
							this.method_18188("debug.inspect.client.block");
						}
					} else {
						this.method_18183(blockState, blockPos, null);
						this.method_18188("debug.inspect.client.block");
					}
					break;
				case ENTITY:
					Entity entity = this.field_19926.result.entity;
					if (entity == null) {
						return;
					}

					Identifier identifier = Registry.ENTITY_TYPE.getId(entity.method_15557());
					Vec3d vec3d = new Vec3d(entity.x, entity.y, entity.z);
					if (bl) {
						if (bl2) {
							this.field_19926.player.networkHandler.method_18966().method_18150(entity.getEntityId(), nbtCompound -> {
								this.method_18190(identifier, vec3d, nbtCompound);
								this.method_18188("debug.inspect.server.entity");
							});
						} else {
							NbtCompound nbtCompound2 = entity.toNbt(new NbtCompound());
							this.method_18190(identifier, vec3d, nbtCompound2);
							this.method_18188("debug.inspect.client.entity");
						}
					} else {
						this.method_18190(identifier, vec3d, null);
						this.method_18188("debug.inspect.client.entity");
					}
			}
		}
	}

	private void method_18183(BlockState blockState, BlockPos blockPos, @Nullable NbtCompound nbtCompound) {
		if (nbtCompound != null) {
			nbtCompound.remove("x");
			nbtCompound.remove("y");
			nbtCompound.remove("z");
			nbtCompound.remove("id");
		}

		String string = class_4238.method_19289(blockState, nbtCompound);
		String string2 = String.format(Locale.ROOT, "/setblock %d %d %d %s", blockPos.getX(), blockPos.getY(), blockPos.getZ(), string);
		this.method_18187(string2);
	}

	private void method_18190(Identifier identifier, Vec3d vec3d, @Nullable NbtCompound nbtCompound) {
		String string2;
		if (nbtCompound != null) {
			nbtCompound.remove("UUIDMost");
			nbtCompound.remove("UUIDLeast");
			nbtCompound.remove("Pos");
			nbtCompound.remove("Dimension");
			String string = nbtCompound.asText().getString();
			string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", identifier.toString(), vec3d.x, vec3d.y, vec3d.z, string);
		} else {
			string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", identifier.toString(), vec3d.x, vec3d.y, vec3d.z);
		}

		this.method_18187(string2);
	}

	public void method_18182(long l, int i, int j, int k, int m) {
		if (l == this.field_19926.field_19944.method_18315()) {
			if (this.field_19928 > 0L) {
				if (!class_4107.method_18154(67) || !class_4107.method_18154(292)) {
					this.field_19928 = -1L;
				}
			} else if (class_4107.method_18154(67) && class_4107.method_18154(292)) {
				this.field_19931 = true;
				this.field_19928 = Util.method_20227();
				this.field_19929 = Util.method_20227();
				this.field_19930 = 0L;
			}

			class_4122 lv = this.field_19926.currentScreen;
			if (k == 1 && (!(this.field_19926.currentScreen instanceof ControlsOptionsScreen) || ((ControlsOptionsScreen)lv).time <= Util.method_20227() - 20L)) {
				if (this.field_19926.options.fullscreenKey.method_18166(i, j)) {
					this.field_19926.field_19944.method_18313();
					return;
				}

				if (this.field_19926.options.screenshotKey.method_18166(i, j)) {
					if (Screen.hasControlDown()) {
					}

					ScreenshotUtils.method_18271(
						this.field_19926.runDirectory,
						this.field_19926.field_19944.method_18317(),
						this.field_19926.field_19944.method_18318(),
						this.field_19926.getFramebuffer(),
						text -> this.field_19926.submit(() -> this.field_19926.inGameHud.getChatHud().addMessage(text))
					);
					return;
				}
			}

			if (lv != null) {
				boolean[] bls = new boolean[]{false};
				Screen.method_18605(() -> {
					if (k != 1 && (k != 2 || !this.field_19927)) {
						if (k == 0) {
							bls[0] = lv.keyReleased(i, j, m);
						}
					} else {
						bls[0] = lv.keyPressed(i, j, m);
					}
				}, "keyPressed event handler", lv.getClass().getCanonicalName());
				if (bls[0]) {
					return;
				}
			}

			if (this.field_19926.currentScreen == null || this.field_19926.currentScreen.passEvents) {
				class_4107.class_4108 lv2 = class_4107.method_18155(i, j);
				if (k == 0) {
					KeyBinding.method_18168(lv2, false);
					if (i == 292) {
						if (this.field_19931) {
							this.field_19931 = false;
						} else {
							this.field_19926.options.debugEnabled = !this.field_19926.options.debugEnabled;
							this.field_19926.options.field_19982 = this.field_19926.options.debugEnabled && Screen.hasShiftDown();
							this.field_19926.options.field_19983 = this.field_19926.options.debugEnabled && Screen.hasAltDown();
						}
					}
				} else {
					if (i == 66 && Screen.hasControlDown()) {
						this.field_19926.options.method_18258(GameOptions.Option.NARRATOR, 1);
						if (lv instanceof ChatOptionsScreen) {
							((ChatOptionsScreen)lv).method_14501();
						}
					}

					if (i == 293 && this.field_19926.field_3818 != null) {
						this.field_19926.field_3818.method_19076();
					}

					boolean bl = false;
					if (this.field_19926.currentScreen == null) {
						if (i == 256) {
							this.field_19926.openGameMenuScreen();
						}

						bl = class_4107.method_18154(292) && this.method_18198(i);
						this.field_19931 |= bl;
						if (i == 290) {
							this.field_19926.options.field_19987 = !this.field_19926.options.field_19987;
						}
					}

					if (bl) {
						KeyBinding.method_18168(lv2, false);
					} else {
						KeyBinding.method_18168(lv2, true);
						KeyBinding.method_18167(lv2);
					}

					if (this.field_19926.options.field_19982) {
						if (i == 48) {
							this.field_19926.handleProfilerKeyPress(0);
						}

						for (int n = 0; n < 9; n++) {
							if (i == 49 + n) {
								this.field_19926.handleProfilerKeyPress(n + 1);
							}
						}
					}
				}
			}
		}
	}

	private void method_18181(long l, int i, int j) {
		if (l == this.field_19926.field_19944.method_18315()) {
			class_4122 lv = this.field_19926.currentScreen;
			if (lv != null) {
				if (Character.charCount(i) == 1) {
					Screen.method_18605(() -> lv.charTyped((char)i, j), "charTyped event handler", lv.getClass().getCanonicalName());
				} else {
					for (char c : Character.toChars(i)) {
						Screen.method_18605(() -> lv.charTyped(c, j), "charTyped event handler", lv.getClass().getCanonicalName());
					}
				}
			}
		}
	}

	public void method_18191(boolean bl) {
		this.field_19927 = bl;
	}

	public void method_18180(long l) {
		GLFW.glfwSetKeyCallback(l, this::method_18182);
		GLFW.glfwSetCharModsCallback(l, this::method_18181);
	}

	public String method_18177() {
		GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((i, l) -> {
			if (i != 65545) {
				this.field_19926.field_19944.method_18295(i, l);
			}
		});
		String string = GLFW.glfwGetClipboardString(this.field_19926.field_19944.method_18315());
		GLFW.glfwSetErrorCallback(gLFWErrorCallback).free();
		return string == null ? "" : string;
	}

	private void method_18189(ByteBuffer byteBuffer, String string) {
		MemoryUtil.memUTF8(string, true, byteBuffer);
		GLFW.glfwSetClipboardString(this.field_19926.field_19944.method_18315(), byteBuffer);
	}

	public void method_18187(String string) {
		int i = MemoryUtil.memLengthUTF8(string, true);
		if (i < this.field_19932.capacity()) {
			this.method_18189(this.field_19932, string);
			this.field_19932.clear();
		} else {
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(i);
			this.method_18189(byteBuffer, string);
		}
	}

	public void method_18193() {
		if (this.field_19928 > 0L) {
			long l = Util.method_20227();
			long m = 10000L - (l - this.field_19928);
			long n = l - this.field_19929;
			if (m < 0L) {
				if (Screen.hasControlDown()) {
					MemoryUtil.memSet(0L, 0, 1L);
				}

				throw new CrashException(new CrashReport("Manually triggered debug crash", new Throwable()));
			}

			if (n >= 1000L) {
				if (this.field_19930 == 0L) {
					this.method_18188("debug.crash.message");
				} else {
					this.method_18196("debug.crash.warning", MathHelper.ceil((float)m / 1000.0F));
				}

				this.field_19929 = l;
				this.field_19930++;
			}
		}
	}
}
