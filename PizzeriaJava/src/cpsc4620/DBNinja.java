package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 *
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 *
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 *
 *
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "Pickup";
	public final static String delivery = "Delivery";
	public final static String dine_in = "Dine-In";

	public final static String size_s = "Small";
	public final static String size_m = "Medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";




	private static boolean connect_to_db() throws SQLException, IOException {
		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}


	public static void addOrder(Order o) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 *
		 */
		String curOrder = "insert into user_order(Customer_ID, Order_Type, Order_Time, Order_Price, Order_Cost, Order_Status)"
				+ " values" + "(?,?,?,?,?,?)";
		try (PreparedStatement state = conn.prepareStatement(curOrder)) {
			state.setInt(1, o.getCustID());
			state.setString(2, o.getOrderType());
			state.setString(3, o.getDate());
			state.setDouble(4, o.getCustPrice());
			state.setDouble(5, o.getBusPrice());
			state.setInt(6, o.getIsComplete());
			state.executeUpdate();
			// state.close();
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	public static void addPizza(Pizza p) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Add the code needed to insert the pizza into the database.
		 * Keep in mind adding pizza discounts and toppings associated with the pizza,
		 * there are other methods below that may help with that process.
		 *
		 */
		String curPizza = "{call new_pizza((select max(Order_ID) from user_order), ?, ?, ?, ?)}";
		try (CallableStatement state = conn.prepareCall(curPizza)) {
			state.setString(1, p.getSize());
			state.setString(2, p.getCrustType());
			state.setDouble(3, p.getCustPrice());
			state.setDouble(4, p.getBusPrice());
			state.executeQuery();
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}


	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this method will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		connect_to_db();
		/*
		 * This method should do 2 two things.
		 * - update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * - connect the topping to the pizza
		 *   What that means will be specific to your yimplementatinon.
		 *
		 * Ideally, you shouldn't let toppings go negative....but this should be dealt with BEFORE calling this method.
		 *
		 */
		int topID = t.getTopID();
		int topExtra = 0;
		Double numTops = 0.0;
		String pizzaSize = p.getSize();
		switch (pizzaSize) {
			case size_s:
				numTops = t.getPerAMT();
				break;
			case size_m:
				numTops = t.getMedAMT();
				break;
			case size_l:
				numTops = t.getLgAMT();
				break;
			case size_xl:
				numTops = t.getXLAMT();
		}
		if (isDoubled) {
			topExtra = 1;
			numTops = numTops * 2;
		}
		String updateTopping = "update topping set Topping_Cur_Lvl " + "= Topping_Cur_Lvl -"
				+ numTops + " where Topping_ID =" + topID;
		try (PreparedStatement state = conn.prepareStatement(updateTopping)) {
			state.executeUpdate();
			// ps.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}

		String updatePT = "insert into pizza_topping (Pizza_ID, Topping_ID, Topping_Extra)"
				+ "values(?,?,?)";
		try (PreparedStatement state = conn.prepareStatement(updatePT)) {
			state.setInt(1, p.getPizzaID());
			state.setInt(2, t.getTopID());
			state.setInt(3, topExtra);
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}


	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * This method connects a discount with a Pizza in the database.
		 *
		 * What that means will be specific to your implementatinon.
		 */
		try {
			String insert = "insert into pizza_discount (Pizza_ID, Disc_ID) " +
					"values ((select max(Pizza_ID) from pizza), " +
					"(select Disc_ID from discount where Disc_Code = ?));";

			PreparedStatement state = conn.prepareStatement(insert);

			state.setString(1, d.getDiscountName());

			state.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * This method connects a discount with a Pizza in the database.
		 *
		 * What that means will be specific to your implementation.
		 */
		try {
			String insert = "insert into order_discount (Order_ID, Disc_ID) " +
					"values ((select max(Order_ID) from user_order), " +
					"(select Disc_ID from discount where Disc_Code = ?));";

			PreparedStatement state = conn.prepareStatement(insert);

			state.setString(1, d.getDiscountName());

			state.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		/*
		 * This method adds a new customer to the database.
		 *
		 */
		String curCust = "insert into customer(Customer_FName, Customer_LName, Customer_Address, Customer_Phone)" + "values" + "(?,?,?,?)";
		try (PreparedStatement ps = conn.prepareStatement(curCust)) {
			ps.setString(1, c.getFName());
			ps.setString(2, c.getLName());
			ps.setString(3, c.getAddress());
			ps.setString(4, c.getPhone());
			ps.executeUpdate();
			// ps.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	public static void completeOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Find the specified order in the database and mark that order as complete in the database.
		 *
		 */
		try {
			String update = "update user_order set Order_Status = 1 where Order_ID = ?;";

			PreparedStatement state = conn.prepareStatement(update);

			state.setInt(1, o.getOrderID());

			state.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	public static ArrayList<Order> getOrders(boolean openOnly) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Return an arraylist of all the orders.
		 * 	openOnly == true => only return a list of open (ie orders that have not been marked as completed)
		 *           == false => return a list of all the orders in the database
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 *
		 * Don't forget to order the data coming from the database appropriately.
		 *
		 */
		ArrayList<Order> list = new ArrayList<Order>();
		ResultSet result = null;
		if(openOnly) {
			String allOrders = "select Order_ID, Order_Type, Order_Status, Order_Price, Order_Cost, Order_Time,"
					+ " Customer_ID from user_order where Order_Status = 0;";
			try (PreparedStatement state = conn.prepareStatement(allOrders)) {
				result = state.executeQuery();
				while (result.next()) {
					int orderID = result.getInt("Order_ID");
					String orderType = result.getString("Order_Type");
					int orderStatus = result.getInt("Order_Status");
					Double orderPrice = result.getDouble("Order_Price");
					Double orderCost = result.getDouble("Order_Cost");
					String orderTime = result.getString("Order_Time");
					int custID = result.getInt("Customer_ID");
					Order order = new Order(orderID, custID, orderType, orderTime, orderPrice, orderCost, orderStatus);
					list.add(order);
				}
			}
		} else {
			String allOrders = "select Order_ID, Order_Type, Order_Status, Order_Price, Order_Cost, Order_Time,"
					+ " Customer_ID from user_order;";
			try (PreparedStatement state = conn.prepareStatement(allOrders)) {
				result = state.executeQuery();
				while (result.next()) {
					int orderID = result.getInt("Order_ID");
					String orderType = result.getString("Order_Type");
					int orderStatus = result.getInt("Order_Status");
					Double orderPrice = result.getDouble("Order_Price");
					Double orderCost = result.getDouble("Order_Cost");
					String orderTime = result.getString("Order_Time");
					int custID = result.getInt("Customer_ID");
					Order order = new Order(orderID, custID, orderType, orderTime, orderPrice, orderCost, orderStatus);
					list.add(order);
				}
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		for (Order order : list) {
			System.out.println(order.toString());
		}
		return list;
	}

	public static Order getLastOrder() throws SQLException, IOException{
		connect_to_db();
		/*
		 * Query the database for the LAST order added
		 * then return an Order object for that order.
		 * NOTE...there should ALWAYS be a "last order"!
		 */
		try {
			String getOrder = "select Order_ID, Order_Type, Order_Status, Order_Price, Order_Cost, Order_Time,"
					+ " Customer_ID from user_order order by Order_ID desc limit 1;";

			PreparedStatement state = conn.prepareStatement(getOrder);

			ResultSet result = state.executeQuery();

			if (result.next()) {
				int orderID = result.getInt("Order_ID");
				String orderType = result.getString("Order_Type");
				int orderStatus = result.getInt("Order_Status");
				Double orderPrice = result.getDouble("Order_Price");
				Double orderCost = result.getDouble("Order_Cost");
				String orderTime = result.getString("Order_Time");
				int custID = result.getInt("Customer_ID");
				Order lastOrder = new Order(orderID, custID, orderType, orderTime, orderPrice, orderCost, orderStatus);
				conn.close();
				return lastOrder;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		conn.close();
		return null;
	}

	public static ArrayList<Order> getOrdersByDate(String date) throws SQLException, IOException{
		connect_to_db();
		/*
		 * Query the database for ALL the orders placed on a specific date
		 * and return a list of those orders.
		 *
		 */
		ArrayList<Order> list = new ArrayList<Order>();
		ResultSet result = null;
		String allOrders = "select Order_ID, Order_Type, Order_Status, Order_Price, Order_Cost, Order_Time,"
				+ " Customer_ID from user_order where Order_Time = ?;";
		try (PreparedStatement state = conn.prepareStatement(allOrders)) {
			state.setString(1, date);
			result = state.executeQuery();

			while (result.next()) {
				int orderID = result.getInt("Order_ID");
				String orderType = result.getString("Order_Type");
				int orderStatus = result.getInt("Order_Status");
				Double orderPrice = result.getDouble("Order_Price");
				Double orderCost = result.getDouble("Order_Cost");
				String orderTime = result.getString("Order_Time");
				int custID = result.getInt("Customer_ID");
				Order order = new Order(orderID, custID, orderType, orderTime, orderPrice, orderCost, orderStatus);
				list.add(order);
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		for (Order order : list) {
			System.out.println(order.toString());
		}
		return list;
	}

	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the database for all the available discounts and
		 * return them in an arrayList of discounts.
		 *
		 */
		ArrayList<Discount> list = new ArrayList<Discount>();
		ResultSet result = null;
		String allDiscounts = "select Disc_ID, Disc_Flat, Disc_Percent, Disc_Code"
				+ " from discount;";
		try (PreparedStatement state = conn.prepareStatement(allDiscounts)) {
			result = state.executeQuery();

			while (result.next()) {
				int discID = result.getInt("Disc_ID");
				Double discFlat = result.getDouble("Disc_Flat");
				int discPercent = result.getInt("Disc_Percent");
				String discCode = result.getString("Disc_Code");
				Discount discount;
				if (discFlat == null) {
					discount = new Discount(discID, discCode, discPercent, true);
				} else {
					discount = new Discount(discID, discCode, discFlat, false);
				}
				list.add(discount);
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return list;
	}

	public static Discount findDiscountByName(String name) throws SQLException, IOException{
		connect_to_db();
		/*
		 * Query the database for a discount using it's name.
		 * If found, then return an OrderDiscount object for the discount.
		 * If it's not found....then return null
		 *
		 */
		try {
			String find = "SELECT Disc_ID, Disc_Flat, Disc_Percent, Disc_Code " +
					"FROM discount WHERE Disc_Code = ?;";

			PreparedStatement state = conn.prepareStatement(find);

			state.setString(1, name);

			ResultSet result = state.executeQuery();

			if (result.next()) {
				int discID = result.getInt("Disc_ID");
				Double discFlat = result.getDouble("Disc_Flat");
				int discPercent = result.getInt("Disc_Percent");
				Discount discount;
				if(discFlat == null) {
					discount = new Discount(discID, name, discPercent, true);
				} else {
					discount = new Discount(discID, name, discFlat, false);
				}
				conn.close();
				return discount;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		conn.close();
		return null;
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the data for all the customers and return an arrayList of all the customers.
		 * Don't forget to order the data coming from the database appropriately.
		 *
		 */
		ArrayList<Customer> list = new ArrayList<Customer>();
		ResultSet result = null;
		String allDiscounts = "select Customer_ID, Customer_FName, Customer_LName, Customer_Address, Customer_Phone"
				+ " from customer group by Customer_LName, Customer_FName, Customer_Phone;";
		try (PreparedStatement state = conn.prepareStatement(allDiscounts)) {
			result = state.executeQuery();

			while (result.next()) {
				int custID = result.getInt("Customer_ID");
				String phone = result.getString("Customer_Phone");
				String firstName = result.getString("Customer_FName");
				String lastName = result.getString("Customer_LName");
				Customer customer = new Customer(custID, firstName, lastName, phone);
				list.add(customer);
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return list;
	}

	public static Customer findCustomerByPhone(String phoneNumber) throws SQLException, IOException{
		connect_to_db();
		/*
		 * Query the database for a customer using a phone number.
		 * If found, then return a Customer object for the customer.
		 * If it's not found....then return null
		 *
		 */
		try {
			String find = "select Customer_ID, Customer_FName, Customer_LName, Customer_Address, Customer_Phone " +
					"from customer where Customer_Phone = ?;";

			PreparedStatement state = conn.prepareStatement(find);

			// Set parameters in the PreparedStatement
			state.setString(1, phoneNumber);

			// Execute the query and get the result set
			ResultSet result = state.executeQuery();

			// Process the result set
			if (result.next()) {
				// Retrieve data from the result set and create a Customer object
				int custID = result.getInt("Customer_ID");
				String firstName = result.getString("Customer_FName");
				String lastName = result.getString("Customer_LName");
				Customer customer = new Customer(custID, firstName, lastName, phoneNumber);
				conn.close();
				return customer;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		conn.close();
		return null;
	}


	public static ArrayList<Topping> getToppingList() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the database for the available toppings and
		 * return an arrayList of all the available toppings.
		 * Don't forget to order the data coming from the database appropriately.
		 *
		 */
		ArrayList<Topping> list = new ArrayList<Topping>();
		ResultSet result = null;
		String allTops = "Select Topping_ID, Topping_Name, Topping_Price, Topping_Cost, Topping_Min_Lvl, Topping_Cur_Lvl,"
				+ " Topping_Small, Topping_Medium, Topping_Large, Topping_XLarge from topping";
		try(PreparedStatement state = conn.prepareStatement(allTops)) {
			result = state.executeQuery();

			while (result.next()) {
				int topID = result.getInt("Topping_ID");
				String topName = result.getString("Topping_Name");
				int topMinInv = result.getInt("Topping_Min_Lvl");
				int topCurInv = result.getInt("Topping_Cur_Lvl");
				int topSmall = result.getInt("Topping_Small");
				int topMedium = result.getInt("Topping_Medium");
				int topLarge = result.getInt("Topping_Large");
				int topXLarge = result.getInt("Topping_XLarge");
				Double topPrice = result.getDouble("Topping_Price");
				Double topCost = result.getDouble("Topping_Cost");
				Topping top = new Topping(topID, topName, topSmall, topMedium,
						topLarge, topXLarge, topPrice, topCost, topMinInv, topCurInv);
				list.add(top);
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		for (Topping top : list) {
			System.out.println(top.toString());
		}
		return list;
	}

	public static Topping findToppingByName(String name) throws SQLException, IOException{
		connect_to_db();
		/*
		 * Query the database for the topping using it's name.
		 * If found, then return a Topping object for the topping.
		 * If it's not found....then return null
		 *
		 */
		try {
			String find = "select Topping_ID, Topping_Name, Topping_Price, Topping_Cost, " +
					"Topping_Min_Lvl, Topping_Cur_Lvl, Topping_Small, Topping_Medium, Topping_Large, Topping_XLarge " +
					"from topping where Topping_Name = ?;";

			PreparedStatement state = conn.prepareStatement(find);

			state.setString(1, name);

			ResultSet result = state.executeQuery();

			if (result.next()) {
				int toppingID = result.getInt("Topping_ID");
				double price = result.getDouble("Topping_Price");
				double cost = result.getDouble("Topping_Cost");
				int minLevel = result.getInt("Topping_Min_Lvl");
				int curLevel = result.getInt("Topping_Cur_Lvl");
				double small = result.getDouble("Topping_Small");
				double medium = result.getDouble("Topping_Medium");
				double large = result.getDouble("Topping_Large");
				double xLarge = result.getDouble("Topping_XLarge");

				Topping topping = new Topping(toppingID, name, small, medium,
						large, xLarge, price, cost, minLevel, curLevel);
				conn.close();
				return topping;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		conn.close();
		return null;
	}


	public static void addToInventory(Topping t, double quantity) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Updates the quantity of the topping in the database by the amount specified.
		 *
		 * */
		int currTop = t.getTopID();
		int amount = (int) quantity;
		String updateTopping = "update topping set Topping_Cur_Lvl " + "= Topping_Cur_Lvl +"
				+ amount + " where Topping_ID =" + currTop;
		try (PreparedStatement state = conn.prepareStatement(updateTopping)) {
			state.executeUpdate();
			// ps.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the database for the base customer price for that size and crust pizza.
		 *
		 */
		double pizzaPrice = 0.0;
		String price = "select Base_Price from base_price where Pizza_Crust = ? and Pizza_Size = ?;";
		try (PreparedStatement state = conn.prepareStatement(price)){
			state.setString(1, crust);
			state.setString(2, size);
			try (ResultSet result = state.executeQuery()) {
				// Check if there are any results
				if (result.next()) {
					// Retrieve the price value from the result set
					pizzaPrice = result.getDouble("Base_Price");
				}
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return pizzaPrice;
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the database fro the base customer price for that size and crust pizza.
		 *
		 */
		double pizzaCost = 0.0;
		String price = "select Base_Cost from base_price where Pizza_Crust = ? and Pizza_Size = ?;";
		try (PreparedStatement state = conn.prepareStatement(price)){
			state.setString(1, crust);
			state.setString(2, size);
			try (ResultSet result = state.executeQuery()) {
				// Check if there are any results
				if (result.next()) {
					// Retrieve the price value from the result set
					pizzaCost = result.getDouble("Base_Cost");
				}
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return pizzaCost;
	}

	public static void printInventory() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Queries the database and prints the current topping list with quantities.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 */
		String print = "select Topping_ID, Topping_Name, Topping_Cur_Lvl from topping order by Topping_Name asc;";

		PreparedStatement preparedStatement = conn.prepareStatement(print);
			 ResultSet resultSet = preparedStatement.executeQuery();
			// Print the column headers
			System.out.printf("%-12s%-20s%-15s%n", "Topping_ID", "Topping_Name", "Topping_Cur_Lvl");

			// Iterate through the result set and print each row
			while (resultSet.next()) {
				System.out.printf("%-12d%-20s%-15d%n",
						resultSet.getInt("Topping_ID"),
						resultSet.getString("Topping_Name"),
						resultSet.getInt("Topping_Cur_Lvl"));
			}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	public static void printToppingPopReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ToppingPopularity view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 * PreparedStatement os;
		ResultSet rset2;
		String query2;
		query2 = "Select Customer_FName, Customer_LName From customer WHERE Customer_ID=?;";
		os = conn.prepareStatement(query2);
		 *
		 */
		try{
			String printView = "select * from ToppingPopularity;";

			PreparedStatement state = conn.prepareStatement(printView);

			ResultSet result = state.executeQuery();

			System.out.printf("%-20s%-15s%n", "Topping", "ToppingCount");

			// Iterate through the result set and print each row
			while (result.next()) {
				System.out.printf("%-20s%-15d%n",
						result.getString("Topping"),
						result.getInt("ToppingCount"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	public static void printProfitByPizzaReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByPizza view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 */
		try {

			String printView = "select * from ProfitByPizza;";

			PreparedStatement state = conn.prepareStatement(printView);

			ResultSet result = state.executeQuery();

			System.out.printf("%-10s%-15s%-10s%n", "Size", "Crust", "Profit");

			while (result.next()) {
				System.out.printf("%-10s%-15s%-10.2f%n",
						result.getString("Size"),
						result.getString("Crust"),
						result.getDouble("Profit"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	public static void printProfitByOrderType() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByOrderType view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 */
		try {

			String printView = "select * from ProfitByOrderType;";

			PreparedStatement state = conn.prepareStatement(printView);

			// Execute the query and get the result set
			ResultSet result = state.executeQuery();

			// Print the column headers
			System.out.printf("%-15s%-15s%-20s%-20s%-10s%n",
					"Customer Type", "Order Month", "Total Order Price", "Total Order Cost", "Profit");

			// Iterate through the result set and print each row
			while (result.next()) {
				System.out.printf("%-15s%-15s%-20.2f%-20.2f%-10.2f%n",
						result.getString("customerType"),
						result.getString("Order Month"),
						result.getDouble("TotalOrderPrice"),
						result.getDouble("TotalOrderCost"),
						result.getDouble("Profit"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}



	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
		/*
		 * This is a helper method to fetch and format the name of a customer
		 * based on a customer ID. This is an example of how to interact with
		 * your database from Java.  It's used in the model solution for this project...so the code works!
		 *
		 * OF COURSE....this code would only work in your application if the table & field names match!
		 *
		 */

		connect_to_db();

		/*
		 * an example query using a constructed string...
		 * remember, this style of query construction could be subject to sql injection attacks!
		 *
		 */

		/* String cname1 = "";
		String query = "Select Customer_FName, Customer_LName From customer WHERE Customer_ID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);

		while(rset.next())
		{
			cname1 = rset.getString(1) + " " + rset.getString(2);
		}*/

		/*
		 * an example of the same query using a prepared statement...
		 *
		 */
		String cname2 = "";
		PreparedStatement os;
		ResultSet rset2;
		String query2;
		query2 = "Select Customer_FName, Customer_LName From customer WHERE Customer_ID=?;";
		os = conn.prepareStatement(query2);
		os.setInt(1, CustID);
		rset2 = os.executeQuery();
		while(rset2.next())
		{
			cname2 = rset2.getString("Customer_FName") + " " + rset2.getString("Customer_LName"); // note the use of field names in the getSting methods
		}

		conn.close();
		return cname2; // OR cname1
	}

	/*
	 * The next 3 private methods help get the individual components of a SQL datetime object.
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		if(getYear(dateOfOrder) > year)
			return true;
		else if(getYear(dateOfOrder) < year)
			return false;
		else
		{
			if(getMonth(dateOfOrder) > month)
				return true;
			else if(getMonth(dateOfOrder) < month)
				return false;
			else
			{
				if(getDay(dateOfOrder) >= day)
					return true;
				else
					return false;
			}
		}
	}
}