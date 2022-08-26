package com.craft.silicon.centemobile.view.navigation;

import androidx.navigation.NavDirections;

import com.craft.silicon.centemobile.data.model.StandingOrder;
import com.craft.silicon.centemobile.data.model.dynamic.TransactionData;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.view.dialog.MainDialogData;
import com.craft.silicon.centemobile.view.ep.controller.DisplayData;
import com.craft.silicon.centemobile.view.ep.data.DynamicData;
import com.craft.silicon.centemobile.view.ep.data.MiniList;
import com.craft.silicon.centemobile.view.ep.data.MiniStatement;

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

    default NavDirections navigateToOTP(String mobile) {
        return null;
    }

    default NavDirections navigateValidation() {
        return null;
    }

    default NavDirections navigatePurchase() {
        return null;
    }

    default NavDirections navigateGlobal() {
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

    default NavDirections navigateToLevelOne(DynamicData dynamicData) {
        return null;
    }

    default NavDirections navigateToLevelTwo(DynamicData dynamicData) {
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

    default NavDirections navigateToBeneficiary(Modules modules) {
        return null;
    }

    default NavDirections navigateToPendingTransaction(Modules modules) {
        return null;
    }

}
