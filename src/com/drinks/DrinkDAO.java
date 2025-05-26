package com.drinks;

import java.sql.*;
import java.util.*;

public class DrinkDAO {
    private final IngredientDAO ingredientDAO = new IngredientDAO();

    public List<Drink> getAllDrinks() throws SQLException {
        List<Drink> drinks = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM drink");
            while (rs.next()) {
                int drinkId = rs.getInt("id");
                List<Ingredient> ingredients = ingredientDAO.getIngredientsForDrink(drinkId);
                Drink drink = new Drink(
                    drinkId,
                    rs.getString("name"),
                    rs.getString("photo"),
                    rs.getDouble("price"),
                    rs.getString("location"),
                    rs.getBoolean("homemade"),
                    ingredients
                );
                drinks.add(drink);
            }
        }
        return drinks;
    }

    public Drink getDrinkById(int id) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM drink WHERE id=?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                List<Ingredient> ingredients = ingredientDAO.getIngredientsForDrink(id);
                return new Drink(
                    id,
                    rs.getString("name"),
                    rs.getString("photo"),
                    rs.getDouble("price"),
                    rs.getString("location"),
                    rs.getBoolean("homemade"),
                    ingredients
                );
            }
        }
        return null;
    }

    public int addDrink(Drink drink) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(
                "INSERT INTO drink (name, photo, price, location, homemade) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            pst.setString(1, drink.getName());
            pst.setString(2, drink.getPhoto());
            pst.setDouble(3, drink.getPrice());
            pst.setString(4, drink.getLocation());
            pst.setBoolean(5, drink.isHomemade());
            pst.executeUpdate();
            ResultSet keys = pst.getGeneratedKeys();
            int drinkId = 0;
            if (keys.next()) drinkId = keys.getInt(1);
            // Link ingredients
            if (drink.getIngredients() != null) {
                for (Ingredient ing : drink.getIngredients()) {
                    Ingredient dbIng = ingredientDAO.getOrCreateIngredient(ing.getName());
                    addDrinkIngredient(drinkId, dbIng.getId());
                }
            }
            return drinkId;
        }
    }

    public void updateDrink(Drink drink) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(
                "UPDATE drink SET name=?, photo=?, price=?, location=?, homemade=? WHERE id=?"
            );
            pst.setString(1, drink.getName());
            pst.setString(2, drink.getPhoto());
            pst.setDouble(3, drink.getPrice());
            pst.setString(4, drink.getLocation());
            pst.setBoolean(5, drink.isHomemade());
            pst.setInt(6, drink.getId());
            pst.executeUpdate();
            // Update ingredients
            PreparedStatement del = conn.prepareStatement("DELETE FROM drink_ingredient WHERE drink_id=?");
            del.setInt(1, drink.getId());
            del.executeUpdate();
            if (drink.getIngredients() != null) {
                for (Ingredient ing : drink.getIngredients()) {
                    Ingredient dbIng = ingredientDAO.getOrCreateIngredient(ing.getName());
                    addDrinkIngredient(drink.getId(), dbIng.getId());
                }
            }
        }
    }

    public void deleteDrink(int id) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement pst = conn.prepareStatement("DELETE FROM drink WHERE id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    private void addDrinkIngredient(int drinkId, int ingId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement pst = conn.prepareStatement(
                "INSERT INTO drink_ingredient (drink_id, ingredient_id) VALUES (?, ?)"
            );
            pst.setInt(1, drinkId);
            pst.setInt(2, ingId);
            pst.executeUpdate();
        }
    }
}

