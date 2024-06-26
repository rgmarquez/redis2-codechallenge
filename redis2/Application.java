package redis2;

// Greg Marquez
// Engineering Redis Challenge
// Multithreaded server code from :
// http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html

public class Application {

	private static final int SECONDS_TO_RUN = 50;
	private static final int SERVER_PORT = 5556;
	
	public static void main(String[] args) {

		MultiThreadedServer server = new MultiThreadedServer(SERVER_PORT);
		new Thread(server).start();

		// For testing, we only run the server for a limited time...
		// remove sleep and server.stop(), and add this.wait()
		// to run the server until it is halted from an outside source
		try {
		    Thread.sleep(SECONDS_TO_RUN * 1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		System.out.println("Stopping Server");
		server.stop();
	}
}
