package com.paypal.checkoutsamples.order.usecase

import com.paypal.checkoutsamples.order.CreatedItem
import com.paypal.pyplcheckout.merchantIntegration.Amount
import com.paypal.pyplcheckout.merchantIntegration.BreakDown
import com.paypal.pyplcheckout.merchantIntegration.UnitAmount
import com.paypal.pyplcheckout.merchantIntegration.createorder.CurrencyCode
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * CreateAmountRequest contains all of the necessary properties to successfully create an Amount with
 * the PayPal Checkout SDK.
 */
data class CreateAmountRequest(
    val createdItems: List<CreatedItem>,
    val currencyCode: CurrencyCode
)

/**
 * CreateOrderUseCase provides a way to construct an [Amount] given a [CreateAmountRequest]. In
 * order to successfully create an [Amount] for a PurchaseUnit the value (what the customer is
 * charged). The following calculation is used when determining the total value:
 *     itemTotal + taxTotal + shipping + handling + insurance - shippingDiscount - discount
 */
class CreateAmountUseCase {

    fun execute(request: CreateAmountRequest): Amount = with(request) {
        val itemTotal = createdItems.map { it.amount.toDouble() * it.quantity.toInt() }
            .sum().toBigDecimal().scaledForMoney
        val taxTotal = createdItems.map { it.taxAmount.toDouble() * it.quantity.toInt() }
            .sum().toBigDecimal().scaledForMoney
        val shippingTotal = BigDecimal(0.00).scaledForMoney
        val handlingTotal = BigDecimal(0.00).scaledForMoney
        val shippingDiscountTotal = BigDecimal(0.00).scaledForMoney
        val itemDiscountTotal = BigDecimal(0.00).scaledForMoney
        val totalValue = itemTotal
            .add(taxTotal)
            .add(shippingTotal)
            .add(handlingTotal)
            .subtract(shippingDiscountTotal)
            .subtract(itemDiscountTotal)

        return Amount.Builder()
            .currencyCode(currencyCode)
            .value(totalValue.asMoneyString)
            .breakdown(
                BreakDown.Builder()
                    .itemTotal(itemTotal.unitAmountFor(currencyCode))
                    .shipping(shippingTotal.unitAmountFor(currencyCode))
                    .handling(handlingTotal.unitAmountFor(currencyCode))
                    .taxTotal(taxTotal.unitAmountFor(currencyCode))
                    .shippingDiscount(shippingDiscountTotal.unitAmountFor(currencyCode))
                    .discount(itemDiscountTotal.unitAmountFor(currencyCode))
                    .build()
            )
            .build()
    }

    private fun BigDecimal.unitAmountFor(currencyCode: CurrencyCode): UnitAmount {
        return UnitAmount.Builder()
            .value(asMoneyString)
            .currencyCode(currencyCode)
            .build()
    }

    private val BigDecimal.asMoneyString: String
        get() = DecimalFormat("#0.00").format(this)

    private val BigDecimal.scaledForMoney: BigDecimal
        get() = setScale(2, RoundingMode.HALF_UP)
}
