package com.pop.pvp;

import com.pop.pvp.ui.UIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Clean HUD overlay displaying enabled mods in top-right corner.
 * Matches the modern design language of the mod menu.
 * 
 * Design Choices:
 * - Rounded corners create visual consistency with mod menu
 * - Semi-transparent background doesn't obstruct gameplay
 * - Right-aligned text follows natural reading flow
 * - Dynamic sizing adapts to content
 */
public class HUDOverlay {
    private final ConfigManager configManager;
    
    public HUDOverlay(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }
        
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        
        ScaledResolution scaled = new ScaledResolution(mc);
        int accentColor = configManager.getAccentColor();
        
        // Draw mod name and version in top-left if enabled
        if (configManager.getConfig().showVersion) {
            drawVersionDisplay(mc, scaled, accentColor);
        }
        
        // Get list of enabled mods
        List<String> enabledMods = getEnabledMods();
        
        if (enabledMods.isEmpty()) {
            return; // Don't show anything if no mods are enabled
        }
        
        int screenWidth = scaled.getScaledWidth();
        
        // Position in top right corner with padding (Wurst style)
        int padding = 8;
        int cornerRadius = 2; // Smaller radius for cleaner look
        int shadowSize = 2;
        int boxPadding = 8;
        int lineHeight = 11;
        
        // Calculate dimensions needed for the mod list
        int maxWidth = 0;
        int totalHeight = 0;
        
        for (String modName : enabledMods) {
            int width = mc.fontRendererObj.getStringWidth(modName);
            if (width > maxWidth) {
                maxWidth = width;
            }
            totalHeight += lineHeight;
        }
        
        // Add padding around the text
        int boxWidth = maxWidth + (boxPadding * 2);
        int boxHeight = totalHeight + (boxPadding * 2);
        
        // Calculate position (top-right, right-aligned)
        int boxX = screenWidth - boxWidth - padding;
        int boxY = padding;
        
        // Draw shadow for depth
        UIUtils.drawShadow(boxX, boxY, boxWidth, boxHeight, cornerRadius, shadowSize, 0.4F);
        
        // Draw background box (very dark - Wurst style)
        int bgColor = UIUtils.color(255, 10, 10, 10); // Almost black
        UIUtils.drawRoundedRect(boxX, boxY, boxWidth, boxHeight, cornerRadius, bgColor);
        
        // No border - clean look
        
        // Draw mod names (right-aligned) with accent color
        int currentY = boxY + boxPadding;
        for (String modName : enabledMods) {
            int textX = boxX + boxWidth - boxPadding - mc.fontRendererObj.getStringWidth(modName);
            // Use accent color (same blue as mod menu) instead of white
            int textColor = accentColor | 0xFF000000; // Ensure full opacity
            mc.fontRendererObj.drawString(modName, textX, currentY, textColor);
            currentY += lineHeight;
        }
    }
    
    private List<String> getEnabledMods() {
        List<String> enabledMods = new ArrayList<String>();
        
        // Check auto-clicker
        if (configManager.getConfig().autoClickerEnabled) {
            // Use accent color for enabled mods (same blue as mod menu)
            enabledMods.add("Auto-Clicker");
        }
        
        // Check sprint
        if (configManager.getConfig().sprintEnabled) {
            enabledMods.add("Sprint");
        }
        
        // Add more mods here as they are created
        // Example:
        // if (configManager.getConfig().someOtherModEnabled) {
        //     enabledMods.add("Some Other Mod");
        // }
        
        return enabledMods;
    }
    
    /**
     * Draws the mod name and version in the top-left corner of the screen.
     */
    private void drawVersionDisplay(Minecraft mc, ScaledResolution scaled, int accentColor) {
        int topLeftX = 10;
        int topLeftY = 10;
        
        String modName = "Popular";
        String modVersion = "v" + com.pop.pvp.PopPvPMod.VERSION;
        
        // Draw mod name
        mc.fontRendererObj.drawString(modName, topLeftX, topLeftY, 0xFFFFFF);
        
        // Draw version below name
        mc.fontRendererObj.drawString(modVersion, topLeftX, topLeftY + 12, 0xAAAAAA);
    }
}
