package com.example.dao;

import com.example.model.Entity;
import java.time.LocalDateTime;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityDAOImpl implements EntityDAO {
    private Connection connection;

    public EntityDAOImpl() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:entities.db");
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS entities (id TEXT PRIMARY KEY, name TEXT NOT NULL, description TEXT, createdAt TEXT NOT NULL, updatedAt TEXT NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(Entity entity) {
        String sql = "INSERT INTO entities(id, name, description, createdAt, updatedAt) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getId().toString());
            statement.setString(2, entity.getName());
            statement.setString(3, entity.getDescription());
            statement.setString(4, entity.getCreatedAt().toString());
            statement.setString(5, entity.getUpdatedAt().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Entity read(UUID id) {
        String sql = "SELECT * FROM entities WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Entity entity = new Entity();
                entity.setId(UUID.fromString(resultSet.getString("id")));
                entity.setName(resultSet.getString("name"));
                entity.setDescription(resultSet.getString("description"));
                entity.setCreatedAt(LocalDateTime.parse(resultSet.getString("createdAt")));
                entity.setUpdatedAt(LocalDateTime.parse(resultSet.getString("updatedAt")));
                return entity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Entity> readAll(int page, int size) {
        List<Entity> entities = new ArrayList<>();
        String sql = "SELECT * FROM entities LIMIT ? OFFSET ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, size);
            statement.setInt(2, (page - 1) * size);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Entity entity = new Entity();
                entity.setId(UUID.fromString(resultSet.getString("id")));
                entity.setName(resultSet.getString("name"));
                entity.setDescription(resultSet.getString("description"));
                entity.setCreatedAt(LocalDateTime.parse(resultSet.getString("createdAt")));
                entity.setUpdatedAt(LocalDateTime.parse(resultSet.getString("updatedAt")));
                entities.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entities;
    }

    @Override
    public void update(Entity entity) {
        String sql = "UPDATE entities SET name = ?, description = ?, updatedAt = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.setString(3, entity.getUpdatedAt().toString());
            statement.setString(4, entity.getId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM entities WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}