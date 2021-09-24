package com.template.model

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
enum class Status(val description: String) {
    WAITING_APPROVAL("WAITING_APPROVAL"),
    APPROVED("APPROVED"),
    REFUSED("REFUSED")
}