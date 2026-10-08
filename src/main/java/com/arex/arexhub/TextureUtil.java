package com.arex.arexhub;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

public class TextureUtil {
    public static NativeImage load(Identifier id) throws IOException {
        var res = MinecraftClient.getInstance().getResourceManager().getResource(id)
                .orElseThrow(() -> new IOException("Missing texture " + id));
        try (InputStream in = res.getInputStream()) {
            return NativeImage.read(in);
        }
    }

    public static NativeImage tint(NativeImage src, float r, float g, float b) {
        NativeImage out = new NativeImage(src.getWidth(), src.getHeight(), false);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int c = src.getColorArgb(x, y);
                int a = (c >>> 24) & 0xFF;
                int rr = clamp(((c >> 16) & 0xFF) * r);
                int gg = clamp(((c >> 8) & 0xFF) * g);
                int bb = clamp((c & 0xFF) * b);
                out.setColorArgb(x, y, (a << 24) | (rr << 16) | (gg << 8) | bb);
            }
        }
        return out;
    }

    private static int clamp(float v) {
        return Math.max(0, Math.min(255, Math.round(v)));
    }
}
