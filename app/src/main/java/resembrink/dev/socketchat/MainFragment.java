package resembrink.dev.socketchat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import java.util.List;
import java.util.ArrayList;
import android.os.Handler;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */

public class MainFragment extends Fragment {

    private static final String TAG="MainFragment";
    private static  final int REQUEST_LOGIN=0;
    private static final int TYPING_TIMER_LENGTH=600;

    private RecyclerView mMessageView;
    private EditText mInputMessageView;
    private List<Message> mMessages= new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private boolean mTyping = false;
    private Handler mTypingHandler = new  Handler();
    private String mUsername;
    private Socket mSocket;


    private  Boolean isConnected = true;

    public MainFragment() {
        // Required empty public constructor
        super();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mAdapter = new MessageAdapter(context, mMessages);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        ChatApplication app= (ChatApplication) getActivity().getApplication();
        mSocket= app.getSocket();

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(mSocket.EVENT_CONNECT_ERROR,onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);


        mSocket.on("new Message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);

        mSocket.connect();
        startSignIn();

    }

    private void startSignIn() {

        mUsername= null;
        Intent intent= new Intent(getActivity(),LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
        }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.off(mSocket.EVENT_CONNECT_ERROR,onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);


        mSocket.off("new Message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
    }

    private Emitter.Listener onStopTyping= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data= (JSONObject) args[0];

                    String username;
                    String message;
                    int numUsers;

                    try {
                        username= data.getString("username");

                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                        e.printStackTrace();
                    }

                    //removeTyping(username);

                }
            });

        }
    };



    private Emitter.Listener onTyping= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data= (JSONObject) args[0];

                    String username;
                    String message;
                    int numUsers;

                    try {
                        username= data.getString("username");

                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                        e.printStackTrace();
                    }

                    //addTyping(username);

                }
            });

        }
    };


    private Emitter.Listener onUserLeft= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data= (JSONObject) args[0];

                    String username;
                    String message;
                    int numUsers;

                    try {
                        username= data.getString("username");
                        numUsers=data.getInt("numUsers");

                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                        e.printStackTrace();
                    }


                    //addLog("%s joined");
                    //addParticipantsLog(numUsers);
                    //removeTyping(username);



                }
            });

        }
    };




    private Emitter.Listener onUserJoined= new Emitter.Listener() {
        //se ha unido
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data= (JSONObject) args[0];

                    String username;
                    String message;
                    int numUsers;

                    try {
                        username= data.getString("username");
                        numUsers=data.getInt("numUsers");

                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                        e.printStackTrace();
                    }


                    //addLog("%s joined");
                    //addParticipantsLog(numUsers);



                }
            });

        }
    };


    private Emitter.Listener onNewMessage= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data= (JSONObject) args[0];

                    String username;
                    String message;

                    try {
                        username= data.getString("username");
                        message= data.getString("message");

                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                        e.printStackTrace();
                    }

                    //removeTyping(username);
                    //addMessage(username,message);



                }
            });

        }
    };

    private Emitter.Listener onConnectError =new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.i(TAG, "error connecting");
            Toast.makeText(getActivity().getApplicationContext(), "Failed to connect", Toast.LENGTH_SHORT).show();



        }
    };

    private Emitter.Listener onDisconnect= new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "disconnect");
                    isConnected= false;
                    Toast.makeText(getActivity().getApplicationContext(), "Disconect", Toast.LENGTH_SHORT).show();
                }
            });

        }
    };

    private Emitter.Listener onConnect= new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(isConnected)
                    {
                        if(null!= mUsername)
                        {
                            mSocket.emit("add user", mUsername);
                            Toast.makeText(getActivity().getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                            isConnected=true;
                        }
                    }
                }
            });

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

}
