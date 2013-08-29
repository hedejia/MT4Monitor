package com.miteke.mt4.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.miteke.mt4.monitor.model.CheckQuoteRule;
import com.miteke.mt4.monitor.model.CheckTradeRule;

public class SystemConf {
	private static Logger logger = Logger.getLogger(SystemConf.class.getName());
	
	private Properties prop4DB = new Properties();
	private String today = null;	
	
	private List<CheckTradeRule> checkTradeRules = new ArrayList<CheckTradeRule>();
	private List<CheckQuoteRule> checkQuoteRules = new ArrayList<CheckQuoteRule>();
	private boolean quoteMonitorEnabled = false;
	private boolean tradeMonitorEnabled = false;
	
	private static class SingletonHolder {
		static final SystemConf uniqueInstance = new SystemConf();
	}

	public static SystemConf getInstance() {
		return SingletonHolder.uniqueInstance;
	}

	private SystemConf() {

		try {
			String confFileName;
			
			confFileName = "cfg/log4j.properties";
			PropertyConfigurator.configureAndWatch(confFileName, 10000);
			
			confFileName = "cfg/db.properties";		
			prop4DB.load(new FileReader(confFileName));
		
			SimpleDateFormat dateFormatCompact = new SimpleDateFormat("yyyyMMdd");
			today = dateFormatCompact.format(new Date());

		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}
		

	}
	
	public void initAllConfig() {
		String confFileName;	
		
		confFileName = "cfg/TradeMonitor.properties";
		loadCheckTradeRulesConfig(confFileName);
		
		confFileName = "cfg/QuoteMonitor.properties";
		loadCheckQuoteRulesConfig(confFileName);
	}

	public String getDBProperty(String key) {
		return prop4DB.getProperty(key);
	}
	
	public List<CheckTradeRule> getCheckTradeRules() {
		return checkTradeRules;
	}
	
	public List<CheckQuoteRule> getCheckQuoteRules() {
		return checkQuoteRules;
	}
	
	public boolean isTradeMonitorEnabled() {
		return this.tradeMonitorEnabled;
	}
	
	public boolean isQuoteMonitorEnabled() {
		return this.quoteMonitorEnabled;
	}
	
	public String getToday() {
		return today;
	}
	
	public boolean loadCheckTradeRulesConfig(String confFileName) {
		boolean loadRulesFromDB = true;
		
	    File file=new File(confFileName);
	    if(!file.exists()) {
	    	logger.error("Config file "+confFileName+" doesn't exist.");
	    	return false;
	    }
	    try {
		    BufferedReader br=new BufferedReader(new FileReader(file));
		    
		    String line=null;
		    CheckTradeRule currTradeRule=null;
	    
		    while((line=br.readLine())!=null){
		    	
		    	if (!line.trim().isEmpty() && line.trim().charAt(0)!='#') {
		    		if (line.trim().toUpperCase().startsWith("ENABLED")) {
	    				String value = line.substring(line.indexOf('=') + 1).trim();
	    				if (value.equalsIgnoreCase("Y")) {
	    					tradeMonitorEnabled = true;
	    				} else {
	    					tradeMonitorEnabled = false;
	    					break;
	    				}
		    		} else if (line.trim().toUpperCase().startsWith("LOADRULEFROMDB")) {
	    				String value = line.substring(line.indexOf('=') + 1).trim();
	    				if (value.equalsIgnoreCase("Y")) {
	    					loadRulesFromDB = true;
	    					break;
	    				} else {
	    					loadRulesFromDB = false;
	    				}
		    		} else if (line.trim().equalsIgnoreCase("[Rule]")) {
		    			currTradeRule=new CheckTradeRule();
		    			checkTradeRules.add(currTradeRule);
		    		} else {
	    				String field = line.substring(0,line.indexOf('=')).trim();
	    				String value = line.substring(line.indexOf('=') + 1).trim();
	    				if (field.equalsIgnoreCase("IP")) {
							currTradeRule.ip = value;
	    				} else if (field.equalsIgnoreCase("Port")) {
	    					currTradeRule.port = value;
	    				} else if (field.equalsIgnoreCase("Login")) {
	    					currTradeRule.login = value;
	    				} else if (field.equalsIgnoreCase("Password")) {
	    					currTradeRule.pwd = value;
	    				} else if (field.equalsIgnoreCase("ApiVersion")) {
	    					currTradeRule.apiVersion = value;
	    				} else if (field.equalsIgnoreCase("Symbol")) {
	    					currTradeRule.symbol = value;
	    				} else if (field.equalsIgnoreCase("CheckPeriod")) {
	    					currTradeRule.checkPeriod = Integer.valueOf(value);
	    				} else {
	    					//should not be here
	    				}
		    			
		    		}
					
		    	}
		    }
		    
		    if (loadRulesFromDB && tradeMonitorEnabled) {
		    	checkTradeRules.clear();
		    	checkTradeRules=DBHandler.getInstance().loadCheckTradeRules();
		    }
		    
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}
		return false;
	}
	
	public boolean loadCheckQuoteRulesConfig(String confFileName) {
		boolean loadRulesFromDB = true;
		
	    File file=new File(confFileName);
	    if(!file.exists()) {
	    	logger.error("Config file "+confFileName+" doesn't exist.");
	    	return false;
	    }
	    try {
		    BufferedReader br=new BufferedReader(new FileReader(file));
		    
		    String line=null;
		    CheckQuoteRule currQuoteRule=null;
	    
		    while((line=br.readLine())!=null){
		    	
		    	if (!line.trim().isEmpty() && line.trim().charAt(0)!='#') {
		    		if (line.trim().toUpperCase().startsWith("ENABLED")) {
	    				String value = line.substring(line.indexOf('=') + 1).trim();
	    				if (value.equalsIgnoreCase("Y")) {
	    					quoteMonitorEnabled = true;
	    				} else {
	    					quoteMonitorEnabled = false;
	    					break;
	    				}
		    		} else if (line.trim().toUpperCase().startsWith("LOADRULEFROMDB")) {
	    				String value = line.substring(line.indexOf('=') + 1).trim();
	    				if (value.equalsIgnoreCase("Y")) {
	    					loadRulesFromDB = true;
	    					break;
	    				} else {
	    					loadRulesFromDB = false;
	    				}
		    		} else if (line.trim().equalsIgnoreCase("[Rule]")) {
		    			currQuoteRule=new CheckQuoteRule();
		    			checkQuoteRules.add(currQuoteRule);
		    		} else {
	    				String field = line.substring(0,line.indexOf('=')).trim();
	    				String value = line.substring(line.indexOf('=') + 1).trim();
	    				if (field.equalsIgnoreCase("IP")) {
							currQuoteRule.ip = value;
	    				} else if (field.equalsIgnoreCase("Port")) {
	    					currQuoteRule.port = value;
	    				} else if (field.equalsIgnoreCase("Symbol")) {
	    					currQuoteRule.symbol = value;
	    				} else if (field.equalsIgnoreCase("CheckPeriod")) {
	    					currQuoteRule.checkPeriod = Integer.valueOf(value);
	    				} else if (field.equalsIgnoreCase("MaxUnchangeTime")) {
	    					currQuoteRule.maxUnchangeTime = Integer.valueOf(value);
	    				} else {
	    					//should not be here
	    				}
		    			
		    		}
					
		    	}
		    }
		    
		    if (loadRulesFromDB && quoteMonitorEnabled) {
		    	checkQuoteRules.clear();
		    	checkQuoteRules=DBHandler.getInstance().loadCheckQuoteRules();
		    }
		    
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}
		return false;
	}

}
