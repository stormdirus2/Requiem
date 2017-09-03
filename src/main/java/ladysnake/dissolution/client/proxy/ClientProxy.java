package ladysnake.dissolution.client.proxy;

import ladysnake.dissolution.client.gui.GuiIncorporealOverlay;
import ladysnake.dissolution.client.models.blocks.BakedModelLoader;
import ladysnake.dissolution.client.renders.blocks.RenderModularMachine;
import ladysnake.dissolution.client.renders.entities.LayerDisguise;
import ladysnake.dissolution.client.renders.entities.LayerScythe;
import ladysnake.dissolution.common.init.CommonProxy;
import ladysnake.dissolution.common.init.ModEntities;
import ladysnake.dissolution.common.tileentities.TileEntityModularMachine;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit() {
		super.preInit();
		ModEntities.registerRenders();
		ModelLoaderRegistry.registerLoader(new BakedModelLoader());
	}
	
	@Override
	public void init() {
		super.init();
		MinecraftForge.EVENT_BUS.register(new GuiIncorporealOverlay(Minecraft.getMinecraft()));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityModularMachine.class, new RenderModularMachine());
		initAddedLayers();
	}
	
    private static void initAddedLayers() {
    	Minecraft.getMinecraft().getRenderManager().getSkinMap().forEach((s, render) -> {
    			render.addLayer(new LayerScythe());
    			render.addLayer(new LayerDisguise(render, s.equals("slim")));
    		});
    }

}
