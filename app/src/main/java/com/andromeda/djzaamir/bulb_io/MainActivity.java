package com.andromeda.djzaamir.bulb_io;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

    private ConstraintLayout layout;

    private TextView status_text , title_text;
    private Button toggle_button;
    private RequestQueue volley_request_queue;
    private String MSG_PROCESSING = "Getting Status...";
    private final String STATUS_ON = "Status : ON";
    private final String STATUS_OFF = "Status : OFF";
    private final String base_url = "http://192.168.1.225";
    private String toggle_url;
    private String status_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //prepare Http routes
        toggle_url = base_url + "/toggle";
        status_url = base_url + "/status";

        //Keep the Screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //get UI references
        layout = findViewById(R.id.main_container);
        status_text = findViewById(R.id.status_text);
        title_text = findViewById(R.id.bulb_io_title);
        toggle_button = findViewById(R.id.toggle_button);

        //Perform any pre-processing on the UI
        status_text.setText(MSG_PROCESSING);

        //instantiate volley request queue
        volley_request_queue = Volley.newRequestQueue(this);

        sendVolleyRequest(status_url);
     }

    public void sendToggle(View view) {
        sendVolleyRequest(toggle_url);
    }

    private void sendVolleyRequest(String url) {
        //Pre-request preparations
        toggle_button.setEnabled(false);
        status_text.setText(MSG_PROCESSING);

        StringRequest bulb_toggle_request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Integer.valueOf(response) == 0) {
                    status_text.setText(STATUS_ON);
                    title_text.setTextColor(Color.BLACK);
                    backgroundCrossFadeAnimation(Color.BLACK, Color.parseColor("#B0CA6D") ,layout , 1000);
                } else if (Integer.valueOf(response) == 1) {
                    status_text.setText(STATUS_OFF);
                    title_text.setTextColor(Color.WHITE);
                    backgroundCrossFadeAnimation(Color.parseColor("#B0CA6D"), Color.BLACK, layout, 1000);
                }
                toggle_button.setEnabled(true);
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    status_text.setText("Connection Timeout");
                } else if (error instanceof NoConnectionError) {
                    status_text.setText("No Connect");
                } else if (error instanceof ServerError) {
                    status_text.setText("Server Error");
                } else if (error instanceof NetworkError) {
                    status_text.setText("Network Error");
                } else if (error instanceof ParseError) {
                    status_text.setText("Parse Error");
                }
                toggle_button.setEnabled(true);
            }
        });

         //Turn of Volley caching, Effectively forcing volley to always make new requests
        bulb_toggle_request.setShouldCache(false);

        //Add to volley queue, so it may be processed
        volley_request_queue.add(bulb_toggle_request);
    }

    private void backgroundCrossFadeAnimation(int from_color, int to_color, ConstraintLayout layout, int duration_ms) {
        ObjectAnimator animator =  ObjectAnimator.ofObject(layout,
                                                "backgroundColor",
                                                            new ArgbEvaluator(),
                                                            from_color,
                                                            to_color
                                                            );
        animator.setDuration(duration_ms);
        animator.start();
    }
}
