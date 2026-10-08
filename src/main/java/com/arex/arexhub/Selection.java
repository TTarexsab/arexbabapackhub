package com.arex.arexhub;

import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

/** The chosen texture for a tab + its colour multipliers (1.0 = unchanged). */
public class Selection {
    public static final Map<Category, Selection> ALL = new EnumMap<>(Category.class);

    public final Identifier texture;
    public float r = 1f, g = 1f, b = 1f;

    public Selection(Identifier texture) {
        this.texture = texture;
    }
}
