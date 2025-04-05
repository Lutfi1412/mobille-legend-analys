package org.example.mlanalis.data

import kotlinx.serialization.Serializable

@Serializable
data class GetMeta(
    val name: String,
    val id: Int,
    val win_rate: String,
    val pick_rate: String,
    val ban_rate: String
)
@Serializable
data class ResponseMeta(
    val data: List<GetMeta>
)