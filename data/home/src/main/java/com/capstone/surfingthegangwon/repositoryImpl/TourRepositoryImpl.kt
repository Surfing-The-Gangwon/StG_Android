package com.capstone.surfingthegangwon.repositoryImpl

import com.capstone.surfingthegangwon.dto.HubPlace
import com.capstone.surfingthegangwon.dto.HubResult
import com.capstone.surfingthegangwon.api.TourApiService
import com.capstone.surfingthegangwon.repository.TourRepository
import javax.inject.Inject

class TourRepositoryImpl @Inject constructor(
    private val api: TourApiService
) : TourRepository {

    override suspend fun getHubs(
        baseYm: String,
        areaCd: String,
        signguCd: String,
        pageNo: Int,
        numOfRows: Int
    ): HubResult {
        val res = api.getRelatedTourList(
            baseYm = baseYm,
            areaCd = areaCd,
            signguCd = signguCd,
            pageNo = pageNo,
            numOfRows = numOfRows
        )

        val body = res.response?.body
        val items = body?.items?.item.orEmpty().map { i ->
            HubPlace(
                name = i.hubTatsNm.orEmpty(),
                categoryMid = i.hubCtgryMclsNm,
                lat = i.mapY?.toDoubleOrNull(),
                lng = i.mapX?.toDoubleOrNull()
            )
        }

        return HubResult(
            total = body?.totalCount ?: items.size,
            pageNo = body?.pageNo ?: pageNo,
            numOfRows = body?.numOfRows ?: numOfRows,
            items = items
        )
    }
}