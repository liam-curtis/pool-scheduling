import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.sql.*;

public class Pool_Scheduling 
{
	public static void main(String [] args)
	{
		
		try{  
			Connection con = DriverManager.getConnection("**jdbc server**","root","**password**");  
			
			Statement selectStatementEmployee = con.createStatement();
			String query = "SELECT * FROM Employee;";
			ResultSet rs = selectStatementEmployee.executeQuery(query);
			
			Statement selectStatementPool = con.createStatement();
			String queryPool = "SELECT * FROM Pool";
			ResultSet rsPool = selectStatementPool.executeQuery(queryPool);
	        
			//POOL INFORMATION
			ArrayList<Pool> poolList = new ArrayList<Pool>();
			HashMap<String, Integer> amountOfStaff = new HashMap<String, Integer>();
			amountOfStaff.put("M", 2);
			amountOfStaff.put("T", 2);
			amountOfStaff.put("W", 2);
			amountOfStaff.put("H", 2);
			amountOfStaff.put("F", 3);
			amountOfStaff.put("S", 3);
			amountOfStaff.put("U", 3);
			
			while(rsPool.next()) {
				Pool p = new Pool(amountOfStaff, rsPool.getString("PoolName"), rsPool.getInt("PoolID"));
				poolList.add(p);
				System.out.println("Successfully added Pool " + rsPool.getString("PoolName") + " to Application.");
			}
			
			while (rs.next()) {
				Lifeguard l = new Lifeguard(rs.getInt("EmpID"),rs.getString("FirstName"),rs.getString("LastName"),rs.getString("Title"),rs.getBoolean("Fulltime"),rs.getInt("SupervisorID"),rs.getDouble("Salary"),rs.getInt("PoolID"),rs.getInt("RegionID"),rs.getString("Availability"),rs.getBoolean("hasPoolOperator"));
			    
				for(Pool p : poolList)
				{
					if (p.getID() == rs.getInt("PoolID"))
							p.addGuard(l);
				}
			    System.out.println("Successfully added Employee " + rs.getInt("EmpID") + " to the staff.");
			}
			
			createGUI(poolList);
			con.close();  
		} catch(Exception e) { 
			System.out.println(e);
		}  
	}
	
	
	//creates the gui that the program uses
	public static void createGUI(ArrayList<Pool> poolList) {
        JFrame f = new JFrame("Lifeguard Management");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1200, 600);

        // Table to view shifts
        JTable shiftTable = new JTable(new DefaultTableModel(new Object[]{"Day", "Start", "End", "Lifeguard", "Cost", "Pool Operator"}, 0));
        JScrollPane scrollPane = new JScrollPane(shiftTable);
        
        // Dropdown bar to switch between pools
        JComboBox<String> poolDropdown = new JComboBox<>();
        for (Pool pool : poolList) {
            poolDropdown.addItem(pool.getName());
        }
        poolDropdown.addActionListener(e -> {
            String selectedPoolName = (String) poolDropdown.getSelectedItem();
            Pool selectedPool = null;
            for (Pool pool : poolList) {
                if (pool.getName().equals(selectedPoolName)) {
                    selectedPool = pool;
                    break;
                }
            }
            if (selectedPool != null) {
                updateTable(selectedPool, shiftTable); // Call the updateTable method with the selected Pool
            }
        });

        // Menu to add shifts and remove shifts
        JMenuBar menuBar = new JMenuBar();
        JMenu shiftMenu = new JMenu("Shifts");
        
        JMenuItem addShiftItem = new JMenuItem("Add Shift");
        addShiftItem.addActionListener(e -> {
        	//TODO
            // Handle adding a new shift to the selected pool
            // You can use dialog boxes or other components to get input from the user
        });
        shiftMenu.add(addShiftItem);
        
        JMenuItem removeShiftItem = new JMenuItem("Remove Shift");
        removeShiftItem.addActionListener(e -> {
            int selectedRow = shiftTable.getSelectedRow();
            if (selectedRow != -1) {
                DefaultTableModel model = (DefaultTableModel) shiftTable.getModel();
                model.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(f, "Please select a shift to remove.", "No Shift Selected", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        //unchanged above
        shiftMenu.add(removeShiftItem);

        menuBar.add(shiftMenu);

        // Add a new JMenu for adding/removing lifeguards
        JMenu lifeguardMenu = new JMenu("Lifeguards");

        JMenuItem addLifeguardItem = new JMenuItem("Add Lifeguard to Pool");
        addLifeguardItem.addActionListener(e -> {
            // Handle adding a new lifeguard to the selected pool
            // You can use dialog boxes or other components to get input from the user
            String selectedPoolName = (String) poolDropdown.getSelectedItem();
            Pool selectedPool = null;
            for (Pool pool : poolList) {
                if (pool.getName().equals(selectedPoolName)) {
                    selectedPool = pool;
                    break;
                }
            }
            if (selectedPool != null) {
            	// Create a pop-up dialog box to get lifeguard details from the user
                JTextField firstNameField = new JTextField();
                JTextField lastNameField = new JTextField();
                JTextField titleField = new JTextField();
                JTextField salaryField = new JTextField();
                JTextField availabilityField = new JTextField();
                JCheckBox fullTimeCheckBox = new JCheckBox("Full Time");
                JCheckBox poolOperatorCheckBox = new JCheckBox("Has Pool Operator");
                // Add more fields for other lifeguard properties like fullTime, supervisorId, salary, etc.

                Object[] fields = {
                    "First Name:", firstNameField,
                    "Last Name:", lastNameField,
                    "Title:", titleField,
                    "Salary:", salaryField,
                    "Availability (Raw):", availabilityField,
                    fullTimeCheckBox,
                    poolOperatorCheckBox
                };

                int option = JOptionPane.showConfirmDialog(f, fields, "Add New Lifeguard", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    // Retrieve the values entered by the user
                    String firstName = firstNameField.getText();
                    String lastName = lastNameField.getText();
                    String title = titleField.getText();
                    boolean fullTime = fullTimeCheckBox.isSelected();
                    int supervisorID = 5;
                    double salary = Double.parseDouble(salaryField.getText());
                    int poolID = selectedPool.getID();
                    int regionID = 5;
                    String availability = availabilityField.getText();
                    boolean poolOps = poolOperatorCheckBox.isSelected();
                    
                    int empID = 0;
                    try {
                    	//get next nessacery employee ID
                    	Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Pool_Scheduling","root","bobbles76");
                    	Statement selectStatementEmployee = con.createStatement();
                    	String query = "SELECT COUNT(*) FROM Employee;";
                    	ResultSet rs = selectStatementEmployee.executeQuery(query);
                    	if (rs.next()) {
                    		empID = rs.getInt(1); // Retrieve the count value from the first column of the result
                    	}
                    	
                    	//enter the new lifeguard into the database
                    	PreparedStatement enterEmployee = con.prepareStatement("INSERT INTO Employee (FirstName, LastName, Title, FullTime, SupervisorID, Salary, PoolID, RegionID, Availability, hasPoolOperator) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    	enterEmployee.setString(1, firstName);
                    	enterEmployee.setString(2, lastName);
                    	enterEmployee.setString(3, title);
                    	enterEmployee.setBoolean(4, fullTime);
                    	enterEmployee.setInt(5, supervisorID);
                    	enterEmployee.setDouble(6, salary);
                    	enterEmployee.setInt(7, poolID);
                    	enterEmployee.setInt(8, regionID);
                    	enterEmployee.setString(9, availability);
                    	enterEmployee.setBoolean(10, poolOps);
                    	
                    	enterEmployee.executeUpdate();
                    	System.out.println("Inserted records into the table...");
                    	
                    	con.close();
   
                    } catch (SQLException ex) {
                        // Handle database-related exceptions
                        ex.printStackTrace();
                    } 
                    
                    // Create a new Lifeguard object with the entered details
                    Lifeguard newLifeguard = new Lifeguard(empID, firstName, lastName, title, fullTime, supervisorID, salary, poolID, regionID, availability, poolOps);

                    // Add the new lifeguard to the selected pool
                    selectedPool.addGuard(newLifeguard);
                    selectedPool.shiftScheduler();
                    updateTable(selectedPool, shiftTable);
                }
                // Implement the logic to add a lifeguard to the selected pool
                // For example, prompt the user for lifeguard details and create a new Lifeguard object
                // Then add the lifeguard to the selectedPool using selectedPool.addLifeguard(newLifeguard);
            }
        });
        lifeguardMenu.add(addLifeguardItem);

        JMenuItem removeLifeguardItem = new JMenuItem("Remove Lifeguard from Pool");
        removeLifeguardItem.addActionListener(e -> {
            // Handle removing a lifeguard from the selected pool 
                String selectedPoolName = (String) poolDropdown.getSelectedItem();
                Pool selectedPool = null;
                for (Pool pool : poolList) {
                    if (pool.getName().equals(selectedPoolName)) {
                        selectedPool = pool;
                        break;
                    }
                }
                if (selectedPool != null) {
                	Lifeguard[] choices = new Lifeguard[selectedPool.getLifeguardList().size()];
                    choices = selectedPool.getLifeguardList().toArray(choices);
                    Lifeguard input = (Lifeguard) JOptionPane.showInputDialog(null, "Remove Lifeguard", "Which guard would you like to remove?", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]); // Initial choice
                    
                    if (input != null) {
                        // If the user selects a lifeguard and clicks "OK"
                    	System.out.println("Removed Lifeguard: " + input);
                        Lifeguard lifeguardToRemove = null;
                        for (Lifeguard lifeguard : selectedPool.getLifeguardList()) {
                            if (lifeguard.equals(input)) {
                                lifeguardToRemove = lifeguard;
                                break;
                            }
                        }
                        if (lifeguardToRemove != null) {
                            // Remove the selected lifeguard from the selected pool
                            selectedPool.removeLifeguard(lifeguardToRemove);
                            selectedPool.shiftScheduler();
                            updateTable(selectedPool, shiftTable);
                            
                            //removes the lifeguard from database
                            try {
                            	//get next nessacery employee ID
                            	Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Pool_Scheduling","root","bobbles76");
                            	PreparedStatement enterEmployee = con.prepareStatement("DELETE FROM Employee WHERE EmpID = ?");
                            	enterEmployee.setInt(1, lifeguardToRemove.getID());
                            	enterEmployee.executeUpdate();
                            	con.close();
                            } catch (SQLException ex) {
                                // Handle database-related exceptions
                                ex.printStackTrace();
                            } 
                        }
                    }
                }

        });
        lifeguardMenu.add(removeLifeguardItem);

        // Add the new JMenu to the menu bar
        menuBar.add(lifeguardMenu);
        
        shiftMenu.add(removeShiftItem);
        
        menuBar.add(shiftMenu);

        // Add components to the frame
        f.setJMenuBar(menuBar);
        f.add(poolDropdown, BorderLayout.NORTH);
        f.add(scrollPane, BorderLayout.CENTER);

        // Show the frame
        f.setVisible(true);
    }
	
	//updates the table every time a new pool is selected from the drop down menu
	private static void updateTable(Pool pool, JTable shiftTable) {
	    DefaultTableModel model = (DefaultTableModel) shiftTable.getModel();
	    model.setRowCount(0); // Clear existing rows

	    HashMap<String, ArrayList<Shift>> schedule = pool.getSchedule();

	    // Create a custom Comparator to sort shifts by day of the week (Sunday to Saturday)
	    Comparator<Shift> shiftComparator = (s1, s2) -> {
	        String day1 = s1.getDay();
	        String day2 = s2.getDay();
	        // Custom sorting order (Sunday -> Monday -> ... -> Saturday)
	        String[] daysOfWeek = {"U", "M", "T", "W", "H", "F", "S"};
	        int index1 = Arrays.asList(daysOfWeek).indexOf(day1);
	        int index2 = Arrays.asList(daysOfWeek).indexOf(day2);
	        return Integer.compare(index1, index2);
	    };

	    ArrayList<Shift> sortedShifts = new ArrayList<>();

	    for (ArrayList<Shift> shifts : schedule.values()) {
	        sortedShifts.addAll(shifts);
	    }

	    // Sort the shifts based on the custom comparator
	    Collections.sort(sortedShifts, shiftComparator);

	    for (Shift shift : sortedShifts) {
	        model.addRow(new Object[]{
	            shift.getDayWritten(),
	            shift.getStartTime(),
	            shift.getEndTime(),
	            shift.getLifeguard().getName(),
	            shift.getCost(),
	            shift.getLifeguard().getPoolOps()
	        });
	    }
	}
	

}

interface Employee {}

class Lifeguard implements Employee
{
	private int EmpId;
	private String firstName;
	private String lastName;
	private String title;
	private boolean fullTime;
	private String rawAvailability;
	private HashMap<String, ArrayList<Integer>> availability;
	private int supervisorId;
	private double salary;
	private int phoneNumber;
	private int poolID;
	private int regionID;
	private ArrayList<Shift> shiftList;
	public boolean hasPoolOperator;
	public int hours;
	
	//default constructor of Lifeguard
	public Lifeguard(int i, String fN, String lN, String t, boolean fT, int sID, double s, int pID, int rID, String rA, boolean hPO)
	{
		EmpId = i;
		firstName = fN;
		lastName = lN;
		title = t;
		fullTime = fT;
		supervisorId = sID;
		salary = s;
		poolID = pID;
		regionID = rID;
		rawAvailability = rA;
		hasPoolOperator = hPO;
		parseAvailability();
	}
	
	//parses the raw availability string into a HashMap of times, with k=day and v=hours available, with the first value
	//of the arraylist being the start time and the second value being the end time
	public void parseAvailability()
	{
		availability = new HashMap<String, ArrayList<Integer>>(); //initialize the formatted availability HashMap
		int dayIndex = 0; //should never go above 7 (# of days)
		int stringIndex = 0; //can go above 7, should always be less than length of rawAvailability
		while(stringIndex != rawAvailability.length() || !rawAvailability.substring(stringIndex,stringIndex+1).equals("*") || dayIndex >= 7)
		{
			String current = rawAvailability.substring(stringIndex,stringIndex+1);
			//start of parsing section
			if(current.equals("M") || current.equals("T") || current.equals("W") || current.equals("H")|| current.equals("F") || current.equals("S") || current.equals("U"))
			{
				dayIndex++;
				ArrayList <Integer> times = new ArrayList<Integer>();
				HashSet<String> days = new HashSet<String>(); //contains all of the days in the current time
				days.add(current);
				stringIndex++;
				while(!rawAvailability.substring(stringIndex,stringIndex+1).equals(" ")) //cycle to end of listing of all days and add all days to HashSet
				{
					dayIndex++;
					current = rawAvailability.substring(stringIndex,stringIndex+1);
					days.add(current);
					stringIndex++;
				}
				stringIndex++;
				
				//gather the start and end times
				String startTimeRaw = rawAvailability.substring(stringIndex,stringIndex+1);
				stringIndex++;
				if(!rawAvailability.substring(stringIndex,stringIndex+1).equals("-"))
				{ //checks to see if double digit
					startTimeRaw += rawAvailability.substring(stringIndex,stringIndex+1);
					stringIndex++;
				}
				int startTime = Integer.parseInt(startTimeRaw);
				
				stringIndex++; //goes over the - 
				
				String endTimeRaw = rawAvailability.substring(stringIndex,stringIndex+1);
				stringIndex++;
				if(!rawAvailability.substring(stringIndex,stringIndex+1).equals(",") && !rawAvailability.substring(stringIndex,stringIndex+1).equals("*"))
				{ //checks to see if double digit
					endTimeRaw += rawAvailability.substring(stringIndex,stringIndex+1);
					stringIndex++;
				}
				int endTime = Integer.parseInt(endTimeRaw);
				
				//put everything together
				times.add(startTime);
				times.add(endTime);
				for(String d : days)
					availability.put(d, times);
			}
			stringIndex++;
			if(stringIndex == rawAvailability.length())
				break;
		}
	}
	
	public HashMap<String, ArrayList<Integer>> getAvailability()
	{
		return availability;
	}
	
	public double getSalary() 
	{
		return salary;
	}
	public String getName() 
	{
		return firstName + " " + lastName;
	}
	public int getID()
	{
		return EmpId;
	}
	
	public String toString()
	{
		return this.getName() + ", " + title;
	}
	
	public boolean getPoolOps()
	{
		return hasPoolOperator;
	}
	
	public void setHours(int h)
	{
		hours = h;
	}
}

class Supervisor implements Employee{}

class Pool
{
	
	private ArrayList <Lifeguard> lifeguardList;
	private HashMap <String, ArrayList<Shift>> schedule;
	private HashMap <String, Integer> numberOfLifeguards; //contains all the days and the expected needs of the pool
	private HashMap <Lifeguard, Integer> trackHours;
	private int poolID;
	private String name;
	
	public Pool(ArrayList <Lifeguard> lL, HashMap <String, Integer> nOL, String n)
	{
		lifeguardList = lL;
		numberOfLifeguards = nOL;
		name = n;
		shiftScheduler();
	}
	
	public Pool(HashMap <String, Integer> nOL, String n, int pID)
	{
		poolID = pID;
		numberOfLifeguards = nOL;
		name = n;
		lifeguardList = new ArrayList<Lifeguard>();
	}
	
	//method to schedule the shifts
	public void shiftScheduler()
	{
		trackHours = new HashMap<Lifeguard, Integer> ();
		schedule = new HashMap<String, ArrayList<Shift>>();
		
		for(Lifeguard l : lifeguardList)
			trackHours.put(l, 0);
		
		for (String day : numberOfLifeguards.keySet())
		{
			//number of lifeguards needed
			int beginningDay = numberOfLifeguards.get(day);
			int endDay = numberOfLifeguards.get(day);
			
			//loop through all lifeguards
			for(Lifeguard l : lifeguardList)
			{
				//check to see if lifeguard is available, and if they have less than 40 hours
				if(l.getAvailability().containsKey(day) && trackHours.get(l) < 40)
				{
					ArrayList<Integer> hours = l.getAvailability().get(day);
					int start = hours.get(0);
					int end = hours .get(1);
					Shift s;
					//check to see if their availale time is needed
					if(beginningDay > 0 && endDay > 0) 
					{
						if((start == 11 || start == 12) && (end >= 8)) //full day
						{
							s = new Shift(day, 11, 8, l);
							beginningDay--;
							endDay--;
							trackHours.put(l, trackHours.get(l) + 9);
						} else if (start == 11 || start == 12) //morning shift
						{
							s = new Shift(day, 11, 4, l);
							beginningDay--;
							trackHours.put(l, trackHours.get(l) + 5);
						} else //closing shift
						{
							s = new Shift(day, 4, 8, l);
							endDay--;
							trackHours.put(l, trackHours.get(l) + 4);
						}
						//adding the new shift to the schedule
						if(schedule.containsKey(day))
						{
							ArrayList<Shift> shiftList = schedule.get(day);
							shiftList.add(s);
							schedule.put(day, shiftList);
						}
						else
						{
							ArrayList<Shift> shiftList = new ArrayList<Shift>();
							shiftList.add(s);
							schedule.put(day, shiftList);
						}
						
					} else if(beginningDay > 0) 
					{
						if (start == 11 || start == 12) //morning shift
						{
							//create beginning shift
							beginningDay--;
							trackHours.put(l, trackHours.get(l) + 5);
							s = new Shift(day,11,4,l);
							if(schedule.containsKey(day))
							{
								ArrayList<Shift> shiftList = schedule.get(day);
								shiftList.add(s);
								schedule.put(day, shiftList);
							}
							else
							{
								ArrayList<Shift> shiftList = new ArrayList<Shift>();
								shiftList.add(s);
								schedule.put(day, shiftList);
							}
						}
					} else if (endDay > 0) 
					{
						if (end >= 8) //morning shift
						{
							//create end day shift
							endDay--;
							trackHours.put(l, trackHours.get(l) + 4);
							s = new Shift(day,4,8,l);
							if(schedule.containsKey(day))
							{
								ArrayList<Shift> shiftList = schedule.get(day);
								shiftList.add(s);
								schedule.put(day, shiftList);
							}
							else
							{
								ArrayList<Shift> shiftList = new ArrayList<Shift>();
								shiftList.add(s);
								schedule.put(day, shiftList);
							}
						}
					}	
				}
			}
		}
		
		updateHours(); //update the track hours to all lifeguard objects
		balanceSchedule(); //balance the schedule 
		
	}
	
	//this method is meant to balance the schedule
	//has two goals:
	//1. ensure that a pool operator is working at all times
	//2. get all workers as close to 40 hours as possible
	public void balanceSchedule()
	{
		//TODO
	}
	
	public ArrayList<Lifeguard> getLifeguardList()
	{
		return lifeguardList;
	}
	
	public void addGuard(Lifeguard l)
	{
		lifeguardList.add(l);
		shiftScheduler();
	}
	
	public HashMap<String, ArrayList<Shift>> getSchedule() 
	{
		return schedule;
	}
	
	public HashMap<Lifeguard, Integer> getHours()
	{
		return trackHours;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getID()
	{
		return poolID;
	}
	
	public void updateHours()
	{
		for(Lifeguard l : trackHours.keySet())
			l.setHours(trackHours.get(l));
	}
	
	public void removeLifeguard(Lifeguard guard)
	{
		lifeguardList.remove(guard);
	}
}


//Scheules should be 11-8, 11-4, or 4-8
class Shift
{
	private String day; //M T W H F S U represent the 7 days of the week
	private Lifeguard guard;
	private int startTime;
	private int endTime;
	private double cost;
	
	public Shift(String d, int sT, int eT, Lifeguard g)
	{
		day = d;
		startTime = sT;
		endTime = eT;
		guard = g;
		
		//calculate cost of the shift
		int tempStart = startTime;
		if (tempStart >= 10)
			tempStart -= 12;
		cost = (endTime - tempStart) * guard.getSalary();
	}
	
	public String getDay()
	{
		return day;
	}
	
	public double getCost()
	{
		return cost;
	}
	
	public String toString() 
	{
		return startTime + "-" + endTime + " " + guard.getName();
	}
	
	public int getStartTime()
	{
		return startTime;
	}
	
	public int getEndTime()
	{
		return endTime;
	}
	
	public Lifeguard getLifeguard()
	{
		return guard;
	}
	
	public String getDayWritten()
	{
		String letter = this.getDay();
		
		if(letter.equals("S"))
			return "Saturday";
		else if(letter.equals("U"))
			return "Sunday";
		else if(letter.equals("M"))
			return "Monday";
		else if(letter.equals("T"))
			return "Tuesday";
		else if(letter.equals("W"))
			return "Wednesday";
		else if(letter.equals("H"))
			return "Thursday";
		else
			return "Friday";
	}
}



