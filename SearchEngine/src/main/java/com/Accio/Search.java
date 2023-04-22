package com.Accio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// we are providing path as it is in action case so that we can perform action based on that
@WebServlet("/Search")
public class Search extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // getting keyword from frontend
        String keyword = request.getParameter("keyword");
        // setting up connection to database
        Connection connection= DatabaseConnection.getConnection();

        try {
            //for getting history we are setting
            PreparedStatement preparedStatement = connection.prepareStatement("Insert into history values(?, ?);");
            preparedStatement.setString(1,keyword);
            preparedStatement.setString(2,"http://localhost:8080/SearchEngine/Search?keyword="+keyword);
            preparedStatement.executeUpdate();

            // Getting results after running ranking query
            ResultSet resultSet = connection.createStatement().executeQuery("select pageTitle, pageLink, (length(lower(pageText)) - length(replace(lower(pageText), '" + keyword.toLowerCase() + "', ' ')))/length('" + keyword.toLowerCase() + "') as countoccurence from pages order by countoccurence desc limit 30;");
            ArrayList<SearchResult> results = new ArrayList<SearchResult>();

            // while result .next not null then iterate over result arraylist
            // transferring values from resultset to results arraylist
            while (resultSet.next()) {
                SearchResult searchResult = new SearchResult();
                searchResult.setTitle(resultSet.getString("pageTitle"));
                searchResult.setLink(resultSet.getString("pageLink"));
                results.add(searchResult);
            }

        // displaying results arraylist in console
            for(SearchResult result:results) {
                System.out.println(result.getTitle()+"\n"+result.getLink()+"\n");
            }

            request.setAttribute("results", results);
            request.getRequestDispatcher("search.jsp").forward(request, response);

            // after making request we got some response from server
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
        }
        catch(SQLException | ServletException sqlException) {
            sqlException.printStackTrace();
        }
        // here printing response in the html text manner
       // out.println("<h3>This is the keyword you have entered "+keyword+"</h3>");
    }
}
