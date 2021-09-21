package com.template.contracts

import com.template.model.Status
import com.template.states.ReinsuranceState
import net.corda.core.identity.Party
import org.junit.Test
import kotlin.test.assertEquals

class ReinsuranceStateTest {

    @Test
    fun hasFieldOfCorrectType() {
        //TEST ID
        ReinsuranceState::class.java.getDeclaredField("contractId")
        // Is the field of the correct type?
        assertEquals(ReinsuranceState::class.java.getDeclaredField("contractId").type, String()::class.java)

        //TEST Party - Es do tipo Party
        ReinsuranceState::class.java.getDeclaredField("issuer")
        assertEquals(ReinsuranceState::class.java.getDeclaredField("issuer").type, Party::class.java)

        //TEST Party
        ReinsuranceState::class.java.getDeclaredField("reinsurer")
        assertEquals(ReinsuranceState::class.java.getDeclaredField("reinsurer").type, Party::class.java)

        //TEST Party
        val registerField = ReinsuranceState::class.java.getDeclaredField("register")
        assertEquals(registerField.type, Party::class.java)
    }
}
