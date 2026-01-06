package com.pop.pvp;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Sprint mod that keeps sprint enabled when toggled on.
 */
public class Sprint {
    private final ConfigManager configManager;
    
    public Sprint(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        
        // Only work when in-game and window is focused
        if (!mc.inGameHasFocus) {
            return;
        }
        
        // Check if sprint mod is enabled
        if (!configManager.getConfig().sprintEnabled) {
            return;
        }
        
        // Don't sprint if a GUI is open
        if (mc.currentScreen != null) {
            return;
        }
        
        // Keep sprint enabled
        if (mc.thePlayer.movementInput.moveForward > 0.0F && !mc.thePlayer.isSneaking()) {
            mc.thePlayer.setSprinting(true);
        }
    }
}

