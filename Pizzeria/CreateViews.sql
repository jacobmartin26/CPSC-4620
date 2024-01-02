/* By Jacob Martin & Timmy Lam */

use Pizzeria;

drop view if exists ToppingPopularity;
create view ToppingPopularity as
select t.Topping_Name as Topping, 
       coalesce(count(pt.Topping_Name), 0) + coalesce(sum(pt.Topping_Extra), 0) as ToppingCount
from topping t
left join pizza_topping pt on t.Topping_Name = pt.Topping_Name
group by t.Topping_Name
order by ToppingCount DESC, t.Topping_Name ASC;
select * from ToppingPopularity;


drop view if exists ProfitByPizza;
create view ProfitByPizza as
select pizza_profit.Pizza_Size as Size, pizza_profit.Pizza_Crust as Crust,
	   cast(sum_profit as decimal(4, 2)) as Profit
from (select Pizza_Size,
			 Pizza_Crust,
			 round(sum(Pizza_Price - Pizza_Cost), 2) as sum_profit
	  from pizza
	  group by Pizza_Size, Pizza_Crust
	 ) as pizza_profit
group by Pizza_Size, Pizza_Crust
order by sum_profit desc;
select * from ProfitByPizza;


drop view if exists ProfitByOrderType;
create view ProfitByOrderType as
select Order_Type as customerType, date_format(Order_Time, '%m/%Y') as `Order Month`,
								   round(sum(Order_Price), 2) as TotalOrderPrice,
								   round(sum(Order_Cost), 2) as TotalOrderCost,
								   round(sum(Order_Price - Order_Cost), 2) as Profit
from user_order
group by `Order Month`, Order_Type
union
select null, 'Grand Total', round(sum(Order_Price), 2) as TotalOrderPrice,
							round(sum(Order_Cost), 2) as TotalOrderCost,
							round(sum(Order_Price - Order_Cost),2) as Profit
from user_order;
select * from ProfitByOrderType;