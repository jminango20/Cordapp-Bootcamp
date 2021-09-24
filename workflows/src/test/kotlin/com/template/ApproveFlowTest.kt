package com.template

import com.template.flows.CreateFlow
import net.corda.testing.node.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.template.states.TemplateState
import java.util.concurrent.Future;
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import com.template.flows.Initiator
import com.template.model.Status
import com.template.states.ReinsuranceState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault.StateStatus


class ApproveFlowTest {
    private lateinit var network: MockNetwork
    private lateinit var issuer: StartedMockNode
    private lateinit var reinsurer: StartedMockNode
    private lateinit var register: StartedMockNode


    @Before
    fun setup() {
        network = MockNetwork(MockNetworkParameters(cordappsForAllNodes = listOf(
            TestCordapp.findCordapp("com.template.contracts"),
            TestCordapp.findCordapp("com.template.flows")
        )))
        issuer = network.createPartyNode()
        reinsurer = network.createPartyNode()
        register = network.createPartyNode()
        network.runNetwork()
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }
    @Test
    fun `approve a state`() {
        val contractId:String = "1234"
        val reinsuraceState = ReinsuranceState(contractId,issuer.info.legalIdentities.first(), reinsurer.info.legalIdentities.first(), register.info.legalIdentities.first(),Status.WAITING_APPROVAL)
        val flow = CreateFlow(reinsurer.info.legalIdentities.first(), register.info.legalIdentities.first(), contractId )

        val future: Future<SignedTransaction> = issuer.startFlow(flow)
        network.runNetwork()

        //successful query means the state is stored at node b's vault. Flow went through.
        val inputCriteria: QueryCriteria = QueryCriteria.VaultQueryCriteria().withStatus(StateStatus.UNCONSUMED)
        val state = issuer.services.vaultService.queryBy(ReinsuranceState::class.java, inputCriteria).states[0].state.data
    }
}