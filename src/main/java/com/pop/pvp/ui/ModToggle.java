package com.pop.pvp.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;

/**
 * Simple mod toggle component - just highlighted text when enabled.
 * Clean, minimal design matching Wurst/RusherHack style.
 * 
 * Why: Highlighted text is cleaner than toggle switches.
 * Less visual clutter, easier to scan.
 */
public class ModToggle extends Component {
    private String label;
    private boolean value;
    private Runnable onToggle;
    private int accentColor;
    
    private static final int HEIGHT = 12;
    
    public ModToggle(int x, int y, int width, String label, boolean initialValue, Runnable onToggle, int accentColor) {
        super(x, y, width, HEIGHT);
        this.label = label;
        this.value = initialValue;
        this.onToggle = onToggle;
        this.accentColor = accentColor;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        float hoverProgress = getHoverProgress();
        
        // Calculate text color based on enabled state and hover
        int textColor;
        if (value) {
            // Enabled: use accent color
            textColor = 0xFF000000 | 
                ((accentColor >> 16) & 255) << 16 | 
                ((accentColor >> 8) & 255) << 8 | 
                (accentColor & 255);
        } else {
            // Disabled: gray, brighter on hover
            int grayValue = (int)(100 + hoverProgress * 100);
            textColor = 0xFF000000 | (grayValue << 16) | (grayValue << 8) | grayValue;
        }
        
        // Draw label
        font.drawString(label, x, y, textColor);
        
        // Draw indicator dot if enabled (Wurst style)
        if (value) {
            int dotSize = 3;
            int dotX = x + font.getStringWidth(label) + 4;
            int dotY = y + (height - dotSize) / 2;
            int dotColor = UIUtils.color(255,
                (accentColor >> 16) & 255,
                (accentColor >> 8) & 255,
                accentColor & 255);
            UIUtils.drawRoundedRect(dotX, dotY, dotSize, dotSize, dotSize / 2, dotColor);
        }
    }
    
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!visible || !isMouseOver(mouseX, mouseY)) return false;
        
        if (mouseButton == 0) {
            value = !value;
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
    }
}

