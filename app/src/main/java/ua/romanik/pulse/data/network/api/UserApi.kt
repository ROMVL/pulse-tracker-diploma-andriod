package ua.romanik.pulse.data.network.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ua.romanik.pulse.data.network.model.UserDataModel

interface UserApi {

    @GET("/login/{email}")
    suspend fun signIn(@Path("email") email: String): UserDataModel

    @POST("/add")
    suspend fun signUp(@Body userDataModel: UserDataModel): UserDataModel

}