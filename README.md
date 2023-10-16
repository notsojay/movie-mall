# MovieMall Web Application

[👉 Click here to visit the MovieMall website! 👈](http://18.222.89.9:8080/movie-mall/)

[👉 Click here to watch the demo! 👈](https://www.youtube.com/watch?v=OHKB2PyAv14)

## Table of Contents
- [Overview](#overview)
- [Backend Services](#backend-services)
- [Frontend Details](#frontend-details)
- [Setup and Installation](#setup-and-installation)
- [APIs](#apis)
- [Contributors](#contributors)

## Overview
This web application provides movie and star details with a clean and responsive interface. It has been structured to maintain separation between frontend and backend, ensuring modularity and ease of maintenance.

## Backend Services
Backend Servlets provide endpoints to get detailed information about movies and stars. It is built using Java Servlets, utilizes JNDI for database connection pooling, and incorporates best practices such as preprocessing statements to prevent SQL injection.

### Technology Stack:
- Java 11
- Jakarta Servlets
- JSON for data interchange
- JDBC for database connectivity

### Main Components:
1. **Servlets**: Handles HTTP requests and serves data in JSON format.
2. **Database Manager**: Manages database connections and executes SQL queries.
3. **Adapters**: Transforms data between database result sets and Java objects. Additionally, provides utilities for JSON conversion.

#### Servlets:
- [**MovieListServlet**](#movielistservlet):
  - Lists top-rated movies.
  - Path: /MovieListServlet
  - Method: GET
  - Response: A JSON array containing a list of the top 20 movies, ordered by their ratings.
- [**MovieDetailServlet**](#moviedetailservlet):
  - Fetches and provides detailed information about a specific movie.
  - Path: /MovieDetailServlet
  - Method: GET
  - Parameters: query - A Base64 encoded movie ID.
  - Response: A detailed JSON object representing a single movie.
  - Errors: Returns a 500 error with a descriptive message if the movie is not found or there's an invalid URL.
- [**StarDetailServlet**](#stardetailservlet):
  - Fetches and provides detailed information about a particular star.
  - Path: /StarDetailServlet
  - Method: GET
  - Parameters: query - A Base64 encoded star ID.
  - Response: A detailed JSON object representing a single star, including their associated movies.
  - Errors: Returns a 500 error with a descriptive message if the star is not found or there's an invalid URL.

#### Database Operations
- All database operations are centralized in the DatabaseManager class:
  - getJDBCDatabaseConnection: Establishes a connection using JDBC.
  - getJNDIDatabaseConnection: Establishes a connection using JNDI.
  - queryFrom_moviedb: A generic method that executes a SQL query and uses a processor function to transform the result set.
  - getSafeColumnValue: Used to safely retrieve column values from a database result set. It takes a result set (ResultSet), a column name (columnName), and a ResultSetGetter function to handle exceptions. This method attempts to fetch the value of the specified column from the result set and returns null if a SQL exception occurs.

#### Data Adapters
- The application uses adapter classes (MovieAdapter and StarAdapter) to transform database result sets into domain entities and then into JSON. These adapters use a combination of mappings and functional interfaces to ensure the data is transformed in a maintainable and extensible manner.

#### Utility Functions
- Utility classes like URLUtils provide additional functionality such as Base64 encoding and decoding for URL parameters.

#### Exception Handling:
- The ExceptionHandler utility is integrated into the base servlet class. If exceptions occur during the execution of endpoint logic, they are caught and a consistent error response is returned to the client.

## Frontend Details

### Technology Stack:
- React.js
- HTML5
- CSS3
- JavaScript (with AJAX for asynchronous data fetch)
- Bootstrap for responsive design

### Features:
1. **Search Bar**: Users can search for movies or stars.
2. **Listing Page**: Displays a list of top-rated movies with pagination.
3. **Detail Page**: Shows detailed information of a selected movie or star.

### Components:
- **Navbar**: Navigation bar for easy access to all features.

## Setup and Installation
1. Ensure you have Java 11 or higher installed.
2. Clone the repository.
3. Navigate to the backend directory and compile the Java files.
4. Set up your database and adjust the connection details in `DatabaseManager`.
5. Deploy the backend to a servlet container like Tomcat.
6. Navigate to the frontend directory and open `index.html` in a web browser.

## APIs
- [**GET** `/MovieListServlet`](#movielistservlet): List top-rated movies.
- [**GET** `/MovieDetailServlet?query=<movie_id>`](#moviedetailservlet): Fetch detailed information about a specific movie.
- [**GET** `/StarDetailServlet?query=<star_id>`](#stardetailservlet): Fetch detailed information about a specific star.

## Contributors
- Jiahao Liang
  Utilize React to modularize the web servlet, segmenting the application based on specific functionalities. Develop the frontend code to enhance the website's aesthetic appeal.   
  Create the demo video.
  
- Xiaohua Zhang
  Implement the web servlet and draft SQL queries for backend logic. Conduct comprehensive website testing to ensure optimal functionality.
  
  
