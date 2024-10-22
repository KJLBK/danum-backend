package com.danum.danum.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class AddressParser {
    private static final Pattern CITY_PATTERN = Pattern.compile("^(서울|부산|대구|인천|광주|대전|울산|세종|경기|강원|충북|충남|전북|전남|경북|경남|제주)(특별시|광역시|특별자치시|도)?");
    private static final Pattern DISTRICT_PATTERN = Pattern.compile("(\\S+시 )?(\\S+구|\\S+군)");

    public static String parseAddress(String fullAddress) {
        return Optional.ofNullable(fullAddress)
                .filter(address -> !address.isEmpty())
                .map(AddressParser::parseValidAddress)
                .orElse("");
    }

    private static String parseValidAddress(String address) {
        List<String> addressParts = Arrays.asList(
                extractCity(address),
                extractDistrict(address)
        );

        return addressParts.stream()
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining(" "));
    }

    private static String extractCity(String address) {
        return Optional.of(address)
                .map(CITY_PATTERN::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .orElse("");
    }

    private static String extractDistrict(String address) {
        return Optional.of(address)
                .map(DISTRICT_PATTERN::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(2))
                .orElse("");
    }
}