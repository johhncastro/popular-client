package com.pop.pvp;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Method;
import java.util.Random;

public class AutoClicker {
    private final ConfigManager configManager;
    private final Random random = new Random();
    
    private boolean leftMouseHeld = false;
    private long lastClickTime = 0;
    private double currentCPS = 0;
    private Method clickMouseMethod = null;
    
    public AutoClicker(ConfigManager configManager) {
        this.configManager = configManager;
        
        // Use reflection to access clickMouse() method
        try {
            clickMouseMethod = Minecraft.class.getDeclaredMethod("clickMouse");
            clickMouseMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            System.err.println("[Popular] Failed to find clickMouse method: " + e.getMessage());
        }
    }
    
    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        // Track left mouse button state (button 0 is left click)
        if (event.button == 0) {
            leftMouseHeld = event.buttonstate;
        }
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
        
        // Check if auto-clicker is enabled
        if (!configManager.getConfig().autoClickerEnabled) {
            return;
        }
        
        // Don't click if a GUI is open
        if (mc.currentScreen != null) {
            return;
        }
        
        // Check if left mouse button is held down
        // Also check LWJGL Mouse directly as a fallback
        boolean mouseHeld = leftMouseHeld || Mouse.isButtonDown(0);
        
        if (!mouseHeld) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Get CPS range from config
        double minCPS = configManager.getConfig().autoClickerMinCPS;
        double maxCPS = configManager.getConfig().autoClickerMaxCPS;
        
        // Calculate variable CPS (randomize between min-max from config)
        if (currentCPS == 0 || random.nextDouble() < 0.1) {
            // Randomize CPS every ~10 clicks or on first run
            currentCPS = minCPS + (maxCPS - minCPS) * random.nextDouble();
        }
        
        // Calculate delay in milliseconds based on CPS
        long delayMs = (long) (1000.0 / currentCPS);
        
        // Check if enough time has passed since last click
        if (currentTime - lastClickTime >= delayMs) {
            // Perform left mouse click using reflection
            if (clickMouseMethod != null) {
                try {
                    clickMouseMethod.invoke(mc);
                    lastClickTime = currentTime;
                } catch (Exception e) {
                    System.err.println("[Popular] Failed to invoke clickMouse: " + e.getMessage());
                }
            }
        }
    }
    
    public double getCurrentCPS() {
        return currentCPS;
    }
}

