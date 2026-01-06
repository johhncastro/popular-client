package com.pop.pvp.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Draggable panel component with title and content area.
 * Provides visual grouping and organization of UI elements.
 * 
 * Why: Panels create visual hierarchy and allow logical grouping of related settings.
 */
public class Panel extends Component {
    private String title;
    private List<Component> children;
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;
    private int accentColor;
    private Animation openAnimation;
    private boolean isOpen = true;
    
    // Visual properties
    private static final int CORNER_RADIUS = 8;
    private static final int SHADOW_SIZE = 4;
    private static final int PANEL_PADDING = 12;
    private static final int HEADER_HEIGHT = 24;
    
    public Panel(int x, int y, int width, int height, String title, int accentColor) {
        super(x, y, width, height);
        this.title = title;
        this.children = new ArrayList<Component>();
        this.accentColor = accentColor;
        this.openAnimation = new Animation(1.0F, 12.0F);
    }
    
    @Override
    public void update(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        
        super.update(mouseX, mouseY, partialTicks);
        openAnimation.update(partialTicks);
        
        // Update drag position
        if (dragging) {
            this.x = mouseX - dragOffsetX;
            this.y = mouseY - dragOffsetY;
        }
        
        // Update children
        float openProgress = openAnimation.getValue();
        if (openProgress > 0.01F) {
            for (Component child : children) {
                child.update(mouseX, mouseY, partialTicks);
            }
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        
        float openProgress = openAnimation.getValue();
        if (openProgress < 0.01F) return;
        
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        
        // Draw shadow
        UIUtils.drawShadow(x, y, width, height, CORNER_RADIUS, SHADOW_SIZE, 0.5F);
        
        // Draw panel background with rounded corners
        int bgColor = UIUtils.color(200, 20, 20, 20); // Dark semi-transparent
        UIUtils.drawRoundedRect(x, y, width, height, CORNER_RADIUS, bgColor);
        
        // Draw accent border (top edge)
        float hoverProgress = getHoverProgress();
        int borderAlpha = (int)(100 + hoverProgress * 50);
        int borderColor = UIUtils.color(borderAlpha, 
            (accentColor >> 16) & 255,
            (accentColor >> 8) & 255,
            accentColor & 255);
        UIUtils.drawRoundedRect(x, y, width, 2, CORNER_RADIUS, borderColor);
        
        // Draw header (draggable area)
        int headerY = y + 2;
        int headerBgColor = UIUtils.color(150, 30, 30, 30);
        UIUtils.drawRoundedRect(x, headerY, width, HEADER_HEIGHT, CORNER_RADIUS, headerBgColor);
        
        // Draw title
        int titleX = x + PANEL_PADDING;
        int titleY = headerY + (HEADER_HEIGHT - 8) / 2;
        font.drawString(
            EnumChatFormatting.BOLD + title,
            titleX,
            titleY,
            0xFFFFFF
        );
        
        // Draw children with fade-in animation
        if (openProgress > 0.1F) {
            float childAlpha = Math.min(1.0F, (openProgress - 0.1F) / 0.9F);
            int childY = y + HEADER_HEIGHT + PANEL_PADDING;
            
            for (Component child : children) {
                child.setPosition(x + PANEL_PADDING, childY);
                child.render(mouseX, mouseY, partialTicks);
                childY += child.getHeight() + 8; // Spacing between children
            }
        }
    }
    
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!visible || !isMouseOver(mouseX, mouseY)) return false;
        
        // Check if clicking on header (draggable area)
        int headerY = y + 2;
        if (mouseY >= headerY && mouseY <= headerY + HEADER_HEIGHT) {
            if (mouseButton == 0) { // Left click
                dragging = true;
                dragOffsetX = mouseX - x;
                dragOffsetY = mouseY - y;
                return true;
            }
        }
        
        // Check children
        for (Component child : children) {
            if (child.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        for (Component child : children) {
            child.mouseReleased(mouseX, mouseY, state);
        }
    }
    
    public void addChild(Component component) {
        children.add(component);
    }
    
    public void removeChild(Component component) {
        children.remove(component);
    }
    
    public void setOpen(boolean open) {
        isOpen = open;
        openAnimation.animateTo(open ? 1.0F : 0.0F);
    }
    
    public void toggle() {
        setOpen(!isOpen);
    }
    
    public void setAccentColor(int color) {
        this.accentColor = color;
    }
}

