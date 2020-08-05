package com.paypal.checkoutsamples.sdkhelper

/**
 * The method by which the payer wants to get their items.
 */
enum class ShippingType {
    /**
     * The payer intends to receive the items at a specified address.
     */
    SHIPPING,

    /**
     * The payer intends to pick up the items at a specified address. For example, a store address.
     */
    PICKUP
}
