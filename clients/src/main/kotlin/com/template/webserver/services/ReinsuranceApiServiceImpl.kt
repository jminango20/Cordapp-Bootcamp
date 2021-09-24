package com.template.webserver.services

import com.template.flows.CreateFlow
import com.template.states.ReinsuranceState
import com.template.webserver.NodeRPCConnection
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.utilities.getOrThrow
import org.openapitools.api.ReinsuranceApiService
import org.openapitools.model.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReinsuranceApiServiceImpl @Autowired constructor(rpc:NodeRPCConnection) : ReinsuranceApiService {

    companion object{
        private val logger = LoggerFactory.getLogger(ReinsuranceApiServiceImpl::class.java)
    }

    private val proxy = rpc.proxy
    override fun reinsuranceGet(status: String): ListDetailResponse {
     var list = ListDetailResponse()
     list.add(
         DetailResponse(
          issuer = "O=PartyA,L=Sao Paulo,C=BR",
          register = "O=PartyB,L=Sao Paulo,C=BR",
          reinsurer = "O=PartyC,L=Sao Paulo,C=BR",
             contractId = "12345",
         ledgerHashKey = "23455566",
             status = "APPROVED"
     ))
        return list
    }

    override fun reinsuranceIdGet(id: String): DetailResponse {
        return DetailResponse(
            issuer = "O=PartyA,L=Sao Paulo,C=BR",
            register = "O=PartyB,L=Sao Paulo,C=BR",
            reinsurer = "O=PartyC,L=Sao Paulo,C=BR",
            contractId = "12345",
            ledgerHashKey = "23455566",
            status = "APPROVED"
        )
    }

    override fun reinsuranceIdPatch(id: String, body: StatusRequest): ReinsuranceResponse {
        return ReinsuranceResponse(
            contractId = "12345",
            ledgerHashKey = "23455566"
        )
    }

    private fun getParty(partyName: String): Party {
        return proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(partyName))
            ?: throw IllegalArgumentException("Unkown party name $partyName")
    }
    override fun reinsurancePost(body: ReinsuranceRequest): ReinsuranceResponse {
        logger.info("Running reinsurancePost")

        val reinsurer = getParty(body.reinsurer)
        val register = getParty(body.register)

        val flowHandle = proxy.startTrackedFlowDynamic(CreateFlow::class.java, reinsurer, register, body.contractId)

        flowHandle.progress.subscribe {
            logger.info("CreatedFlow: $it")
        }

        val result = flowHandle.use {
            it.returnValue.getOrThrow()
        }.tx.outputsOfType<ReinsuranceState>().single()

        logger.info("Result is $result")

        return ReinsuranceResponse(ledgerHashKey = result.linearId.id.toString(), contractId = result.contractId, message = "Registro atualizado com sucesso"
        )
    }


}