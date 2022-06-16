package com.craft.silicon.centemobile.view.navigation

import androidx.navigation.NavDirections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationDirection @Inject constructor() : NavigationDataSource {

    override fun navigateToLogin(): NavDirections {
        return super.navigateToLogin()
    }

    override fun navigateToWelcome(): NavDirections {
        return super.navigateToWelcome()
    }

    override fun navigateToHome(): NavDirections {
        return super.navigateToHome()
    }
}