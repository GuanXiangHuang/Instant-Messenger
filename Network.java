package Project;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
public abstract class Network {

    private ConnectionThread conThread = new ConnectionThread();
    private Consumer<Serializable> onReceiveCallBack;

    public Network(Consumer<Serializable> onReceiveCallBack){
        this.onReceiveCallBack = onReceiveCallBack;
        conThread.setDaemon(true);
    }

    public void startConnection() throws Exception{
        conThread.start();
    }
    public void send(Serializable data) throws Exception{
        conThread.out.writeObject(data);
    }
    public void closeConnection() throws Exception{
        conThread.socket.close();
    }

    protected abstract boolean isServer();
    protected abstract String getIP();
    protected abstract int getPort();

    private class ConnectionThread extends Thread{
        private Socket socket;
        private ObjectOutputStream out;
        @Override
        public void run(){
            try(ServerSocket server = isServer() ? new ServerSocket(getPort()): null;
                Socket socket = isServer() ? server.accept() : new Socket(getIP(),getPort());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())){

                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);

                while(true){
                    Serializable data = (Serializable) in.readObject();
                    onReceiveCallBack.accept(data);
                }
            }
            catch (Exception e){
                onReceiveCallBack.accept("Connection Closed\n");
            }
        }
    }
}
