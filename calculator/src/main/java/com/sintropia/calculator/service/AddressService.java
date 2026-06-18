package com.sintropia.calculator.service;


import java.util.Arrays;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sintropia.calculator.model.Coordinates;
import com.sintropia.calculator.model.EdenredAddress;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class AddressService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper(); 
    
    @Value("${openrouteservice.api.key}")
    private String apiKey;
    
    @Value("${contact.email}")
    private String contactEmail;
    
    public Coordinates getCoordinates(String city, String state) {
        try {
            String encodedCity = java.net.URLEncoder.encode(city, java.nio.charset.StandardCharsets.UTF_8);
            String encodedState = java.net.URLEncoder.encode(state, java.nio.charset.StandardCharsets.UTF_8);

            String url = "https://api.openrouteservice.org/geocode/search/structured?locality=" + encodedCity + "&region=" + encodedState + "&country=BR&size=1";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            String body = restTemplate.exchange(url, HttpMethod.GET, request, String.class).getBody();
            JsonNode json = mapper.readTree(body);
            JsonNode features = json.get("features");
            
            if (features != null && !features.isEmpty()) {
                JsonNode coords = features.get(0).get("geometry").get("coordinates");
                return new Coordinates(coords.get(1).asDouble(), coords.get(0).asDouble());
            }

            String openMeteoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + encodedCity + "&count=1&language=pt";
            String meteoBody = restTemplate.getForObject(openMeteoUrl, String.class);
            JsonNode meteoJson = mapper.readTree(meteoBody);
            
            if (meteoJson.has("results") && !meteoJson.get("results").isEmpty()) {
                double lat = meteoJson.get("results").get(0).get("latitude").asDouble();
                double lon = meteoJson.get("results").get(0).get("longitude").asDouble();
                return new Coordinates(lat, lon);
            }
            
                        String fallbackUrl = "https://api.openrouteservice.org/geocode/search?text=" + encodedCity + "&boundary.country=BR&size=1";
            body = restTemplate.exchange(fallbackUrl, HttpMethod.GET, request, String.class).getBody();
            json = mapper.readTree(body);
            features = json.get("features");

            if (features != null && !features.isEmpty()) {
                JsonNode coords = features.get(0).get("geometry").get("coordinates");
                return new Coordinates(coords.get(1).asDouble(), coords.get(0).asDouble());
            }

            throw new RuntimeException("Localização não encontrada em nenhuma API: " + city + ", " + state);

        } catch (Exception e) {
            throw new RuntimeException("Erro na API de coordenadas para " + city + ": " + e.getMessage(), e);
        }
    }
    
    public double getDistanceHaversine(Coordinates origin, Coordinates destination) {
        final int EARTH_RADIUS_KM = 6371;
        
        double deltaLatitude = Math.toRadians(destination.latitude() - origin.latitude());
        double deltaLongitude = Math.toRadians(destination.longitude() - origin.longitude());
        
        double angularDistance = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2)
                 + Math.cos(Math.toRadians(origin.latitude())) * Math.cos(Math.toRadians(destination.latitude()))
                 * Math.sin(deltaLongitude / 2) * Math.sin(deltaLongitude / 2);
        
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(angularDistance), Math.sqrt(1 - angularDistance));        
    }
    
    public double getDistanceHaversine(Coordinates origin, EdenredAddress edenred) {
        return getDistanceHaversine(origin, edenred.getCoordinates());
    }
    
    public double getRealDistanceKm(Coordinates origin, Coordinates destination) throws Exception {
        String url = "https://api.openrouteservice.org/v2/directions/driving-car";
        
        String body = """
                {
                    "coordinates": [
                        [%s, %s],
                        [%s, %s]
                    ]
                }
                """.formatted(
                    origin.longitude(), origin.latitude(),
                    destination.longitude(), destination.latitude()
                );
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();

        JsonNode json = mapper.readTree(response);
        
        return json.get("routes").get(0).get("summary").get("distance").asDouble() / 1000;
    }
    
    public double getRealDistanceKm(Coordinates origin, EdenredAddress edenred) throws Exception {
        return getRealDistanceKm(origin, edenred.getCoordinates());
    }
	
	public EdenredAddress getClosestFactory(Coordinates coordinates){
		return Arrays.stream(EdenredAddress.values())
				.min(Comparator.comparingDouble(
						factory -> getDistanceHaversine(coordinates, factory)
				)).orElseThrow(() -> new RuntimeException("Nenhuma fabrica encontrada"));
	}
	
	public double getClosestFactoryDistance(Coordinates coordinates) throws Exception {
			EdenredAddress closest = getClosestFactory(coordinates);

			return getRealDistanceKm(coordinates, closest);
	}
}
