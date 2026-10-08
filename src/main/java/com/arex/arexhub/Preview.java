package com.arex.arexhub;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

/** One dynamic texture that always shows the currently selected (and tinted) texture. */
public class Preview {
    public static final Identifier ID = Identifier.of("arexhub", "dynamic/preview");
    public static boolean ready = false;
    public static int size = 16;
    private static NativeImageBackedTexture texture;

    public static void update(Selection sel) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (texture != null) {
            mc.getTextureManager().destroyTexture(ID);
            texture = null;
        }
        ready = false;
        if (sel == null) return;
        try (NativeImage src = TextureUtil.load(sel.texture)) {
            NativeImage out = TextureUtil.tint(src, sel.r, sel.g, sel.b);
            size = out.getWidth();
            texture = new NativeImageBackedTexture(out);
            mc.getTextureManager().registerTexture(ID, texture);
            ready = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
