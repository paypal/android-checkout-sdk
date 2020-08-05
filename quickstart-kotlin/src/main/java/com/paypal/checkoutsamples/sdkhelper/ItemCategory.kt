package com.paypal.checkoutsamples.sdkhelper

/**
 * An ItemCategory provides additional context for items in a category.
 *
 * @see [Item Definition](https://developer.paypal.com/docs/api/orders/v2/#definition-item)
 */
enum class ItemCategory {

    /**
     * Goods that are stored, delivered, and used in their electronic format. This value is not
     * currently supported for API callers that leverage the
     * [PayPal for Commerce Platform](https://www.paypal.com/us/webapps/mpp/commerce-platform) product.
     */
    DIGITAL_GOODS,

    /**
     * A tangible item that can be shipped with proof of delivery.
     */
    PHYSICAL_GOODS
}
