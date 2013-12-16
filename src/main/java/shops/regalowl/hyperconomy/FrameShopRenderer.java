package regalowl.hyperconomy;



import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;















import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.bukkit.map.MapRenderer;

import regalowl.databukkit.CommonFunctions;





public class FrameShopRenderer extends MapRenderer {

	private HyperConomy hc;
	private HyperObject ho;
    private Image image;
    private CommonFunctions cf;
    private LanguageFile L;
    private ArrayList<String> renderedFor = new ArrayList<String>();
    
    public FrameShopRenderer(HyperObject ho) {
        super();
        hc = HyperConomy.hc;
        cf = hc.getCommonFunctions();
        L = hc.getLanguageFile();
        this.ho = ho;
        this.image = getImage();
    }
    
    public Image getImage() {
    	if (ho instanceof HyperItem) {
    		HyperItem hi = (HyperItem)ho;
    		URL url = null;
    		if (hi.getData() == 0) {
    			url = hc.getClass().getClassLoader().getResource("Images/"+hi.getMaterial().toLowerCase()+".png");
    		} else {
    			url = hc.getClass().getClassLoader().getResource("Images/"+hi.getMaterial().toLowerCase()+hi.getData()+".png");
    		}
            try {
            	Image unprocessedImage = ImageIO.read(url);
            	if (unprocessedImage != null) {
            		return unprocessedImage.getScaledInstance(60, 60, Image.SCALE_DEFAULT);
            	}
    		} catch (Exception e) {} 
    	}
    	return null;
    }
    
    @SuppressWarnings("deprecation")
	@Override
	public void render(MapView map, MapCanvas canvas, Player p) {
    	if (!renderedFor.contains(p.getName())) {
  
			int fHeight = MinecraftFont.Font.getHeight();
			
			canvas.drawText(7, fHeight, MinecraftFont.Font, applyMapColor(ho.getDisplayName(), MapPalette.RED));
			//canvas.drawText(8, fHeight + 10, MinecraftFont.Font, applyMapColor("Sell: Left Click", MapPalette.DARK_GRAY));
			//canvas.drawText(8, fHeight + 20, MinecraftFont.Font, applyMapColor("Buy: Right Click", MapPalette.DARK_GRAY));
			
			
			String sell = "";
			if (ho instanceof HyperEnchant) {
				HyperEnchant he = (HyperEnchant)ho;
				double value = he.getValue(EnchantmentClass.DIAMOND);
				sell = "Sell (L Click): " + L.fCS(cf.twoDecimals((value - ho.getSalesTaxEstimate(value))));
			} else if (ho instanceof HyperItem) {
				HyperItem hi = (HyperItem)ho;
				double value = hi.getValue(1);
				value = cf.twoDecimals((value - ho.getSalesTaxEstimate(value)));
				sell = "Sell (L Click): " + L.fCS(value);
			} else if (ho instanceof BasicObject) {
				BasicObject bo = (BasicObject)ho;
				double value = bo.getValue(1);
				value = cf.twoDecimals((value - ho.getSalesTaxEstimate(value)));
				sell = "Sell (L Click): " + L.fCS(value);
			}
			canvas.drawText(8, fHeight + 10, MinecraftFont.Font, applyMapColor(sell, MapPalette.LIGHT_GREEN));
			
			String buy = "";
			if (ho instanceof HyperEnchant) {
				HyperEnchant he = (HyperEnchant)ho;
				double cost = he.getCost(EnchantmentClass.DIAMOND);
				buy = "Buy (R Click): " + L.fCS(cf.twoDecimals((cost + ho.getPurchaseTax(cost))));
			} else if (ho instanceof HyperItem) {
				HyperItem hi = (HyperItem)ho;
				double pcost = hi.getCost(1);
				buy = "Buy (R Click): " + L.fCS(cf.twoDecimals((pcost + ho.getPurchaseTax(pcost))));
			} else if (ho instanceof BasicObject) {
				BasicObject bo = (BasicObject)ho;
				double pcost = bo.getCost(1);
				buy = "Buy (R Click): " + L.fCS(cf.twoDecimals((pcost + ho.getPurchaseTax(pcost))));
			}
			canvas.drawText(8, fHeight + 20, MinecraftFont.Font, applyMapColor(buy, MapPalette.LIGHT_GREEN));
			
			canvas.drawText(8, fHeight + 30, MinecraftFont.Font, applyMapColor("Stock: " + ho.getStock(), MapPalette.PALE_BLUE));
			
			
			
			if (image != null) {
				canvas.drawImage(68, 68, image);
			}
			renderedFor.add(p.getName());
			p.sendMap(map);
    	}
	}
    
    public void clearRendered() {
    	renderedFor.clear();
    }
    
    public String applyMapColor(String message, byte color) {
    	return L.get("CC") + color + ";" + message;
    }
}
