package com.paypal.checkoutsamples.order.usecase

import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.ShippingPreference
import com.paypal.checkout.order.PurchaseUnit
import com.paypal.checkoutsamples.order.CreatedItem
import java.util.UUID

/**
 * CreatePurchaseUnitRequest contains all of the necessary properties to successfully create a
 * [PurchaseUnit] with the PayPal Checkout SDK.
 */
data class CreatePurchaseUnitRequest(
    val createdItems: List<CreatedItem>,
    val shippingPreference: ShippingPreference,
    val currencyCode: CurrencyCode
)

/**
 * CreatePurchaseUnitUseCase provides a way to construct a [PurchaseUnit] given a
 * [CreatePurchaseUnitRequest]. It's worth noting that a [PurchaseUnit] contains the bulk of an
 * Order's information as it contains items, shipping information, along with the total amount and
 * a breakdown of those totals.
 */
class CreatePurchaseUnitUseCase(
    private val createItemsUseCase: CreateItemsUseCase = CreateItemsUseCase(),
    private val createShippingUseCase: CreateShippingUseCase = CreateShippingUseCase(),
    private val createAmountUseCase: CreateAmountUseCase = CreateAmountUseCase()
) {

    fun execute(request: CreatePurchaseUnitRequest): PurchaseUnit = with(request) {
        val createItemsRequest = CreateItemsRequest(createdItems, currencyCode)
        val items = createItemsUseCase.execute(createItemsRequest)

        val createShippingRequest = CreateShippingRequest(shippingPreference, currencyCode)
        val shipping = createShippingUseCase.execute(createShippingRequest)

        val amountRequest = CreateAmountRequest(createdItems, currencyCode)
        val amount = createAmountUseCase.execute(amountRequest)

        return PurchaseUnit.Builder()
            .referenceId(UUID.randomUUID().toString())
            .amount(amount)
            .items(items)
            /*
             * Omitting shipping will default to the customer's default shipping address.
             */
            .shipping(shipping)
            /*
             * The API caller-provided external ID. Used to reconcile API caller-initiated transactions
             * with PayPal transactions. Appears in transaction and settlement reports.
             */
            .customId("CUSTOM-123")
            /*
             * The purchase description.
             */
            .description("Purchase from Orders Quick Start")
            /*
             * The soft descriptor is the dynamic text used to construct the statement descriptor
             * that appears on a payer's card statement.
             *
             * Maximum Length: 22 characters
             */
            .softDescriptor("800-123-1234")
            .build()
    }
}
