package com.ticketgo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PopularRoutesResponse {
    private String routeImage;
    private String routeName;
    private Long price;
}
