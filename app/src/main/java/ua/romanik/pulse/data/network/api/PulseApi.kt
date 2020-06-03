package ua.romanik.pulse.data.network.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ua.romanik.pulse.data.network.model.PulseDataModel
import ua.romanik.pulse.data.network.model.PulseRequestModel

interface PulseApi {

    @GET("/pulse/get/{email}")
    suspend fun getPulse(@Path("email") email: String): List<PulseDataModel>

    @POST("/pulse/set")
    suspend fun setPulse(@Body data: PulseRequestModel)

}