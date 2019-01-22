package Project;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.application.Application;
import javafx.stage.Stage;

public class ChatApp extends Application{

    private TextArea messages = new TextArea("Welcome To The Chat!\n");
    private boolean isServer = true;
    private Network connection = isServer ? createServer() : createClient();


    public static void main(String[] args){
        launch(args);
    }
    public void init() throws Exception{
        connection.startConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        if(isServer == true){
            primaryStage.setTitle("Chat (Server)");
        }
        else{
            primaryStage.setTitle("Chat (Client)");
        }
        if(isServer == false){
            messages.appendText("Client has joined the Chat!\n\n");
        }
        else{
            messages.appendText("Chat Room is now open!\n\n");
        }
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();

    }
    public void stop() throws Exception{
        connection.closeConnection();
    }

    private Parent createContent(){
        messages.setPrefHeight(550);
        TextField input = new TextField();
        input.setOnAction(e -> {
            String message = isServer ? "Server: " : "Client: ";
            message += input.getText();
            input.clear();

            messages.appendText(message+"\n");
            try {
                connection.send(message);
            } catch (Exception e1){
                messages.appendText("Failed to send\n");
            }
        });
        input.setPromptText("Enter your message");
        VBox root = new VBox(20, messages, input);
        root.setPrefSize(600,600);
        return root;
    }

    private Server createServer(){
        return new Server(55555, data -> {
            Platform.runLater(()->{
                messages.appendText(data.toString() + "\n");
            });
        });
    }
    private Client createClient(){
        return new Client("127.0.0.1",55555, data ->{
            messages.appendText(data.toString()+"\n");
        });
    }

}
