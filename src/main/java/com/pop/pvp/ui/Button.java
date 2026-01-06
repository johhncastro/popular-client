package com.pop.pvp.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;

/**
 * Modern button component with hover animations and smooth transitions.
 * 
 * Why: Buttons provide clear call-to-action with visual feedback.
 * Hover animations give immediate feedback without being distracting.
 */
public class Button extends Component {
    private String text;
    private Runnable onClick;
    private int textColor;
    private int accentColor;
    private boolean enabled = true;
    
    // Visual properties
    private static final int CORNER_RADIUS = 6;
    private static final int PADDING_X = 16;
    private static final int PADDING_Y = 8;
    
    public Button(int x, int y, int width, int height, String text, Runnable onClick, int accentColor) {
        super(x, y, width, height);
        this.text = text;
        this.onClick = onClick;
        this.textColor = 0xFFFFFF;
        this.accentColor = accentColor;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        float hoverProgress = getHoverProgress();
        
        // Calculate colors with hover interpolation
        int bgAlpha = (int)(150 + hoverProgress * 50);
        int bgRed = (int)(30 + hoverProgress * 20);
        int bgGreen = (int)(30 + hoverProgress * 20);
        int bgBlue = (int)(30 + hoverProgress * 20);
        int bgColor = UIUtils.color(bgAlpha, bgRed, bgGreen, bgBlue);
        
        // Draw button background with rounded corners
        UIUtils.drawRoundedRect(x, y, width, height, CORNER_RADIUS, bgColor);
        
        // Draw accent border on hover
        if (hoverProgress > 0.1F) {
            int borderAlpha = (int)(hoverProgress * 150);
            int borderColor = UIUtils.color(borderAlpha,
                (accentColor >> 16) & 255,
                (accentColor >> 8) & 255,
                accentColor & 255);
            UIUtils.drawRoundedRect(x, y, width, 2, CORNER_RADIUS, borderColor);
        }
        
        // Draw text (centered)
        int textWidth = font.getStringWidth(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - 8) / 2;
        
        // Fade text color based on enabled state
        int finalTextColor = enabled ? textColor : 0x888888;
        font.drawString(text, textX, textY, finalTextColor);
    }
    
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!visible || !enabled || !isMouseOver(mouseX, mouseY)) return false;
        
        if (mouseButton == 0 && onClick != null) {
            onClick.run();
            // Play click sound
            Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.3F, 1.0F);
            return true;
        }
        
        return false;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setAccentColor(int color) {
        this.accentColor = color;
    }
}

