# MarketCruiser (project in progress)

* MarketCruiser is an online eCommerce marketplace.
* Backend using Spring Boot and frontend using Thymeleaf, Bootstrap, and JQuery.
* MySQL.

## Info: What we have done so far

* This is a multi-module Maven project
* The project is divided into two parts: market-cruiser-backend (server side) & market-cruiser-frontend (client side). Both exist under the market-cruiser-web-parent.

* In the market-cruiser-backend we made User Management Module - a module that manages users.
* There are 5 roles: Admin, Salesperson, Editor, Assistant, and Shipper.
* A user can be created (first name, last name, password, email, photo, role, enabled) and a role can be assigned to him.
* All CRUD operations for users are enabled.
* Pagination and sorting for users are implemented.
* Filter function for user is implemented.
* Table content of the users can be exported & downloaded in CSV, Excel and PDF.

* Spring Security is implemented.
* The login page has been created. The user can now log in and log out.
* The user can change his data now (excluding email and roles).
* From now on, each user can access links only with permission, depending on the role.
* Custom Error pages added (403, 404, and 500)

* Category Module added. categories now can be created, as well as subcategories under the parent category.
* All CRUD operations for categories are enabled.
* Pagination and Sorting for categories are implemented.
* Filter function for categories is implemented.
* Table content of the categories can be exported & downloaded in CSV.

## Requirements

* [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/)
* [Java IDE (IntelliJ Ultimate preferred)](https://www.jetbrains.com/idea/download/#section=windows)
* [MySQL Workbench](https://dev.mysql.com/downloads/workbench/)

### Launching a web application step by step

* Download JDK.
* Download IntelliJ IDEA.
* From the main menu, select **File | Project Structure | Project Settings | Project**.
* If the necessary JDK is already defined in IntelliJ IDEA, select it from the **SDK** list.
* If the JDK is installed on your computer, but not defined in the IDE, select **Add SDK | JDK**, and specify the path to the JDK home directory (for example,  **/Library/Java/JavaVirtualMachines/jdk-12.0.1.jdk**).
* Apply the changes and close the dialog.
* Download MySQL Workbench.
* Create new database - `CREATE database market_cruiser_db;`
* Modify `application.properties` under the `market-cruiser-back-end` if needed - `spring.datasource.username` and `spring.datasource.password` with your username and password from MySQL Workbench.
* Run the application on localhost:8080 - `MarketCruiserBackEnd`.
* Go to **market-cruiser-web-parent | market-cruiser-back-end | src | test | MainTest** and run the method `testCreateFirstUserAdmin()`.
* Run the application on localhost:8080 - `MarketCruiserBackEnd` again and log in (email: admin@gmail.com; password: admin123).

## Some Images

![01](https://user-images.githubusercontent.com/109813536/207864751-1d6c7086-52ad-44da-98c5-bfe30c66fe62.png)
![02](https://user-images.githubusercontent.com/109813536/207864788-8b148009-dc7e-4688-ae14-d6e93d96c03c.png)
![03](https://user-images.githubusercontent.com/109813536/207864805-dde73594-808b-46e5-a56a-7ca215d77036.png)
![4](https://user-images.githubusercontent.com/109813536/208247308-6a0e98b5-6bc0-444a-abf5-e5d657190996.png)
![5](https://user-images.githubusercontent.com/109813536/208247313-164f36e9-f827-468a-b934-d4e87850e393.png)
