package com.paypal.checkoutsamples.token.repository.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * OAuthTokenResponse is used for capturing an OAuth Token and the params available here are a subset
 * of the ones actually returned.
 *
 * @property accessToken should be used for other API requests, passing it in as "Bearer [accessToken]".
 * @property expiresIn provides the amount of time this token will be valid for to make it easier to
 * know when to re-authenticate.
 */
@Serializable
data class OAuthTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Long
)
