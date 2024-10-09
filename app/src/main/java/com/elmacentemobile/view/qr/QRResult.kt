package com.elmacentemobile.view.qr

sealed class QRResult {

  /**
   * MLKit successfully detected a QR code.
   *
   * @property content the wrapped MLKit response.
   */
  data class QRSuccess internal constructor(val content: QRContent) : QRResult()

  /**
   * Activity got cancelled by the user.
   */
  object QRUserCanceled : QRResult()

  /**
   * Camera permission was not granted.
   */
  object QRMissingPermission : QRResult()

  /**
   * Error while setting up CameraX or while MLKit analysis.
   *
   * @property exception the cause why the Activity was finished.
   */
  data class QRError internal constructor(val exception: Exception) : QRResult()
}