package com.controller;

import com.api.AdapterApi;
import com.entity.GismeteoResponse;
import com.entity.MsgA;
import com.entity.MsgB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@RestController
public class AdapterApiController implements AdapterApi {
    private final RestTemplate restTemplate;
    private final String gismeteoApiUrl;

    public AdapterApiController(RestTemplate restTemplate, @Value("${gismeteo.api.url}") String gismeteoApiUrl) {
        this.restTemplate = restTemplate;
        this.gismeteoApiUrl = gismeteoApiUrl;
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public CompletableFuture<ResponseEntity<MsgB>> execute(MsgA body) {
        if (body == null || body.getLng() == null || body.getCoordinates() == null) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }

        if (!body.getLng().equals(MsgA.LngEnum.RU)) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        }

        if (isMessageEmpty(body)) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }

        URI uri = UriComponentsBuilder
                .fromHttpUrl(gismeteoApiUrl)
                .queryParam("latitude", body.getCoordinates().getLatitude())
                .queryParam("longitude", body.getCoordinates().getLongitude())
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
        return CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<GismeteoResponse> responseEntity = restTemplate.exchange(requestEntity, GismeteoResponse.class);

                if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                    GismeteoResponse gismeteoResponse = responseEntity.getBody();
                    MsgB response = new MsgB();
                    response.setTxt(body.getMsg());
                    response.setCurrentTemp(String.valueOf(gismeteoResponse.getTemperature()));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    private boolean isMessageEmpty(MsgA body) {
        return body.getMsg() == null || body.getMsg().trim().isEmpty();
    }
}
