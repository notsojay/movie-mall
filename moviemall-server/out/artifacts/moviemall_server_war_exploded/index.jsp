<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Test Index Page</title>
</head>
<body>
<h1>Welcome to the Test Page</h1>

<p>
    Click the link below to test the MovieListServlet:
</p>

<p>Test MovieListServlet:</p>
<a href="MovieListServlet?requestType=get-top20-movies">Top 20 movies</a>
<br>
<a href="MovieListServlet?requestType=browse-movies_by-initial&initial=G">Browse movies by initial</a>
<br>
<a href="MovieListServlet?requestType=browse-movies-by-genre&genre=Action">Browse movies by genre</a>
<br>
<a href="MovieListServlet?requestType=get-all-genres">Get all genres</a>

</body>
</html>
