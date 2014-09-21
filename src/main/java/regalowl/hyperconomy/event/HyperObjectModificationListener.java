package regalowl.hyperconomy.event;

import regalowl.hyperconomy.hyperobject.HyperObject;

public interface HyperObjectModificationListener extends HyperListener {
	public void onHyperObjectModification(HyperObject ho);
}