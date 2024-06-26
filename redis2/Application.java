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

		// Add a shutdown hook to stop the server gracefully
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Shutting down server...");
			server.stop();
		}));

		// Put the main thread to sleep indefinitely
		try {
			while (true) {
				Thread.sleep(Long.MAX_VALUE);
			}
		} catch (InterruptedException e) {
			// Handle interruption, if needed
			System.out.println("Main thread interrupted");
		}
	}
}
