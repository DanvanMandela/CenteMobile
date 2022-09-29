package com.elmacentemobile.data.repository.splash;

import androidx.navigation.NavDirections;

public interface SplashDataSource {
    default NavDirections getNavigation() {
        return null;
    }
}
