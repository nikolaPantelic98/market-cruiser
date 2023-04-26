package com.marketcruiser.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The StateDTO class represents a simplified version of a State object that contains only
 * the state's ID and name.
 * This class is used as a data transfer object (DTO) to transfer state information between
 * different layers of the application, such as between the controller and service layers.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StateDTO {

    private Long stateId;
    private String name;
}
