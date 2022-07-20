package com.craft.silicon.centemobile.view.navigation;

import androidx.navigation.NavDirections;

import com.craft.silicon.centemobile.view.ep.data.DynamicData;

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

    default NavDirections navigateDynamic() {
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
}
