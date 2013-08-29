package com.miteke.mt4.monitor;

public class StartApp {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SystemConf.getInstance().initAllConfig();
		DBHandler.getInstance();
		if (SystemConf.getInstance().isTradeMonitorEnabled()) {
			TradeMonitor.getInstance();
		}
		if (SystemConf.getInstance().isQuoteMonitorEnabled()){
			QuoteMonitor.getInstance();
		}
		
	}
}
