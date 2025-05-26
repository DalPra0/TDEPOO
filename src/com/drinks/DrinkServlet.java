package com.drinks;

import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/api/drinks")
@MultipartConfig
public class DrinkServlet extends HttpServlet {
    private final DrinkDAO drinkDAO = new DrinkDAO();
    private final IngredientDAO ingredientDAO = new IngredientDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            List<Drink> drinks = drinkDAO.getAllDrinks();
            JSONArray arr = new JSONArray();
            for (Drink d : drinks) {
                JSONObject obj = new JSONObject();
                obj.put("id", d.getId());
                obj.put("name", d.getName());
                obj.put("photo", d.getPhoto());
                obj.put("price", d.getPrice());
                obj.put("location", d.getLocation());
                obj.put("homemade", d.isHomemade());
                JSONArray ings = new JSONArray();
                if (d.getIngredients() != null) {
                    for (Ingredient ing : d.getIngredients()) {
                        ings.put(ing.getName());
                    }
                }
                obj.put("ingredients", ings);
                arr.put(obj);
            }
            resp.getWriter().write(arr.toString());
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String name = req.getParameter("name");
            String priceStr = req.getParameter("price");
            String location = req.getParameter("location");
            String homemadeStr = req.getParameter("homemade");
            String[] ingredients = req.getParameterValues("ingredients");
            String customIngredient = req.getParameter("custom-ingredient");
            Part photoPart = req.getPart("photo");
            String photoFileName = null;
            if (photoPart != null && photoPart.getSize() > 0) {
                photoFileName = System.currentTimeMillis() + "_" + photoPart.getSubmittedFileName();
                String uploadPath = getServletContext().getRealPath("/uploads");
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();
                photoPart.write(uploadPath + File.separator + photoFileName);
            }
            List<Ingredient> ingredientList = new ArrayList<>();
            if (ingredients != null) {
                for (String ing : ingredients) {
                    ingredientList.add(new Ingredient(ing));
                }
            }
            if (customIngredient != null && !customIngredient.isEmpty()) {
                ingredientList.add(new Ingredient(customIngredient));
            }
            Drink drink = new Drink();
            drink.setName(name);
            drink.setPhoto(photoFileName);
            drink.setPrice(Double.parseDouble(priceStr));
            drink.setLocation(location);
            drink.setHomemade(homemadeStr != null && homemadeStr.equals("on"));
            drink.setIngredients(ingredientList);
            drinkDAO.addDrink(drink);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            JSONObject data = new JSONObject(sb.toString());
            int id = data.getInt("id");
            String name = data.getString("name");
            double price = data.getDouble("price");
            String location = data.optString("location", null);
            boolean homemade = data.getBoolean("homemade");
            JSONArray ingredients = data.getJSONArray("ingredients");
            List<Ingredient> ingredientList = new ArrayList<>();
            for (int i = 0; i < ingredients.length(); i++) {
                ingredientList.add(new Ingredient(ingredients.getString(i)));
            }
            Drink drink = new Drink();
            drink.setId(id);
            drink.setName(name);
            drink.setPrice(price);
            drink.setLocation(location);
            drink.setHomemade(homemade);
            drink.setIngredients(ingredientList);
            drinkDAO.updateDrink(drink);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"Missing id\"}");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            drinkDAO.deleteDrink(id);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
