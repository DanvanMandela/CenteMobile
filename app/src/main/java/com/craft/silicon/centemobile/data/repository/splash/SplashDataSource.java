package com.craft.silicon.centemobile.data.repository.splash;

import androidx.navigation.NavDirections;

public interface SplashDataSource {
    default NavDirections getNavigation() {
        return null;
    }
}
