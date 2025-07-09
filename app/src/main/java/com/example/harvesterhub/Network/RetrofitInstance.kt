import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api: MarketApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.data.gov.in/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))  // <- Moshi with Kotlin adapter
            .build()
            .create(MarketApiService::class.java)
    }
}
