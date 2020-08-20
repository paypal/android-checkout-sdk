package com.paypal.checkoutsamples.token.repository.response

import kotlinx.serialization.Serializable

/**
 * OrderResponse is a partial response returned by /v2/checkout/orders
 *
 * @property id is the identifier for the Order, it is also referred to as a token or ec token. This
 * is used when creating a new pay sheet via the Checkout SDK.
 */
@Serializable
data class OrderResponse(
    val id: String
)
