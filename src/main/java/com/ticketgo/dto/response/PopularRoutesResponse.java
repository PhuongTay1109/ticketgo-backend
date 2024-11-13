package com.ticketgo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PopularRoutesResponse {
    private String routeImage;
    private String routeName;
    private Long price;
}
