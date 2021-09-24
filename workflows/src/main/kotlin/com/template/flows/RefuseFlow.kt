package com.template.flows


import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
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
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class RefuseFlow(val contractId: String) : FlowLogic<SignedTransaction>() {

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

        //Analizo o serviceHub
        val inputReinsuranceStateAndRef =  serviceHub.vaultService.queryBy<ReinsuranceState>(criteria).states

        if (inputReinsuranceStateAndRef.isNotEmpty()) {
            val inputReinsuranceState = inputReinsuranceStateAndRef.single().state.data

            // identidade do nó do register
            requireThat {
                "You must be the reinsurer" using (inputReinsuranceState.reinsurer == ourIdentity)
                throw FlowException("You must be the reinsurer")
            }

            progressTracker.currentStep = BUILDING_TRANSACTION

            // I need to use the same notary in order to avoid conflicts
            val notary = inputReinsuranceStateAndRef.single().state.notary

            // criacao output state
            val output = inputReinsuranceState.copy(status = Status.REFUSED)

            val txBuilder = TransactionBuilder(notary)
                .addInputState(inputReinsuranceStateAndRef.single())
                .addOutputState(output)
                .addCommand(ReinsuranceContract.Commands.Approve(), ourIdentity.owningKey)

            progressTracker.currentStep = VERIFYING_TRANSACTION

            // validacao de contrato
            txBuilder.verify(serviceHub)

            progressTracker.currentStep = SIGNING_TRANSACTION

            val stx = serviceHub.signInitialTransaction(txBuilder)


            progressTracker.currentStep = FINALIZING_TRANSACTION
            val signedTx = subFlow(
                FinalityFlow(
                    stx,
                    CreateFlow.Companion.FINALIZING_TRANSACTION.childProgressTracker()
                )
            )
            return signedTx
        }

        else{
            throw FlowException("Contract  $contractId does not exist. Please create one")
        }
}
/*
@InitiatedBy(CreateFlow::class)
class ApproveFlowResponder(val flowSession: FlowSession): FlowLogic<SignedTransaction>() {
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
*/
}