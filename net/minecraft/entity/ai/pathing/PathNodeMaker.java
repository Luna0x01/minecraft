package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

public abstract class PathNodeMaker {
	protected final TextFieldWidget field_13323;
	protected final boolean field_13324;
	protected boolean field_13325;
	protected boolean field_13326;
	protected int field_13327;
	protected List<String> field_13328 = Lists.newArrayList();

	public PathNodeMaker(TextFieldWidget textFieldWidget, boolean bl) {
		this.field_13323 = textFieldWidget;
		this.field_13324 = bl;
	}

	public void method_12183() {
		if (this.field_13325) {
			this.field_13323.eraseCharacters(0);
			this.field_13323.eraseCharacters(this.field_13323.getWordSkipPosition(-1, this.field_13323.getCursor(), false) - this.field_13323.getCursor());
			if (this.field_13327 >= this.field_13328.size()) {
				this.field_13327 = 0;
			}
		} else {
			int i = this.field_13323.getWordSkipPosition(-1, this.field_13323.getCursor(), false);
			this.field_13328.clear();
			this.field_13327 = 0;
			String string = this.field_13323.getText().substring(0, this.field_13323.getCursor());
			this.method_12184(string);
			if (this.field_13328.isEmpty()) {
				return;
			}

			this.field_13325 = true;
			this.field_13323.eraseCharacters(i - this.field_13323.getCursor());
		}

		this.field_13323.write((String)this.field_13328.get(this.field_13327++));
	}

	private void method_12184(String string) {
		if (string.length() >= 1) {
			MinecraftClient.getInstance().player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(string, this.method_12186(), this.field_13324));
			this.field_13326 = true;
		}
	}

	@Nullable
	public abstract BlockPos method_12186();

	public void method_12185(String... strings) {
		if (this.field_13326) {
			this.field_13325 = false;
			this.field_13328.clear();

			for (String string : strings) {
				if (!string.isEmpty()) {
					this.field_13328.add(string);
				}
			}

			String string2 = this.field_13323.getText().substring(this.field_13323.getWordSkipPosition(-1, this.field_13323.getCursor(), false));
			String string3 = StringUtils.getCommonPrefix(strings);
			if (!string3.isEmpty() && !string2.equalsIgnoreCase(string3)) {
				this.field_13323.eraseCharacters(0);
				this.field_13323.eraseCharacters(this.field_13323.getWordSkipPosition(-1, this.field_13323.getCursor(), false) - this.field_13323.getCursor());
				this.field_13323.write(string3);
			} else if (!this.field_13328.isEmpty()) {
				this.field_13325 = true;
				this.method_12183();
			}
		}
	}

	public void method_12187() {
		this.field_13325 = false;
	}

	public void method_12188() {
		this.field_13326 = false;
	}
}
