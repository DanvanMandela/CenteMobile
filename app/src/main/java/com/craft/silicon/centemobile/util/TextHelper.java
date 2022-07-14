package com.craft.silicon.centemobile.util;

import android.text.Editable;

import com.craft.silicon.centemobile.R;

import butterknife.OnTextChanged;

public class TextHelper {
    public static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    public static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    public static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    public static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    public static final char CARD_NUMBER_DIVIDER = '-';

    public static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    public static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    public static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    public static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    public static final char CARD_DATE_DIVIDER = '/';

    public static final int CARD_CVC_TOTAL_SYMBOLS = 3;

    public TextHelper() {
    }

    @OnTextChanged(value = R.id.editATM, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardNumberTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
        }
    }


    protected void onCardDateTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));
        }
    }


    protected void onCardCVCTextChanged(Editable s) {
        if (s.length() > CARD_CVC_TOTAL_SYMBOLS) {
            s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length());
        }
    }

    public static boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    public static String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    public static char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }
}
