package com.revolut.txmgr.api.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ValidatableDTO implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -2731218129023113632L;
	
	private final String jsonSchemaName;

    /**
     *
     * @param jsonSchemaName
     */
    public ValidatableDTO(final String jsonSchemaName) {

        this.jsonSchemaName = jsonSchemaName;
    }

    @JsonIgnore
    public String getJsonSchemaName() {
        return jsonSchemaName;
    }

    @JsonIgnore
    public <T> Collection<T> getUnmodifiableOrEmpty( Collection<T> collection ) {
        if ( collection != null ) {
            return Collections.unmodifiableCollection( collection );
        }
        else {
            return Collections.unmodifiableCollection( new ArrayList<T>() );
        }
    }

    @JsonIgnore
    public <T> Collection<T> getUnmodifiableOrNull( Collection<T> collection ) {
        if ( collection != null ) {
            return Collections.unmodifiableCollection( collection );
        }
        else {
            return null;
        }
    }

}
