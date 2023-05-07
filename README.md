# MarketCruiser

MarketCruiser is an online eCommerce shop built with a combination of Java, Spring, Spring Boot, Spring Security, Thymeleaf, jQuery, AJAX, and MySQL.
* [www.marketcruiser.com](https://marketcruiser.herokuapp.com)

## Table of Contents

* [Introduction](https://github.com/nikolaPantelic98/market-cruiser#introduction)
* [Features](https://github.com/nikolaPantelic98/market-cruiser#features)
* [Requirements](https://github.com/nikolaPantelic98/market-cruiser#requirements)
* [Installation](https://github.com/nikolaPantelic98/market-cruiser#installation)
* [Contributing](https://github.com/nikolaPantelic98/market-cruiser#contributing)
* [Some Images](https://github.com/nikolaPantelic98/market-cruiser#some-images)

## Introduction

MarketCruiser is a multi-module Maven project that consists of the following modules: market-cruiser-back-end (server-side), market-cruiser-front-end (client-side), and market-cruiser-common (common code). The project provides an eCommerce platform with features such as user management, category management, brand management, product management, customer management, order management, and more.

## Features

* User Management:
  - User roles: Admin, Salesperson, Editor, Assistant, and Shipper.
  - CRUD operations for users, pagination, sorting, and filtering.
  - Exporting user data to CSV, Excel, and PDF formats.

* User Authentication and Authorization:
  - Implemented with Spring Security.
  - Login and logout functionality.
  - User data update (excluding email and roles).
  - Role-based access control.

* Category Management:
  - CRUD operations for categories, including subcategories. 
  - Pagination, sorting, and filtering.
  - Exporting category data to CSV.

* Brand Management:
  - CRUD operations for brands, with category assignment.
  - Pagination, sorting, and filtering.

* Product Management:
  - CRUD operations for products.
  - Product sections: Overview, Description, Images, Details, and Shipping.
  - Pagination, sorting, and filtering.

* Settings Module:
  - General settings for site management.
  - Country and state/province management.
  - Mail server and mail template settings.
  - Payment settings.

* Customer Registration:
  - Registration form for customers.
  - Email verification for customer registration:

* Customer Management:
  - CRUD operations for customers.
  - Pagination, sorting, and filtering.

* Customer Authentication:
  - Customer login and logout.
  - Authentication types: DATABASE and GOOGLE.
  - Email verification for registered customers.
  - Login via Gmail.
  - Password reset via email.

* Shopping Cart:
  - Adding products to the cart.
  - Quantity selection and total price calculation.
  
* Shipping Rates Management:
  - CRUD operations for shipping rates.
  - Pagination, sorting, and filtering.

* Address Book:
  - Managing customer addresses.
  - Default address selection.
  - CRUD operations for addresses.

* Order Management:
  - Admin view of customer orders and order status management.
  - Order deletion.
  - Pagination and sorting.
  - Order tracking information.
  - Customer order management and returns.
  - Order listing for customers.
  
* Checkout:
  - Added a Checkout module to facilitate the purchasing process.
  - Created a dedicated checkout page.
  - Implemented the option for Cash on Delivery (COD), allowing customers to make purchases and pay upon delivery.
  - Developed functionality for sending order confirmation emails to customers.
  - Integrated PayPal API to enable customers to complete purchases using PayPal as a payment method.
  
* Order Management Module (2/2):
  - Order Tracking Information added.
  - Edit Order Form updated (Overview, Shipping, Products, Track Records).
  - Order Management for Shipper Role added (some actions are read-only).
  - Order Management for Customer added.
  - Order listing page for customers added.
  - The Customer can see details for every order.
  - Return order function is implemented, so the customer can submit the request to return the order.
  
* All Images and Files migrated to AWS Amazon S3.

* Deployment:
  - Server-side and client-side applications deployed on Heroku.
  

## Requirements

* [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/)
* [Java IDE (IntelliJ Ultimate preferred)](https://www.jetbrains.com/idea/download/#section=windows)
* [MySQL Workbench](https://dev.mysql.com/downloads/workbench/)
* Apache Maven

Ensure you have these dependencies installed and properly configured before proceeding with the installation steps.

## Installation

To run the web application locally, follow these steps:

1. Ensure you have the following dependencies installed on your system:
  - Java Development Kit (JDK)
  - MySQL Workbench
  - Apache Maven
2. Clone the repository to your local machine by running the following command: `git clone https://github.com/nikolaPantelic98/market-cruiser`
3. Open a command line or terminal window and navigate to the project directory: `cd market-cruiser`
4. Create a new database named `market_cruiser_db` in MySQL Workbench.
5. Modify the `application.properties` file located at `market-cruiser-back-end/src/main/resources/application.properties`, if needed, to update the database connection properties. Update the values for `spring.datasource.username` and `spring.datasource.password` with your MySQL Workbench username and password.
6. Build the application by running the following command: `./mvnw clean install`
7. Launch the application locally by running the following command: `./mvnw spring-boot:run -pl market-cruiser-back-end`
8. Once the application is running, access it by opening a web browser and navigating to `http://localhost:8080`.
9. Initialize the first admin user by executing the `testCreateFirstUserAdmin()` method located in the `MainTest` class. To do this, open a new command line or terminal window, navigate to the project directory, and run the following command: `./mvnw test -pl market-cruiser-back-end`
10. You can now log in as the admin user with the following credentials:
 - Email: admin@gmail.com
 - Password: admin123

## Contributing

We welcome contributions to improve the MarketCruiser application. To contribute, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make the necessary changes in your branch.
4. Test your changes thoroughly.
5. Commit your changes and push them to your forked repository.
6. Submit a pull request, providing a clear description of your changes.

Our team will review the pull request and work with you to merge the changes into the main repository if they meet the project's guidelines and standards.

## Some Images

![marketcruiser1](https://user-images.githubusercontent.com/109813536/235348277-aa3a3396-b230-48af-a5a1-3a166e1770d1.png)
![marketcruiser2](https://user-images.githubusercontent.com/109813536/235348283-572f66c4-b94e-49d0-86f3-48dc6f766bed.png)
![marketcruiser3](https://user-images.githubusercontent.com/109813536/235348287-dd2a67cf-746e-40bd-a29c-079663f1ea91.png)
![marketcruiser4](https://user-images.githubusercontent.com/109813536/235348293-991540cd-146b-414a-a69a-aac029b74055.png)
![marketcruiser5](https://user-images.githubusercontent.com/109813536/235348297-a15b9003-6f6b-4fbd-a313-06abb480fd29.png)
![marketcruiser6](https://user-images.githubusercontent.com/109813536/235348299-67f79aae-9e26-4381-8f3a-a8d62909a20d.png)
![marketcruiser7](https://user-images.githubusercontent.com/109813536/235348302-7a749466-e16a-4123-8e93-609adb8f308b.png)
![marketcruiser8](https://user-images.githubusercontent.com/109813536/235348306-1102dc5a-6c1a-4a6e-b359-e5632a460d74.png)

