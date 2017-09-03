package ladysnake.dissolution.common.blocks.powersystem;

import java.util.Random;

import ladysnake.dissolution.common.blocks.BlockSepulture;
import ladysnake.dissolution.common.init.ModItems;
import ladysnake.dissolution.common.tileentities.TileEntityModularMachine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCasing extends BlockBaseMachine {
	
	public static final PropertyEnum<EnumPartType> PART = PropertyEnum.create("part", EnumPartType.class);
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	
	public BlockCasing() {
		super();
		this.setDefaultState(this.blockState.getBaseState().withProperty(PART, EnumPartType.BOTTOM));
		this.setHardness(1f);
        this.setSoundType(SoundType.WOOD);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, PART, POWERED);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return state.getValue(PART) == EnumPartType.BOTTOM;
	}

	@Override
	public TileEntity createTileEntity(World worldIn, IBlockState state) {
		return new TileEntityModularMachine();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return state.getValue(PART) == EnumPartType.TOP ? Items.AIR : ModItems.WOODEN_CASING;
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(ModItems.WOODEN_CASING);
	}
	
	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state) {
		return EnumPushReaction.IGNORE;
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode && state.getValue(PART) == EnumPartType.TOP) {
			BlockPos blockpos = pos.down();

			if (worldIn.getBlockState(blockpos) == state.withProperty(PART, EnumPartType.BOTTOM)) {
				worldIn.setBlockToAir(blockpos);
			}
		}
	}
	
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getHorizontal(meta);
		return (meta & 8) > 0
				? this.getDefaultState().withProperty(PART, EnumPartType.TOP).withProperty(FACING,
						enumfacing)
				: this.getDefaultState().withProperty(PART, EnumPartType.BOTTOM).withProperty(FACING,
						enumfacing);
	}

	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = state.getValue(FACING).getHorizontalIndex();

		if (state.getValue(PART) == EnumPartType.TOP) {
			i |= 8;
		}

		return i;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (state.getValue(PART) == EnumPartType.TOP) {
			if (worldIn.getBlockState(pos.down()) != state.withProperty(PART, EnumPartType.BOTTOM)) {
				worldIn.setBlockToAir(pos);
			}
		} else if (worldIn.getBlockState(pos.up()) != state.withProperty(PART, EnumPartType.TOP)) {
			worldIn.setBlockToAir(pos);

			if (!worldIn.isRemote) {
				this.dropBlockAsItem(worldIn, pos, state, 0);
			}
		}
	}
	
	public static enum EnumPartType implements IStringSerializable
    {
        TOP,
        BOTTOM;

        public String getName()
        {
            return this.toString().toLowerCase();
        }
    }

}
