package com.template.states

import com.template.contracts.ReinsuranceContract
import com.template.model.Status
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

// *********
// * State *
// *********
@BelongsToContract(ReinsuranceContract::class)
data class ReinsuranceState(val contractId: String,
                            val issuer: Party,
                            val reinsurer: Party,
                            val register: Party,
                            val status: Status,
                            override val participants: List<AbstractParty> = listOf(issuer,reinsurer,register),
                            override val linearId: UniqueIdentifier = UniqueIdentifier(contractId)



) : LinearState
