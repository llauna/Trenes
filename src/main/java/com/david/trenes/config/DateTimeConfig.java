package com.david.trenes.config;

import com.david.trenes.util.DateUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Locale;

@Configuration
public class DateTimeConfig implements WebMvcConfigurer {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new SpanishDateTimeDeserializer());
        module.addDeserializer(LocalDate.class, new SpanishDateDeserializer());
        mapper.registerModule(module);

        return mapper;
    }

    @Bean
    public Formatter<LocalDateTime> spanishDateTimeFormatter() {
        return new Formatter<LocalDateTime>() {
            @Override
            public LocalDateTime parse(String text, Locale locale) {
                // Intentar formato ISO primero (yyyy-MM-ddTHH:mm:ss)
                try {
                    return LocalDateTime.parse(text);
                } catch (Exception e) {
                    // Formato español con hora: dd/MM/yyyy HH:mm:ss
                    LocalDateTime result = DateUtils.parseSpanishDateTime(text);
                    if (result != null) return result;

                    // Formato español solo fecha: dd/MM/yyyy (-> 00:00:00)
                    result = DateUtils.parseSpanishDate(text);
                    if (result != null) return result;

                    throw new IllegalArgumentException(
                            "No se puede parsear la fecha: " + text +
                                    ". Formatos aceptados: ISO (yyyy-MM-ddTHH:mm:ss), dd/MM/yyyy, dd/MM/yyyy HH:mm:ss"
                    );
                }
            }

            @Override
            public String print(LocalDateTime object, Locale locale) {
                return DateUtils.formatSpanishDateTime(object);
            }
        };
    }

    @Bean
    public Formatter<LocalDate> spanishDateFormatter() {
        return new Formatter<LocalDate>() {
            @Override
            public LocalDate parse(String text, Locale locale) {
                // ISO primero: yyyy-MM-dd
                try {
                    return LocalDate.parse(text);
                } catch (Exception e) {
                    LocalDateTime result = DateUtils.parseSpanishDate(text);
                    if (result != null) return result.toLocalDate();

                    throw new IllegalArgumentException(
                            "No se puede parsear la fecha: " + text +
                                    ". Formatos aceptados: ISO (yyyy-MM-dd), dd/MM/yyyy"
                    );
                }
            }

            @Override
            public String print(LocalDate object, Locale locale) {
                return object.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
        };
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(spanishDateTimeFormatter());
        registry.addFormatter(spanishDateFormatter());
    }

    public static class SpanishDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateStr = p.getValueAsString();

            LocalDateTime result = DateUtils.parseSpanishDateTime(dateStr);
            if (result != null) return result;

            result = DateUtils.parseSpanishDate(dateStr);
            if (result != null) return result;

            try {
                return LocalDateTime.parse(dateStr);
            } catch (Exception e) {
                throw new IOException("No se puede parsear la fecha: " + dateStr +
                        ". Formatos aceptados: dd/MM/yyyy, dd/MM/yyyy HH:mm:ss, ISO");
            }
        }
    }

    public static class SpanishDateDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateStr = p.getValueAsString();

            LocalDateTime result = DateUtils.parseSpanishDate(dateStr);
            if (result != null) return result.toLocalDate();

            try {
                return LocalDate.parse(dateStr);
            } catch (Exception e) {
                throw new IOException("No se puede parsear la fecha: " + dateStr +
                        ". Formatos aceptados: dd/MM/yyyy, ISO");
            }
        }
    }
}