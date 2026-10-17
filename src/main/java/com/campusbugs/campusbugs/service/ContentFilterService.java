package com.campusbugs.campusbugs.service;

import org.springframework.stereotype.Service;


import java.util.Set;

@Service
public class ContentFilterService {

    private static final Set<String> BLOCKED_WORDS =
            Set.of("fuck","shit","bitch","asshole");


    public boolean containBlockedWords(String text){
        String cleaned = normalize(text);
        for(String word : cleaned.split("\\s+")){
            if(BLOCKED_WORDS.contains(word)) {
                return true;
            }
        }
        return false;
    }


    private String normalize(String text){
        String lower = text.toLowerCase()
                .replace('0','o')
                .replace('1','i')
                .replace('3', 'e')
                .replace('4', 'a')
                .replace('5', 's')
                .replace('@', 'a')
                .replace('$', 's');

        return lower.replaceAll("[^a-z\\s]", " ");
    }

}
