package com.example.chosrat.hmandroid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chosrat.hmandroid.net.ServerConnection;


public class MainActivity extends AppCompatActivity {

    Handler handler;
    TextView textruta;
    private ServerConnection serverConnection;

    //Skapa grundsidan för applikationen.
    //setContentView hämtar Actiwity_main och visar upp den.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //Efter onCreate så körs onStart. Hämtar standardview som finns i textView.
    //en handler skapas och skapar en funktion handleMessage och run skrivs över för att göra som vi vill.
    //Sen Connectar vi till servern
    @Override
    protected void onStart() {
        super.onStart();

        textruta = (TextView) findViewById(R.id.textView);
        handler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Bundle bundle = msg.getData();
                        String key = bundle.getString("KEY");

                        textruta.setText(key);
                    }
                });
            }
        };
        new ConnectToServer().execute();
    }

    public void sendMessage(View view){
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        editText.setText(null);
        new SendMessageToServer().execute(message);
    }
    //Här skapas uppkopplingen och handlern som skapades tidigare skickas till
    //en listener som hanterar det som skickas från servern.
    private class ConnectToServer extends AsyncTask<Void, Void, ServerConnection> {

        @Override
        protected ServerConnection doInBackground(Void...voids ) {
            ServerConnection serverConnection = new ServerConnection();
            serverConnection.connect();
            serverConnection.createListener(handler);
            return serverConnection;
        }

        @Override
        protected void onPostExecute(ServerConnection serverConnection){
            MainActivity.this.serverConnection = serverConnection;
        }
    }
    //tar emot en sträng och skickar denna till servern.
    private class SendMessageToServer extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            serverConnection.send(params[0]);
            return null;
        }
    }
}
