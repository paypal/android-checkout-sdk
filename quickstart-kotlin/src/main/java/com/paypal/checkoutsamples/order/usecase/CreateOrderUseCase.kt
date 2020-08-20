package com.paypal.checkoutsamples.order.usecase

import com.paypal.checkoutsamples.order.CreatedItem
import com.paypal.checkoutsamples.sdkhelper.CurrencyCode
import com.paypal.checkoutsamples.sdkhelper.OrderIntent
import com.paypal.checkoutsamples.sdkhelper.ShippingPreference
import com.paypal.checkoutsamples.sdkhelper.UserAction
import com.paypal.pyplcheckout.merchantIntegration.AppContext
import com.paypal.pyplcheckout.merchantIntegration.Order

/**
 * CreateOrderRequest contains all of the necessary properties to successfully create an [Order] with
 * the PayPal Checkout SDK.
 */
data class CreateOrderRequest(
    val orderIntent: OrderIntent,
    val userAction: UserAction,
    val shippingPreference: ShippingPreference,
    val currencyCode: CurrencyCode,
    val createdItems: List<CreatedItem>
)

/**
 * CreateOrderUseCase provides a way to construct an [Order] given a [CreateOrderRequest].
 */
class CreateOrderUseCase(
    private val createPurchaseUnitUseCase: CreatePurchaseUnitUseCase = CreatePurchaseUnitUseCase()
) {

    fun execute(request: CreateOrderRequest): Order = with(request) {
        val createPurchaseUnitRequest = CreatePurchaseUnitRequest(
            createdItems = createdItems,
            shippingPreference = shippingPreference,
            currencyCode = currencyCode
        )
        val purchaseUnit = createPurchaseUnitUseCase.execute(createPurchaseUnitRequest)

        return Order.Builder()
            .intent(orderIntent.name)
            .purchaseUnitList(listOf(purchaseUnit))
            .appContext(
                AppContext.Builder()
                    .brandName("Acme Inc")
                    .userAction(userAction.name)
                    .shippingPreference(shippingPreference.name)
                    .build()
            )
            .build()
    }
}
