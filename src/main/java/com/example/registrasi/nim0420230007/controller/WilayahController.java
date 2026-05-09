package com.example.registrasi.nim0420230007.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Controller
@RequestMapping("/api/wilayah")
public class WilayahController {

  private static final String BASE_URL = "https://emsifa.github.io/api-wilayah-indonesia/api";

  // followRedirects ALWAYS supaya ikuti redirect 301 dari GitHub
  private final HttpClient httpClient = HttpClient.newBuilder()
      .followRedirects(HttpClient.Redirect.ALWAYS)
      .build();

  // GET /api/wilayah/provinsi
  @GetMapping(value = "/provinsi", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String getProvinsi() throws IOException, InterruptedException {

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + "/provinces.json"))
        .header("Accept", "application/json")
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(
        request, HttpResponse.BodyHandlers.ofString());

    return response.body();
  }

  // GET /api/wilayah/kota/{provinsiId}
  @GetMapping(value = "/kota/{provinsiId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String getKota(@PathVariable String provinsiId) throws IOException, InterruptedException {

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + "/regencies/" + provinsiId + ".json"))
        .header("Accept", "application/json")
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(
        request, HttpResponse.BodyHandlers.ofString());

    return response.body();
  }
}