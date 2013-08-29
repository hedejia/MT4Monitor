package com.miteke.mt4.monitor.model;


public class CheckQuoteRule {

	public String ip="";
	public String port="";
	public String symbol="";
	public Integer checkPeriod = 10; //in seconds
	public Integer maxUnchangeTime = 5;
}
