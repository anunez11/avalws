package pe.gob.mpfn.wsAval.utilitario;

import java.util.HashMap;

public class MessageInfo {

    private  String events="";

    private  String user="";

    private  String message="";
    private  HashMap<String,Object> data= new HashMap<String,Object>();

    
    public MessageInfo() {
		super();
		// TODO Auto-generated constructor stub
	}


	public MessageInfo(String events, String user, String message,HashMap<String,Object> data) {
        this.user = user;
        this.events = events;
        this.message = message;
        this.data=data;
    }

  
    public String getEvents() {
		return events;
	}


	public String getUser() {
		return user;
	}


	public String getMessage() {
        return message;
    }


	public HashMap<String, Object> getData() {
		return data;
	}


	@Override
	public String toString() {
		return "MessageInfo [events=" + events + ", user=" + user
				+ ", message=" + message + ", data=" + data + "]";
	}
	
}