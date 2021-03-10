package com.paypal.checkoutsamples

import android.app.Application
import com.paypal.pyplcheckout.PayPalCheckout
import com.paypal.pyplcheckout.merchantIntegration.config.CheckoutConfigNew
import com.paypal.pyplcheckout.merchantIntegration.config.Environment

class QuickStartApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PayPalCheckout.setConfig(
            checkoutConfig = CheckoutConfigNew(
                application = this,
                clientId = PAYPAL_CLIENT_ID,
                environment = Environment.SANDBOX,
                returnUrl = "${BuildConfig.APPLICATION_ID}://paypalpay"
            )
        )
    }
}
