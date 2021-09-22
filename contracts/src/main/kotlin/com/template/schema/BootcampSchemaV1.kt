package com.template.schema

import com.template.model.Status
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

/**
 * The family of schemas for ReinsuranceState.
 */
object BootcampSchema

/**
 * An ReinsuranceState schema V1.
 */
object BootcampSchemaV1 : MappedSchema(
    schemaFamily = BootcampSchema.javaClass,
    version = 1,
    mappedTypes = listOf(PersistentDetails::class.java)) {

    override val migrationResource: String?
        get() = "bootcamp.changelog-master";

    @Entity
    @Table(name = "BOOTCAMP_DETAIL")
    class PersistentDetails(
        @Column(name = "issuer")
        val issuer: String,

        @Column(name = "reinsurer")
        val reinsurer: String,

        @Column(name = "register")
        val register: String,

        @Column(name = "contractId")
        val contractId: String,

        @Column(name = "status")
        val status: Status

    ): PersistentState() {
        constructor() : this("", "", "", "", Status.WAITING_APPROVAL)
    }
}