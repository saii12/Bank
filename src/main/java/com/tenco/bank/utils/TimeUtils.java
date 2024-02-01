package com.tenco.bank.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TimeUtils {

	// Timestamp --> String으로 변경하는 코드 작성
	public static String timestampToString(Timestamp timestamp) {
		
		// yyyy-MM-dd HH:mm:ss
		// 패턴을 지정하여 SimpleDateFormat 객체 생성
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		// 포맷 적용하여 문자열로 변환하여 반환
		return sdf.format(timestamp);
	}
}
