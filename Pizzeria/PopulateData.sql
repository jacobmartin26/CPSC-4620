/* By Jacob Martin & Timmy Lam */

use Pizzeria;

DELIMITER $$
drop procedure if exists `new_pizza` $$
create procedure `new_pizza`(
	in Order_ID int,
	in Size varchar(255),
  	in Crust varchar(255),
    in Price float,
    in Cost float)
begin
		insert into pizza (Pizza_Crust, Pizza_Size, Order_ID, Pizza_Price, Pizza_Cost, Pizza_State)
		values
			(Crust, Size, Order_ID, Price, Cost, 1);
end $$
DELIMITER ;


insert into topping (Topping_Name, Topping_Price, Topping_Cost, Topping_Cur_Lvl, 
					 Topping_Min_Lvl, Topping_Small, Topping_Medium, Topping_Large, Topping_XLarge)
	 values ('Pepperoni', 1.25, 0.2, 100, 50, 2, 2.75, 3.5, 4.5),
       		('Sausage', 1.25, 0.15, 100, 50, 2.5, 3, 3.5, 4.25),
       		('Ham', 1.5,  0.15, 78, 25, 2, 2.5, 3.25, 4),
       		('Chicken', 1.75, 0.25, 56, 25, 1.5, 2, 2.25, 3),
       		('Green Pepper', 0.5, 0.02, 79, 25, 1, 1.5, 2, 2.5),
       		('Onion', 0.5, 0.02, 85, 25, 1, 1.5, 2, 2.75),
       		('Roma Tomato', 0.75, 0.03, 86, 10, 2, 3, 3.5, 4.5),
       		('Mushrooms', 0.75, 0.1, 52, 50, 1.5, 2, 2.5, 3),
       		('Black Olives', 0.6, 0.1, 39, 25, 0.75, 1, 1.5, 2),
       		('Pineapple', 1, 0.25, 15, 0, 1, 1.25, 1.75, 2),
       		('Jalapenos', 0.5, 0.05, 64, 0, 0.5, 0.75, 1.25, 1.75),
       		('Banana Peppers', 0.5, 0.05, 36, 0, 0.6, 1, 1.3, 1.75),
       		('Regular Cheese', 0.5, 0.12, 250, 50, 2, 3.5, 5, 7),
       		('Four Cheese Blend', 1, 0.15, 150, 25, 2, 3.5, 5, 7),
       		('Feta Cheese', 1.5, 0.18, 75, 0, 1.75, 3, 4, 5.5),
       		('Goat Cheese', 1.5, 0.2, 54, 0, 1.6, 2.75, 4, 5.5),
       		('Bacon', 1.5, 0.25, 89, 0, 1, 1.5, 2, 3);
            
            
insert into discount (Disc_Code, Disc_Percent, Disc_Flat)
values
('Employee', 15, null),
('Lunch Special Medium', null, 1),
('Lunch Special Large', null, 2),
('Specialty Pizza', null, 1.5),
('Happy Hour', 10, null),
('Gameday Special', 20, null);  
 
 
insert into base_price (Pizza_Size, Pizza_Crust, Base_Price, Base_Cost)
values
('Small', 'Thin', 3, 0.5),
('Small', 'Original', 3, 0.75),
('Small', 'Pan', 3.5, 1),
('Small', 'Gluten-Free', 4, 2),
('Medium', 'Thin', 5, 1),
('Medium', 'Original', 5, 1.5),
('Medium', 'Pan', 6, 2.25),
('Medium', 'Gluten-Free', 6.25, 3),
('Large', 'Thin', 8, 1.25),
('Large', 'Original', 8, 2),
('Large', 'Pan', 9, 3),
('Large', 'Gluten-Free', 9.5, 4),
('XLarge', 'Thin', 10, 2),
('XLarge', 'Original', 10, 3),
('XLarge', 'Pan', 11.5, 4.5),
('XLarge', 'Gluten-Free', 12.5, 6);


/* order 1 */
insert into user_order (Customer_ID, Order_Type, Order_Time, Order_Price, Order_Cost, Order_Status)
values (null, 'Dine-In', '2023-03-05 12:03:00', 20.75, 3.68, 1);

insert into dine_in (Table_Num, Order_ID) values (21, (select max(Order_ID) from user_order));

call new_pizza((select max(Order_ID) from user_order), 'Large', 'Thin', 20.75, 3.68);

insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Regular Cheese', 1),
	   ((select max(Pizza_ID) from pizza), 'Pepperoni', 0),
       ((select max(Pizza_ID) from pizza), 'Sausage', 0);
       
insert into pizza_discount (Pizza_ID, Disc_ID)
values ((select max(Pizza_ID) from pizza), (select Disc_ID from discount where Disc_Code = 'Lunch Special Large'));

/* order 2 */
insert into user_order (Customer_ID, Order_Type, Order_Time, Order_Price, Order_Cost, Order_Status)
values (null, 'Dine-In', '2023-04-03 12:05:00', 19.78, 4.63, 1);

insert into dine_in (Table_Num, Order_ID) values (4, (select max(Order_ID) from user_order));

call new_pizza((select max(Order_ID) from user_order), 'Medium', 'Pan', 12.85, 3.23);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Feta Cheese', 0),
	   ((select max(Pizza_ID) from pizza), 'Black Olives', 0),
       ((select max(Pizza_ID) from pizza), 'Roma Tomato', 0),
       ((select max(Pizza_ID) from pizza), 'Mushrooms', 0),
       ((select max(Pizza_ID) from pizza), 'Banana Peppers', 0);
       
insert into pizza_discount (Pizza_ID, Disc_ID)
values ((select max(Pizza_ID) from pizza), (select Disc_ID from discount where Disc_Code = 'Lunch Special Medium')),
	   ((select max(Pizza_ID) from pizza), (select Disc_ID from discount where Disc_Code = 'Specialty Pizza'));

call new_pizza((select max(Order_ID) from user_order), 'Small', 'Original', 6.93, 1.40);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Regular Cheese', 0),
	   ((select max(Pizza_ID) from pizza), 'Chicken', 0),
       ((select max(Pizza_ID) from pizza), 'Banana Peppers', 0);
       
/* order 3 */
insert into customer (Customer_FName, Customer_LName, Customer_Address, Customer_Phone)
values ('Andrew', 'Wilkes-Krier', null, '864-254-5861');

insert into user_order (Customer_ID, Order_Type, Order_Time, Order_Price, Order_Cost, Order_Status)
values ((select max(Customer_ID) from customer), 'Pickup', '2023-03-03 9:30:00', 89.28, 19.8, 1);

insert into pick_up(Customer_ID, Order_ID) values ((select max(Customer_ID) from customer), (select max(Order_ID) from user_order));

call new_pizza((select max(Order_ID) from user_order), 'Large', 'Original', 14.88, 3.30);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Regular Cheese', 0),
	   ((select max(Pizza_ID) from pizza), 'Pepperoni', 0);
       
call new_pizza((select max(Order_ID) from user_order), 'Large', 'Original', 14.88, 3.30);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Regular Cheese', 0),
	   ((select max(Pizza_ID) from pizza), 'Pepperoni', 0);
       
call new_pizza((select max(Order_ID) from user_order), 'Large', 'Original', 14.88, 3.30);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Regular Cheese', 0),
	   ((select max(Pizza_ID) from pizza), 'Pepperoni', 0);
       
call new_pizza((select max(Order_ID) from user_order), 'Large', 'Original', 14.88, 3.30);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Regular Cheese', 0),
	   ((select max(Pizza_ID) from pizza), 'Pepperoni', 0);
       
call new_pizza((select max(Order_ID) from user_order), 'Large', 'Original', 14.88, 3.30);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Regular Cheese', 0),
	   ((select max(Pizza_ID) from pizza), 'Pepperoni', 0);
       
call new_pizza((select max(Order_ID) from user_order), 'Large', 'Original', 14.88, 3.30);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Regular Cheese', 0),
	   ((select max(Pizza_ID) from pizza), 'Pepperoni', 0);
       
/* order 4 */
update customer set Customer_Address = '115 Party Blvd, Anderson SC 29621' where Customer_ID = 1;

insert into user_order (Customer_ID, Order_Type, Order_Time, Order_Price, Order_Cost, Order_Status)
values ((select Customer_ID from customer where Customer_Phone = '864-254-5861'), 'Delivery', '2023-04-20 7:11:00', 86.19, 23.62, 1);

insert into delivery (Customer_ID, Order_ID) values (((select Customer_ID from customer where Customer_Phone = '864-254-5861')), (select max(Order_ID) from user_order));

call new_pizza((select max(Order_ID) from user_order), 'XLarge', 'Original', 27.94, 9.19);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Pepperoni', 0),
	   ((select max(Pizza_ID) from pizza), 'Sausage', 0),
       ((select max(Pizza_ID) from pizza), 'Four Cheese Blend', 0);

call new_pizza((select max(Order_ID) from user_order), 'XLarge', 'Original', 31.50, 6.25);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Ham', 1),
	   ((select max(Pizza_ID) from pizza), 'Pineapple', 1),
       ((select max(Pizza_ID) from pizza), 'Four Cheese Blend', 0);
       
insert into pizza_discount (Pizza_ID, Disc_ID)
values ((select max(Pizza_ID) from pizza), (select Disc_ID from discount where Disc_Code = 'Specialty Pizza'));

call new_pizza((select max(Order_ID) from user_order), 'XLarge', 'Original', 26.75, 8.18);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Chicken', 0),
	   ((select max(Pizza_ID) from pizza), 'Bacon', 0),
       ((select max(Pizza_ID) from pizza), 'Four Cheese Blend', 0);
       
insert into order_discount (Order_ID, Disc_ID)
values ((select max(Order_ID) from user_order), (select Disc_ID from discount where Disc_Code = 'Gameday Special'));

/* order 5 */
insert into customer (Customer_FName, Customer_LName, Customer_Address, Customer_Phone)
values ('Matt', 'Engers', null, '864-474-9953');

insert into user_order (Customer_ID, Order_Type, Order_Time, Order_Price, Order_Cost, Order_Status)
values ((select max(Customer_ID) from customer), 'Pickup', '2023-03-02 5:30:00', 27.45, 7.88, 1);

insert into pick_up(Customer_ID, Order_ID) values ((select max(Customer_ID) from customer), (select max(Order_ID) from user_order));

call new_pizza((select max(Order_ID) from user_order), 'XLarge', 'Gluten-Free', 27.45, 7.88);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Green Pepper', 0),
	   ((select max(Pizza_ID) from pizza), 'Onion', 0),
       ((select max(Pizza_ID) from pizza), 'Roma Tomato', 0),
       ((select max(Pizza_ID) from pizza), 'Mushrooms', 0),
       ((select max(Pizza_ID) from pizza), 'Black Olives', 0),
       ((select max(Pizza_ID) from pizza), 'Goat Cheese', 0);
       
insert into pizza_discount (Pizza_ID, Disc_ID)
values ((select max(Pizza_ID) from pizza), (select Disc_ID from discount where Disc_Code = 'Specialty Pizza'));

/* order 6 */
insert into customer (Customer_FName, Customer_LName, Customer_Address, Customer_Phone)
values ('Frank', 'Turner', '6745 Wessex St Anderson SC 29621', '864-232-8944');

insert into user_order (Customer_ID, Order_Type, Order_Time, Order_Price, Order_Cost, Order_Status)
values ((select max(Customer_ID) from customer), 'Delivery', '2023-03-02 6:17:00', 25.81, 4.24, 1);

insert into delivery (Customer_ID, Order_ID) values (((select Customer_ID from customer where Customer_Phone = '864-232-8944')), (select max(Order_ID) from user_order));

call new_pizza((select max(Order_ID) from user_order), 'Large', 'Thin', 25.81, 4.24);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Chicken', 0),
	   ((select max(Pizza_ID) from pizza), 'Green Pepper', 0),
       ((select max(Pizza_ID) from pizza), 'Onion', 0),
       ((select max(Pizza_ID) from pizza), 'Mushrooms', 0),
       ((select max(Pizza_ID) from pizza), 'Four Cheese Blend', 1);
       
/* order 7 */
insert into customer (Customer_FName, Customer_LName, Customer_Address, Customer_Phone)
values ('Milo', 'Auckerman', '8879 Suburban Home, Anderson, SC 29621', '864-878-5679');

insert into user_order (Customer_ID, Order_Type, Order_Time, Order_Price, Order_Cost, Order_Status)
values ((select max(Customer_ID) from customer), 'Delivery', '2023-04-13 8:32:00', 37.25, 6.00, 1);

insert into delivery (Customer_ID, Order_ID) values (((select Customer_ID from customer where Customer_Phone = '864-878-5679')), (select max(Order_ID) from user_order));

call new_pizza((select max(Order_ID) from user_order), 'Large', 'Thin', 18.00, 2.75);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Four Cheese Blend', 1);

call new_pizza((select max(Order_ID) from user_order), 'Large', 'Thin', 19.25, 3.25);
insert into pizza_topping (Pizza_ID, Topping_Name, Topping_Extra)
values ((select max(Pizza_ID) from pizza), 'Regular Cheese', 0),
	   ((select max(Pizza_ID) from pizza), 'Pepperoni', 1);
       
insert into order_discount (Order_ID, Disc_ID)
values ((select max(Order_ID) from user_order), (select Disc_ID from discount where Disc_Code = 'Employee'));