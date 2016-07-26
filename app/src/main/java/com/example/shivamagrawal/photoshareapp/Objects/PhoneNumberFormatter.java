package com.example.shivamagrawal.photoshareapp.Objects;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class PhoneNumberFormatter {

    private static PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public static String formatOne(String number, String countryISO) {
        String formatted = null;
        try {
            formatted = phoneUtil.format(phoneUtil.parse(number,
                    countryISO.toUpperCase()), PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return formatted;
    }

}
