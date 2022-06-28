package com.craft.silicon.centemobile.view.navigation;

import androidx.navigation.NavDirections;

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
}
