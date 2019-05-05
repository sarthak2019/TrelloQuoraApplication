package com.upgrad.quora.api.Controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserAuthBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {


    @Autowired
    private QuestionBusinessService questionBusinessService;

    @Autowired
    private UserAuthBusinessService userAuthBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequest) throws AuthorizationFailedException {

        final UserAuthEntity userAuthEntity = userAuthBusinessService.getUser(authorization);


        final ZonedDateTime now = ZonedDateTime.now();
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setUser(userAuthEntity.getUser());
        questionEntity.setDate(now);

        final QuestionEntity createdQuestion = questionBusinessService.createQuestion(questionEntity, userAuthEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> GetAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException{

        final UserAuthEntity userAuthEntity = userAuthBusinessService.getUser(authorization);
        final List<QuestionEntity> allQuestion = questionBusinessService.getAllQuestion(userAuthEntity);

        List<QuestionDetailsResponse> questionResponse = questionslist(allQuestion);

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionResponse, HttpStatus.OK);


    }

    public List<QuestionDetailsResponse> questionslist(List<QuestionEntity> allQuestion){
        List<QuestionDetailsResponse> listofquestions = new ArrayList<>();
        for ( QuestionEntity questionEntity : allQuestion){
            QuestionDetailsResponse Response = new QuestionDetailsResponse();
            Response.id(questionEntity.getUuid());
            Response.content(questionEntity.getContent());
            listofquestions.add(Response);
        }
        return listofquestions;
    }
}