package com.example.shivamagrawal.photoshareapp.Objects;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Server {

    private static String baseURL = "http://10.0.0.11:3000";
    private static String getFullURL(String extension) { return baseURL + extension; }

    // Sign Up, LoginActivity, Verify
    public static String signupURL = getFullURL("/api/users/signup");
    public static String loginURL = getFullURL("/api/users/login");
    public static String verifyURL = getFullURL("/api/users/verify");

    // Groups and events
    public static String createGroupURL = getFullURL("/api/groups/newGroup");
    public static String createEventURL = getFullURL("/api/groups/createEvent");
    public static String getAllGroupsURL = getFullURL("/api/groups/getAllGroups");
    public static String getGroupURL = getFullURL("/api/groups/getGroup");
    public static String addMembersURL = getFullURL("/api/groups/addMembers");
    public static String deleteGroupURL = getFullURL("/api/groups/deleteGroup");
    public static String deleteEventURL = getFullURL("/api/groups/deleteEvent");

    // Photos
    public static String uploadPhotoURL = getFullURL("/api/photos/upload");
    public static String getPhotoURL = getFullURL("/api/photos/get");
    public static String getAllPhotosURL = getFullURL("/api/photos/getAll");

    // StringRequest Generators

    public static StringRequest POST(final Map<String, String> params, String url,
                                     Response.Listener<String> cbSuccess, Response.ErrorListener cbError) {
        return new StringRequest(Request.Method.POST, url, cbSuccess, cbError){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }
        };
    }

    public static StringRequest GET(final Map<String, String> params, String url,
                                    Response.Listener<String> cbSuccess, Response.ErrorListener cbError) {
        return new StringRequest(Request.Method.GET, url, cbSuccess, cbError) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = params;
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }
        };
    }

}
