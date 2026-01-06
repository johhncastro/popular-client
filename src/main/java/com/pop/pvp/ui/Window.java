package com.pop.pvp.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Draggable window component for mod categories.
 * Similar to RusherHack/Meteor Client windows.
 * 
 * Why: Windows provide clear visual separation and organization.
 * Draggable windows allow users to customize their layout.
 */
public class Window extends Component {
    private String title;
    private List<Component> children;
    private boolean dragging = false;
    private boolean resizing = false;
    private int dragOffsetX, dragOffsetY;
    private int resizeOffsetX, resizeOffsetY;
    private int accentColor;
    private boolean minimized = false;
    private Animation minimizeAnimation;
    
    // Visual properties (Wurst/RusherHack style)
    private static final int CORNER_RADIUS = 2; // Smaller radius for cleaner look
    private static final int SHADOW_SIZE = 2;
    private static final int PADDING = 8;
    private static final int HEADER_HEIGHT = 16; // More compact header
    private static final int MINIMIZED_HEIGHT = HEADER_HEIGHT;
    private static final int RESIZE_HANDLE_SIZE = 8; // Size of resize corner
    private static final int MIN_WIDTH = 200; // Increased to fit slider text
    private static final int MIN_HEIGHT = 100;
    
    public Window(int x, int y, int width, int height, String title, int accentColor) {
        super(x, y, width, height);
        this.title = title;
        this.children = new ArrayList<Component>();
        this.accentColor = accentColor;
        this.minimizeAnimation = new Animation(1.0F, 10.0F);
    }
    
    @Override
    public void update(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        
        super.update(mouseX, mouseY, partialTicks);
        minimizeAnimation.update(partialTicks);
        
        // Update drag position
        if (dragging) {
            this.x = mouseX - dragOffsetX;
            this.y = mouseY - dragOffsetY;
            // Clamp to screen bounds
            ScaledResolution scaled = new ScaledResolution(Minecraft.getMinecraft());
            this.x = Math.max(0, Math.min(this.x, scaled.getScaledWidth() - width));
            this.y = Math.max(0, Math.min(this.y, scaled.getScaledHeight() - (minimized ? MINIMIZED_HEIGHT : height)));
        }
        
        // Update resize
        if (resizing) {
            int newWidth = mouseX - x + resizeOffsetX;
            int newHeight = mouseY - y + resizeOffsetY;
            // Clamp to minimum size
            this.width = Math.max(MIN_WIDTH, newWidth);
            this.height = Math.max(MIN_HEIGHT, newHeight);
            // Clamp to screen bounds
            ScaledResolution scaled = new ScaledResolution(Minecraft.getMinecraft());
            this.width = Math.min(this.width, scaled.getScaledWidth() - x);
            this.height = Math.min(this.height, scaled.getScaledHeight() - y);
        }
        
        // Update children if not minimized
        float minimizeProgress = minimizeAnimation.getValue();
        if (minimizeProgress > 0.1F) {
            for (Component child : children) {
                child.update(mouseX, mouseY, partialTicks);
            }
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        
        float minimizeProgress = minimizeAnimation.getValue();
        int currentHeight = (int)(height * minimizeProgress + MINIMIZED_HEIGHT * (1.0F - minimizeProgress));
        
        if (currentHeight < HEADER_HEIGHT) return;
        
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        
        // Draw shadow (subtle)
        UIUtils.drawShadow(x, y, width, currentHeight, CORNER_RADIUS, SHADOW_SIZE, 0.3F);
        
        // Draw window background (very dark, almost black - Wurst style)
        int bgColor = UIUtils.color(255, 10, 10, 10); // Almost black
        UIUtils.drawRoundedRect(x, y, width, currentHeight, CORNER_RADIUS, bgColor);
        
        // Draw accent border (1px border around entire window - RusherHack style)
        int borderColor = UIUtils.color(255,
            (accentColor >> 16) & 255,
            (accentColor >> 8) & 255,
            accentColor & 255);
        // Top border
        UIUtils.drawRoundedRect(x, y, width, 1, 0, borderColor);
        // Left border
        UIUtils.drawRoundedRect(x, y, 1, currentHeight, 0, borderColor);
        // Right border
        UIUtils.drawRoundedRect(x + width - 1, y, 1, currentHeight, 0, borderColor);
        // Bottom border
        UIUtils.drawRoundedRect(x, y + currentHeight - 1, width, 1, 0, borderColor);
        
        // Draw header (darker than content - Wurst style)
        int headerBgColor = UIUtils.color(255, 5, 5, 5); // Even darker
        UIUtils.drawRoundedRect(x, y, width, HEADER_HEIGHT, CORNER_RADIUS, headerBgColor);
        
        // Draw accent line under header
        UIUtils.drawRoundedRect(x, y + HEADER_HEIGHT - 1, width, 1, 0, borderColor);
        
        // Draw title (clean, no bold - Wurst style)
        int titleX = x + PADDING;
        int titleY = y + (HEADER_HEIGHT - 8) / 2;
        font.drawString(
            title,
            titleX,
            titleY,
            0xFFFFFF
        );
        
        // Draw minimize button (minimal style)
        int buttonSize = 10;
        int buttonX = x + width - PADDING - buttonSize;
        int buttonY = y + (HEADER_HEIGHT - buttonSize) / 2;
        boolean buttonHovered = isMouseOverButton(mouseX, mouseY, buttonX, buttonY, buttonSize);
        int buttonColor = buttonHovered ? 
            UIUtils.color(200, (accentColor >> 16) & 255, (accentColor >> 8) & 255, accentColor & 255) :
            UIUtils.color(150, 40, 40, 40);
        UIUtils.drawRoundedRect(buttonX, buttonY, buttonSize, buttonSize, 1, buttonColor);
        
        // Draw minimize icon (- or +)
        String icon = minimized ? "+" : "-";
        int iconWidth = font.getStringWidth(icon);
        int iconColor = buttonHovered ? 0xFFFFFF : 0xCCCCCC;
        font.drawString(icon, buttonX + (buttonSize - iconWidth) / 2, buttonY + 1, iconColor);
        
        // Draw children with fade animation
        if (minimizeProgress > 0.1F && currentHeight > HEADER_HEIGHT) {
            float childAlpha = Math.min(1.0F, (minimizeProgress - 0.1F) / 0.9F);
            int contentY = y + HEADER_HEIGHT + PADDING;
            int contentHeight = currentHeight - HEADER_HEIGHT - PADDING * 2;
            
            for (Component child : children) {
                if (contentY + child.getHeight() > y + currentHeight - PADDING) break;
                
                child.setPosition(x + PADDING, contentY);
                child.setSize(width - PADDING * 2, child.getHeight());
                child.render(mouseX, mouseY, partialTicks);
                contentY += child.getHeight() + 6; // Spacing between children
            }
        }
        
        // Draw resize handle (bottom-right corner) if not minimized
        if (!minimized && currentHeight > HEADER_HEIGHT) {
            int handleX = x + width - RESIZE_HANDLE_SIZE;
            int handleY = y + currentHeight - RESIZE_HANDLE_SIZE;
            boolean handleHovered = mouseX >= handleX && mouseX <= x + width &&
                                   mouseY >= handleY && mouseY <= y + currentHeight;
            
            // Draw resize handle (small square)
            int handleColor = handleHovered ? 
                UIUtils.color(200, (accentColor >> 16) & 255, (accentColor >> 8) & 255, accentColor & 255) :
                UIUtils.color(150, 50, 50, 50);
            UIUtils.drawRoundedRect(handleX, handleY, RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE, 1, handleColor);
        }
    }
    
    private boolean isMouseOverButton(int mouseX, int mouseY, int buttonX, int buttonY, int buttonSize) {
        return mouseX >= buttonX && mouseX <= buttonX + buttonSize && 
               mouseY >= buttonY && mouseY <= buttonY + buttonSize;
    }
    
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!visible || !isMouseOver(mouseX, mouseY)) return false;
        
        float minimizeProgress = minimizeAnimation.getValue();
        int currentHeight = (int)(height * minimizeProgress + MINIMIZED_HEIGHT * (1.0F - minimizeProgress));
        
        // Check resize handle (bottom-right corner)
        if (!minimized && currentHeight > HEADER_HEIGHT) {
            int handleX = x + width - RESIZE_HANDLE_SIZE;
            int handleY = y + currentHeight - RESIZE_HANDLE_SIZE;
            if (mouseX >= handleX && mouseX <= x + width &&
                mouseY >= handleY && mouseY <= y + currentHeight) {
                if (mouseButton == 0) {
                    resizing = true;
                    resizeOffsetX = x + width - mouseX;
                    resizeOffsetY = y + currentHeight - mouseY;
                    return true;
                }
            }
        }
        
        // Check minimize button
        int buttonSize = 10;
        int buttonX = x + width - PADDING - buttonSize;
        int buttonY = y + (HEADER_HEIGHT - buttonSize) / 2;
        
        if (isMouseOverButton(mouseX, mouseY, buttonX, buttonY, buttonSize)) {
            if (mouseButton == 0) {
                toggleMinimize();
                Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.2F, 1.0F);
                return true;
            }
        }
        
        // Check if clicking on header (draggable area)
        if (mouseY >= y && mouseY <= y + HEADER_HEIGHT) {
            if (mouseButton == 0) {
                dragging = true;
                dragOffsetX = mouseX - x;
                dragOffsetY = mouseY - y;
                return true;
            }
        }
        
        // Check children
        if (!minimized) {
            for (Component child : children) {
                if (child.mouseClicked(mouseX, mouseY, mouseButton)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        resizing = false;
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
    
    public void toggleMinimize() {
        minimized = !minimized;
        minimizeAnimation.animateTo(minimized ? 0.0F : 1.0F);
    }
    
    public void setMinimized(boolean minimized) {
        this.minimized = minimized;
        minimizeAnimation.animateTo(minimized ? 0.0F : 1.0F);
    }
    
    public boolean isMinimized() {
        return minimized;
    }
    
    public void setAccentColor(int color) {
        this.accentColor = color;
    }
    
    public String getTitle() {
        return title;
    }
}

