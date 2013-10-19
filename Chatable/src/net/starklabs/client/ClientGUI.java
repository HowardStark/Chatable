package net.starklabs.client;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;

public class ClientGUI {
	
	private JTextField inputField = new JTextField(10);
	private JButton appendButton = new JButton("append");
	public JTextArea logArea = new JTextArea(10, 30);
	public JFrame f = new JFrame("Chatable");
	
	private ActionListener listener = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			String line = inputField.getText().trim();
			inputField.setText("");
			if (line.length() > 0) {
				Client.sendChat(Client.username + "|" + line);
				Client.writeOut(Client.username + " > " + line, true);
			}
			inputField.requestFocusInWindow();
		}
	};

	public ClientGUI() {
		inputField.addActionListener(listener);
		appendButton.addActionListener(listener);
		logArea.setEditable(false);
		logArea.setLineWrap(true);
		logArea.setWrapStyleWord(true);
		logArea.setCaretPosition(logArea.getDocument().getLength());

	}

	public void launchGUI() {
		JPanel p = new JPanel();
		p.add(new JLabel("Enter text:"));
		p.add(inputField);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getRootPane().setDefaultButton(appendButton);
		Container cp = f.getContentPane();
		cp.add(new JScrollPane(logArea), BorderLayout.CENTER);
		cp.add(p, BorderLayout.SOUTH);
		f.pack();
		f.setLocationRelativeTo(null);
        f.setAlwaysOnTop( true );
		f.setVisible(true);
	}

}
