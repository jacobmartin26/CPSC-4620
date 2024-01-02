select * from user_order where Order_status = 1;

Select Topping_ID, Topping_Price, Topping_Cost, Topping_Min_Lvl, Topping_Cur_Lvl, Topping_Small, Topping_Medium, Topping_Large, Topping_XLarge from topping;

UPDATE Topping SET Topping_Cur_Lvl = Topping_Cur_Lvl + 5 WHERE Topping_ID = "Bacon";

select Base_Cost from base_price where Pizza_Crust = "Original" and Pizza_Size = "Large";

Select Topping_ID, Topping_Cur_Lvl from topping;

select Topping_ID, Topping_Name, Topping_Cur_Lvl from topping;

update user_order set Order_Status = 2 where Order_ID = 1;

select Order_ID, Order_Type, Order_Status, Order_Price, Order_Cost, Order_Time, Customer_ID from user_order ORDER BY Order_ID desc
LIMIT 1;

select Disc_ID, Disc_Flat, Disc_Percent, Disc_Code from discount;

select Customer_ID, Customer_FName, Customer_LName, Customer_Address, Customer_Phone from customer;

select Disc_ID, Disc_Flat, Disc_Percent, Disc_Code from discount where Disc_Code = 'Employee';

select Customer_ID, Customer_FName, Customer_LName, Customer_Address, Customer_Phone from customer where Customer_Phone = '864-254-5861';

Select Topping_ID, Topping_Name, Topping_Price, Topping_Cost, Topping_Min_Lvl, Topping_Cur_Lvl, Topping_Small, Topping_Medium, Topping_Large, Topping_XLarge from topping where Topping_Name = 'Ham';

select Topping_ID, Topping_Name, Topping_Cur_Lvl from topping order by Topping_Name asc;

select Customer_ID, Customer_FName, Customer_LName, Customer_Address, Customer_Phone from customer group by Customer_LName, Customer_FName, Customer_Phone;