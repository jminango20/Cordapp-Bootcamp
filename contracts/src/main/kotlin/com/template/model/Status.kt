package com.template.model

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
enum class Status {
    WAITING_APPROVAL, APPROVED, REFUSED
}