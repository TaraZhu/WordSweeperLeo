package admin.admincontroller;


import admin.adminview.DrawSee;


import java.util.ArrayList;

import admin.MockServerAccess;
import admin.adminmodel.AdministratorModel;
import junit.framework.TestCase;
import xml.Message;

public class ListGamesControllerTest extends TestCase {

	// Mock server object that extends (and overrides) ServerAccess for its purposes
	MockServerAccess mockServer;
	
	//admin to connect
	DrawSee admin;
	
	// model being maintained by admin.
	AdministratorModel model;
	protected void setUp() throws Exception {
		super.setUp();
		// FIRST thing to do is register the protocol being used.
		if (!Message.configure("wordsweeper.xsd")) {
			fail ("unable to configure protocol");
		}
		// prepare client and connect to server.
		model = new AdministratorModel();
		admin = new DrawSee (model);
		admin.setVisible(true);
		// Create mockServer to simulate server, and install 'obvious' handler
		// that simply dumps to the screen the responses.
		mockServer = new MockServerAccess("localhost");
		
		// as far as the client is concerned, it gets a real object with which
		// to communicate.
		admin.setServerAccess(mockServer);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testProcess() {
		new ListGamesController(admin,model).process();
		 // validate from mockServer
		 
		 ArrayList<Message> reqs = mockServer.getAndClearMessages();
		 assertTrue (reqs.size() == 1);
		 Message r = reqs.get(0);
		 // a lock request is sent out.
		 assertEquals ("listGamesRequest", r.contents.getFirstChild().getLocalName());
	}

}
