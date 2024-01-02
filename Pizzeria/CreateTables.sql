/* By Jacob Martin & Timmy Lam */

create database if not exists Pizzeria;
use Pizzeria;

create table if not exists customer(
Customer_ID int auto_increment primary key,
Customer_FName varchar(255) not null,
Customer_LName varchar(255) not null,
Customer_Address varchar(255),
Customer_Phone varchar(255) not null);

create table if not exists user_order(
Order_ID int auto_increment primary key,
Order_Type varchar(255) not null,
Order_Status int not null,
Order_Price float not null,
Order_Cost float not null,
Order_Time datetime not null,
Customer_ID int,
foreign key (Customer_ID) references customer(Customer_ID));

create table if not exists base_price(
Pizza_Crust varchar(255),
Pizza_Size varchar(255),
primary key(Pizza_Crust, Pizza_Size),
Base_Price float not null,
Base_Cost float not null);

create table if not exists pizza(
Pizza_ID int auto_increment primary key,
Pizza_Crust varchar(255) not null,
Pizza_Size varchar(255) not null,
Pizza_Price float not null,
Pizza_Cost float not null,
Pizza_State bool not null,
Order_ID int not null,
foreign key (Order_ID) references user_order(Order_ID),
foreign key (Pizza_Crust, Pizza_Size) references base_price(Pizza_Crust, Pizza_Size));

create table if not exists topping(
Topping_ID int auto_increment primary key,
Topping_Name varchar(255) not null,
Topping_Price float not null,
Topping_Cost float not null,
Topping_Small float not null,
Topping_Medium float not null,
Topping_Large float not null,
Topping_XLarge float not null,
Topping_Min_Lvl int not null,
Topping_Cur_Lvl int not null,
INDEX idx_Topping_Name (Topping_Name));

create table if not exists pizza_topping(
Pizza_ID int,
Topping_Name varchar(255),
Topping_Extra bool not null,
primary key (Pizza_ID, Topping_Name),
foreign key (Pizza_ID) references pizza(Pizza_ID),
foreign key (Topping_Name) references topping(Topping_Name));

create table if not exists discount(
Disc_ID int auto_increment primary key,
Disc_Flat float,
Disc_Percent float,
Disc_Code varchar(255));

create table if not exists order_discount(
Disc_ID int,
Order_ID int,
primary key (Disc_ID, Order_ID),
foreign key (Disc_ID) references discount(Disc_ID),
foreign key (Order_ID) references user_order(Order_ID));

create table if not exists pizza_discount(
Disc_ID int,
Pizza_ID int,
primary key (Disc_ID, Pizza_ID),
foreign key (Disc_ID) references discount(Disc_ID),
foreign key (Pizza_ID) references pizza(Pizza_ID));

create table if not exists dine_in(
Table_Num int auto_increment,
Order_ID int,
primary key (Table_Num, Order_ID),
foreign key (Order_ID) references user_order(Order_ID));

create table if not exists pick_up(
Customer_ID int,
Order_ID int,
primary key (Customer_ID, Order_ID),
foreign key (Customer_ID) references customer(Customer_ID),
foreign key (Order_ID) references user_order(Order_ID));

create table if not exists delivery(
Customer_ID int,
Order_ID int,
primary key (Customer_ID, Order_ID),
foreign key (Customer_ID) references customer(Customer_ID),
foreign key (Order_ID) references user_order(Order_ID));