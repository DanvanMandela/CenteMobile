package com.craft.silicon.centemobile.util.custom

import android.content.Context
import android.graphics.Rect
import android.text.InputType
import android.util.AttributeSet
import com.craft.silicon.centemobile.R
import com.google.android.material.textfield.TextInputEditText
import java.math.BigDecimal
import java.util.*

class CurrencyInput(
    context: Context,
    attrs: AttributeSet?
) : TextInputEditText(context, attrs) {
    private lateinit var currencySymbolPrefix: String
    private var textWatcher: CurrencyInputWatcher
    private var locale: Locale = Locale.getDefault()
    private var maxDP: Int

    init {
        var useCurrencySymbolAsHint = false
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        var localeTag: String?
        val prefix: String
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CurrencyInput,
            0, 0
        ).run {
            try {
                prefix = getString(R.styleable.CurrencyInput_currencySymbol).orEmpty()
                localeTag = getString(R.styleable.CurrencyInput_localeTag)
                useCurrencySymbolAsHint =
                    getBoolean(R.styleable.CurrencyInput_useCurrencySymbolAsHint, false)
                maxDP = getInt(R.styleable.CurrencyInput_maxNumberOfDecimalDigits, 2)
            } finally {
                recycle()
            }
        }
        currencySymbolPrefix = if (prefix.isBlank()) "" else "$prefix "
        if (useCurrencySymbolAsHint) hint = currencySymbolPrefix
        if (isLollipopAndAbove() && !localeTag.isNullOrBlank()) locale =
            getLocaleFromTag(localeTag!!)
        textWatcher = CurrencyInputWatcher(this, currencySymbolPrefix, locale, maxDP)
        addTextChangedListener(textWatcher)
    }

    fun setLocale(locale: Locale) {
        this.locale = locale
        invalidateTextWatcher()
    }

    fun setLocale(localeTag: String) {
        locale = getLocaleFromTag(localeTag)
        invalidateTextWatcher()
    }

    fun setCurrencySymbol(currencySymbol: String, useCurrencySymbolAsHint: Boolean = false) {
        currencySymbolPrefix = "$currencySymbol "
        if (useCurrencySymbolAsHint) hint = currencySymbolPrefix
        invalidateTextWatcher()
    }

    fun setMaxNumberOfDecimalDigits(maxDP: Int) {
        this.maxDP = maxDP
        invalidateTextWatcher()
    }

    private fun invalidateTextWatcher() {
        removeTextChangedListener(textWatcher)
        textWatcher = CurrencyInputWatcher(this, currencySymbolPrefix, locale, maxDP)
        addTextChangedListener(textWatcher)
    }

    fun getNumericValue(): Double {
        return parseMoneyValueWithLocale(
            locale,
            text.toString(),
            textWatcher.decimalFormatSymbols.groupingSeparator.toString(),
            currencySymbolPrefix
        ).toDouble()
    }

    fun getNumericValueBigDecimal(): BigDecimal {
        return BigDecimal(
            parseMoneyValueWithLocale(
                locale,
                text.toString(),
                textWatcher.decimalFormatSymbols.groupingSeparator.toString(),
                currencySymbolPrefix
            ).toString()
        )
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        getText()?.length?.let { setSelection(it) }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            removeTextChangedListener(textWatcher)
            addTextChangedListener(textWatcher)
            if (text.toString().isEmpty()) setText(currencySymbolPrefix)
        } else {
            removeTextChangedListener(textWatcher)
            if (text.toString() == currencySymbolPrefix) setText("")
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        if (::currencySymbolPrefix.isInitialized.not()) return
        val symbolLength = currencySymbolPrefix.length
        if (selEnd < symbolLength && text.toString().length >= symbolLength) {
            setSelection(symbolLength)
        } else {
            super.onSelectionChanged(selStart, selEnd)
        }
    }
}