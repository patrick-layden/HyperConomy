package regalowl.hyperconomy.command;



import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.LanguageFile;

public class BaseCommand {

	protected boolean requirePlayer;

	protected HyperConomy hc;
	protected LanguageFile L;
	protected DataManager dm;

	
	protected HyperPlayer hp;
	protected boolean isPlayer;
	protected String[] args;
	protected CommandData data;
	
	public BaseCommand(HyperConomy hc, boolean requirePlayer) {
		this.hc = hc;
		this.L = hc.getLanguageFile();
		this.dm = hc.getDataManager();
		this.requirePlayer = requirePlayer;
	}
	
	
	protected HyperEconomy getEconomy() {
		return hc.getDataManager().getEconomy(getEconomyName());
	}
	
	protected String getEconomyName() {
		if (isPlayer && hp != null) {
			return hp.getEconomy();
		} else if (!isPlayer) {
			return hc.getConsoleEconomy();
		}
		return "default";
	}
	
	protected boolean validate(CommandData data) {
		this.data = data;
		this.args = data.getArgs();
		this.isPlayer = data.isPlayer();
		this.hp = data.getHyperPlayer();
		if (requirePlayer && !isPlayer) {
			data.addResponse(hc.getLanguageFile().get("MUST_BE_PLAYER"));
			return false;
		}
		if (hc.getHyperLock().isLocked(hp)) {
			hc.getHyperLock().sendLockMessage(data);
			return false;
		}
		return true;
	}
	

}
