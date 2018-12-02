package org.ieszaidinvergeles.dam.chaterbot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.ieszaidinvergeles.dam.chaterbot.api.ChatterBot;
import org.ieszaidinvergeles.dam.chaterbot.api.ChatterBotFactory;
import org.ieszaidinvergeles.dam.chaterbot.api.ChatterBotSession;
import org.ieszaidinvergeles.dam.chaterbot.api.ChatterBotType;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static org.ieszaidinvergeles.dam.chaterbot.ChatLogs.CHAT_LOG_PICK;

//https://github.com/pierredavidbelanger/chatter-bot-api

//Instrucciones:

//Los registros del chat se deben guardar en una base de datos.

//Habrá dos tablas, la tabla conversación, que tendrá un ID y una fecha, y una tabla chat, con los campos id, idconversación, quien, texto

//En recyclerview deben salir las diferentes fechas, y al pinchar en una fecha deben verse las conversaciones de ese día.

//Fecha límite: 12 de noviembre

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MITAG";
    private Button btSend;
    private EditText etTexto;
    private ScrollView svScroll;
    private TextView tvTexto;
    private Switch aSwitch;
    public static ChatManager chatManager;
    private static FireBaseConnection fbc;
    private Chat chat;
    boolean spanish = false;

    private ChatterBot bot;
    private ChatterBotFactory factory;
    private ChatterBotSession botSession;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fb);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addChatLog();
                Log.v(TAG, "added");
                Toast.makeText(MainActivity.this,R.string.successText, Toast.LENGTH_SHORT).show();
                toChatLogs();
            }
        });

        init();
    }

    private void addChatLog() {
        chat.setDate(dateFormat.format(date));
        DBManager.insert(chatManager,fbc,chat);
        //long id = chatManager.insert(chat);
        //Log.v(TAG, id + "");
    }

    private void toChatLogs() {
        Intent i = new Intent(MainActivity.this, ChatLogs.class);
        i.putExtra("date", chat.getDate());
        startActivityForResult(i,CHAT_LOG_PICK);
    }

    private void init() {
        btSend = findViewById(R.id.btSend);
        etTexto = findViewById(R.id.etTexto);
        svScroll = findViewById(R.id.svScroll);
        tvTexto = findViewById(R.id.tvTexto);
        aSwitch = findViewById(R.id.switch1);
        fbc = new FireBaseConnection();
        chatManager = new ChatManager(this);
        chat = new Chat();
        if(startBot()) {
            setEvents();
        }
    }

    private String chat(final String text) {
        String response;
        try {
            response = getString(R.string.bot) + " " + botSession.think(text);
        } catch (final Exception e) {
            response = getString(R.string.exception) + " " + e.toString();
        }

        return response;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHAT_LOG_PICK){
                if(resultCode == RESULT_OK){
                chat = data.getParcelableExtra("chat");
                String chatlog = chat.getChatlog();
                tvTexto.setText(chatlog + "\n");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.toChatLogs){
            toChatLogs();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setEvents() {
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = getString(R.string.you) + " " + etTexto.getText().toString().trim();
                btSend.setEnabled(false);
                etTexto.setText("");
                tvTexto.append(text + "\n");
                /*new Thread(){
                    @Override
                    public void run() {
                        chat(text);
                    }
                }.start();*/
                new ChatTask().execute(text);
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    spanish = true;
                } else {
                    spanish = false;
                }
            }
        });
    }

    private boolean startBot() {
        boolean result = true;
        String initialMessage;
        factory = new ChatterBotFactory();
        try {
            bot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            botSession = bot.createSession();
            initialMessage = getString(R.string.messageConnected) + "\n";
        } catch(Exception e) {
            initialMessage = getString(R.string.messageException) + "\n" + getString(R.string.exception) + " " + e.toString();
            result = false;
        }
        tvTexto.setText(initialMessage);
        return result;
    }

    private Runnable showMessage(final String message) {
        return new Runnable() {
            @Override
            public void run() {
                etTexto.requestFocus();
                tvTexto.append(message + "\n");
                svScroll.fullScroll(View.FOCUS_DOWN);
                btSend.setEnabled(true);
                hideKeyboard();
            }
        };
    }

    private String translate(final String text, final String from, final String to){

        AsyncTask task = new AsyncTask() {
            String response = "";
            @Override
            protected Object doInBackground(Object[] objects) {

                String trad = text;
                String link = "&text=";
                 try {
                    URL url = new URL("https://www.bing.com/ttranslate?&category=&IG=51C950C044BE4176885CCAFA7B90FD83&IID=translator.5034.22");
                    URLConnection conexion = url.openConnection();
                    conexion.setDoOutput(true);
                    OutputStreamWriter out = new OutputStreamWriter(
                            conexion.getOutputStream());
                    link += URLEncoder.encode(trad, "UTF-8");
                    link = link.replace("+", "%20");
                    //
                    link +="&from="+from+"&to="+to;
                    out.write(link);
                    out.close();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            conexion.getInputStream()));
                     String linea;
                     while ((linea = in.readLine()) != null) {
                         response += linea;
                     }
                     in.close();
                     JSONObject reader = new JSONObject(response);
                     response = reader.getString("translationResponse");

                     return response;

                }catch (Exception e){
                    System.out.println(e.toString());
                }
                return response;
            }
        };
        try {
            return task.execute().get().toString();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class ChatTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            chat.appendChatLog(params[0] + "\n");
            return chat(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            chat.appendChatLog(result + "\n");
            //tvTexto.post(showMessage(result));
            etTexto.requestFocus();
            if(spanish) {
                result = translate(result, "en", "es");
            }
            tvTexto.append(result + "\n");
            svScroll.fullScroll(View.FOCUS_DOWN);
            btSend.setEnabled(true);
            hideKeyboard();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}