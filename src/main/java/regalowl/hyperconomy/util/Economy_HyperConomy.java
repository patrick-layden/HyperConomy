/* This file is part of Vault.

    Vault is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Vault is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */
package regalowl.hyperconomy.util;

import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.api.EconomyAPI;

public class Economy_HyperConomy implements Economy {
	private final String name = "HyperConomy";
	private HyperConomy hc;
	private EconomyAPI api;

	public Economy_HyperConomy() {
		hc = HyperConomy.hc;
		api = HyperConomy.economyApi;
	}

	public boolean isEnabled() {
		if (hc == null) {
			return false;
		} else {
			return hc.isEnabled();
		}
	}

	public String getName() {
		return name;
	}

	public double getBalance(String playerName) {
		return api.getBalance(playerName);
	}

	public boolean createPlayerAccount(String playerName) {
		return api.createAccount(playerName);
	}

	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		if (api.checkAccount(playerName)) {
			if (api.checkFunds(amount, playerName)) {
				api.withdrawAccount(amount, playerName);
				return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
			} else {
				return new EconomyResponse(0, 0, ResponseType.FAILURE, "Insufficient funds");
			}
		} else {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Account does not exist");
		}
	}

	public EconomyResponse depositPlayer(String playerName, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot deposit negative funds");
		}
		if (api.checkAccount(playerName)) {
			api.depositAccount(amount, playerName);
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
		} else {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Account does not exist");
		}
	}

	public String format(double amount) {
		return api.formatMoney(amount);
	}

	public String currencyNameSingular() {
		return api.currencyNamePlural();
	}

	public String currencyNamePlural() {
		return api.currencyName();
	}

	public boolean has(String playerName, double amount) {
		return api.checkFunds(amount, playerName);
	}

	public boolean hasAccount(String playerName) {
		return api.checkAccount(playerName);
	}

	public int fractionalDigits() {
		return api.fractionalDigits();
	}

    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

	public EconomyResponse createBank(String name, String player) {
		DataManager dm = hc.getDataManager();
		LanguageFile L = hc.getLanguageFile();
		if (dm.hasBank(name)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, L.get("BANK_ALREADY_EXISTS"));
		}
		if (!dm.hyperPlayerExists(player)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, L.get("PLAYER_NOT_FOUND"));
		}
		HyperPlayer hp = dm.getHyperPlayer(player);
		HyperBank hb = new HyperBank(name, hp);
		dm.addHyperBank(hb);
		return new EconomyResponse(0, hb.getBalance(), ResponseType.SUCCESS, "");
	}

	public EconomyResponse deleteBank(String name) {
		DataManager dm = hc.getDataManager();
		LanguageFile L = hc.getLanguageFile();
		if (!dm.hasBank(name)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, L.get("BANK_NOT_EXIST"));
		}
		HyperBank hb = dm.getHyperBank(name);
		hb.delete();
		return new EconomyResponse(0, hb.getBalance(), ResponseType.SUCCESS, "");
	}

	public EconomyResponse bankHas(String name, double amount) {
		DataManager dm = hc.getDataManager();
		LanguageFile L = hc.getLanguageFile();
		if (!dm.hasBank(name)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, L.get("BANK_NOT_EXIST"));
		}
		HyperBank hb = dm.getHyperBank(name);
		double balance = hb.getBalance();
		if (balance < amount) {
			return new EconomyResponse(0, balance, ResponseType.FAILURE, L.get("INSUFFICIENT_FUNDS"));
		} else {
			return new EconomyResponse(0, balance, ResponseType.SUCCESS, "");
		}
	}

	public EconomyResponse bankWithdraw(String name, double amount) {
		DataManager dm = hc.getDataManager();
		EconomyResponse er = bankHas(name, amount);
		if (!er.transactionSuccess()) {
			return er;
		} else {
			HyperBank hb = dm.getHyperBank(name);
			hb.setBalance(hb.getBalance() - amount);
			return new EconomyResponse(amount, hb.getBalance(), ResponseType.SUCCESS, "");
		}
	}

	public EconomyResponse bankDeposit(String name, double amount) {
		DataManager dm = hc.getDataManager();
		LanguageFile L = hc.getLanguageFile();
		if (!dm.hasBank(name)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, L.get("BANK_NOT_EXIST"));
		} else {
			HyperBank hb = dm.getHyperBank(name);
			hb.setBalance(hb.getBalance() + amount);
			return new EconomyResponse(amount, hb.getBalance(), ResponseType.SUCCESS, "");
		}
	}

	public EconomyResponse isBankOwner(String name, String playerName) {
		DataManager dm = hc.getDataManager();
		LanguageFile L = hc.getLanguageFile();
		if (!dm.hasBank(name)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, L.get("BANK_NOT_EXIST"));
		}
		if (!dm.hyperPlayerExists(name)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, L.get("PLAYER_NOT_FOUND"));
		}
		HyperBank hb = dm.getHyperBank(name);
		HyperPlayer hp = dm.getHyperPlayer(playerName);
		if (hb.isOwner(hp)) {
			return new EconomyResponse(0, hb.getBalance(), ResponseType.SUCCESS, "");
		} else {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, L.get("BANK_NOT_OWNER"));
		}
	}

	public EconomyResponse isBankMember(String name, String playerName) {
		DataManager dm = hc.getDataManager();
		LanguageFile L = hc.getLanguageFile();
		if (!dm.hasBank(name)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, L.get("BANK_NOT_EXIST"));
		}
		if (!dm.hyperPlayerExists(name)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, L.get("PLAYER_NOT_FOUND"));
		}
		HyperBank hb = dm.getHyperBank(name);
		HyperPlayer hp = dm.getHyperPlayer(playerName);
		if (hb.isMember(hp)) {
			return new EconomyResponse(0, hb.getBalance(), ResponseType.SUCCESS, "");
		} else {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, L.get("BANK_NOT_MEMBER"));
		}
	}

	public EconomyResponse bankBalance(String name) {
		DataManager dm = hc.getDataManager();
		LanguageFile L = hc.getLanguageFile();
		if (!dm.hasBank(name)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, L.get("BANK_NOT_EXIST"));
		}
		HyperBank hb = dm.getHyperBank(name);
		return new EconomyResponse(0, hb.getBalance(), ResponseType.SUCCESS, null);
	}

	public List<String> getBanks() {
		DataManager dm = hc.getDataManager();
		return dm.getHyperBankNames();
	}

	public boolean hasBankSupport() {
		return true;
	}
}