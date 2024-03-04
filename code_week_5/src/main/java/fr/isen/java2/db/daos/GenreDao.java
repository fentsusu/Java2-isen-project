package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import fr.isen.java2.db.entities.Genre;

public class GenreDao {
	public List<Genre> listGenres() {
		List<Genre> listGenres = new ArrayList<>();
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()){
			try(Statement statement = connection.createStatement()){
				try(ResultSet results = statement.executeQuery("SELECT * FROM genre")){
					while(results.next()) {
						Genre genre = new Genre(results.getInt("idgenre"),
												results.getString("name"));
						listGenres.add(genre);
					}
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return listGenres;
		//throw new RuntimeException("Method is not yet implemented");
	}

	public Genre getGenre(String name) {
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()){
			try(PreparedStatement statement = connection.prepareStatement(
					"SELECT * FROM genre WHERE name = ?")){
				statement.setString(1, name);
				try(ResultSet results = statement.executeQuery()){
					if(results.next()) {
						return new Genre(results.getInt("idgenre"),
										results.getString("name"));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
		//throw new RuntimeException("Method is not yet implemented");
	}

	public Genre addGenre(String name) {
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()){
			String sqlQuery = "INSERT INTO genre(name) VALUES(?)";
			try(PreparedStatement statement = connection.prepareStatement(
					sqlQuery, Statement.RETURN_GENERATED_KEYS)){
				statement.setString(1, name);
				statement.executeUpdate();
				ResultSet ids = statement.getGeneratedKeys();
				if(ids.next()) {
					return new Genre(ids.getInt(1),name);
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Genre getGenreById(int id) {
	    try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
	        try (PreparedStatement statement = connection.prepareStatement(
	        		"SELECT * FROM genre WHERE idgenre = ?")) {
	            statement.setInt(1, id);
	            try (ResultSet resultSet = statement.executeQuery()) {
	                if (resultSet.next()) {
	                	return new Genre(resultSet.getInt("idgenre"), 
	                					resultSet.getString("name"));
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

}
