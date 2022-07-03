package com.craft.silicon.centemobile.view.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.craft.silicon.centemobile.R
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationDirection @Inject constructor() : NavigationDataSource {

    override fun navigateToLogin(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_login)
    }

    override fun navigateToWelcome(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_home)
    }

    override fun navigateToHome(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_home)
    }

    override fun navigateAuth(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_auth)
    }

    override fun navigateLanding(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_land)
    }

    override fun navigateToOTP(mobile: String?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_otp
            override val arguments: Bundle
                get() = mArguments()

            @Suppress("CAST_NEVER_SUCCEEDS")
            fun mArguments(): Bundle {
                val result = Bundle()
                if (Parcelable::class.java.isAssignableFrom(String::class.java)) {
                    result.putParcelable("mobile", mobile as Parcelable)
                } else if (Serializable::class.java.isAssignableFrom(String::class.java)) {
                    result.putSerializable("mobile", mobile as Serializable)
                }
                return result
            }
        }
    }


    override fun navigateValidation(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_validation)
    }

    override fun navigateDynamic(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_dynamic)
    }

    override fun navigatePurchase(account: String?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_purchase
            override val arguments: Bundle
                get() = mArguments()

            @Suppress("CAST_NEVER_SUCCEEDS")
            fun mArguments(): Bundle {
                val result = Bundle()
                if (Parcelable::class.java.isAssignableFrom(String::class.java)) {
                    result.putParcelable("account", account as Parcelable)
                } else if (Serializable::class.java.isAssignableFrom(String::class.java)) {
                    result.putSerializable("account", account as Serializable)
                }
                return result
            }
        }
    }
}