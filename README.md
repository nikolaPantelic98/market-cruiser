# MarketCruiser (project in progress)

* MarketCruiser is an online eCommerce marketplace.
* Backend using Spring Boot and frontend using Thymeleaf, JQuery and AJAX.
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

* Customer Registration:
  - Customer Registration Form created.
  - E-Mail settings added to Settings module:
    - Mail Server,
    - Mail Template.
  - Verification mail for customer registration implemented and the customer must be verified now.

* Customer Management Module:
  - Customer Module added.
  - Customers listing page created.
  - All CRUD operations for customers are enabled.
  - Pagination and Sorting for customers are implemented.

* Customer Authentication:
  - A login page for customers has been created, as well as a logout option.
  - Two types of authentication have been created for customers: DATABASE and GOOGLE.
  - Mandatory verification of the user who registered in the application was created.
  - The ability of the customer to log in to the application via gmail has been created.
  - A Forgot Password option was created, through which the customer can reset the password via email.

* Shopping Cart Module:
  - Shopping Cart Module added.
  - The customer can add the product to the cart.
  - The customer can determine how many of the same products will be in the cart (quantity).
  - The cart shows the total price of all products.

* Shipping Rates Management Module:
  - Shipping Rates Management Module added.
  - Shipping rates listing page is created.
  - Shipping rates can now be created to determine the cost of postage to a specific country.
  - All CRUD operations for shipping rates are enabled.
  - Pagination and Sorting for shipping rates are implemented.

* Address Book Module:
  - Address Book Module added.
  - Customer can add several different addresses and specify to which address he wants to buy the product.
  - Customer can specify which address is default.
  - All CRUD operations are enabled, so customer can create, read, update or delete his addresses.

* Order Management Module (1/2):
  - Order Management Module added.
  - Order listing page is created.
  - Admin can see the entire customer order and change its status.
  - The option to delete orders has been added.
  - Pagination and sorting for orders have been implemented, starting from the latest date to the oldest.
  
* Checkout Module:
  - Checkout Module added.
  - Checkout page created.
  - COD (Cash on Delivery) option added, so customer can purchase the product that way.
  - Order Confirmation EMail coded.
  - PayPal API added, so the customer can finish the purchase via PayPal.
  
* Order Management Module (2/2):
  - Order Tracking Information added.
  - Edit Order Form updated (Overview, Shipping, Products, Track Records).
  - Order Management for Shipper Role added (some actions are read-only).
  - Order Management for Customer added.
  - Order listing page for customers added.
  - The Customer can see details for every order.
  - Return order function is implemented, so the customer can submit the request to return the order.
  

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

![marketcruiser1](https://user-images.githubusercontent.com/109813536/235348277-aa3a3396-b230-48af-a5a1-3a166e1770d1.png)
![marketcruiser2](https://user-images.githubusercontent.com/109813536/235348283-572f66c4-b94e-49d0-86f3-48dc6f766bed.png)
![marketcruiser3](https://user-images.githubusercontent.com/109813536/235348287-dd2a67cf-746e-40bd-a29c-079663f1ea91.png)
![marketcruiser4](https://user-images.githubusercontent.com/109813536/235348293-991540cd-146b-414a-a69a-aac029b74055.png)
![marketcruiser5](https://user-images.githubusercontent.com/109813536/235348297-a15b9003-6f6b-4fbd-a313-06abb480fd29.png)
![marketcruiser6](https://user-images.githubusercontent.com/109813536/235348299-67f79aae-9e26-4381-8f3a-a8d62909a20d.png)
![marketcruiser7](https://user-images.githubusercontent.com/109813536/235348302-7a749466-e16a-4123-8e93-609adb8f308b.png)
![marketcruiser8](https://user-images.githubusercontent.com/109813536/235348306-1102dc5a-6c1a-4a6e-b359-e5632a460d74.png)

