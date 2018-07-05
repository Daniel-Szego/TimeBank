
/*
 * This file is public domain.
 *
 * SWIRLDS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF 
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SWIRLDS SHALL NOT BE LIABLE FOR 
 * ANY DAMAGES SUFFERED AS A RESULT OF USING, MODIFYING OR 
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.nio.charset.StandardCharsets;

import com.swirlds.platform.Browser;
import com.swirlds.platform.Console;
import com.swirlds.platform.Platform;
import com.swirlds.platform.SwirldMain;
import com.swirlds.platform.SwirldState;

/**
 * This HelloSwirld creates a single transaction, consisting of the string "Hello Swirld", and then goes
 * into a busy loop (checking once a second) to see when the state gets the transaction. When it does, it
 * prints it, too.
 */
public class TimeBankDemoMain implements SwirldMain {
	/** the platform running this app */
	public Platform platform;
	/** ID number for this member */
	public long selfId;
	/** a console window for text output */
	public Console console;
	/** sleep this many milliseconds after each sync */
	public final int sleepPeriod = 100;
	/** a GUI window */
	public  javax.swing.JFrame window;
	/** a GUI window */
	public  Integer numberOfTokens;
	/** a GUI window */
	public  String serviceToOffer;
	/** Initial balances */
	public  Integer initialBalances;
	

	
	
	/**
	 * This is just for debugging: it allows the app to run in Eclipse. If the config.txt exists and lists a
	 * particular SwirldMain class as the one to run, then it can run in Eclipse (with the green triangle
	 * icon).
	 * 
	 * @param args
	 *            these are not used
	 */
	public static void main(String[] args) {
		Browser.main(args);
	}

	// ///////////////////////////////////////////////////////////////////

	@Override
	public void preEvent() {
	}

	@Override
	public void init(Platform platform, long id) {
		this.platform = platform;
		this.selfId = id;
		this.console = platform.createConsole(true); // create the window, make it visible
		//this.window = platform.createWindow(true);		
		//initWindow();
		String[] pars = platform.getParameters();
		initialBalances = Integer.parseInt(pars[0]);
		
		platform.setAbout("Hello TimeBank v. 1.0\n"); // set the browser's "about" box
		platform.setSleepAfterSync(sleepPeriod);
	}	

	@Override
	public void run() {
		try {
		String myName = platform.getState().getAddressBookCopy()
				.getAddress(selfId).getSelfName();
		serviceToOffer = platform.getState().getAddressBookCopy()
				.getAddress(selfId).getNickname();

		
		console.out.println("Time Bank Demo v.01");
		console.out.println("USER: " + myName + "");
		console.out.println("Service to offer: " + 	serviceToOffer + "");
		console.out.println("");
		console.out.println("Service DB syncronising:");

		String addInitBlance = "initbalance name:" + myName + " balance: " +  initialBalances + "";
		byte[] addInitBlancetransaction = addInitBlance.getBytes(StandardCharsets.UTF_8);
		platform.createTransaction(addInitBlancetransaction);

		String addService = "addservice name:" + myName + " service: " +  serviceToOffer + "";
		byte[] addServicetransaction = addService.getBytes(StandardCharsets.UTF_8);
		platform.createTransaction(addServicetransaction);
			
		
		String lastReceivedBalance = "";
		String lastReceivedServiceDB = "";

		while (true) {
			TimeBankDemoState state = (TimeBankDemoState) platform
					.getState();
			String receivedBalance = state.getReceivedBalances();
			String serviceDB =  state.getReceivedServiceDB();
			
			if (!lastReceivedBalance.equals(receivedBalance)) {
				lastReceivedBalance = receivedBalance;
				console.out.println("Balances: " + receivedBalance); // print all received transactions
			}
			if(!lastReceivedServiceDB.equals(serviceDB)) {
				lastReceivedServiceDB = serviceDB;
				console.out.println("Services: " + serviceDB); // print all received transactions
			} 
			
			try {
				Thread.sleep(sleepPeriod);
			} catch (Exception e) {
			}
		}
		} catch(Exception e){
			console.out.println(e.toString());
		}
	}

	@Override
	public SwirldState newState() {
		return new TimeBankDemoState();
	}
	
	
	protected void initWindow(){
		
		// label 1
		Container c = window.getContentPane();
		Label titleLbl = new Label ();		
		Font font = titleLbl.getFont();
		// same font but bold
		//Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
		//titleLbl.setFont(boldFont);
		titleLbl.setText("Timebank Demo");				
		window.add(titleLbl, BorderLayout.AFTER_LAST_LINE);
		
		// label 2
		String myName = platform.getState().getAddressBookCopy()
				.getAddress(selfId).getSelfName();
		Label nameLbl = new Label ();
		nameLbl.setText("User Name" + myName);
		window.add(nameLbl, BorderLayout.AFTER_LAST_LINE);
		
		// label 3
		Label listLbl = new Label ();
		listLbl.setText("List of services:");
		window.add(listLbl,  BorderLayout.AFTER_LAST_LINE);
		
		
		
	}

	
}