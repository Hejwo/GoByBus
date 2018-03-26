package org.hejwo.gobybus.commons.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SerializationUtils {

    public static CollectionType getCollectionType(Class<? extends Collection> collectionType, Class<?> objectClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.getTypeFactory().constructCollectionType(collectionType, objectClass);
    }

}
