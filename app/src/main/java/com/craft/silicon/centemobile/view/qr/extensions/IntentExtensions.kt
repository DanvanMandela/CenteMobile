package com.craft.silicon.centemobile.view.qr.extensions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.craft.silicon.centemobile.view.qr.*
import com.craft.silicon.centemobile.view.qr.CalendarEventParcelable
import com.craft.silicon.centemobile.view.qr.ContactInfoParcelable
import com.craft.silicon.centemobile.view.qr.GeoPointParcelable
import com.craft.silicon.centemobile.view.qr.WifiParcelable
import com.google.mlkit.vision.barcode.common.Barcode


internal fun Intent?.toQuickieContentType(): QRContent {
    val rawValue = this?.getStringExtra(EXTRA_RESULT_VALUE).orEmpty()
    return this?.toQuickieContentType(rawValue) ?: QRContent.Plain(rawValue)
}

@Suppress("LongMethod")
private fun Intent.toQuickieContentType(rawValue: String): QRContent? {
    return when (getIntExtra(EXTRA_RESULT_TYPE, Barcode.TYPE_UNKNOWN)) {
        Barcode.TYPE_CONTACT_INFO -> {
            getParcelableExtra<ContactInfoParcelable>(EXTRA_RESULT_PARCELABLE)?.run {
                QRContent.ContactInfo(
                    rawValue = rawValue,
                    addresses = addressParcelables.map { it.toAddress() },
                    emails = emailParcelables.map { it.toEmail(rawValue) },
                    name = nameParcelable.toPersonName(),
                    organization = organization,
                    phones = phoneParcelables.map { it.toPhone(rawValue) },
                    title = title,
                    urls = urls
                )
            }
        }
        Barcode.TYPE_EMAIL -> {
            getParcelableExtra<EmailParcelable>(EXTRA_RESULT_PARCELABLE)?.run {
                QRContent.Email(
                    rawValue = rawValue,
                    address = address,
                    body = body,
                    subject = subject,
                    type = QRContent.Email.EmailType.values()
                        .getOrElse(type) { QRContent.Email.EmailType.UNKNOWN }
                )
            }
        }
        Barcode.TYPE_PHONE -> {
            getParcelableExtra<PhoneParcelable>(EXTRA_RESULT_PARCELABLE)?.run {
                QRContent.Phone(
                    rawValue = rawValue,
                    number = number,
                    type = QRContent.Phone.PhoneType.values()
                        .getOrElse(type) { QRContent.Phone.PhoneType.UNKNOWN })
            }
        }
        Barcode.TYPE_SMS -> {
            getParcelableExtra<SmsParcelable>(EXTRA_RESULT_PARCELABLE)?.run {
                QRContent.Sms(rawValue = rawValue, message = message, phoneNumber = phoneNumber)
            }
        }
        Barcode.TYPE_URL -> {
            getParcelableExtra<UrlBookmarkParcelable>(EXTRA_RESULT_PARCELABLE)?.run {
                QRContent.Url(rawValue = rawValue, title = title, url = url)
            }
        }
        Barcode.TYPE_WIFI -> {
            getParcelableExtra<WifiParcelable>(EXTRA_RESULT_PARCELABLE)?.run {
                QRContent.Wifi(
                    rawValue = rawValue,
                    encryptionType = encryptionType,
                    password = password,
                    ssid = ssid
                )
            }
        }
        Barcode.TYPE_GEO -> {
            getParcelableExtra<GeoPointParcelable>(EXTRA_RESULT_PARCELABLE)?.run {
                QRContent.GeoPoint(rawValue = rawValue, lat = lat, lng = lng)
            }
        }
        Barcode.TYPE_CALENDAR_EVENT -> {
            getParcelableExtra<CalendarEventParcelable>(EXTRA_RESULT_PARCELABLE)?.run {
                QRContent.CalendarEvent(
                    rawValue = rawValue,
                    description = description,
                    end = end.toCalendarEvent(),
                    location = location,
                    organizer = organizer,
                    start = start.toCalendarEvent(),
                    status = status,
                    summary = summary
                )
            }
        }
        else -> null
    }
}

internal fun Intent?.getRootException(): Exception {
    this?.getSerializableExtra(EXTRA_RESULT_EXCEPTION).let {
        return if (it is Exception) it else IllegalStateException("Could retrieve root exception")
    }
}

private fun PhoneParcelable.toPhone(rawValue: String) =
    QRContent.Phone(
        rawValue = rawValue,
        number = number,
        type = QRContent.Phone.PhoneType.values()
            .getOrElse(type) { QRContent.Phone.PhoneType.UNKNOWN })

private fun EmailParcelable.toEmail(rawValue: String) =
    QRContent.Email(
        rawValue = rawValue,
        address = address,
        body = body,
        subject = subject,
        type = QRContent.Email.EmailType.values()
            .getOrElse(type) { QRContent.Email.EmailType.UNKNOWN }
    )

private fun AddressParcelable.toAddress() =
    QRContent.ContactInfo.Address(
        addressLines = addressLines,
        type = QRContent.ContactInfo.Address.AddressType.values()
            .getOrElse(type) { QRContent.ContactInfo.Address.AddressType.UNKNOWN })

private fun PersonNameParcelable.toPersonName() =
    QRContent.ContactInfo.PersonName(
        first = first,
        formattedName = formattedName,
        last = last,
        middle = middle,
        prefix = prefix,
        pronunciation = pronunciation,
        suffix = suffix
    )

private fun CalendarDateTimeParcelable.toCalendarEvent() =
    QRContent.CalendarEvent.CalendarDateTime(
        day = day,
        hours = hours,
        minutes = minutes,
        month = month,
        seconds = seconds,
        year = year,
        utc = utc
    )

const val EXTRA_RESULT_EXCEPTION = "exception"

const val EXTRA_CONFIG = "qr-config"
const val EXTRA_RESULT_VALUE = "qr-value"
const val EXTRA_RESULT_TYPE = "qr-type"
const val EXTRA_RESULT_PARCELABLE = "qr-parcelable"
const val RESULT_MISSING_PERMISSION = AppCompatActivity.RESULT_FIRST_USER + 1
const val RESULT_ERROR = AppCompatActivity.RESULT_FIRST_USER + 2