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
import com.paypal.checkout.order.Order
import com.paypal.checkout.order.PurchaseUnit
import com.paypal.checkout.paymentbutton.PaymentButtonEligibilityStatus
import com.paypal.checkoutsamples.R
import kotlinx.android.synthetic.main.activity_payment_button_quick_start.*

class PaymentButtonQuickStartActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_button_quick_start)
        paymentButton.onEligibilityStatusChanged = { buttonEligibilityStatus: PaymentButtonEligibilityStatus ->
            Log.v(TAG, "OnEligibilityStatusChanged")
            Log.d(TAG, "Button eligibility status: $buttonEligibilityStatus")
        }
        setupPaymentButton()
    }

    private fun setupPaymentButton() {
        paymentButton.setup(
            createOrder = CreateOrder { createOrderActions ->
                Log.v(TAG, "CreateOrder")
                createOrderActions.create(
                    Order.Builder()
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
                        .also { Log.d(TAG, "Order: $it") }
                )
            },
            onApprove = OnApprove { approval ->
                Log.v(TAG, "OnApprove")
                Log.d(TAG, "Approval details: $approval")
                approval.orderActions.capture { captureOrderResult ->
                    Log.v(TAG, "Capture Order")
                    Log.d(TAG, "Capture order result: $captureOrderResult")
                }
            },
            onCancel = OnCancel {
                Log.v(TAG, "OnCancel")
                Log.d(TAG, "Buyer cancelled the checkout experience.")
            },
            onError = OnError { errorInfo ->
                Log.v(TAG, "OnError")
                Log.d(TAG, "Error details: $errorInfo")
            }
        )
    }

    companion object {
        fun startIntent(context: Context): Intent {
            return Intent(context, PaymentButtonQuickStartActivity::class.java)
        }
    }
}
