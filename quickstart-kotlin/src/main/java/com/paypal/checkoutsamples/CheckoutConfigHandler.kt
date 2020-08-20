package com.paypal.checkoutsamples

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.paypal.pyplcheckout.exception.CheckoutCancelReason
import com.paypal.pyplcheckout.interfaces.PayPalCheckoutCompleteListener
import com.paypal.pyplcheckout.merchantIntegration.CheckoutConfig
import com.paypal.pyplcheckout.merchantIntegration.RunTimeEnvironment

/**
 * CheckoutConfigHandler provides a way of reusing common [CheckoutConfig] setup code across several
 * sample activities. This is one example of what a native integration might look like and is not
 * necessarily the best option for all merchants/developers.
 */
interface CheckoutConfigHandler : PayPalCheckoutCompleteListener {
    val checkoutConfig: CheckoutConfig

    override fun onCheckoutCancelled(cancelReason: CheckoutCancelReason, reason: String)

    override fun onCheckoutComplete(params: HashMap<String, String>)

    companion object {
        operator fun invoke(supportFragmentManager: FragmentManager): CheckoutConfigHandler {
            return object : CheckoutConfigHandler {
                private val checkoutConfigHandler = this

                override val checkoutConfig: CheckoutConfig
                    get() {
                        return CheckoutConfig.getInstance()
                            /*
                             * Please see [QuickStartConstants], be sure to update the value of this constant prior
                             * to running this sample.
                             */
                            .apply { clientId = PAYPAL_CLIENT_ID }
                            /*
                             * Note: this will append ://paypalpay which will be used as the Return URL. In our case
                             * for this sample we set the following return url within the admin console on PayPal
                             * Developers: com.paypal.checkoutsamples://paypalpay
                             */
                            .apply { setMerchantRedirectUrlScheme(BuildConfig.APPLICATION_ID) }
                            /*
                             * For the purpose of this sample we are working within the Sandbox environment. Once
                             * you are ready to do a production release this should instead point to
                             * [RunTimeEnvironment.LIVE].
                             */
                            .apply { setPayPalEnvironment(RunTimeEnvironment.SANDBOX) }
                            /*
                             * While actively developing you can toggle debug flags on or off. It's recommended to
                             * disable this prior to releasing your app to production.
                             */
                            .apply { setIsDebug(true) }
                            /*
                             * [PayPalCheckoutCompleteListener] is required and will be invoked after the pay sheet
                             * is closed.
                             */
                            .apply { payPalCheckoutCompleteListener = checkoutConfigHandler }
                    }

                override fun onCheckoutCancelled(cancelReason: CheckoutCancelReason, reason: String) {
                    Log.i("CheckoutCancelled", reason)
                    showCheckoutComplete("Checkout Cancelled", "Reason: $reason")
                }

                override fun onCheckoutComplete(params: HashMap<String, String>) {
                    val formattedParameters = params.map { "${it.key} : ${it.value}" }
                        .joinToString(separator = "\n\n")
                    Log.i("CheckoutComplete", formattedParameters)
                    showCheckoutComplete("Checkout Complete", formattedParameters)
                }

                private fun showCheckoutComplete(resultsTitle: String, completionResults: String) {
                    CheckoutCompleteDialog.create(resultsTitle, completionResults)
                        .show(supportFragmentManager, "CheckoutCompleteDialog")
                }

            }
        }
    }
}
