package regalowl.hyperconomy.event;

import regalowl.hyperconomy.account.HyperPlayer;

public interface HyperPlayerModificationListener extends HyperListener {
	public void onHyperPlayerModification(HyperPlayer hp);
}