package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class CyclingButtonWidget<T> extends PressableWidget implements OrderableTooltip {
	static final BooleanSupplier HAS_ALT_DOWN = Screen::hasAltDown;
	private static final List<Boolean> BOOLEAN_VALUES = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
	private final Text optionText;
	private int index;
	private T value;
	private final CyclingButtonWidget.Values<T> values;
	private final Function<T, Text> valueToText;
	private final Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory;
	private final CyclingButtonWidget.UpdateCallback<T> callback;
	private final CyclingButtonWidget.TooltipFactory<T> tooltipFactory;
	private final boolean optionTextOmitted;

	CyclingButtonWidget(
		int x,
		int y,
		int width,
		int height,
		Text message,
		Text optionText,
		int index,
		T value,
		CyclingButtonWidget.Values<T> values,
		Function<T, Text> valueToText,
		Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory,
		CyclingButtonWidget.UpdateCallback<T> callback,
		CyclingButtonWidget.TooltipFactory<T> tooltipFactory,
		boolean optionTextOmitted
	) {
		super(x, y, width, height, message);
		this.optionText = optionText;
		this.index = index;
		this.value = value;
		this.values = values;
		this.valueToText = valueToText;
		this.narrationMessageFactory = narrationMessageFactory;
		this.callback = callback;
		this.tooltipFactory = tooltipFactory;
		this.optionTextOmitted = optionTextOmitted;
	}

	@Override
	public void onPress() {
		if (Screen.hasShiftDown()) {
			this.cycle(-1);
		} else {
			this.cycle(1);
		}
	}

	private void cycle(int amount) {
		List<T> list = this.values.getCurrent();
		this.index = MathHelper.floorMod(this.index + amount, list.size());
		T object = (T)list.get(this.index);
		this.internalSetValue(object);
		this.callback.onValueChange(this, object);
	}

	private T getValue(int offset) {
		List<T> list = this.values.getCurrent();
		return (T)list.get(MathHelper.floorMod(this.index + offset, list.size()));
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (amount > 0.0) {
			this.cycle(-1);
		} else if (amount < 0.0) {
			this.cycle(1);
		}

		return true;
	}

	public void setValue(T value) {
		List<T> list = this.values.getCurrent();
		int i = list.indexOf(value);
		if (i != -1) {
			this.index = i;
		}

		this.internalSetValue(value);
	}

	private void internalSetValue(T value) {
		Text text = this.composeText(value);
		this.setMessage(text);
		this.value = value;
	}

	private Text composeText(T value) {
		return (Text)(this.optionTextOmitted ? (Text)this.valueToText.apply(value) : this.composeGenericOptionText(value));
	}

	private MutableText composeGenericOptionText(T value) {
		return ScreenTexts.composeGenericOptionText(this.optionText, (Text)this.valueToText.apply(value));
	}

	public T getValue() {
		return this.value;
	}

	@Override
	protected MutableText getNarrationMessage() {
		return (MutableText)this.narrationMessageFactory.apply(this);
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.getNarrationMessage());
		if (this.active) {
			T object = this.getValue(1);
			Text text = this.composeText(object);
			if (this.isFocused()) {
				builder.put(NarrationPart.USAGE, new TranslatableText("narration.cycle_button.usage.focused", text));
			} else {
				builder.put(NarrationPart.USAGE, new TranslatableText("narration.cycle_button.usage.hovered", text));
			}
		}
	}

	public MutableText getGenericNarrationMessage() {
		return getNarrationMessage((Text)(this.optionTextOmitted ? this.composeGenericOptionText(this.value) : this.getMessage()));
	}

	@Override
	public List<OrderedText> getOrderedTooltip() {
		return (List<OrderedText>)this.tooltipFactory.apply(this.value);
	}

	public static <T> CyclingButtonWidget.Builder<T> builder(Function<T, Text> valueToText) {
		return new CyclingButtonWidget.Builder<>(valueToText);
	}

	public static CyclingButtonWidget.Builder<Boolean> onOffBuilder(Text on, Text off) {
		return new CyclingButtonWidget.Builder<Boolean>(value -> value ? on : off).values(BOOLEAN_VALUES);
	}

	public static CyclingButtonWidget.Builder<Boolean> onOffBuilder() {
		return new CyclingButtonWidget.Builder<Boolean>(value -> value ? ScreenTexts.ON : ScreenTexts.OFF).values(BOOLEAN_VALUES);
	}

	public static CyclingButtonWidget.Builder<Boolean> onOffBuilder(boolean initialValue) {
		return onOffBuilder().initially(initialValue);
	}

	public static class Builder<T> {
		private int initialIndex;
		@Nullable
		private T value;
		private final Function<T, Text> valueToText;
		private CyclingButtonWidget.TooltipFactory<T> tooltipFactory = value -> ImmutableList.of();
		private Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory = CyclingButtonWidget::getGenericNarrationMessage;
		private CyclingButtonWidget.Values<T> values = CyclingButtonWidget.Values.of(ImmutableList.of());
		private boolean optionTextOmitted;

		public Builder(Function<T, Text> valueToText) {
			this.valueToText = valueToText;
		}

		public CyclingButtonWidget.Builder<T> values(List<T> values) {
			this.values = CyclingButtonWidget.Values.of(values);
			return this;
		}

		@SafeVarargs
		public final CyclingButtonWidget.Builder<T> values(T... values) {
			return this.values(ImmutableList.copyOf(values));
		}

		public CyclingButtonWidget.Builder<T> values(List<T> defaults, List<T> alternatives) {
			this.values = CyclingButtonWidget.Values.of(CyclingButtonWidget.HAS_ALT_DOWN, defaults, alternatives);
			return this;
		}

		public CyclingButtonWidget.Builder<T> values(BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
			this.values = CyclingButtonWidget.Values.of(alternativeToggle, defaults, alternatives);
			return this;
		}

		public CyclingButtonWidget.Builder<T> tooltip(CyclingButtonWidget.TooltipFactory<T> tooltipFactory) {
			this.tooltipFactory = tooltipFactory;
			return this;
		}

		public CyclingButtonWidget.Builder<T> initially(T value) {
			this.value = value;
			int i = this.values.getDefaults().indexOf(value);
			if (i != -1) {
				this.initialIndex = i;
			}

			return this;
		}

		public CyclingButtonWidget.Builder<T> narration(Function<CyclingButtonWidget<T>, MutableText> narrationMessageFactory) {
			this.narrationMessageFactory = narrationMessageFactory;
			return this;
		}

		public CyclingButtonWidget.Builder<T> omitKeyText() {
			this.optionTextOmitted = true;
			return this;
		}

		public CyclingButtonWidget<T> build(int x, int y, int width, int height, Text optionText) {
			return this.build(x, y, width, height, optionText, (button, value) -> {
			});
		}

		public CyclingButtonWidget<T> build(int x, int y, int width, int height, Text optionText, CyclingButtonWidget.UpdateCallback<T> callback) {
			List<T> list = this.values.getDefaults();
			if (list.isEmpty()) {
				throw new IllegalStateException("No values for cycle button");
			} else {
				T object = (T)(this.value != null ? this.value : list.get(this.initialIndex));
				Text text = (Text)this.valueToText.apply(object);
				Text text2 = (Text)(this.optionTextOmitted ? text : ScreenTexts.composeGenericOptionText(optionText, text));
				return new CyclingButtonWidget<>(
					x,
					y,
					width,
					height,
					text2,
					optionText,
					this.initialIndex,
					object,
					this.values,
					this.valueToText,
					this.narrationMessageFactory,
					callback,
					this.tooltipFactory,
					this.optionTextOmitted
				);
			}
		}
	}

	@FunctionalInterface
	public interface TooltipFactory<T> extends Function<T, List<OrderedText>> {
	}

	public interface UpdateCallback<T> {
		void onValueChange(CyclingButtonWidget button, T value);
	}

	interface Values<T> {
		List<T> getCurrent();

		List<T> getDefaults();

		static <T> CyclingButtonWidget.Values<T> of(List<T> values) {
			final List<T> list = ImmutableList.copyOf(values);
			return new CyclingButtonWidget.Values<T>() {
				@Override
				public List<T> getCurrent() {
					return list;
				}

				@Override
				public List<T> getDefaults() {
					return list;
				}
			};
		}

		static <T> CyclingButtonWidget.Values<T> of(BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
			final List<T> list = ImmutableList.copyOf(defaults);
			final List<T> list2 = ImmutableList.copyOf(alternatives);
			return new CyclingButtonWidget.Values<T>() {
				@Override
				public List<T> getCurrent() {
					return alternativeToggle.getAsBoolean() ? list2 : list;
				}

				@Override
				public List<T> getDefaults() {
					return list;
				}
			};
		}
	}
}
