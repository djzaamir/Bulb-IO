package com.andromeda.djzaamir.bulb_io;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    private TextView status_text;
    private Button toggle_button;
    private RequestQueue volley_request_queue;
    private String MSG_PROCESSING   = "Getting Status...";
    private final String STATUS_ON  = "Status : ON";
    private final String STATUS_OFF = "Status : OFF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get UI references
        status_text = findViewById(R.id.status_text);
        toggle_button = findViewById(R.id.toggle_button);

        //Perform any pre-processing on the UI
        status_text.setText(MSG_PROCESSING);

        //instantiate volley request queue
        volley_request_queue = Volley.newRequestQueue(this);

        sendVolleyRequest( "http://192.168.0.111/status");
    }

    public void sendToggle(View view) {
        sendVolleyRequest("http://192.168.0.111/toggle");
    }

    private void sendVolleyRequest(String url) {
        toggle_button.setEnabled(false);

        status_text.setText(MSG_PROCESSING);
        StringRequest bulb_toggle_request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Integer.valueOf(response) == 0) {
                    status_text.setText(STATUS_ON);
                } else if (Integer.valueOf(response) == 1) {
                    status_text.setText(STATUS_OFF);
                }
                toggle_button.setEnabled(true);
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError){
                    status_text.setText("Connection Timeout");
                }else if(error instanceof NoConnectionError){
                    status_text.setText("No Connect");
                }else if(error instanceof ServerError){
                    status_text.setText("Server Error");
                }else if(error instanceof NetworkError){
                    status_text.setText("Network Error");
                }else if(error instanceof ParseError){
                    status_text.setText("Parse Error");
                }
                status_text.setEnabled(true);
            }
        });


        //Add to volley queue, so it may be processed
        volley_request_queue.add(bulb_toggle_request);
    }
}
