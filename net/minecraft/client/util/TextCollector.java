package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.text.StringVisitable;

public class TextCollector {
	private final List<StringVisitable> texts = Lists.newArrayList();

	public void add(StringVisitable text) {
		this.texts.add(text);
	}

	@Nullable
	public StringVisitable getRawCombined() {
		if (this.texts.isEmpty()) {
			return null;
		} else {
			return this.texts.size() == 1 ? (StringVisitable)this.texts.get(0) : StringVisitable.concat(this.texts);
		}
	}

	public StringVisitable getCombined() {
		StringVisitable stringVisitable = this.getRawCombined();
		return stringVisitable != null ? stringVisitable : StringVisitable.EMPTY;
	}

	public void clear() {
		this.texts.clear();
	}
}
