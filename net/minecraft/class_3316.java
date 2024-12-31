package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.KeyBindComponent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public class class_3316 {
	private final MinecraftClient field_16231;
	@Nullable
	private class_3318 field_16232;

	public class_3316(MinecraftClient minecraftClient) {
		this.field_16231 = minecraftClient;
	}

	public void method_14723(Input input) {
		if (this.field_16232 != null) {
			this.field_16232.method_14736(input);
		}
	}

	public void method_19639(double d, double e) {
		if (this.field_16232 != null) {
			this.field_16232.method_19640(d, e);
		}
	}

	public void method_14721(@Nullable ClientWorld clientWorld, @Nullable BlockHitResult blockHitResult) {
		if (this.field_16232 != null && blockHitResult != null && clientWorld != null) {
			this.field_16232.method_14734(clientWorld, blockHitResult);
		}
	}

	public void method_14722(ClientWorld clientWorld, BlockPos blockPos, BlockState blockState, float f) {
		if (this.field_16232 != null) {
			this.field_16232.method_14735(clientWorld, blockPos, blockState, f);
		}
	}

	public void method_14718() {
		if (this.field_16232 != null) {
			this.field_16232.method_14738();
		}
	}

	public void method_14719(ItemStack itemStack) {
		if (this.field_16232 != null) {
			this.field_16232.method_14732(itemStack);
		}
	}

	public void method_14726() {
		if (this.field_16232 != null) {
			this.field_16232.method_14737();
			this.field_16232 = null;
		}
	}

	public void method_14727() {
		if (this.field_16232 != null) {
			this.method_14726();
		}

		this.field_16232 = this.field_16231.options.field_15878.method_14740(this);
	}

	public void method_14728() {
		if (this.field_16232 != null) {
			if (this.field_16231.world != null) {
				this.field_16232.method_14731();
			} else {
				this.method_14726();
			}
		} else if (this.field_16231.world != null) {
			this.method_14727();
		}
	}

	public void method_14724(class_3319 arg) {
		this.field_16231.options.field_15878 = arg;
		this.field_16231.options.save();
		if (this.field_16232 != null) {
			this.field_16232.method_14737();
			this.field_16232 = arg.method_14740(this);
		}
	}

	public MinecraftClient method_14729() {
		return this.field_16231;
	}

	public GameMode method_14730() {
		return this.field_16231.interactionManager == null ? GameMode.NOT_SET : this.field_16231.interactionManager.method_9667();
	}

	public static Text method_14725(String string) {
		return new KeyBindComponent("key." + string).formatted(Formatting.BOLD);
	}
}
