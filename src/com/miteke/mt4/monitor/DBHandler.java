package com.miteke.mt4.monitor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.miteke.mt4.monitor.model.CheckQuoteRule;
import com.miteke.mt4.monitor.model.CheckTradeRule;

public class DBHandler {
	
	private static Logger logger = Logger.getLogger(DBHandler.class.getName());
	
   // JDBC driver name and database URL
	static final String JDBC_DRIVER = SystemConf.getInstance().getDBProperty("JDBC_DRIVER");
	static final String DB_URL = SystemConf.getInstance().getDBProperty("DB_URL");

   //  Database credentials
	static final String USER = SystemConf.getInstance().getDBProperty("USER");
	static final String PASSWORD = SystemConf.getInstance().getDBProperty("PASSWORD");
	
	private Connection conn = null;
		

	private static class SingletonHolder {
       static final DBHandler uniqueInstance = new DBHandler();
    }
    public static DBHandler getInstance() {
       return SingletonHolder.uniqueInstance;
    }
    
    private boolean isRunning=false;
    public boolean isRunning() {
    	return isRunning;
    }
    
    DBHandler() {
 	   try{
  	      //STEP 1: Register JDBC driver
  	      Class.forName(JDBC_DRIVER);

  	      //STEP 2: Open a connection
  	      logger.info("Connecting to database...");
  	      conn = DriverManager.getConnection(DB_URL,USER,PASSWORD);
  	      logger.info("Database is connected!");
 	     

 	   }catch(SQLException se){
 	      //Handle errors for JDBC
 	      se.printStackTrace();
 	   }catch(Exception e){
 	      //Handle errors for Class.forName
 	      e.printStackTrace();
 	   }finally{
 	         if(conn!=null){
// 	        	 conn.close();
 	         }

 	   }//end try
 	}
    
	private void clearUp() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}
	}
	
	
	public List<CheckTradeRule> loadCheckTradeRules() {
		
		List<CheckTradeRule> rules= new ArrayList<CheckTradeRule>();
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select ip,port,login,password,api_version,symbol,check_period from T_TRADE_STATUS");
			
			while(rs.next()){
				CheckTradeRule rule = new CheckTradeRule();
				rule.ip = rs.getString("ip");
				rule.port = rs.getString("port");
				rule.login = rs.getString("login");
				rule.pwd = rs.getString("password");
				rule.apiVersion = rs.getString("api_version");
				rule.symbol = rs.getString("symbol");
				rule.checkPeriod = rs.getInt("check_period");
				rules.add(rule);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			return null;
		}
		return rules;
	}
	
	public boolean updateTradeStatus(CheckTradeRule rule, boolean status) {
		
		try {
			String sql = "update t_trade_status set status=?,last_check_time=? where ip=? and port=? and login=? and symbol=?";
			
			Timestamp now = new Timestamp((new Date()).getTime());
			PreparedStatement stmt = conn.prepareStatement(sql);
			int i=1;
			stmt.setString(i++,status?"1":"0");
			stmt.setTimestamp(i++,now);
			stmt.setString(i++,rule.ip);
			stmt.setString(i++,rule.port);
			stmt.setString(i++,rule.login);
			stmt.setString(i++,rule.symbol);
			
			int rt=stmt.executeUpdate();
			
			stmt.close();
			
			if (rt==0) {
				sql = "insert into t_trade_status(ip,port,api_version,login,password,symbol,check_period,status,last_check_time) values(?,?,?,?,?,?,?,?,?)";
				
				PreparedStatement stmtInsert = conn.prepareStatement(sql);
				int j=1;
				
				stmtInsert.setString(j++,rule.ip);
				stmtInsert.setString(j++,rule.port);
				stmtInsert.setString(j++,rule.apiVersion);
				stmtInsert.setString(j++,rule.login);
				stmtInsert.setString(j++,rule.pwd);
				stmtInsert.setString(j++,rule.symbol);
				stmtInsert.setInt(j++,rule.checkPeriod);
				stmtInsert.setString(j++,status?"1":"0");
				stmtInsert.setTimestamp(j++,now);
				
				stmtInsert.executeUpdate();
				stmtInsert.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			return false;
		}
		return true;
	}
	
	public List<CheckQuoteRule> loadCheckQuoteRules() {
		
		List<CheckQuoteRule> rules= new ArrayList<CheckQuoteRule>();
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select ip,port,symbol,check_period,max_unchange_time from T_QUOTE_STATUS");
			
			while(rs.next()){
				CheckQuoteRule rule = new CheckQuoteRule();
				rule.ip = rs.getString("ip");
				rule.port = rs.getString("port");
				rule.symbol = rs.getString("symbol");
				rule.checkPeriod = rs.getInt("check_period");
				rule.maxUnchangeTime = rs.getInt("max_unchange_time");
				rules.add(rule);
			}
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			return null;
		}
		return rules;
	}
	
	public boolean updateQuoteStatus(CheckQuoteRule rule, boolean changed, double ask, double bid, boolean status) {
		
		try {
			int effectedRowNum=0;
			Timestamp now = new Timestamp((new Date()).getTime());
			if (changed) {
				String sql = "update t_quote_status set status=?,last_check_time=?,last_check_ask=?,last_check_bid=?,last_quote_change=?" +
						"where ip=? and port=? and symbol=?";
				
				PreparedStatement stmt = conn.prepareStatement(sql);
				int i=1;
				stmt.setString(i++,status?"1":"0");
				stmt.setTimestamp(i++,now);
				stmt.setDouble(i++, ask);
				stmt.setDouble(i++, bid);
				stmt.setTimestamp(i++,now);
				stmt.setString(i++,rule.ip);
				stmt.setString(i++,rule.port);
				stmt.setString(i++,rule.symbol);
				effectedRowNum=stmt.executeUpdate();				
				stmt.close();
			} else {
				String sql = "update t_quote_status set status=?,last_check_time=?" +
						"where ip=? and port=? and symbol=?";
				
				PreparedStatement stmt = conn.prepareStatement(sql);
				int i=1;
				stmt.setString(i++,status?"1":"0");
				stmt.setTimestamp(i++,now);
				stmt.setString(i++,rule.ip);
				stmt.setString(i++,rule.port);
				stmt.setString(i++,rule.symbol);
				effectedRowNum=stmt.executeUpdate();
				stmt.close();
			}
			

			
			if (effectedRowNum==0) {
				String sql = "insert into t_quote_status(ip,port,symbol,check_period,max_unchange_time,status,last_check_time,last_check_ask,last_check_bid,last_quote_change) values(?,?,?,?,?,?,?,?,?,?)";
				
				PreparedStatement stmtInsert = conn.prepareStatement(sql);
				int j=1;
				
				stmtInsert.setString(j++,rule.ip);
				stmtInsert.setString(j++,rule.port);
				stmtInsert.setString(j++,rule.symbol);
				stmtInsert.setInt(j++,rule.checkPeriod);
				stmtInsert.setInt(j++,rule.maxUnchangeTime);
				stmtInsert.setString(j++,status?"1":"0");
				stmtInsert.setTimestamp(j++,now);
				stmtInsert.setDouble(j++, ask);
				stmtInsert.setDouble(j++, bid);
				stmtInsert.setTimestamp(j++,changed?now:null);
				
				stmtInsert.executeUpdate();
				stmtInsert.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			return false;
		}
		return true;
	}
	
}
