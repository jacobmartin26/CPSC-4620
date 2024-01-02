These two files represent my work on a made up Pizzeria. The initial Pizzeria file is the purely SQL implementation of the organization.
It is fairly simple although it does make use of stored procedures to create new pizzas faster. All orders were given to the database without
any custom input from a user. However, in the Java Pizzeria file, I implemented the original SQL queries into a Java file using JDBC libraries.
In this file, the user is given the freedom to create any type of pizza and order of their liking. I made use of AWS' RDS service to store all 
of the user's orders and details pertaining to the user such as their name, address, etc... Also, prepared statements and calls are used to protect against any
potential SQL injection attacks. While it would be unlikely for such a small project to be attacked, it is still good practice to have protections
in place.
