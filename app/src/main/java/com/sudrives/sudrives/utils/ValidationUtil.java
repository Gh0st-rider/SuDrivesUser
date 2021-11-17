package com.sudrives.sudrives.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class for validating i/p
 *
 * @author kuldips
 */
public class ValidationUtil {
    private final static String NAME_PATTERN = "^[a-zA-Z\\s]*$";//  "^[a-zA-Z]+(\\s{1}[a-zA-Z]+)+[ ]?$";
    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final String USERNAME_PATTERN = "^[a-zA-Z\\\\s]{1,30}$";  //"^[a-zA-Z\\\\s]*$";// "^[a-zA-Z0-9_-]{1,30}$";

    private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9@.#$%^&*\\-_&\\\\]+$";//"(^[A-Za-z0-9]{5,20}$)";
    private static final String URL_PATTERN = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";



    private static final String DATE_PATTERN = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
    private static final String ZIP_PATTERN = "^[0-9]{5}$";//"[a-zA-Z\\d\\s\\-\\,\\#\\.\\+]+";
    private static final String MOBILE_PATTERN = "[1-9]{1}[0-9]{9}";

    private static final String ALPHANUMRIC_PATTERN = "[a-zA-Z0-9]+";

    /**
     * Validates email with regular expression
     *
     * @param email the email to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        email = email.toLowerCase();

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static boolean isValidPhone(String phone)
    {

        CharSequence inputString = phone;
        Pattern pattern = Pattern.compile(MOBILE_PATTERN);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches())
        {
            return true;
        }
        else{
            return false;
        }
    }

}
