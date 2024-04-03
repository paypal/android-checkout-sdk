package com.paypal.checkoutsamples.paymentbutton

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.OrderRequest
import com.paypal.checkout.order.PurchaseUnit
import com.paypal.checkout.paymentbutton.PaymentButtonEligibilityStatus
import com.paypal.checkoutsamples.R
import kotlinx.android.synthetic.main.activity_payment_button_quick_start.*

class PaymentButtonQuickStartActivity : AppCompatActivity() {

    private val tag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_button_quick_start)
        paymentButton.onEligibilityStatusChanged = { buttonEligibilityStatus: PaymentButtonEligibilityStatus ->
            Log.v(tag, "OnEligibilityStatusChanged")
            Log.d(tag, "Button eligibility status: $buttonEligibilityStatus")
        }
        setupPaymentButton()
    }

    private fun setupPaymentButton() {
        paymentButton.setup(
            createOrder = CreateOrder { createOrderActions ->
                Log.v(tag, "CreateOrder")
                createOrderActions.create(
                    OrderRequest.Builder()
                        .appContext(
                            AppContext(
                                userAction = UserAction.PAY_NOW
                            )
                        )
                        .intent(OrderIntent.CAPTURE)
                        .purchaseUnitList(
                            listOf(
                                PurchaseUnit.Builder()
                                    .amount(
                                        Amount.Builder()
                                            .value("0.01")
                                            .currencyCode(CurrencyCode.USD)
                                            .build()
                                    )
                                    .build()
                            )
                        )
                        .build()
                        .also { Log.d(tag, "Order: $it") }
                )
            },
            onApprove = OnApprove { approval ->
                Log.v(tag, "OnApprove")
                Log.d(tag, "Approval details: $approval")
                approval.orderActions.capture { captureOrderResult ->
                    Log.v(tag, "Capture Order")
                    Log.d(tag, "Capture order result: $captureOrderResult")
                }
            },
            onCancel = OnCancel {
                Log.v(tag, "OnCancel")
                Log.d(tag, "Buyer cancelled the checkout experience.")
            },
            onError = OnError { errorInfo ->
                Log.v(tag, "OnError")
                Log.d(tag, "Error details: $errorInfo")
            },
            // This sample app has not obtained consent from the buyer to collect location data.
            // This flag enables PayPal to collect necessary information required for Fraud Detection and Risk Management
            hasUserLocationConsent = false
        )
    }

    companion object {
        fun startIntent(context: Context): Intent {
            return Intent(context, PaymentButtonQuickStartActivity::class.java)
        }
    }
}
