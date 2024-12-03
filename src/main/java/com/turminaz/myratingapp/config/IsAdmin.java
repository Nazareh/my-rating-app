package com.turminaz.myratingapp.config;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("authentication.credentials.claims['admin']")
public @interface IsAdmin {
}
