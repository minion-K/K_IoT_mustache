package org.example.demo_ssr_v1._cors.utils;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MyDateUtil {
    // 정적 메서드 (기능) 시간 포맷터 기능
    private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");
    private static final ZoneId ZONE_UTC = ZoneOffset.UTC;

    private static final DateTimeFormatter KST_FORMAT
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String KstString(Timestamp utcTime) {
        if(utcTime == null) return null;

        return utcTime.toInstant()
                .atZone(ZONE_UTC)
                .withZoneSameInstant(ZONE_KST)
                .format(KST_FORMAT);
    }
}
