package base;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameUser {
    public static final int ALL_BOATS_NUM = 20;
    private final String myName;
    private String enemyName;
    private Map<String, String> aliveBoats;
    private Map<String, String> killedBoats;
    private Map<String, Integer> aliveCounters;

    public GameUser(String myName, Map<String, String> myBoats) {
        this.myName = myName;
        aliveBoats = new HashMap<>(myBoats);
        killedBoats = new HashMap<>();

        aliveCounters = new HashMap<>();
        aliveCounters.put("four-decked", 4);
        for(Map.Entry<String, String> e : aliveBoats.entrySet()){
            if(aliveCounters.size() == 6) break;
            if(e.getValue().startsWith("two-decked")){
                aliveCounters.put(e.getValue(), 2);
            } else if (e.getValue().startsWith("three-decked")) {
                aliveCounters.put(e.getValue(), 3);
            }
        }
    }

    public String getMyName() {
        return myName;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public void setEnemyName(String enemyName) {
        this.enemyName = enemyName;
    }

    public JsonObject shootMyShip(String coordiantes) {
        final JsonObject responseBody = new JsonObject();

        if (aliveBoats.containsKey(coordiantes)) {

            final String boatName = aliveBoats.get(coordiantes);
            if (Objects.equals(boatName, "one-decked")) {
                responseBody.add("status", new JsonPrimitive("killed"));
                responseBody.add("coordinates", new JsonPrimitive(coordiantes));
                return responseBody;
            }

            killedBoats.put(coordiantes, boatName);
            aliveBoats.remove(coordiantes);

            if (killedBoats.size() == ALL_BOATS_NUM) {
                responseBody.add("status", new JsonPrimitive("lost"));
                killedBoats.clear();
                aliveBoats.clear();
                aliveCounters.clear();
                return responseBody;
            }

            final int alive = aliveCounters.get(boatName) - 1;
            if(alive == 0){
                responseBody.add("status", new JsonPrimitive("killed"));
                final JsonArray jsonArray = new JsonArray();
                killedBoats.entrySet().stream().filter(e -> e.getValue().equals(boatName)).forEach(e -> {
                    jsonArray.add(e.getKey());
                });
                responseBody.add("coordinates", jsonArray);
                return responseBody;
            }
            aliveCounters.replace(boatName, alive);
            responseBody.add("status", new JsonPrimitive("picked"));
            responseBody.add("coordinates", new JsonPrimitive(coordiantes));
            return responseBody;
        }
        responseBody.add("status", new JsonPrimitive("missed"));
        responseBody.add("coordinates", new JsonPrimitive(coordiantes));
        return responseBody;

    }

}