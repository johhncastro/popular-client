package com.pop.pvp.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;

/**
 * Category button for navigating between mod categories.
 * Clean, modern design matching RusherHack/Meteor style.
 */
public class CategoryButton extends Component {
    private String label;
    private Runnable onClick;
    private int accentColor;
    private boolean selected = false;
    
    private static final int CORNER_RADIUS = 4;
    private static final int PADDING_X = 12;
    private static final int PADDING_Y = 6;
    
    public CategoryButton(int x, int y, int width, int height, String label, Runnable onClick, int accentColor) {
        super(x, y, width, height);
        this.label = label;
        this.onClick = onClick;
        this.accentColor = accentColor;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        float hoverProgress = getHoverProgress();
        
        // Calculate colors
        int bgAlpha = selected ? 200 : (int)(100 + hoverProgress * 80);
        int bgRed = selected ? ((accentColor >> 16) & 255) : (int)(20 + hoverProgress * 15);
        int bgGreen = selected ? ((accentColor >> 8) & 255) : (int)(20 + hoverProgress * 15);
        int bgBlue = selected ? (accentColor & 255) : (int)(20 + hoverProgress * 15);
        int bgColor = UIUtils.color(bgAlpha, bgRed, bgGreen, bgBlue);
        
        // Draw button background
        UIUtils.drawRoundedRect(x, y, width, height, CORNER_RADIUS, bgColor);
        
        // Draw accent border if selected or hovered
        if (selected || hoverProgress > 0.1F) {
            int borderAlpha = selected ? 255 : (int)(hoverProgress * 150);
            int borderColor = UIUtils.color(borderAlpha,
                (accentColor >> 16) & 255,
                (accentColor >> 8) & 255,
                accentColor & 255);
            UIUtils.drawRoundedRect(x, y, width, 2, CORNER_RADIUS, borderColor);
        }
        
        // Draw text (centered)
        int textWidth = font.getStringWidth(label);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - 8) / 2;
        
        int textColor = selected ? 0xFFFFFF : (hoverProgress > 0.5F ? 0xFFFFFF : 0xCCCCCC);
        font.drawString(label, textX, textY, textColor);
    }
    
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!visible || !isMouseOver(mouseX, mouseY)) return false;
        
        if (mouseButton == 0 && onClick != null) {
            onClick.run();
            Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.3F, 1.0F);
            return true;
        }
        
        return false;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public String getLabel() {
        return label;
    }
}

