package ru.practicum.statsdto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
