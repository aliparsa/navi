package com.graphhopper.android.Interfaces;


import com.graphhopper.android.DataModel.ServerResponse;

/**
 * Created by ashkan on 8/11/2014.
 */
public interface ResponseHandler {

    public void handleResponse(ServerResponse response);

    public void error(String err);
}