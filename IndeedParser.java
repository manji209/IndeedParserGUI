package com.hotech.indeed;

import java.io.BufferedReader;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
//import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;



//import com.ibm.icu.impl.duration.TimeUnit;

public class IndeedParser {

	protected Shell shlIndeedContact;
	// static List<String> tokenURL = new ArrayList<String>();
	static List<String> userList = new ArrayList<String>();
	static List<String> selectedList = new ArrayList<String>();
	static List<String> skillsTok = new ArrayList<String>();
	static LinkedList<UserInfo> person = new LinkedList<UserInfo>();
	private static int processed = 0;
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Process the source page and extract the user links
		processFiles();

		try {
			IndeedParser window = new IndeedParser();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlIndeedContact.open();
		shlIndeedContact.layout();
		while (!shlIndeedContact.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlIndeedContact = new Shell();
		shlIndeedContact.setSize(671, 443);
		shlIndeedContact.setText("Indeed Contact");

		// GUI List copied from userList
		org.eclipse.swt.widgets.List contactList = new org.eclipse.swt.widgets.List(shlIndeedContact,
				SWT.BORDER | SWT.V_SCROLL);
		contactList.setEnabled(true);
		if (!userList.isEmpty()) {
			for (String item : userList) {
				contactList.add(item);
			}
		}
		contactList.setBounds(21, 53, 212, 224);

		Label lblContactsList = new Label(shlIndeedContact, SWT.NONE);
		lblContactsList.setBounds(21, 25, 84, 15);
		lblContactsList.setText("Contacts List");

		Label lblContactsFound = new Label(shlIndeedContact, SWT.NONE);
		lblContactsFound.setBounds(21, 307, 97, 15);
		lblContactsFound.setText("Contacts Found:");

		Label lblContactsSelected = new Label(shlIndeedContact, SWT.NONE);
		lblContactsSelected.setBounds(21, 334, 104, 15);
		lblContactsSelected.setText("Contacts Selected:");

		Label lblContactsProcessed = new Label(shlIndeedContact, SWT.NONE);
		lblContactsProcessed.setBounds(22, 361, 114, 15);
		lblContactsProcessed.setText("Contacts Processed");

		Label lblNumContacts = new Label(shlIndeedContact, SWT.NONE);
		lblNumContacts.setAlignment(SWT.RIGHT);
		lblNumContacts.setBounds(178, 307, 55, 15);
		lblNumContacts.setText(Integer.toString(userList.size()));

		Label lblNumContactsSelected = new Label(shlIndeedContact, SWT.NONE);
		lblNumContactsSelected.setText("0");
		lblNumContactsSelected.setAlignment(SWT.RIGHT);
		lblNumContactsSelected.setBounds(178, 334, 55, 15);

		Label lblNumContactsProcessed = new Label(shlIndeedContact, SWT.NONE);
		lblNumContactsProcessed.setText(Integer.toString(processed));
		lblNumContactsProcessed.setAlignment(SWT.RIGHT);
		lblNumContactsProcessed.setBounds(178, 361, 55, 15);

		org.eclipse.swt.widgets.List contactsSelected = new org.eclipse.swt.widgets.List(shlIndeedContact, SWT.BORDER);
		contactsSelected.setBounds(422, 53, 212, 224);

		Label lblContactsSelected_1 = new Label(shlIndeedContact, SWT.NONE);
		lblContactsSelected_1.setText("Contacts Selected");
		lblContactsSelected_1.setBounds(420, 25, 104, 15);

		Button btnAddName = new Button(shlIndeedContact, SWT.NONE);
		btnAddName.setBounds(290, 107, 75, 45);
		btnAddName.setText(">>");

		btnAddName.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				if (contactList.getSelectionCount() > 0) {
					selectedList.add(contactList.getItem(contactList.getSelectionIndex()));
					contactsSelected.add(contactList.getItem(contactList.getSelectionIndex()));
					contactList.remove(contactList.getSelectionIndex());
					lblNumContactsSelected.setText(Integer.toString(contactsSelected.getItemCount()));
				} else {

					MessageBox messageBox = new MessageBox(shlIndeedContact, SWT.OK | SWT.ICON_ERROR);
					messageBox.setText("Alert");
					String errorMsg = "No contact selected";
					messageBox.setMessage(errorMsg);
					messageBox.open();

				}

			}
		});

		Button btnRemoveName = new Button(shlIndeedContact, SWT.NONE);
		btnRemoveName.setText("<<");
		btnRemoveName.setBounds(290, 177, 75, 45);

		btnRemoveName.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				if (contactsSelected.getSelectionCount() > 0) {
					contactList.add(contactsSelected.getItem(contactsSelected.getSelectionIndex()));
					selectedList.remove(contactsSelected.getSelectionIndex());
					contactsSelected.remove(contactsSelected.getSelectionIndex());
					lblNumContactsSelected.setText(Integer.toString(contactsSelected.getItemCount()));
				} else {

					MessageBox messageBox = new MessageBox(shlIndeedContact, SWT.OK | SWT.ICON_ERROR);
					messageBox.setText("Alert");
					String errorMsg = "No contact selected";
					messageBox.setMessage(errorMsg);
					messageBox.open();

				}

			}
		});

		Button btnProcessSelected = new Button(shlIndeedContact, SWT.NONE);
		btnProcessSelected.setBounds(422, 297, 212, 34);
		btnProcessSelected.setText("Process Selected");

		btnProcessSelected.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				if (!selectedList.isEmpty()) {

					MessageBox messageBox = new MessageBox(shlIndeedContact,
							SWT.OK | SWT.CANCEL | SWT.ICON_INFORMATION);
					messageBox.setText("Alert");
					String errorMsg = "Do you want to proceed";
					messageBox.setMessage(errorMsg);
					int val = messageBox.open();

					switch (val) {
					case SWT.OK:
						new Thread(new Runnable() {
							public void run() {
								try {
									createContacts(selectedList, shlIndeedContact, lblNumContactsProcessed);
								} // 1 second pause
								catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
						break;
					case SWT.CANCEL:
						break;
					}
				} else {

					MessageBox messageBox = new MessageBox(shlIndeedContact, SWT.OK | SWT.ICON_ERROR);
					messageBox.setText("Alert");
					String errorMsg = "No Contacts to Process";
					messageBox.setMessage(errorMsg);
					messageBox.open();

				}

			}
		});

		
		Button btnProcessAll = new Button(shlIndeedContact, SWT.NONE);
		btnProcessAll.setText("Process All");
		btnProcessAll.setBounds(422, 342, 212, 34);

		btnProcessAll.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				System.out.println("Number of users: " + userList.size());
				if (!userList.isEmpty()) {

					MessageBox messageBox = new MessageBox(shlIndeedContact,
							SWT.OK | SWT.CANCEL | SWT.ICON_INFORMATION);
					messageBox.setText("Alert");
					String errorMsg = "Do you want to proceed";
					messageBox.setMessage(errorMsg);
					int val = messageBox.open();

					switch (val) {
					case SWT.OK:
						new Thread(new Runnable() {
							public void run() {
								try {
									createContacts(userList, shlIndeedContact, lblNumContactsProcessed);
								} 
								catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
						
						break;
					case SWT.CANCEL:
						break;
					}
				} else {

					MessageBox messageBox = new MessageBox(shlIndeedContact, SWT.OK | SWT.ICON_ERROR);
					messageBox.setText("Alert");
					String errorMsg = "No Contacts to Process";
					messageBox.setMessage(errorMsg);
					messageBox.open();

				}

			}
		});

		Button btnRescan = new Button(shlIndeedContact, SWT.NONE);
		btnRescan.setBounds(290, 53, 75, 25);
		btnRescan.setText("<- Rescan");

		btnRescan.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				MessageBox messageBox = new MessageBox(shlIndeedContact, SWT.OK | SWT.CANCEL | SWT.ICON_INFORMATION);
				messageBox.setText("Alert");
				String errorMsg = "Continue with rescan?";
				messageBox.setMessage(errorMsg);
				int val = messageBox.open();

				switch (val) {
				case SWT.OK:
					userList.clear();
					contactList.removeAll();
					if (!processFiles()) {

						MessageBox noFilesBox = new MessageBox(shlIndeedContact, SWT.OK | SWT.ICON_INFORMATION);
						messageBox.setText("Alert");
						String errMsg = "No files found";
						noFilesBox.setMessage(errMsg);
						noFilesBox.open();
						lblNumContacts.setText(Integer.toString(userList.size()));
					}

					if (!userList.isEmpty()) {
						for (String item : userList) {
							contactList.add(item);
						}
						lblNumContacts.setText(Integer.toString(userList.size()));
					}

					break;
				case SWT.CANCEL:
					break;
				}

			}
		});

		Button btnResetSelected = new Button(shlIndeedContact, SWT.NONE);
		btnResetSelected.setBounds(290, 252, 75, 25);
		btnResetSelected.setText("Clear ->");

		btnResetSelected.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {

				int contacts = contactsSelected.getItemCount();
				if (contacts > 0) {
					for (int i = 0; i < contacts; i++) {
						contactList.add(contactsSelected.getItem(0));
						selectedList.remove(0);
						contactsSelected.remove(0);
						lblNumContactsSelected.setText(Integer.toString(contactsSelected.getItemCount()));
						processed = 0;
						lblNumContactsProcessed.setText(Integer.toString(processed));

					}
				} else {

					MessageBox messageBox = new MessageBox(shlIndeedContact, SWT.OK | SWT.ICON_ERROR);
					messageBox.setText("Alert");
					String errorMsg = "No contact selected";
					messageBox.setMessage(errorMsg);
					messageBox.open();

				}

			}
		});

	}

	// Process the downloaded source files and extract the individual user links
	public static boolean processFiles() {
		// int num = 0;
		List<String> tokenURL = new ArrayList<String>();
		List<String> results = new ArrayList<String>();

		File[] files = new File("./").listFiles();
		// If this pathname does not denote a directory, then listFiles()
		// returns null.

		for (File file : files) {
			if (file.isFile() && file.getName().contains("indeed")) {
				if (file.getName().contains(".txt")) {
					results.add(file.getName());
				} else {
					results.add(file.getName() + ".txt");
				}
			}
		}

		if (results.isEmpty()) {

			return false;
		}

		for (String fileName : results) {

			try {

				BufferedReader br = new BufferedReader(new FileReader(fileName));
				String line;
				PrintWriter out = new PrintWriter("module2.part1.out", "utf-8");

				while ((line = br.readLine()) != null) {
					ProcessLinks doc = new ProcessLinks(line);

					tokenURL = doc.processURL();

					for (String tok : tokenURL) {
						// System.out.println(tok);
						userList.add(tok);
						out.print(tok);
						// num++;
					}

				}
				out.print("\n");
				out.close();
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// System.out.println("Number of Names: " + num);
		// System.out.println("Size of tokenURL: " + userList.size());

		return true;
	}

	public static void createContacts(List<String> contacts, Shell mainWindow, Label numProcessed)
			throws InterruptedException {

		String indeedURL = "http://www.indeed.com";
		String url = null;
		String filename = "testresume.txt";
		processed = 0;

		Random rand = new Random();

		// Process each Resume individually
		for (String str : contacts) {

			int n = rand.nextInt(12) + 1;


			processResume(str);

			processed++;
			//numProcessed.setText(Integer.toString(processed));

			//Run as a new Thread to get access to the main Display
			setLableContactsProcessed(getDisplay(), numProcessed, processed);
			// Random delay for processing each resume

			try {
				sleep(n);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Save to Excel sheet
		try {
			SaveToExcel(person);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Run as new Thread to call main Thread
		msgBoxCompletion(getDisplay(), mainWindow);
		
	}

	

	public static void processResume(String info) {

		// List<String> tokenResponsibilities = new ArrayList<String>();
		List<String> tokenLocation = new ArrayList<String>();
		List<String> tokenWork = new ArrayList<String>();
		// List<String> tokenWorkHistory = new ArrayList<String>();
		String saveDir = ".";
		String resumeFileName = "";
		String workText = "";
		String responsibilities = null;
		String contactURL = "http://www.indeed.com" + info;
		int workIndex = 0;

		// Process the firstname, lastname, and resume from List<String>
		// userList
		// for (String url : userList) {
		UserInfo user = new UserInfo(info);
		user.setFirstName();
		user.setLastName();
		user.setFullName();
		// user.setLocation();
		user.setResume(info);

		resumeFileName = user.getFullName() + ".pdf";
		// download Resume

		try {
			downloadResume(user.getResume(), saveDir, resumeFileName);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// }
		
		 try(BufferedReader in = new BufferedReader(
		            new InputStreamReader(new URL(contactURL).openStream()))) {
		        String line = null;
		        while((line = in.readLine()) != null) {
		            //System.out.println(line);
		        	UserInfo doc = new UserInfo(line);

					// Parse through doc file and tokenize skills
					// doc.findResponsibilities();

					// WORK ON THIS - strip bullet from line
					if (responsibilities == null) {
						doc.findResponsibilities();
						responsibilities = doc.getResponsibilities();

						if (responsibilities != null) {

							// Replaces all non-UTF-8 characters that represent
							// bullet points
							responsibilities = responsibilities.replaceAll("[^\\x00-\\x7F]", "")
									.replaceAll("\\<.*?\\>", "?").replaceAll("&nbsp;", "?");

							user.setResponsibilitiesList(responsibilities);

						
						}

					}

					// Parse through doc file and get user location
		
					tokenLocation = doc.findLocation();

					// Parse through doc file and get user work history
					doc.setWorkHistory();

					tokenWork = doc.getWorkHistory();

					// WORK ON THIS: Get Location
					for (String tok : tokenLocation) {
						// System.out.println("Location of user: " + tok);
						user.setlocation(tok);
						
					}

					// WORK ON THIS: Get Work History
					for (String tok : tokenWork) {
						// System.out.println("Work History: " + tok);

						if (workIndex < 2) {
							// Remove all HTML tags and replace with "-" for further
							// processing
							workText = tok.replaceAll("\\<.*?\\>", "-");

							// Extract individual work history from workText using
							// "-" as delimiter
							user.setWorkRecent(workText);
							// System.out.println(workText);

							workIndex++;
						} else {
							continue;
						
						
						}
					}
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }

		

		person.addLast(user);

	}

	public static void downloadResume(String fileURL, String saveDir, String resumeName) throws IOException {

		URL url = new URL(fileURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();
		String fileName = "";

		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			// extracts file name from URL

			fileName = resumeName;

			// opens input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();
			String saveFilePath = saveDir + File.separator + fileName;

			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			System.out.println("File downloaded");

		} else {
			System.out.println("No file to download. Server replied HTTP code: " + responseCode);
		}
		httpConn.disconnect();

	}

	public static void SaveToExcel(LinkedList<UserInfo> userList) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Indeed Contacts");
		XSSFCreationHelper richTextFactory = workbook.getCreationHelper();
		List<String> header = new ArrayList<String>();
		List<String> userInfo = new ArrayList<String>();

		// int MAX_COLUMNS = 13;

		// Create the headers for the spreadsheet
		header.add("FullName");
		header.add("FirstName");
		header.add("LastName");
		header.add("JobTitle");
		header.add("Location");
		header.add("Recent Work History JobTitle");
		header.add("Recent Work History Company");
		header.add("Recent Work History Location");
		header.add("Recent Work History Date");
		header.add("Prior Work History JobTitle");
		header.add("Prior Work History Company");
		header.add("Prior Work History Location");
		header.add("Prior Work History Date");

		int rowCount = 0;
		int columnCount = 0;

		Row title = sheet.createRow(rowCount++);

		// Populate the header fields
		for (String info : header) {
			Cell cell = title.createCell(columnCount++);
			cell.setCellValue(info);
		}

		for (UserInfo user : person) {
			userInfo = UserToList(user);
			Row row = sheet.createRow(rowCount++);
			columnCount = 0;

			for (String item : userInfo) {
				Cell cell = row.createCell(columnCount++);
				cell.setCellValue(item);

				if (columnCount == 6) {

					String tempString = ":::";
					/* Create Drawing Object to hold comment */
					XSSFDrawing drawing = sheet.createDrawingPatriarch();
					/*
					 * Let us draw a big comment box to hold lots of comment
					 * data
					 */
					XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);
					/* Create a comment object */
					XSSFComment comment1 = drawing.createCellComment(anchor);

					for (String temp : user.getResponsibilitiesList()) {
						tempString = tempString + temp + "\n" + ":::";
					}
					/* Create some comment text as Rich Text String */
					XSSFRichTextString rtf1 = richTextFactory.createRichTextString(tempString);
					comment1.setString(rtf1);
					cell.setCellComment(comment1);

				}
			}

		}

		// Auto size all the columns
		for (int x = 0; x < sheet.getRow(0).getPhysicalNumberOfCells(); x++) {
			sheet.autoSizeColumn(x);
		}

		try (FileOutputStream outputStream = new FileOutputStream("Contacts.xlsx")) {
			workbook.write(outputStream);
			outputStream.flush();
			outputStream.close();
		}
		workbook.close();

	}

	public static List<String> UserToList(UserInfo user) {
		List<String> info = new ArrayList<String>();
		List<String> work = new ArrayList<String>();
		String location;

		work = user.getWorkRecent();
		location = user.getLocation();

		info.add(user.getFullName());
		info.add(user.getFirstName());
		info.add(user.getLastName());

		if (!work.isEmpty()) {
			info.add(work.get(0));
		} else {
			info.add("null");
		}
		info.add(location);
		// info.add(user.getLocation().get(0));

		// work = user.getWorkRecent();
		for (String str : work) {
			info.add(str);
		}

		return info;
	}

	public static Integer getNumProcessed() {
		return processed;
	}

	

	//Send HTTP request to delay the createContacts loop.  Using this instead of Thread.sleep()
	public static void sleep(Integer sleepSeconds) throws MalformedURLException, IOException {

		String url = "http://1.cuzillion.com/bin/resource.cgi?sleep=" + sleepSeconds;


		InputStream response = new URL(url).openStream();

		response.close();

	}

	//Create a new thread to update the lblContactsProcessed 
	private static void setLableContactsProcessed(final Display display, final Label numProcessed, final int processed) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!numProcessed.isDisposed()) {
					numProcessed.setText(Integer.toString(processed));
					numProcessed.getParent().layout();
				}
			}
		});
	}
	
	//Create a new thread to display msgBox once contacts have been processed
	private static void msgBoxCompletion(final Display display, Shell mainWindow) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageBox messageBox = new MessageBox(mainWindow, SWT.OK | SWT.ICON_INFORMATION);
				messageBox.setText("Alert");
				String errorMsg = "Contacts Processed and Saved";
				messageBox.setMessage(errorMsg);
				messageBox.open();
				
			}
		});
	}
	
	//Get the current GUI display to be used for setLableContactsProcessed and msgBoxCompletion methods
	 public static Display getDisplay() {
	      Display display = Display.getCurrent();
	      //may be null if outside the UI thread
	      if (display == null)
	         display = Display.getDefault();
	      return display;		
	   }
	 
	 public static void disableButtons(){
		
	 }

}
