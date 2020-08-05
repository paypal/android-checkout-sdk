package com.paypal.checkoutsamples.sdkhelper

/**
 * The shipping preference:
 *   - Displays the shipping address to the customer.
 *   - Enables the customer to choose an address on the PayPal site.
 *   - Restricts the customer from changing the address during the payment-approval process.
 */
enum class ShippingPreference {

    /**
     * Use the customer-provided shipping address on the PayPal site.
     */
    GET_FROM_FILE,

    /**
     * Redact the shipping address from the PayPal site. Recommended for digital goods.
     */
    NO_SHIPPING,

    /**
     * Use the merchant-provided address. The customer cannot change this address on the PayPal site.
     */
    SET_PROVIDED_ADDRESS
}
