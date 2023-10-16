# 🎬 MovieMall Web Application

[🎥 **Click here to visit the MovieMall website**! 🎥](http://18.222.89.9:8080/movie-mall/)

[👉 **Click here to watch the demo**! 👈](https://www.youtube.com/watch?v=OHKB2PyAv14)

## 📌 Table of Contents
- [📖 Overview](#overview)
- [⚙️ Backend Services](#backend-services)
- [🎨 Frontend Details](#frontend-details)
- [🔧 Setup and Installation](#setup-and-installation)
- [🌐 APIs](#apis)
- [🤝 Contributors](#contributors)

## 📖 Overview
MovieMall is a meticulously crafted web application designed to offer movie and star details with an intuitive and responsive interface. We've taken special care to ensure a clear separation between frontend and backend components, guaranteeing modularity and ease of future updates.

## ⚙️ Backend Services
Our robust backend leverages Servlets to provide endpoints for extracting comprehensive information about movies and stars. 

### 🛠️ Technology Stack:
- **Java**: 19.0.2
- **Apache Maven**: 3.8.7
- **Apache Tomcat**: 10.1.13
- **Jakarta Servlet**: 6.0.0
- **JSON**: For data interchange
- **JDBC**: For seamless database connectivity

### 📚 Main Components:
1. **Servlets**: Expertly manage HTTP requests and deliver data in a crisp JSON format.
2. **Database Manager**: Orchestrates database connections and spearheads SQL query executions.
3. **Adapters**: Act as the bridge, transforming data from database result sets to Java objects and facilitating JSON conversions.

### 🔗 Servlet Endpoints:
- [**MovieListServlet**](#movielistservlet): Lists the crème de la crème of movies.
  - **Path**: /MovieListServlet
  - **Method**: GET
  - **Response**: A curated JSON array of top 20 movies, ranked by ratings.
- [**MovieDetailServlet**](#moviedetailservlet): Delve deeper into the intricacies of a particular movie.
  - **Path**: /MovieDetailServlet
  - **Method**: GET
  - **Parameters**: query - A Base64 encoded movie ID.
  - **Response**: A detailed JSON depiction of a movie.
  - **Error Handling**: Gracefully handles situations where a movie isn't found or the URL encounters issues.
- [**StarDetailServlet**](#stardetailservlet): Unveil details of a shining star.
  - **Path**: /StarDetailServlet
  - **Method**: GET
  - **Parameters**: query - A Base64 encoded star ID.
  - **Response**: A rich JSON portrayal of a star and their cinematic journey.

### 📖 Detailed Backend Features:
- **Database Operations**: All interactions with the database are managed via the DatabaseManager class. 
- **Data Adapters**: These are pivotal in molding database results into domain-specific entities and then crafting them into JSON.
- **Utility Functions**: Classes like `URLUtils` augment functionality, especially in URL parameter handling.
- **Exception Handling**: Our built-in ExceptionHandler guarantees consistent error responses, ensuring transparency with the client.

## 🎨 Frontend Details

### 🛠️ Technology Stack:
- **React.js**: 18.2.0
- **HTML**: 5
- **CSS**: 3
- **JavaScript**: Enhanced with AJAX for asynchronous data retrieval
- **Node.js**: 20.5.0
- **npm**: 9.8.0
- **Bootstrap**: For a fluid, responsive design

### ✨ Features:
1. **Search Bar**: Dive into a world of movies and stars.
2. **Listing Page**: A showcase of top-tier movies, complete with pagination.
3. **Detail Page**: A closer look at your favorite movie or star.

### 🧩 Components:
- **Navbar**: A handy navigation tool for a seamless browsing experience.

## 🔧 Setup and Installation
1. Ensure that Java 19 or higher is installed on your system.
2. Clone the repository.
3. Navigate to the moviemall-server directory and use the following command, Apache Maven will download the dependencies required by the project.
   ```bash
   mvn clean install
4. After that, use the following command to package it into a WAR file.
   ```bash
   mvn package
4. Adjust your database settings.
5. In the moviemall-client directory, use npm to build the React project. This will generate a static directory.
   ```bash
   npm run build
6. Move the static content generated from the React build into the appropriate location within the WAR directory structure.
7. Deploy the combined WAR file to a servlet container, like Tomcat.
8. Access the application using your preferred web browser via the server's address.


## 🌐 APIs
- [**GET** `/MovieListServlet`](#movielistservlet): Spotlight on top-rated movies.
- [**GET** `/MovieDetailServlet?query=<movie_id>`](#moviedetailservlet): A cinematic deep dive into a specific movie.
- [**GET** `/StarDetailServlet?query=<star_id>`](#stardetailservlet): Illuminate the life of a star.

## 🤝 Contributors
- **Jiahao Liang**：
    - Utilize React to modularize the web servlet, segmenting the application based on specific functionalities.
    - Develop the frontend code to enhance the website's aesthetic appeal.
    - Create the demo video.
- **Xiaohua Zhang**：
  - Implement the web servlet and draft SQL queries for backend logic.
  - Conduct comprehensive website testing to ensure optimal functionality and the satisfaction of project requirement.
