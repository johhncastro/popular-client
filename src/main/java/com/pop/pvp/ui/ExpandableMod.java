package com.pop.pvp.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Expandable mod component that shows settings when clicked.
 * Similar to Wurst/RusherHack expandable mods.
 */
public class ExpandableMod extends Component {
    private String label;
    private boolean value;
    private Runnable onToggle;
    private int accentColor;
    private boolean expanded = false;
    private Animation expandAnimation;
    private List<Component> settings;
    
    private static final int BASE_HEIGHT = 12;
    private static final int SETTING_SPACING = 6;
    
    public ExpandableMod(int x, int y, int width, String label, boolean initialValue, Runnable onToggle, int accentColor) {
        super(x, y, width, BASE_HEIGHT);
        this.label = label;
        this.value = initialValue;
        this.onToggle = onToggle;
        this.accentColor = accentColor;
        this.expandAnimation = new Animation(0.0F, 10.0F);
        this.settings = new ArrayList<Component>();
    }
    
    @Override
    public void update(int mouseX, int mouseY, float partialTicks) {
        super.update(mouseX, mouseY, partialTicks);
        expandAnimation.update(partialTicks);
        
        // Update settings if expanded
        float expandProgress = expandAnimation.getValue();
        if (expandProgress > 0.1F) {
            for (Component setting : settings) {
                setting.update(mouseX, mouseY, partialTicks);
            }
        }
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        float hoverProgress = getHoverProgress();
        float expandProgress = expandAnimation.getValue();
        
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
        
        // Draw expand/collapse indicator (> or v)
        String indicator = expanded ? "v" : ">";
        int indicatorX = x + font.getStringWidth(label) + 4;
        font.drawString(indicator, indicatorX, y, 0xAAAAAA);
        
        // Draw indicator dot if enabled
        if (value) {
            int dotSize = 3;
            int dotX = indicatorX + font.getStringWidth(indicator) + 4;
            int dotY = y + (BASE_HEIGHT - dotSize) / 2;
            int dotColor = UIUtils.color(255,
                (accentColor >> 16) & 255,
                (accentColor >> 8) & 255,
                accentColor & 255);
            UIUtils.drawRoundedRect(dotX, dotY, dotSize, dotSize, dotSize / 2, dotColor);
        }
        
        // Draw settings with animation
        if (expandProgress > 0.1F) {
            float settingsAlpha = Math.min(1.0F, (expandProgress - 0.1F) / 0.9F);
            int settingsY = y + BASE_HEIGHT + SETTING_SPACING;
            
            for (Component setting : settings) {
                setting.setPosition(x + 8, settingsY); // Indent settings
                setting.setSize(width - 16, setting.getHeight());
                setting.render(mouseX, mouseY, partialTicks);
                settingsY += setting.getHeight() + SETTING_SPACING;
            }
            
            // Update height based on expanded state
            int totalSettingsHeight = 0;
            for (Component setting : settings) {
                totalSettingsHeight += setting.getHeight() + SETTING_SPACING;
            }
            this.height = BASE_HEIGHT + (int)(totalSettingsHeight * expandProgress);
        } else {
            this.height = BASE_HEIGHT;
        }
    }
    
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!visible) return false;
        
        // Left click anywhere on the mod = toggle on/off
        if (mouseButton == 0 && mouseX >= x && mouseX <= x + width && 
            mouseY >= y && mouseY <= y + BASE_HEIGHT) {
            value = !value;
            if (onToggle != null) {
                onToggle.run();
            }
            Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.3F, 1.0F);
            return true;
        }
        
        // Right click on the label/indicator area = expand/collapse
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        int indicatorX = x + font.getStringWidth(label) + 4;
        
        if (mouseX >= x && mouseX <= indicatorX + 20 && mouseY >= y && mouseY <= y + BASE_HEIGHT) {
            if (mouseButton == 1) {
                expanded = !expanded;
                expandAnimation.animateTo(expanded ? 1.0F : 0.0F);
                Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.2F, 1.0F);
                return true;
            }
        }
        
        // Pass left clicks to settings if expanded
        if (expanded && mouseButton == 0) {
            for (Component setting : settings) {
                if (setting.mouseClicked(mouseX, mouseY, mouseButton)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (expanded) {
            for (Component setting : settings) {
                setting.mouseReleased(mouseX, mouseY, state);
            }
        }
    }
    
    public void addSetting(Component setting) {
        settings.add(setting);
    }
    
    public boolean getValue() {
        return value;
    }
    
    public void setValue(boolean value) {
        this.value = value;
    }
    
    public boolean isExpanded() {
        return expanded;
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        expandAnimation.animateTo(expanded ? 1.0F : 0.0F);
    }
}

