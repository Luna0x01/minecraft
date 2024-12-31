package net.minecraft;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class class_4462<T extends class_4465> {
	private final Set<class_4463> field_21905 = Sets.newHashSet();
	private final Map<String, T> field_21906 = Maps.newLinkedHashMap();
	private final List<T> field_21907 = Lists.newLinkedList();
	private final class_4465.class_4467<T> field_21908;

	public class_4462(class_4465.class_4467<T> arg) {
		this.field_21908 = arg;
	}

	public void method_21347() {
		Set<String> set = (Set<String>)this.field_21907.stream().map(class_4465::method_21365).collect(Collectors.toCollection(LinkedHashSet::new));
		this.field_21906.clear();
		this.field_21907.clear();

		for (class_4463 lv : this.field_21905) {
			lv.method_21356(this.field_21906, this.field_21908);
		}

		this.method_21355();
		this.field_21907.addAll((Collection)set.stream().map(this.field_21906::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));

		for (T lv2 : this.field_21906.values()) {
			if (lv2.method_21366() && !this.field_21907.contains(lv2)) {
				lv2.method_21368().method_21370(this.field_21907, lv2, Functions.identity(), false);
			}
		}
	}

	private void method_21355() {
		List<Entry<String, T>> list = Lists.newArrayList(this.field_21906.entrySet());
		this.field_21906.clear();
		list.stream().sorted(Entry.comparingByKey()).forEachOrdered(entry -> {
			class_4465 var10000 = (class_4465)this.field_21906.put(entry.getKey(), entry.getValue());
		});
	}

	public void method_21349(Collection<T> collection) {
		this.field_21907.clear();
		this.field_21907.addAll(collection);

		for (T lv : this.field_21906.values()) {
			if (lv.method_21366() && !this.field_21907.contains(lv)) {
				lv.method_21368().method_21370(this.field_21907, lv, Functions.identity(), false);
			}
		}
	}

	public Collection<T> method_21352() {
		return this.field_21906.values();
	}

	public Collection<T> method_21353() {
		Collection<T> collection = Lists.newArrayList(this.field_21906.values());
		collection.removeAll(this.field_21907);
		return collection;
	}

	public Collection<T> method_21354() {
		return this.field_21907;
	}

	@Nullable
	public T method_21348(String string) {
		return (T)this.field_21906.get(string);
	}

	public void method_21351(class_4463 arg) {
		this.field_21905.add(arg);
	}
}
