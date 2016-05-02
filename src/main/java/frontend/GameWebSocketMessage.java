package frontend;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GameWebSocketMessage {

    public enum MessageType {
        GAME_INIT,
        GAME_START,
        GAME_TURN,
        GAME_OVER,
        SHOOT_RESULT,
        ERROR,
        GAME_STATUS,
        OPPONENT_ONLINE,
        GAME_TOO_LONG
    }

    private boolean ok;
    private MessageType type;
    private String error;

    private Long id = null;
    private String opponentName = null;

    private String status = null;

    private Integer xVar = null;
    private Integer yVar = null;

    private Integer xStart = null;
    private Integer yStart = null;
    private Integer length = null;
    private Boolean isVertical = null;

    public GameWebSocketMessage(MessageType type) {
        this.ok = true;
        this.type = type;
        this.error = null;
    }

    public GameWebSocketMessage(MessageType type, String error) {
        this.ok = false;
        this.type = type;
        this.error = error;
    }

    public void setOk() {
        this.ok = true;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public void setNotOk() {
        this.ok = false;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setOpponentName(String name) {
        this.opponentName = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setX(int x) {
        this.xVar = x;
    }

    public void setY(int y) {
        this.yVar = y;
    }

    public void setStartX(int x) {
        this.xStart = x;
    }

    public void setStartY(int y) {
        this.yStart = y;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
    }

    @SuppressWarnings("OverlyComplexMethod")
    public JsonObject getAsJSON() {
        final JsonObject obj = new JsonObject();
        obj.add("ok", new JsonPrimitive(this.ok));
        obj.add("type", new JsonPrimitive(this.type.toString().toLowerCase().replace("type_", "")));
        if (this.error != null) {
            obj.add("error", new JsonPrimitive(this.error));
        }

        if (this.id != null) {
            obj.add("id", new JsonPrimitive(this.id));
        }

        if(this.opponentName != null) {
            obj.add("opponentName", new JsonPrimitive(this.opponentName));
        }

        if(this.status != null) {
            obj.add("status", new JsonPrimitive(this.status.toLowerCase().replace("state_", "")));
        }

        if(this.xVar != null) {
            obj.add("x", new JsonPrimitive(this.xVar));
        }

        if(this.yVar != null) {
            obj.add("y", new JsonPrimitive(this.yVar));
        }

        if(this.xStart != null) {
            obj.add("startX", new JsonPrimitive(this.xStart));
        }

        if(this.yStart != null) {
            obj.add("startY", new JsonPrimitive(this.yStart));
        }

        if(this.length != null) {
            obj.add("length", new JsonPrimitive(this.length));
        }

        if(this.isVertical != null) {
            obj.add("isVertical", new JsonPrimitive(this.isVertical));
        }
        return obj;
    }

    @Override
    public String toString() {
        return this.getAsJSON().toString();
    }
}
