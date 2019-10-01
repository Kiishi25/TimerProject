package TimerApplication;
import javax.swing.*;



import java.awt.*;
import java.awt.event.*;
import java.io.*;

import net.miginfocom.swing.MigLayout;

public class Timer extends JFrame {
	
	// Interface components
	
	// Fonts to be used
	Font countdownFont = new Font("Arial", Font.BOLD, 20);
	Font elapsedFont = new Font("Arial", Font.PLAIN, 14);
	Thread thread;
	TimerDialog td;
	
	// Labels and text fields
	JLabel countdownLabel = new JLabel("Seconds remaining:");
	JTextField countdownField = new JTextField(15);
	JLabel elapsedLabel = new JLabel("Time running:");
	JTextField elapsedField = new JTextField(15);
	JButton startButton = new JButton("START");
	JToggleButton pauseButton = new JToggleButton("PAUSE");
	JButton stopButton = new JButton("STOP");
	
	// The text area and the scroll pane in which it resides
	JTextArea display;
	
	JScrollPane myPane;
	
	// These represent the menus
	JMenuItem saveData = new JMenuItem("Save data", KeyEvent.VK_S);
	JMenuItem displayData = new JMenuItem("Display data", KeyEvent.VK_D);
	
	JMenu options = new JMenu("Options");
	
	JMenuBar menuBar = new JMenuBar();
	//joption chooser
	private JFileChooser jfc = new JFileChooser();
	
	// These booleans are used to indicate whether the START button has been clicked
	boolean started;
	
	// and the state of the timer (paused or running)
	boolean paused;
	
	
	
	// Number of seconds
	long totalSeconds = 0;
	long secondsToRun = 0;
	long secondsSinceStart = 0;
	
	// This is the thread that performs the countdown and can be started, paused and stopped
	TimerThread countdownThread;

	// Interface constructed
	Timer() {
		
		setTitle("Timer Application");
		
    	MigLayout layout = new MigLayout("fillx");
    	JPanel panel = new JPanel(layout);
    	getContentPane().add(panel);
    	jfc.setCurrentDirectory(new File(System.getProperty("user.dir")));
    	
    	options.add(saveData);
    	options.add(displayData);
    	menuBar.add(options);
    	
    	panel.add(menuBar, "spanx, north, wrap");
    	
    	MigLayout centralLayout = new MigLayout("fillx");
    	
    	JPanel centralPanel = new JPanel(centralLayout);
    	
    	GridLayout timeLayout = new GridLayout(2,2);
    	
    	JPanel timePanel = new JPanel(timeLayout);
    	
    	countdownField.setEditable(false);
    	countdownField.setHorizontalAlignment(JTextField.CENTER);
    	countdownField.setFont(countdownFont);
    	countdownField.setText("00:00:00");
    	
    	timePanel.add(countdownLabel);
    	timePanel.add(countdownField);

    	elapsedField.setEditable(false);
    	elapsedField.setHorizontalAlignment(JTextField.CENTER);
    	elapsedField.setFont(elapsedFont);
    	elapsedField.setText("00:00:00");
    	
    	timePanel.add(elapsedLabel);
    	timePanel.add(elapsedField);

    	centralPanel.add(timePanel, "wrap");
    	
    	GridLayout buttonLayout = new GridLayout(1, 3);
    	
    	JPanel buttonPanel = new JPanel(buttonLayout);
    	
    	buttonPanel.add(startButton);
    	buttonPanel.add(pauseButton, "");
    	buttonPanel.add(stopButton, "");
    	
    	centralPanel.add(buttonPanel, "spanx, growx, wrap");
    	
    	panel.add(centralPanel, "wrap");
    	
    	display = new JTextArea(100,150);
        display.setMargin(new Insets(5,5,5,5));
        display.setEditable(false);
        
        JScrollPane myPane = new JScrollPane(display, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panel.add(myPane, "alignybottom, h 100:320, wrap");
     
        // Initial state of system
        paused = false;
        started = false;
        
        // Allowing interface to be displayed
    	setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        
        // TODO: SAVE: This method should allow the user to specify a file name to which to save the contents of the text area using a 
        // JFileChooser. You should check to see that the file does not already exist in the system.

        saveData.addActionListener(new ActionListener() {
        	
			public void actionPerformed(ActionEvent e) { 
				if(started == true)  //IF THE APPLICATION IS RUNNING DISPLAY ERROR MESSAGE
				{  
					JOptionPane.showMessageDialog(Timer.this,
							"Timer has to be stopped to display data!",
							"ERROR", 
							JOptionPane.ERROR_MESSAGE);
					   }
				else   // IF ITS NOT RUNNING DISPLAY SAVE DIALOG USING FILE CHOOSER
				{
					int value = jfc.showSaveDialog(Timer.this);
					   if (value == JFileChooser.APPROVE_OPTION) { 
					     File f = jfc.getSelectedFile();
					     
					     try {
							writeDataFile(f); //CALLS WRITE DATA FILE METHOD
						
					     } catch (IOException ex) {
								
								ex.printStackTrace();
							}
					   }	   }}});
        
        // TODO: DISPLAY DATa: This method should retrieve the contents of a file representing a previous report using a JFileChooser.
        // The result should be displayed as the contents of a dialog object.
          displayData.addActionListener(new ActionListener() 
          {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(started == true) //IF THE APPLICATION IS RUNNING DISPLAY ERROR MESSAGE
				{
					JOptionPane.showMessageDialog(Timer.this,
							"Timer has to be stopped to display data!",
							"ERROR", 
							JOptionPane.ERROR_MESSAGE);
					   }
					   else // IF ITS NOT RUNNING DISPLAY OPEN DIALOG USING FILE CHOOSER
					   { 
						  
						   int value = jfc.showOpenDialog(Timer.this);
						   if (value == JFileChooser.APPROVE_OPTION)
						   {
							   File f = jfc.getSelectedFile(); 
						    
								try {
										readDataFile(f); //CALLS READ DATA FILE METHOD
									} catch (ClassNotFoundException e) {
										
										
										e.printStackTrace();
									} catch (IOException e) {
										
										e.printStackTrace();
										
									}		   
				}}}});
            
        
        // TODO: START: This method should check to see if the application is already running, and if not, launch a TimerThread object.
		// If the application is running, you may wish to ask the user if the existing thread should be stopped and a new thread started.
        // It should begin by launching a TimerDialog to get the number of seconds to count down, and then pass that number of seconds along
		// with the seconds since the start (0) to the TimerThread constructor.
		// It can then display a message in the text area stating how long the countdown is for.
        
    
       startButton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(started == true) //IF THE TIMER HAS ALREADY BEEN STARTED
				                //OPTION DIALOG IS DISPLAYED AND USER IS ASKED TO RESTART TIMER
			{
				 String message = " Do you want to restart the timer?";
			     
			     int answer = JOptionPane.showConfirmDialog(Timer.this, message);
			     if (answer == JOptionPane.YES_OPTION) {
			    	 
			    	    display.setText(""); //CLEARING TEXT AREA
			    	 
			    	   countdownThread.stop(); //STOPPING THREAD USING STOP() FROM TIMER THREAD CLASS
			    	 
			             td = new TimerDialog(Timer.this, secondsToRun,true); //CREATES TIMER DIALOG
			        	 
			            secondsToRun = td.getSeconds(); 
			    	    countdownThread = new TimerThread(countdownField,elapsedField, secondsToRun, secondsSinceStart,Timer.this);
			    	    
						thread = new Thread(countdownThread);	
						thread.start(); //STARTING THE THREAD
						
						display.append("Countdown for "  + " "+ secondsToRun + " " + "seconds\n"  );
						
			     } else if (answer == JOptionPane.NO_OPTION) {
			       // User clicked NO.
			    	 //DO NOTHING.
			     }
			     else if (answer == JOptionPane.CANCEL_OPTION){
			    	 // User clicked CANCEL
			    	 //DO NOTHING. 
			     }
			}
					else {
						
					td = new TimerDialog(Timer.this, secondsToRun,true);
					secondsToRun = td.getSeconds();
					
					countdownThread = new TimerThread(countdownField,elapsedField, secondsToRun, secondsSinceStart,Timer.this);
					thread = new Thread(countdownThread);	
					thread.start();
					
					 display.append("Countdown for "  + " "+ secondsToRun + " " + "seconds\n"  );
					 
					 started = true;
				}
			}});
			
	
		
        
        // TODO: PAUSE: This method should call the TimerThread object's pause method and display a message in the text area
        // indicating whether this represents pausing or restarting the timer.
       
       pauseButton.addItemListener(new ItemListener() { 

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			
			if(pauseButton.isSelected())  //IF PAUSE IS SELECTED SET TEXT TO RESUME
			{ 
				
				pauseButton.setText("RESUME");
				
				display.append("Paused at: " + "     "+  countdownThread.getElapsedSeconds() + "seconds\n");
				
				countdownThread.pause(); //CALLS PAUSE() FROM TIMER THREAD CLASS
	
				paused = true;
				  
			}
			else {
			
				
				pauseButton.setText("PAUSE");
				//long elapsed = countdownThread.getCountdownSeconds() - countdownThread.getElapsedSeconds(); // ms elapsed since last update
				countdownThread.pause();
				
                thread = new Thread(countdownThread);
				thread.start();
				//display.append("Restarted at:" + "  "+ elapsed + "seconds\n");
				
				display.append("Restarted at:" + "  "+ countdownThread.getCountdownSeconds() + "seconds\n");
				
		       paused = false;
			}
		
			}
	
		});
			

        // TODO: STOP: This method should stop the TimerThread object and use appropriate methods to display the stop time
        // and the total amount of time remaining in the countdown (if any).

	stopButton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			countdownThread.stop();
			
			started = false; //SETS STARTED TO FALSE
		
			     }});
	}
		
	
	// TODO: These methods can be used in the action listeners above.
	public synchronized void writeDataFile(File f) throws IOException, FileNotFoundException {
		
    BufferedWriter outFile = null;
    
      try {
    	  
       outFile = new BufferedWriter(new FileWriter(f));
       
       display.write(outFile);  
         }
        
    	   finally {
    		   
    		if (outFile != null) {
    			   
    	   outFile.close(); // Close stream to avoid resource leaks
        	  
     }}
        }
      
	
	// TODO: These methods can be used in the action listeners above.
		public synchronized String readDataFile(File f) throws IOException, ClassNotFoundException {
			
		String result = new String();	
	    BufferedReader br = null;
	    JDialog jd = new JDialog(Timer.this,f.getName()); //CREATES A NEW JDIALOG
	        
	        	try {
	            
	            br = new BufferedReader(new FileReader(f));
	          
	            while ((result != null)) {
	            	
	            	result = br.readLine();
	            	   
	            }
	              jd.add(display); //ADDS THE TEXT AREA TO THE JDIALOG
	              jd.setVisible(true); 
	              jd.setSize(300,300);
	              jd.setLocationRelativeTo(null); //PUTS DIALOG INSIDE TIMER FRAME
	              
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	            
	        } finally {
	            try {
	                if (br != null) {
	                    br.close(); //Close stream to avoid resource leaks
	                    
	                }
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
			  return result;
		  }
		  

	 
     // THIS METHOD UPDATES THE COUNTDOWN TEXT FIELD 	
	public void updateCountdownTextField(long seconds){
	
		String sec = countdownThread.convertToHMSString(seconds);
	   countdownField.setText(sec);
	   
	   if(started == false) {
		 countdownField.setText("00:00:00"); //SETS TEXT FIELD BACK ONCE TIMER IS STOPPED
			
	   }}
	
	// THIS METHOD UPDATES THE ELAPSED TEXT FIELD
	public void UpdateElapsedTextField(long seconds) {
		
		String sec =  countdownThread.convertToHMSString(seconds);
		elapsedField.setText(sec);
		 
		 if(started == false) {
		 elapsedField.setText("00:00:00"); //SETS TEXT FIELD BACK ONCE TIMER IS STOPPED
		}
		
	}
	
	public static void main(String[] args) {

        Timer timer = new Timer();

    }
}