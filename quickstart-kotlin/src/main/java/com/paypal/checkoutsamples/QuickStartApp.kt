package com.paypal.checkoutsamples

import android.app.Application
import com.paypal.checkout.PayPalCheckout
import com.paypal.pyplcheckout.merchantIntegration.config.CheckoutConfig
import com.paypal.pyplcheckout.merchantIntegration.config.Environment
import com.paypal.pyplcheckout.merchantIntegration.config.SettingsConfig
import com.paypal.pyplcheckout.merchantIntegration.createorder.CurrencyCode
import com.paypal.pyplcheckout.merchantIntegration.createorder.UserAction

class QuickStartApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PayPalCheckout.setConfig(
            checkoutConfig = CheckoutConfig(
                application = this,
                clientId = PAYPAL_CLIENT_ID,
                environment = Environment.SANDBOX,
                returnUrl = "${BuildConfig.APPLICATION_ID}://paypalpay",
                currencyCode = CurrencyCode.USD,
                userAction = UserAction.PAY_NOW,
                settingsConfig = SettingsConfig(
                    loggingEnabled = true,
                    shouldFailEligibility = false
                )
            )
        )
    }
}
