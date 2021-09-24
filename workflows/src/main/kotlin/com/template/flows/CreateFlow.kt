package com.template.flows


import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step
import co.paralleluniverse.fibers.Suspendable
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import com.template.states.ReinsuranceState
import com.template.contracts.ReinsuranceContract
import com.template.model.Status
import com.template.schema.BootcampSchemaV1
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class CreateFlow(val reinsurer: Party, val register: Party, val contractId: String) : FlowLogic<SignedTransaction>() {

    companion object {
        object QUERYING_VAULT : Step("Querying vault")
        object BUILDING_TRANSACTION : Step("Building transaction")
        object VERIFYING_TRANSACTION : Step("Verifying transaction")
        object SIGNING_TRANSACTION : Step("Signing transaction")

        object GATHERING_SIGS : Step("Gathering the counterparty`s signature") {
            override fun childProgressTracker() = CollectSignaturesFlow.tracker()
        }

        object FINALIZING_TRANSACTION : Step("Finalizing transaction") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(
            QUERYING_VAULT,
            BUILDING_TRANSACTION,
            VERIFYING_TRANSACTION,
            SIGNING_TRANSACTION,
            GATHERING_SIGS,
            FINALIZING_TRANSACTION
        )
    }
    override val progressTracker = tracker()

    @Suspendable
    override fun call(): SignedTransaction {
        val criteria = QueryCriteria.VaultCustomQueryCriteria(builder {
            BootcampSchemaV1.PersistentDetails::contractId.equal(contractId)
        })

        if (serviceHub.vaultService.queryBy<ReinsuranceState>(criteria).states.isNotEmpty()) {
            throw FlowException("Contract $contractId does not exist. Please create one")
        }


        progressTracker.currentStep = BUILDING_TRANSACTION

        // identidade do nó
        val issuer = ourIdentity

        // primeiro notary
        val notary = serviceHub.networkMapCache.notaryIdentities.single()

        // criacao output state
        val output = ReinsuranceState(contractId, issuer, reinsurer, register, Status.WAITING_APPROVAL)

        val txBuilder = TransactionBuilder(notary).addOutputState(output).
        addCommand(ReinsuranceContract.Commands.Create(), output.participants.map { it.owningKey })

        progressTracker.currentStep = VERIFYING_TRANSACTION

        // validacao de contrato
        txBuilder.verify(serviceHub)

        progressTracker.currentStep = SIGNING_TRANSACTION

        val stx = serviceHub.signInitialTransaction(txBuilder)

        progressTracker.currentStep = GATHERING_SIGS
        val sessions = (output.participants - ourIdentity).map { initiateFlow(it) }.toSet()


        // coletei a assinatura dos outros participantes
        val fullySignedTx = subFlow(
            CollectSignaturesFlow(
                stx,
                sessions,
                GATHERING_SIGS.childProgressTracker()
            )
        )

        progressTracker.currentStep = FINALIZING_TRANSACTION
        val signedTx = subFlow(
            FinalityFlow(
                fullySignedTx,
                sessions,
                FINALIZING_TRANSACTION.childProgressTracker()
            )
        )
        return signedTx
    }
}


@InitiatedBy(CreateFlow::class)
class CreateFlowResponder(val flowSession: FlowSession): FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call() : SignedTransaction {
        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
            @Suspendable
            override fun checkTransaction(stx: SignedTransaction) {
            }
        }
        val txWebJustSignedId = subFlow(signedTransactionFlow)
        return subFlow(ReceiveFinalityFlow( otherSideSession = flowSession, expectedTxId = txWebJustSignedId.id))
    }
}