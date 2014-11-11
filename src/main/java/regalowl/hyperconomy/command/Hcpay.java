package regalowl.hyperconomy.command;


public class Hcpay extends BaseCommand implements HyperCommand{
	
	public Hcpay() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length == 2) {
				String recipient = args[0];
				Double amount = Double.parseDouble(args[1]);
				if (amount <= 0) {
					data.addResponse(L.get("CANNOT_PAY_NEGATIVE"));
					return data;
				}
				if (dm.accountExists(recipient)) {
					if (hp.hasBalance(amount)) {
						hp.withdraw(amount);
						dm.getAccount(recipient).deposit(amount);
						data.addResponse(L.f(L.get("MONEY_PAYED"), amount, recipient));
					} else {
						data.addResponse(L.get("INSUFFICIENT_FUNDS"));
					}
				} else {
					data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
				}
			} else {
				data.addResponse(L.get("HCPAY_INVALID"));
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
		return data;
		
		
		
		
		
		
	}
}
