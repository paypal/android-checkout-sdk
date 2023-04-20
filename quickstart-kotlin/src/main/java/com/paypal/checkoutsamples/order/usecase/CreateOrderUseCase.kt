package com.paypal.checkoutsamples.order.usecase

import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.ShippingPreference
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.OrderRequest
import com.paypal.checkoutsamples.order.CreatedItem

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

    fun execute(request: CreateOrderRequest): OrderRequest = with(request) {
        val createPurchaseUnitRequest = CreatePurchaseUnitRequest(
            createdItems = createdItems,
            shippingPreference = shippingPreference,
            currencyCode = currencyCode
        )
        val purchaseUnit = createPurchaseUnitUseCase.execute(createPurchaseUnitRequest)

        return OrderRequest.Builder()
            .intent(orderIntent)
            .purchaseUnitList(listOf(purchaseUnit))
            .appContext(
                AppContext.Builder()
                    .brandName("Acme Inc")
                    .userAction(userAction)
                    .shippingPreference(shippingPreference)
                    .build()
            )
            .build()
    }
}
