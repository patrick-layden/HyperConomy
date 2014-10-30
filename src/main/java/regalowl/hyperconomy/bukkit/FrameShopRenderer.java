package regalowl.hyperconomy.bukkit;



import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import java.awt.Image;
import java.util.ArrayList;
import org.bukkit.map.MapRenderer;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.util.LanguageFile;





public class FrameShopRenderer extends MapRenderer {

	private HyperConomy hc;
	private HyperObject ho;
    private Image image;
    private CommonFunctions cf;
    private LanguageFile L;
    private ArrayList<String> renderedFor = new ArrayList<String>();
    @SuppressWarnings("deprecation")
	private final byte borderColor = MapPalette.DARK_BROWN;
    
    public FrameShopRenderer(HyperObject ho) {
        super();
        hc = HyperConomy.hc;
        cf = hc.getCommonFunctions();
        L = hc.getLanguageFile();
        this.ho = ho;
        this.image = ho.getImage(60,60);
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
			
			
			//adds item name
			canvas.drawText(7, fHeight, MinecraftFont.Font, color(ho.getDisplayName(), MapPalette.BLUE));

			
			//adds sell price
			double value = 0.0;
			if (ho.getType() == HyperObjectType.ENCHANTMENT) {
				value = ho.getSellPrice(EnchantmentClass.DIAMOND);
			} else if (ho.getType() == HyperObjectType.ITEM) {
				value = ho.getSellPrice(1);
			} else {
				value = ho.getSellPrice(1);
			}
			String sell = color("Sell: ", MapPalette.DARK_GRAY) + color(L.fCS(cf.twoDecimals((value - ho.getSalesTaxEstimate(value)))), MapPalette.DARK_GREEN);
			canvas.drawText(8, fHeight + 10, MinecraftFont.Font, sell);
			
			
			//adds buy price
			double cost = 0.0;
			if (ho.getType() == HyperObjectType.ENCHANTMENT) {
				cost = ho.getBuyPrice(EnchantmentClass.DIAMOND);
			} else if (ho.getType() == HyperObjectType.ITEM) {
				cost = ho.getBuyPrice(1);
			} else {
				cost = ho.getBuyPrice(1);
			}
			String buy = color("Buy: ", MapPalette.DARK_GRAY) + color(L.fCS(cf.twoDecimals((cost + ho.getPurchaseTax(cost)))), MapPalette.DARK_GREEN);
			canvas.drawText(8, fHeight + 20, MinecraftFont.Font, buy);
			
			
			//adds stock info
			String stock = color("Stock: ", MapPalette.DARK_GRAY) + color(cf.twoDecimals(ho.getStock())+"", MapPalette.DARK_GREEN);
			canvas.drawText(8, fHeight + 30, MinecraftFont.Font, stock);
			
			
			//draws image if it exists
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
			//creates border
			for (int i=0;i<128;i++) {
				canvas.setPixel(i, 0, borderColor);
				canvas.setPixel(i, 1, borderColor);
				canvas.setPixel(i, 2, borderColor);
				canvas.setPixel(i, 3, borderColor);
			}
			for (int i=0;i<128;i++) {
				canvas.setPixel(i, 127, borderColor);
				canvas.setPixel(i, 126, borderColor);
				canvas.setPixel(i, 125, borderColor);
				canvas.setPixel(i, 124, borderColor);
			}
			for (int i=0;i<128;i++) {
				canvas.setPixel(0, i, borderColor);
				canvas.setPixel(1, i, borderColor);
				canvas.setPixel(2, i, borderColor);
				canvas.setPixel(3, i, borderColor);
			}
			for (int i=0;i<128;i++) {
				canvas.setPixel(127, i, borderColor);
				canvas.setPixel(126, i, borderColor);
				canvas.setPixel(125, i, borderColor);
				canvas.setPixel(124, i, borderColor);
			}
			
			renderedFor.add(p.getName());
			p.sendMap(map);
    	}
	}
    
    public void clearRendered() {
    	renderedFor.clear();
    }
    
    public String color(String message, byte color) {
    	return L.get("CC") + color + ";" + message;
    }
}
