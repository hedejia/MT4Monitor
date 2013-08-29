package com.miteke.mt4.monitor;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.miteke.mt4.monitor.model.CheckQuoteRule;

public class QuoteMonitor {
	
	private static Logger logger = Logger.getLogger(QuoteMonitor.class.getName());

	private static class SingletonHolder {
       static final QuoteMonitor uniqueInstance = new QuoteMonitor();
    }
    public static QuoteMonitor getInstance() {
       return SingletonHolder.uniqueInstance;
    }
    
    private final List<CheckQuoteThread> checkQuoteThreads = new ArrayList<CheckQuoteThread>();
    
	QuoteMonitor() {
		try {
			List<CheckQuoteRule> checkQuoteRules = SystemConf.getInstance()
					.getCheckQuoteRules();
			for (CheckQuoteRule rule : checkQuoteRules) {
				CheckQuoteThread cqt = new CheckQuoteThread(rule);
				checkQuoteThreads.add(cqt);
				cqt.start();
				Thread.sleep(111);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}
	}
    
	class CheckQuoteThread extends Thread {
		private CheckQuoteRule rule = null;
		private double lastAsk=0.0;
		private double lastBid=0.0;
		private int unchangedTimes=0;
		public CheckQuoteThread(CheckQuoteRule rule) {
			this.rule=rule;
		}
		public void run() {
			while (true) {
				try {
					
					String content = SocketHandler.sendData(this.rule.ip, Integer.valueOf(this.rule.port),
							"WQUOTES-" + this.rule.symbol +
							",\nQUIT\n");
					logger.info(this.rule.ip+":"+content);
					
					if (content.startsWith("up")||content.startsWith("down")) {
						content = content.substring(content.indexOf(' ')+1);
						String symbol = content.substring(0, content.indexOf(' '));
						
						if (!symbol.equals(this.rule.symbol)) {
							logger.error("Receive an unexpected quote response which has different symbol "
									+ symbol
									+ ", but it's supposed to be "
									+ this.rule.symbol);
						} else {
						
							content = content.substring(content.indexOf(' ')+1);
							double bid = Double.valueOf(content.substring(0, content.indexOf(' ')));
							
							content = content.substring(content.indexOf(' ')+1);
							double ask = Double.valueOf(content.substring(0, content.indexOf(' ')));
							
							if (bid==this.lastBid && ask==this.lastAsk) {
								unchangedTimes++;
							} else {
								unchangedTimes=0;
							}
							
							this.lastBid=bid;
							this.lastAsk=ask;
							
							if (unchangedTimes>this.rule.maxUnchangeTime) {
								DBHandler.getInstance().updateQuoteStatus(rule, false, ask, bid, false);
							} else if (unchangedTimes>0){
								DBHandler.getInstance().updateQuoteStatus(rule, false, ask, bid, true);
							} else {
								DBHandler.getInstance().updateQuoteStatus(rule, true, ask, bid, true);
							}
							
						}
					} else {
						DBHandler.getInstance().updateQuoteStatus(rule, false, 0.0, 0.0, false);
					}
					
					Thread.sleep(this.rule.checkPeriod*1000);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					logger.error(e.getMessage(),e);
					DBHandler.getInstance().updateQuoteStatus(rule, false, 0.0, 0.0, false);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error(e.getMessage(),e);
					DBHandler.getInstance().updateQuoteStatus(rule, false, 0.0, 0.0, false);
				} catch (InterruptedException e) {
					e.printStackTrace();
					logger.error(e.getMessage(),e);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage(),e);
				}
					
			}
			
		}
	}
}
