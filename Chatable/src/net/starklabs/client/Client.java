package net.starklabs.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.starklabs.client.ClientGUI;

public class Client {

	public static String version = "1.5";

	static ServerSocket echoServer = null;
	static String chatMessage;
	public static Socket smtpSocket;
	static Socket clientSocket;
	public static DataOutputStream os;
	public static BufferedReader br;
	public static BufferedReader incoming;
	static String targetHostname;
	static InetAddress addr;
	static String currentHostname[];
	static String dirLocation = System.getProperty("user.home")
			+ "/Library/Application Support/Stark Chat/";
	static File soundFile = new File(dirLocation + "sounds/alert.wav");
	static String ipContent;
	public static ClientGUI clientGUI;
	public static ArrayList<String> arrayList = new ArrayList<String>();
	public static String styleHost;
	public static String[] split;
	public static String host;
	public static Date date = new Date(); // given date
	Calendar calendar = GregorianCalendar.getInstance(); // creates a new
	static InetAddress address;
	public static String username;
	public static boolean canConnect;
	static boolean firstChat = true;

	static ArrayList<String> latestVersionArray = getVersion("https://dl.dropboxusercontent.com/u/17098162/version.txt");
	static JFrame frame = new JFrame("Update Available");
	static JOptionPane updateAvailable = new JOptionPane();

	public static void main(String[] args) {

		clientGUI = new ClientGUI();

		String latestVersion = latestVersionArray.get(0).toString();

		if(!version.equalsIgnoreCase(latestVersion)){
			JOptionPane pane = new JOptionPane("Update " + latestVersion + " available", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);

			JDialog dialog = pane.createDialog("Update Available");
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent evt) {
				}
			});
			dialog.setContentPane(pane);
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.pack();

			dialog.setVisible(true);
			int c = ((Integer)pane.getValue()).intValue();

			if(c == JOptionPane.YES_OPTION) {
				URL newVersion = null;

				try {
					newVersion = new URL("https://dl.dropboxusercontent.com/u/17098162/Client.jar");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				BufferedInputStream in = null;
				FileOutputStream fout = null;
				try
				{
					in = new BufferedInputStream(newVersion.openStream());
					fout = new FileOutputStream(new File(System.getProperty("user.home") + "/Documents/Chatable.jar"));

					byte data[] = new byte[1024];
					int count;
					while ((count = in.read(data, 0, 1024)) != -1)
					{
						fout.write(data, 0, count);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally
				{
					try {
						if (in != null)
							in.close();
						if (fout != null)
							fout.close();
						String[] cmds = {"java", "-jar", System.getProperty("user.home") + "/Documents/Chatable.jar"};
						Runtime.getRuntime().exec(cmds);
						System.exit(0);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			else if (c == JOptionPane.NO_OPTION) {
				JOptionPane.showMessageDialog(null, "ok cool", "Order",JOptionPane.PLAIN_MESSAGE);
			}
		}

		do {
			JTextField localHostname = new JTextField();
			final JComponent[] inputs = new JComponent[] {
					new JLabel("Hostname"), localHostname, };

			/**/
			JOptionPane.showMessageDialog(null, inputs, "Set Hostname",
					JOptionPane.PLAIN_MESSAGE);

			targetHostname = localHostname.getText() + ".lan";
			String[] split2 = targetHostname.split("\\.");
			System.out.println(split2[0]);
			styleHost = split2[0].toUpperCase();
			try {
				address = InetAddress.getByName(targetHostname);
				if (!address.isReachable(1000000)) {
					canConnect = false;
				} else {
					canConnect = true;
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (!canConnect);

		System.out.println("Debug");

		File ipDir = new File(dirLocation);

		if (!ipDir.exists()) {
			System.out.println("creating directory: " + ipDir);
			boolean result = ipDir.mkdirs();
			if (result) {
				System.out.println("DIR created");
			}
		}

		File ipLog = new File(dirLocation + "ips.txt");

		if (!ipLog.exists()) {
			try {
				ipLog.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Debug");

		File firstBoot = new File(dirLocation + ".firstBoot");
		BufferedReader usernameReader = null;
		System.out.println(firstBoot.getAbsolutePath());
		String tempUser;
		if (!firstBoot.exists()) {
			JTextField usernameField = new JTextField();
			final JComponent[] inputs2 = new JComponent[] {
					new JLabel("Username"), usernameField, };

			JOptionPane.showMessageDialog(null, inputs2, "Set Username",
					JOptionPane.PLAIN_MESSAGE);
			username = usernameField.getText();
			try {
				firstBoot.createNewFile();
				FileWriter fw = new FileWriter(firstBoot, true);
				fw.write(username + "\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				usernameReader = new BufferedReader(new FileReader(firstBoot));
				while ((tempUser = usernameReader.readLine()) != null) {
					System.out.println(tempUser);
					username = tempUser;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println(username);

		try {
			FileWriter ipWriter = new FileWriter(ipLog, true);
			ipWriter.write(styleHost + "\n");
			ipWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			InetAddress addr = InetAddress.getLocalHost();
			host = addr.getHostName().toUpperCase();
			System.out.println(host);
			System.out.println(addr.toString());
			currentHostname = addr.toString().split("/");
			System.out.println(currentHostname[1]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		clientGUI.launchGUI();

		writeOut("*** New Session ***", false);

		BufferedReader ipsReader = null;

		try {
			String sCurrentLine;
			ipsReader = new BufferedReader(new FileReader(dirLocation
					+ styleHost + ".txt"));
			while ((sCurrentLine = ipsReader.readLine()) != null) {
				arrayList.add(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ipsReader != null)
					ipsReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		for (int count = 0; count < arrayList.size(); count++) {
			clientGUI.logArea.append(arrayList.get(count) + "\n");
		}

		startServer();

	}

	public static void sendChat(String chat) {
		try {
			try {
				System.out.println(targetHostname);
				smtpSocket = new Socket(targetHostname, 31337);
			} catch (UnknownHostException e) {
				System.err.println("Don't know about host: hostname");
			} catch (IOException e) {
				System.err
				.println("Couldn't get I/O for the connection to: hostname");
			}
			os = new DataOutputStream(smtpSocket.getOutputStream());
			clientGUI.logArea.setCaretPosition(clientGUI.logArea.getDocument()
					.getLength());
			os.writeBytes(timeStamp() + chat);
			os.close();
			smtpSocket.close();
		} catch (UnknownHostException e) {
			System.err.println("Trying to connect to unknown host: " + e);
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}
	}

	public static void startServer() {
		try {
			echoServer = new ServerSocket(31337);
		} catch (IOException e) {
			System.out.println(e);
		}

		try {
			while (true) {
				clientSocket = echoServer.accept();
				incoming = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
				chatMessage = incoming.readLine();
				if (chatMessage != null) {
					String[] chatMessageSplit = chatMessage.split("\\|");
					String string = chatMessageSplit[1];
					writeOut(chatMessageSplit[0] + " > " + string, false);
					clientGUI.logArea.setCaretPosition(clientGUI.logArea
							.getDocument().getLength());
					if (!clientGUI.f.isFocused()) {
						PlaySound.playClip(soundFile);
					}
					System.out.println(string);
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	public static void writeOut(String message, boolean timestamp) {
		try {
			FileWriter fw = new FileWriter(dirLocation + styleHost + ".txt",
					true);
			if (timestamp) {
				clientGUI.logArea.append(timeStamp() + message + "\n");
				fw.write(timeStamp() + message + "\n");
			} else {
				clientGUI.logArea.append(message + "\n");
				fw.write(message + "\n");
			}
			fw.close();
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	public static String timeStamp() {
		Date date = new Date();
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.get(Calendar.HOUR_OF_DAY);
		calendar.get(Calendar.HOUR);
		calendar.get(Calendar.MONTH);
		return "(" + calendar.get(Calendar.HOUR) + ":"
		+ calendar.get(Calendar.MINUTE) + ") ";
	}

	public static ArrayList<String> getVersion(String versionURL){
		ArrayList<String> arrayList = new ArrayList<String>();
		try{
			URL url = new URL(versionURL);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String currentLine;
			while ((currentLine = br.readLine()) != null){
				arrayList.add(currentLine);
			}
			br.close();
		}catch (Exception ex){
			System.err.println(ex);
		}
		return arrayList;
	}

}