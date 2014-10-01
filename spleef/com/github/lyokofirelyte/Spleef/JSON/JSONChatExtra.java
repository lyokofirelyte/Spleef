package com.github.lyokofirelyte.Spleef.JSON;

public class JSONChatExtra {
	
    private JSONObject chatExtra;

    public JSONChatExtra(String text, Object nothing1, Object nothing2) {
    	
        chatExtra = new JSONObject();
        chatExtra.put("text", text);
    }
    
    public JSONChatExtra(String text) {
    	
        chatExtra = new JSONObject();
        chatExtra.put("text", text);
    }

    public void setClickEvent(JSONChatClickEventType action, String value) {
        JSONObject clickEvent = new JSONObject();
        clickEvent.put("action", action.getTypeString());
        clickEvent.put("value", value);
        chatExtra.put("clickEvent", clickEvent);
    }

    public void setHoverEvent(JSONChatHoverEventType action, String value) {
        JSONObject hoverEvent = new JSONObject();
        hoverEvent.put("action", action.getTypeString());
        hoverEvent.put("value", value);
        chatExtra.put("hoverEvent", hoverEvent);
    }

    public JSONObject toJSON() {
        return chatExtra;
    }
}