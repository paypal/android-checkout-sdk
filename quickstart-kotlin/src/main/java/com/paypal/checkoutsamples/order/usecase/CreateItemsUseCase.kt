package com.paypal.checkoutsamples.order.usecase

import com.paypal.checkoutsamples.order.CreatedItem
import com.paypal.pyplcheckout.merchantIntegration.Items
import com.paypal.pyplcheckout.merchantIntegration.UnitAmount
import com.paypal.pyplcheckout.merchantIntegration.createorder.CurrencyCode

/**
 * CreateItemRequest contains all of the necessary properties to successfully create a list of Items
 * with the PayPal Checkout SDK.
 */
data class CreateItemsRequest(
    val createdItems: List<CreatedItem>,
    val currencyCode: CurrencyCode
)

/**
 * CreateItemsUseCase provides a way to construct a [List] of [Items] given a [CreateItemsRequest].
 */
class CreateItemsUseCase {
    fun execute(request: CreateItemsRequest): List<Items> = with(request) {
        return createdItems.map { createdItem ->
            Items.Builder()
                .name(createdItem.name)
                .quantity(createdItem.quantity)
                .category(createdItem.itemCategory)
                .unitAmount(
                    UnitAmount.Builder()
                        .value(createdItem.amount)
                        .currencyCode(currencyCode)
                        .build()
                )
                .tax(
                    UnitAmount.Builder()
                        .value(createdItem.taxAmount)
                        .currencyCode(currencyCode)
                        .build()
                )
                .build()
        }
    }
}
