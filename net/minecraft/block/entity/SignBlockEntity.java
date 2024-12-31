package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SignBlockEntity extends BlockEntity {
	public final Text[] text = new Text[]{new LiteralText(""), new LiteralText(""), new LiteralText(""), new LiteralText("")};
	public int lineBeingEdited = -1;
	private boolean editable = true;
	private PlayerEntity editor;
	private final CommandStats commandStats = new CommandStats();

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);

		for (int i = 0; i < 4; i++) {
			String string = Text.Serializer.serialize(this.text[i]);
			nbt.putString("Text" + (i + 1), string);
		}

		this.commandStats.toNbt(nbt);
		return nbt;
	}

	@Override
	protected void method_13323(World world) {
		this.setWorld(world);
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		this.editable = false;
		super.fromNbt(nbt);
		CommandSource commandSource = new CommandSource() {
			@Override
			public String getTranslationKey() {
				return "Sign";
			}

			@Override
			public Text getName() {
				return new LiteralText(this.getTranslationKey());
			}

			@Override
			public void sendMessage(Text text) {
			}

			@Override
			public boolean canUseCommand(int permissionLevel, String commandLiteral) {
				return true;
			}

			@Override
			public BlockPos getBlockPos() {
				return SignBlockEntity.this.pos;
			}

			@Override
			public Vec3d getPos() {
				return new Vec3d(
					(double)SignBlockEntity.this.pos.getX() + 0.5, (double)SignBlockEntity.this.pos.getY() + 0.5, (double)SignBlockEntity.this.pos.getZ() + 0.5
				);
			}

			@Override
			public World getWorld() {
				return SignBlockEntity.this.world;
			}

			@Override
			public Entity getEntity() {
				return null;
			}

			@Override
			public boolean sendCommandFeedback() {
				return false;
			}

			@Override
			public void setStat(CommandStats.Type statsType, int value) {
			}

			@Override
			public MinecraftServer getMinecraftServer() {
				return SignBlockEntity.this.world.getServer();
			}
		};

		for (int i = 0; i < 4; i++) {
			String string = nbt.getString("Text" + (i + 1));
			Text text = Text.Serializer.deserializeText(string);

			try {
				this.text[i] = ChatSerializer.process(commandSource, text, null);
			} catch (CommandException var7) {
				this.text[i] = text;
			}
		}

		this.commandStats.fromNbt(nbt);
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket getUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 9, this.getUpdatePacketContent());
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
	}

	@Override
	public boolean shouldNotCopyNbtFromItem() {
		return true;
	}

	public boolean isEditable() {
		return this.editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		if (!editable) {
			this.editor = null;
		}
	}

	public void setEditor(PlayerEntity editor) {
		this.editor = editor;
	}

	public PlayerEntity getEditor() {
		return this.editor;
	}

	public boolean onActivate(PlayerEntity player) {
		CommandSource commandSource = new CommandSource() {
			@Override
			public String getTranslationKey() {
				return player.getTranslationKey();
			}

			@Override
			public Text getName() {
				return player.getName();
			}

			@Override
			public void sendMessage(Text text) {
			}

			@Override
			public boolean canUseCommand(int permissionLevel, String commandLiteral) {
				return permissionLevel <= 2;
			}

			@Override
			public BlockPos getBlockPos() {
				return SignBlockEntity.this.pos;
			}

			@Override
			public Vec3d getPos() {
				return new Vec3d(
					(double)SignBlockEntity.this.pos.getX() + 0.5, (double)SignBlockEntity.this.pos.getY() + 0.5, (double)SignBlockEntity.this.pos.getZ() + 0.5
				);
			}

			@Override
			public World getWorld() {
				return player.getWorld();
			}

			@Override
			public Entity getEntity() {
				return player;
			}

			@Override
			public boolean sendCommandFeedback() {
				return false;
			}

			@Override
			public void setStat(CommandStats.Type statsType, int value) {
				if (SignBlockEntity.this.world != null && !SignBlockEntity.this.world.isClient) {
					SignBlockEntity.this.commandStats.method_10792(SignBlockEntity.this.world.getServer(), this, statsType, value);
				}
			}

			@Override
			public MinecraftServer getMinecraftServer() {
				return player.getMinecraftServer();
			}
		};

		for (Text text : this.text) {
			Style style = text == null ? null : text.getStyle();
			if (style != null && style.getClickEvent() != null) {
				ClickEvent clickEvent = style.getClickEvent();
				if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					player.getMinecraftServer().getCommandManager().execute(commandSource, clickEvent.getValue());
				}
			}
		}

		return true;
	}

	public CommandStats getCommandStats() {
		return this.commandStats;
	}
}
