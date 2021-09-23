package com.template.webserver.services

import com.template.webserver.NodeRPCConnection
import org.openapitools.api.ReinsuranceApiService
import org.openapitools.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReinsuranceApiServiceImpl @Autowired constructor(rpc:NodeRPCConnection) : ReinsuranceApiService {
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

    override fun reinsurancePost(body: ReinsuranceRequest): ReinsuranceResponse {
        return ReinsuranceResponse(
            contractId = "12345",
            ledgerHashKey = "23455566"
        )
    }


}