package com.example.temanbaik_kila

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// 1. Model Tabel Users
data class UserModel(
    val id: Long? = null,
    val nama: String,
    val email: String,
    val password: String
)

data class UserInsertRequest(
    val nama: String,
    val email: String,
    val password: String
)

// 2. Model Tabel Campaigns (CRUD 1)
data class CampaignModel(
    val id: Long? = null,
    val judul: String,
    val kategori: String,
    val terkumpul: Long,
    val target: Long
)

data class CampaignInsertRequest(
    val judul: String,
    val kategori: String,
    val terkumpul: Long,
    val target: Long
)

// 3. Model Tabel Donations (CRUD 2)
data class DonationModel(
    val id: Long? = null,
    val nama_donatur: String,
    val judul_program: String,
    val nominal: Long,
    val total_bayar: Long,
    val status: String
)

data class DonationInsertRequest(
    val nama_donatur: String,
    val judul_program: String,
    val nominal: Long,
    val total_bayar: Long,
    val status: String
)

// Interface REST API Supabase
interface SupabaseApi {
    // --- USERS (REGISTER & LOGIN) ---
    @POST("rest/v1/users")
    suspend fun registerUser(@Body user: UserInsertRequest): Response<Unit>

    @GET("rest/v1/users")
    suspend fun loginUser(
        @Query("email") emailFilter: String,
        @Query("password") passwordFilter: String
    ): Response<List<UserModel>>

    // --- FITUR CRUD 1: CAMPAIGNS ---
    @GET("rest/v1/campaigns?select=*&order=id.desc")
    suspend fun getCampaigns(): Response<List<CampaignModel>>

    @POST("rest/v1/campaigns")
    suspend fun createCampaign(@Body campaign: CampaignInsertRequest): Response<Unit>

    @PATCH("rest/v1/campaigns")
    suspend fun updateCampaign(
        @Query("id") idFilter: String,
        @Body campaign: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @DELETE("rest/v1/campaigns")
    suspend fun deleteCampaign(@Query("id") idFilter: String): Response<Unit>

    // --- FITUR CRUD 2: DONATIONS ---
    @GET("rest/v1/donations?select=*&order=id.desc")
    suspend fun getDonations(): Response<List<DonationModel>>

    @POST("rest/v1/donations")
    suspend fun createDonation(@Body donation: DonationInsertRequest): Response<Unit>

    @PATCH("rest/v1/donations")
    suspend fun updateDonation(
        @Query("id") idFilter: String,
        @Body donation: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @DELETE("rest/v1/donations")
    suspend fun deleteDonation(@Query("id") idFilter: String): Response<Unit>
}

// Konfigurasi Koneksi ke Supabase (Tanpa Cache agar Real-Time)
object SupabaseClient {
    private const val SUPABASE_URL = "https://rznharxufwekgrkezykp.supabase.co/"
    private const val SUPABASE_KEY = "sb_publishable_nywrASYuLrMd9hnusCtu-g_OnpFmLbs"

    private val client = OkHttpClient.Builder()
        .cache(null)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .addHeader("Cache-Control", "no-cache, no-store, must-revalidate")
                .addHeader("Pragma", "no-cache")
                .build()
            chain.proceed(request)
        }
        .build()

    val api: SupabaseApi by lazy {
        Retrofit.Builder()
            .baseUrl(SUPABASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApi::class.java)
    }
}