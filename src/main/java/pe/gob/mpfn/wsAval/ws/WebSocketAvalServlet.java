package pe.gob.mpfn.wsAval.ws;


import com.google.gson.Gson;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import pe.gob.mpfn.wsAval.utilitario.ConnectionInfoMessage;
import pe.gob.mpfn.wsAval.utilitario.MessageInfo;
//import pe.gob.mpfn.wsAval.utilitario.MessageInfoMessage;
//import pe.gob.mpfn.wsAval.utilitario.StatusInfoMessage;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.*;

@WebServlet(urlPatterns = "/aval")
public class WebSocketAvalServlet extends WebSocketServlet {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAvalServlet.class);

    private static final Map<String, AvalConnection> connections = new HashMap<String, AvalConnection>();

    @Override
    protected boolean verifyOrigin(String origin) {
        return true;
    }

    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {
        final String connectionId = request.getSession().getId();
        final String userName = request.getParameter("userName");
        return new AvalConnection(connectionId, userName);
    }

    private static class AvalConnection extends MessageInbound {

        private final String connectionId;

        private final String userName;

        private final Gson jsonProcessor;

        private AvalConnection(String connectionId, String userName) {
            this.connectionId = connectionId;
            this.userName = userName;
            this.jsonProcessor = new Gson();
        }

        @Override
        protected void onOpen(WsOutbound outbound) {
           // sendConnectionInfo(outbound);
           // sendStatusInfoToOtherUsers(new StatusInfoMessage(userName, StatusInfoMessage.STATUS.CONNECTED));
            connections.put(connectionId, this);
        }

        @Override
        protected void onClose(int status) {
           // sendStatusInfoToOtherUsers(new StatusInfoMessage(userName, StatusInfoMessage.STATUS.DISCONNECTED));
            connections.remove(connectionId);
        }

        @Override
        protected void onBinaryMessage(ByteBuffer byteBuffer) throws IOException {
            throw new UnsupportedOperationException("Binary messages not supported");
        }

        @Override
        protected void onTextMessage(CharBuffer charBuffer) throws IOException {
        	
            System.out.println(" enviado  =========>" + charBuffer.toString());
            MessageInfo messageTexto = jsonProcessor.fromJson(charBuffer.toString(), MessageInfo.class);
            
            System.out.println(" enviado 2  =========>" + messageTexto.getUser());
           
            final AvalConnection destinationConnection = getDestinationUserConnection(messageTexto.getUser());
            if (destinationConnection != null) {
                final CharBuffer jsonMessage = CharBuffer.wrap(jsonProcessor.toJson(messageTexto));
                destinationConnection.getWsOutbound().writeTextMessage(jsonMessage);
            } else {
                log.warn("Se está intentando enviar un mensaje a un usuario no conectado");
            }
        }

        public String getUserName() {
            return userName;
        }


       private AvalConnection getDestinationUserConnection(String destinationUser) {
            for (AvalConnection connection : connections.values()) {
                if (destinationUser.equals(connection.getUserName())) {
                    return connection;
                }
            }
            return null;
        }

    }

}
