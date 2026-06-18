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
        String url = "https://nominatim.openstreetmap.org/search?city="
                + city + "&state=" + state + "&country=Brazil&format=json&limit=1";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Sintropia/1.0 (" + contactEmail + ")");
    
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            String body = restTemplate.exchange(url, HttpMethod.GET, request, String.class).getBody();
            JsonNode json = mapper.readTree(body);
            
            if (json.isEmpty()) {
                throw new RuntimeException("Localização não encontrada: " + city + ", " + state);
            }
            
            Thread.sleep(1500);
            
            return new Coordinates(
                    json.get(0).get("lat").asDouble(),
                    json.get(0).get("lon").asDouble()
            );
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("A requisição foi interrompida", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar as coordenadas", e);
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
