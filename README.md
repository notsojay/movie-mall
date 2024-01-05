# 🎬 MovieMall Web Application

## General
    
- #### Demo1 Link: [👉 **Click here to watch the demo1**! 👈](https://www.youtube.com/watch?v=QotI8r10k1s&t=2s)
 
- #### Demo2 Link: [👉 **Click here to watch the demo2**! 👈](https://www.youtube.com/watch?v=ztUwsBEetuE&t=5s)
    
- #### Stress testing Link: [👉 **Click here to watch the Stress testing**! 👈](https://www.youtube.com/watch?v=R0JvKTR3c38)
      

## 📌 Table of Contents
- [📖 Overview](#overview)
- [⚙️ Backend Services](#backend-services)
- [🎨 Frontend Details](#frontend-details)
- [🔧 Setup and Installation](#setup-and-installation)
- [🌐 APIs](#apis)
- [🏊‍♂️ Connection Pooling](#Connection-Pooling)
- [Master/Slave](#Master/Slave)
- [JMeter TS/TJ Time Measurement Report](JMeter-TS/TJ-Time-Measurement-Report)
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
4. **XML Parsings**: Parsing multiple xml and inserting them into the database according to rules.


### 🗂️ Performance Optimization Strategies
This section details the optimization strategies implemented in our XML parsing project. A key highlight is the use of multi-threading, which is intricately designed to enhance processing speed and efficiency.

#### 1. Multi-Threading Implementation
- **Thread Pool and Executors**: We utilize `Executors.newFixedThreadPool(3)` to create a pool of threads, which allows for parallel processing of multiple XML files. This approach significantly reduces the time required for parsing large XML files as compared to a sequential approach.
- **Concurrent HashMaps**: `ConcurrentHashMap` is used for `sharedStarMap` and `sharedMovieMap`, ensuring thread-safe operations while allowing concurrent reads and updates. This is crucial in a multi-threaded environment to prevent data corruption.
- **CountDownLatch Mechanism**: The `CountDownLatch` is used to synchronize the completion of tasks. For instance, after parsing movies and stars in separate threads, a latch ensures both are completed before initiating the cast parsing. This ensures data consistency and integrity.

#### 2. Efficient Data Structures
- **Custom Data Structures**: Depending on the specific access patterns observed during parsing, such as more frequent reads than writes, custom data structures with read-write locks can be used to improve performance.
- **Data Access Optimization**: By analyzing how data is accessed and modified in the maps, we can reduce contention and improve throughput. For instance, using temporary local structures to accumulate data before a single batch update to the shared map can minimize lock contention.

#### 3. Batch Processing for Database Operations
- **Batch Database Operations**: Instead of individual inserts or updates for each record, we implement batch operations. This approach reduces the number of network calls and database I/O operations, significantly decreasing the total execution time.
- **Bulk Insertions**: Where possible, we leverage bulk insertion techniques provided by the database to handle large data sets more efficiently. This reduces the overhead associated with individual row insertions.


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


## 🏊‍♂️ Connection Pooling
#### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
  - /var/lib/tomcat10/conf/server.xml - Contains DataSource configuration:
          
    ````
          <Resource name="jdbc/moviedbMaster"
                    auth="Container"
                    type="javax.sql.DataSource"
                    factory="org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory"
                    driverClassName="com.mysql.cj.jdbc.Driver"
                url="jdbc:mysql://172.31.26.229:3306/moviedb?cachePrepStmts=true&amp;prepStmtCacheSize=250&amp;prepStmtCacheSqlLimit=2048"
                    username="repl"
                    password="slave66Pass$word"
                    maxTotal="100"
                    maxIdle="30"
                    maxWaitMillis="10000"/>
        
          <Resource name="jdbc/moviedbSlave"
                    auth="Container"
                    type="javax.sql.DataSource"
                    factory="org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory"
                    driverClassName="com.mysql.cj.jdbc.Driver"
                url="jdbc:mysql://172.31.38.149:3306/moviedb?cachePrepStmts=true&amp;prepStmtCacheSize=250&amp;prepStmtCacheSqlLimit=2048"
                    username="readonly_user"
                    password="readonly_password"
                    maxTotal="100"
                    maxIdle="30"
                    maxWaitMillis="10000"/>
        ````

#### Explain how Connection Pooling is utilized in the Fabflix code.
    - /var/lib/tomcat10/conf/context.xml - Defines the connection pool settings.

        ````
        <ResourceLink name="jdbc/moviedbMaster"
                      global="jdbc/moviedbMaster"
                      type="javax.sql.DataSource"/>
        
        <ResourceLink name="jdbc/moviedbSlave"
                      global="jdbc/moviedbSlave"
                      type="javax.sql.DataSource"/>
        ````


#### Explain how Connection Pooling works with two backend SQL.
    - The application uses two SQL backends, set up as master and slave. The connection pooling is configured to interact with both the master and the slave databases. This setup enhances performance and provides fault tolerance. Read operations are routed to the slave database, while write operations go to the master database.
    

## Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
        - /etc/apache2/sites-enabled/000-default.conf - Apache configuration for load balancing：
          
            ````
            Header add Set-Cookie "ROUTEID=.%{BALANCER_WORKER_ROUTE}e; path=/" env=BALANCER_ROUTE_CHANGED
            
            <VirtualHost *:80>
                ServerName movie-mall.com
            
                #SSLEngine on
                #SSLCertificateFile "/var/lib/tomcat10/0000_cert.pem"
                #SSLCertificateKeyFile "/var/lib/tomcat10/privatekey.pem"
                #SSLCertificateChainFile "/var/lib/tomcat10/0001_chain.pem"
            
                <Proxy "balancer://Session_balancer">
                    BalancerMember "http://172.31.26.229:8080/server" route=j1
                    BalancerMember "http://172.31.38.149:8080/server" route=j2
                    ProxySet stickysession=ROUTEID
                </Proxy>
            
                <Proxy "balancer://mycluster">
                    BalancerMember "http://172.31.26.229:8080" route=r1
                    BalancerMember "http://172.31.38.149:8080" route=r2
                    ProxySet stickysession=ROUTEID
                </Proxy>
            
                ProxyRequests Off
                ProxyPreserveHost On
                ProxyVia Full
                <Proxy *>
                    Require all granted
                </Proxy>
            
                ProxyPass /server balancer://Session_balancer
                ProxyPassReverse /server balancer://Session_balancer
                ProxyPass / balancer://mycluster/
                ProxyPassReverse / balancer://mycluster/
            
                ErrorLog ${APACHE_LOG_DIR}/error.log
                CustomLog ${APACHE_LOG_DIR}/access.log combined
            </VirtualHost>
            ````

        - [moviemall-server/src/main/java/com/db/DatabaseManager.java](moviemall-server/src/main/java/com/db/DatabaseManager.java) - Contains logic for routing queries.
      
    - #### How read/write requests were routed to Master/Slave SQL?
        - Read requests are routed to the slave SQL through the Apache load balancer configured with sticky sessions.
        - This is achieved by the ProxyPass directives in Apache's configuration, which direct traffic to the appropriate backend based on the route identifier.
        - Write requests are directed to the master SQL server.
        - In the application code, the getJNDIDatabaseConnection method checks if the operation is a read or write and accordingly returns a connection to either the master or the slave database.  
    

## JMeter TS/TJ Time Logs
    - The `log_processing.py` script is designed to process JMeter logs to calculate average Servlet Time (TS) and JDBC Time (TJ). Follow these steps to use the script:

    - ### Prerequisites
        - Ensure Python is installed on your system.
        - The script requires a log file generated by JMeter as input.

    - ### Steps to Run the Script
        1. **Locate the Log File**: Identify the JMeter log file that you want to process. This file should contain entries with Servlet and JDBC times.
        2. **Run the Script**: Open your terminal or command prompt, navigate to the directory containing the `log_processing.py` script, and run the following command:

        ```bash
        python log_processing.py path_to_your_log_file.log
        ```

        3. Replace `path_to_your_log_file.log` with the actual path to your JMeter log file.

        4. **View the Results**: After running the script, it will output the average Servlet Time (TS) and JDBC Time (TJ) in nanoseconds. Look for lines that say "Average Servlet Time (TS): ..." and "Average JDBC Time (TJ): ..." in the output.

    - ### Example
        - If your JMeter log file is located at `/path/to/jmeter/logs/test_results.log`, run the script like this:
          
            ```bash
            python log_processing.py /path/to/jmeter/logs/test_results.log
      

## JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 24.7                       | 29.12                          | 26.8              | Best performance, single thread, no competition, quick response.           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | 37.9                         | 40.45                                  | 39.77                        | More threads, slower due to competition and context switching.           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | 85.7                         | 91.01                                  | 88.67                        | HTTPS slower than HTTP due to encryption overhead.           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | 83.1                         | 86.83                                  | 84.32                        | Significantly slower, no connection pooling means each query establishes new database connection.           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 49.8                         | 51.57                                  | 50.04                        | Slower due to load balancing and network latency impacts           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | 33.9                         | 34.61                                  | 34.26                        | Slightly better due to requests distributed across multiple servers.           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | 75.5                         | 78.2                                  | 76.5                        |  Slower than with pooling; load balancing helps but each query needs new DB connection.           |


## 🤝 Contributors
- **Jiahao Liang**：
    - Utilize React to modularize the web servlet, segmenting the application based on specific functionalities.
    - Develop the frontend code to enhance the website's aesthetic appeal.
    - Create the demo video.
- **Xiaohua Zhang**：
  - Implement the web servlet and draft SQL queries for backend logic.
  - Conduct comprehensive website testing to ensure optimal functionality and the satisfaction of project requirement.
