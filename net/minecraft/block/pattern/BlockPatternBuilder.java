package net.minecraft.block.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BlockPatternBuilder {
	private static final Joiner JOINER = Joiner.on(",");
	private final List<String[]> aisles = Lists.newArrayList();
	private final Map<Character, Predicate<CachedBlockPosition>> charMap = Maps.newHashMap();
	private int height;
	private int width;

	private BlockPatternBuilder() {
		this.charMap.put(' ', Predicates.alwaysTrue());
	}

	public BlockPatternBuilder aisle(String... args) {
		if (!ArrayUtils.isEmpty(args) && !StringUtils.isEmpty(args[0])) {
			if (this.aisles.isEmpty()) {
				this.height = args.length;
				this.width = args[0].length();
			}

			if (args.length != this.height) {
				throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + args.length + ")");
			} else {
				for (String string : args) {
					if (string.length() != this.width) {
						throw new IllegalArgumentException(
							"Not all rows in the given aisle are the correct width (expected " + this.width + ", found one with " + string.length() + ")"
						);
					}

					for (char c : string.toCharArray()) {
						if (!this.charMap.containsKey(c)) {
							this.charMap.put(c, null);
						}
					}
				}

				this.aisles.add(args);
				return this;
			}
		} else {
			throw new IllegalArgumentException("Empty pattern for aisle");
		}
	}

	public static BlockPatternBuilder start() {
		return new BlockPatternBuilder();
	}

	public BlockPatternBuilder method_16940(char c, Predicate<CachedBlockPosition> predicate) {
		this.charMap.put(c, predicate);
		return this;
	}

	public BlockPattern build() {
		return new BlockPattern(this.method_16941());
	}

	private Predicate<CachedBlockPosition>[][][] method_16941() {
		this.validate();
		Predicate<CachedBlockPosition>[][][] predicates = (Predicate<CachedBlockPosition>[][][])Array.newInstance(
			Predicate.class, new int[]{this.aisles.size(), this.height, this.width}
		);

		for (int i = 0; i < this.aisles.size(); i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.width; k++) {
					predicates[i][j][k] = (Predicate<CachedBlockPosition>)this.charMap.get(((String[])this.aisles.get(i))[j].charAt(k));
				}
			}
		}

		return predicates;
	}

	private void validate() {
		List<Character> list = Lists.newArrayList();

		for (Entry<Character, Predicate<CachedBlockPosition>> entry : this.charMap.entrySet()) {
			if (entry.getValue() == null) {
				list.add(entry.getKey());
			}
		}

		if (!list.isEmpty()) {
			throw new IllegalStateException("Predicates for character(s) " + JOINER.join(list) + " are missing");
		}
	}
}
