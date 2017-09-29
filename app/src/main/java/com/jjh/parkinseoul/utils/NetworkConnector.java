package com.jjh.parkinseoul.utils;

import com.jjh.parkinseoul.service.BoardService;
import com.jjh.parkinseoul.service.LogCollectService;
import com.jjh.parkinseoul.service.NaverImageAPIService;
import com.jjh.parkinseoul.service.NearSubwayService;
import com.jjh.parkinseoul.service.ParkAPIService;
import com.jjh.parkinseoul.service.ProgramAPIService;
import com.jjh.parkinseoul.service.ProgramInfoService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by JJH on 2016-08-21.
 */
public class NetworkConnector {

    private final String PARK_API_AUTH_KEY = "77416d776b6a6a68373958426f7364"; //공원 API 인증키
    private final String BASE_URL = "http://openAPI.seoul.go.kr:8088/" + PARK_API_AUTH_KEY + "/json/";

    /**
     * 공원정보 API 서비스
     */
    public ParkAPIService getParkAPIService(){
        OkHttpClient client = getDefaultOkHttpClientBuilder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ParkAPIService service =  retrofit.create(ParkAPIService.class);
        return service;
    }

    /**
     * 프로그램 정보 API 서비스
     */
    public ProgramAPIService getProgramAPIService(){
        OkHttpClient client =getDefaultOkHttpClientBuilder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ProgramAPIService service =  retrofit.create(ProgramAPIService.class);
        return service;
    }


    private final String NAVER_IMAGE_API_CLIENT_ID = "test"; //네이버 API 인증키
    private final String NAVER_IMAGE_API_CLIENT_SECRET = "test"; //네이버 ClientId값

    /**
     * 네이버 이미지 검색 API
     */
    public NaverImageAPIService getNaverImageAPIService(){
        OkHttpClient client = getDefaultOkHttpClientBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                                    .addHeader("X-Naver-Client-Id", NAVER_IMAGE_API_CLIENT_ID)
                                    .addHeader("X-Naver-Client-Secret", NAVER_IMAGE_API_CLIENT_SECRET).build();
                return chain.proceed(request);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://openapi.naver.com/v1/search/")
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        NaverImageAPIService service =  retrofit.create(NaverImageAPIService.class);
        return service;
    }


    /**
     * 주변 지하철 API
     */
    public NearSubwayService getNearSubwayAPIService(){
        OkHttpClient client = getDefaultOkHttpClientBuilder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://swopenapi.seoul.go.kr/api/subway/" + PARK_API_AUTH_KEY + "/json/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NearSubwayService service =  retrofit.create(NearSubwayService.class);
        return service;
    }

    /**
     * 게시판
     */
    public BoardService getBoardService(){
        OkHttpClient client = getDefaultOkHttpClientBuilder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.35.88.244:9090/pis/board/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BoardService service =  retrofit.create(BoardService.class);
        return service;
    }

    /**
     * 프로그램 상세정보 조회
     */
    public ProgramInfoService getProgramInfoService(){
        OkHttpClient client = getDefaultOkHttpClientBuilder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.35.88.244:9090/pis/program/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ProgramInfoService service =  retrofit.create(ProgramInfoService.class);
        return service;
    }

    public LogCollectService getExceptionLogService(){
        OkHttpClient client = getDefaultOkHttpClientBuilder().build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.35.88.244:9090/pis/data/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LogCollectService service =  retrofit.create(LogCollectService.class);
        return service;
    }

    private OkHttpClient.Builder getDefaultOkHttpClientBuilder(){
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return  new OkHttpClient.Builder().addInterceptor(logInterceptor).readTimeout(60, TimeUnit.SECONDS).connectTimeout(60,TimeUnit.SECONDS);
    }
}
