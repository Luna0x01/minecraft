package net.minecraft;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3328 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<Identifier, SimpleAdvancement> field_16280 = Maps.newHashMap();
	private final Set<SimpleAdvancement> field_16281 = Sets.newLinkedHashSet();
	private final Set<SimpleAdvancement> field_16282 = Sets.newLinkedHashSet();
	private class_3328.class_3329 field_16283;

	private void method_14810(SimpleAdvancement advancement) {
		for (SimpleAdvancement simpleAdvancement : advancement.getChildren()) {
			this.method_14810(simpleAdvancement);
		}

		LOGGER.info("Forgot about advancement {}", advancement.getIdentifier());
		this.field_16280.remove(advancement.getIdentifier());
		if (advancement.getParent() == null) {
			this.field_16281.remove(advancement);
			if (this.field_16283 != null) {
				this.field_16283.method_14819(advancement);
			}
		} else {
			this.field_16282.remove(advancement);
			if (this.field_16283 != null) {
				this.field_16283.method_14821(advancement);
			}
		}
	}

	public void method_14813(Set<Identifier> set) {
		for (Identifier identifier : set) {
			SimpleAdvancement simpleAdvancement = (SimpleAdvancement)this.field_16280.get(identifier);
			if (simpleAdvancement == null) {
				LOGGER.warn("Told to remove advancement {} but I don't know what that is", identifier);
			} else {
				this.method_14810(simpleAdvancement);
			}
		}
	}

	public void method_14812(Map<Identifier, SimpleAdvancement.TaskAdvancement> map) {
		Function<Identifier, SimpleAdvancement> function = Functions.forMap(this.field_16280, null);

		while (!map.isEmpty()) {
			boolean bl = false;
			Iterator<Entry<Identifier, SimpleAdvancement.TaskAdvancement>> iterator = map.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Identifier, SimpleAdvancement.TaskAdvancement> entry = (Entry<Identifier, SimpleAdvancement.TaskAdvancement>)iterator.next();
				Identifier identifier = (Identifier)entry.getKey();
				SimpleAdvancement.TaskAdvancement taskAdvancement = (SimpleAdvancement.TaskAdvancement)entry.getValue();
				if (taskAdvancement.method_14806(function)) {
					SimpleAdvancement simpleAdvancement = taskAdvancement.method_14807(identifier);
					this.field_16280.put(identifier, simpleAdvancement);
					bl = true;
					iterator.remove();
					if (simpleAdvancement.getParent() == null) {
						this.field_16281.add(simpleAdvancement);
						if (this.field_16283 != null) {
							this.field_16283.method_14818(simpleAdvancement);
						}
					} else {
						this.field_16282.add(simpleAdvancement);
						if (this.field_16283 != null) {
							this.field_16283.method_14820(simpleAdvancement);
						}
					}
				}
			}

			if (!bl) {
				for (Entry<Identifier, SimpleAdvancement.TaskAdvancement> entry2 : map.entrySet()) {
					LOGGER.error("Couldn't load advancement {}: {}", entry2.getKey(), entry2.getValue());
				}
				break;
			}
		}

		LOGGER.info("Loaded {} advancements", this.field_16280.size());
	}

	public void method_14809() {
		this.field_16280.clear();
		this.field_16281.clear();
		this.field_16282.clear();
		if (this.field_16283 != null) {
			this.field_16283.method_14817();
		}
	}

	public Iterable<SimpleAdvancement> method_14815() {
		return this.field_16281;
	}

	public Collection<SimpleAdvancement> method_20270() {
		return this.field_16280.values();
	}

	@Nullable
	public SimpleAdvancement method_14814(Identifier identifier) {
		return (SimpleAdvancement)this.field_16280.get(identifier);
	}

	public void method_14811(@Nullable class_3328.class_3329 arg) {
		this.field_16283 = arg;
		if (arg != null) {
			for (SimpleAdvancement simpleAdvancement : this.field_16281) {
				arg.method_14818(simpleAdvancement);
			}

			for (SimpleAdvancement simpleAdvancement2 : this.field_16282) {
				arg.method_14820(simpleAdvancement2);
			}
		}
	}

	public interface class_3329 {
		void method_14818(SimpleAdvancement advancement);

		void method_14819(SimpleAdvancement advancement);

		void method_14820(SimpleAdvancement advancement);

		void method_14821(SimpleAdvancement advancement);

		void method_14817();
	}
}
