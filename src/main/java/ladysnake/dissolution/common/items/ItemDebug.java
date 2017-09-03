package ladysnake.dissolution.common.items;

import java.util.List;
import java.util.UUID;

import ladysnake.dissolution.client.particles.AdditiveParticle;
import ladysnake.dissolution.client.particles.DissolutionParticleManager;
import ladysnake.dissolution.client.renders.ShaderHelper;
import ladysnake.dissolution.common.blocks.ISoulInteractable;
import ladysnake.dissolution.common.capabilities.CapabilityIncorporealHandler;
import ladysnake.dissolution.common.entity.soul.EntitySoulCamera;
import ladysnake.dissolution.common.handlers.CustomDissolutionTeleporter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemDebug extends Item implements ISoulInteractable {
	
	protected int debugWanted = 0;

	public ItemDebug() {
		super();
        this.setMaxStackSize(1);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if(playerIn.isSneaking()) {
			if(!worldIn.isRemote) {
				debugWanted = (debugWanted + 1) % 8;
				playerIn.sendStatusMessage(new TextComponentString("debug: " + debugWanted), true);
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}
		switch(debugWanted) {
		case 0 : 
			CapabilityIncorporealHandler.getHandler(playerIn).setIncorporeal(!CapabilityIncorporealHandler.getHandler(playerIn).isIncorporeal());
			break;
		case 1 :	
			if(worldIn.isRemote) {
				ResourceLocation shader = new ResourceLocation("minecraft:shaders/post/intangible.json");
				if(Minecraft.getMinecraft().entityRenderer.isShaderActive())
					Minecraft.getMinecraft().entityRenderer.stopUseShader();
				else
					Minecraft.getMinecraft().entityRenderer.loadShader(shader);
			}
			break;
		case 2 :
			if(!playerIn.world.isRemote)
				CustomDissolutionTeleporter.transferPlayerToDimension((EntityPlayerMP) playerIn, playerIn.dimension == -1 ? 0 : -1);
			break;
		case 3 :
			if(!playerIn.world.isRemote) {
				worldIn.loadedEntityList.stream().filter(e -> e instanceof EntitySoulCamera).forEach(e -> e.onKillCommand());
				EntitySoulCamera cam = new EntitySoulCamera(playerIn);
				cam.setDest(new BlockPos(playerIn).add(5, 0, 0));
				worldIn.spawnEntity(cam);
			} 
			break;
		case 4 :
			if(!playerIn.world.isRemote) {
				playerIn.sendStatusMessage(new TextComponentString("Printing fire information"), true);
				List<Entity> fires = playerIn.world.getEntities(Entity.class, e -> e.getDistanceToEntity(playerIn) < 20);
				fires.forEach(System.out::println);
			}
			break;
		case 5 :
			if(playerIn.world.isRemote) {
				ShaderHelper.initShaders();
				playerIn.sendStatusMessage(new TextComponentString("Reloaded shaders"), false);
			}
			break;
		case 6 :
			if(playerIn.world.isRemote) {
				DissolutionParticleManager.INSTANCE.addParticle(new AdditiveParticle(playerIn, AdditiveParticle.PINK_STAR_PARTICLE_TEXTURE, true));
				// Minecraft.getMinecraft().effectRenderer.addEffect(new AdditiveParticle(playerIn.world, playerIn.posX, playerIn.posY + 2, playerIn.posZ+1, AdditiveParticle.PINK_STAR_PARTICLE_TEXTURE, true));
			}
			break;
		case 7 :
			if(!CapabilityIncorporealHandler.getHandler(playerIn).getDisguise().isPresent())
				CapabilityIncorporealHandler.getHandler(playerIn).setDisguise(UUID.fromString("70a33f8a-5fb0-46fd-b6fd-295130dcb464"));
			else
				CapabilityIncorporealHandler.getHandler(playerIn).setDisguise(null);
			break;
		default : break;
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
}
