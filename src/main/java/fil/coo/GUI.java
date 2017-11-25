package fil.coo;

import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import fil.coo.plugin.Plugin;
import fil.coo.checker.FileListener;
import fil.coo.checker.util.FileEvent;

@SuppressWarnings("serial")
public class GUI extends JFrame implements FileListener {

	protected JMenuBar menuBar = new JMenuBar();
	protected JMenu filesMenu = new JMenu("Files");
	protected JMenu pluginsMenu = new JMenu("Plugins");
	protected JMenu helpMenu = new JMenu("Help");
	protected JMenuItem openMenuItem = new JMenuItem("Open");
	protected JMenuItem resetMenuItem = new JMenuItem("Reset");
	protected JMenuItem closeMenuItem = new JMenuItem("Exit");
	protected JMenuItem helpApp = new JMenuItem("Help");
	protected JTextArea text = new JTextArea();


	public GUI(String title) {
		super(title);

	    this.setLocationRelativeTo(null);
		this.setSize(800, 600);


		this.add(new JScrollPane(this.text));

		// Ajout des items Files
		this.filesMenu.add(openMenuItem);
	    this.filesMenu.add(this.resetMenuItem);
	    this.filesMenu.addSeparator();
	    this.filesMenu.add(this.closeMenuItem);

	    this.openMenuItem.addActionListener(new OpenMenuItemActionListener());
	    this.closeMenuItem.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		System.exit(0);
	    	}
	    });
	    this.resetMenuItem.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		GUI.this.text.setText("");
	    	}
	    });


	    this.helpMenu.add(this.helpApp);
	    this.helpMenu.addSeparator();

		this.helpApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Vous trouverez ici le message d'aide");
			}
		});


	    this.menuBar.add(this.filesMenu);
	    this.menuBar.add(this.pluginsMenu);
	    this.menuBar.add(this.helpMenu);
	    this.setJMenuBar(this.menuBar);


		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}


  public void fileAdded(FileEvent file) {
    String name = (String) file.getSource();
    try {
      Class<?> c = Class.forName("fil.coo.plugins." + name.substring(0, name.lastIndexOf(".class")));
      Plugin plugin = (Plugin)c.getConstructor().newInstance();

			JMenuItem item = new JMenuItem(plugin.getLabel());
			this.pluginsMenu.add(item);
			item.addActionListener(new PluginActionMenuItemActionListener(plugin));

			item = new JMenuItem("help to : " + plugin.getLabel());
			this.helpMenu.add(item);
			item.addActionListener(new PluginHelpMenuItemActionListener(plugin));
    } catch (Exception e) {System.out.println("\t/!\\ PAS OK");}
  }

  public void fileRemoved(FileEvent file) {

  }


	protected class PluginHelpMenuItemActionListener implements ActionListener {
		protected Plugin plugin;

		public PluginHelpMenuItemActionListener(Plugin plugin) {
			super();
			this.plugin = plugin;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null, "HELP (" + this.plugin.getLabel() + ")\n\n" + this.plugin.helpMessage());
		}

	}

	protected class PluginActionMenuItemActionListener implements ActionListener {
		protected Plugin plugin;

		public PluginActionMenuItemActionListener(Plugin plugin) {
			super();
			this.plugin = plugin;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GUI.this.text.setText(this.plugin.transform(GUI.this.text.getText()));
		}

	}

	protected class OpenMenuItemActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
    		chooser.setFileFilter(new FileNameExtensionFilter("txt files", "txt"));
    		chooser.setCurrentDirectory(new File("./"));
    		int status = chooser.showOpenDialog(new JFrame());
    		if (status == JFileChooser.APPROVE_OPTION) {
    			File selectedFile = chooser.getSelectedFile();
    			GUI.this.text.setText(readFile(selectedFile.getAbsolutePath()));
    		}
		}

		private String readFile(String fileName) {
			File source = new File(fileName);
			BufferedReader in = null;
			String res = "";

			try {
				in = new BufferedReader(new FileReader(source));
				String text;
				// read block of 3 lines : text,answer and number of points
				while ((text = in.readLine())!= null)
					res += text + "\n";
				in.close();
			} catch (Exception e) {System.out.println(fileName);}

			return res;
		}

	}
}