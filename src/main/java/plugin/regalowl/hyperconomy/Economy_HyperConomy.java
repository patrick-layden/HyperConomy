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
package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconAPI;

public class Economy_HyperConomy implements Economy {
	private final String name = "HyperConomy";
	private HyperConomy hc;
	private HyperEconAPI api;

	public Economy_HyperConomy() {
		hc = HyperConomy.hc;
		api = HyperConomy.hyperEconAPI;
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
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	public EconomyResponse isBankOwner(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	public EconomyResponse isBankMember(String name, String playerName) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "HyperConomy does not support bank accounts!");
	}

	public List<String> getBanks() {
		return new ArrayList<String>();
	}

	public boolean hasBankSupport() {
		return false;
	}
}