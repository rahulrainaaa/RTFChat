package chat.realtime.org.rtfchat;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ibt.ortc.api.Ortc;
import ibt.ortc.extensibility.OnConnected;
import ibt.ortc.extensibility.OnMessage;
import ibt.ortc.extensibility.OrtcClient;
import ibt.ortc.extensibility.OrtcFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    OrtcFactory factory;
    OrtcClient client;

    private TextView textView;
    private Button button;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(this);

        Ortc ortc = new Ortc();

        try {
            factory = ortc.loadOrtcFactory("IbtRealtimeSJ");
            client = factory.createClient();
            client.setClusterUrl("http://ortc-developers.realtime.co/server/2.1");
            client.connect("jRCDxz", "3afxcv4ymzzsfmovdon22kmh");
        } catch (Exception e) {
            System.out.println(String.format("Realtime Error: %s", e.toString()));
            Toast.makeText(this, "EXCEPTION:\nRealTime Errors.", Toast.LENGTH_SHORT).show();
        }

        client.onConnected = new OnConnected() {
            @Override
            public void run(final OrtcClient sender) {
                // Messaging client connected
                System.out.println("\n\n***************client connected******************\n\n");
                // Now subscribe the channel
                client.subscribe("myChannel", true,
                        new OnMessage() {
                            // This function is the message handler
                            // It will be invoked for each message received in myChannel
                            public void run(OrtcClient sender, String channel, String message) {
                                // Received a message
                                System.out.println(message);
                                final String msgg = "" + message;
                                Handler handler = new Handler(MainActivity.this.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(msgg);
                                        Toast.makeText(getApplication(), "Received message.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }
        };
    }

    @Override
    public void onClick(View v) {

        String str = editText.getText().toString();
        client.send("myChannel", "" + str);
    }
}
