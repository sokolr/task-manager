package ua.edu.sumdu.j2se.sokol.lab;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ua.edu.sumdu.j2se.sokol.lab.Controller.CreateAndEditTaskViewController;
import ua.edu.sumdu.j2se.sokol.lab.Controller.GeneralViewController;
import ua.edu.sumdu.j2se.sokol.lab.Model.LinkedTaskList;
import ua.edu.sumdu.j2se.sokol.lab.Model.Task;
import ua.edu.sumdu.j2se.sokol.lab.Model.TaskIO;
import ua.edu.sumdu.j2se.sokol.lab.Model.TaskList;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainApp extends Application {
    private static TaskList tasks = new LinkedTaskList();
    private ObservableList<Task> tasksData = FXCollections.observableArrayList();
    public static final File DATABASE = new File("resources/database");
    private boolean exit = false;

    public ObservableList<Task> getTasksData() {
        return tasksData;
    }

    public static TaskList getTask() {
        return tasks;
    }

    private Stage primaryStage;
    private VBox rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Task Manager");
        this.primaryStage.setMinWidth(750);
        this.primaryStage.setMinHeight(500);

        initRootLayout();

        this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                writeInDataBase();
                exit = true;
            }
        });
    }

    public MainApp() {
        try {
            TaskIO.readBinary(tasks, DATABASE);
            tasksData.clear();
            for (Task t : tasks) {
                tasksData.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("View/GeneralView.fxml"));
            rootLayout = loader.load();

            GeneralViewController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showCreateAndEditWindow(Task task, boolean newTask) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("View/CreateAndEditTaskView.fxml"));

            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            if (newTask) {
                dialogStage.setTitle("Create Task");
            } else {
                dialogStage.setTitle("Edit Task");
            }

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setResizable(false);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CreateAndEditTaskViewController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            if (newTask) {
                controller.setNewTask(task);
            } else {
                controller.setTask(task);
            }

            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showCalendarWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("View/CalendarView.fxml"));

            HBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Calendar");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void writeInDataBase() {
        try {
            TaskIO.writeBinary(tasks, DATABASE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
