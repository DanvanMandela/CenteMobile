package com.craft.silicon.centemobile.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.Arrays;

public class CardUtil {


    public static class CardWatcher implements TextWatcher {

        private static final String LOG_TAG = "AndroidExample";
        public static final char SEPARATOR = '-';

        private final EditText editText;

        private int after;
        private String beforeString;

        public CardWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            this.after = after;
            this.beforeString = s.toString();
            Log.e(LOG_TAG, "@@beforeTextChanged s=" + s
                    + " . start=" + start + " . after=" + after + " . count=" + count);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.e(LOG_TAG, "@@onTextChanged s=" + s
                    + " . start=" + start + " . before=" + before + " . count=" + count);
            String newText = s.toString();

            String textPrefix = newText.substring(0, start);
            String textInserted = newText.substring(start, start + this.after);
            String textSuffix = newText.substring(start + this.after);
            String textBeforeCursor = textPrefix + textInserted;

            // User delete the SEPARATOR.
            if (this.after == 0 && count == 0 && beforeString.charAt(start) == SEPARATOR) {
                if (start > 0) {
                    textPrefix = textPrefix.substring(0, textPrefix.length() - 1);
                }
            }

            // Non-digit
            String regex = "[^\\d]";

            String textPrefixClean = textPrefix.replaceAll(regex, "");
            String textInsertedClean = textInserted.replaceAll(regex, "");
            String textSuffixClean = textSuffix.replaceAll(regex, "");
            String textBeforeCursorClean = textPrefixClean + textInsertedClean;

            // creditCardNumber
            String newTextClean = textPrefixClean + textInsertedClean + textSuffixClean;

            CreditCardType creditCardType = this.showDetectedCreditCardImage(newTextClean);

            int[] blockLengths = CreditCardType.DEFAULT_BLOCK_LENGTHS; // {4,4,4,4,4}
            int minLength = 0;
            int maxLength = CreditCardType.DEFAULT_MAX_LENGTH; // 4*5

            if (creditCardType != null) {
                blockLengths = creditCardType.getBlockLengths();
                minLength = creditCardType.getMinLength();
                maxLength = creditCardType.getMaxLength();
            }
            Log.i(LOG_TAG, "newTextClean= " + newTextClean);


            int[] separatorIndexs = new int[blockLengths.length];
            for (int i = 0; i < separatorIndexs.length; i++) {
                if (i == 0) {
                    separatorIndexs[i] = blockLengths[i];
                } else {
                    separatorIndexs[i] = blockLengths[i] + separatorIndexs[i - 1];
                }
            }
            Log.i(LOG_TAG, "blockLengths= " + this.toString(blockLengths));
            Log.i(LOG_TAG, "separatorIndexs= " + this.toString(separatorIndexs));

            int cursorPosition = start + this.after - textBeforeCursor.length() + textBeforeCursorClean.length();

            StringBuilder sb = new StringBuilder();
            int separatorCount = 0;
            int cursorPositionDelta = 0;
            int LOOP_MAX = Math.min(newTextClean.length(), maxLength);

            for (int i = 0; i < LOOP_MAX; i++) {
                sb.append(newTextClean.charAt(i));

                if (this.contains(separatorIndexs, i + 1) && i < LOOP_MAX - 1) {
                    sb.append(SEPARATOR);
                    separatorCount++;
                    if (i < cursorPosition) {
                        cursorPositionDelta++;
                    }
                }
            }
            cursorPosition = cursorPosition + cursorPositionDelta;

            String textFormatted = sb.toString();
            if (cursorPosition > textFormatted.length()) {
                cursorPosition = textFormatted.length();
            }

            this.editText.removeTextChangedListener(this);
            this.editText.setText(textFormatted);
            this.editText.addTextChangedListener(this);
            this.editText.setSelection(cursorPosition);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        private String toString(int[] array) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                if (i == 0) {
                    sb.append("[").append(array[i]);
                } else {
                    sb.append(", ").append(array[i]);
                }
            }
            sb.append("]");
            return sb.toString();
        }

        private boolean contains(int[] values, int value) {
            for (int i = 0; i < values.length; i++) {
                if (values[i] == value) {
                    return true;
                }
            }
            return false;
        }

        private CreditCardType showDetectedCreditCardImage(String creditCardNumber) {
            CreditCardType type = CreditCardType.detect(creditCardNumber);
//            if (type != null) {
//                Drawable icon = ResourceUtils.getDrawableByName(this.editText.getContext(), type.getImageResourceName());
//                this.editText.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
//            } else {
//                Drawable icon = ResourceUtils.getDrawableByName(this.editText.getContext(), "icon_none");
//                this.editText.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
//            }
            return type;
        }
    }

    public enum CreditCardType {
        // 4
        VISA("Visa", "icon_visa",
                13, 19,
                new int[]{4},
                new int[]{4, 4, 4, 4, 4}
        ),
        // 4026, 417500, 4405, 4508, 4844, 4913, 4917
        VISA_ELECTRON("Visa Electron", "icon_visa_electron",
                16, 16,
                new int[]{4026, 417500, 4405, 4508, 4844, 4913, 4917},
                new int[]{4, 4, 4, 4}
        ),
        // 34, 37
        AMERICAN_EXPRESS("American Express", "icon_american_express",
                15, 15,
                new int[]{34, 37},
                new int[]{4, 6, 5}
        ),
        // 51‑55, 222100‑272099
        MASTERCARD("MasterCard", "icon_mastercard",
                16, 16,
                concat(intRange(51, 55), intRange(222100, 272099)),
                new int[]{4, 4, 4, 4}
        ),
        // 6011, 622126‑622925, 644‑649, 65
        DISCOVER("MasterCard", "icon_mastercard",
                16, 16,
                append(concat(intRange(622126, 622925), intRange(644, 649)), 6011, 65),
                new int[]{4, 4, 4, 4}
        ),
        // 3528‑3589
        JCB("JCB", "icon_jcb",
                16, 16,
                intRange(3528, 3589),
                new int[]{4, 4, 4, 4}
        ),
        // 1
        UATP("UATP", "icon_uatp",
                15, 15,
                new int[]{1},
                new int[]{4, 5, 6}
        ),
        // 5019
        DANKORT("Dankort", "icon_dankort",
                16, 16,
                new int[]{5019},
                new int[]{4, 4, 4, 4}
        );

        public static final int[] DEFAULT_BLOCK_LENGTHS = new int[]{4, 4, 4, 4, 4};
        public static final int DEFAULT_MAX_LENGTH = 4 * 5;

        private String[] prefixs;
        private int[] blockLengths;
        private String name;
        // Name of Image in "drawable" folder.
        private String imageResourceName;

        private int minLength;
        private int maxLength;

        CreditCardType(String name, String imageResourceName,
                       int minLength, int maxLength,
                       int[] intPrefixs, int[] blockLengths) {
            this.name = name;
            this.imageResourceName = imageResourceName;
            if (intPrefixs != null) {
                this.prefixs = new String[intPrefixs.length];
                for (int i = 0; i < intPrefixs.length; i++) {
                    this.prefixs[i] = String.valueOf(intPrefixs[i]);
                }
            }
            this.minLength = minLength;
            this.maxLength = maxLength;
            this.blockLengths = blockLengths;
        }

        public String getName() {
            return name;
        }

        public int getMinLength() {
            return minLength;
        }

        public int getMaxLength() {
            return maxLength;
        }

        public String[] getPrefixs() {
            return this.prefixs;
        }

        public int[] getBlockLengths() {
            return this.blockLengths;
        }

        public String getImageResourceName() {
            return this.imageResourceName;
        }

        private static int[] intRange(int from, int to) {
            int length = to - from + 1;
            int[] ret = new int[length];
            for (int i = from; i < to + 1; i++) {
                ret[i - from] = i;
            }
            return ret;
        }

        private static int[] concat(int[] first, int[] second) {
            int[] both = Arrays.copyOf(first, first.length + second.length);
            System.arraycopy(second, 0, both, first.length, second.length);
            return both;
        }

        private static int[] append(int[] first, int... value) {
            if (value == null || value.length == 0) {
                return first;
            }
            int[] both = Arrays.copyOf(first, first.length + value.length);

            for (int i = 0; i < value.length; i++) {
                both[first.length + i] = value[i];
            }
            return both;
        }

        public static CreditCardType detect(String creditCardNumber) {
            if (creditCardNumber == null || creditCardNumber.isEmpty()) {
                return null;
            }
            CreditCardType found = null;
            int max = 0;
            for (CreditCardType type : CreditCardType.values()) {
                for (String prefix : type.prefixs) {
                    if (creditCardNumber.startsWith(prefix) && prefix.length() > max) {
                        found = type;
                        max = prefix.length();
                    }
                }
            }
            return found;
        }

    }
}
