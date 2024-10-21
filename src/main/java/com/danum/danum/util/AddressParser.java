package com.danum.danum.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressParser {

    private static final Pattern CITY_PATTERN = Pattern.compile("^(서울|부산|대구|인천|광주|대전|울산|세종|경기|강원|충북|충남|전북|전남|경북|경남|제주)(특별시|광역시|특별자치시|도)?");
    private static final Pattern DISTRICT_PATTERN = Pattern.compile("(\\S+시 )?(\\S+구|\\S+군)");

    public static String parseAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.isEmpty()) {
            return "";
        }

        String city = extractCity(fullAddress);
        String district = extractDistrict(fullAddress);

        if (city.isEmpty() && district.isEmpty()) {
            return "";
        } else if (city.isEmpty()) {
            return district;
        } else if (district.isEmpty()) {
            return city;
        } else {
            return city + " " + district;
        }
    }

    private static String extractCity(String address) {
        Matcher matcher = CITY_PATTERN.matcher(address);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private static String extractDistrict(String address) {
        Matcher matcher = DISTRICT_PATTERN.matcher(address);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return "";
    }
}