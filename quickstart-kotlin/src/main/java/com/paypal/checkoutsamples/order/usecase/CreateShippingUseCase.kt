package com.paypal.checkoutsamples.order.usecase

import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.ShippingPreference
import com.paypal.checkout.order.Address
import com.paypal.checkout.order.Shipping

/**
 * CreateOrderRequest contains all of the necessary properties to successfully create a [Shipping]
 * instance with the PayPal Checkout SDK.
 */
data class CreateShippingRequest(
    val shippingPreference: ShippingPreference,
    val currencyCode: CurrencyCode
)

/**
 * CreateShippingUseCase provides a way to construct a [Shipping] instance given a
 * [CreateShippingRequest].
 */
class CreateShippingUseCase {

    fun execute(request: CreateShippingRequest): Shipping {
        /*
         * Options for Shipping such as Standard, Express, Next Day, etc.
         *
         * Only supported for [ShippingPreference.GET_FROM_FILE], and displaying the shipping
         * options are currently disabled in 0.0.4, for now this can safely default to null for all
         * cases.
         */
        val shippingOptions = null

        return Shipping.Builder()
            .address(
                Address.Builder()
                    /*
                     * The first line of the address. For example, number or street. For example,
                     * 173 Drury Lane. Required for data entry and compliance and risk checks.
                     * Must contain the full address.
                     * Maximum length: 300.
                     */
                    .addressLine1("123 Townsend St")
                    /*
                     * The second line of the address. For example, suite or apartment number.
                     * Maximum length: 300.
                     */
                    .addressLine2("Floor 6")
                    /*
                     * A city, town, or village. Smaller than adminArea1
                     */
                    .adminArea2("San Francisco")
                    /*
                     * The highest level sub-division in a country, which is usually a province,
                     * state, or ISO-3166-2 subdivision. Format for postal delivery.
                     * For example, CA and not California. Value, by country, is:
                     *   UK. A county.
                     *   US. A state.
                     *   Canada. A province.
                     *   Japan. A prefecture.
                     *   Switzerland. A kanton.
                     * Maximum length: 300.
                     */
                    .adminArea1("CA")
                    /*
                     * The postal code, which is the zip code or equivalent. Typically required
                     * for countries with a postal code or an equivalent.
                     * @see [Postal Code](https://en.wikipedia.org/wiki/Postal_code)
                     * Maximum length: 60.
                     */
                    .postalCode("94107")
                    /*
                     * The two-character ISO 3166-1 code that identifies the country or region.
                     *
                     * @see [Country Codes](https://developer.paypal.com/docs/integration/direct/rest/country-codes/)
                     */
                    .countryCode("US")
                    .build()
            )
            .options(shippingOptions)
            .build()
    }
}
