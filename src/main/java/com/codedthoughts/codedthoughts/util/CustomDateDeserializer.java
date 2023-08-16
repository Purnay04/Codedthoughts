package com.codedthoughts.codedthoughts.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class CustomDateDeserializer extends StdDeserializer<Date> {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat normalFormat = new SimpleDateFormat("MMM dd yyyy");
    private static final SimpleDateFormat unchangedFormat = new SimpleDateFormat("dd MMM yyyy");

    public CustomDateDeserializer() {
        this(null);
    }

    public CustomDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        String date = jsonparser.getText();

        try {

            log.warn("Data: {}, format: {}", date, format.toPattern());

            return format.parse(date);
        } catch(ParseException e) {
            try {
                return normalFormat.parse(date);
            } catch(ParseException f) {
                try {
                    return unchangedFormat.parse(date);
                } catch(ParseException g) {
                    throw new RuntimeException(g);
                }
            }
        }
    }

}