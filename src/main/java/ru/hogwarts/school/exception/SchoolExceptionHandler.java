package ru.hogwarts.school.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SchoolExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SchoolExceptionHandler.class);

    @ExceptionHandler(
            {
                    FacultyNotFoundException.class,
                    StudentNotFoundException.class,
                    AvatarNotFoundException.class
            }
    )
    public ResponseEntity<String> handleNotFound(RuntimeException exception) {
        logger.error(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(AvatarProcessingException.class)
    public ResponseEntity<String> handleInternalServerError() {
        logger.error("Exception AvatarProcessingException was thrown");
        return ResponseEntity.internalServerError().build();
    }

}
