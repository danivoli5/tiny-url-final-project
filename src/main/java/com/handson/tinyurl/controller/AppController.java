package com.handson.tinyurl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handson.tinyurl.model.NewTinyRequest;
import com.handson.tinyurl.model.User;
//import com.handson.tinyurl.model.UserClick;
//import com.handson.tinyurl.model.UserClickOut;
//import com.handson.tinyurl.repository.UserClickRepository;
import com.handson.tinyurl.repository.UserRepository;
import com.handson.tinyurl.service.Redis;
import org.apache.el.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.handson.tinyurl.model.User.UserBuilder.anUser;
//import static com.handson.tinyurl.model.UserClick.UserClickBuilder.anUserClick;
//import static com.handson.tinyurl.model.UserClickKey.UserClickKeyBuilder.anUserClickKey;
import static com.handson.tinyurl.util.Dates.getCurMonth;
import static org.springframework.data.util.StreamUtils.createStreamFromIterator;




@RestController
public class AppController {
    @Autowired
    Redis redis;
    @Autowired
    ObjectMapper om;
    @Value("${base.url}")
    String baseUrl;
    private static final int TINY_LENGTH = 6;
    private static final int MAX_RETRIES = 3;
    Random random = new Random();
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

//    @Autowired
//    private UserClickRepository userClickRepository;

//    @RequestMapping(value = "/user/{name}/clicks", method = RequestMethod.GET)
//    public List<UserClickOut> getUserClicks(@RequestParam String name) {
//        var userClicks = createStreamFromIterator( userClickRepository.findByUserName(name).iterator())
//                .map(userClick -> UserClickOut.of(userClick))
//                .collect(Collectors.toList());
//        return userClicks;
//    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public User createUser(@RequestParam String name) {
        User user = anUser().withName(name).build();
        user = userRepository.insert(user);
        return user;
    }

    @RequestMapping(value = "/user/{name}", method = RequestMethod.GET)
    public User getUser(@RequestParam String name) {
        User user = userRepository.findFirstByName(name);
        return user;
    }

    private void incrementMongoField(String userName, String key){
        Query query = Query.query(Criteria.where("name").is(userName));
        Update update = new Update().inc(key, 1);
        mongoTemplate.updateFirst(query, update, "users");
    }


    @RequestMapping(value = "/tiny", method = RequestMethod.POST)
    public String generate(@RequestBody NewTinyRequest request) throws JsonProcessingException {
        String tinyCode = generateTinyCode();
        int i = 0;
        while (!redis.set(tinyCode, om.writeValueAsString(request)) && i < MAX_RETRIES) {
            tinyCode = generateTinyCode();
            i++;
        }
        if (i == MAX_RETRIES) throw new RuntimeException("SPACE IS FULL");
        return baseUrl + tinyCode + "/";
    }

    @RequestMapping(value = "/{tiny}/", method = RequestMethod.GET)
    public ModelAndView getTiny(@PathVariable String tiny) throws JsonProcessingException {
        Object tinyRequestStr = redis.get(tiny);
        NewTinyRequest tinyRequest = om.readValue(tinyRequestStr.toString(),NewTinyRequest.class);
        if (tinyRequest.getLongUrl() != null) {
            String userName = tinyRequest.getUserName();
            if ( userName != null) {
                // only after the first click on the short link the shorts field inside the user will be updated and then increment the the num of clicks value.
                // if the month or the short not exist(the first click on the short line) mongo will update and add the missing short or month as needed. else(if its exist and not the forst click) it will just increment the value.
                incrementMongoField(userName, "allUrlClicks");
                incrementMongoField(userName,
                        "shorts."  + tiny + ".clicks." + getCurMonth());
                // saving the UserClick in the db - notice that cassandra will manage the saving and save the userclick in the specific UserClickKey user(primary key) cassandra node,
                // this way its easy and fast to fetch the data because it sorted in the user cassandra nodes(its good practice to manage the collection of the single userClicks.
//                userClickRepository.save(anUserClick().userClickKey(anUserClickKey().withUserName(userName).withClickTime(new Date()).build())
//                        .tiny(tiny).longUrl(tinyRequest.getLongUrl()).build());
            }
            return new ModelAndView("redirect:" + tinyRequest.getLongUrl());
        } else {
            throw new RuntimeException(tiny + " not found");
        }
    }

//    @RequestMapping(value = "/tiny", method = RequestMethod.POST)
//    public String generate(@RequestBody NewTinyRequest request) throws JsonProcessingException {
//        String tinyCode = generateTinyCode();
//        int i = 0;
//        while (!redis.set(tinyCode, om.writeValueAsString(request)) && i < MAX_RETRIES) {
//            tinyCode = generateTinyCode();
//            i++;
//        }
//        if (i == MAX_RETRIES) throw new RuntimeException("SPACE IS FULL");
//        return baseUrl + tinyCode + "/";
//    }
//
//    @RequestMapping(value = "/{tiny}/", method = RequestMethod.GET)
//    public ModelAndView getTiny(@PathVariable String tiny) throws JsonProcessingException {
//        System.out.println("getRequest for tiny: " + tiny);
//        Object tinyRequestStr = redis.get(tiny);
//        NewTinyRequest tinyRequest = om.readValue(tinyRequestStr.toString(),NewTinyRequest.class);
//        if (tinyRequest.getLongUrl() != null) {
//            return new ModelAndView("redirect:" + tinyRequest.getLongUrl());
//        } else {
//            throw new RuntimeException(tiny + " not found");
//        }
//    }
    private String generateTinyCode() {
        String charPool = "ABCDEFHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // 62 chars - 6 chars long - 62*62*62*62*62*62 = 56,800,235,584 possible combinations for name space
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < TINY_LENGTH; i++) {
            res.append(charPool.charAt(random.nextInt(charPool.length())));
        }
        return res.toString();
    }
//
//    @RequestMapping(value = "/getkey", method = RequestMethod.GET)
//    public String getKey(@RequestParam String key) {
//        return redis.get(key).toString();
//    }
//
//    @RequestMapping(value = "/setkey", method = RequestMethod.GET)
//    public Boolean setKey(@RequestParam String key, @RequestParam String value) {
//         return redis.set(key,value);
//    }
}
