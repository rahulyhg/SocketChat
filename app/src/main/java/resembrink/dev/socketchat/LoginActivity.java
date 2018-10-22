package resembrink.dev.socketchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity {


    private EditText mUserNameView;
    private String mUsername;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ChatApplication app= (ChatApplication) getApplication();

        mSocket=app.getSocket();
        mUserNameView=findViewById(R.id.username_input);
        mUserNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return false;
            }
        });

        Button signInButton = findViewById(R.id.sign_in_button);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempLogin();
            }
        });
        
        mSocket.on("login", onLogin);
    }

    private void attempLogin() {
        
        mUserNameView.setError(null);
        
        String username= mUserNameView.getText().toString();
        
        if(TextUtils.isEmpty(mUsername))
        {
            mUserNameView.setError(" this field is required");
            mUserNameView.requestFocus();
            return;
        }
        
        mUsername = username;
        mSocket.emit("add user", username);
        


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("login", onLogin);
    }

    private  Emitter.Listener onLogin= new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject data= (JSONObject) args[0];
            
            int  numUsers = 0;

            try {
                numUsers= data.getInt("numUsers");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
            Intent intent= new Intent();
            intent.putExtra("username", mUsername);
            intent.putExtra("numUsers",numUsers);
            setResult(RESULT_OK, intent);
            finish();
        }
    };
    
    
}
