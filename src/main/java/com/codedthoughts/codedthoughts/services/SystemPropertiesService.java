package com.codedthoughts.codedthoughts.services;

import com.codedthoughts.codedthoughts.exceptions.NoSuchElementPresentException;
import com.codedthoughts.codedthoughts.repo.SystemPropertiesRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SystemPropertiesService {
    public static final String SUPPORTING_MIME_TYPES = "SUPPORTING_MIME_TYPES";

    private static final Logger logger = LoggerFactory.getLogger(SystemPropertiesService.class);
    private final SystemPropertiesRepository sysPropRepo;


    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Object getSysPropertyByName(String propName) throws NoSuchElementPresentException{
        return sysPropRepo.findByName(propName)
                .map(prop -> prop.isBoolean() ? Boolean.valueOf(prop.getValue()) : prop.getValue())
                .orElseThrow(() -> {
                    logger.debug(String.format("No system property configured with '%s'!!", propName));
                    return new NoSuchElementPresentException(String.valueOf(propName));
                });
    }

}
