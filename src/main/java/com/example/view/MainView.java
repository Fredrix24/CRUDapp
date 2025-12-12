package com.example.view;

import com.example.dao.EntityDAOImpl;
import com.example.model.Entity;
import com.example.service.EntityService;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class MainView extends Application {

    private TableView<Entity> tableView;
    private Button createButton, updateButton, deleteButton;
    private ObservableList<Entity> entityList;
    private TextField searchField; // <-- Новое поле
    private TextField nameField = new TextField();
    private TextField descriptionField = new TextField();
    private EntityService entityService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CRUD приложение");
        try {
            entityService = new EntityService(new EntityDAOImpl());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Критическая ошибка инициализации DAO. Проверьте путь к entities.db в EntityDAOImpl.");
            alert.showAndWait();
            primaryStage.close();
            return;
        }
        setupTableView();

        createButton = new Button("Создать");
        updateButton = new Button("Изменить");
        deleteButton = new Button("Удалить");

        searchField = new TextField();
        searchField.setPromptText("Поиск по Имени или Описанию..."); // Подсказка

        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        createButton.setOnAction(e -> handleCreate());
        updateButton.setOnAction(e -> handleUpdate());
        deleteButton.setOnAction(e -> handleDelete());

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
                nameField.setText(newSelection.getName());
                descriptionField.setText(newSelection.getDescription());
            } else {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        HBox inputFields = new HBox(10, new Label("Имя:"), nameField, new Label("Описание:"), descriptionField);
        inputFields.setPadding(new Insets(10));

        HBox buttonBox = new HBox(10, createButton, updateButton, deleteButton);
        buttonBox.setPadding(new Insets(10));

        HBox searchBox = new HBox(10, new Label("Поиск:"), searchField);
        searchBox.setPadding(new Insets(10));

        VBox root = new VBox(10, searchBox, tableView, inputFields, buttonBox);

        loadData();
        setupSearchFilter();

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void setupTableView() {
        this.tableView = new TableView<>();

        TableColumn<Entity, String> idColumn = new TableColumn<>("ID");
        TableColumn<Entity, String> nameColumn = new TableColumn<>("Имя");
        TableColumn<Entity, String> descriptionColumn = new TableColumn<>("Описание");
        TableColumn<Entity, String> createdAtColumn = new TableColumn<>("Создано в");
        TableColumn<Entity, String> updatedAtColumn = new TableColumn<>("Изменено в");

        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        createdAtColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreatedAt().format(DATE_FORMATTER)));
        updatedAtColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUpdatedAt().format(DATE_FORMATTER)));

        tableView.getColumns().addAll(idColumn, nameColumn, descriptionColumn, createdAtColumn, updatedAtColumn);
    }

    private void loadData() {
        try {
            List<Entity> entities = entityService.readAllEntities(0, 100);
            tableView.getItems().setAll(entities);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки данных: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Не удалось загрузить данные. Проверьте DAO и DB файл.").showAndWait();
        }
        try {
            List<Entity> allEntities = entityService.readAllEntities(0, 100);

            entityList = FXCollections.observableArrayList(allEntities);

            tableView.setItems(entityList);

            setupSearchFilter();

        } catch (Exception e) {
        }
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterList(newValue);
        });

        if (entityList != null) {
            tableView.setItems(entityList);
        }
    }

    private void filterList(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            tableView.setItems(entityList);
            return;
        }

        String lowerCaseFilter = searchText.toLowerCase();

        FilteredList<Entity> filteredData = entityList.filtered(entity -> {
            boolean nameMatch = entity.getName().toLowerCase().contains(lowerCaseFilter);
            boolean descriptionMatch = entity.getDescription().toLowerCase().contains(lowerCaseFilter);

            return nameMatch || descriptionMatch;
        });

        tableView.setItems(filteredData);
    }

    private void handleCreate() {
        String name = nameField.getText().trim();
        String desc = descriptionField.getText().trim();

        if (name.isEmpty() || desc.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Имя и Описание не могут быть пустыми.").showAndWait();
            return;
        }

        Entity newEntity = new Entity();
        newEntity.setId(UUID.randomUUID());
        newEntity.setName(name);
        newEntity.setDescription(desc);
        LocalDateTime now = LocalDateTime.now();
        newEntity.setCreatedAt(now);
        newEntity.setUpdatedAt(now);

        entityService.createEntity(newEntity);

        tableView.getItems().add(newEntity);
        nameField.clear();
        descriptionField.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void handleUpdate() {
        Entity selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String newName = nameField.getText().trim();
            String newDesc = descriptionField.getText().trim();

            if (newName.isEmpty() || newDesc.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Поля не могут быть пустыми.").showAndWait();
                return;
            }

            selected.setName(newName);
            selected.setDescription(newDesc);
            selected.setUpdatedAt(LocalDateTime.now());

            entityService.updateEntity(selected);
            tableView.refresh();
        }
    }

    private void handleDelete() {
        Entity selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            entityService.deleteEntity(selected.getId());
            tableView.getItems().remove(selected);

            nameField.clear();
            descriptionField.clear();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}