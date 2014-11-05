package regalowl.hyperconomy.command;

import java.util.ArrayList;
import java.util.Iterator;

public class Listcategories extends BaseCommand implements HyperCommand {


	public Listcategories() {
		super(false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			Iterator<String> it = hc.gYH().getFileConfiguration("categories").getTopLevelKeys().iterator();
			ArrayList<String> categories = new ArrayList<String>();
			while (it.hasNext()) {
				categories.add(it.next().toString());
			}
			data.addResponse("&b" + categories.toString());
			return data;
		} catch (Exception e) {
			data.addResponse(L.get("LISTCATEGORIES_INVALID"));
			return data;
		}
	}
}
