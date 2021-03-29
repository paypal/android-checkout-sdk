package com.paypal.checkoutsamples.order

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.ShippingPreference
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.AuthorizeOrderResult
import com.paypal.checkout.order.CaptureOrderResult
import com.paypal.checkoutsamples.R
import com.paypal.checkoutsamples.order.usecase.CreateOrderRequest
import com.paypal.checkoutsamples.order.usecase.CreateOrderUseCase
import kotlinx.android.synthetic.main.activity_orders_quick_start.*
import kotlinx.android.synthetic.main.item_preview_item.view.*

class OrdersQuickStartActivity : AppCompatActivity() {

    private val tag = javaClass.simpleName

    private val checkoutSdk: PayPalCheckout
        get() = PayPalCheckout

    private val selectedUserAction: UserAction
        get() {
            return when (val selectedId = selectUserAction.checkedRadioButtonId) {
                R.id.userActionOptionContinue -> UserAction.CONTINUE
                R.id.userActionOptionPayNow -> UserAction.PAY_NOW
                else -> {
                    throw IllegalArgumentException(
                        "Expected one of the following ids: ${R.id.userActionOptionContinue}, or " +
                                "${R.id.userActionOptionPayNow} but was $selectedId"
                    )
                }
            }
        }

    private val selectedOrderIntent: OrderIntent
        get() {
            return when (val selectedId = selectOrderIntent.checkedRadioButtonId) {
                R.id.orderIntentOptionAuthorize -> OrderIntent.AUTHORIZE
                R.id.orderIntentOptionCapture -> OrderIntent.CAPTURE
                else -> {
                    throw IllegalArgumentException(
                        "Expected one of the following ids: ${R.id.orderIntentOptionAuthorize}, or " +
                                "${R.id.orderIntentOptionCapture} but was $selectedId"
                    )
                }
            }
        }

    private val selectedShippingPreference: ShippingPreference
        get() {
            return when (val selectedId = selectShippingPreference.checkedRadioButtonId) {
                R.id.shippingPreferenceOptionGetFromFile -> ShippingPreference.GET_FROM_FILE
                R.id.shippingPreferenceOptionNoShipping -> ShippingPreference.NO_SHIPPING
                R.id.shippingPreferenceOptionSetProvidedAddress -> ShippingPreference.SET_PROVIDED_ADDRESS
                else -> {
                    throw IllegalArgumentException(
                        "Expected one of the following ids: ${R.id.shippingPreferenceOptionGetFromFile}, " +
                                "${R.id.shippingPreferenceOptionNoShipping}, or " +
                                "${R.id.shippingPreferenceOptionSetProvidedAddress} but was $selectedId"
                    )
                }
            }
        }

    private val selectedCurrencyCode: CurrencyCode
        get() {
            return when (val selectedId = selectCurrencyCode.checkedRadioButtonId) {
                R.id.currencyCodeUsd -> CurrencyCode.USD
                R.id.currencyCodeEur -> CurrencyCode.EUR
                R.id.currencyCodeGbp -> CurrencyCode.GBP
                else -> {
                    throw IllegalArgumentException(
                        "Expected one of the following ids: ${R.id.currencyCodeUsd}, " +
                                "${R.id.currencyCodeEur}, or ${R.id.currencyCodeGbp} but was $selectedId"
                    )
                }
            }
        }

    private val createItemDialog: CreateItemDialog by lazy {
        CreateItemDialog().apply { onItemCreated = ::onItemCreated }
    }

    private val createdItems = mutableListOf<CreatedItem>()

    private val createOrderUseCase by lazy { CreateOrderUseCase() }

    override fun onCreate(savedInstanceState: Bundle?) = with(applicationContext) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_quick_start)

        addItemButton.setOnClickListener {
            createItemDialog.show(supportFragmentManager, "CreateItemDialog")
        }
        submitOrderButton.setOnClickListener {
            if (createdItems.isEmpty()) {
                itemErrorTextView.visibility = View.VISIBLE
            } else {
                startCheckoutWithSampleOrders(createdItems, selectedCurrencyCode)
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun onItemCreated(createdItem: CreatedItem) {
        val itemView = layoutInflater.inflate(R.layout.item_preview_item, itemsContainer, false)
            .apply {
                itemNameText.text = createdItem.name
                itemAmountText.text = createdItem.amount
                itemTaxText.text = createdItem.taxAmount
                itemQuantityText.text = getString(
                    R.string.orders_quick_start_activity_created_item_quantity,
                    createdItem.quantity
                )
            }
        itemsContainer.addView(itemView)

        createdItems.add(createdItem)
        itemErrorTextView.visibility = View.GONE
    }

    private fun startCheckoutWithSampleOrders(
        createdItems: List<CreatedItem>,
        currencyCode: CurrencyCode
    ) {
        fun showSnackbar(text: String) {
            Snackbar.make(rootOrdersQuickStart, text, Snackbar.LENGTH_LONG).show()
        }

        val createOrderRequest =
            CreateOrderRequest(
                orderIntent = selectedOrderIntent,
                userAction = selectedUserAction,
                shippingPreference = selectedShippingPreference,
                currencyCode = currencyCode,
                createdItems = createdItems
            )
        val order = createOrderUseCase.execute(createOrderRequest)

        checkoutSdk.start(
            createOrder = CreateOrder { actions ->
                actions.create(order) { id ->
                    Log.d(tag, "Order ID: $id")
                }
            },
            onApprove = OnApprove { approval ->
                Log.i(tag, "OnApprove: $approval")
                when (selectedOrderIntent) {
                    OrderIntent.AUTHORIZE -> approval.orderActions.authorize { result ->
                        val message = when (result) {
                            is AuthorizeOrderResult.Success -> {
                                Log.i(tag, "Success: $result")
                                "💰 Order Authorization Succeeded 💰"
                            }
                            is AuthorizeOrderResult.Error -> {
                                Log.i(tag, "Error: $result")
                                "🔥 Order Authorization Failed 🔥"
                            }
                        }
                        showSnackbar(message)
                    }
                    OrderIntent.CAPTURE -> approval.orderActions.capture { result ->
                        val message = when (result) {
                            is CaptureOrderResult.Success -> {
                                Log.i(tag, "Success: $result")
                                "💰 Order Capture Succeeded 💰"
                            }
                            is CaptureOrderResult.Error -> {
                                Log.i(tag, "Error: $result")
                                "🔥 Order Capture Failed 🔥"
                            }
                        }
                        showSnackbar(message)
                    }
                }
            },
            onCancel = OnCancel {
                Log.d(tag, "OnCancel")
                showSnackbar("😭 Buyer Cancelled Checkout 😭")
            },
            onError = OnError { errorInfo ->
                Log.d(tag, "ErrorInfo: $errorInfo")
                showSnackbar("🚨 An Error Occurred 🚨")
            }
        )
    }

    companion object {
        fun startIntent(context: Context): Intent {
            return Intent(context, OrdersQuickStartActivity::class.java)
        }
    }
}
