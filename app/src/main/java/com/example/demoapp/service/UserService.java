package com.example.demoapp.service;

import com.example.demoapp.modal.Users;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserService
{
    @GET("api/users")
    Call<Users> getUsers(
            @Query("page") int pageIndex,
            @Query("delay") int delay
    );
}
