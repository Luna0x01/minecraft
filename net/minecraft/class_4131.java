package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4131 implements AutoCloseable {
	private static final Logger field_20102 = LogManager.getLogger();
	private static final class_4137 field_20103 = new class_4137();
	private static final class_4134 field_20104 = () -> 4.0F;
	private static final Random field_20105 = new Random();
	private final TextureManager field_20106;
	private final Identifier field_20107;
	private class_4136 field_20108;
	private final List<class_4142> field_20109 = Lists.newArrayList();
	private final Char2ObjectMap<class_4136> field_20110 = new Char2ObjectOpenHashMap();
	private final Char2ObjectMap<class_4134> field_20111 = new Char2ObjectOpenHashMap();
	private final Int2ObjectMap<CharList> field_20112 = new Int2ObjectOpenHashMap();
	private final List<class_4132> field_20113 = Lists.newArrayList();

	public class_4131(TextureManager textureManager, Identifier identifier) {
		this.field_20106 = textureManager;
		this.field_20107 = identifier;
	}

	public void method_18463(List<class_4142> list) {
		for (class_4142 lv : this.field_20109) {
			lv.close();
		}

		this.field_20109.clear();
		this.method_18458();
		this.field_20113.clear();
		this.field_20110.clear();
		this.field_20111.clear();
		this.field_20112.clear();
		this.field_20108 = this.method_18462(class_4138.INSTANCE);
		Set<class_4142> set = Sets.newHashSet();

		for (char c = 0; c < '\uffff'; c++) {
			for (class_4142 lv2 : list) {
				class_4134 lv3 = (class_4134)(c == ' ' ? field_20104 : lv2.method_18486(c));
				if (lv3 != null) {
					set.add(lv2);
					if (lv3 != class_4138.INSTANCE) {
						((CharList)this.field_20112.computeIfAbsent(MathHelper.ceil(lv3.getAdvance(false)), i -> new CharArrayList())).add(c);
					}
					break;
				}
			}
		}

		list.stream().filter(set::contains).forEach(this.field_20109::add);
	}

	public void close() {
		this.method_18458();
	}

	public void method_18458() {
		for (class_4132 lv : this.field_20113) {
			lv.close();
		}
	}

	public class_4134 method_18459(char c) {
		return (class_4134)this.field_20111.computeIfAbsent(c, i -> (class_4134)(i == 32 ? field_20104 : this.method_18467((char)i)));
	}

	private class_4135 method_18467(char c) {
		for (class_4142 lv : this.field_20109) {
			class_4135 lv2 = lv.method_18486(c);
			if (lv2 != null) {
				return lv2;
			}
		}

		return class_4138.INSTANCE;
	}

	public class_4136 method_18465(char c) {
		return (class_4136)this.field_20110.computeIfAbsent(c, i -> (class_4136)(i == 32 ? field_20103 : this.method_18462(this.method_18467((char)i))));
	}

	private class_4136 method_18462(class_4135 arg) {
		for (class_4132 lv : this.field_20113) {
			class_4136 lv2 = lv.method_18470(arg);
			if (lv2 != null) {
				return lv2;
			}
		}

		class_4132 lv3 = new class_4132(
			new Identifier(this.field_20107.getNamespace(), this.field_20107.getPath() + "/" + this.field_20113.size()), arg.method_18475()
		);
		this.field_20113.add(lv3);
		this.field_20106.loadTexture(lv3.method_18469(), lv3);
		class_4136 lv4 = lv3.method_18470(arg);
		return lv4 == null ? this.field_20108 : lv4;
	}

	public class_4136 method_18461(class_4134 arg) {
		CharList charList = (CharList)this.field_20112.get(MathHelper.ceil(arg.getAdvance(false)));
		return charList != null && !charList.isEmpty() ? this.method_18465(charList.get(field_20105.nextInt(charList.size()))) : this.field_20108;
	}
}
