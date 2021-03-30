package com.paypal.checkoutsamples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.paypal.checkoutsamples.order.OrdersQuickStartActivity
import com.paypal.checkoutsamples.paymentbutton.PaymentButtonQuickStartActivity
import com.paypal.checkoutsamples.token.TokenQuickStartActivity
import kotlinx.android.synthetic.main.activity_kotlin_quick_start.*

class KotlinQuickStartActivity : AppCompatActivity() {

    private val clientIdWasUpdated by lazy {
        PAYPAL_CLIENT_ID != "YOUR-CLIENT-ID-HERE"
    }

    private val secretWasUpdated by lazy {
        PAYPAL_SECRET != "ONLY-FOR-QUICKSTART-DO-NOT-INCLUDE-SECRET-IN-CLIENT-SIDE-APPLICATIONS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_quick_start)

        buyWithOrder.setOnClickListener {
            if (clientIdWasUpdated) {
                startActivity(OrdersQuickStartActivity.startIntent(this))
            } else {
                displayErrorSnackbar("Please Update PAYPAL_CLIENT_ID In QuickStartConstants.")
            }
        }

        buyWithOrderToken.setOnClickListener {
            if (clientIdWasUpdated && secretWasUpdated) {
                startActivity(TokenQuickStartActivity.startIntent(this))
            } else {
                displayErrorSnackbar("Please Update PAYPAL_CLIENT_ID and PAYPAL_SECRET In QuickStartConstants.")
            }
        }

        buyWithPaymentButton.setOnClickListener {
            if (clientIdWasUpdated) {
                startActivity(PaymentButtonQuickStartActivity.startIntent(this))
            } else {
                displayErrorSnackbar("Please Update PAYPAL_CLIENT_ID In QuickStartConstants.")
            }
        }
    }

    private fun displayErrorSnackbar(errorMessage: String) {
        Snackbar.make(rootQuickStart, errorMessage, Snackbar.LENGTH_INDEFINITE)
            .apply { setAction("Got It 👍") { dismiss() } }
            .show()
    }
}
