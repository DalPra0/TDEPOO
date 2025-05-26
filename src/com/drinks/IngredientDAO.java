package com.drinks;

import java.sql.*;
import java.util.*;

public class IngredientDAO {
    public Ingredient getOrCreateIngredient(String name) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement pst = conn.prepareStatement("SELECT id, name FROM ingredient WHERE name=?");
            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Ingredient(rs.getInt("id"), rs.getString("name"));
            }
            pst = conn.prepareStatement("INSERT INTO ingredient (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, name);
            pst.executeUpdate();
            ResultSet keys = pst.getGeneratedKeys();
            if (keys.next()) {
                return new Ingredient(keys.getInt(1), name);
            }
            throw new SQLException("Failed to create ingredient");
        }
    }

    public List<Ingredient> getIngredientsForDrink(int drinkId) throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(
                "SELECT i.id, i.name FROM ingredient i " +
                "JOIN drink_ingredient di ON i.id = di.ingredient_id WHERE di.drink_id = ?");
            pst.setInt(1, drinkId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ingredients.add(new Ingredient(rs.getInt("id"), rs.getString("name")));
            }
        }
        return ingredients;
    }

    public List<Ingredient> getAllIngredients() throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, name FROM ingredient");
            while (rs.next()) {
                ingredients.add(new Ingredient(rs.getInt("id"), rs.getString("name")));
            }
        }
        return ingredients;
    }
}

