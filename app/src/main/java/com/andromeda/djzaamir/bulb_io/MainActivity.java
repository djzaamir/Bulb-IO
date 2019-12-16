package com.andromeda.djzaamir.bulb_io;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    private TextView status_text;
    private Button toggle_button;
    private RequestQueue volley_request_queue;
    private String MSG_PROCESSING = "Processing...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get UI references
        status_text   = findViewById(R.id.status_text);
        toggle_button = findViewById(R.id.toggle_button);

        //Perform any pre-processing on the UI
        status_text.setText(MSG_PROCESSING);

        //instantiate volley request queue
        volley_request_queue = Volley.newRequestQueue(this);
        String url = "http://192.168.0.111/status";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Integer.valueOf(response) == 0){
                    status_text.setText("ON");
                }else if (Integer.valueOf(response) == 1){
                    status_text.setText("OFF");
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                status_text.setText("Error : " + error.networkResponse.statusCode);
            }
        });

        //add to volley background thread queue
        volley_request_queue.add(stringRequest);
    }

    public void sendToggle(View view) {

        toggle_button.setEnabled(false);

        status_text.setText(MSG_PROCESSING);
        String url = "http://192.168.0.111/toggle";
        StringRequest bulb_toggle_request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Integer.valueOf(response) == 0){
                    status_text.setText("ON");
                }else if (Integer.valueOf(response) == 1){
                    status_text.setText("OFF");
                }
                toggle_button.setEnabled(true);
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                status_text.setText("Error : " + error.networkResponse.statusCode);
                status_text.setEnabled(true);
            }
        });
        volley_request_queue.add(bulb_toggle_request);
    }
}
