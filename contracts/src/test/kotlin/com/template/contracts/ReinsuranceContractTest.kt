/*
package com.template.contracts

import com.template.model.Status
import com.template.states.ReinsuranceState
import net.corda.core.identity.CordaX500Name
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test
import com.template.states.TemplateState
import net.corda.core.contracts.Requirements.using
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.transactions.LedgerTransaction
import java.util.*

class ReinsuranceContractTest {
    private val ledgerServices: MockServices = MockServices(listOf("com.template"))
    var issuer = TestIdentity(CordaX500Name("Issuer", "TestLand", "US"))
    var reinsurer = TestIdentity(CordaX500Name("Reinsurer", "TestLand", "US"))
    var register = TestIdentity(CordaX500Name("Register", "TestLand", "US"))

    //TESTE CREATE
    @Test
    fun create() {
        //val inputState = ReinsuranceState("A", issuer.party, reinsurer.party, register.party, Status.APPROVED )
        //val outputState = ReinsuranceState("A", issuer.party, reinsurer.party, register.party, Status.APPROVED )
        //ZE-DANIEL
        val stateReinsuraceState = ReinsuranceState(UUID.randomUUID().toString(), issuer.party, reinsurer.party, register.party, Status.WAITING_APPROVAL)

        // Sem Input
        ledgerServices.ledger {
            transaction {
                //failing transaction
                input(ReinsuranceContract.ID, stateReinsuraceState)
                output(ReinsuranceContract.ID, stateReinsuraceState)
                command(issuer.publicKey, ReinsuranceContract.Commands.Create())
                fails()
            }
            //pass
            transaction {
                //passing transaction
                output(ReinsuranceContract.ID, stateReinsuraceState)
                command(issuer.publicKey, ReinsuranceContract.Commands.Create())
                verifies()
            }
        }
        // Com um Output
       ledgerServices.ledger {
            transaction {
                //failing transaction
                output(ReinsuranceContract.ID, stateReinsuraceState)
                output(ReinsuranceContract.ID, stateReinsuraceState)
                command(issuer.publicKey, ReinsuranceContract.Commands.Create())
                fails()
            }
            //pass
            transaction {
                //passing transaction
                output(ReinsuranceContract.ID, stateReinsuraceState)
                command(issuer.publicKey, ReinsuranceContract.Commands.Create())
                verifies()
            }
        }
    }

    //TESTE APPROVED
    @Test
    fun reinsuranceContractVerifyApprovedTest(){
        val stateReinsuraceInput = ReinsuranceState(UUID.randomUUID().toString(), issuer.party, reinsurer.party, register.party, Status.WAITING_APPROVAL)
        val stateReinsuraceOutput = stateReinsuraceInput.copy(status = Status.APPROVED) //Sofreo alteracao

        val commandApprove = ReinsuranceContract.Commands.Approve()

        ledgerServices.ledger {
            transaction {
                //failing transaction pela falta de um Input
                output(ReinsuranceContract.ID, stateReinsuraceOutput)
                command(issuer.publicKey, commandApprove)
                fails()
            }
            //pass
            transaction {
                //passing transaction
                input(ReinsuranceContract.ID, stateReinsuraceInput)
                output(ReinsuranceContract.ID, stateReinsuraceOutput)
                command(issuer.publicKey, commandApprove)
                verifies()
            }
        }
    }

    //TESTE REFUSE
    @Test
    fun reinsuranceContractVerifyRefusedTest(){
        val stateReinsuraceInput = ReinsuranceState(UUID.randomUUID().toString(), issuer.party, reinsurer.party, register.party, Status.WAITING_APPROVAL)
        val stateReinsuraceOutput = stateReinsuraceInput.copy(status = Status.APPROVED) //Sofreo alteracao

        val commandApprove = ReinsuranceContract.Commands.Approve()

        ledgerServices.ledger {
            transaction {
                //failing transaction pela falta de um Input
                output(ReinsuranceContract.ID, stateReinsuraceOutput)
                command(issuer.publicKey, commandApprove)
                fails()
            }
            //pass
            transaction {
                //passing transaction
                input(ReinsuranceContract.ID, stateReinsuraceInput)
                output(ReinsuranceContract.ID, stateReinsuraceOutput)
                command(issuer.publicKey, commandApprove)
                verifies()
            }
        }
    }

    }
*/
