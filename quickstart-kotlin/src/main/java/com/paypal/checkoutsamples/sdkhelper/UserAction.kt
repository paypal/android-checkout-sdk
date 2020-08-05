package com.paypal.checkoutsamples.sdkhelper

/**
 * Configures a Continue or Pay Now checkout flow.
 * Defaults to [CONTINUE]
 */
enum class UserAction {
    /**
     * After you redirect the customer to the PayPal payment page, a Continue button appears. Use
     * this option when the final amount is not known when the checkout flow is initiated and you
     * want to redirect the customer to the merchant page without processing the payment.
     *
     * This will hide the total price on the PayPal pay sheet.
     */
    CONTINUE,

    /**
     * After you redirect the customer to the PayPal payment page, a Pay Now button appears. Use
     * this option when the final amount is known when the checkout is initiated and you want to
     * process the payment immediately when the customer clicks Pay Now.
     *
     * This will display the total price on the PayPal pay sheet.
     */
    PAY_NOW
}
