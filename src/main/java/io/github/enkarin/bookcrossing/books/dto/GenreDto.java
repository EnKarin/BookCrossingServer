package io.github.enkarin.bookcrossing.books.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreDto {
    private int id;
    private String ruName;
    private String engName;
}
