package regalowl.hyperconomy.event;

import regalowl.hyperconomy.account.HyperBank;

public interface HyperBankModificationListener extends HyperListener {
	public void onHyperBankModification(HyperBank hb);
}