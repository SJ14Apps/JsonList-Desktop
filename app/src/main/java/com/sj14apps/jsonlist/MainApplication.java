package com.sj14apps.jsonlist;

import com.sj14apps.jsonlist.controllers.DesktopFileManager;
import com.sj14apps.jsonlist.controllers.DesktopJsonLoader;
import com.sj14apps.jsonlist.controllers.DesktopRawJsonView;
import com.sj14apps.jsonlist.core.AppState;
import com.sj14apps.jsonlist.core.JsonData;
import com.sj14apps.jsonlist.core.JsonFunctions;
import com.sj14apps.jsonlist.core.ListItem;
import com.sj14apps.jsonlist.core.controllers.FileManager;
import com.sj14apps.jsonlist.core.controllers.JsonLoader;
import com.sj14apps.jsonlist.core.controllers.RawJsonView;
import com.sj14apps.jsonlist.core.controllers.WebManager;
import javafx.animation.PauseTransition;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static com.sj14apps.jsonlist.core.JsonFunctions.getListFromPath;

public class MainApplication extends Application {

    Scene scene;
    public MainView controller;

    public JsonData data = new JsonData();

boolean isMenuOpen, isTopMenuVisible, isUrlSearching, isVertical = true;
// todo   PathListAdapter pathAdapter;
// todo   View menu, dim_bg, pathListView;
// todo   AutoTransition autoTransition = new AutoTransition();
// todo   Handler handler = new Handler();
    Thread readFileThread;
    public AppState state;
// todo   int listPrevDx = 0;
    RawJsonView rawJsonView;
    FileManager fileManager;
    WebManager webManager;
    JsonLoader jsonLoader;
// todo   ArrayList<String> filterList = new ArrayList<>();


    @Override
    public void start(Stage stage) throws IOException {
        initialize(stage);
        LoadStateData();
        setEvents();

        //todo animate image and button
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        webManager.shutdownClient();
    }

    private void initialize(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));

        scene = new Scene(fxmlLoader.load(), stage.getWidth(), stage.getHeight());
        controller = fxmlLoader.getController();
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/icon.png"))));
        stage.setTitle("Json List");
        stage.setScene(scene);
        stage.show();
        stage.widthProperty().addListener((o, oldValue, newValue)->{
            if(newValue.intValue() < 1000.0) {
                controller.mainLL.setOrientation(Orientation.VERTICAL);
                controller.rawJsonRL.setPadding(new Insets(0,10,10,10));
            }else {
                controller.mainLL.setOrientation(Orientation.HORIZONTAL);
                controller.rawJsonRL.setPadding(new Insets(10,10,10,0));
            }
        });

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.BACK)
                goBack();
        });

        stage.setMaximized(false);

        //todo colors
        rawJsonView = new DesktopRawJsonView(this,
                scene,
                0x151B2C,
                0x0053DC,
                0x735470,
                0xBA1B1B,
                0xDCE1F9);
        rawJsonView.showJson = true;
        rawJsonView.toggleSplitView();


        rawJsonView.updateRawJson("");

        fileManager = new DesktopFileManager(stage,this);
        jsonLoader = new DesktopJsonLoader(this);
        webManager = new WebManager();



        //todo DragAndDrop

    }



    private void setEvents() {

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE){
                goBack();
            }
        });


//        todo menuBtn.setOnClickListener(view -> open_closeMenu());
        controller.menuBtn.setOnAction(e -> fileManager.importFromFile());

        controller.openFileBtn.setOnAction(e -> fileManager.importFromFile());
        controller.openUrlBtn.setOnAction(e -> controller.showLinkView());
        controller.backBtn.setOnAction(e -> goBack());
        controller.searchUrlBtn.setOnAction(e -> SearchUrl());
        //todo openUrlBtn

        //todo titleTxt.setOnClickListener

        //todo pathListView.setOnClickListener(v -> showHidePathList());

        //todo urlSearch.setOnEditorActionListener

        //todo menu.findViewById(R.id.openFileBtn2).setOnClickListener
        //todo menu.findViewById(R.id.searchUrlBtn).setOnClickListener
        //todo menu.findViewById(R.id.settingsBtn).setOnClickListener
        //todo menu.findViewById(R.id.aboutBtn).setOnClickListener
        //todo menu.findViewById(R.id.logBtn).setOnClickListener
        //todo dim_bg.setOnClickListener(view -> open_closeMenu());
        controller.splitViewBtn.setOnAction(e -> rawJsonView.toggleSplitView());
        //todo filterBtn.setOnClickListener(view -> filter());
    }


    //todo
    public void LoadStateData() {
        boolean prevSH = state != null && state.isSyntaxHighlighting();

        state = new AppState(); //todo FileSystem.loadStateData(this);
        state.setSyntaxHighlighting(true);

        if (rawJsonView.isRawJsonLoaded && prevSH != state.isSyntaxHighlighting()) {
            rawJsonView.isRawJsonLoaded = false;
            if (rawJsonView.showJson)
                rawJsonView.ShowJSON();
        }
    }

    public void loadFileIntoWebView(File file) {
        try {
            fileManager.readFile(new FileInputStream(file),file.getName(),0,fileCallback);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.setProperty("prism.order", "d3d"); // For Windows (Direct3D)
        System.setProperty("prism.order", "es2"); // For OpenGL (Linux/macOS)
        System.setProperty("prism.text", "t2k");
        System.setProperty("javafx.web.enableCef", "true");
        launch();
    }

    public void open(String Title, String path, int previousPosition) {
//   todo     TransitionManager.endTransitions(viewGroup);
//        TransitionManager.beginDelayedTransition(viewGroup, autoTransition);
//
//        if (isMenuOpen)
//            open_closeMenu();
//
//        if (emptyListTxt.getVisibility() == View.VISIBLE)
//            emptyListTxt.setVisibility(View.GONE);
//
//
//
//        pathAdapter = new PathListAdapter(this,path);
//        pathList.setAdapter(pathAdapter);
        data.setPath(path);
        controller.titleTxt.setText(Title);
        ArrayList<ListItem> arrayList = getListFromPath(path,data.getRootList());
        data.setCurrentList(arrayList);
//        updateFilterList(arrayList);
//        adapter = new ListAdapter(arrayList, this, path);
//        list.setAdapter(adapter);
        controller.list.getItems().clear();
        controller.list.getItems().addAll(arrayList);
        controller.list.setCellFactory(this::ListAdapter);
//        list.refresh();
//
        if (previousPosition == -1) {
//  todo          handler.postDelayed(() -> {
//  todo              list.smoothScrollToPosition(data.getPreviousPos()+2);
//  todo              adapter.setHighlightItem(data.getPreviousPos());
//  todo          }, 500);
//  todo          handler.postDelayed(() -> {
//  todo              adapter.notifyItemChanged(data.getPreviousPos());
//  todo          }, 600);
            delay(400,() ->{
                highlightedItem = data.getPreviousPos();
                controller.list.refresh();
                controller.list.scrollTo(data.getPreviousPos()-2);
            });
        }
        else data.addPreviousPos(previousPosition);
//
//  todo      if (arrayList.isEmpty()) {
//  todo          emptyListTxt.setVisibility(View.VISIBLE);
//  todo      }
        if (!path.isEmpty()) {
          controller.showBackBtn();
        } else controller.hideBackBtn();

    }

    public static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try { Thread.sleep(millis); }
                catch (InterruptedException e) { }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }

    private void SearchUrl() {
        String url = controller.urlSearch.getText();
        webManager.getFromUrl(url,webCallback);
    }

    FileManager.FileCallback fileCallback = new FileManager.FileCallback() {

        @Override
        public void onFileLoaded(String data, String fileName) {
            if (data == null) {
                System.out.println("ReadFile: null data");
                return;
            }
            jsonLoader.LoadData(data,fileName,jsonLoaderCallback); //todo thread??
        }

        @Override
        public void onFileLoadFailed() {

        }

        @Override
        public void onProgressUpdate(int progress) {

        }
    };

    JsonLoader.JsonLoaderCallback jsonLoaderCallback = new JsonLoader.JsonLoaderCallback() {


        @Override
        public void start() {
//            loadingStarted(getString(R.string.loading_json));
//            emptyListTxt.setVisibility(View.GONE);
        }

        @Override
        public void started() {
//       todo     handler.post(()-> loadingStarted(getString(R.string.creating_list)));
        }

        @Override
        public void failed() {
//      todo      handler.post(() -> loadingFinished(false));
        }

        @Override
        public void success() {
            Platform.runLater(() -> {
                if (controller.urlView.isVisible())
                    controller.hideUrlSearchView();


                data.setCurrentList(data.getRootList());
//                updateFilterList(data.getRootList());
//                pathAdapter = new PathListAdapter(MainActivity.this,data.getPath());

                controller.list.getItems().clear();
                controller.list.getItems().addAll(data.getRootList());
                controller.list.setCellFactory(listItemListView -> ListAdapter(listItemListView));

//                pathList.setAdapter(pathAdapter);
//                fileImg.clearAnimation();
//                openFileBtn.clearAnimation();
//                fileImg.setVisibility(View.GONE);
                controller.openBtns.setVisible(false);
                controller.list.setVisible(true);
//                functions.setAnimation(MainActivity.this,list,R.anim.scale_in2,new DecelerateInterpolator());
                controller.hideBackBtn();
                controller.titleTxt.setText("");
                data.clearPath();
            });

//            });
        }

        @Override
        public void after() {
            rawJsonView.isRawJsonLoaded = false;
            if (rawJsonView.showJson){
                rawJsonView.ShowJSON();
            }
//          todo  else handler.post(() -> loadingFinished(true));
        }
    };

    WebManager.WebCallback webCallback = new WebManager.WebCallback() {
        @Override
        public void onStarted() {
            controller.hideUrlSearchView();
            //todo loading Txt
            isUrlSearching = true;
        }

        @Override
        public void onInvalidURL() {
            //todo onInvalidURL Txt
        }

        @Override
        public void onResponse(String data) {
            //todo loading Txt
            isUrlSearching = false;

            jsonLoader.LoadData(data,null,jsonLoaderCallback);
        }

        @Override
        public void onFailure() {
            //todo loading Txt
            isUrlSearching = false;
            //todo Fail Txt
        }

        @Override
        public void onFailure(int code) {
            //todo loading Txt
            isUrlSearching = false;
            //todo Fail code Txt
        }
    };

    private int cssColorToHex(String rgbColor) {
        Color color = Color.decode(rgbColor);
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return (r << 16) | (g << 8) | b;
    }

    public int highlightedItem = -1;

    //todo move to separate class
    private ListCell<ListItem> ListAdapter(ListView<ListItem> lv) {
        return new ListCell<>() {
            private AnchorPane root;
            private ListItemController listController;
            private PauseTransition resetStylePause = new PauseTransition(Duration.seconds(1));

            {
                try {
                    FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("list_item.fxml"));
                    root = loader.load();
                    listController = loader.getController();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void updateItem(ListItem item, boolean empty) {
                super.updateItem(item, empty);


                if (empty || item == null || item.isSpace()) {
                    setStyle("");
                    setGraphic(null);
                } else {

                    setStyle("");

                    listController.itemVB.getChildren().clear();

                    if (item.getName() != null) {
                        listController.itemVB.getChildren().add(listController.itemName);
                        listController.itemName.setText(item.getName());
                    }

                    if(!(item.isArray() || item.isObject())){
                        listController.itemVB.getChildren().add(listController.itemValue);
                        listController.itemValue.setText(item.getValue());
                    }

                    if (highlightedItem != -1 && highlightedItem == getIndex()){
                        setStyle("-fx-background-color: yellow;");
                        resetStylePause.setOnFinished(e -> {
                            highlightedItem = -1;
                            controller.list.refresh();
                        });
                        resetStylePause.play();
                    }


                    setGraphic(root);
                }

            }

            @Override
            public void updateSelected(boolean b) {
//                        super.updateSelected(b);
                ListItem item = getItem();
                if (item.isObject() || item.isArray()) {

                    String newPath = data.getPath() + (data.getPath().equals("") ? "": "///" + (item.getId()!=-1?"{" + item.getId() + "}":"")) + item.getName();
                    open(JsonData.getPathFormat(newPath),newPath,item.getPosition()!=-1?item.getPosition():getIndex());

                }
            }
        };
    }

    public void goBack(){
//  todo      if (pathListView.getVisibility() == View.VISIBLE){
//  todo          showHidePathList();
//  todo          return;
//  todo      }
//todo
//  todo      if (isMenuOpen) {
//  todo          open_closeMenu();
//  todo          return;
//  todo      }
//todo
        if (controller.urlView.isVisible()){
            controller.hideUrlSearchView();
            return;
        }

//  todo      if (adapter!= null && adapter.selectedItem != -1){
//  todo          adapter.selectedItem = -1;
//  todo          adapter.notifyItemRangeChanged(0,adapter.getItemCount());
//  todo          return;
//  todo      }
//
        if (data.isEmptyPath()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit?");
            alert.setHeaderText("Do you want to exit Json List?");
            alert.getDialogPane().getButtonTypes().setAll(ButtonType.YES,ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.YES){
                System.exit(0);
            }
            return;
        }
// todo       TransitionManager.endTransitions(viewGroup);
// todo       TransitionManager.beginDelayedTransition(viewGroup, autoTransition);
        data.goBack();
        open(JsonData.getPathFormat(data.getPath()), data.getPath(),-1);

    }


}