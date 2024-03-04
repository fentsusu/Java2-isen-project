package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDaoTestCase {
	
	private MovieDao movieDao = new MovieDao();
	
	@Before
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS movie (\r\n"
				+ "  idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third movie')");
		stmt.close();
		connection.close();
	}
	
	 @Test
	 public void shouldListMovies() {		 
		 	// WHEN
			List<Movie> movies = movieDao.listMovies();
			
			// THEN
	        assertNotNull("List of movies should not be null", movies);
	        assertFalse("List of movies should not be empty", movies.isEmpty());
	        
			assertThat(movies).hasSize(3);
			assertThat(movies).extracting("id", "title").containsOnly(tuple(1, "Title 1"), tuple(2, "My Title 2"), tuple(3, "Third title"));
			
			// Assert that all movies have attributes
	        for (Movie movie : movies) {
	            assertNotNull("Movie ID should not be null", movie.getId());
	            assertNotNull("Movie title should not be null", movie.getTitle());
	            assertNotNull("Movie release date should not be null", movie.getReleaseDate());
	            assertNotNull("Movie genre should not be null", movie.getGenre());
	            assertNotNull("Movie duration should not be null", movie.getDuration());
	            assertNotNull("Movie director should not be null", movie.getDirector());
	            assertNotNull("Movie summary should not be null", movie.getSummary());
	        }
	 }
	
	 @Test
	 public void shouldListMoviesByGenre() {	
			//WHEN
			String genreName = "Drama";

			List<Movie> movies = movieDao.listMoviesByGenre(genreName);
			
			//THEN
			assertTrue("List of movies should not be null", movies != null);
	        assertTrue("List of movies should not be empty", !movies.isEmpty());
	        
	        // Assert that all movies belong to the specified genre
	         for (Movie movie : movies) {
	             assertEquals("Movie genre should be " + genreName, genreName, movie.getGenre().getName());
	         }
	 }

	
	 @Test
	 public void shouldAddMovie() throws Exception {

		 	// WHEN 
			Movie movie = new Movie();
			movie.setTitle("4th Title");
			
			// Parse the release_date string into LocalDateTime
		    LocalDateTime releaseDateTime = LocalDateTime.parse("2019-12-12 12:00:00.000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		    movie.setReleaseDate(releaseDateTime);
			//movie.setReleaseDate("2019-12-12 12:00:00.000");
		    
			movie.setDuration(130);
			movie.setGenre(new Genre(1, "Drama"));
			movie.setDirector("director 4");
			movie.setSummary("summary of the forth movie");
			Movie addedMovie = movieDao.addMovie(movie);

			// THEN
			Connection connection = DataSourceFactory.getDataSource().getConnection();
			String sqlQuery = "SELECT * FROM movie WHERE idmovie=?";
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setInt(1, addedMovie.getId());
			ResultSet result = statement.executeQuery();
			assertThat(result.next()).isTrue();
			assertThat(result.getInt("idmovie")).isNotNull();
			assertThat(result.getInt("idmovie")).isEqualTo(movie.getId());
			assertThat(result.getString("title")).isEqualTo("4th Title");
			assertThat(result.next()).isFalse();
			result.close();
			statement.close();
			connection.close();
	 }
}
