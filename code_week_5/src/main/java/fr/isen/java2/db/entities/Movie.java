package fr.isen.java2.db.entities;

import java.time.LocalDateTime;

public class Movie {

	private Integer id;
	private String title;
	
	//private LocalDate releaseDate;
	private LocalDateTime releaseDate; // Changed to LocalDateTime
	
	private Genre genre;
	private Integer duration;
	private String director;
	private String summary;

	public Movie() {
	}

	public Movie(Integer id, String title, LocalDateTime releaseDate, Genre genre, Integer duration, String director,
			String summary) {
		super();
		this.id = id;
		this.title = title;
		this.releaseDate = releaseDate;
		this.genre = genre;
		this.duration = duration;
		this.director = director;
		this.summary = summary;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDateTime getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDateTime releaseDate) {
		this.releaseDate = releaseDate;
	}

	public Genre getGenre() {
		return genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

}
