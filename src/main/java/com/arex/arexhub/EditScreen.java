package com.arex.arexhub;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

import java.util.function.DoubleConsumer;

/** RMB screen: change the colour of the selected texture with R/G/B sliders. */
public class EditScreen extends Screen {
    private final ArexScreen parent;
    private final Category cat;
    private final Selection sel;
    private ChannelSlider sr, sg, sb;

    public EditScreen(ArexScreen parent, Category cat) {
        super(Text.literal("Edit texture"));
        this.parent = parent;
        this.cat = cat;
        this.sel = Selection.ALL.get(cat);
    }

    @Override
    protected void init() {
        int w = 160;
        int x = (width - w) / 2;
        int y = height / 2 + 30;

        sr = new ChannelSlider(x, y, w, 20, "Red", sel.r / 2f, v -> { sel.r = (float) v * 2f; Preview.update(sel); });
        sg = new ChannelSlider(x, y + 24, w, 20, "Green", sel.g / 2f, v -> { sel.g = (float) v * 2f; Preview.update(sel); });
        sb = new ChannelSlider(x, y + 48, w, 20, "Blue", sel.b / 2f, v -> { sel.b = (float) v * 2f; Preview.update(sel); });
        addDrawableChild(sr);
        addDrawableChild(sg);
        addDrawableChild(sb);

        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> {
            sel.r = sel.g = sel.b = 1f;
            Preview.update(sel);
            clearAndInit();
        }).dimensions(x, y + 76, 78, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(x + 82, y + 76, 78, 20).build());
    }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        ctx.fill(0, 0, width, height, ArexScreen.BG);
        int s = 96;
        int cx = width / 2;
        int top = height / 2 - 90;
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("EDIT TEXTURE"), cx, top - 16, ArexScreen.LIGHT);
        ctx.fill(cx - s / 2 - 6, top - 2, cx + s / 2 + 6, top + s + 6, ArexScreen.PANEL);
        if (Preview.ready) {
            ctx.drawTexture(RenderLayer::getGuiTextured, Preview.ID, cx - s / 2, top + 2, 0f, 0f, s, s,
                    Preview.size, Preview.size, Preview.size, Preview.size);
        }
        super.render(ctx, mx, my, delta);
    }

    @Override
    public void close() {
        parent.refreshPreview();
        client.setScreen(parent);
    }

    private static class ChannelSlider extends SliderWidget {
        private final String label;
        private final DoubleConsumer onChange;

        ChannelSlider(int x, int y, int w, int h, String label, double value, DoubleConsumer onChange) {
            super(x, y, w, h, Text.empty(), Math.max(0, Math.min(1, value)));
            this.label = label;
            this.onChange = onChange;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(label + ": " + (int) (value * 200) + "%"));
        }

        @Override
        protected void applyValue() {
            onChange.accept(value);
        }
    }
}
