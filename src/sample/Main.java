package sample;

import com.sun.deploy.net.HttpResponse;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.net.www.http.HttpClient;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    ListView lv;
    AnchorPane anchorPane;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("School Guard");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        lv = (ListView) primaryStage.getScene().lookup("#listView");
        anchorPane = (AnchorPane) primaryStage.getScene().lookup("#anchorPane");
        anchorPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("#3F51B5"), null, null)));
        Timer timer = new Timer();
        timer.schedule(new loadStuff(), 0, 5000);

        lv.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.DELETE)){
                    ObservableList observableList = lv.getItems();
                    observableList.remove(lv.getSelectionModel().getSelectedIndex());
                    String message = (String) observableList.get(lv.getSelectionModel().getSelectedIndex());
                    Scanner read = new Scanner(message);
                    read.useDelimiter("|");
                    String id = read.next();
                    System.out.println(id);
                    try {
                        URL url = new URL("http://tristanwiley.com/schoolguard/deleteincident.php?message=" + id);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            InputStream is = conn.getInputStream();
                            // do something with the data here
                        } else {
                            InputStream err = conn.getErrorStream();
                            // err may have useful information.. but could be null see javadocs for more information
                        }



                    }catch(Exception e){

                    }
                }
            }
        });
        lv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2) {
                    ObservableList observableList = lv.getItems();
                    String message = (String) observableList.get(lv.getSelectionModel().getSelectedIndex());

                    Alert alert = new Alert(Alert.AlertType.NONE);
                    alert.setTitle("More About Incident");
                    alert.setContentText(message);
                    ButtonType buttonClose = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(buttonClose);
                    alert.show();
                }
            }
        });

    }


    public static void main(String[] args) {
        launch(args);
    }

    class loadStuff extends TimerTask{
        public void run(){
            try {
                String json = getJson("http://tristanwiley.com/schoolguard/getincidents.php");

                JSONArray jsonArray = new JSONArray(json);

                ArrayList<String> items = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String building = jsonObject.getString("building");
                    String location = jsonObject.getString("location");
                    String message = jsonObject.getString("message");
                    items.add(id + "|There was an incident in the " + building + ".  It occurred at " + location + ".  The student said '" + message);
                }


                ObservableList<String> listItems = FXCollections.observableArrayList(items);
                lv.setItems(listItems);
            }catch(Exception e){

            }
        }
    }

    private static String getJson(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }




}