package com.duoku.platform.demo.single;

import android.content.Context;
import android.telephony.TelephonyManager;
public class PhoneUtil {

	public enum MNCType {
		ChinaMobile, ChinaUnicom, ChinaTelcom, UNKNOWN, OTHER
	}
	
	/**
	 * 是否为中国移动卡
	 * @param context
	 * @return
	 */
	public static boolean isChinaMobile(Context context){
		if(CHINA_MOBILE.equals(getDKSIMOperator(context))){
			return true;
		}
		return false;
	}

	/**
	 * 得到手机卡类型
	 * @param context 上下文对象
	 * @return
	 */
	public static synchronized String getDKSIMOperator(Context context){
		String strOperator = "";
		MNCType type = getMNCType(context);
		if(MNCType.ChinaMobile == type){
			strOperator = CHINA_MOBILE;
		} else if(MNCType.ChinaUnicom == type){
			strOperator = CHINA_UNICOM;
		} else if(MNCType.ChinaTelcom == type){
			strOperator = CHINA_TELCOM;
		}
		return strOperator;
	}
	
	/**
	 * 得到MNC类型
	 * @param pContext 上下文对象
	 * @return
	 */
	public static synchronized MNCType getMNCType(Context pContext) {
		TelephonyManager tempTelephonyManager = (TelephonyManager) pContext.getSystemService(Context.TELEPHONY_SERVICE);
		String strOperator = tempTelephonyManager.getSimOperator().trim();
		if (!isSimInserted(pContext)) {
			return MNCType.UNKNOWN;
		}
		if (strOperator.endsWith("00") || strOperator.endsWith("02") || strOperator.endsWith("07")) {
			return MNCType.ChinaMobile;
		} else if (strOperator.endsWith("01")) {
			return MNCType.ChinaUnicom;
		} else if (strOperator.endsWith("03") || strOperator.endsWith("99") || strOperator.endsWith("20404")) {
			return MNCType.ChinaTelcom;
		} else {
			return MNCType.UNKNOWN;
		}
	}
	
	/**
	 * 是否有sim卡
	 * @param context 上下文对象
	 * @return
	 */
	public static boolean isSimInserted(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		int status = tm.getSimState();
		if ((status == TelephonyManager.SIM_STATE_UNKNOWN) || (status == TelephonyManager.SIM_STATE_ABSENT)) {
			return false;
		}
		return true;
	}
	
	
	// MNC String
	public static final String CHINA_MOBILE = "cm";
	public static final String CHINA_UNICOM = "cu";
	public static final String CHINA_TELCOM = "ct";
	public static final String CHINA_OTHER = "other";
}
