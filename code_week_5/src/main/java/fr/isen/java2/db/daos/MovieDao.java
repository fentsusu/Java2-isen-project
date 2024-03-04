package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDao {

	public List<Movie> listMovies() {
		//Create list to contain movies
		List<Movie> listOfMovies = new ArrayList<>();
		GenreDao genreDao = new GenreDao();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		try(Connection connection = DataSourceFactory.getDataSource().getConnection()){
			try(Statement statement = connection.createStatement()){
				try(ResultSet results = statement.executeQuery("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre")){
					while(results.next()) {
						
						//Create Object genre from GenreDao and get id
						Genre genre = genreDao.getGenreById(results.getInt("genre_id"));

						// Parse the release_date string into LocalDateTime
                        //LocalDateTime releaseDateTime = results.getTimestamp("release_date").toLocalDateTime();
                        String releaseDateString = results.getString("release_date");
                        java.util.Date releaseDate = dateFormat.parse(releaseDateString);

						//Create Object movie form Movie
	                    Movie movie = new Movie(results.getInt("idmovie"),
												results.getString("title"),
												releaseDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
												//releaseDateTime,
												genre,
												results.getInt("duration"),
												results.getString("director"),
												results.getString("summary"));
						//Add Object movie to the list
	                    listOfMovies.add(movie);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return listOfMovies;
	}

	public List<Movie> listMoviesByGenre(String genreName) {
		
		//Create list to contain movies
		List<Movie> listOfMovies = new ArrayList<>();
		GenreDao genreDao = new GenreDao();
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			String sqlQuery = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name=?";
			try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
				statement.setString(1, genreName);
				try (ResultSet results = statement.executeQuery()) {
					while (results.next()) {
						//Create Object genre from GenreDao and get id
						Genre genre = genreDao.getGenreById(results.getInt("genre_id"));
						
						// Parse the release_date string into LocalDateTime
                        LocalDateTime releaseDateTime = results.getTimestamp("release_date").toLocalDateTime();

						//Create Object movie form Movie
						Movie movie = new Movie(
								results.getInt("idmovie"),
								results.getString("title"),
								releaseDateTime,
								genre,
								results.getInt("duration"),
								results.getString("director"),
								results.getString("summary"));
						//Add Object movie to the list
						listOfMovies.add(movie);
					}
				}
			}
		} catch (SQLException e) {
			return null;
		}
		return listOfMovies;

	}

	public Movie addMovie(Movie movie) {
		Movie newMovie = new Movie();
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			String sqlQuery = "INSERT INTO movie(title,release_date,genre_id,duration,director,summary) VALUES(?,?,?,?,?,?)";
			try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
				
				//Set values of each constructor
				statement.setString(1, movie.getTitle());
				statement.setObject(2, movie.getReleaseDate());
				statement.setInt(3, movie.getGenre().getId());
				statement.setInt(4, movie.getDuration());
				statement.setString(5, movie.getDirector());
				statement.setString(6, movie.getSummary());

				statement.executeUpdate();

				try (ResultSet ids = statement.getGeneratedKeys()) {
					if (ids.next()) {
						int generatedId = ids.getInt(1);
						movie.setId(generatedId);
						newMovie.setId(generatedId);
						newMovie.setTitle(movie.getTitle());
						newMovie.setReleaseDate(movie.getReleaseDate());
						newMovie.setGenre(movie.getGenre());
						newMovie.setDirector(movie.getDirector());
						newMovie.setDuration(movie.getDuration());
						newMovie.setSummary(movie.getSummary());
					} else {
						throw new SQLException("Failed to Create Movie.");
					}
				}
			}
		} catch (SQLException e) {
			newMovie = null;
		}
		return newMovie;
	}

}
