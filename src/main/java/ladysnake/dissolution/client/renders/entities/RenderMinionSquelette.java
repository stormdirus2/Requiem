package ladysnake.dissolution.client.renders.entities;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import ladysnake.dissolution.client.model.ModelMinionSquelette;
import ladysnake.dissolution.common.entity.EntityMinionSquelette;
import ladysnake.dissolution.common.entity.EntityMinion;
import ladysnake.dissolution.common.entity.EntityWanderingSoul;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderMinionSquelette extends RenderBiped<EntityMinionSquelette> {

	private static final ResourceLocation SQUELETTE_TEXTURES = new ResourceLocation("tartaros:textures/entity/minions/minion_skeleton.png");
	private static final ResourceLocation STRAY_TEXTURES = new ResourceLocation("tartaros:textures/entity/minions/minion_stray.png");
	private static final DataParameter<Boolean> DEATH = EntityDataManager.<Boolean>createKey(EntityMinionSquelette.class, DataSerializers.BOOLEAN);
	private EntityDataManager dataManager;

    public static final Factory FACTORY = new Factory();

    public RenderMinionSquelette(RenderManager rendermanagerIn, boolean death) {
    		super(rendermanagerIn, new ModelMinionSquelette(), 0.5F);
    }

    @Override
    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull EntityMinionSquelette entity) {
    	if(entity.setStray()){
    		return STRAY_TEXTURES;
    	}
    	else
        	return SQUELETTE_TEXTURES;
    }

    public static class Factory implements IRenderFactory<EntityMinionSquelette> {

        @Override
        public Render<EntityMinionSquelette> createRenderFor(RenderManager manager) {
        	
        		return new RenderMinionSquelette(manager, true);	
 	
        }

    }
   
    
    @Override
    protected void preRenderCallback(EntityMinionSquelette minionIn, float partialTickTime)
    {
    	
        super.preRenderCallback(minionIn, partialTickTime);
    }
}
