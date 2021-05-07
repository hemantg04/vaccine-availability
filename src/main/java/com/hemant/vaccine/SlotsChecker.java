package com.hemant.vaccine;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@RestController
public class SlotsChecker {
    private static final String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/calendarByDistrict";
    @Autowired
    RestTemplate restTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SlotsChecker.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @GetMapping("/slots")
    public ResponseEntity<List<Slot>> getSlots(@RequestParam(required = false, defaultValue = "365") String district,
                                               @RequestParam(required = false, defaultValue = "") String vaccine,
                                               @RequestParam(required = false, defaultValue = "0") Integer age) {
        List<Slot> availableSlots = new ArrayList<>();
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        try {
            String responseString = restTemplate.getForObject(url + "?district_id=" + district + "&date=" + date, String.class);
            JSONObject jsonObject = new JSONObject(responseString);
            JSONArray centers = jsonObject.getJSONArray("centers");
            for (int i = 0; i < centers.length(); i++) {
                JSONObject center = centers.getJSONObject(i);
                JSONArray sessions = center.getJSONArray("sessions");
                for (int j = 0; j < sessions.length(); j++) {
                    JSONObject session = sessions.getJSONObject(j);
                    int minAge = session.getInt("min_age_limit");
                    int availableCapacity = session.getInt("available_capacity");
                    String vaccineName = session.getString("vaccine");

                    if ((vaccine.isEmpty() || vaccine.equalsIgnoreCase(vaccineName))
                            && (age == 0 || minAge == age)
                            && availableCapacity > 0) {
                        availableSlots.add(new Slot(session.getString("date"), minAge, vaccineName,
                                center.getString("name"), center.getString("block_name"),
                                center.getInt("pincode"), availableCapacity));
                    }
                }
            }
        } catch (Exception err) {
            System.out.println(err.getMessage());
            err.printStackTrace();
        }
        System.out.println("Total available slots: " + availableSlots.size());
        return ResponseEntity.ok(availableSlots);
    }

}
