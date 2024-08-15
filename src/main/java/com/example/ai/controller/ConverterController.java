package com.example.ai.controller;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ConverterController {
	private final ChatModel chatModel;
	
	record ActorsFilms(String actor, List<String> movies) {
	};
	
	@GetMapping("/numbers")
	public Map<String, Object> numbers(Integer number){
		MapOutputConverter mapOutputConverter = new MapOutputConverter();

		String format = mapOutputConverter.getFormat();
		String template = """
		        Provide me a List of an array of numbers from 1 to {number} under they key name "numbers"
		        {format}
		        """;
		PromptTemplate promptTemplate = new PromptTemplate(template,
		        Map.of("number", number, "format", format));
		Prompt prompt = new Prompt(promptTemplate.createMessage());
		Generation generation = chatModel.call(prompt).getResult();

		Map<String, Object> result = mapOutputConverter.convert(generation.getOutput().getContent());
		return result;
	}

	
    @GetMapping("/films")
    public ActorsFilms films(String actor) {
    	String template = """
    	        列出演員{actor}最有名的五部電影，需用繁體中文回答
    	        {format}
    	        """;
    	
    	BeanOutputConverter<ActorsFilms> beanOutputConverter =
    		    new BeanOutputConverter<>(ActorsFilms.class);

    	String format = beanOutputConverter.getFormat();
        
    	Generation generation = chatModel.call(
    		    new Prompt(new PromptTemplate(template, Map.of("actor", actor, "format", format)).createMessage())).getResult();

    	ActorsFilms actorsFilms = beanOutputConverter.convert(generation.getOutput().getContent());
    	
    	return actorsFilms;
    }
	
	 @GetMapping("/fiveactors")
	    public List<ActorsFilms> listfilms() {
	    	String template = """
	    	        列出五位隨機演員，並列出每個演員最有名的五部電影，需用繁體中文回答
	    	        {format}
	    	        """;
	    	
	    	BeanOutputConverter<List<ActorsFilms>> beanOutputConverter =
	    		    new BeanOutputConverter<>( new ParameterizedTypeReference<List<ActorsFilms>>() {});

	    	String format = beanOutputConverter.getFormat();
	        
	    	Generation generation = chatModel.call(
	    		    new Prompt(new PromptTemplate(template, Map.of("format", format)).createMessage())).getResult();

	    	List<ActorsFilms> fiveActorsFilms = beanOutputConverter.convert(generation.getOutput().getContent());
	    	
	    	return fiveActorsFilms;
	    }

}
