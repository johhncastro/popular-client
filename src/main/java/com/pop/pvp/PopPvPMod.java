package com.pop.pvp;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = PopPvPMod.MODID, name = PopPvPMod.NAME, version = PopPvPMod.VERSION, clientSideOnly = true)
public class PopPvPMod {
    public static final String MODID = "popular";
    public static final String NAME = "Popular";
    public static final String VERSION = "1.0";
    
    private ConfigManager configManager;
    private AutoClicker autoClicker;
    public static KeyBinding menuKeyBinding;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Load config
        configManager = new ConfigManager(event.getModConfigurationDirectory());
        configManager.loadConfig();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        // Register key binding for mod menu (Up Arrow key)
        menuKeyBinding = new KeyBinding("key.popular.menu", Keyboard.KEY_UP, "key.categories.gameplay");
        ClientRegistry.registerKeyBinding(menuKeyBinding);
        
        // Register this class to handle key input
        MinecraftForge.EVENT_BUS.register(this);
        
        // Initialize auto-clicker
        autoClicker = new AutoClicker(configManager);
        MinecraftForge.EVENT_BUS.register(autoClicker);
        
        // Initialize sprint mod
        Sprint sprint = new Sprint(configManager);
        MinecraftForge.EVENT_BUS.register(sprint);
        
        // Initialize chest ESP
        ChestESP chestESP = new ChestESP(configManager);
        MinecraftForge.EVENT_BUS.register(chestESP);
        
        // Initialize HUD overlay
        HUDOverlay hudOverlay = new HUDOverlay(configManager);
        MinecraftForge.EVENT_BUS.register(hudOverlay);
        
        // Initialize main menu splash text override
        MainMenuSplash mainMenuSplash = new MainMenuSplash();
        MinecraftForge.EVENT_BUS.register(mainMenuSplash);
        
        System.out.println("[Popular] Mod loaded successfully");
    }
    
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        // Open mod menu when Up Arrow is pressed
        if (menuKeyBinding.isPressed()) {
            net.minecraft.client.Minecraft.getMinecraft().displayGuiScreen(
                new ModMenuGUI(configManager)
            );
        }
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
}

