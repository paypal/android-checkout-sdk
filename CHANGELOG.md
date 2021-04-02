# Change Log

## Version 0.1.0
Initial release. Please see [official documentation](https://developer.paypal.com/docs/business/native-checkout/android/) for full integration steps.

* Added `PayPalCheckout` as one of the main interfaces for launching the pay sheet.
* Added `PaymentButton` along with `PayPalButton`, `PayLaterButton`, and `PayPalCreditButton` which can also be used to launch the pay sheet (this is the preferred option as well).
* Added `CreateOrder` interface which allows for orders to be created with both client-side and server-side integrations.
* Added `OnApprove` interface which notifies the client when a buyer approves an order, at this point the client application can either `Capture` or `Authorize` an order.
* Added `OnError` interface which notifies the client when a terminal error occurred in the experience, in these situations the pay sheet will be dismissed.
* Added `OnCancel` interface which notifies the client application that a buyer cancelled out of the experience.