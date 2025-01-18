package com.mihai.whatsappclone.common;

import lombok.*;

/**
 * A simple DTO (Data Transfer Object) class for encapsulating a string response.
 * This is typically used to standardize responses containing a single string message.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StringResponse {

    /**
     * The response message.
     */
    private String response;
}
