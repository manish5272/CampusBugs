package com.campusbugs.campusbugs.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;

@Service
public class AnonymizerService {

        private final SecretKeySpec keySpec;

        public AnonymizerService(@Value("${anon.secret") String secret){
            this.keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        }

        public String hash(String value) {
            try{
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(keySpec);
                byte[] result = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
                return HexFormat.of().formatHex(result);
            }

            catch(GeneralSecurityException e){
                throw new IllegalStateException("Could not create hash",e);
            }
        }

}
