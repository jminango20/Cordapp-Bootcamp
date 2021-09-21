package com.template.contracts

import com.template.model.Status
import com.template.states.ReinsuranceState
import com.template.states.TemplateState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.Requirements.using
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.contracts.requireThat
// ************
// * Contract *
// ************
class ReinsuranceContract : Contract {
    companion object {
        // Used to identify our contract when building a transaction.
        const val ID = "com.template.contracts.ReinsuranceContract"
    }

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        // Verification logic goes here.
        val command = tx.commands.requireSingleCommand<Commands>()
        when (command.value) {
            is Commands.Create -> verifyCreate(tx)
            is Commands.Approve -> verifyApprove(tx)
            is Commands.Refuse -> verifyRefuse(tx)
        }
    }

    //Verificiation function
    fun verifyCreate(tx: LedgerTransaction){
        requireThat {
            val output = tx.outputsOfType<ReinsuranceState>()//.single()
            "Sem input" using (tx.inputsOfType<ReinsuranceState>().isEmpty())
            "Com um output" using (output.size==1)
            //"Issuer, reinsurer e register não podem ser a mesma entidade" using (output.single().participants.toSet().size==3)
            "Issuer, reinsurer não podem ser a mesma entidade" using (output.single().issuer != output.single().reinsurer)
            "Issuer e Register não podem ser a mesma entidade" using (output.single().issuer != output.single().register)
            "Reinsurer e Register não podem ser a mesma entidade" using (output.single().reinsurer != output.single().register)
        }

    }

    fun verifyApprove(tx: LedgerTransaction){
        requireThat {
            val input = tx.inputsOfType<ReinsuranceState>()
            val output = tx.outputsOfType<ReinsuranceState>()
            "Um input" using (input.size==1)
            "Um output" using (output.size==1)
            "issuer do input precisa ser igual ao do output" using (input.single().issuer == output.single().issuer)
            "reinsurer do input precisa ser igual ao do output" using (input.single().reinsurer == output.single().reinsurer)
            "register do input precisa ser igual ao do output" using (input.single().register == output.single().register)
            "status precisa ser APPROVED" using (output.single().status == Status.APPROVED)
            "linearId permanece igual" using (input.single().linearId == output.single().linearId)
        }
    }

    fun verifyRefuse(tx: LedgerTransaction){
        val input = tx.inputsOfType<ReinsuranceState>()
        val output = tx.outputsOfType<ReinsuranceState>()
        "Um input" using (input.size==1)
        "Um output" using (output.size==1)
        "issuer do input precisa ser igual ao do output" using (input.single().issuer == output.single().issuer)
        "reinsurer do input precisa ser igual ao do output" using (input.single().reinsurer == output.single().reinsurer)
        "register do input precisa ser igual ao do output" using (input.single().register == output.single().register)
        "status precisa ser REFUSED" using (output.single().status == Status.REFUSED)
        "linearId permanece igual" using (input.single().linearId == output.single().linearId)
    }


    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class Create : Commands
        class Approve: Commands
        class Refuse : Commands
    }
}