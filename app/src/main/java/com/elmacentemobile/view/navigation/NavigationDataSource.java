package com.elmacentemobile.view.navigation;

import androidx.navigation.NavDirections;

import com.elmacentemobile.data.model.StandingOrder;
import com.elmacentemobile.data.model.dynamic.TransactionData;
import com.elmacentemobile.data.model.module.Modules;
import com.elmacentemobile.view.dialog.DayTipData;
import com.elmacentemobile.view.dialog.MainDialogData;
import com.elmacentemobile.view.ep.controller.DisplayData;
import com.elmacentemobile.view.ep.data.ActivateData;
import com.elmacentemobile.view.ep.data.DynamicData;

public interface NavigationDataSource {
    default NavDirections navigateToLogin() {
        return null;
    }

    default NavDirections navigateToWelcome() {
        return null;
    }

    default NavDirections navigateToHome() {
        return null;
    }

    default NavDirections navigateAuth() {
        return null;
    }

    default NavDirections navigateLanding() {
        return null;
    }

    default NavDirections navigateLandingCompose() {
        return null;
    }
    default NavDirections navigateToOTP(ActivateData data) {
        return null;
    }


    default NavDirections navigateResetPinATM() {
        return null;
    }

    default NavDirections navigateSelfReg() {
        return null;
    }

    default NavDirections navigateCardDetails(String mobile) {
        return null;
    }

    default NavDirections navigateMap() {
        return null;
    }

    default NavDirections navigateOnTheGo() {
        return null;
    }

    default NavDirections navigateConnection() {
        return null;
    }

    default NavDirections navigateBottomSheet() {
        return null;
    }

    default NavDirections navigateReceipt(DynamicData data) {
        return null;
    }

    default NavDirections navigationBio() {
        return null;
    }

    default NavDirections navigateToLoading() {
        return null;
    }

    default NavDirections navigateToDisclaimer() {
        return null;
    }

    default NavDirections navigateToGlobalOtp() {
        return null;
    }

    default NavDirections navigateToNotification() {
        return null;
    }

    default NavDirections navigateToTransactionCenter(Modules modules) {
        return null;
    }

    default NavDirections navigateToTransactionDetails(TransactionData data) {
        return null;
    }

    default NavDirections navigateToPreResetPin() {
        return null;
    }

    default NavDirections navigateToAlertDialog(MainDialogData data) {
        return null;
    }

    default NavDirections navigateToSuccessDialog(MainDialogData data) {
        return null;
    }

    default NavDirections navigateToDisplayDialog(DisplayData data) {
        return null;
    }

    default NavDirections navigateToMini() {
        return null;
    }

    default NavDirections navigateToChangePin() {
        return null;
    }

    default NavDirections navigateToStandingDetails(StandingOrder standingOrder) {
        return null;
    }

    default NavDirections navigateToLogoutFeedBack() {
        return null;
    }

    default NavDirections navigateToBeneficiary(Modules modules) {
        return null;
    }


    default NavDirections navigateToDeviceRooted() {
        return null;
    }

    default NavDirections navigateToRejectTransaction() {
        return null;
    }

    default NavDirections navigateToTips(DayTipData data) {
        return null;
    }

    default NavDirections navigateToPolicy(){return null;}
}