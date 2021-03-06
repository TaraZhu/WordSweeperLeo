package server.controller;
import java.util.ArrayList;

/**
 * when server receives the exitGameRequest from client, then the server will remove this client 
 * from game and send the exitGameResponse to the client and tell all client in the game that 
 * the client exit the game.
 * 
 * 
 * The {@link #process()} makes a boardResponse and a exitGameResponse in XML format, 
 * sends the boardResponse to all clients which in the game and sends exitGameResponse
 * to the client.
 * 
 * @author Zhenyu Hu
 */

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import server.ClientState;
import server.Server;
import server.model.Model;
import server.model.Player;
import server.model.Game;
import xml.Message;
public class ExitGameRequestController {

	Model model;
	public ExitGameRequestController (Model model) {
		this.model = model;
		
	}
	public Message process(ClientState client, Message request) {
		Node exitRequest = request.contents.getFirstChild();
		NamedNodeMap map = exitRequest.getAttributes();
		String ID = map.getNamedItem("gameId").getNodeValue();
		String name = map.getNamedItem("name").getNodeValue();
		Game game = model.getGame(ID);
		game.removePlayer(name);
		String xmlString = Message.responseHeader(request.id()) +
				"<exitGameResponse gameId='"+ game.getGameID() +"'>" +
			  "</exitGameResponse>" +
			"</response>";
		// send this response back to the client which sent us the request.
		
		String player = new String();
		ArrayList<Player> Players = game.getPlayers();
		for (Player p : Players){
			player = player + "<player name='" + p.getName() + "' position = '"+p.getPlayerLocation().getColumn()+","+ p.getPlayerLocation().getRow() +"' board = '"+ game.getPlayerboard(p) +"' score='" + p.getScore() +"'/>" ;
		}
		
		String xmlString1 = Message.responseHeader(request.id()) +
				"<boardResponse gameId='"+ game.getGameID() +"' managingUser = '"+ game.getManageUsername()+"' bonus ='" + game.getBoard().getBonusCell().getColumn()+","+ game.getBoard().getBonusCell().getRow()+"'>" +
			  player +
				"</boardResponse>" +
			"</response>";
		// send this response back to the client which sent us the request.
		Message message = new Message(xmlString1);
		for (Player p : game.getPlayers()) {
			for (String id : Server.ids()) {
				if (id.equals(p.getClientId())) {
					
						Server.getState(id).sendMessage(message);
					
				}
			}
		}
		
		
		return new Message (xmlString);
	}
}
