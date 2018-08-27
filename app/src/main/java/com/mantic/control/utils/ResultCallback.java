package com.mantic.control.utils;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public abstract class ResultCallback {
    public abstract void onResponse(Response response) throws IOException;
    public abstract void onError(Request request,Exception ex);
}
