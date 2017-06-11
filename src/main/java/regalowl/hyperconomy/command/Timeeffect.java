package regalowl.hyperconomy.command;

import java.util.ArrayList;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.timeeffects.TimeEffect;
import regalowl.hyperconomy.timeeffects.TimeEffectType;
import regalowl.hyperconomy.timeeffects.TimeEffectsManager;
import regalowl.hyperconomy.tradeobject.TradeObject;


public class Timeeffect extends BaseCommand implements HyperCommand {
	public Timeeffect(HyperConomy hc) {
		super(hc, false);
	}

	

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			TimeEffectsManager tem = hc.getTimeEffectsManager();
			if (!hc.getConf().getBoolean("enable-feature.time-effects")) {
				data.addResponse(L.get("TIMEEFFECT_DISABLED"));
				return data;
			}
			if (args.length < 1) {
				data.addResponse(L.get("TIMEEFFECT_INVALID"));
				return data;
			}
			
			if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) {
				int pageEnd;
				if (args.length == 2) {
					pageEnd = Integer.parseInt(args[1]);
				} else if (args.length == 1) {
					pageEnd = 1;
				} else {
					data.addResponse(L.get("TIMEEFFECT_INVALID"));
					return data;
				}
				
				//TODO add section to new format LanguageFile once completed
				data.addResponse("Time Effects");
				ArrayList<TimeEffect> effects = tem.getTimeEffects();
				data.addResponse(L.f(L.get("TOP_BALANCE_PAGE"), pageEnd, (int)Math.ceil(effects.size()/8.0)));
				int pageStart = pageEnd - 1;
				pageStart *= 8;
				pageEnd *= 8;


				for (int i = pageStart; i < pageEnd; i++) {
					if (i > (effects.size() - 1)) {
						data.addResponse(L.get("REACHED_END"));
						return data;
					}
					
					TimeEffect te = effects.get(i);
					data.addResponse("&b" + te.getName() + "&9(" + te.getEconomy() + ")&e[" + effects.get(i).getType().toString().replace("_", " ") + "] &fFreq:&a" 
					+ secondsToReadable(effects.get(i).getSeconds()) + " &fInc:&a" + te.getIncrement() + " &fVal:&a" + te.getValue() + " &fRem:&a" + secondsToReadable(te.getTimeRemaining()));
				}
				return data;
			}
			
			HyperEconomy he = getEconomy();
			if (args.length < 2) {
				data.addResponse(L.get("TIMEEFFECT_INVALID"));
				return data;
			}
			boolean add;
			if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("a")) {
				add = true;
			} else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("r")) {
				add = false;
			} else {
				data.addResponse(L.get("TIMEEFFECT_INVALID"));
				return data;
			}

			if (args.length < 5 && add) {
				data.addResponse(L.get("TIMEEFFECT_INVALID"));
				return data;
			}
			String name = args[1];
			TimeEffectType type = TimeEffectType.fromString(args[2]);
			if (type == TimeEffectType.NONE) {
				data.addResponse(L.get("TIMEEFFECT_INVALID_TYPE"));
				return data;
			}
			if (type == TimeEffectType.BALANCE_STOCK || type == TimeEffectType.BALANCE_BALANCE) {
				if (args.length < 6 && add) {
					data.addResponse(L.get("TIMEEFFECT_INVALID"));
					return data;
				}
			}
			
			int frequency = 0;
			double increment = 0;
			double value = 0;
			
			if (add) {
				try {
					frequency = readableToSeconds(args[3]);
					if (frequency < 0) {
						data.addResponse(L.get("TIMEEFFECT_INVALID_INCREMENT"));
						return data;
					}
					increment = Double.parseDouble(args[4]);
					if (args.length >= 6) value = Double.parseDouble(args[5]);
				} catch (Exception e) {
					data.addResponse(L.get("TIMEEFFECT_INVALID"));
					return data;
				}
			}
			
			
			if (name.equalsIgnoreCase("all:objects")) {
				if (!TimeEffectType.isTradeObjectType(type)) {
					data.addResponse(L.get("TIMEEFFECT_INCOMPATIBLE_TYPE"));
					return data;
				}
				if (add) {
					for (TradeObject to:he.getTradeObjects()) {
						if (!tem.hasTimeEffect(to.getName(), he.getName(), type)) {
							TimeEffect te = new TimeEffect(hc, type, to.getName(), he.getName(), value, frequency, increment, frequency);
							tem.addNewTimeEffect(te);
						}
					}
					data.addResponse(L.get("TIMEEFFECT_ADDED"));
				} else {
					for (TradeObject to:he.getTradeObjects()) {
						TimeEffect te = tem.getTimeEffect(to.getName(), he.getName(), type);
						tem.deleteTimeEffect(te);
					}
					data.addResponse(L.get("TIMEEFFECT_REMOVED"));
				}
			} else if (name.equalsIgnoreCase("all:players")) {
				if (TimeEffectType.isTradeObjectType(type)) {
					data.addResponse(L.get("TIMEEFFECT_INCOMPATIBLE_TYPE"));
					return data;
				}
				if (add) {
					for (HyperAccount ha:hc.getHyperPlayerManager().getHyperPlayers()) {
						if (!tem.hasTimeEffect(ha.getName(), he.getName(), type)) {
							TimeEffect te = new TimeEffect(hc, type, ha.getName(), he.getName(), value, frequency, increment, frequency);
							tem.addNewTimeEffect(te);
						}
					}
					data.addResponse(L.get("TIMEEFFECT_ADDED"));
				} else {
					for (HyperAccount ha:hc.getHyperPlayerManager().getHyperPlayers()) {
						TimeEffect te = tem.getTimeEffect(ha.getName(), he.getName(), type);
						tem.deleteTimeEffect(te);
					}
					data.addResponse(L.get("TIMEEFFECT_REMOVED"));
				}
			} else if (name.equalsIgnoreCase("all:banks")) {
				if (TimeEffectType.isTradeObjectType(type)) {
					data.addResponse(L.get("TIMEEFFECT_INCOMPATIBLE_TYPE"));
					return data;
				}
				if (add) {
					for (HyperAccount ha:hc.getHyperBankManager().getHyperBanks()) {
						if (!tem.hasTimeEffect(ha.getName(), he.getName(), type)) {
							TimeEffect te = new TimeEffect(hc, type, ha.getName(), he.getName(), value, frequency, increment, frequency);
							tem.addNewTimeEffect(te);
						}
					}
					data.addResponse(L.get("TIMEEFFECT_ADDED"));
				} else {
					for (HyperAccount ha:hc.getHyperBankManager().getHyperBanks()) {
						TimeEffect te = tem.getTimeEffect(ha.getName(), he.getName(), type);
						tem.deleteTimeEffect(te);
					}
					data.addResponse(L.get("TIMEEFFECT_REMOVED"));
				}
			} else if (hc.getDataManager().categoryExists(name)) {
				if (!TimeEffectType.isTradeObjectType(type)) {
					data.addResponse(L.get("TIMEEFFECT_INCOMPATIBLE_TYPE"));
					return data;
				}
				if (add) {
					for (TradeObject to:he.getCategory(name)) {
						if (!tem.hasTimeEffect(to.getName(), he.getName(), type)) {
							TimeEffect te = new TimeEffect(hc, type, to.getName(), he.getName(), value, frequency, increment, frequency);
							tem.addNewTimeEffect(te);
						}
					}
					data.addResponse(L.get("TIMEEFFECT_ADDED"));
				} else {
					for (TradeObject to:he.getCategory(name)) {
						TimeEffect te = tem.getTimeEffect(to.getName(), he.getName(), type);
						tem.deleteTimeEffect(te);
					}
					data.addResponse(L.get("TIMEEFFECT_REMOVED"));
				}
			} else if (TimeEffectType.isTradeObjectType(type)) {
				TradeObject to = he.getTradeObject(name);
				if (to == null) {
					data.addResponse(L.get("OBJECT_NOT_FOUND"));
					return data;
				}
				if (add) {
					if (!tem.hasTimeEffect(to.getName(), he.getName(), type)) {
						TimeEffect te = new TimeEffect(hc, type, to.getName(), he.getName(), value, frequency, increment, frequency);
						tem.addNewTimeEffect(te);
						data.addResponse(L.get("TIMEEFFECT_ADDED"));
					} else {
						data.addResponse(L.get("TIMEEFFECT_ALREADY_EXISTS"));
					}
				} else {
					if (tem.hasTimeEffect(to.getName(), he.getName(), type)) {
						TimeEffect te = tem.getTimeEffect(to.getName(), he.getName(), type);
						tem.deleteTimeEffect(te);
						data.addResponse(L.get("TIMEEFFECT_REMOVED"));
					} else {
						data.addResponse(L.get("TIMEEFFECT_NOT_EXIST"));
					}
				}
			} else {
				HyperAccount account = hc.getDataManager().getAccount(name);
				if (account == null) {
					data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
					return data;
				}
				if (add) {
					if (!tem.hasTimeEffect(account.getName(), he.getName(), type)) {
						TimeEffect te = new TimeEffect(hc, type, account.getName(), he.getName(), value, frequency, increment, frequency);
						tem.addNewTimeEffect(te);
						data.addResponse(L.get("TIMEEFFECT_ADDED"));
					} else {
						data.addResponse(L.get("TIMEEFFECT_ALREADY_EXISTS"));
					}
				} else {
					if (tem.hasTimeEffect(account.getName(), he.getName(), type)) {
						TimeEffect te = tem.getTimeEffect(account.getName(), he.getName(), type);
						tem.deleteTimeEffect(te);
						data.addResponse(L.get("TIMEEFFECT_REMOVED"));
					} else {
						data.addResponse(L.get("TIMEEFFECT_NOT_EXIST"));
					}
				}
			}

		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
		return data;
	}
	
	private String secondsToReadable(int seconds) {
		int number;
		String letter;
		if (seconds % (60*60*24*365) == 0) {
			number = seconds/(60*60*24*365);
			letter = "y";
		} else if (seconds % (60*60*24*7) == 0) {
			number = seconds/(60*60*24*7);
			letter = "w";
		} else if (seconds % (60*60*24) == 0) {
			number = seconds/(60*60*24);
			letter = "d";
		} else if (seconds % (60*60) == 0) {
			number = seconds/(60*60);
			letter = "h";
		} else if (seconds % 60 == 0) {
			number = seconds/60;
			letter = "m";
		} else {
			number = seconds;
			letter = "s";
		}
		return number + letter;
	}
	
	private int readableToSeconds(String readable) {
		double seconds;
		String letter = readable.substring(readable.length() - 1, readable.length());
		Double number = Double.parseDouble(readable.substring(0, readable.length() - 1));
		if (letter.equalsIgnoreCase("s")) {
			seconds = number;
		} else if (letter.equalsIgnoreCase("m")) {
			seconds = number * 60;
		} else if (letter.equalsIgnoreCase("h")) {
			seconds = number * 60 * 60;
		} else if (letter.equalsIgnoreCase("d")) {
			seconds = number * 60 * 60 * 24;
		} else if (letter.equalsIgnoreCase("w")) {
			seconds = number * 60 * 60 * 24 * 7;
		} else if (letter.equalsIgnoreCase("y")) {
			seconds = number * 60 * 60 * 24 * 365;
		} else {
			seconds = -1;
		}
		return (int)seconds;
	}
	
}
