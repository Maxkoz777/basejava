package com.urise.webapp.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import static com.urise.webapp.util.DateUtil.*;

public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Link homePage;
    private List<Position> positions;


    public Organization(String name, String url, Position... positions) {
        this(new Link(name, url), Arrays.asList(positions));
    }

    public Organization(Link homePage, List<Position> positions) {
        this.homePage = homePage;
        this.positions = positions;
    }

    public static class Position implements Serializable{

        private static final long serialVersionUID = 1L;

        private LocalDate startDate;
        private LocalDate endDate;
        private String title;
        private String description;

        public Position() {
        }

        public Position(int startYear, Month startMonth, String title, String description){
            this(of(startYear, startMonth), NOW, title, description);
        }

        public Position(int startYear, Month startMonth, int endYear, Month endMonth, String title, String description){
            this(of(startYear, startMonth), of(endYear, endMonth), title, description);
        }

        public Position(LocalDate startDate, LocalDate endDate, String title, String description) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.title = title;
            this.description = description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Position position = (Position) o;

            if (!Objects.equals(startDate, position.startDate)) return false;
            if (!Objects.equals(endDate, position.endDate)) return false;
            if (!Objects.equals(title, position.title)) return false;
            return Objects.equals(description, position.description);
        }

        @Override
        public int hashCode() {
            int result = startDate != null ? startDate.hashCode() : 0;
            result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
            result = 31 * result + (title != null ? title.hashCode() : 0);
            result = 31 * result + (description != null ? description.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "startDate=" + startDate +
                    ", endDate=" + endDate +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}
