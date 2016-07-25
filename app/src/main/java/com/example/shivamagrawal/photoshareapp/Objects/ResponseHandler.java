package com.example.shivamagrawal.photoshareapp.Objects;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.widget.Toast;

public class ResponseHandler {

    // here, handle responses from requests (succesfull or not)
    // and also have a static error method w/ a toast for non server (my fault) errors


    // if error, make toast from err message

    private String res;
    private JSONObject body;
    private JSONObject error;

    public ResponseHandler(String res) {
        this.res = res;
    }

    private void parseRes() {
        try {
            JSONObject results = new JSONObject(res);
            if (results.getBoolean("success")) {
                body = results;
            } else {
                error = results;
            }
        } catch (JSONException e) {
            body = null;
            e.printStackTrace();
        }
    }

    // TODO need to work on sending the error if there is one

    public JSONObject getBody() {
        return body;
    }

    public static void errorToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
