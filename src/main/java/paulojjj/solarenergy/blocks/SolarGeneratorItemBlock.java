package paulojjj.solarenergy.blocks;

import java.util.List;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import paulojjj.solarenergy.EnergyFormatter;
import paulojjj.solarenergy.Main;
import paulojjj.solarenergy.ModCreativeTab;
import paulojjj.solarenergy.Tier;
import paulojjj.solarenergy.tiles.SolarGeneratorTileEntity;

public class SolarGeneratorItemBlock extends BlockItem {
	
	public SolarGeneratorItemBlock(Tier tier) {
		super(new SolarGenerator(tier), new Item.Properties().tab(ModCreativeTab.getInstance()));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		SolarGenerator block = (SolarGenerator)this.getBlock();
		SolarGeneratorTileEntity te = (SolarGeneratorTileEntity)block.newBlockEntity(new BlockPos(0, 0, 0), block.defaultBlockState());
		double production = te.getMaxProduction();
		String str = String.format("%s: %s/t", I18n.get(Main.MODID + ".produces"), EnergyFormatter.format(production));
		tooltip.add(new TextComponent(str));
	}

}
