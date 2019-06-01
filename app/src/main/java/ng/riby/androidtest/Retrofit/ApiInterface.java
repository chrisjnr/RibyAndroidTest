package ng.riby.androidtest.Retrofit;

import java.util.Map;

import ng.riby.androidtest.Retrofit.models.DistanceParams;
import ng.riby.androidtest.Retrofit.models.GoogleDistanceApiResponseModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Manuel Chris-Ogar on 6/1/2019.
 */
public interface ApiInterface {

  @GET("{outputFormat}")
    Call<GoogleDistanceApiResponseModel> getDistance(@Path("outputFormat") String outputFormat,
                                                     @QueryMap Map<String, String> map);
}
