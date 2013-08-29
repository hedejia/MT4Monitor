package com.miteke.mt4.monitor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChkReqIDMgr {
	
	private static Long reqSeq;
	private static String dateStr= "";//yyyyMMdd
	static {
		reqSeq=0L;
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		dateStr=df.format(new Date());
	}
	
	private static final String get6DigitLeftPad0Number(Long l) {
		l=l%1000000;
		String six0="000000";
		return six0.substring(l.toString().length())+l.toString();
	}
	
	public static synchronized String generate20DigitsRequestID () {
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
		
		String dateNowStr=df.format(new Date());

		if (!dateNowStr.startsWith(dateStr)) {
			reqSeq=0L;
			dateStr=dateNowStr.substring(0,8);
		}
		reqSeq++;
		return dateNowStr+get6DigitLeftPad0Number(reqSeq);
	}
}
