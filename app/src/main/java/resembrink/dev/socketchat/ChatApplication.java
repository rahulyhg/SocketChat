package resembrink.dev.socketchat;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import android.app.Application;

public class ChatApplication extends Application {

    private Socket mSocket;

    {
        try {
            mSocket= IO.socket(Constantes.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket()
    {
        return mSocket;
    }
}
