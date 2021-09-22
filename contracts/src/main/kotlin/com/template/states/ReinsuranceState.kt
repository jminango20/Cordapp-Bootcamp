package com.template.states

import com.template.contracts.ReinsuranceContract
import com.template.model.Status
import com.template.schema.BootcampSchemaV1
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

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



) : LinearState, QueryableState {
    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when(schema){
            is BootcampSchemaV1 -> BootcampSchemaV1.PersistentDetails(
                this.issuer.name.toString(),
                this.reinsurer.name.toString(),
                this.register.name.toString(),
                this.contractId,
                this.status,
                this.linearId.id
            )

            else-> throw IllegalArgumentException("Unrecognised schema $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(BootcampSchemaV1)

}
