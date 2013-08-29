package com.miteke.mt4.monitor;

import org.apache.log4j.Logger;

public class Scheduler {
	private static Logger logger = Logger.getLogger(Scheduler.class.getName());
	private int timeSlice=100;
	private int beat=timeSlice*5/2;

}
