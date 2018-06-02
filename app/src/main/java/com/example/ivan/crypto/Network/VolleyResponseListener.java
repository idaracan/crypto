package com.example.ivan.crypto.Network;

public interface VolleyResponseListener {
    void onError(String message);
    void onResponse(Object response);
}
