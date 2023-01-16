package com.elmacentemobile.view.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.elmacentemobile.R
import com.elmacentemobile.data.model.StandingOrder
import com.elmacentemobile.data.model.dynamic.TransactionData
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.view.dialog.DayTipData
import com.elmacentemobile.view.dialog.MainDialogData
import com.elmacentemobile.view.ep.controller.DisplayData
import com.elmacentemobile.view.ep.data.ActivateData
import com.elmacentemobile.view.ep.data.DynamicData
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
        return ActionOnlyNavDirections(
            if (Constants.Data.TEST) R.id.action_nav_land_compose
            else R.id.action_nav_land
        )
    }


    override fun navigateToDisclaimer(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_disclaimer)
    }

    override fun navigateToOTP(data: ActivateData?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_otp
            override val arguments: Bundle
                get() = mArguments()

            @Suppress("CAST_NEVER_SUCCEEDS")
            fun mArguments(): Bundle {
                val result = Bundle()
                if (Parcelable::class.java.isAssignableFrom(ActivateData::class.java)) {
                    result.putParcelable("mobile", data as Parcelable)
                } else if (Serializable::class.java.isAssignableFrom(ActivateData::class.java)) {
                    result.putSerializable("mobile", data as Serializable)
                }
                return result
            }
        }
    }


    override fun navigateResetPinATM(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_pin_atm)
    }

    override fun navigateSelfReg(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_self)
    }

    override fun navigateReceipt(data: DynamicData?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_receipt
            override val arguments: Bundle
                get() = mArguments()

            @Suppress("CAST_NEVER_SUCCEEDS")
            fun mArguments(): Bundle {
                val result = Bundle()
                if (Parcelable::class.java.isAssignableFrom(String::class.java)) {
                    result.putParcelable("receiptData", data as Parcelable)
                }
                return result
            }
        }
    }

    override fun navigateMap(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_map)
    }

    override fun navigateOnTheGo(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_on_the_go)
    }

    override fun navigateConnection(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_connection)
    }

    override fun navigateBottomSheet(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_bottom_map)
    }

    override fun navigateCardDetails(mobile: String?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_card_details
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

    override fun navigationBio(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_bio)
    }

    override fun navigateToLoading(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_loading)
    }

    override fun navigateToGlobalOtp(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_global_otp)
    }

    override fun navigateToNotification(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_notification)
    }


    override fun navigateToTransactionCenter(modules: Modules?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_transaction_center
            override val arguments: Bundle
                get() = mArguments()

            @Suppress("CAST_NEVER_SUCCEEDS")
            fun mArguments(): Bundle {
                val result = Bundle()
                if (Parcelable::class.java.isAssignableFrom(Modules::class.java)) {
                    result.putParcelable("module", modules as Parcelable)
                }
                return result
            }
        }
    }

    override fun navigateToTransactionDetails(data: TransactionData?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_transaction_details
            override val arguments: Bundle
                get() = mArguments()

            @Suppress("CAST_NEVER_SUCCEEDS")
            fun mArguments(): Bundle {
                val result = Bundle()
                if (Parcelable::class.java.isAssignableFrom(Modules::class.java)) {
                    result.putParcelable("transaction", data as Parcelable)
                }
                return result
            }
        }
    }

    override fun navigateToPreResetPin(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_pre_pin)
    }

    override fun navigateToAlertDialog(data: MainDialogData?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_alert
            override val arguments: Bundle
                get() = mArguments()

            @Suppress("CAST_NEVER_SUCCEEDS")
            fun mArguments(): Bundle {
                val result = Bundle()
                if (Parcelable::class.java.isAssignableFrom(MainDialogData::class.java)) {
                    result.putParcelable("data", data as Parcelable)
                }
                return result
            }
        }
    }

    override fun navigateToSuccessDialog(data: MainDialogData?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_success
            override val arguments: Bundle
                get() = mArguments()

            @Suppress("CAST_NEVER_SUCCEEDS")
            fun mArguments(): Bundle {
                val result = Bundle()
                if (Parcelable::class.java.isAssignableFrom(MainDialogData::class.java)) {
                    result.putParcelable("dialog", data as Parcelable)
                }
                return result
            }
        }
    }

    override fun navigateToDisplayDialog(data: DisplayData?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_display_dialog
            override val arguments: Bundle
                get() = mArguments()

            @Suppress("CAST_NEVER_SUCCEEDS")
            fun mArguments(): Bundle {
                val result = Bundle()
                if (Parcelable::class.java.isAssignableFrom(DisplayData::class.java)) {
                    result.putParcelable("data", data as Parcelable)
                }
                return result
            }
        }
    }

    override fun navigateToMini(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_mini)
    }

    override fun navigateToChangePin(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_change_pin)
    }

    override fun navigateToStandingDetails(standingOrder: StandingOrder?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_standing
            override val arguments: Bundle
                get() = mArguments()

            @Suppress("CAST_NEVER_SUCCEEDS")
            fun mArguments(): Bundle {
                val result = Bundle()
                if (Parcelable::class.java.isAssignableFrom(StandingOrder::class.java)) {
                    result.putParcelable("data", standingOrder as Parcelable)
                }
                return result
            }
        }
    }

    override fun navigateToBeneficiary(modules: Modules?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_beneficiary_management
            override val arguments: Bundle
                get() = mArguments()

            @Suppress("CAST_NEVER_SUCCEEDS")
            fun mArguments(): Bundle {
                val result = Bundle()
                if (Parcelable::class.java.isAssignableFrom(StandingOrder::class.java)) {
                    result.putParcelable("module", modules as Parcelable)
                }
                return result
            }
        }
    }


    override fun navigateToLogoutFeedBack(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_login_out_feedback)
    }

    override fun navigateToDeviceRooted(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_rooted)
    }

    override fun navigateToRejectTransaction(): NavDirections {
        return ActionOnlyNavDirections(R.id.action_nav_reject)
    }

    override fun navigateToTips(data: DayTipData?): NavDirections {
        return object : NavDirections {
            override val actionId: Int
                get() = R.id.action_nav_tips
            override val arguments: Bundle
                get() = mArguments()

            @Suppress("CAST_NEVER_SUCCEEDS")
            fun mArguments(): Bundle {
                val result = Bundle()
                if (Parcelable::class.java.isAssignableFrom(StandingOrder::class.java)) {
                    result.putParcelable("data", data as Parcelable)
                }
                return result
            }
        }
    }

    override fun navigateLandingCompose(): NavDirections {
        return ActionOnlyNavDirections(
            if (Constants.Data.TEST) R.id.action_nav_land_compose
            else R.id.action_nav_land
        )
    }


}