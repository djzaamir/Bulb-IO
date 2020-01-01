package com.andromeda.djzaamir.bulb_io;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
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
    private final String toggle_url = "http://192.168.0.111/toggle";
    private final String status_url = "http://192.168.0.111/status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        toggle_button.setEnabled(false);

        status_text.setText(MSG_PROCESSING);
        StringRequest bulb_toggle_request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Integer.valueOf(response) == 0) {
                    status_text.setText(STATUS_ON);
                    title_text.setTextColor(Color.BLACK);
//                    layout.setBackgroundColor(Color.parseColor("#B0CA6D"));

                    //Background color fadder
                    ObjectAnimator colorFade = ObjectAnimator.ofObject(layout, "backgroundColor", new ArgbEvaluator(),                      Color.BLACK , Color.parseColor("#B0CA6D") );
                    colorFade.setDuration(1000);
                    colorFade.start();


                } else if (Integer.valueOf(response) == 1) {
                    status_text.setText(STATUS_OFF);
                    title_text.setTextColor(Color.WHITE);
//                    layout.setBackgroundColor(Color.BLACK);

                     ObjectAnimator colorFade = ObjectAnimator.ofObject(layout, "backgroundColor", new ArgbEvaluator(),                      Color.parseColor("#B0CA6D") , Color.BLACK );
                    colorFade.setDuration(1000);
                    colorFade.start();

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


        //Add to volley queue, so it may be processed
        volley_request_queue.add(bulb_toggle_request);
    }
}
