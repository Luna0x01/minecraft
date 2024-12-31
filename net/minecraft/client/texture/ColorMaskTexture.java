package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.List;
import net.minecraft.class_4277;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ColorMaskTexture extends AbstractTexture {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Identifier identifier;
	private final List<String> names;
	private final List<DyeColor> colors;

	public ColorMaskTexture(Identifier identifier, List<String> list, List<DyeColor> list2) {
		this.identifier = identifier;
		this.names = list;
		this.colors = list2;
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		try {
			Resource resource = manager.getResource(this.identifier);
			Throwable var3 = null;

			try (
				class_4277 lv = class_4277.method_19472(resource.getInputStream());
				class_4277 lv2 = new class_4277(lv.method_19458(), lv.method_19478(), false);
			) {
				lv2.method_19470(lv);

				for (int i = 0; i < 17 && i < this.names.size() && i < this.colors.size(); i++) {
					String string = (String)this.names.get(i);
					if (string != null) {
						Resource resource2 = manager.getResource(new Identifier(string));
						Throwable var11 = null;

						try (class_4277 lv3 = class_4277.method_19472(resource2.getInputStream())) {
							int j = ((DyeColor)this.colors.get(i)).method_14222();
							if (lv3.method_19458() == lv2.method_19458() && lv3.method_19478() == lv2.method_19478()) {
								for (int k = 0; k < lv3.method_19478(); k++) {
									for (int l = 0; l < lv3.method_19458(); l++) {
										int m = lv3.method_19459(l, k);
										if ((m & 0xFF000000) != 0) {
											int n = (m & 0xFF) << 24 & 0xFF000000;
											int o = lv.method_19459(l, k);
											int p = MathHelper.multiplyColors(o, j) & 16777215;
											lv2.method_19479(l, k, n | p);
										}
									}
								}
							}
						} catch (Throwable var142) {
							var11 = var142;
							throw var142;
						} finally {
							if (resource2 != null) {
								if (var11 != null) {
									try {
										resource2.close();
									} catch (Throwable var138) {
										var11.addSuppressed(var138);
									}
								} else {
									resource2.close();
								}
							}
						}
					}
				}

				TextureUtil.prepareImage(this.getGlId(), lv2.method_19458(), lv2.method_19478());
				GlStateManager.method_19122(3357, Float.MAX_VALUE);
				lv2.method_19466(0, 0, 0, false);
				GlStateManager.method_19122(3357, 0.0F);
			} catch (Throwable var148) {
				var3 = var148;
				throw var148;
			} finally {
				if (resource != null) {
					if (var3 != null) {
						try {
							resource.close();
						} catch (Throwable var135) {
							var3.addSuppressed(var135);
						}
					} else {
						resource.close();
					}
				}
			}
		} catch (IOException var150) {
			LOGGER.error("Couldn't load layered color mask image", var150);
		}
	}
}
