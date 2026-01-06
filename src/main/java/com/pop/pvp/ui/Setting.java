package com.pop.pvp.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;

/**
 * Setting component with label and toggle button.
 * Clean, minimal design for configuration options.
 * 
 * Why: Settings provide clear visual separation between label and control.
 * Consistent spacing improves scanability.
 */
public class Setting extends Component {
    private String label;
    private boolean value;
    private Runnable onToggle;
    private int accentColor;
    private Animation toggleAnimation;
    
    // Visual properties (Wurst/RusherHack style)
    private static final int TOGGLE_WIDTH = 36;
    private static final int TOGGLE_HEIGHT = 18;
    private static final int CORNER_RADIUS = 9;
    private static final int LABEL_SPACING = 12;
    
    public Setting(int x, int y, int width, String label, boolean initialValue, Runnable onToggle, int accentColor) {
        super(x, y, width, TOGGLE_HEIGHT);
        this.label = label;
        this.value = initialValue;
        this.onToggle = onToggle;
        this.accentColor = accentColor;
        this.toggleAnimation = new Animation(initialValue ? 1.0F : 0.0F, 10.0F);
    }
    
    @Override
    public void update(int mouseX, int mouseY, float partialTicks) {
        super.update(mouseX, mouseY, partialTicks);
        toggleAnimation.update(partialTicks);
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        float hoverProgress = getHoverProgress();
        
        // Draw label (clean white text - Wurst style)
        font.drawString(
            label,
            x,
            y + (height - 8) / 2,
            0xFFFFFF
        );
        
        // Calculate toggle position (right-aligned)
        int toggleX = x + width - TOGGLE_WIDTH;
        int toggleY = y;
        
        // Draw toggle background (darker when off, accent when on - RusherHack style)
        int bgColor = value ? 
            UIUtils.color(255, (accentColor >> 16) & 255, (accentColor >> 8) & 255, accentColor & 255) :
            UIUtils.color(255, 30, 30, 30); // Dark gray when off
        UIUtils.drawRoundedRect(toggleX, toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT, CORNER_RADIUS, bgColor);
        
        // Draw border around toggle
        int borderColor = UIUtils.color(255, 50, 50, 50);
        UIUtils.drawRoundedRect(toggleX, toggleY, TOGGLE_WIDTH, 1, 0, borderColor);
        UIUtils.drawRoundedRect(toggleX, toggleY + TOGGLE_HEIGHT - 1, TOGGLE_WIDTH, 1, 0, borderColor);
        UIUtils.drawRoundedRect(toggleX, toggleY, 1, TOGGLE_HEIGHT, 0, borderColor);
        UIUtils.drawRoundedRect(toggleX + TOGGLE_WIDTH - 1, toggleY, 1, TOGGLE_HEIGHT, 0, borderColor);
        
        // Draw toggle indicator (circle) with smooth animation (Wurst style)
        int indicatorSize = TOGGLE_HEIGHT - 4;
        int indicatorY = toggleY + 2;
        float toggleProgress = toggleAnimation.getValue();
        int minX = toggleX + 2;
        int maxX = toggleX + TOGGLE_WIDTH - indicatorSize - 2;
        int indicatorX = (int)UIUtils.lerp(minX, maxX, toggleProgress);
        
        // White indicator with subtle shadow effect
        int indicatorColor = UIUtils.color(255, 255, 255, 255);
        UIUtils.drawRoundedRect(indicatorX, indicatorY, indicatorSize, indicatorSize, indicatorSize / 2, indicatorColor);
        
        // Subtle border on indicator
        int indicatorBorder = UIUtils.color(200, 200, 200, 200);
        UIUtils.drawRoundedRect(indicatorX, indicatorY, indicatorSize, 1, 0, indicatorBorder);
    }
    
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!visible || !isMouseOver(mouseX, mouseY)) return false;
        
        if (mouseButton == 0) {
            value = !value;
            toggleAnimation.animateTo(value ? 1.0F : 0.0F);
            if (onToggle != null) {
                onToggle.run();
            }
            Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.3F, 1.0F);
            return true;
        }
        
        return false;
    }
    
    public boolean getValue() {
        return value;
    }
    
    public void setValue(boolean value) {
        this.value = value;
        toggleAnimation.animateTo(value ? 1.0F : 0.0F);
    }
}

