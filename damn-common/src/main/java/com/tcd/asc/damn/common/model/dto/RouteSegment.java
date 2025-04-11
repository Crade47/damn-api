package com.tcd.asc.damn.common.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tcd.asc.damn.common.constants.TransitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,           // Use the name of the subtype as the discriminator
        include = JsonTypeInfo.As.PROPERTY,   // Include the type info as a property in JSON
        property = "transitType"              // The field to use as the discriminator (must match JSON)
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = WalkSegment.class, name = "WALK"),
        @JsonSubTypes.Type(value = TransitSegment.class, name = "LUAS")
})
public abstract class RouteSegment {
    @JsonIgnore
    protected TransitType transitType;
}