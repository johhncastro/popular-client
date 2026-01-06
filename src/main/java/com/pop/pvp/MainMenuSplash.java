package com.pop.pvp;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

import java.lang.reflect.Field;

/**
 * Overrides the main menu splash text to display "Popular Client WOW!"
 */
public class MainMenuSplash {
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiMainMenu) {
            try {
                // Use reflection to access the splashText field
                Field splashTextField = GuiMainMenu.class.getDeclaredField("splashText");
                splashTextField.setAccessible(true);
                
                // Set the splash text to our custom message
                splashTextField.set(event.gui, "Popular Client WOW!");
            } catch (NoSuchFieldException e) {
                // Field name might be different, try alternative names
                try {
                    Field field = GuiMainMenu.class.getDeclaredField("field_73975_c"); // Obfuscated name
                    field.setAccessible(true);
                    field.set(event.gui, "Popular Client WOW!");
                } catch (Exception ex) {
                    System.err.println("[Popular] Failed to set splash text: " + ex.getMessage());
                }
            } catch (Exception e) {
                System.err.println("[Popular] Failed to set splash text: " + e.getMessage());
            }
        }
    }
}

