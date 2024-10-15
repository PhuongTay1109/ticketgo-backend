package com.ticketgo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@Getter
class Data {
    private int height;
    @JsonProperty("is_silhouette")
    private boolean isSilhouette;
    private String url;
    private int width;
}

@Getter
class Picture {
    private Data data;
}

@Getter
@ToString
public class FacebookUserInfoResponse {
    private String id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String email;
    private Picture picture;

    public String getPictureUrl() {
        return picture.getData().getUrl();
    }
}
