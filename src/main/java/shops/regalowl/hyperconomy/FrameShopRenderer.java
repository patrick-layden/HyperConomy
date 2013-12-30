package regalowl.hyperconomy;



import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import java.awt.Image;
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
    private final byte borderColor = MapPalette.DARK_BROWN;
    
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
		if (ho == null || canvas == null || p == null) {return;}

    	if (!renderedFor.contains(p.getName())) {
    		
    		//sets white default background
			for (int i=0;i<128;i++) {
				for (int j=0;j<128;j++) {
					canvas.setPixel(i, j, MapPalette.WHITE);
				}
			}
			
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
			
    		//clears transparent image pixels
			for (int i=0;i<128;i++) {
				for (int j=0;j<128;j++) {
					if (canvas.getPixel(i, j) == MapPalette.TRANSPARENT) {
						canvas.setPixel(i, j, MapPalette.WHITE);
					}
				}
			}
			
			for (int i=0;i<128;i++) {
				canvas.setPixel(i, 0, borderColor);
				canvas.setPixel(i, 1, borderColor);
				canvas.setPixel(i, 2, borderColor);
			}
			for (int i=0;i<128;i++) {
				canvas.setPixel(i, 127, borderColor);
				canvas.setPixel(i, 126, borderColor);
				canvas.setPixel(i, 125, borderColor);
			}
			for (int i=0;i<128;i++) {
				canvas.setPixel(0, i, borderColor);
				canvas.setPixel(1, i, borderColor);
				canvas.setPixel(2, i, borderColor);
			}
			for (int i=0;i<128;i++) {
				canvas.setPixel(127, i, borderColor);
				canvas.setPixel(126, i, borderColor);
				canvas.setPixel(125, i, borderColor);
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
