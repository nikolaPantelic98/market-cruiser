# MarketCruiser (project in progress)

* MarketCruiser is an online eCommerce marketplace.
* Backend using Spring Boot and frontend using Thymeleaf, Bootstrap, and JQuery.
* MySQL.

## Info: What we have done so far

* This is a multi-module Maven project
* The project is divided into two parts: `market-cruiser-back-end` (server side) & `market-cruiser-front-end` (client side). Both exist under the `market-cruiser-web-parent`. `market-cruiser-common` is used for the common code in both directories.

* User Management Module:
  - In the market-cruiser-backend we made User Management Module - a module that manages users.
  - There are 5 roles: Admin, Salesperson, Editor, Assistant, and Shipper.
  - Users listing page created.
  - A user can be created (first name, last name, password, email, photo, role, enabled) and a role can be assigned to him.
  - All CRUD operations for users are enabled.
  - Pagination and sorting for users are implemented.
  - Filter function for user is implemented.
  - Table content of the users can be exported & downloaded in CSV, Excel and PDF.

* User Authentication and Autorization (Spring Security):
  - Spring Security is implemented.
  - The login page has been created. The user can now log in and log out.
  - The user can change his data now (excluding email and roles).
  - From now on, each user can access links only with permission, depending on the role.
  - Custom Error pages added (403, 404, and 500)

* Category Management Module:
  - Category Module added. 
  - Categories listing page created.
  - Categories now can be created, as well as subcategories under the parent category.
  - All CRUD operations for categories are enabled.
  - Pagination and Sorting for categories are implemented.
  - Filter function for categories is implemented.
  - Table content of the categories can be exported & downloaded in CSV.

* Brand Management Module:
  - Brand Module added.
  - Brands listing page created.
  - Brands now can be created and the categories can be assigned to brand.
  - All CRUD operations for brands are enabled.
  - Pagination and Sorting for brands are implemented.
  - Filter function for brands is implemented.

* Product Management Module:
  - Product Module added.
  - Products listing page created.
  - Products now can be created, separated into 5 sections:
    - Overview - basic information about the product,
    - Description - short description and full description with the possibility to format the text,
    - Images - one main image and multiple additional images,
    - Details - arbitrary addition of product details/components,
    - Shipping - all necessary information about shipping.
  - All CRUD operations for products are enabled.
  - Pagination and Sorting for products are implemented.
  - Filter function for products is implemented.
  - Product management depending on the role

* Listing Products to Customers:
  - Home Page
  - Listing categories and products
  - Search function

* Settings Module:
  - Settings Module added, separated into 5 sections:
    - General - management of basic settings (site name, logo, footer, currencies, decimal digits etc.),
    - Countries - setting countries,
    - States - setting states/provinces within the countries,
    - Mail Server - *upcoming*,
    - Mail Template - *upcoming*,
    - Payment - *upcoming*.
  - All CRUD operations for countries and states.
  - Module done with AJAX, without the need to refresh the page.

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
* Modify `application.properties` under the `market-cruiser-back-end` **(market-cruiser-web-parent | market-cruiser-back-end | src | main | resources | application.properties)** if needed - `spring.datasource.username` and `spring.datasource.password` with your username and password from MySQL Workbench.
* Run the application on localhost:8080 - `MarketCruiserBackEnd`.
* Go to **market-cruiser-web-parent | market-cruiser-back-end | src | test | MainTest** and run the method `testCreateFirstUserAdmin()`.
* Run the application on localhost:8080 - `MarketCruiserBackEnd` again and log in (email: admin@gmail.com; password: admin123).

## Some Images

![marketcruiser1](https://user-images.githubusercontent.com/109813536/213311356-f289c9d4-aced-4b95-9a2d-1d5b7c3bdb13.png)
![marketcruiser2](https://user-images.githubusercontent.com/109813536/213311432-5b810dff-041e-4de1-a213-7e8cab577566.png)
![marketcruiser3](https://user-images.githubusercontent.com/109813536/213311445-ae4a8627-88d2-47e6-b638-c5996b850600.png)
![marketcruiser4](https://user-images.githubusercontent.com/109813536/213311450-fa557e20-604b-40ac-9fd1-f36cbe537c69.png)
![marketcruiser5](https://user-images.githubusercontent.com/109813536/213311456-f522fc1f-6047-423c-9fb1-1e2b3f946e40.png)
![marketcruiser6](https://user-images.githubusercontent.com/109813536/213311469-e938c0a6-68d2-41a9-8a22-b774ae7a80d6.png)
