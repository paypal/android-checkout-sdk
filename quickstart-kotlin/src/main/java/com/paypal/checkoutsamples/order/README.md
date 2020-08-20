# startCheckoutWithOrders

If you want the Native Checkout SDK to create a new `Order` on your behalf then using `checkoutSdk.startCheckoutWithOrders`
is the perfect solution. The sample app provides an example which can be found in `OrdersQuickStartActivity`.

```kotlin
val order: Order = //... refer to the full walk through for creating a valid order found in the next section.
checkoutSdk.startCheckoutWithOrders(
    context = this, // Activity Context
    order = order,
    orderCallbacks = object : OrderCallbacks {
        override fun onOrderCreateFailed(exception: PYPLException) {
            // The order could not be created, please consult Logcat for the reason why.
            Log.w("OrderCreateFailed", exception)
        }

        override fun onOrderCreated(id: String) {
            // The order was created, at this point the pay sheet should be displaying the order total
            // along with additional details.
            Log.i("OrderCreated", "id: $id")
        }
    },
    checkoutConfig = checkoutConfig
)
```

## Creating An Order Instance

You can create a valid order through the use of `Order.Builder` and supplying the necessary properties.
Here is a short example of what this may look like:

```kotlin
Order.Builder()
    .intent(orderIntent.name) // see `OrderIntent` within this sample project.
    .purchaseUnitList(listOf(purchaseUnit)) // see `CreatePurchaseUnitUseCase` within this sample project.
    .appContext(
        AppContext.Builder()
            .brandName("Acme Inc")
            .landingPage(landingPageType.name) // see `LandingPageType` within this sample project.
            .userAction(userAction.name) // see `UserAction` within this sample project.
            .shippingPreference(shippingPreference.name) // see `ShippingPreference` within this sample project.
            .build()
    )
    .build()
```

The above code snippet is an abbreviated version taken from `CreateOrderUseCase`.

### Creating A Purchase Unit Instance

A valid `Order` will contain one or more `PurchaseUnit` instances. A valid purchase unit contains information
such as the amount, which items are included, shipping details, along with many other options. You can
find complete documentation about purchase units [here](https://developer.paypal.com/docs/api/orders/v2/#definition-purchase_unit_request).
Here is a short example of what constructing a purchase unit may look like:

```kotlin
PurchaseUnit.Builder()
    .referenceId(UUID.randomUUID().toString())
    .amount(amount) // see `CreateAmountUseCase` within this sample project.
    .items(items) // see `CreateItemsUseCase` within this sample project.
    .shipping(shipping) // see `CreateShippingUseCase within this sample project.
    .customId("CUSTOM-123")
    .description("Purchase from Orders Quick Start")
    .softDescriptor("800-123-1234")
    .build()
```

The above code snippet is an abbreviated version taken from `CreatePurchaseUnitUseCase`.

#### Creating An Amount Instance

A valid `PurchaseUnit` will contain a valid `Amount`. In order for the amount to be valid, its `totalValue`
must equal the following: `itemTotal + taxTotal + shipping + handling + insurance - shippingDiscount - discount`.
Here is a short example of what this may look like:

```kotlin
val itemTotal = "13.43" // should be item value * quantity
val taxTotal = "0.94" // should be item tax * quantity
val shippingTotal = "4.99"
val handlingTotal = "0.00"
val shippingDiscountTotal = "4.99"
val itemDiscountTotal = "0.00"
val totalValue = "14.37"

Amount.Builder()
    .currencyCode("USD")
    .value(
        UnitAmount.Builder()
            .value(totalValue)
            .currencyCode("USD")
            .build()
    )
    .breakdown(
        BreakDown.Builder()
            .itemTotal(
                UnitAmount.Builder()
                    .value(itemTotal)
                    .currencyCode("USD")
                    .build()
            )
            .shipping(
                UnitAmount.Builder()
                    .value(shippingTotal)
                    .currencyCode("USD")
                    .build()
            )
            .handling(
                UnitAmount.Builder()
                    .value(handlingTotal)
                    .currencyCode("USD")
                    .build()            
                )
            .taxTotal(
                UnitAmount.Builder()
                    .value(taxTotal)
                    .currencyCode("USD")
                    .build()            
            )
            .shippingDiscount(
                UnitAmount.Builder()
                    .value(shippingDiscountTotal)
                    .currencyCode("USD")
                    .build()
            )
            .discount(
                UnitAmount.Builder()
                    .value(itemDiscountTotal)
                    .currencyCode("USD")
                    .build()
            )
            .build()
    )
    .build()
```

The above code snippet is an abbreviated version taken from `CreateAmountUseCase`.

#### Creating An Item Instance

A valid `PurchaseUnit` will contain one or more `Items`. A valid `Items` will contain a name, quantity,
category, unit amount, and tax. Here is a short example of what this may look like:

```kotlin
Items.Builder()
    .name("Example Item")
    .quantity("3")
    .category("PHYSICAL_GOODS") // see `ItemCategory` for all valid categories.
    .unitAmount(
        UnitAmount.Builder()
            .value("13.43")
            .currencyCode("USD")
            .build()
    )
    .tax(
        UnitAmount.Builder()
            .value("0.94")
            .currencyCode("USD")
            .build()
    )
    .build()
```

The above code snippet is an abbreviated version taken from `CreateItemsUseCase`. 

#### Creating A Shipping Instance

A valid `PurchaseUnit` may contain shipping information. Here is a short example of what this may look
like:

```kotlin
return Shipping.Builder()
    .address(
        Address.Builder()
            .addressLine1("123 Townsend St")
            .addressLine2("Floor 6")
            .adminArea2("San Francisco")
            .adminArea1("CA")
            .postalCode("94107")
            .countryCode("US")
            .build()
    )
    .options(null) // options currently will not be displayed by the SDK so `null` is fine for now.
    .build()
```

The above code snippet is an abbreviated version taken from `CreateShippingUseCase`.
