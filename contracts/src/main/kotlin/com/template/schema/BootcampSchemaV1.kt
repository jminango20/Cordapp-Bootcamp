package com.template.schema

import com.template.model.Status
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import org.hibernate.annotations.Type
import java.util.*
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
    @Table(name = "bootcamp_detail")
    class PersistentDetails(
        @Column(name = "issuer", nullable = false)
        var issuer: String,

        @Column(name = "reinsurer", nullable = false)
        var reinsurer: String,

        @Column(name = "register", nullable = false)
        var register: String,

        @Column(name = "contractId", nullable = false)
        var contractId: String,

        @Column(name = "status", nullable = false)
        var status: Status,

        @Column(name = "linear_id", nullable = false)
        @Type(type="uuid-char")
        var uuid: UUID

    ): PersistentState() {
        constructor() : this("", "", "", " ", Status.WAITING_APPROVAL, UUID.randomUUID())
    }
}