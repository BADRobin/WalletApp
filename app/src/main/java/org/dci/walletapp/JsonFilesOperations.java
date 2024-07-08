package org.dci.walletapp;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonFilesOperations {
    private static JsonFilesOperations instance;

    private JsonFilesOperations() {

    }

    public static synchronized JsonFilesOperations getInstance() {
        if (instance == null) {
            instance = new JsonFilesOperations();
        }
        return instance;
    }

    public void writeTransactions(Context context, List<Transaction> transactionsList) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file =  new File(directory, "transaction.json");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(mapper.writeValueAsBytes(transactionsList));
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Transaction> readTransactions(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file =  new File(directory, "transaction.json");
        List<Transaction> transactionsList = new ArrayList<>();
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            JsonNode rootNode = new ObjectMapper().readTree(stream);

            for (JsonNode transaction : rootNode) {
                transactionsList.add(new Transaction(
                        transaction.get("amount").asDouble(),
                        LocalDateTime.parse(transaction.get("dateTime").asText()),
                        transaction.get("description").asText(),
                        transaction.get("income").asBoolean(),
                        transaction.get("category").asText()
                ));
            }
        } catch (IOException e) {
            return new ArrayList<Transaction>();
        }
        return transactionsList;
    }

    public void writeCategories(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file =  new File(directory, "categories.json");

        try (FileWriter writer = new FileWriter(file)) {
            JSONObject rootNode = new JSONObject();
            rootNode.put("incomesCategories", new JSONArray(MainActivity.getIncomesCategorieslist()));
            rootNode.put("expensesCategories", new JSONArray(MainActivity.getExpensesCategorieslist()));
            writer.write(rootNode.toString());
        }  catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void readCategories(Context context, boolean isIncome) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file =  new File(directory, "categories.json");
        if (!file.exists()) {
            MainActivity.setIncomesCategorieslist(List.of("Salary", "Bonus", "Others"));
            MainActivity.setExpensesCategorieslist(List.of("Food", "Transport", "Entertainment",
                    "House", "Children", "Others"));
            writeCategories(context);
            return;
        }

        try (InputStream stream = Files.newInputStream(file.toPath())) {
            JsonNode categories;
            if (isIncome) {
                categories = new ObjectMapper().readTree(stream).get("incomesCategories");
            } else {
                categories = new ObjectMapper().readTree(stream).get("expensesCategories");
            }

            //List<String> categoriesList = new ArrayList<>();
            MainActivity.setExpensesCategorieslist(new ArrayList<>());
            MainActivity.setIncomesCategorieslist(new ArrayList<>());

            for (JsonNode category : categories) {
                if (isIncome) {
                    //Log.d("testIncome", category.asText());
                    MainActivity.getIncomesCategorieslist().add(category.asText());
                    //Log.d("testIcome", MainActivity.getIncomesCategorieslist().size() + "");
                } else {
                    MainActivity.getExpensesCategorieslist().add(category.asText());
                }
            }
        } catch (IOException e) {
            Log.d("Categories file read was failed", e.toString());
        }
    }
}