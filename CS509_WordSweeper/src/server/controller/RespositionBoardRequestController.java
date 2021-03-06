package server.controller;

import java.util.ArrayList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import server.ClientState;
import server.Server;
import server.model.Game;
import server.model.Model;
import server.model.Player;
import xml.Message;

/**
 * when server receives the repositionBoardRequest from client, the server will change the position
 * of player and send a boardResponse to all clients in the game.
 * 
 * 
 * The {@link #process()} makes a boardResponse in XML format, move the plyer's position and sends it
 * to the client.
 * 
 *  @author Zhenyu Hu
 */

public class RespositionBoardRequestController {

	Model model;
	public RespositionBoardRequestController (Model model) {
		this.model = model;
	}
	public Message process(ClientState client, Message request) {

		Node repositionBoardRequest = request.contents.getFirstChild();
		NamedNodeMap map = repositionBoardRequest.getAttributes();
		String ID = map.getNamedItem("gameId").getNodeValue();
		Game game = model.getGame(ID);
		model.selectGame(ID);
		String playername = map.getNamedItem("name").getNodeValue();
		System.out.println(playername);
		Player pl=game.getPlayer(playername);
		String rc = map.getNamedItem("rowChange").getNodeValue();
		String cc = map.getNamedItem("colChange").getNodeValue();
		int rowchange = Integer.valueOf(rc).intValue();
		int colchange = Integer.valueOf(cc).intValue();
		game.rePosition(pl, colchange, rowchange);
		


		//construct xml response message
		
		
		String player = new String();
		ArrayList<Player> Players = game.getPlayers();
		for (Player p : Players){
			player = player + "<player name='" + p.getName() + "' position = '"+p.getPlayerLocation().getColumn()+","+ p.getPlayerLocation().getRow() +"' board = '"+ game.getPlayerboard(p) +"' score='" + p.getScore() +"'/>" ;
		}
		
		String xmlString = Message.responseHeader(request.id()) +
				"<boardResponse gameId='"+ game.getGameID() +"' managingUser = '"+ game.getManageUsername()+"' bonus ='" + game.getBoard().getBonusCell().getColumn()+","+ game.getBoard().getBonusCell().getRow()+"'>" +
			  player +
				"</boardResponse>" +
			"</response>";
		// send this response back to the client which sent us the request.
		Message message = new Message(xmlString);
		for (Player p : game.getPlayers()) {
			for (String id : Server.ids()) {
				if (!id.equals(client.id()) && id.equals(p.getClientId())) {
					
						Server.getState(id).sendMessage(message);
					
				}
			}
		}
		return message;
		
	}
}
