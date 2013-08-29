package com.miteke.mt4.monitor;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.miteke.mt4.monitor.model.CheckTradeRule;

public class TradeMonitor {
	
	private static Logger logger = Logger.getLogger(TradeMonitor.class.getName());

	private static class SingletonHolder {
       static final TradeMonitor uniqueInstance = new TradeMonitor();
    }
    public static TradeMonitor getInstance() {
       return SingletonHolder.uniqueInstance;
    }
    
    private final List<CheckTradeThread> checkTradeThreads = new ArrayList<CheckTradeThread>();
    
	TradeMonitor() {
		try {
			List<CheckTradeRule> checkTradeRules = SystemConf.getInstance()
					.getCheckTradeRules();
			for (CheckTradeRule rule : checkTradeRules) {
				CheckTradeThread ctt = new CheckTradeThread(rule);
				checkTradeThreads.add(ctt);
				ctt.start();
				Thread.sleep(111);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}
	}
    
	class CheckTradeThread extends Thread {
		private CheckTradeRule rule = null;
		public CheckTradeThread(CheckTradeRule rule) {
			this.rule=rule;
		}
		public void run() {
			while (true) {
				try {
					String reqID=ChkReqIDMgr.generate20DigitsRequestID();
					String content = SocketHandler.sendData(this.rule.ip, Integer.valueOf(this.rule.port),
							"Wver=" + reqID +
							"&login=" + this.rule.login +
							"&webTrade=" + this.rule.apiVersion +
							"&cmd=0&volume=0.1&price=1" +
							"&symbol=" + this.rule.symbol +
							"&type=66\r\nQUIT\r\n");
					logger.info(this.rule.ip+":"+content);
					
					if (content.contains("status=0")) {
						DBHandler.getInstance().updateTradeStatus(rule, true);
						
						reqID=ChkReqIDMgr.generate20DigitsRequestID();
						
						String order=content.substring(content.indexOf("order")).substring(
								content.substring(content.indexOf("order")).indexOf('=')+1,
								content.substring(content.indexOf("order")).indexOf('&'));
						
						content = SocketHandler.sendData(this.rule.ip, Integer.valueOf(this.rule.port),
								"Wver=" + reqID +
								"&login=" + this.rule.login +
								"&webTrade=" + this.rule.apiVersion +
								"&order=" + order +
								"&cmd=1&volume=0.1&price=1" +
								"&symbol=" + this.rule.symbol +
								"&type=70\r\nQUIT\r\n");
						logger.info(this.rule.ip+":"+content);
					} else {
						DBHandler.getInstance().updateTradeStatus(rule, false);
					}
					
					Thread.sleep(this.rule.checkPeriod*1000);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					logger.error(e.getMessage(),e);
					DBHandler.getInstance().updateTradeStatus(rule, false);
				} catch (IOException e) {
					e.printStackTrace();
					logger.error(e.getMessage(),e);
					DBHandler.getInstance().updateTradeStatus(rule, false);
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
