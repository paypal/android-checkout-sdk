package com.paypal.checkoutsamples.token.repository.request

import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * OrderRequest is used for creating a new Order with the v2 Order's API.
 *
 * @see https://developer.paypal.com/docs/api/orders/v2/#definition-order_request
 *
 * @property intent describes how the order should be handled, should it be captured so the checkout is
 * completely shortly after the pay sheet is complete or authorized.
 * @property applicationContext provides details about application being used for placing the order, of
 * note is the user action which determines whether or not we show a cart total.
 * @property purchaseUnits contains details about the items that are part of the order.
 */
@Serializable
data class OrderRequest(
    val intent: String = OrderIntent.CAPTURE.name,
    @SerialName("application_context")
    val applicationContext: ApplicationContextRequest = ApplicationContextRequest(),
    @SerialName("purchase_units")
    val purchaseUnits: List<PurchaseUnitRequest> = listOf(PurchaseUnitRequest())
)

/**
 * ApplicationContextRequest provides additional details about the application. For the purpose of this
 * sample we are only concerned with a subset of the parameters.
 *
 * @see https://developer.paypal.com/docs/api/orders/v2/#definition-order_application_context
 *
 * @property userAction determines whether or not the pay sheet will display the total order amount via
 * PAY_NOW being passed in. When CONTINUE is provided then the pay sheet will not display the total.
 */
@Serializable
data class ApplicationContextRequest(
    @SerialName("user_action")
    val userAction: String = UserAction.PAY_NOW.name
)

/**
 * PurchaseUnitRequest is used to provide item, payment, and shipping information. For the purpose of
 * this sample we are only concerned with a subset of the available parameters.
 *
 * @see https://developer.paypal.com/docs/api/orders/v2/#definition-purchase_unit_request
 *
 * @param amount is the total amount for the order.
 */
@Serializable
data class PurchaseUnitRequest(
    val amount: AmountRequest = AmountRequest()
)

/**
 * AmountRequest is used for outlining the amount of something (item, shipping, total, etc).
 *
 * @see https://developer.paypal.com/docs/api/orders/v2/#definition-order_request
 * @see [CurrencyCode]
 *
 * @property currencyCode defines what currency is being used for this order.
 * @property value defines how much of the amount is.
 *
 * Example, value = 100 + currencyCode = USD is how you would represent $100
 */
@Serializable
data class AmountRequest(
    @SerialName("currency_code")
    val currencyCode: String = CurrencyCode.USD.name,
    val value: String = "0.01"
)
