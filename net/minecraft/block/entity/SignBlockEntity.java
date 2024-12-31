package net.minecraft.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.class_3893;
import net.minecraft.class_3915;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class SignBlockEntity extends BlockEntity implements class_3893 {
	public final Text[] text = new Text[]{new LiteralText(""), new LiteralText(""), new LiteralText(""), new LiteralText("")};
	public int lineBeingEdited = -1;
	private boolean editable = true;
	private PlayerEntity editor;
	private final String[] field_18645 = new String[4];

	public SignBlockEntity() {
		super(BlockEntityType.SIGN);
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);

		for (int i = 0; i < 4; i++) {
			String string = Text.Serializer.serialize(this.text[i]);
			nbt.putString("Text" + (i + 1), string);
		}

		return nbt;
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		this.editable = false;
		super.fromNbt(nbt);

		for (int i = 0; i < 4; i++) {
			String string = nbt.getString("Text" + (i + 1));
			Text text = Text.Serializer.deserializeText(string);
			if (this.world instanceof ServerWorld) {
				try {
					this.text[i] = ChatSerializer.method_20185(this.method_16839(null), text, null);
				} catch (CommandSyntaxException var6) {
					this.text[i] = text;
				}
			} else {
				this.text[i] = text;
			}

			this.field_18645[i] = null;
		}
	}

	public Text method_16836(int i) {
		return this.text[i];
	}

	public void method_16837(int i, Text text) {
		this.text[i] = text;
		this.field_18645[i] = null;
	}

	@Nullable
	public String method_16838(int i, Function<Text, String> function) {
		if (this.field_18645[i] == null && this.text[i] != null) {
			this.field_18645[i] = (String)function.apply(this.text[i]);
		}

		return this.field_18645[i];
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
		for (Text text : this.text) {
			Style style = text == null ? null : text.getStyle();
			if (style != null && style.getClickEvent() != null) {
				ClickEvent clickEvent = style.getClickEvent();
				if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					player.method_12833().method_2971().method_17519(this.method_16839((ServerPlayerEntity)player), clickEvent.getValue());
				}
			}
		}

		return true;
	}

	@Override
	public void method_5505(Text text) {
	}

	public class_3915 method_16839(@Nullable ServerPlayerEntity serverPlayerEntity) {
		String string = serverPlayerEntity == null ? "Sign" : serverPlayerEntity.method_15540().getString();
		Text text = (Text)(serverPlayerEntity == null ? new LiteralText("Sign") : serverPlayerEntity.getName());
		return new class_3915(
			this,
			new Vec3d((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5),
			Vec2f.ZERO,
			(ServerWorld)this.world,
			2,
			string,
			text,
			this.world.getServer(),
			serverPlayerEntity
		);
	}

	@Override
	public boolean method_17413() {
		return false;
	}

	@Override
	public boolean method_17414() {
		return false;
	}

	@Override
	public boolean method_17412() {
		return false;
	}
}
